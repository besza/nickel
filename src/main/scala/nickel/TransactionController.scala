package nickel

import java.time.YearMonth
import scala.concurrent.{ExecutionContext, Future}

class TransactionController(
  transactionRepository: TransactionRepository,
  database: common.DatabaseProfile.api.Database
)(implicit ec: ExecutionContext) {

  def getAll(month: Option[YearMonth], accountId: Option[Id[Account]]): Future[ApiResponse[Seq[Transaction.Tracked]]] =
    database.run { transactionRepository.filtered(month, accountId) }
      .map(Ok(_))

  def create(transaction: Transaction): Future[ApiResponse[Transaction.Tracked]] =
    database.run { transactionRepository.create(transaction) }
      .map(Created(_))

  def update(id: Id[Transaction], transaction: Transaction): Future[ApiResponse[Transaction.Tracked]] =
    database.run { transactionRepository.update(id, transaction) }
      .map {
        case Some(updated) => Ok(updated)
        case None => NotFound
      }

  def delete(id: Id[Transaction]): Future[ApiResponse[Nothing]] =
    database.run { transactionRepository.delete(id) }
      .map(if (_) NoContent else NotFound)

  def getMonths: Future[ApiResponse[Seq[YearMonth]]] =
    database.run { transactionRepository.months }
      .map(Ok(_))

  def getBalance(accountId: Id[Account], withAccountId: Option[Id[Account]]): Future[ApiResponse[Seq[MonthlyBalance]]] =
    for {
      sumsFrom <- database.run { transactionRepository.monthlySums(from = Some(accountId), to = withAccountId) }
      sumsTo <- database.run { transactionRepository.monthlySums(to = Some(accountId), from = withAccountId) }
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
