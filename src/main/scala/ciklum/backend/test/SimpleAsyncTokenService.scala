package ciklum.backend.test

import scala.concurrent.Future

trait SimpleAsyncTokenService {
  def requestToken(credentials: Credentials): Future[UserToken]
}
