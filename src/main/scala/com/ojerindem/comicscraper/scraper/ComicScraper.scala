package com.ojerindem.comicscraper.scraper

import java.io.FileWriter
import java.net.SocketTimeoutException

import com.ojerindem.comicscraper.helper.ParseObjects.{ComicIssueDetail, WriterArtistCriticReviewCntUserReviewCntTuple}
import com.ojerindem.comicscraper.helper.Utils._
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.jdk.CollectionConverters._
import scala.collection.parallel.CollectionConverters._

object ComicScraper {

  private def getComicIssueCount(doc: Document) = {
    val summarySection = doc.select("#issue-summary a").asScala
    val numOfComicIssues = try {summarySection(1).text } catch { case x: IndexOutOfBoundsException => "0" }
    numOfComicIssues.toInt
  }

  private def getReleaseDate(url: String) = {
    url.endsWith("/") match {
      case true => ""
      case false =>
        try {
          val comicIndividualIssueDoc = Jsoup.connect(url).validateTLSCertificates(false).get()
          val releaseDate = try {comicIndividualIssueDoc.select("#issue-summary a").text } catch{case x: IndexOutOfBoundsException => ""}
          val formattedReleaseDate = releaseDate.replaceAll(",","")

          "(\\w+)\\s(\\d+)\\s(\\d+)".r.findFirstIn(formattedReleaseDate) match {
            case Some(value) => formatReleaseDate(value)
            case None => ""
          }
        }
        catch {
          case x: SocketTimeoutException => ""
        }
    }
  }

  private def getSpecificIssueDetails(doc: Document, elem: Element,url: String) = {
    val regex = "(\\d+|\\w+)".r
    val name = try {doc.select(".container h1 span").get(0).text() } catch{case x: IndexOutOfBoundsException => ""}
    val publisher = try {doc.select("#issue-summary a").get(0).text() } catch{case x: IndexOutOfBoundsException => ""}
    val releaseStart = try {formatDate(doc.select("#issue-summary span").get(1).text()).releaseStart } catch{case x: IndexOutOfBoundsException => ""}
    val releaseEnd = try {formatDate(doc.select("#issue-summary span").get(1).text()).releaseEnd } catch{case x: IndexOutOfBoundsException => ""}
    val criticIssueReview = try {elem.select(".review*").text() } catch{case x: IndexOutOfBoundsException => ""}
    val userIssueReview = try {elem.select(".user-review*").text() } catch{case x: IndexOutOfBoundsException => ""}
    val issueNum = try {elem.select(".info a").get(0).text } catch{case x: IndexOutOfBoundsException => ""}
    val issueWriter = try {elem.select(".info a").get(1).text } catch{case x: IndexOutOfBoundsException => ""}
    val issueArtist = try {elem.select(".info a").get(2).text } catch{case x: IndexOutOfBoundsException => ""}
    val criticIssueReviews = try {elem.select(".info a").get(3).text } catch{case x: IndexOutOfBoundsException => ""}
    val userIssueReviews = try {(elem.select(".info a").get(4).text) } catch{case x: IndexOutOfBoundsException => ""}
    val regexIssueNum = regex.findFirstMatchIn(issueNum)
    val cleanIssueNum = regexIssueNum match {
      case Some(value) => value.group(1).toString
      case None => ""
    }
    val formattedUrl = s"$url/$cleanIssueNum"
    val releaseDate = getReleaseDate(formattedUrl)
    val formatCorrector = (issueWriter,issueArtist,criticIssueReviews,userIssueReviews) match {
      case (a,b,"","") => WriterArtistCriticReviewCntUserReviewCntTuple("","",a,b)
      case (a,b,c,"") => WriterArtistCriticReviewCntUserReviewCntTuple(a,"",b,c)
      case (a,b,c,d) => WriterArtistCriticReviewCntUserReviewCntTuple(a,b,c,d)
    }
    ComicIssueDetail(formattedUrl,publisher,name,formatCorrector.writer,formatCorrector.artist,releaseDate,releaseStart,releaseEnd,cleanIssueNum,criticIssueReview,userIssueReview,formatCorrector.criticReviewCount,formatCorrector.userReviewCount)

  }

  private def getPublisherComicCount(doc: Document) = {
    val regex = "\\d+".r
    val seriesCount = doc.select(".tabs .selected").text
    val cleanedSeriesCount = regex.findFirstMatchIn(seriesCount)
    val cleanSeriesCount =
      cleanedSeriesCount match  {
        case Some(value) => value.group(0).toInt
        case None => 0
      }
    cleanSeriesCount
  }

  private def getPublisherComicUrls(doc: Document) = {
    val allComics = doc.getElementById("all-series").getElementsByTag("tr")
    val publisherComicCount = getPublisherComicCount(doc)
    for(i <- 1 until publisherComicCount) yield
      getUrl + allComics.get(i).select(".series*").select("a").attr("href")
  }

