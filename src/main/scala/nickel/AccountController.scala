package nickel

import scala.concurrent.{ExecutionContext, Future}

class AccountController(
  accountRepository: AccountRepository
)(implicit ec: ExecutionContext) {

  def getAll: Future[ApiResponse] =
    accountRepository.all.map(Ok(_))

  def create(account: Account): Future[ApiResponse] =
    accountRepository.create(account).map(Created(_))
}
