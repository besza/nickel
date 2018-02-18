package nickel

import play.api.libs.json._

import java.time.{Instant, LocalDate}

case class Transaction(
  from: Id[Account],
  to: Id[Account],
  on: LocalDate,
  amount: Money,
  description: String
)

object Transaction {
  implicit val format: OFormat[Transaction] = Json.format

  implicit val validator: Validator[Transaction] = Validator {
    Right(_)
      .filterOrElse(t => t.from != t.to, "Transaction source and destination must be different")
      .filterOrElse(_.amount.cents > 0, "Transaction amount must be positive")
  }
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
