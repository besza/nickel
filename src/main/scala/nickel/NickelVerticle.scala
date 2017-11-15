package nickel

import scala.concurrent.Future

import io.vertx.core.json.JsonObject
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.jdbc.JDBCClient
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.StaticHandler
import org.flywaydb.core.Flyway

class NickelVerticle extends ScalaVerticle {
  val jdbcConfig: JsonObject = new JsonObject()
    .put("url", "jdbc:hsqldb:mem:nickel")
    .put("driver_class", "org.hsqldb.jdbc.JDBCDriver")

  override def startFuture(): Future[Unit] = {
    val flyway = new Flyway
    flyway.setDataSource("jdbc:hsqldb:mem:nickel", "SA", "")
    flyway.migrate()

    val jdbcClient = JDBCClient.createShared(vertx, jdbcConfig)
    val accountRepository = new AccountRepository(jdbcClient)
    val transactionRepository = new TransactionRepository(jdbcClient)

    val router = Router.router(vertx)
    new AccountController(router, accountRepository)
    new TransactionController(router, transactionRepository)

    router
      .route("/*")
      .handler(StaticHandler.create)

    vertx
      .createHttpServer()
      .requestHandler(router.accept)
      .listenFuture(8666, "0.0.0.0")
      .map(_ => ())
  }
}
