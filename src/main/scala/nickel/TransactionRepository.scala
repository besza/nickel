package nickel

import scala.concurrent.Future

import java.time.{Instant, YearMonth}

class TransactionRepository {
  var values: List[StoredTransaction] = List.empty

  def inMonth(month: YearMonth): Future[List[StoredTransaction]] =
    Future.successful(
      values
        .filter { st => YearMonth.from(st.transaction.on) == month }
        .sortBy(_.transaction.on.toEpochDay)
    )

  def months: Future[List[YearMonth]] =
    Future.successful(
      values
        .map { st => YearMonth.from(st.transaction.on)}
        .distinct
        .sorted
    )

  def create(transaction: Transaction): Future[StoredTransaction] = {
    val id = Id[Transaction](values.size)
    val stored = StoredTransaction(id, Instant.now, transaction)
    values = values :+ stored
    Future.successful(stored)
  }
}
