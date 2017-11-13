package nickel

import play.api.libs.json._

case class Account(
  name: String
) {
  def validate: Either[String, Account] =
    Right(this)
      .filterOrElse(_.name.nonEmpty, "Account name must not be empty")
}

object Account {
  implicit val format: OFormat[Account] = Json.format
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
