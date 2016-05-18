package controllers.helper

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.util.TablesNamesFinder
import scala.collection.JavaConversions._
import org.apache.spark.sql.SchemaRDD
import java.io.File
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import java.util.UUID;
import utils.GlobalContext

object SparkHelper {
  val MASTER = "local[2]"
  val APPNAME = "firstSparkApp"
  val SPARK_DRIVER_HOST = "localhost"

  def saveMyDataset(query: String, userPath :String) :Long = {
    val conf = new SparkConf(false) // skip loading external settings
      .setMaster(MASTER) // run locally with enough threads
      .setAppName(UUID.randomUUID().toString())
      .set("spark.logConf", "true")
      .set("spark.driver.host", SPARK_DRIVER_HOST)
  

    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    import sqlContext._
    val statement = CCJSqlParserUtil.parse(query);
    val selectStatement = statement.asInstanceOf[Select];
    val tablesNamesFinder = new TablesNamesFinder();
    val tableList = tablesNamesFinder.getTableList(selectStatement);

    tableList.foreach(x => {
      println(x)
      val path = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark_cleaned/" + x + ".JSON"
      //     println(path)
      val loadedFile = sqlContext.jsonFile(path)
      loadedFile.registerTempTable(x)
      loadedFile.printSchema
    })
    println("ALEOOO")
    val test = sqlContext.sql(query)
    test.saveAsParquetFile(userPath)
    val size = new File(userPath).length()
 //   test.foreach(println)
    sc.stop
    size
  }
  
  def schemaPersonalDataset(tableName:String, path: String, format :String) :String = {
       val conf = new SparkConf(false) // skip loading external settings
        .setMaster(MASTER) // run locally with enough threads
      .setAppName(UUID.randomUUID().toString())
      .set("spark.logConf", "true")
      .set("spark.driver.host", SPARK_DRIVER_HOST)
      
      conf.set("spark.scheduler.mode", "FAIR")

    val sc = new SparkContext(conf)
 //   val sc = GlobalContext.contextGlobal
    val sqlContext = new SQLContext(sc)
    import sqlContext._

    //     println(path)
    var loadedFile :SchemaRDD = null 
    if (format.equals("json")){
      loadedFile = sqlContext.jsonFile(path)
    } else {
      loadedFile = sqlContext.parquetFile(path)
    }

    loadedFile.registerTempTable(tableName)
    loadedFile.printSchema
    var schema = loadedFile.schemaString
    schema = schema.replace("root", "")
    schema = schema.replaceAll("\\|--", "")
    schema = schema.replaceAll("\\)", "\\)<br>")
    schema = schema.replaceAll("\\|", """<span style="padding-left:20px"></span>""")
    sc.stop()
    schema

  }
  
  def queryToListJson(sqlContext :SQLContext,query :String) : List[JsValue] = {
        val test = sqlContext.sql(query)
        test.foreach(println)
        println(test.schemaString)
        val schema = test.schema

        val fields = schema.fieldNames
        val zipped = test.map(x => {
          val m = (fields zip x).toMap
          val m2 = m.map( { case(x, null) => (x, "")
                            case(x, y) => (x, y.toString)
                         })
          m2
        })

        val jsonList = zipped.collect.map(x => {
          Json.toJson(x)
        }).toList
        jsonList
  }
  
}