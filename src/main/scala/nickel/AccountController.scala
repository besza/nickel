package nickel

import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext

class AccountController(
  accountRepository: AccountRepository,
  transactionRepository: TransactionRepository
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
      } ~
      pathPrefix(LongNumber.map(Id[Account])) { accountId =>
        pathPrefix("balance") {
          (get & parameters("with".as[Id[Account]].?)) { withOpt =>
            complete {
              for {
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
              }
            }
          }
        }
      }
    }
}
