package controllers

import play.api._
import play.api.mvc._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import scala.collection.mutable.MutableList
import play.api.libs.json.JsValue
import play.api.libs.json.Json

object SanityCheckController extends Controller {

  def exiperedDataset() = Action {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("open_memories")
    val coll = db("dataset_check")
    //  val queryObject = MongoDBObject()
    val results = coll.find("last_update" $ne "").toList
    val cal = Calendar.getInstance()
    cal.setTime(new Date())
        var listJson = MutableList[JsValue]()
    val expiredDataset = results.map(x => {
      val expirationDate = Calendar.getInstance()
      val lastUpdate = x.get("last_update").asInstanceOf[String]
      val updatedType = x.get("Aggiornamento").asInstanceOf[String]
      val formatter = new SimpleDateFormat("dd/MM/yyyy");
      val updatedDate = formatter.parse(lastUpdate)
      expirationDate.setTime(updatedDate)
  //    println(expirationDate)
      updatedType.toLowerCase() match {
        case "annuale" => expirationDate.add(Calendar.YEAR, 1)
        case "decennale" => expirationDate.add(Calendar.YEAR, 10)
        case "giornaliero" => {
       //   println(lastUpdate)
       //   println(updatedType)
          expirationDate.add(Calendar.DATE, 1)
     //     println("Expiration Date : " + formatter.format(expirationDate.getTime()))
        }
        case "settimanale" => expirationDate.add(Calendar.DATE, 7)
        case "mensile" => expirationDate.add(Calendar.MONTH, 1)
        case "biennale" => expirationDate.add(Calendar.YEAR, 2)
        case "trimestrale" => expirationDate.add(Calendar.MONTH, 3)
        case "quinquennale" => expirationDate.add(Calendar.YEAR, 5)
        case "triennale" => expirationDate.add(Calendar.YEAR, 5)
        case _ => expirationDate.add(Calendar.YEAR, 1)
      }
      println(lastUpdate)
      println(updatedType)
      println("Expiration Date : " +  formatter.format(expirationDate.getTime()))
      if(expirationDate.before(cal)){
         val title = x.get("title").asInstanceOf[String]
         val json =  Json.toJson(Map("title" -> title, "Aggiornamento" -> updatedType,"modified" -> lastUpdate, "expiration" -> formatter.format(expirationDate.getTime())))
         listJson += json
        
      }

    })
    //  println(results.length)
    mongoClient.close
    println(expiredDataset.length)
 //   val json = com.mongodb.util.JSON.serialize(expiredDataset)
  //      Ok(json)
      Ok(Json.toJson(listJson))

  }

  def exiperedDatasetByResourceDate() = Action {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("open_memories")
    val coll = db("dataset_check")
    //  val queryObject = MongoDBObject()
    val results = coll.find(MongoDBObject("last_update" -> "")).toList
    val cal = Calendar.getInstance()
    cal.setTime(new Date())
    var listJson = MutableList[JsValue]()
    val expiredResults = results.map(x => {
      val resources = x.get("resources").asInstanceOf[BasicDBList]
      var firstResourceDate = resources.get(0).asInstanceOf[DBObject].get("created").asInstanceOf[String]
      val firstResourceModified = Option(resources.get(0).asInstanceOf[DBObject].get("last_modified").asInstanceOf[String])
      val lastModified = firstResourceModified.getOrElse("")
      if (!lastModified.equals("")){
         firstResourceDate = lastModified
      }
      val correctDate = firstResourceDate.split("T")(0).replaceAll("-", "/")
      val expirationDate = Calendar.getInstance()
      val formatter = new SimpleDateFormat("yyyy/MM/dd");
      val updatedDate = formatter.parse(correctDate)
      expirationDate.setTime(updatedDate)
      val optionUpdatedType = Option(x.get("Aggiornamento").asInstanceOf[String])
      val updatedType = optionUpdatedType.getOrElse("")
      updatedType.toLowerCase() match {
        case "annuale" => expirationDate.add(Calendar.YEAR, 1)
        case "decennale" => expirationDate.add(Calendar.YEAR, 10)
        case "giornaliero" => {
      //    println(correctDate)
      //    println(updatedType)
          expirationDate.add(Calendar.DATE, 1)
      //    println("Expiration Date : " + formatter.format(expirationDate.getTime()))
        }
        case "settimanale" => expirationDate.add(Calendar.DATE, 7)
        case "mensile" => expirationDate.add(Calendar.MONTH, 1)
        case "biennale" => expirationDate.add(Calendar.YEAR, 2)
        case "trimestrale" => expirationDate.add(Calendar.MONTH, 3)
        case "quinquennale" => expirationDate.add(Calendar.YEAR, 5)
        case "triennale" => expirationDate.add(Calendar.YEAR, 5)
        case _ => expirationDate.add(Calendar.YEAR, 1)
      }

      println(correctDate)
      println(updatedType)
      println("Expiration Date : " +  formatter.format(expirationDate.getTime()))
      if(expirationDate.before(cal)){
         val title = x.get("title").asInstanceOf[String]
         val json =  Json.toJson(Map("title" -> title, "Aggiornamento" -> updatedType,"modified" -> correctDate, "expiration" -> formatter.format(expirationDate.getTime())))
         listJson += json
      }

    })
    println(expiredResults.length)

    mongoClient.close
 //   val json = com.mongodb.util.JSON.serialize(expiredResults)
    Ok(Json.toJson(listJson))
  }
  
   def datesetWithoutCategory= Action {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("open_memories")
    val coll = db("dataset_check")
    //  val queryObject = MongoDBObject()
    val results = coll.find(MongoDBObject("categorie" -> "")).toList

    mongoClient.close
    val json = com.mongodb.util.JSON.serialize(results)
    Ok(json)
  }
   
   
}