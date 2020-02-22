package com.ojerindem.comicscraper.helper

import java.sql.Date

object ParseObjects {

  case class ComicIssueDetail
  (
    url: String,
    publisher: String,
    name: String,
    writer: String,
    artist: String,
    releaseDate: String,
    releaseStart: String,
    releaseEnd: String,
    issueNumber: String,
    criticReviewScore: String,
    userReviewScore: String,
    criticReviewCount: String,
    userReviewCount: String
  )

  case class ComicIssueDetailCleaned
  (
    url: String,
    publisher: String,
    name: String,
    writer: String,
    artist: String,
    releaseDate: Date,
    releaseStart: String,
    releaseEnd: String,
    issueNumber: String,
    criticReviewScore: BigDecimal,
    userReviewScore: BigDecimal,
    criticReviewCount: Int,
    userReviewCount: Int
  )

  case class ReleaseDateReleaseEndTuple
  (
    releaseStart: String,
    releaseEnd: String
  )

  case class WriterArtistCriticReviewCntUserReviewCntTuple
  (
    writer: String,
    artist: String,
    criticReviewCount: String,
    userReviewCount: String
  )

}
