package ciklum.backend.test.service

import ciklum.backend.test.{Credentials, UserToken}

import scala.concurrent.Future

trait SimpleAsyncTokenService {
  def requestToken(credentials: Credentials): Future[UserToken]
}
