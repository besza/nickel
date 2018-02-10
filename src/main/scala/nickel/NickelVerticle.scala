package nickel

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.StaticHandler
import org.flywaydb.core.Flyway
import slick.jdbc.HsqldbProfile.api._

import scala.concurrent.Future

class NickelVerticle extends ScalaVerticle {
  override def startFuture(): Future[Unit] = {
    val configuration = Configuration.fromSystem

    val flyway = new Flyway
    flyway.setDataSource(configuration.dbUrl, "SA", "")
    flyway.migrate()

    val database = Database.forURL(
      url = configuration.dbUrl,
      driver = "org.hsqldb.jdbc.JDBCDriver"
    )
    val accountRepository = new AccountRepository(database)
    val transactionRepository = new TransactionRepository(database)

    val router = Router.router(vertx)
    new AccountController(router, accountRepository)
    new TransactionController(router, transactionRepository)

    router
      .route("/*")
      .handler(StaticHandler.create)

    vertx
      .createHttpServer()
      .requestHandler(router.accept)
      .listenFuture(configuration.serverPort, configuration.serverHost)
      .map(_ => ())
  }
}
