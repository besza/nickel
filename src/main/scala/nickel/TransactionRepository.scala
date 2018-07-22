package nickel

import common.DatabaseProfile.api._
import common.DatabaseProfile.Mappers._
import common.DatabaseProfile.Funs

import java.time.{Instant, LocalDate, YearMonth}
import scala.concurrent.ExecutionContext

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

class TransactionRepository(implicit ec: ExecutionContext) {
  private val table = TableQuery[TransactionTable]
  private val insert =
    table returning table.map(_.id) into
    ((inserted, id) => inserted.tracked(id, inserted.createdAt))

  def single(id: Id[Transaction]): DBIO[Option[Transaction.Tracked]] =
    table.filter(_.id === id).result.headOption

  def filtered(month: Option[YearMonth], account: Option[Id[Account]]): DBIO[Seq[Transaction.Tracked]] = {
    val monthFiltered = month
      .map { m => table.filter { t => Funs.year(t.on) === m.getYear && Funs.month(t.on) === m.getMonthValue } }
      .getOrElse(table)
    val accountFiltered = account
      .map { a => monthFiltered.filter { t => t.from === a || t.to === a } }
      .getOrElse(monthFiltered)
    accountFiltered.sortBy(_.on).result
  }

  def months: DBIO[Seq[YearMonth]] =
    table
      .map { t => (Funs.year(t.on), Funs.month(t.on)) }
      .distinct
      .sorted
      .result
      .map(_.map { case (y, m) => YearMonth.of(y, m) })

  def monthlySums(from: Option[Id[Account]] = None, to: Option[Id[Account]] = None): DBIO[Map[YearMonth, Money]] = {
    val filtered = (from, to) match {
      case (Some(f), Some(t)) => table.filter(_.from === f).filter(_.to === t)
      case (Some(f), None) => table.filter(_.from === f)
      case (None, Some(t)) => table.filter(_.to === t)
      case (None, None) => table
    }
    filtered
      .groupBy { t => (Funs.year(t.on), Funs.month(t.on)) }
      .map { case ((year, month), group) => (year, month, group.map(_.amount).sum)}
      .result
      .map(_.collect { case (year, month, Some(sum)) => (YearMonth.of(year, month), sum)}.toMap)
  }

  def create(transaction: Transaction): DBIO[Transaction.Tracked] = {
    val createdAt = Instant.now
    insert += transaction.tracked(Id(0), createdAt)
  }

  def update(id: Id[Transaction], transaction: Transaction): DBIO[Option[Transaction.Tracked]] =
    table
      .filter(_.id === id)
      .map { t => (t.from, t.to, t.on, t.amount, t.description )}
      .update((transaction.from, transaction.to, transaction.on, transaction.amount, transaction.description))
      .flatMap {
        case 0 => DBIO.successful(None)
        case _ => single(id)
      }

  def delete(id: Id[Transaction]): DBIO[Boolean] =
    table.filter(_.id === id).delete.map(_ > 0)
}
