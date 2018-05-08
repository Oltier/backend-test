package ciklum.backend.test

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}



object Entry extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val credentialsFormat = jsonFormat2(Credentials)
  implicit val userTokenFormat = jsonFormat1(UserToken)
  val actor = system.actorOf(Props[AsyncTokenIssueActor], "token-issue-actor")
  implicit val timeout = Timeout(7 seconds)
  val route =
    path("issue-token") {
      post {
        entity(as[Credentials]) { credentials: Credentials =>
          val future: Future[Future[UserToken]] = (actor ? credentials).asInstanceOf[Future[Future[UserToken]]]
          onComplete(future.flatten) {
            case Success(value) => complete(value)
            case Failure(ex) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>${ex.getMessage}</h1>"))
          }
        }
      }
    }
  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8085)
}
