package ciklum.backend.test.actor

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import ciklum.backend.test.Credentials

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class Supervisor extends Actor {
  val log = Logging(context.system, this)
  implicit val ec = ExecutionContext.Implicits.global
  val child1 = context.actorOf(Props[AsyncTokenIssueActor], name = "asyncTokenIssueActor")
  implicit val timeout = Timeout(10 seconds)
  def receive = {
    case c: Credentials ⇒ child1 ? c
    case _  ⇒ log.info("received unknown message")
  }
}
