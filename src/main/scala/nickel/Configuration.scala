package nickel

import com.typesafe.config.ConfigFactory

final case class Configuration(
  server: ServerConfiguration,
  database: DatabaseConfiguration,
)

final case class ServerConfiguration(
  host: String,
  port: Int
)

final case class DatabaseConfiguration(
  url: String,
  driver: String,
  user: String,
  password: String
)

object Configuration {
  def load(): Configuration = {
    val conf = ConfigFactory.load()
    Configuration(
      ServerConfiguration(
        host = conf.getString("server.host"),
        port = conf.getInt("server.port"),
      ),
      DatabaseConfiguration(
        url = conf.getString("database.url"),
        driver = conf.getString("database.driver"),
        user = conf.getString("database.user"),
        password = conf.getString("database.password")
      )
    )
  }
}
