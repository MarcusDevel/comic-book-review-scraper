package com.ojerindem.comicscraper.spark

import java.io.File
import java.nio.file.Paths

import org.apache.spark.sql.SparkSession

object Cleanser extends App {
  val spark =
    SparkSession.builder
      .appName("comic-book-review-scraper")
      .master("local[*]")
      .getOrCreate()

  val projectDirPath = Paths.get(".").toAbsolutePath.toString.replace(".","")

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  val csvList = getListOfFiles(s"$projectDirPath/zip_and_csv_files")

  for (file <- csvList ) yield  {
    println(file)
    val df = spark.read.option("header","true").csv(file.toString)
    val newDF = df.take(50)

    for (row <- newDF) yield {
      println(row)
    }
    println()
  }

}
