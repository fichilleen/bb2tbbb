name := "irc_bot"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.13",
  "com.typesafe.akka" %% "akka-stream" % "2.5.13",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.0-akka-2.5.x",
  "com.typesafe" % "config" % "1.3.1",
  "org.scalactic" %% "scalactic" % "3.0.4",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "org.mockito" % "mockito-core" % "2.16.0" % "test",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "org.xerial" % "sqlite-jdbc" % "3.21.0.1"
)

