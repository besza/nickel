package nickel

import nickel.JsonImplicits._

import scala.concurrent.ExecutionContext
import scala.util.Try

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
      req.getParam("month")
        .toRight("Missing parameter: month")
        .flatMap { str => Try(YearMonth.parse(str)).toOption.toRight("Invalid month") }
    },
    produceOutput = transactionRepository.inMonth
  )

  Endpoint.post(
    router,
    path = "/api/transactions",
    produceInput = (_, transaction: Transaction) => transaction.validate,
    produceOutput = transactionRepository.create
  )

  Endpoint.get(
    router,
    path = "/api/transactions/months",
    produceInput = Function.const(Right(Unit)),
    produceOutput = (_: Unit.type) => transactionRepository.months
  )

}
