package nickel

import nickel.common.Id

import akka.http.scaladsl.server.Directives._

import java.time.YearMonth
import scala.concurrent.ExecutionContext

class Routing(
  accountController: AccountController,
  transactionController: TransactionController
)(implicit ec: ExecutionContext) {
  import common.Matchers._

  def routes =
    (get & path("accounts")) {
      accountController.getAll.route
    } ~
    (post & path("accounts") & body[Account]) { account =>
      accountController.create(account).route
    } ~
    (get & path("accounts" / IdPath[Account] / "balance") & parameters("with".as[Id[Account]].?)) { (accountId, withOpt) =>
      transactionController.getBalance(accountId, withOpt).route
    } ~
    (get & path("transactions") & parameters("month".as[YearMonth].?, "account".as[Id[Account]].?)) { (month, accountId) =>
      transactionController.getAll(month, accountId).route
    } ~
    (post & path("transactions") & body[Transaction]) { transaction =>
      transactionController.create(transaction).route
    } ~
    (put & path("transactions" / IdPath[Transaction]) & body[Transaction]) { (id, transaction) =>
      transactionController.update(id, transaction).route
    } ~
    (delete & path("transactions" / IdPath[Transaction])) { id =>
      transactionController.delete(id).route
    } ~
    (get & path("transactions" / "months")) {
      transactionController.getMonths.route
    }
}
