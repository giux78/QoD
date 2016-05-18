package controllers

import play.api._
import play.api.mvc._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import play.api.libs.json.JsArray
import play.api.libs.json.JsValue
import java.util.UUID
import java.io.File
import controllers.helper._

object GenerateDataController extends Controller {

  def generateData() = Action {
    // implicit val jsonReader = Json.reads[List[JsObject]]

    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("open_memories")
    val coll = db("dataset_2")

    //  val queryObject = MongoDBObject()
    val result = coll.find()

    while (result.hasNext) {
      val x = result.next
      val json = com.mongodb.util.JSON.serialize(x)
      println(json)
      val resultJson = Json.parse(json)
      val resources = (resultJson \ "resources").as[List[JsObject]]
    }
    mongoClient.close
    //    val resultJson = Json.parse(json)
    //    val title = (resultJson \ "title").asInstanceOf[String] //.as[String]
    //    val resources = (resultJson \ "resources").asInstanceOf[List[JsObject]]
    //    for (res <- resources) {
    //      val r = (res \ "resource_name").asInstanceOf[String]
    //      println(r)
    //   }

    Ok("ales")
  }
  def generateData2() = Action {

    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("meta_ckan")
    //   val coll = db("dataset_2")
    val coll = db("dataset")
    val newcoll = db("dashboard_chart")
    //  val queryObject = MongoDBObject()
    //  val result = coll.findOne("name" $eq )

    val results = coll.find()

    //  println(results.length)
    // TODO Aggiungere replace per accenti e virgolette anche in prepareOpen python
    val chartArray: scala.collection.mutable.MutableList[JsValue] = scala.collection.mutable.MutableList.empty
    for (result <- results) {
      val codiciPathFile = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark_cleaned/comuni_comunita_codici.JSON"

      val json = com.mongodb.util.JSON.serialize(result)
      println(json)
      val resultJson = Json.parse(json)
      val title = (resultJson \ "title").as[String]
      val name = (resultJson \ "name").as[String]
      val id = (resultJson \\ "$oid")(0).as[String]   //.asInstanceOf[String] //.as[String]
      println("id : " + id)
      val resources = (resultJson \ "resources").as[List[JsObject]].filter(x => (x \ "resource_format").as[String].equals("JSON"))

      println("Length : " + resources.length)
      for (reso <- resources) {
        val resourceName = (reso \ "resource_name").as[String].replaceAll("-", "_").replaceAll(" ", "_")
        						.replaceAll("'","").replaceAll("\\(","").replaceAll("\\)","")
        						.replaceAll("&","").replaceAll("\\+","").replaceAll("=", "")
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
            val chart1 = Json.obj("resource_name" -> resourceName, "chart_type" -> "comunita_valle", "result" -> result,
                                    "dataset_name" -> name, "dataset_id" -> id, "dataset_title" -> title)
            chartArray += chart1
            val mongo1 = com.mongodb.util.JSON.parse(chart1.toString()).asInstanceOf[DBObject]
            newcoll.insert(mongo1)
            val sqlQueryComune = s"""select anno, descriz_comu as descriz, sum(valore) as valore from $resourceName, comuni_comunita_codici where $resourceName.codEnte = comuni_comunita_codici.comu group by descriz_comu, anno order by descriz_comu, anno"""
            //      println(sqlQueryComune)
            val resultComuni = SparkHelper.queryToListJson(sqlContext, sqlQueryComune)
            val chart2 = Json.obj("resource_name" -> resourceName, "chart_type" -> "comuni", "result" -> resultComuni,
                "dataset_name" -> name, "dataset_id" -> id, "dataset_title" -> title)
            chartArray += chart2
            val mongo2= com.mongodb.util.JSON.parse(chart2.toString()).asInstanceOf[DBObject]
            newcoll.insert(mongo2)
          } else if (schemaString.contains("Anno") && schemaString.contains("Trentino")) {
            val sqlQuery = s"""Select * from $resourceName"""
            val result = SparkHelper.queryToListJson(sqlContext, sqlQuery)
            val chart3 =  Json.obj("resource_name" -> resourceName, "chart_type" -> "bar_chart", "result" -> result,
                                   "dataset_name" -> name, "dataset_id" -> id, "dataset_title" -> title)
            chartArray += chart3
            val mongo3 = com.mongodb.util.JSON.parse(chart3.toString()).asInstanceOf[DBObject]
            newcoll.insert(mongo3)
          }
          sc.stop()
        }
      }
    }
    mongoClient.close
    Ok(Json.obj("chart_data" -> chartArray))
  }
}