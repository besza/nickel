name := "nickel"
version := "0.1"
scalaVersion := "2.12.3"

libraryDependencies ++= Vector(
  "io.vertx" %% "vertx-lang-scala" % "3.5.0",
  "io.vertx" %% "vertx-web-scala" % "3.5.0",
  "io.vertx" %% "vertx-jdbc-client-scala" % "3.5.0",
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "com.typesafe.play" %% "play-json" % "2.6.6",
  "org.flywaydb" % "flyway-core" % "4.2.0",
  "org.hsqldb" % "hsqldb" % "2.4.0"
)

mainClass := Some("io.vertx.core.Launcher")
packageOptions += Package.ManifestAttributes(
  ("Main-Verticle", "scala:nickel.NickelVerticle")
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", xs @ _*) => MergeStrategy.last
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
  case PathList("codegen.json") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
