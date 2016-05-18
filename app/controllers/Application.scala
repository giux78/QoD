package controllers

import play.api._
import play.api.mvc._
//import securesocial.core._
import securesocial.core.SocialUser
import securesocial.core.java.BaseUserService
import play.api.libs.json.JsValue
import controllers.helper._
import play.api.libs.json.Json
import scala.concurrent.Future
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.JsObject
//import com.mongodb.util.JSON;
import com.mongodb.DBObject;
//import java.util.UUID

object Application extends Controller with securesocial.core.SecureSocial with Secured {

  // def index = Action {
  //   Future { SparkMLLibUtility.SparkMLLibExample }
  //    Ok(views.html.index(""))
  //  }

  def home = Action {
    Ok(views.html.home())
  }

  def myLogin = Action {
    Ok(views.html.auth_login())
  }

  def mySignUp = Action {
    Ok(views.html.auth_signup())
  }

  def myLoginData() = Action {
    request =>
      println(request.body)
      println(request.queryString)
      println("ciao")
      val body: AnyContent = request.body
      val textBody: Option[JsValue] = body.asJson
      textBody.map { text =>
        val email = (text \ "email").as[String]
        val pass = (text \ "pass").as[String]
        //    println(email)
        //    println(pass)
        val url = "http://localhost:9001/itermediate?email=" + email
        val json = Json.toJson(Map("ok" -> url))
        Ok(json)
        // Ok(Json.toJson(Map("Ok" -> "./open_memories_hadoop")))
        //  Redirect(routes.Application.openMemoriesHadoop).withSession("email" -> email)
      }.getOrElse {
        println("error")
        BadRequest("error")
      }
  }

  def mySignUpData() = Action {
    request =>
      val mongoClient = MongoClient("localhost", 27017)
      val db = mongoClient("open_memories")
      val collection = db("users")
      val body: AnyContent = request.body
      val textBody: Option[JsValue] = body.asJson
      textBody.map { text =>
        val email = (text \ "email").as[String]
        val pass = (text \ "pass").as[String]
        MyUserDAO.findOneByEmail(email).map { x =>
            Ok(Json.toJson(Map("ko" -> "user already exist")))
        }.getOrElse({         
          val id = java.util.UUID.randomUUID().toString().replaceAll("-", "")
          val newObj = text.as[JsObject] + ("id" -> Json.toJson(id))
          val dbObject = com.mongodb.util.JSON.parse(newObj.toString()).asInstanceOf[DBObject]
          //  val dbObj = 
          collection.insert(dbObject)
          mongoClient.close
          val url = "http://localhost:9001/itermediate?email=" + email
          val json = Json.toJson(Map("ok" -> url))
          Ok(json)
          }
        )
      }.getOrElse {
        println("error")
        BadRequest("error")
      }
  }

  //def admin = SecuredAction { implicit request =>
  //  Ok(views.html.admin(request.user))
 // }

  def admin = withUser { user => implicit request =>
     Ok(views.html.admin(user))
  }
  
 // def busRealtime = SecuredAction { implicit request =>
 //   Ok(views.html.realtime_bus(request.user))
 // }

  def busRealtime = withUser { user => implicit request =>
     Ok(views.html.realtime_bus(user))
  }
  
  def openMemories = SecuredAction { implicit request =>
    Ok(views.html.open_memories(request.user))
  }

     def openMemoriesHadoop = Action { implicit request =>
       Ok(views.html.open_memories_hadoop())
   }

//  def openMemoriesHadoop = withUser { user =>
//    implicit request =>
//      Ok(views.html.open_memories_hadoop())
//  }

  def itermediate = Action { request =>
    val email = request.queryString("email")(0)
    Redirect(routes.Application.openMemoriesHadoop).withSession("email" -> email)
  }
  
   def myquery = withUser { user => implicit request =>
     Ok(views.html.myquery("MyQuery",user))
  }
   
  def mydataset = withUser { user => implicit request =>
     Ok(views.html.mydataset("MyDataset",user))
  }
    
  def mydataset2(evertything :String) = withUser { user => implicit request =>
     Ok(views.html.mydataset2(user))
  }
  
    def trentinoChart(evertything :String) = withUser { user => implicit request =>
     Ok(views.html.trentino_chart("Trentino Chart", user))
  }
  
    def sanity() = withUser { user => implicit request =>
     Ok(views.html.sanity_check("Sanity", user))
  }
  

  // def openMemoriesHadoop = Authenticated {
  //     request =>
  //     Ok(views.html.open_memories_hadoop())
  // }

  class AuthenticatedRequest[A](val username: String, request: Request[A]) extends WrappedRequest[A](request)

  object Authenticated extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
      println(request.session.get("email"))
      request.session.get("email").map { username =>
        block(new AuthenticatedRequest(username, request))
      } getOrElse {
        Future.successful(Forbidden)
      }
    }
  }

}

