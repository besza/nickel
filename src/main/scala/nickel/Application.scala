package nickel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.flywaydb.core.Flyway
import slick.jdbc.HsqldbProfile.api._

object Application {
  def main(args: Array[String]): Unit = {
    val configuration = Configuration.fromSystem

    val flyway = new Flyway
    flyway.setDataSource(configuration.dbUrl, "SA", "")
    flyway.migrate()

    val database = Database.forURL(
      url = configuration.dbUrl,
      driver = "org.hsqldb.jdbc.JDBCDriver"
    )
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val accountRepository = new AccountRepository(database)
    val transactionRepository = new TransactionRepository(database)

    val accountController = new AccountController(accountRepository)
    val transactionController = new TransactionController(transactionRepository)

    val route =
      pathPrefix("api") {
        accountController.routes ~
        transactionController.routes
      } ~
      pathSingleSlash { getFromResource("webroot/index.html") } ~
      getFromResourceDirectory("webroot")

    Http().bindAndHandle(route, configuration.serverHost, configuration.serverPort)
  }
}
