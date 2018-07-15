name := "nickel"
version := "0.1"
scalaVersion := "2.12.4"

libraryDependencies ++= Vector(
  "com.typesafe.akka" %% "akka-http"   % "10.1.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.14",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "org.flywaydb" % "flyway-core" % "5.1.4",
  "org.hsqldb" % "hsqldb" % "2.4.1",
  "com.typesafe" % "config" % "1.3.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

mainClass := Some("nickel.Application")
