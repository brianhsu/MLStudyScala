package ufo.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.io.Source
import scala.util.{Try, Using}

case class UFOSight(
  dateOccurred: Option[LocalDate], dateReported: Option[LocalDate], 
  location: Option[Location], shortDescription: Option[String], 
  duration: Option[String], longDescription: Option[String]
) {
  def yearAndQuarter = dateOccurred.map(date => date.getYear + "Q" + date.format(DateTimeFormatter.ofPattern("Q")))
  def yearAndMonth = dateOccurred.map(date => date.format(DateTimeFormatter.ofPattern("yyyy-MM")))
}

object UFOSight {

  def readFromTSVFile(fileName: String): List[UFOSight] = {
    Using.resource(Source.fromFile(fileName)) { source =>
      source.getLines()
        .map(_.split("\t").toList)
        .map(parseRow)
        .toList
    }
  }

  private def parseRow(row: List[String]): UFOSight = {
    val dateOccurred = Try(LocalDate.parse(row(0), DateTimeFormatter.ofPattern("yyyyMMdd"))).toOption
    val dateReported = Try(LocalDate.parse(row(1), DateTimeFormatter.ofPattern("yyyyMMdd"))).toOption
    val location = parseLocation(row(2))
    val shortDescription = Try(row(3)).map(_.trim).filter(!_.isEmpty).toOption
    val duration = Try(row(4)).map(_.trim).filter(!_.isEmpty).toOption
    val longDescription = Try(row(5)).map(_.trim).filter(!_.isEmpty).toOption

    UFOSight(dateOccurred, dateReported, location, shortDescription, duration, longDescription)
  }

  private def parseLocation(rawData: String): Option[Location] = {
    Try {
      val Array(city, state) = rawData.split(",")
      Location(city.trim, state.trim.toUpperCase)
    }.toOption
  }

}

