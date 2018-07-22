package nickel

import akka.http.scaladsl.server.Directives._

import java.time.YearMonth
import scala.concurrent.ExecutionContext

class TransactionController(
  transactionRepository: TransactionRepository
)(implicit ec: ExecutionContext) {
  import common.Matchers._

  val routes =
    (get & path("transactions") & parameters("month".as[YearMonth].?, "account".as[Id[Account]].?)) { (month, account) =>
      transactionRepository.filtered(month, account).map(Ok(_)).route
    } ~
    (post & path("transactions") & body[Transaction]) { transaction =>
      transactionRepository.create(transaction).map(Created(_)).route
    } ~
    (put & path("transactions" / IdPath[Transaction]) & body[Transaction]) { (id, transaction) =>
      transactionRepository.update(id, transaction).map {
        case Some(updated) => Ok(updated)
        case None => NotFound
      }.route
    } ~
    (delete & path("transactions" / IdPath[Transaction])) { id =>
      transactionRepository.delete(id).map(if (_) NoContent else NotFound).route
    } ~
    (get & path("transactions" / "months")) {
      transactionRepository.months.map(Ok(_)).route
    }
}
