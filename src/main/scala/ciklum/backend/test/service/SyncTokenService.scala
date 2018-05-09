package ciklum.backend.test.service

import ciklum.backend.test.{Credentials, User, UserToken}

trait SyncTokenService {
  protected def authenticate(credentials: Credentials): User
  protected def issueToken(user: User): UserToken

  def requestToken(credentials: Credentials): UserToken = ???
}
