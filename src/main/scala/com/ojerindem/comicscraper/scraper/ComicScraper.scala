package com.ojerindem.comicscraper.scraper

import java.io.{FileWriter}

import com.ojerindem.comicscraper.helper.ParseObjects.{ComicIssueDetail, ReleaseDateReleaseEndTuple, WriterArtistCriticReviewCntUserReviewCntTuple}
import com.ojerindem.comicscraper.helper.Utils.{getUrl,getFilePath}

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.jdk.CollectionConverters._
import scala.collection.parallel.CollectionConverters._

object ComicScraper {

  private def caseClassToString(comic: ComicIssueDetail) = {
    val comicStr = comic.toString
    val comicLength = comicStr.length
    comicStr
      .substring(0,comicLength - 1)
      .replaceAll("\"","")
      .replaceAll("\\w(,)\\s","\\w ")
      .replaceAll(",","\",\"")
      .replaceAll("ComicIssueDetail\\(", "\"")
      .replaceAll("\\)\\)", "\\)\"") + "\""
  }

  private def getComicIssueCount(doc: Document) = {
    val summarySection = doc.select("#issue-summary a").asScala
    val numOfComicIssues = try {summarySection(1).text } catch { case x: IndexOutOfBoundsException => "0" }
    numOfComicIssues.toInt
  }

  private def formatMonth(month: String) = {
    val formattedMonth = month match {
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
      case "Present" => "2099-00"
      case _ => null
    }
    formattedMonth

  }

  private def formatDate(date: String) = {
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

  private def getSpecificIssueDetails(doc: Document, elem: Element,url: String) = {
    val regex = "#(\\d+)".r
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
    val formatCorrector = (issueWriter,issueArtist,criticIssueReviews,userIssueReviews) match {
      case (a,b,"","") => WriterArtistCriticReviewCntUserReviewCntTuple("","",a,b)
      case (a,b,c,"") => WriterArtistCriticReviewCntUserReviewCntTuple(a,"",b,c)
      case (a,b,c,d) => WriterArtistCriticReviewCntUserReviewCntTuple(a,b,c,d)
    }
    ComicIssueDetail(url,publisher,name,formatCorrector.writer,formatCorrector.artist,releaseStart,releaseEnd,cleanIssueNum,criticIssueReview,userIssueReview,formatCorrector.criticReviewCount,formatCorrector.criticReviewCount)

  }

  private def getPublisherComicCount(doc: Document) = {
    val regex = "\\d+".r
    val seriesCount = doc.select(".tabs .selected").text
    //println("s count: "+ seriesCount)
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
    for (i <- 0 to issueCount - 1) yield getSpecificIssueDetails(doc,comicIssuesSection.get(i),url)
  }

  def createPublisherCsv(doc: Document, fileName: String) = {
    val f = getFilePath(fileName)
    val writer = new FileWriter(f)
    try { writer.append("url,publisher,name,writer,artist,release_start,release_end,issue_number,critic_review_score,user_review_score,critic_review_count,user_review_count").append("\n") }
    for (url <- getPublisherComicUrls(doc).par)
      yield {
        val comicUrl = Jsoup.connect(url).validateTLSCertificates(false).get()
        val numOfIssues = getComicIssueCount(comicUrl)
        val issueDetails = getComicIssueDetails(comicUrl,url,numOfIssues)
        for (issueDetail <- issueDetails)
          yield {
            val comicIssueDetailCaseClass = caseClassToString(issueDetail)
            //println(comicIssueDetailCaseClass)
            try { writer.append(comicIssueDetailCaseClass).append("\n") }
          }
      }
    writer.close
  }

}
