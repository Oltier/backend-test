package ciklum.backend.test.actor

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.util.Timeout
import ciklum.backend.test.Credentials
import scala.concurrent.duration._

class Supervisor extends Actor {

  val log = Logging(context.system, this)
  implicit val system = context.system
  implicit val executionContext = system.dispatcher
  val validationActor = system.actorOf(Props[ValidationActor], name = "validationActor")
  implicit val timeout = Timeout(10 seconds)

  def receive = {
    case c: Credentials ⇒ validationActor forward c
    case _  ⇒ log.info("unknown message")
  }

}
