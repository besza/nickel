package nickel.common

import nickel.{ApiResponse, Id}

import akka.http.scaladsl.server.Directives.{as, complete, entity}
import akka.http.scaladsl.server.PathMatchers.LongNumber
import akka.http.scaladsl.server.{Directive1, PathMatcher1, StandardRoute}
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller

import scala.concurrent.Future

object Matchers {
  def IdPath[T]: PathMatcher1[Id[T]] = LongNumber.map(Id[T])

  def body[T : FromRequestUnmarshaller]: Directive1[T] = entity(as[T])

  implicit class FutureApiResponseOps(x: Future[ApiResponse]) {
    val route: StandardRoute = complete(x)
  }
}
