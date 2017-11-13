package nickel

import scala.concurrent.ExecutionContext

import io.vertx.scala.ext.web.Router

class AccountController(
  router: Router,
  accountRepository: AccountRepository
)(implicit ec: ExecutionContext) {

  Endpoint.get(
    router,
    path = "/api/accounts",
    produceInput = Function.const(Right(Unit)),
    produceOutput = (_: Unit.type) => accountRepository.all
  )

  Endpoint.post(
    router,
    path = "/api/accounts",
    produceInput = (_, account: Account) => account.validate,
    produceOutput = accountRepository.create
  )
}
