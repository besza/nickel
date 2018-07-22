package nickel

import slick.jdbc.HsqldbProfile.api._

import java.time.{Instant, LocalDate, YearMonth}
import scala.concurrent.{ExecutionContext, Future}

private class TransactionTable(tag: Tag) extends Table[Transaction.Tracked](tag, "transaction") {
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
    Transaction(from, to, on, amount, description).tracked(id, createdAt)
  def toDb(t: Transaction.Tracked) =
    Some((t.id, t.createdAt, t.from, t.to, t.on, t.amount, t.description))
}

class TransactionRepository(database: Database)(implicit ec: ExecutionContext) {
  private val table = TableQuery[TransactionTable]
  private val insert =
    table returning table.map(_.id) into
    ((inserted, id) => inserted.tracked(id, inserted.createdAt))

  def single(id: Id[Transaction]): Future[Option[Transaction.Tracked]] =
    database.run { table.filter(_.id === id).result.headOption }

  def filtered(month: Option[YearMonth], account: Option[Id[Account]]): Future[Seq[Transaction.Tracked]] = {
    val monthFiltered = month
      .map { m => table.filter { t => DbFun.year(t.on) === m.getYear && DbFun.month(t.on) === m.getMonthValue } }
      .getOrElse(table)
    val accountFiltered = account
      .map { a => monthFiltered.filter { t => t.from === a || t.to === a } }
      .getOrElse(monthFiltered)
    database.run { accountFiltered.sortBy(_.on).result }
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

  def monthlySums(from: Option[Id[Account]] = None, to: Option[Id[Account]] = None): Future[Map[YearMonth, Money]] =
    database.run {
      val filtered = (from, to) match {
        case (Some(f), Some(t)) => table.filter(_.from === f).filter(_.to === t)
        case (Some(f), None) => table.filter(_.from === f)
        case (None, Some(t)) => table.filter(_.to === t)
        case (None, None) => table
      }
      filtered
        .groupBy { t => (DbFun.year(t.on), DbFun.month(t.on)) }
        .map { case ((year, month), group) => (year, month, group.map(_.amount).sum)}
        .result
        .map(_.collect { case (year, month, Some(sum)) => (YearMonth.of(year, month), sum)}.toMap)
    }

  def create(transaction: Transaction): Future[Transaction.Tracked] = {
    val createdAt = Instant.now
    database.run { insert += transaction.tracked(Id(0), createdAt) }
  }

  def update(id: Id[Transaction], transaction: Transaction): Future[Option[Transaction.Tracked]] =
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
