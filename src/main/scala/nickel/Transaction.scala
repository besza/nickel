package nickel

import java.time.{Instant, LocalDate}

import play.api.libs.json._

case class Transaction(
  from: Id[Account],
  to: Id[Account],
  on: LocalDate,
  amount: Money,
  description: String
) {
  def validate: Either[String, Transaction] =
    Right(this)
      .filterOrElse(t => t.from != t.to, "Transaction source and destination must be different")
      .filterOrElse(_.amount.cents > 0, "Transaction amount must be positive")
}

object Transaction {
  implicit val format: OFormat[Transaction] = Json.format
}

case class StoredTransaction(
  id: Id[Transaction],
  createdAt: Instant,
  transaction: Transaction
)

object StoredTransaction {
  implicit def writes: OWrites[StoredTransaction] = {
    case StoredTransaction(id, createdAt, transaction) =>
      Json.obj("id" -> id, "createdAt" -> createdAt) ++ Transaction.format.writes(transaction)
  }
}
