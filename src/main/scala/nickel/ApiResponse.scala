package nickel

import akka.http.javadsl.model.HttpEntities
import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.{ContentTypes, HttpResponse, StatusCodes}
import play.api.libs.json._

sealed abstract class ApiResponse(val response: HttpResponse)

final case class Ok[T: Writes](t: T) extends ApiResponse(
  ApiResponse.writesToHttpResponse(t).withStatus(StatusCodes.OK)
)
final case class Created[T: Writes](t: T) extends ApiResponse(
  ApiResponse.writesToHttpResponse(t).withStatus(StatusCodes.Created)
)
object NoContent extends ApiResponse(
  HttpResponse(status = StatusCodes.NoContent)
)
object NotFound extends ApiResponse(
  HttpResponse(status = StatusCodes.NotFound)
)

object ApiResponse {
  implicit def apiResponseMarshaller: ToResponseMarshaller[ApiResponse] =
    Marshaller.opaque(_.response)

  def writesToHttpResponse[T: Writes] (t: T): HttpResponse =
    HttpResponse(entity = HttpEntities.create(Json.toJson(t).toString) )
      .mapEntity(_.withContentType(ContentTypes.`application/json`) )
}
