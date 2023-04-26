ThisBuild / scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "org.knowm.xchart" % "xchart" % "3.8.3"
)

javaOptions := Seq("-Xmx4096m")
scalacOptions := Seq("-deprecation")

fork := true
