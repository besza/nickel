package nickel

import io.vertx.core.json.JsonObject
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.jdbc.JDBCClient
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.StaticHandler
import org.flywaydb.core.Flyway
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.Future
import scala.util.{Failure, Success}

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
    val router = Router.router(vertx)

    router
      .get("/api/accounts")
      .produces("application/json")
      .handler { ctx =>
        accountRepository.getAccounts.onComplete {
          case Success(accounts) =>
            val json = Json.toJson(accounts)
            ctx.response
              .setStatusCode(200)
              .end(json.toString)
          case Failure(ex) =>
            ex.printStackTrace()
            ctx.response
              .setStatusCode(500)
              .end(ex.toString)
        }
      }

    router
      .post("/api/accounts")
      .consumes("application/json")
      .produces("application/json")
      .handler { ctx =>
        ctx.request.bodyHandler { body =>
          Json.parse(body.toString()).validate[Account] match {
            case JsSuccess(account, _) if account.valid =>
              accountRepository.createAccount(account).onComplete {
                case Success(accountWithId) =>
                  val json = Json.toJson(accountWithId)
                  ctx.response
                    .setStatusCode(201)
                    .end(json.toString)
                case Failure(ex) =>
                  ex.printStackTrace()
                  ctx.response
                    .setStatusCode(500)
                    .end(ex.toString)
              }
            case JsSuccess(_, _) | JsError(_) =>
              ctx.response
                .setStatusCode(400)
                .end()
          }
        }
      }

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
