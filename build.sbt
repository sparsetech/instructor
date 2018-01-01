val Leaf      = "0.1.0"
val Pine      = "0.1.1"
val Toml      = "0.1.1"
val Scopt     = "3.7.0"
val ScalaTest = "3.0.4"

name         := "instructor"
version      := "0.1-SNAPSHOT"
organization := "tech.sparse"

scalaVersion      := "2.12.4-bin-typelevel-4"
scalaOrganization := "org.typelevel"
scalacOptions     += "-Yliteral-types"

libraryDependencies ++= Seq(
  "tech.sparse"      %% "pine"       % Pine,
  "tech.sparse"      %% "toml-scala" % Toml,
  "tech.sparse"      %% "leaf-core"  % Leaf,
  "com.github.scopt" %% "scopt"      % Scopt,
  "org.scalatest"    %% "scalatest"  % ScalaTest % "test"
)

enablePlugins(BuildInfoPlugin)
buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)
buildInfoPackage := "instructor"

assemblyOutputPath in assembly := file(".") / "instructor.jar"
