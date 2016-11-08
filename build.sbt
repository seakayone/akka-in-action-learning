name := "akka-learning"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += Resolver.bintrayRepo("rbmhtechnology", "maven")

libraryDependencies ++= {
  val akkaVersion = "2.4.12"
  val akkaHttpVersion = "2.4.11"
  val eventuateVersion = "0.7.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core"  % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-experimental"  % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "com.rbmhtechnology" %% "eventuate-core" % eventuateVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.1.3",
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest"     %% "scalatest"       % "2.2.0"       % "test"
  )
}
