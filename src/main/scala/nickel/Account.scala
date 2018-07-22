package nickel

import nickel.common.{Id, Validator}

import play.api.libs.json._

case class Account(
  name: String
) {
  def stored(withId: Id[Account]): Account.Stored =
    new Account(name) with Stored[Account] { val id = withId }
}

object Account {
  type Stored = Account with nickel.Stored[Account]

  implicit val reads: Reads[Account] = Json.reads
  implicit val storedWrites: OWrites[Stored] = Stored.writes(Json.writes[Account])

  implicit val validator: Validator[Account] = Validator {
    Right(_).filterOrElse(_.name.nonEmpty, "Account name must not be empty")
  }
}
