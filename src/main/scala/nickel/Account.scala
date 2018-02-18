package nickel

import play.api.libs.json._

case class Account(
  name: String
)

object Account {
  implicit val format: OFormat[Account] = Json.format

  implicit val validator: Validator[Account] = Validator {
    Right(_).filterOrElse(_.name.nonEmpty, "Account name must not be empty")
  }
}

case class StoredAccount(
  id: Id[Account],
  account: Account
)

object StoredAccount {
  implicit def writes: OWrites[StoredAccount] = {
    case StoredAccount(id, account) =>
      Json.obj("id" -> id) ++ Account.format.writes(account)
  }
}
