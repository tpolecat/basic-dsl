name := "basic-dsl"

version := "0.1"

scalaVersion := "2.11.5"

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core"   % "7.1.0",
  "org.scalaz" %% "scalaz-effect" % "7.1.0"
)





