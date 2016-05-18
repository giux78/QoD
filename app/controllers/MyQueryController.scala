package controllers

import play.api._
import play.api.mvc._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json
import controllers.helper.Secured
import play.api.libs.json.JsValue
import java.io.File
import controllers.helper.SparkHelper
import net.sf.jsqlparser.JSQLParserException
import play.api.libs.json.JsObject

object MyQueryController extends Controller with Secured {

  val USERHOME = System.getProperty("user.home")

  def exportQuery() = withUser { user =>
    implicit request =>
      //  val user = request.user
      val mongoClient = MongoClient("localhost", 27017)
      val db = mongoClient("meta_ckan")
      val collection = db("personal_query")
      val body: AnyContent = request.body
      val textBody: Option[JsValue] = body.asJson
      textBody.map { text =>
        println(text)

        val url = (text \ "placeholder").asOpt[String].get
        val description = (text \ "description").asOpt[String].get
        val name = (text \ "name").asOpt[String].get
        val query = (text \ "query").asOpt[String].get
        val typeQuery = (text \ "type").asOpt[String].get
        var localPath = ""
        var format = ""
        if (typeQuery.equals("personal")){
          localPath = (text \ "local_path").asOpt[String].get
          format = (text \ "format").asOpt[String].get
        }
        collection.insert(MongoDBObject("url" -> url, "description" -> description, "name" -> name,
          "userId" -> user.id, "query" -> query, "type" -> typeQuery, "local_path" -> localPath, "format" -> format))
        mongoClient.close
      }

      //   val json = com.mongodb.util.JSON.serialize(results)
      Ok("ale")
  }

  def myQuery(userId: String) = Action {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("meta_ckan")
    val coll = db("personal_query")
    //  val queryObject = MongoDBObject()
    val results = coll.find("userId" $eq userId).toList
    //  println(results.length)
    mongoClient.close
    val json = com.mongodb.util.JSON.serialize(results)
    Ok(Json.parse(json))
  }

  def myDataset(userId: String) = Action {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("meta_ckan")
    val coll = db("personal_dataset")
    val results = coll.find("userId" $eq userId).toList
    mongoClient.close
    val json = com.mongodb.util.JSON.serialize(results)
    Ok(Json.parse(json))
  }

  def exportDataset() = withUser { user =>
    implicit request =>
      //  val user = request.user
      var errorMessage = Json.toJson(Map("exception" -> ""))
      val mongoClient = MongoClient("localhost", 27017)
      val db = mongoClient("meta_ckan")
      val collection = db("personal_dataset")
      val body: AnyContent = request.body
      println(body)
      val textBody: Option[JsValue] = body.asJson
      val localPath = USERHOME + "/dataset/" + user.id
      val localDir = new File(localPath)
      if (!localDir.exists()) {
        localDir.mkdirs()
      }
      textBody.map { text =>
        println(text)
        val url = (text \ "placeholder").asOpt[String].get
        val uuid = (text \ "uuid").asOpt[String].get
        val description = (text \ "description").asOpt[String].get
        val query = (text \ "query").asOpt[String].get
        val fileName = (text \ "name").asOpt[String].get
        val typeDataset = (text \ "type").asOpt[String].get
        val finalPath = localPath + "/" + uuid
        try {
          val size = SparkHelper.saveMyDataset(query, finalPath)
          collection.insert(MongoDBObject("url" -> localPath, "name" -> fileName ,"description" -> description,
            "userId" -> user.id, "query" -> query, "type" -> typeDataset, "local_path" -> finalPath, "uuid" -> uuid, "size" -> size,
            "format" -> "parquet"))
          mongoClient.close
        } catch {
          case e: JSQLParserException =>
            println("exception caught: " + e.printStackTrace());
            errorMessage = Json.toJson(Map("exception" -> e.getMessage()))
            mongoClient.close
        }
        Ok(Json.toJson(Map("result" -> "ok")))
      }.getOrElse(Ok(Json.toJson(errorMessage)))
  }
  // Ok("ale")

  def upload = Action(parse.multipartFormData) { request =>
    println(request.headers)
    println(request.body)
    val name = request.body.asFormUrlEncoded.get("name").get(0)
    val desc = request.body.asFormUrlEncoded.get("desc").get(0)
    val userId = request.body.asFormUrlEncoded.get("user_id").get(0)
    val query = "Select * from " + name
    request.body.file("dataset").map { pFile =>
      import java.io.File
      println(pFile.filename)
      val filename = pFile.filename
      val contentType = pFile.contentType
      val pathDir =  new File(USERHOME + "/dataset/" + userId)
      if(!pathDir.exists()){
        pathDir.mkdirs()
      }
      val filePath = pathDir + "/" + filename
      val commonPath = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark_cleaned/" + filename
      val mongoClient = MongoClient("localhost", 27017)
      val db = mongoClient("meta_ckan")
      val collection = db("personal_dataset")
      val file = new File(filePath)
      val commonFile = new File(commonPath)
      pFile.ref.moveTo(file)
  //    pFile.ref.moveTo(commonFile)
      val size = file.length()
      collection.insert(MongoDBObject("url" -> filePath, "description" -> desc, "name" -> name,
            "userId" -> userId, "query" -> query , "type" -> "my_dataset", "local_path" -> filePath, "uuid" -> "", "size" -> size,
            "format" -> "json"))
          mongoClient.close
      Ok("File uploaded")
    }.getOrElse {
      Ok("error")
    }
  }
  
  def getPersonalDataset(datasetId :String) = withUser { user =>
    implicit request =>
      //  val user = request.user
      var errorMessage = Json.toJson(Map("exception" -> ""))
      val mongoClient = MongoClient("localhost", 27017)
      val db = mongoClient("meta_ckan")
      val collection = db("personal_dataset")
      val objectId = new ObjectId(datasetId)
      val results = collection.findOne("_id" $eq objectId)
      
      mongoClient.close
      val json = com.mongodb.util.JSON.serialize(results)
      val updateJson = Json.parse(json)
      val tableName = (updateJson \ "name").asOpt[String].get
      val path = (updateJson \ "local_path").asOpt[String].get
      val format = (updateJson \ "format").asOpt[String].get
      val schema = SparkHelper.schemaPersonalDataset(tableName, path, format)
      val prova = updateJson.as[JsObject] + ("schema" -> Json.toJson(schema))
      Ok(prova)  
  }

}