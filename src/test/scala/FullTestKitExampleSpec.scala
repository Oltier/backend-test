import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import ciklum.backend.test.{Credentials, UserToken}
import spray.json.DefaultJsonProtocol.{jsonFormat1, jsonFormat2}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._
import akka.testkit.TestDuration
import ciklum.backend.test.actor.Supervisor
import ciklum.backend.test.service.SimpleAsyncTokenService
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}

class FullTestKitExampleSpec extends WordSpec with Matchers with ScalatestRouteTest with SimpleAsyncTokenService {
  implicit val actor_system = ActorSystem()
  implicit val actor_materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val credentialsFormat = jsonFormat2(Credentials)
  implicit val userTokenFormat = jsonFormat1(UserToken)
  val supervisor = system.actorOf(Props[Supervisor], "supervisor")
  implicit val timeout = Timeout(10 seconds)
  implicit val test_timeout = RouteTestTimeout(10.seconds dilated)

  override def requestToken(credentials: Credentials): Future[UserToken] = (supervisor ? credentials).mapTo[Future[UserToken]] flatten

  val route =
    post {
      entity(as[Credentials]) { credentials: Credentials =>
        val future = requestToken(credentials)
        onComplete(future) {
          case Success(value) => {
            complete(value)
          }
          case Failure(ex) => {
            complete(ex.getMessage)
          }
        }
      }
    }

  "successfully getting user token" in {
    Post("issue-token", Credentials("user", "USER")) ~> route ~> check {
      responseAs[UserToken] should matchPattern { case UserToken(token) if token.startsWith("user_") => }
    }
  }

  "wrong username/password" in {
    Post("issue-token", Credentials("user", "p@ssword")) ~> route ~> check {
      responseAs[HttpEntity] should matchPattern { case HttpEntity.Strict(_,data) if(data.decodeString("UTF-8")=="wrong username/password" ) => }
    }
  }

  "userId starts with A" in {
    Post("issue-token", Credentials("Auser", "AUSER")) ~> route ~> check {
      responseAs[HttpEntity] should matchPattern { case HttpEntity.Strict(_,data) if(data.decodeString("UTF-8")=="userId starts with A" ) => }
    }
  }

}