  private def getComicIssueDetails(doc: Document, url: String, issueCount: Int) = {
    val comicIssuesSection = doc.select(".series-page-list li")//.asScala

    val indexedSeqComicIssuesSplit = for (i <- 0 to issueCount - 1) yield comicIssuesSection.get(i)
    for(comicIssue <- indexedSeqComicIssuesSplit.par) yield getSpecificIssueDetails(doc,comicIssue,url)

  }

  def createPublisherCsv(doc: Document, fileName: String) = {
    val f = getFilePath(fileName)
    val writer = new FileWriter(f)
    try { writer.append("url,publisher,name,writer,artist,release_date,release_start,release_end,issue_number,critic_review_score,user_review_score,critic_review_count,user_review_count").append("\n") }
    for (url <- getPublisherComicUrls(doc).par)
      yield {
        val comicUrl = Jsoup.connect(url).validateTLSCertificates(false).get()
        val numOfIssues = getComicIssueCount(comicUrl)
        val issueDetails = getComicIssueDetails(comicUrl,url,numOfIssues)
        for (issueDetail <- issueDetails)
          yield {
            val comicIssueDetailCaseClass = caseClassToString(issueDetail)
            try { writer.append(comicIssueDetailCaseClass).append("\n") }
          }
      }
    writer.close
  }

  def scrapeBoom() = {
    println(s"Scrapping Boom Studios Comics...")
    createPublisherCsv(boomDoc,"boomStudios")
    println(s"Scrape Complete for Boom! Studios!")
  }
  def scrapeMarvel() = {
    println(s"Scrapping Marvel Comics...")
    createPublisherCsv(marvelDoc,"marvelComics")
    println(s"Scrape Complete for Marvel Comics!")
  }
  def scrapeDc() = {
    println(s"Scrapping DC Comics...")
    createPublisherCsv(dcDoc,"dcComics")
    println(s"Scrape Complete for DC Comics!")
  }
  def scrapeValiant() = {
    println(s"Scrapping Valiant Comics...")
    createPublisherCsv(valiantDoc,"valiantComics")
    println(s"Scrape Complete for Valiant Comics!")
  }
  def scrapeImage() = {
    println(s"Scrapping Image Comics...")
    createPublisherCsv(imageDoc,"imageComics")
    println(s"Scrape Complete for Image Comics!")
  }
  def scrapeDarkHorse() = {
    println(s"Scrapping Dark Horse Comics...")
    createPublisherCsv(darkHorseDoc,"darkHorseComics")
    println(s"Scrape Complete for Dark Horse Comics!")
  }
  def scrapeIdw() = {
    println(s"Scrapping IDW Publishing Comics...")
    createPublisherCsv(idwDoc,"idwPublishing")
    println(s"Scrape Complete for IDW Publishing!")
  }
  def scrapeDynamite() = {
    println(s"Scrapping Dynamite Entertainment Comics...")
    createPublisherCsv(dynamiteDoc,"dynamiteEntertainment")
    println(s"Scrape Complete for Dynamite Entertainment!")
  }
  def scrapeAfterShock() = {
    println(s"Scrapping Aftershock Comics...")
    createPublisherCsv(aftershockDoc,"aftershockComics")
    println(s"Scrape Complete for Aftershock Comics!")
  }
  def scrapeTitan() = {
    println(s"Scrapping Titan Comics...")
    createPublisherCsv(titanDoc,"titanBooks")
    println(s"Scrape Complete for Titan Comics!")
  }
  def scrapeActionLab() = {
    println(s"Scrapping Action Lab Comics...")
    createPublisherCsv(actionLab,"actionLabComics")
    println(s"Scrape Complete for Action Lab Comics!")
  }
  def scrapeZenescope() = {
    println(s"Scrapping Zenescope Comics...")
    createPublisherCsv(zenescopeDoc,"zenescopeEntertainment")
    println(s"Scrape Complete for Zenescope Entertainment!")
  }
  def scrapeOniPress() = {
    println(s"Scrapping Oni Press Comics...")
    createPublisherCsv(onipressDoc,"oniPress")
    println(s"Scrape Complete for Oni Press Comics!")
  }
  def scrapeVertigo() = {
    println(s"Scrapping Vertigo Comics...")
    createPublisherCsv(vertigoDoc,"vertigo")
    println(s"Scrape Complete for Vertigo Comics!")
  }


  def scrapeAllComicPublisherData() = {
    scrapeMarvel
    scrapeDc
    scrapeImage
    scrapeDarkHorse
    scrapeIdw
    scrapeBoom
    scrapeValiant
    scrapeDynamite
    scrapeAfterShock
    scrapeTitan
    scrapeActionLab
    scrapeZenescope
    scrapeOniPress
    scrapeVertigo
    println(s"Scrape Complete for All Comic Publishers!")
  }

}
