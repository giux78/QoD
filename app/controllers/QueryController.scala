package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import utils.SparkMLLibUtility
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.util.JSON
import com.mongodb.casbah.Imports._
import org.apache.spark.sql.SchemaRDD
import utils.GlobalMap
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.util.TablesNamesFinder
import scala.collection.JavaConversions._
import net.sf.jsqlparser.parser.CCJSqlParserManager
import anorm._
import scala.collection.mutable.MutableList
import utils.QueryUtil
import java.util.UUID
//import com.foundationdb.sql.parser.SQLParser

object QueryController extends Controller with securesocial.core.SecureSocial {
  
  def runSql() = Action {
    request =>
      val body: AnyContent = request.body
      val textBody: Option[JsValue] = body.asJson
      textBody.map { text =>
        println(text)
        //   val datasetName = (text \ "dataset_name").as[String]
        val query = (text \ "query").as[String]
        //   var jsonList = List[JsValue]()
        if (query.contains("#")) {
          Ok(Json.toJson(Map("query" -> "parametric")))
        } else {
          val jsonList = QueryUtil.runQuery(query)
          Ok(Json.toJson(jsonList))
        }
        //     sc.stop()
      }.getOrElse {
        println("error")
        BadRequest("Expecting text/plain request body")
      }
  }

  def runSqlOnPersonalDataset() = Action {
    request =>
      val body: AnyContent = request.body
      val textBody: Option[JsValue] = body.asJson
      textBody.map { text =>
        println(text)
        //   val datasetName = (text \ "dataset_name").as[String]
        val query = (text \ "query").as[String]
        val path = (text \ "path").as[String]
        val format = (text \ "format").as[String]
        //   var jsonList = List[JsValue]()
        if (query.contains("#")) {
          Ok(Json.toJson(Map("query" -> "parametric")))
        } else {
          val jsonList = QueryUtil.runQueryPersonalDataset(query, path, format)
          Ok(Json.toJson(jsonList))
        }
        //     sc.stop()
      }.getOrElse {
        println("error")
        BadRequest("Expecting text/plain request body")
      }
  }
  
  def schema(tableName: String) = Action {
    val conf = new SparkConf(false) // skip loading external settings
      .setMaster("local[2]") // run locally with enough threads
      .setAppName(UUID.randomUUID().toString())
      .set("spark.logConf", "true")
      .set("spark.driver.host", "localhost")

      conf.set("spark.scheduler.mode", "FAIR")
      
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    import sqlContext._

    //  val path = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark/" + tableName + ".JSON"
    val path = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark_cleaned/" + tableName + ".JSON"
    //     println(path)
    val loadedFile = sqlContext.jsonFile(path)
    loadedFile.registerTempTable(tableName)
    loadedFile.printSchema
    var schema = loadedFile.schemaString
    schema = schema.replace("root", "")
    schema = schema.replaceAll("\\|--", "")
    schema = schema.replaceAll("\\)", "\\)<br>")
    schema = schema.replaceAll("\\|", """<span style="padding-left:20px"></span>""")
    sc.stop()
    val json = Json.toJson(Map("schema" -> schema))
    //  sc.stop()
    Ok(json)
  }
  

  def schemas = Action { request =>
    val body: AnyContent = request.body
    val textBody: Option[JsValue] = body.asJson
    val conf = new SparkConf(false) // skip loading external settings
      .setMaster("local[2]") // run locally with enough threads
      .setAppName(UUID.randomUUID().toString())
      .set("spark.logConf", "true")
      .set("spark.driver.host", "localhost")

      conf.set("spark.scheduler.mode", "FAIR")
      
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    import sqlContext._
    textBody.map { text =>
      val schemas = (text \ "schemas").as[List[String]]
      var listJson = MutableList[JsValue]()
      for (tableName <- schemas) {
        //  val path = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark/" + tableName + ".JSON"
        val path = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark_cleaned/" + tableName + ".JSON"
        //     println(path)
        val loadedFile = sqlContext.jsonFile(path)
        loadedFile.registerTempTable(tableName)
        loadedFile.printSchema
        var schema = loadedFile.schemaString
        schema = schema.replace("root", "")
        schema = schema.replaceAll("\\|--", "")
        schema = schema.replaceAll("\\)", "\\)<br>")
        schema = schema.replaceAll("\\|", """<span style="padding-left:20px"></span>""")
        val json = Json.toJson(Map("schema_" + tableName -> Json.toJson(schema)))
        listJson += json
      }
      sc.stop
      Ok(Json.toJson(Map("schemas" -> listJson)))
    }.getOrElse {
      println("error")
      BadRequest("Expecting text/plain request body")
    }
  }

/*  def exportQuery() = SecuredAction { implicit request =>
    val user = request.user
    println(user.identityId.userId.toString)
    println(user.identityId.providerId.toString)
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("meta_ckan")
    val collection = db("personal_query")
    val body: AnyContent = request.body
    val textBody: Option[JsValue] = body.asJson
    textBody.map { text =>
      println(text)
      val url = (text \ "placeholder").asOpt[String].get
      val description = (text \ "description").asOpt[String].get
      val query = (text \ "query").asOpt[String].get
      val typeQuery = (text \ "type").asOpt[String].get
      collection.insert(MongoDBObject("url" -> url, "description" -> description,
        "userId" -> user.identityId.userId.toString, "query" -> query, "type" -> typeQuery))
      mongoClient.close
    }

    //   val json = com.mongodb.util.JSON.serialize(results)
    Ok("ale")
  } */

  def datasets() = Action {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("meta_ckan")
    val coll = db("dataset")
    //  val queryObject = MongoDBObject()
    val results = coll.find().toList
//    val results = coll.find("resources" $elemMatch (MongoDBObject("resource_format" -> "JSON"))).toList
    //  println(results.length)
    mongoClient.close
    val json = com.mongodb.util.JSON.serialize(results)
    Ok(json)
  }

  def query(userId: String, UUIDQuery: String) = Action {
    implicit request =>
      val mongoClient = MongoClient("localhost", 27017)
      val db = mongoClient("meta_ckan")
      val coll = db("personal_query")
      val url = "http://localhost:9001/query/" + userId + "/" + UUIDQuery
      
      //  val result = coll.findOne("url" $eq url).get
      // val result = coll.findOne("url" $regex "^033f4cffa2b240b484ef2c85bc5b49c0^").get
      val regex = ".*"+ UUIDQuery +".*"
      val result = coll.findOne(MongoDBObject("url" -> regex.r)).get
      val resultMap = result.toMap()
      val typeQuery = resultMap.get("type").asInstanceOf[String]
      var query = resultMap.get("query").asInstanceOf[String]
      if(typeQuery != null && typeQuery.equals("parametric")){
         val params = request.queryString.map { case (k,v) => k -> v.mkString }
         params.map({ case (k,v) => query = query.replace("#" + k, v)})
      }
      println(query)
      var results : List[JsValue] = null
      typeQuery match {
        case "personal" =>  {
          val filePath = resultMap.get("local_path").asInstanceOf[String]
          val format  = resultMap.get("format").asInstanceOf[String]
          results = QueryUtil.runQueryPersonalDataset(query,filePath, format)
          }
        case _ => results = QueryUtil.runQuery(query)
      }
     // val results = QueryUtil.runQuery(query)
      mongoClient.close
      //  val json = com.mongodb.util.JSON.serialize(results)
      Ok(Json.toJson(results))
  }

}