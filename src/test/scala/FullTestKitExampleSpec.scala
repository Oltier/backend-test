import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import ciklum.backend.test.{Credentials, UserToken}
import spray.json.DefaultJsonProtocol.{jsonFormat1, jsonFormat2}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._
import akka.testkit.TestDuration
import ciklum.backend.test.actor.AsyncTokenIssueActor

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}

class FullTestKitExampleSpec extends WordSpec with Matchers with ScalatestRouteTest {


  implicit val actor_system = ActorSystem()
  implicit val actor_materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val credentialsFormat = jsonFormat2(Credentials)
  implicit val userTokenFormat = jsonFormat1(UserToken)
  val actor = system.actorOf(Props[AsyncTokenIssueActor], "token-issue-actor")
  implicit val timeout = Timeout(7 seconds)
  implicit val test_timeout = RouteTestTimeout(5.seconds dilated)

  val route =
    post {
      entity(as[Credentials]) { credentials: Credentials =>
        val future: Future[Future[UserToken]] = (actor ? credentials).asInstanceOf[Future[Future[UserToken]]]
        onComplete(future.flatten) {
          case Success(value) => complete(value)
          case Failure(ex) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>${ex.getMessage}</h1>"))
        }
      }
    }

  println(1)

  Post("issue-token", Credentials("Nikita", "p@ssword")) ~> route ~> check {
    responseAs[UserToken] should matchPattern {case UserToken(token) if token.startsWith("Nikita_")=>}
  }
  Post("issue-token", Credentials("Nikolay", "p@ssword")) ~> route ~> check {
    responseAs[UserToken] should matchPattern {case UserToken(token) if token.startsWith("Nikolay_")=>}
  }
  Post("issue-token", Credentials("Test", "p@ssword")) ~> route ~> check {
    responseAs[UserToken] should matchPattern {case UserToken(token) if token.startsWith("Test_")=>}
  }

  println(2)

}