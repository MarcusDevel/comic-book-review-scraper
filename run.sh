#!/usr/bin/env bash
if [ $# -eq 0 ]
  then
    echo "Building with default params ..."
  else
    echo "Building with param $1"
fi

echo "Building Jar ..."
sbt assembly
cp target/scala-*/*.jar .
chmod +x *.jar

echo "Creating docker image ..."
docker build -t comic-scraper .
echo "Running comic scraper ..."
docker run --rm -v $PWD:/opt/app comic-scraper $1
echo "Creating zip ..."
echo "List of .csvs to zip ..."
ls *.csv
zip comic-reviews.zip *.csv
echo "Cleaning up...."
mkdir zip_and_csv_filess
mv *.csv *.zip zip_and_csv_filess/
ls zip_and_csv_filess/
rm *.jar
rm -r target/