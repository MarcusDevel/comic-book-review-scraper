package com.ojerindem.comicscraper.helper

import java.io.File
import java.nio.file.Paths

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

  //Put in config maybe ??
  def getUrl: String = "https://comicbookroundup.com"

  /**
   * Returns the URL string for a provided publishers all-series page (https://comicbookroundup.com)
   * @param publisher: name of the publisher
   * @return a URL String
   */
  private def formatAllSeriesUrl(publisher: String) = {
    val publisherTag = publisher match {
      case "idw"  => s"$publisher-publishing"
      case "boom" => s"$publisher-studios"
      case _      => s"$publisher-comics"
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

  def getFilePath(fileName: String) =
    new File(s"${Paths.get(".").toAbsolutePath.toString.replace(".","")}/${fileName}.csv")

}
