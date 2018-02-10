package nickel

import slick.jdbc.HsqldbProfile.api._

import scala.concurrent.{ExecutionContext, Future}

private class AccountTable(tag: Tag) extends Table[StoredAccount](tag, "account") {
  def id = column[Id[Account]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def * = (id, name) <> ((fromDb _).tupled, toDb)
  def fromDb(id: Id[Account], name: String) = StoredAccount(id, Account(name))
  def toDb(a: StoredAccount) = Some((a.id, a.account.name))
}

class AccountRepository(database: Database)(implicit ec: ExecutionContext) {
  private val table = TableQuery[AccountTable]
  private val insert =
    table returning table.map(_.id) into
    ((inserted, id) => StoredAccount(id, inserted.account))

  def all: Future[Seq[StoredAccount]] =
    database.run { table.result }

  def create(account: Account): Future[StoredAccount] =
    database.run { insert += StoredAccount(Id(0), account) }
}
