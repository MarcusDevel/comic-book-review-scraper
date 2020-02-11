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
zip comic-reviews.zip *.csv
echo "Cleaning up...."
rm *.jar
rm -r target