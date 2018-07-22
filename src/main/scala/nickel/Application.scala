package nickel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.flywaydb.core.Flyway
import slick.jdbc.HsqldbProfile.api._

object Application {
  def main(args: Array[String]): Unit = {
    val conf = Configuration.load()

    val flyway = new Flyway
    flyway.setDataSource(conf.database.url, conf.database.user, conf.database.password)
    flyway.migrate()

    val database = Database.forConfig("database")
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val accountRepository = new AccountRepository
    val transactionRepository = new TransactionRepository

    val accountController = new AccountController(accountRepository, database)
    val transactionController = new TransactionController(transactionRepository, database)

    val routing = new Routing(accountController, transactionController)

    val route =
      pathPrefix("api") { routing.routes } ~
      pathSingleSlash { getFromResource("webroot/index.html") } ~
      getFromResourceDirectory("webroot")

    Http().bindAndHandle(route, conf.server.host, conf.server.port)
  }
}
