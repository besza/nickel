package nickel.common

import nickel.{Id, Money}

import java.sql.{Date, Timestamp}
import java.time.{Instant, LocalDate}

object DatabaseProfile {
  val api = slick.jdbc.HsqldbProfile.api

  object Mappers {
    import api._

    implicit def idColumnType[T] = MappedColumnType.base[Id[T], Long](_.value, Id(_))
    implicit val moneyColumnType = MappedColumnType.base[Money, Int](_.cents, Money(_))
    implicit val instantColumnType = MappedColumnType.base[Instant, Timestamp](Timestamp.from, _.toInstant)
    implicit val localDateColumnType = MappedColumnType.base[LocalDate, Date](Date.valueOf, _.toLocalDate)
  }

  object Funs {
    import api._

    val year = SimpleFunction.unary[LocalDate, Int]("YEAR")
    val month = SimpleFunction.unary[LocalDate, Int]("MONTH")
  }
}
