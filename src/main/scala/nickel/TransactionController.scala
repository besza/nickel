package nickel

import nickel.JsonImplicits._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}
import java.time.YearMonth

import io.vertx.scala.ext.web.Router

class TransactionController(
  router: Router,
  transactionRepository: TransactionRepository
)(implicit ec: ExecutionContext) {

  Endpoint.get(
    router,
    path = "/api/transactions",
    produceInput = { req =>
      for {
        month <- req.getParam("month").tryOpt(YearMonth.parse, "Invalid month")
        account <- req.getParam("account").tryOpt(str => Id[Account](str.toLong), "Invalid account id")
      } yield (month, account)
    },
    produceOutput = (transactionRepository.filtered _).tupled
  )

  Endpoint.post(
    router,
    path = "/api/transactions",
    produceInput = (_, transaction: Transaction) => transaction.validate,
    produceOutput = transactionRepository.create
  )

  Endpoint.put(
    router,
    path = "/api/transactions/:id",
    produceInput = { (req, transaction: Transaction) =>
      for {
        id <- req.getParam("id")
          .flatMap { str => Try(str.toLong).toOption }
          .toRight("Invalid id")
        validTransaction <- transaction.validate
      } yield (Id[Transaction](id), validTransaction)
    },
    produceOutput = (transactionRepository.update _).tupled
  )

  Endpoint.get(
    router,
    path = "/api/transactions/months",
    produceInput = Function.const(Right(Unit)),
    produceOutput = (_: Unit.type) => transactionRepository.months
  )

}
