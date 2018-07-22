package nickel

import nickel.common.{ApiResponse, Created, Ok}

import scala.concurrent.{ExecutionContext, Future}

class AccountController(
  accountRepository: AccountRepository,
  database: common.DatabaseProfile.api.Database
)(implicit ec: ExecutionContext) {

  def getAll: Future[ApiResponse[Seq[Account.Stored]]] =
    database.run { accountRepository.all }
      .map(Ok(_))

  def create(account: Account): Future[ApiResponse[Account.Stored]] =
    database.run { accountRepository.create(account) }
      .map(Created(_))
}
