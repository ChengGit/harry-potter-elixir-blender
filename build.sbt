import dev.guardrail.Args
import dev.guardrail.sbt.Types.Language
import sbt.Def

lazy val root = (project in file("."))
  .settings(
      name := "HarryPorterElixirBlender",
      libraryDependencies ++= dependencies ++ testDependencies,
      version      := "0.1",
      scalaVersion := "2.13.8",
      scalacOptions ++= Seq(
          "-encoding",
          "utf-8",
          "-deprecation",
          "-explaintypes",
          "-feature",
          "-language:existentials",
          "-language:experimental.macros",
          "-language:higherKinds",
          "-language:implicitConversions",
          "-language:postfixOps",
          "-unchecked",
          "-Xcheckinit",
          "-Xfatal-warnings",
          "-Xlint:-serial",
          "-Ybackend-parallelism",
          java.lang.Runtime.getRuntime.availableProcessors().toString,
          "-Ywarn-dead-code",
          "-Ywarn-extra-implicit",
          "-Ywarn-numeric-widen",
          "-Ywarn-value-discard"
      )
  )
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .settings(compileSettings)
  .settings(dockerSettings)

lazy val compileSettings: Seq[Def.Setting[List[(Language, Args)]]] = Seq(
    Compile / guardrailTasks += ScalaClient(
        specPath = file("src/main/specs/wizardWord.yaml"),
        pkg = "io.cheng.harrypotter.elixirblender.clients",
        framework = "http4s"
    )
)

lazy val dockerSettings = Seq(
    Docker / maintainer      := "chenggit",
    Docker / packageName     := "harry-potter-elixir-blender",
    dockerBaseImage          := "azul/zulu-openjdk:16"
)

lazy val V = new {
  val http4s    = "0.23.11"
  val log4cats  = "2.2.0"
  val scalaTest = "3.2.11"
  val logback   = "1.3.0-alpha14"
}
lazy val dependencies = Seq(
    // Depend on http4s-managed cats and circe
    "org.http4s"    %% "http4s-ember-client" % V.http4s,
    "org.http4s"    %% "http4s-ember-server" % V.http4s,
    "org.http4s"    %% "http4s-circe"        % V.http4s,
    "org.http4s"    %% "http4s-dsl"          % V.http4s,
    "org.typelevel" %% "log4cats-slf4j"      % V.log4cats,
    "ch.qos.logback" % "logback-classic"     % V.logback
)

lazy val testDependencies =
  Seq("org.scalatest" %% "scalatest" % V.scalaTest % "test")
