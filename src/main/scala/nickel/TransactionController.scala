package nickel

import akka.http.scaladsl.server.Directives._

import java.time.YearMonth
import scala.concurrent.ExecutionContext

class TransactionController(
  transactionRepository: TransactionRepository
)(implicit ec: ExecutionContext) {
  val routes =
    pathPrefix("transactions") {
      pathEnd {
        (get & parameters('month.as[YearMonth].?, 'account.as[Id[Account]].?)) { (month, account) =>
          complete { transactionRepository.filtered(month, account).map(Ok(_)) }
        } ~
        (post & entity(as[Transaction])) { transaction =>
          complete { transactionRepository.create(transaction).map(Created(_)) }
        }
      } ~
      path(LongNumber.map(Id[Transaction])) { id =>
        (put & entity(as[Transaction])) { transaction =>
          complete { transactionRepository.update(id, transaction).map {
            case Some(updated) => Ok(updated)
            case None => NotFound
          } }
        } ~
        delete {
          complete { transactionRepository.delete(id).map(if (_) NoContent else NotFound) }
        }
      } ~
      path("months") {
        get {
          complete { transactionRepository.months.map(Ok(_)) }
        }
      }
    }
}
