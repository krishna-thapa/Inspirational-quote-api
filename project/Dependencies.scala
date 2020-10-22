import sbt._

object Dependencies {

  object Versions {
    val postgresql = "42.2.12"
    val playSlick  = "5.0.0"
    val scalaTest  = "3.2.0"
    val swagger    = "3.32.5"
    val playtest   = "5.0.0"
    val playRedis  = "2.6.1"
    val playjwt    = "4.2.0"
    val bcrypt     = "4.1"
    val elastic4s  = "7.9.1"
  }

  object Libraries {
    def playSlickLib(artifact: String): ModuleID =
      "com.typesafe.play" %% artifact % Versions.playSlick
    def postgresLib(artifact: String): ModuleID  = "org.postgresql" % artifact  % Versions.postgresql
    def scalaTestLib(artifact: String): ModuleID = "org.scalatest"  %% artifact % Versions.scalaTest
    def swaggerLib(artifact: String): ModuleID   = "org.webjars"    % artifact  % Versions.swagger
    def playScalaTestlib(artifact: String): ModuleID =
      "org.scalatestplus.play" %% artifact % Versions.playtest
    def playRedisLib(artifact: String): ModuleID =
      "com.github.karelcemus" %% artifact % Versions.playRedis
    def playJWTLib(artifact: String): ModuleID = "com.pauldijou" %% artifact % Versions.playjwt
    def scalaBcryptLib(artifact: String): ModuleID =
      "com.github.t3hnar" %% artifact % Versions.bcrypt
    def elastic4sLib(artifact: String): ModuleID =
      "com.sksamuel.elastic4s" %% artifact % Versions.elastic4s

    val playSlick           = playSlickLib("play-slick")
    val playSlickEvolutions = playSlickLib("play-slick-evolutions")
    val postgres            = postgresLib("postgresql")
    val scalaTest           = scalaTestLib("scalatest") % "test"
    val swaggerUi           = swaggerLib("swagger-ui")
    val playRedis           = playRedisLib("play-redis")
    val playScalaTest       = playScalaTestlib("scalatestplus-play") % "test"
    val playJwt             = playJWTLib("jwt-play")
    val scalaBcrypt         = scalaBcryptLib("scala-bcrypt")
    val elastic4s           = elastic4sLib("elastic4s-client-esjava")
    val elastic4sJson       = elastic4sLib("elastic4s-json-play")
  }
}
