package ciklum.backend.test.actor

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import ciklum.backend.test.{User, UserToken}
import org.joda.time.DateTime

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Random, Success, Try}

class UserTokenActor extends Actor {
  val log = Logging(context.system, this)
  val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"
  implicit val system = context.system
  implicit val executionContext = system.dispatcher

  override def receive = {
    case fu: Future[User] => {
      val p = Promise[UserToken]()
      Future {
        import org.joda.time.format.DateTimeFormat
        val dtfOut = DateTimeFormat.forPattern(format)
        fu.onComplete {
          case Failure(f) => p.failure(f)
          case Success(u) => {
            if (u.userId.startsWith("A"))
              p.failure(new Exception("userId starts with A"))
            else {
              Thread.sleep(Random.nextInt(5000))
              p.success(UserToken(u.userId + "_" + dtfOut.print(new DateTime())))
            }
          }
        }
      }
      sender ! p.future
    }
    case _ ⇒ log.info("received unknown message")
  }
}
