package controllers

import play.api._
import play.api.mvc._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json
import helper.Secured
import play.api.libs.json.JsValue
import java.io.File
import helper.SparkHelper
import net.sf.jsqlparser.JSQLParserException
import play.api.libs.json.JsObject
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import play.api.libs.json.JsArray
import java.util.UUID

object ChartController extends Controller with Secured {

  def infoDataset(datasetName: String) = Action {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("meta_ckan")
    val coll = db("dataset")
    //  val queryObject = MongoDBObject()
    val result = coll.findOne("name" $eq datasetName)
    mongoClient.close
    //  println(results.length)
    // TODO Aggiungere replace per accenti e virgolette anche in prepareOpen python

    val codiciPathFile = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark_cleaned/comuni_comunita_codici.JSON"

    val json = com.mongodb.util.JSON.serialize(result)
    println(json)
    val resultJson = Json.parse(json)
    val title = (resultJson \ "title").as[String]
    val resources = (resultJson \ "resources").as[List[JsObject]].filter(x => (x \ "resource_format").as[String].equals("JSON"))
    val chartArray :scala.collection.mutable.MutableList[JsValue] = scala.collection.mutable.MutableList.empty
    println("Length : " + resources.length)
    for (reso <- resources) {
      val resourceName = (reso \ "resource_name").as[String].replaceAll("-", "_").replaceAll(" ","_")
      println(resourceName)
    //  val newName = datasetName.replaceAll("/", "")
      val datasetPathFile = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark_cleaned/" + resourceName + ".JSON"
      val file = new File(datasetPathFile)
      if (file.exists() && !resourceName.toLowerCase().contains("metadati")) {
        val conf = new SparkConf(false) // skip loading external settings
          .setMaster(SparkHelper.MASTER) // run locally with enough threads
          .setAppName(UUID.randomUUID().toString())
          .set("spark.logConf", "true")
          .set("spark.driver.host", SparkHelper.SPARK_DRIVER_HOST)
        val sc = new SparkContext(conf)
        val sqlContext = new SQLContext(sc)
        import sqlContext._
        val loadedFile = sqlContext.jsonFile(datasetPathFile)
        loadedFile.registerTempTable(resourceName)
        val codiciFile = sqlContext.jsonFile(codiciPathFile)
        codiciFile.registerTempTable("comuni_comunita_codici")
        val schemaString = loadedFile.schemaString

        if (schemaString.contains("codEnte")) {
          val sqlQueryComunitaValle = s"""select anno, descriz_comv as descriz, sum(valore) as valore from $resourceName, comuni_comunita_codici where $resourceName.codEnte = comuni_comunita_codici.comu group by descriz_comv, anno order by descriz_comv, anno"""
     //     println(sqlQueryComunitaValle)
          val result = SparkHelper.queryToListJson(sqlContext, sqlQueryComunitaValle)
     //     val arr = Json.arr(result)
       //   Json.toJson(Json.obj("resource_name" -> resourceName, "result" -> arr))
          chartArray += Json.obj("resource_name" -> resourceName, "chart_type" -> "comunita_valle", "result" -> result)
          val sqlQueryComune = s"""select anno, descriz_comu as descriz, sum(valore) as valore from $resourceName, comuni_comunita_codici where $resourceName.codEnte = comuni_comunita_codici.comu group by descriz_comu, anno order by descriz_comu, anno"""     
    //      println(sqlQueryComune)
          val resultComuni = SparkHelper.queryToListJson(sqlContext, sqlQueryComune)
          chartArray += Json.obj("resource_name" -> resourceName, "chart_type" -> "comuni", "result" -> resultComuni)
        } else if(schemaString.contains("Anno") && schemaString.contains("Trentino")){
             val sqlQuery = s"""Select * from $resourceName"""
             val result = SparkHelper.queryToListJson(sqlContext, sqlQuery)
             chartArray += Json.obj("resource_name" -> resourceName, "chart_type" -> "bar_chart", "result" -> result)
        }
        sc.stop()
      }

    }
    Ok(Json.obj("title" -> title, "chart_data" -> chartArray))
    //   Ok(Json.toJson(Map("error" -> "impossible to load chart dataset not suitable")))

  }

}