name := "basic-dsl"

version := "0.1"

scalaVersion := "2.10.0"

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "org.scalaz" % "scalaz-core_2.10" % "7.0.0-M9",
  "org.scalaz" % "scalaz-effect_2.10" % "7.0.0-M9"
)





