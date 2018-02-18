package nickel

import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext

class AccountController(
  accountRepository: AccountRepository
)(implicit ec: ExecutionContext) {
  val routes =
    pathPrefix("accounts") {
      pathEnd {
        get {
          complete { accountRepository.all.map(Ok(_)) }
        } ~
        (post & entity(as[Account])) { account =>
          complete { accountRepository.create(account).map(Created(_)) }
        }
      }
    }
}
