package nickel.common

import nickel.{ApiResponse, Id}

import akka.http.scaladsl.server.Directives.{as, complete, entity}
import akka.http.scaladsl.server.PathMatchers.LongNumber
import akka.http.scaladsl.server.{Directive1, PathMatcher1, StandardRoute}
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import play.api.libs.json.Writes

import scala.concurrent.Future

object Matchers {
  def IdPath[T]: PathMatcher1[Id[T]] = LongNumber.map(Id[T])

  def body[T : FromRequestUnmarshaller]: Directive1[T] = entity(as[T])

  implicit class FutureApiResponseOps[T: Writes](x: Future[ApiResponse[T]]) {
    val route: StandardRoute = complete(x)
  }
}
