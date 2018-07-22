package nickel

import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext

class AccountController(
  accountRepository: AccountRepository,
  transactionRepository: TransactionRepository
)(implicit ec: ExecutionContext) {
  import common.Matchers._

  val routes =
    (get & path("accounts")) {
      accountRepository.all.map(Ok(_)).route
    } ~
    (post & path("accounts") & body[Account]) { account =>
      accountRepository.create(account).map(Created(_)).route
    } ~
    (get & path("accounts" / IdPath[Account] / "balance") & parameters("with".as[Id[Account]].?)) { (accountId, withOpt) =>
      (for {
        sumsFrom <- transactionRepository.monthlySums(from = Some(accountId), to = withOpt)
        sumsTo <- transactionRepository.monthlySums(to = Some(accountId), from = withOpt)
      } yield {
        val months = sumsFrom.keySet ++ sumsTo.keySet
        val balances = months.toList.sorted.map { month =>
          val in = sumsTo.getOrElse(month, Money(0))
          val out = sumsFrom.getOrElse(month, Money(0))
          MonthlyBalance(month, in, out, Money(in.cents - out.cents))
        }
        Ok(balances)
      }).route
    }
}
