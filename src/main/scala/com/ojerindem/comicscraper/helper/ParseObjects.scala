package com.ojerindem.comicscraper.helper

object ParseObjects {

  case class ComicDetails(
                           index:String,
                           name:String,
                           publisher:String,
                           releaseDate:String,
                           numberOfIssues:String,
                           criticReviewCount:String,
                           userReviewCount:String,
                           criticAgrRating:String,
                           userAgrRating:String,
                           url:String)

  case class ComicIssueDetail(
                               url: String,
                               publisher: String,
                               name: String,
                               writer: String,
                               artist: String,
                               releaseStart: String,
                               releaseEnd: String,
                               issueNumber: String,
                               criticReviewScore: String,
                               userReviewScore: String,
                               criticReviewCount: String,
                               userReviewCount: String
                             )

  case class ReleaseDateReleaseEndTuple(
                                         releaseStart: String,
                                         releaseEnd: String
                                       )

  case class WriterArtistCriticReviewCntUserReviewCntTuple(
                                                            writer: String,
                                                            artist: String,
                                                            criticReviewCount: String,
                                                            userReviewCount: String
                                                          )

}
