package nickel

import java.time.YearMonth
import scala.concurrent.{ExecutionContext, Future}

class TransactionController(
  transactionRepository: TransactionRepository
)(implicit ec: ExecutionContext) {

  def getAll(month: Option[YearMonth], accountId: Option[Id[Account]]): Future[ApiResponse] =
    transactionRepository.filtered(month, accountId).map(Ok(_))

  def create(transaction: Transaction): Future[ApiResponse] =
    transactionRepository.create(transaction).map(Created(_))

  def update(id: Id[Transaction], transaction: Transaction): Future[ApiResponse] =
    transactionRepository.update(id, transaction).map {
      case Some(updated) => Ok(updated)
      case None => NotFound
    }

  def delete(id: Id[Transaction]): Future[ApiResponse] =
    transactionRepository.delete(id).map(if (_) NoContent else NotFound)

  def getMonths: Future[ApiResponse] =
    transactionRepository.months.map(Ok(_))

  def getBalance(accountId: Id[Account], withAccountId: Option[Id[Account]]): Future[ApiResponse] =
    for {
      sumsFrom <- transactionRepository.monthlySums(from = Some(accountId), to = withAccountId)
      sumsTo <- transactionRepository.monthlySums(to = Some(accountId), from = withAccountId)
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
