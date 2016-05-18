package utils


import play.api.libs.json.JsValue
import play.api.libs.json.Json
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.util.TablesNamesFinder
import scala.collection.JavaConversions._
import net.sf.jsqlparser.parser.CCJSqlParserManager
import org.apache.spark.sql.SchemaRDD
import java.util.UUID

object QueryUtil {
  
  def runQuery(query :String) :List[JsValue] = {

        val conf = new SparkConf(false) // skip loading external settings
          .setMaster("local[2]") // run locally with enough threads
          .setAppName(UUID.randomUUID().toString())
          .set("spark.logConf", "true")
          .set("spark.driver.host", "localhost")
          
          conf.set("spark.scheduler.mode", "FAIR")

        val sc = new SparkContext(conf)
        val sqlContext = new SQLContext(sc)
        import sqlContext._
        val statement = CCJSqlParserUtil.parse(query);
        val selectStatement = statement.asInstanceOf[Select];
        val tablesNamesFinder = new TablesNamesFinder();
        val tableList = tablesNamesFinder.getTableList(selectStatement);
        var dirPath = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark_cleaned/"
          
        tableList.foreach(x => {
          println(x)
          var path =  dirPath + x + ".JSON"
          //     println(path)
          val loadedFile = sqlContext.jsonFile(path)
          loadedFile.registerTempTable(x)
          loadedFile.printSchema
        })

  //      val path = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark/POI___Trentino.JSON"
  //      val poi = sqlContext.jsonFile(path)

   //     val pathAbitazioni = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark/abitazioni.JSON"
   //     val abitazioni = sqlContext.jsonFile(pathAbitazioni)

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
        sc.stop()
        jsonList
  }

   def runQueryPersonalDataset(query :String, path :String, fileFormat :String) :List[JsValue] = {

       val conf = new SparkConf(false) // skip loading external settings
          .setMaster("local[2]") // run locally with enough threads
       //   .setMaster("spark://ales-MacBook-Pro-2.local:7077") 
       //	   .setMaster("spark://10.206.38.41:7077") 
          .setAppName(UUID.randomUUID().toString())
          .setSparkHome("/Users/ale/Development/spark/spark-1.1.0")
          .set("spark.logConf", "true")
          .set("spark.driver.host", "localhost")

          conf.set("spark.scheduler.mode", "FAIR")
        //  val configuration = new org.apache.hadoop.conf.Configuration();
        //  configuration.set("fs.default.name", "hdfs://master");
       //   configuration.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem")
      //    configuration.set("fs.file.impl","org.apache.hadoop.fs.LocalFileSystem")
       
          val sc = new SparkContext(conf)
     //  	  sc.hadoopConfiguration.set("fs.default.name", "hdfs://master")
     //     sc.hadoopConfiguration.set("fs.file.impl","org.apache.hadoop.fs.LocalFileSystem")
     //     sc.hadoopConfiguration.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem")
     //     sc.addJar("/Users/ale/Development/spark/Play-Spark-Scala/target/scala-2.10/firstsparkapp_2.10-1.0-SNAPSHOT.jar")
        
	//	val sc = GlobalContext.contextGlobal
        val sqlContext = new SQLContext(sc)
        import sqlContext._
        val statement = CCJSqlParserUtil.parse(query);
        val selectStatement = statement.asInstanceOf[Select];
        val tablesNamesFinder = new TablesNamesFinder();
        val tableList = tablesNamesFinder.getTableList(selectStatement);
   //     var dirPath = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark_cleaned/"
          
        tableList.foreach(x => {
          println(x)
     //     var path =  dirPath + x + ".JSON"
          //     println(path)
  

          if (fileFormat.equals("json")){
            val subPath = path.substring(0, path.lastIndexOf('/') + 1)
            val filePath = subPath + x + ".json"
            println("qui ohhhh")
            println(filePath)
            val loadedFile = sqlContext.jsonFile(filePath)
            loadedFile.registerTempTable(x)
            loadedFile.printSchema
          } else {
            val loadedFile = sqlContext.parquetFile(path)
            loadedFile.registerTempTable(x)
            loadedFile.printSchema
          }

        })

  //      val path = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark/POI___Trentino.JSON"
  //      val poi = sqlContext.jsonFile(path)

   //     val pathAbitazioni = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark/abitazioni.JSON"
   //     val abitazioni = sqlContext.jsonFile(pathAbitazioni)

        val test = sqlContext.sql(query)
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
        sc.stop()
        jsonList
  }
  
}