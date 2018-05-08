package ciklum.backend.test

import org.joda.time.DateTime

class MyTokenService extends SyncTokenService {

  val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"

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
