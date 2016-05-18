package controllers.helper

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.Imports._


case class MyUser(email :String, password:String, avatar :String, id:String, name:String, surname:String, company:String)

object MyUserDAO {
  
  def findOneByEmail(email:String):Option[MyUser] = {
  //  val user = new MyUser("ale.ercolani@gmail.com", "ale", "", "aleoooo", "Alessandro", "Ercolani", "TrentoRise")
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("open_memories")
    val coll = db("users")
    val results = coll.findOne("email" $eq email)
    val user = results match {
      case Some(x) => { val user = new MyUser(x.as[String]("email"),x.as[String]("pass"), x.as[String]("avatar"),x.as[String]("id"),
                                           x.as[String]("name"),x.as[String]("surname"),x.as[String]("company"))
      				Option(user)}
      case None =>  None
    } 
//     val results = coll.findOne("email" $eq email).getOrElse(None)
//     if (!results.equals(None)){
       
//     }
    mongoClient.close
    val json = com.mongodb.util.JSON.serialize(results)
    user
  }

}