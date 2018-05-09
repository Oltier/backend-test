package ciklum.backend.test.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.event.Logging
import ciklum.backend.test.{Credentials, User, UserToken}
import scala.concurrent.{Future, Promise}
import scala.util.Random
import akka.util.Timeout
import scala.concurrent.duration._

class ValidationActor extends Actor {
  val log = Logging(context.system, this)
  implicit val system = context.system
  implicit val executionContext = system.dispatcher
  val userTokenActor = context.actorOf(Props[UserTokenActor], name = "userTokenActor")
  implicit val timeout = Timeout(5 seconds)
  def receive = {
    case c: Credentials => {
      val p = Promise[User]()
       Future {
        Thread.sleep(Random.nextInt(5000))
        if(c.username.toUpperCase == c.password)
          p.success(User(c.username))
        else
          p.failure(new Exception("wrong username/password"))
      }
      userTokenActor forward p.future
    }
    case _ => log.info("received unknown message")
  }
}