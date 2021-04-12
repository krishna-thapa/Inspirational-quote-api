import sbt._

object Dependencies {

  object Versions {
    val postgresql      = "42.2.12"
    val playSlick       = "5.0.0"
    val swagger         = "3.32.5"
    val playRedis       = "2.6.1"
    val playjwt         = "4.2.0"
    val bcrypt          = "4.1"
    val elastic4s       = "7.9.1"
    val quartzScheduler = "1.8.5-akka-2.6.x"

    // tests
    val scalaTest      = "3.2.0"
    val playtest       = "5.0.0"
    val testContainers = "0.38.8"
    val mockitoVer     = "1.13.0"

    // logs
    val logbackEncoder = "6.6"
  }

  object Libraries {
    def playSlickLib(artifact: String): ModuleID =
      "com.typesafe.play" %% artifact % Versions.playSlick
    def postgresLib(artifact: String): ModuleID = "org.postgresql" % artifact % Versions.postgresql
    def swaggerLib(artifact: String): ModuleID  = "org.webjars"    % artifact % Versions.swagger
    def playRedisLib(artifact: String): ModuleID =
      "com.github.karelcemus" %% artifact % Versions.playRedis
    def playJWTLib(artifact: String): ModuleID = "com.pauldijou" %% artifact % Versions.playjwt
    def scalaBcryptLib(artifact: String): ModuleID =
      "com.github.t3hnar" %% artifact % Versions.bcrypt
    def elastic4sLib(artifact: String): ModuleID =
      "com.sksamuel.elastic4s" %% artifact % Versions.elastic4s
    def elastic4sSteamLib(artifact: String): ModuleID =
      "com.sksamuel.elastic4s" %% artifact % Versions.elastic4s
    def quartzSchedulerLib(artifact: String): ModuleID =
      "com.enragedginger" %% artifact % Versions.quartzScheduler

    // tests
    def scalaTestLib(artifact: String): ModuleID = "org.scalatest" %% artifact % Versions.scalaTest
    def playScalaTestlib(artifact: String): ModuleID =
      "org.scalatestplus.play" %% artifact % Versions.playtest
    def testContainersLib(artifact: String): ModuleID =
      "com.dimafeng" %% artifact % Versions.testContainers
    def mockitoLib(artifact: String): ModuleID =
      "org.mockito" %% artifact % Versions.mockitoVer

    // logs
    def logbackEncoderLib(artifact: String): ModuleID =
      "net.logstash.logback" % artifact % Versions.logbackEncoder

    val playSlick           = playSlickLib("play-slick")
    val playSlickEvolutions = playSlickLib("play-slick-evolutions")
    val postgres            = postgresLib("postgresql")
    val swaggerUi           = swaggerLib("swagger-ui")
    val playRedis           = playRedisLib("play-redis")
    val playJwt             = playJWTLib("jwt-play")
    val scalaBcrypt         = scalaBcryptLib("scala-bcrypt")
    val elastic4s           = elastic4sLib("elastic4s-client-esjava")
    val elastic4sJson       = elastic4sLib("elastic4s-json-play")
    val elastic4sStreams    = elastic4sSteamLib("elastic4s-http-streams")
    val quartzScheduler     = quartzSchedulerLib("akka-quartz-scheduler")

    // logs
    val logbackEncoder = logbackEncoderLib("logstash-logback-encoder")

    // tests
    val scalaTest              = scalaTestLib("scalatest")                               % "test"
    val playScalaTest          = playScalaTestlib("scalatestplus-play")                  % "test"
    val testContainers         = testContainersLib("testcontainers-scala-scalatest")     % "test"
    val postgresContainer      = testContainersLib("testcontainers-scala-postgresql")    % "test"
    val elasticSearchContainer = testContainersLib("testcontainers-scala-elasticsearch") % "test"

    // mock
    val mockito = mockitoLib("mockito-scala-scalatest") % "test"
  }
}
