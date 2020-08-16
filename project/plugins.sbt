// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.2")

// play swagger plugin - https://github.com/iheartradio/play-swagger
addSbtPlugin("com.iheart" % "sbt-play-swagger" % "0.10.0-PLAY2.8")

// scalafmt plugin for scala formatter - https://github.com/scalameta/sbt-scalafmt
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")
