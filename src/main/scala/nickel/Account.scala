package nickel

import play.api.libs.json._

case class Account(
  name: String
) {
  lazy val valid: Boolean = name.nonEmpty
}

object Account {
  implicit val format: OFormat[Account] = Json.format[Account]
}

case class StoredAccount(
  id: Id[Account],
  account: Account
)

object StoredAccount {
  implicit val reads: Reads[StoredAccount] =
    for {
      id <- (JsPath \ "id").read[Id[Account]]
      account <- Account.format
    } yield StoredAccount(id, account)

  implicit def writes: OWrites[StoredAccount] = {
    case StoredAccount(id, account) =>
      Json.obj("id" -> id) ++ Account.format.writes(account)
  }
}
