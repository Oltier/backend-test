package ciklum.backend.test

import akka.actor.Actor
import akka.event.Logging
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class AsyncTokenIssueActor extends Actor with AsyncTokenService {

  implicit val ec = ExecutionContext.global

  val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"
  val log = Logging(context.system, this)

  def receive = {
    case c: Credentials => sender ! requestToken(c)
    case _ ⇒ log.info("received unknown message")
  }

  override protected def authenticate(credentials: Credentials): Future[User] = {
    Future{
      val sleep = Random.nextInt(5000)
      Thread.sleep(sleep)
      User(credentials.username)
    }
  }

  override protected def issueToken(user: User): Future[UserToken] = {
    import org.joda.time.format.DateTimeFormat
    val dtfOut = DateTimeFormat.forPattern(format)
    Future {
      UserToken(user.userId + "_" + dtfOut.print(new DateTime()))
    }
  }

  override def requestToken(credentials: Credentials): Future[UserToken] = {
    val userFuture = authenticate(credentials)
    userFuture.flatMap(user => issueToken(user))
  }
}