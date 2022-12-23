import BuildHelper._
import Dependencies._

inThisBuild(
  List(
    organization := "dev.zio",
    homepage := Some(url("https://zio.github.io/zio-direct/")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "deusaquilus",
        "Alexander Ioffe",
        "deusaquilus@gmail.com",
        url("https://github.com/deusaquilus")
      )
    ),
    pgpPassphrase := sys.env.get("PGP_PASSWORD").map(_.toArray),
    pgpPublicRing := file("/tmp/public.asc"),
    pgpSecretRing := file("/tmp/secret.asc")
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check")

addCommandAlias(
  "testJVM",
  ";zio-direct/test"
)

lazy val modules =
  Seq[sbt.ClasspathDep[sbt.ProjectReference]](
    `zio-direct`, `zio-direct-test`
  ) ++ {
    if (isScala3) Seq[sbt.ClasspathDep[sbt.ProjectReference]](docs) else Seq[sbt.ClasspathDep[sbt.ProjectReference]]()
  }

lazy val root = (project in file("."))
  .aggregate(modules.map(_.project): _*)
  .settings(
    scalaVersion := `zd.scala.version`,
    crossScalaVersions := Nil,
    publish / skip := true
  )

lazy val `zio-direct` = project
  .in(file("zio-direct"))
  .settings(stdSettings("zio-direct"))
  .settings(crossProjectSettings)
  .settings(dottySettings)
  .settings(buildInfoSettings("zio.direct"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    crossScalaVersions := Seq(Scala212, Scala213, ScalaDotty),
    Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat,
    resolvers ++= Seq(
      Resolver.mavenLocal,
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Seq(
      zio,
      `quill-util`,
      pprint,
      sourcecode,
      fansi,
      `scala-java8-compat`,
      `scala-collection-compat`,
      `zio-test`,
      `zio-test-sbt`
    )
  )

lazy val `zio-direct-test` = project
  .in(file("zio-direct-test"))
  .settings(stdSettings("zio-direct-test"))
  .settings(crossProjectSettings)
  .settings(dottySettings)
  .settings(buildInfoSettings("zio.direct"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    crossScalaVersions := Seq(Scala212, Scala213, ScalaDotty),
    Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat,
    resolvers ++= Seq(
      Resolver.mavenLocal,
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Seq(
      zio,
      `quill-util`,
      pprint,
      sourcecode,
      fansi,
      `scala-java8-compat`,
      `scala-collection-compat`,
      `zio-test`,
      `zio-test-sbt`
    ),
    publish / skip := true,
  )
  .dependsOn(`zio-direct` % "compile->compile;test->test")

lazy val docs = project
  .in(file("zio-direct-docs"))
  .settings(stdSettings("zio-direct"))
  .settings(macroDefinitionSettings)
  .settings(
    excludeDependencies ++= Seq(
      ("org.scala-lang.modules" % "scala-collection-compat_2.13"),
      ("com.lihaoyi" % "pprint_2.13"),
      ("com.lihaoyi" % "fansi_2.13"),
      ("com.lihaoyi" % "sourcecode_2.13"),
      ("com.geirsson" % "metaconfig-core_2.13"),
      ("com.geirsson" % "metaconfig-typesafe-config_2.13"),
      ("org.typelevel" % "paiges-core_2.13")
    ),
    crossScalaVersions := Seq(ScalaDotty),
    scalaVersion := ScalaDotty,
    publish / skip := true,
    moduleName := "zio-direct-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    projectName       := "ZIO Direct Style",
    badgeInfo         := Some(
      BadgeInfo(
        artifact = "zio-direct_2.12",
        projectStage = ProjectStage.Development
      )
    ),
    docsPublishBranch := "main"
  )
  .dependsOn(`zio-direct`)
  .enablePlugins(WebsitePlugin)

addCommandAlias("fmt", "all scalafmt test:scalafmt")
