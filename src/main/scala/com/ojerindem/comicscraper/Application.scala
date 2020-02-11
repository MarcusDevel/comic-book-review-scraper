package com.ojerindem.comicscraper

import com.ojerindem.comicscraper.helper.Utils._
import com.ojerindem.comicscraper.scraper.ComicScraper._

object Application extends App {

  args.length match {
    case 0 => throw new Exception("Args list is empty. Please provide which comics to scrape (marvel,dc.image,idw,boom,valiant,dark-horse,all")
    case _ => println(s"Scrapping comic issues for: ${args(0)}")
  }

  val comic = args(0)

  /*
  Refactor!
   */
  comic match {
    case "all" =>
      scrapeAllComicPublisherData
    case "boom" =>
      scrapeBoom
    case "marvel" =>
      scrapeMarvel
    case "dc" =>
      scrapeDc
    case "image" =>
      scrapeImage
    case "valiant" =>
      scrapeValiant
    case "idw" =>
      scrapeIdw
    case "dark-horse" =>
      scrapeDarkHorse
    case "dynamite" =>
      scrapeDynamite
    case "aftershock" =>
      scrapeAfterShock
    case "titan" =>
      scrapeTitan
    case "action-lab" =>
      scrapeActionLab()
    case "zenescope" =>
      scrapeZenescope
    case "oni-press" =>
      scrapeOniPress
    case "vertigo" =>
      scrapeVertigo
    case _ =>
      throw new Exception(
        "Argument provided is not a recognized publisher. Please provide which comics to scrape:\n" +
          "- marvel\n" +
          "- dc\n" +
          "- image\n" +
          "- idw\n" +
          "- boom\n" +
          "- valiant\n" +
          "- dark-horse\n" +
          "- dynamite\n" +
          "- aftershock\n" +
          "- titan\n" +
          "- action-lab\n" +
          "- zenescope\n" +
          "- oni-press\n" +
          "- vertigo\n" +
          "- all\n")
  }
}
