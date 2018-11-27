lazy val V = _root_.scalafix.sbt.BuildInfo

lazy val commonSettings = Seq(
  scalaVersion := V.scala212,
  version := "0.0.1-SNAPSHOT",
  addCompilerPlugin(scalafixSemanticdb),
  scalacOptions ++= List(
    "-Yrangepos",
    "-unchecked",
    "-deprecation",
    "-feature",
    "-opt:l:inline",
    "-opt-inline-from"
  ),
)

// import ReleaseTransformations._
lazy val publishingSettings = Seq(
  // publishing
  organization := "com.github.tanishiking",
  homepage := Some(url("https://github.com/tanishiking/scalafix-check-scaladoc")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "tanishiking",
      "Rikito Taniguchi",
      "rikiriki1238@gmail.com",
      url("https://github.com/tanishiking")
    )
  ),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := {
    if (isSnapshot.value)
      Some(Opts.resolver.sonatypeSnapshots)
    else
      Some(Opts.resolver.sonatypeStaging)
  },
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/tanishiking/scalafix-check-scaladoc"),
      "scm:git:git@github.com:tanishiking/scalafix-check-scaladoc.git"
    )
  )
)

lazy val rules = project.settings(
  moduleName := "scalafix-check-scaladoc",
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
  scalafixDependencies in ThisBuild += "com.github.tanishiking" %% "scalafix-check-scaladoc" % "0.0.1-SNAPSHOT",
).settings(commonSettings).settings(publishingSettings)

lazy val input = project.settings(
  skip in publish := true,
  libraryDependencies += "org.scala-lang" % "scala-reflect" % V.scala212,
).settings(commonSettings)

lazy val output = project.settings(
  skip in publish := true,
).settings(commonSettings)

lazy val tests = project
  .settings(
    skip in publish := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    compile.in(Compile) := 
      compile.in(Compile).dependsOn(compile.in(input, Compile)).value,
    scalafixTestkitOutputSourceDirectories :=
      sourceDirectories.in(output, Compile).value,
    scalafixTestkitInputSourceDirectories :=
      sourceDirectories.in(input, Compile).value,
    scalafixTestkitInputClasspath :=
      fullClasspath.in(input, Compile).value,
  )
  .settings(commonSettings)
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
