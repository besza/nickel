package nickel

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import io.vertx.scala.core.http.{HttpServerRequest, HttpServerResponse}
import io.vertx.scala.ext.web.{Route, Router}
import play.api.libs.json._

object Endpoint {

  def get[A, B: Writes](
    router: Router,
    path: String,
    produceInput: HttpServerRequest => Either[String, A],
    produceOutput: A => Future[B]
  )(implicit ec: ExecutionContext): Route =
    router
      .get(path)
      .produces("application/json")
      .handler { ctx =>
        val input = produceInput(ctx.request)
        handleInput(ctx.response, input, produceOutput, 200)
      }

  def post[A, B: Writes, C: Reads](
    router: Router,
    path: String,
    produceInput: (HttpServerRequest, C) => Either[String, A],
    produceOutput: A => Future[B]
  )(implicit ec: ExecutionContext): Route =
    router
      .post(path)
      .consumes("application/json")
      .produces("application/json")
      .handler { ctx =>
        val request = ctx.request
        request.bodyHandler { body =>
          Json.parse(body.toString()).validate[C] match {
            case JsSuccess(bodyInput, _) =>
              val input = produceInput(ctx.request, bodyInput)
              handleInput(ctx.response, input, produceOutput, 201)
            case JsError(errors) =>
              ctx.response
                .setStatusCode(400)
                .end(errors.toString)
          }
        }
      }

  private def handleInput[A, B: Writes](
    response: HttpServerResponse,
    input: Either[String, A],
    produceOutput: A => Future[B],
    successStatusCode: Int
  )(implicit ec: ExecutionContext): Unit =
    input match {
      case Right(validInput) =>
        produceOutput(validInput).onComplete {
          case Success(output) =>
            val json = Json.toJson(output)
            response
              .setStatusCode(successStatusCode)
              .end(json.toString)
          case Failure(ex) =>
            ex.printStackTrace()
            response
              .setStatusCode(500)
              .end(ex.toString)
        }
      case Left(error) =>
        response
          .setStatusCode(400)
          .end(error)
    }
}
