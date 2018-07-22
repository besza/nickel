package nickel

import common.DatabaseProfile.api._
import common.DatabaseProfile.Mappers._

private class AccountTable(tag: Tag) extends Table[Account.Stored](tag, "account") {
  def id = column[Id[Account]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def * = (id, name) <> ((fromDb _).tupled, toDb)
  def fromDb(id: Id[Account], name: String) = Account(name).stored(id)
  def toDb(a: Account.Stored) = Some((a.id, a.name))
}

class AccountRepository {
  private val table = TableQuery[AccountTable]
  private val insert =
    table returning table.map(_.id) into
    ((inserted, id) => inserted.stored(id))

  def all: DBIO[Seq[Account.Stored]] =
    table.result

  def create(account: Account): DBIO[Account.Stored] =
    insert += account.stored(Id(0))
}
