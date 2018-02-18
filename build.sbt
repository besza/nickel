name := "nickel"
version := "0.1"
scalaVersion := "2.12.3"

libraryDependencies ++= Vector(
  "com.typesafe.akka" %% "akka-http"   % "10.1.0-RC1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.8",
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "com.typesafe.play" %% "play-json" % "2.6.6",
  "org.flywaydb" % "flyway-core" % "4.2.0",
  "org.hsqldb" % "hsqldb" % "2.4.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

mainClass := Some("nickel.Application")
