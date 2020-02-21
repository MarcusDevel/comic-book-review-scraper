name := "comic-book-review-scraper"

version := "2.0"

scalaVersion := "2.12.10"

// https://mvnrepository.com/artifact/org.jsoup/jsoup
libraryDependencies +=
  "org.jsoup" % "jsoup" % "1.8.3"

/*
Uncomment when using scala 2.13.X
libraryDependencies +=
  "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0"
  */

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies +=
  "org.apache.spark" %% "spark-sql" % "2.4.5"


//https://blog.elegantmonkeys.com/dockerizing-your-scala-application-6590385fd501
// No need to run tests while building jar
test in assembly := {}
// Simple and constant jar name
assemblyJarName in assembly := s"comic-review-scraper-2.0-assembly.jar"
// Merge strategy for assembling conflicts
assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _ => MergeStrategy.first
}