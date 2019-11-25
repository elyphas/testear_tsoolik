import sbtcrossproject.{CrossType, crossProject}
import com.typesafe.sbt.less.Import.LessKeys

/**Resolving a snapshot version. It's going to be slow unless you use `updateOptions := updateOptions.value.withLatestSnapshots(false)` options* */
updateOptions := updateOptions.value.withLatestSnapshots(false)

lazy val server = (project in file("server"))
  .settings(commonSettings)
  .settings(
    scalacOptions ++= Seq("-Ypartial-unification"),
    scalacOptions in ThisBuild ++= Seq("-Ypartial-unification"),
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    LessKeys.compress in Assets := true,
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies ++= Seq (
      "org.scalatest" %%% "scalatest" % "3.0.8" % Test,
      "com.typesafe.akka" %% "akka-http" % "10.1.9",
      "com.typesafe.akka" %% "akka-stream" % "2.5.25",
      "com.vmunier" %% "scalajs-scripts" % "1.1.2",
    ),
    WebKeys.packagePrefix in Assets := "public/",
    managedClasspath in Runtime += (packageBin in Assets).value,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0-M4"),
    javaOptions in run += "-Xms4G -Xmx8G",    //-XX:MaxPermSize=1024M,
    // When running tests, we use this configuration
    javaOptions in Test += s"-Dconfig.file=${sourceDirectory.value}/test/resources/application.test.conf",
    // We need to fork a JVM process when testing so the Java options above are applied
    fork in Test := true,
  )
  .enablePlugins(SbtWeb, SbtTwirl, WebScalaJSBundlerPlugin, JavaAppPackaging)
  .dependsOn(sharedJvm)

lazy val client = (project in file("client"))
  .settings(commonSettings)
  .settings(
    npmDependencies in Compile ++= Seq(
      "snabbdom" -> "0.7.3",
    ),
    npmDevDependencies in Compile ++= Seq(
      "webpack-merge" -> "4.1.2",
      "imports-loader" -> "0.8.0",
      "expose-loader" -> "0.7.5"
    ),
    version in webpack := "4.41.2",
    version in startWebpackDevServer := "3.9.0",
    scalaJSUseMainModuleInitializer := true,
    useYarn := true,
    scalacOptions ++= Seq("-P:scalajs:sjsDefinedByDefault", "-Ypartial-unification"),
    webpackBundlingMode := BundlingMode.Application,
    scalaJSModuleKind := ModuleKind.CommonJSModule, // configure Scala.js to emit a JavaScript module instead of a top-level script
    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "dev.webpack.config.js"),
    webpackConfigFile in Test := Some(baseDirectory.value / "test.webpack.config.js"),
    requireJsDomEnv in Test := true,
    skip in packageJSDependencies := false,
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.0.8" % Test,
      "com.github.cornerman.outwatch" %%% "outwatch" % "0a470538",
      "com.github.cornerman.outwatch" %%% "outwatch-monix" % "0a470538",
      "com.chuusai" %%% "shapeless" % "2.3.3",
      "org.scala-js" %%% "scalajs-java-time" % "0.2.4",
    ),
    emitSourceMaps := false,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0-M4"),
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb, ScalaJSBundlerPlugin)
  .dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType ( CrossType.Pure )
  .in(file ( "shared" ) )
  .settings ( commonSettings )
  .settings (
    resolvers += "jitpack" at "https://jitpack.io",
    scalacOptions ++= Seq("-Ypartial-unification"),
    scalacOptions in ThisBuild ++= Seq("-Ypartial-unification"),
    libraryDependencies ++= Seq (
      "org.scalatest" %%% "scalatest" % "3.0.8" % Test,
      "io.suzaku" %%% "boopickle" % "1.3.1",  //"1.2.6",
      //"org.typelevel" %%% "cats-effect" % "2.0.0",
      //"org.typelevel" %% "cats-core" % "2.0.0",
      //"com.github.marklister" % "base64" % "0.2.5",
      "com.typesafe.akka" %% "akka-actor" % "2.5.25",

      "com.github.cornerman.covenant" %%% "covenant-http" % "master-SNAPSHOT",
      "com.github.cornerman.covenant" %%% "covenant-ws" % "master-SNAPSHOT"
    )
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)
  .jsConfigure(_ enablePlugins ScalaJSBundlerPlugin)
  .jsConfigure(_ enablePlugins SbtWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  organization := "testear tsoolik"
)

// loads the server project at sbt startup
onLoad in Global := ( onLoad in Global ).value andThen { s: State => "project server" :: s }