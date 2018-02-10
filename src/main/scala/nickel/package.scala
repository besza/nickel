import slick.jdbc.HsqldbProfile.api._

import java.sql.{Date, Timestamp}
import java.time.{Instant, LocalDate}
import scala.util.{Failure, Success, Try}

package object nickel {

  implicit class TryOptOps[A](val opt: Option[A]) extends AnyVal {
     def tryOpt[B](f: A => B, error: String): Either[String, Option[B]] =
       opt.map(x => Try(f(x))) match {
         case Some(Success(x)) => Right(Some(x))
         case Some(Failure(_)) => Left(error)
         case None => Right(None)
       }
  }

  implicit def idColumnType[T] = MappedColumnType.base[Id[T], Long](_.value, Id(_))
  implicit val moneyColumnType = MappedColumnType.base[Money, Int](_.cents, Money(_))
  implicit val instantColumnType = MappedColumnType.base[Instant, Timestamp](Timestamp.from, _.toInstant)
  implicit val localDateColumnType = MappedColumnType.base[LocalDate, Date](Date.valueOf, _.toLocalDate)

  object DbFun {
    val year = SimpleFunction.unary[LocalDate, Int]("YEAR")
    val month = SimpleFunction.unary[LocalDate, Int]("MONTH")
  }
}
