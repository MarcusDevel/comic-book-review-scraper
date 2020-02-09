# Comic Book Review Scraper
Comic book review scraper for:
* [comicbookroundup](https://comicbookroundup.com) - A comic review aggregation website

## How to use the scraper

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

The jar which the below instructions will build and run does the following:
- Download publisher comic book issue .csv files: The scraper will download a 
.csv file containing a record for every individual issue of comics for a given publisher

```
# TO build a jar
sbt assembly
```
```
# TO build and run docker image
chmod +x run.sh
./run.sh <publisher>
```
* publisher:
    - marvel
    - dc
    - dark-horse
    - image
    - valiant
    - boom
    - idw
    - all (Will download comics of all the above publishers)

## CSV Format
The csv is formatted as follows:

|url|publisher|name|writer|artist|release_start|release_end|issue_number|critic_review_score|user_review_score|critic_review_count|user_review_count|
|---|---|---|---|---|---|---|---|---|---|---|---|
|"https://comicbookroundup.com/comic-books/reviews/marvel-comics/king-size-hulk"|"Marvel Comics"|"King Size Hulk"|"Jeph Loeb"|"Herb Trimpe"|"2008-05"|"null"|"1"|"5.6"|"7.5"|"1"|"1"|