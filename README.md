# Description
This is a Web Crawler Application which starts crawling the web from the given URL and crawls all the linked URLs from the same host in a BFS fashion.

## Requirements

The project is built using Java 1.8. It uses Gradle as the build system.

1. Java - 1.8.x
2. Gradle - 7.x.x

## Building the application using gradle

You can build and package the application in the form of a jar file using gradle command.

```
./gradlew build
```

The above command will produce a standalone jar file named `WebCrawler-1.0-SNAPSHOT.jar` in the ` build/libs/` directory.

## Running Tests

The `./gradlew test ` command runs all the tests.

```
./gradlew test
```

## Running the application

You can run the jar file created by the `./gradlew build` command like so -

```
java -jar  build/libs/WebCrawler-1.0-SNAPSHOT.jar {website_to_crawl} {number_of_crawlers}

example: java -jar build/libs/WebCrawler-1.0-SNAPSHOT.jar https://somewebsite.com 2

```

The output file named output.txt will be produced after crawling is complete