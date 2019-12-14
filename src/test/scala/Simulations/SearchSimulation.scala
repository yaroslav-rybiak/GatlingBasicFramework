package Simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


class SearchSimulation extends Simulation {

  object Search {

    val feeder = csv("search.csv").random

    val search = exec(http("Home")
      .get("/"))
      .pause(1)
      .feed(feeder) // 3
      .exec(http("Search")
        .get("/computers?f=${searchCriterion}")
        .check(css("a:contains('${searchComputerName}')", "href").saveAs("computerURL")))
      .pause(1)
      .exec(http("Select")
        .get("${computerURL}"))
      .pause(1)
  }
  val users = scenario("Users").exec(Search.search)

  val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  setUp(
    users.inject(rampUsers(10) during (10 seconds)),
  ).protocols(httpProtocol)

}
