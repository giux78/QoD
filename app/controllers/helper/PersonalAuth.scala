package controllers.helper

import play.api.mvc._
import controllers.routes


trait Secured {

  def email(request: RequestHeader) = {println("merds");println(request.session.get("email"));request.session.get("email")}

 // def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)
  def onUnauthorized(request: RequestHeader) = {println("ok");println(request.session.get("email"));Results.Redirect(routes.Application.myLogin)}
  
  
  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(email, onUnauthorized) { user =>
      println(user)
      Action(request => f(user)(request))
    }
  }

  /**
   * This method shows how you could wrap the withAuth method to also fetch your user
   * You will need to implement UserDAO.findOneByUsername
   */
  def withUser(f: MyUser => Request[AnyContent] => Result) = withAuth { email => implicit request =>
    println(email)
    MyUserDAO.findOneByEmail(email).map { user =>
      println(user)
    //  user match {
    //    case Some(x) => f(x)(request)
    //    case None => onUnauthorized(request)
    //  }
     f(user)(request)
    }.getOrElse(onUnauthorized(request))
  }
}

