package nickel

import play.api.libs.json._

import java.time.{Instant, LocalDate}

case class Transaction(
  from: Id[Account],
  to: Id[Account],
  on: LocalDate,
  amount: Money,
  description: String
) {
  def tracked(withId: Id[Transaction], withCreatedAt: Instant): Transaction.Tracked =
    new Transaction(from, to, on, amount, description)
      with Tracked[Transaction] { val id = withId; val createdAt = withCreatedAt }
}

object Transaction {
  type Tracked = Transaction with nickel.Tracked[Transaction]

  implicit val reads: Reads[Transaction] = Json.reads[Transaction]
  implicit val trackedWrites: OWrites[Transaction.Tracked] = Tracked.writes(Json.writes[Transaction])

  implicit val validator: Validator[Transaction] = Validator {
    Right(_)
      .filterOrElse(t => t.from != t.to, "Transaction source and destination must be different")
      .filterOrElse(_.amount.cents > 0, "Transaction amount must be positive")
  }
}
