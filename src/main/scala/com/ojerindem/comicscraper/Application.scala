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
      println(s"Scrapping Boom Studios Comics...")
      createPublisherCsv(boomDoc,"boomStudios")
      println(s"Scrapping Image Comics...")
      createPublisherCsv(imageDoc,"imageComics")
      println(s"Scrapping Marvel Comics...")
      createPublisherCsv(marvelDoc,"marvelComics")
      println(s"Scrapping DC Comics...")
      createPublisherCsv(dcDoc,"dcComics")
      println(s"Scrapping Valiant Comics...")
      createPublisherCsv(valiantDoc,"valiantComics")
      println(s"Scrapping IDW Publishing Comics...")
      createPublisherCsv(idwDoc,"idwPublishing")
      println(s"Scrapping Dark Horse Comics...")
      createPublisherCsv(darkHorseDoc,"darkHorseComics")
      println(s"Scrape Complete for $comic!")
    case "boom" =>
      println(s"Scrapping Boom Studios Comics...")
      createPublisherCsv(boomDoc,"boomStudios")
      println(s"Scrape Complete for $comic!")
    case "marvel" =>
      println(s"Scrapping Marvel Comics...")
      createPublisherCsv(marvelDoc,"marvelComics")
      println(s"Scrape Complete for $comic!")
    case "dc" =>
      println(s"Scrapping DC Comics...")
      createPublisherCsv(dcDoc,"dcComics")
      println(s"Scrape Complete for $comic!")
    case "image" =>
      println(s"Scrapping Image Comics...")
      createPublisherCsv(imageDoc,"imageComics")
      println(s"Scrape Complete for $comic!")
    case "valiant" =>
      println(s"Scrapping Valiant Comics...")
      createPublisherCsv(valiantDoc,"valiantComics")
      println(s"Scrape Complete for $comic!")
    case "idw" =>
      println(s"Scrapping IDW Publishing Comics...")
      createPublisherCsv(idwDoc,"idwComics")
      println(s"Scrape Complete for $comic!")
    case "dark-horse" =>
      println(s"Scrapping Dark Horse Comics...")
      createPublisherCsv(idwDoc,"darkHorseComics")
      println(s"Scrape Complete for $comic!")
    case _ =>
      throw new Exception("Argument provided is not a recognized publisher. Please provide which comics to scrape (marvel,dc.image,idw,boom,valiant,dark-horse,all")

  }

}
