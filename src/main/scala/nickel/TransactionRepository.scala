package nickel

import slick.jdbc.HsqldbProfile.api._

import java.time.{Instant, LocalDate, YearMonth}
import scala.concurrent.{ExecutionContext, Future}

private class TransactionTable(tag: Tag) extends Table[StoredTransaction](tag, "transaction") {
  def id = column[Id[Transaction]]("id", O.PrimaryKey, O.AutoInc)
  def createdAt = column[Instant]("created_at")
  def from = column[Id[Account]]("from")
  def to = column[Id[Account]]("to")
  def on = column[LocalDate]("on")
  def amount = column[Money]("amount")
  def description = column[String]("description")

  def * = (id, createdAt, from, to, on, amount, description) <> ((fromDb _).tupled, toDb)
  def fromDb(id: Id[Transaction], createdAt: Instant, from: Id[Account], to: Id[Account], on: LocalDate,
    amount: Money, description: String) =
    StoredTransaction(id, createdAt, Transaction(from, to, on, amount, description))
  def toDb(t: StoredTransaction) =
    Some((t.id, t.createdAt, t.transaction.from, t.transaction.to, t.transaction.on,
      t.transaction.amount, t.transaction.description))
}

class TransactionRepository(database: Database)(implicit ec: ExecutionContext) {
  private val table = TableQuery[TransactionTable]
  private val insert =
    table returning table.map(_.id) into
    ((inserted, id) => StoredTransaction(id, inserted.createdAt, inserted.transaction))

  def single(id: Id[Transaction]): Future[Option[StoredTransaction]] =
    database.run { table.filter(_.id === id).result.headOption }

  def filtered(month: Option[YearMonth], account: Option[Id[Account]]): Future[Seq[StoredTransaction]] = {
    val monthFiltered = month
      .map { m => table.filter { t => DbFun.year(t.on) === m.getYear && DbFun.month(t.on) === m.getMonthValue } }
      .getOrElse(table)
    val accountFiltered = account
      .map { a => monthFiltered.filter { t => t.from === a || t.to === a } }
      .getOrElse(monthFiltered)
    database.run { accountFiltered.result }
  }

  def months: Future[Seq[YearMonth]] =
    database.run {
      table
        .map { t => (DbFun.year(t.on), DbFun.month(t.on)) }
        .distinct
        .sorted
        .result
        .map(_.map { case (y, m) => YearMonth.of(y, m) })
    }

  def create(transaction: Transaction): Future[StoredTransaction] = {
    val createdAt = Instant.now
    database.run { insert += StoredTransaction(Id(0), createdAt, transaction) }
  }

  def update(id: Id[Transaction], transaction: Transaction): Future[Option[StoredTransaction]] =
    database
      .run {
        table
          .filter(_.id === id)
          .map { t => (t.from, t.to, t.on, t.amount, t.description )}
          .update((transaction.from, transaction.to, transaction.on, transaction.amount, transaction.description))
      }
      .flatMap {
        case 0 => Future.successful(None)
        case _ => single(id)
      }

  def delete(id: Id[Transaction]): Future[Boolean] =
    database.run { table.filter(_.id === id).delete.map(_ > 0) }
}
