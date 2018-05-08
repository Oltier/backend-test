package ciklum.backend.test

import akka.actor.Actor
import akka.event.Logging
import org.joda.time.DateTime

class TokenIssueActor extends Actor with SyncTokenService {
  val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"
  val log = Logging(context.system, this)

  def receive = {
    case c: Credentials => {
      println("got mesasge " + c)
      val token = requestToken(c)
      sender ! token
    }
    case _      ⇒ log.info("received unknown message")
  }



  override protected def authenticate(credentials: Credentials): User = {
    User(credentials.username)
  }

  override protected def issueToken(user: User): UserToken = {
    import org.joda.time.format.DateTimeFormat
    val dtfOut = DateTimeFormat.forPattern(format)
    UserToken(user.userId + "_" +  dtfOut.print(new DateTime()))
  }

  override def requestToken(credentials: Credentials): UserToken = {
    val user = authenticate(credentials)
    issueToken(user)
  }

}