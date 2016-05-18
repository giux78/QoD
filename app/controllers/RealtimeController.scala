package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Enumerator
import models.services.ChatRoom
import models.services.Receiver
import utils.ws.Broadcast
import utils.ws.Received
import utils.ws.Broadcast


object RealtimeController extends Controller{
  
   def realtimeBus() = Action { request =>
      val body: AnyContent = request.body
      val textBody: Option[JsValue] = body.asJson
      textBody.map { text =>
        println(text)
          ChatRoom.wsm.broadcast("ale", ChatRoom.buildMsg("talk", "ale", text.toString))
      //  ChatRoom.wsm.supervisor ! Broadcast("ale", ChatRoom.buildMsg("talk", "ale", text.toString))
        }
      Ok("ale")
      }
   
   def websocket = WebSocket.using[String] { request => 
  
  // Just consume and ignore the input
  val in = Iteratee.consume[String]()
  
  // Send a single 'Hello!' message and close
  val out = Enumerator("Hello!").andThen(Enumerator.eof)
  
  (in, out)
}

     def chatRoomJs(username: String) = Action { implicit request =>
       println(username)
    Ok(views.js.chatRoom(username))
  }
  
  /**
   * Handles the chat websocket.
   */
  def chat(username: String) = ChatRoom.wsm.websocket[Receiver, JsValue](username)

   
}