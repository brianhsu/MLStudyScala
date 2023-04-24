ThisBuild / scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "org.knowm.xchart" % "xchart" % "3.8.3",
  "io.github.pityka" %% "nspl-saddle" % "0.10.0",
  "io.github.pityka" %% "nspl-awt" % "0.10.0"
)

javaOptions := Seq("-Xmx4096m")
scalacOptions := Seq("-deprecation")

fork := true
