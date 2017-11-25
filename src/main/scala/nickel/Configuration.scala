package nickel

import scala.util.Try

case class Configuration(
  serverHost: String,
  serverPort: Int,
  dbUrl: String
)

object Configuration {
  def fromSystem: Configuration = Configuration(
    serverHost = sys.env.getOrElse("NICKEL_HOST", "localhost"),
    serverPort = sys.env.get("NICKEL_PORT").flatMap(s => Try(s.toInt).toOption).getOrElse(8666),
    dbUrl = sys.env.getOrElse("NICKEL_DB_URL", "jdbc:hsqldb:mem:nickel")
  )
}
