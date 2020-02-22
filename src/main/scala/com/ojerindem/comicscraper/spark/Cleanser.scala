package com.ojerindem.comicscraper.spark

import java.io.File
import java.nio.file.{Files, Paths, StandardCopyOption}

import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession

import scala.reflect.io.Directory

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

  def moveRenameFile(source: String, destination: String): Unit = {
    val tmpFiles = getListOfFiles(source)
    val tmpCsvFiles = tmpFiles.filter(p => p.toString.endsWith(".csv"))

    tmpCsvFiles.foreach(f => Files.move(
      Paths.get(f.toString),
      Paths.get(destination),
      StandardCopyOption.REPLACE_EXISTING
    ))

  }

  def deleteTmpDir(path: String) = {
    val directory = new Directory(new File(path))
    directory.deleteRecursively()
  }

  val csvList = getListOfFiles(s"$projectDirPath/zip_and_csv_files")

  for (file <- csvList) yield  {
    val tmpFilePath = file.toString.replace(".csv","_cleaned_tmp.csv")
    val cleanedFilePath = file.toString.replace(".csv","_cleaned.csv")
    println(s"Cleaning file: '$file'")
    val df = spark.read.option("header","true").csv(file.toString)
    val cleanedDF =
      df
        .withColumn("critic_review_score", when(col("critic_review_score") === "N/A", "").otherwise(col("critic_review_score")))
        .withColumn("user_review_score", when(col("user_review_score") === "N/A", "").otherwise(col("user_review_score"))
    )

    println(s"Writing cleaned file: '$file'")
    cleanedDF.write.option("header","true").csv(tmpFilePath)
    moveRenameFile(tmpFilePath,cleanedFilePath)
    deleteTmpDir(tmpFilePath)
    println(s"File -> '$file' cleaned!")
  }

}
