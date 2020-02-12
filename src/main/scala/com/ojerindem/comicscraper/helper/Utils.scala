package com.ojerindem.comicscraper.helper

import java.io.File
import java.nio.file.Paths

import com.ojerindem.comicscraper.helper.ParseObjects.{ComicIssueDetail, ReleaseDateReleaseEndTuple}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object Utils {

  val marvelDoc = getConnection("marvel")
  val dcDoc = getConnection("dc")
  val imageDoc = getConnection("image")
  val darkHorseDoc = getConnection("dark-horse")
  val idwDoc = getConnection("idw")
  val boomDoc = getConnection("boom")
  val valiantDoc = getConnection("valiant")
  val vertigoDoc = getConnection("vertigo")
  val dynamiteDoc = getConnection("dynamite")
  val aftershockDoc = getConnection("aftershock")
  val titanDoc = getConnection("titan")
  val actionLab = getConnection("action-lab")
  val zenescopeDoc = getConnection("zenescope")
  val onipressDoc = getConnection("oni-press")

  //Put in config maybe ??
  def getUrl: String = "https://comicbookroundup.com"

  /**
   * Returns the URL string for a provided publishers all-series page (https://comicbookroundup.com)
   * @param publisher: name of the publisher
   * @return a URL String
   */
  private def formatAllSeriesUrl(publisher: String) = {
    val publisherTag = publisher match {
      case "idw"                  => s"$publisher-publishing"
      case "boom"                 => s"$publisher-studios"
      case "dynamite"|"zenescope" => s"$publisher-entertainment"
      case "titan"                => s"$publisher-books"
      case "oni-press"|"vertigo"  => s"$publisher"
      case _                      => s"$publisher-comics"
    }
    val formattedUrl = s"https://comicbookroundup.com/comic-books/reviews/$publisherTag/all-series"
    formattedUrl
  }

  /**
   * Gets the corresponding JSoup document for given publisher name
   * @param publisher: name of the publisher
   * @return A JSoup Document
   */
  private def getConnection(publisher: String): Document = {
    val seriesUrl = formatAllSeriesUrl(publisher)
    Jsoup.connect(seriesUrl)
      .validateTLSCertificates(false)
      .maxBodySize(2000000000)
      .get()
  }

  /**
   * Returns a string row conversion of a given ComicIssueDetail case class
   * @param comic: Case class representing an individual row (a complete comic issue review details)
   * @return A comma separated string row representation of a comic issue review details
   */
  def caseClassToString(comic: ComicIssueDetail) = {
    val comicStr = comic.toString
    val comicLength = comicStr.length
    s"${
      comicStr
      .substring(0,comicLength - 1)
      .replaceAll("\"","")
      .replaceAll("\\w(,)\\s","\\w ")
      .replaceAll(",","\",\"")
      .replaceAll("ComicIssueDetail\\(", "\"")
      .replaceAll("\\)\\)", "\\)\"") + "\""
    }\n"
  }

  private def formatMonth(month: String) = {
    val formattedMonth = month.take(3) match {
      case "Jan" => "-01"
      case "Feb" => "-02"
      case "Mar" => "-03"
      case "Apr" => "-04"
      case "May" => "-05"
      case "Jun" => "-06"
      case "Jul" => "-07"
      case "Aug" => "-08"
      case "Sep" => "-09"
      case "Oct" => "-10"
      case "Nov" => "-11"
      case "Dec" => "-12"
      case "Pre" => "2099-00"
      case _ => ""
    }
    formattedMonth

  }

  def formatDate(date: String) = {
    val regex = "Release:\\s*([a-zA-Z]*)\\s*(\\d*)\\s*-*\\s*([a-zA-Z]*)\\s*(\\d*)".r
    val dateTuple = Option(date) match {
      case Some(value) => {
        val regex(firstMonth,firstYear,secondMonth,secondYear) = date

        val fMonth = formatMonth(firstMonth)
        val sMonth = formatMonth(secondMonth)

        ReleaseDateReleaseEndTuple((firstYear + fMonth),(secondYear + sMonth))
      }
      case None => null
    }
    dateTuple
  }

  def formatReleaseDate(date: String) = {
    val regex = "(\\w+)\\s(\\d+)\\s(\\d{4}+)".r
    val formattedReleaseDate = Option(date) match {
      case Some(value) => {
        val regex(month,day,year) = date

        val formattedMonth = formatMonth(month)
        /*
        Uncomment for extra check before concatenating the year month day into a release date
        Not necessary as no records show this behaviour
         */
        /*val formattedMonth = processedMonth match {
          case "" => "-00"
          case _ => processedMonth
        }*/

        s"$year$formattedMonth-$day"
      }
      case None => ""
    }
    formattedReleaseDate
  }

  val formattedDate =
    java.time.LocalDate.now
      .toString
      .replaceAll("-","")

  def getFilePath(fileName: String) =
    new File(s"${Paths.get(".").toAbsolutePath.toString.replace(".","")}/${fileName}_$formattedDate.csv")

}
