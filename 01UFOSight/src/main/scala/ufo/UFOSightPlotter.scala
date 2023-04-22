package ufo

import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart._
import ufo.model.UFOSight
import ufo.model.USStates.usStates

import java.text.SimpleDateFormat
import scala.jdk.CollectionConverters._

object UFOSightPlotter {

  def main(args: Array[String]): Unit = {
    val allUFOSight: List[UFOSight] = UFOSight.readFromTSVFile("data/ufo_awesome.tsv")
    val usOnlyUFOSightWithDate = allUFOSight
      .filterNot(sight => sight.dateOccurred.isEmpty || sight.dateReported.isEmpty)
      .filterNot(_.location.isEmpty)
      .filter(x => usStates.contains(x.location.get.state))

    val usModernUFOSight = usOnlyUFOSightWithDate.filter(_.dateOccurred.get.getYear >= 1990)

    val allSights = plotHistogramByTenYears(usOnlyUFOSightWithDate, "全部目擊資料")
    val modernSights = plotHistogramByQuarters(usModernUFOSight, "1990 後目擊資料")
    val sightGridByStates = plotLineChartMatrix(usModernUFOSight)

    BitmapEncoder.saveBitmap(allSights, "images/allSightsInUS.png", BitmapFormat.PNG)
    BitmapEncoder.saveBitmap(modernSights, "images/modernSightsInUS.png", BitmapFormat.PNG)
    BitmapEncoder.saveBitmap(sightGridByStates.asJava, 10, 5, "images/sightGirdByStates.png", BitmapFormat.PNG)
  }

  private def plotLineChartMatrix(ufoSight: List[UFOSight]) = {
    val minYear = ufoSight.map(_.dateOccurred.get.getYear).min
    val maxYear = ufoSight.map(_.dateOccurred.get.getYear).max

    usStates.toList
      .sorted
      .map(state => plotLineChart(ufoSight.filter(_.location.get.state == state), state, minYear, maxYear))
  }

  private def plotLineChart(usOnlyUFOSightWithDate: List[UFOSight], title: String, minYear: Int, maxYear: Int): XYChart = {

    val dateRange = for {
      year <- minYear to maxYear
      month <- 1 to 12
    } yield {
      f"$year-$month%02d"
    }

    val dateFormatter = new SimpleDateFormat("yyyy-MM")
    val monthToCount = usOnlyUFOSightWithDate
      .groupBy(x => x.yearAndMonth.get)
      .view.mapValues(x => x.size)
      .toMap

    val xData = dateRange.map(dateFormatter.parse).toList
    val yData = dateRange.map(x => monthToCount.getOrElse(x, 0)).toList

    val chart = new XYChartBuilder()
      .title(title)
      .xAxisTitle("目擊時間")
      .yAxisTitle("次數")
      .height(150)
      .width(350)
      .build()

    chart.getStyler.setYAxisMin(0)
    chart.getStyler.setYAxisMax(100)
    chart.getStyler.setMarkerSize(2)
    chart.getStyler.setLegendVisible(false)
    chart.addSeries("目擊次數", xData.asJava, yData.map(_.asInstanceOf[Integer]).asJava)
    chart


  }

  private def plotHistogramByTenYears(usOnlyUFOSightWithDate: List[UFOSight], title: String): CategoryChart = {
    val minYear = usOnlyUFOSightWithDate.map(_.dateOccurred.get.getYear).min
    val maxYear = usOnlyUFOSightWithDate.map(_.dateOccurred.get.getYear).max

    val tenYearBuckets = minYear to maxYear by 10
    val yearsToCount = usOnlyUFOSightWithDate
      .groupBy(x => (x.dateOccurred.get.getYear / 10) * 10)
      .view
      .mapValues(_.size)
      .toMap

    val xData = tenYearBuckets.toList
    val yData = tenYearBuckets.map(x => yearsToCount.getOrElse(x, 0)).toList

    createHistogramChart(title, xData, yData, 20)
  }

  private def plotHistogramByQuarters(usOnlyUFOSightWithDate: List[UFOSight], title: String): CategoryChart = {
    val minYear = usOnlyUFOSightWithDate.map(_.dateOccurred.get.getYear).min
    val maxYear = usOnlyUFOSightWithDate.map(_.dateOccurred.get.getYear).max

    val quarterBuckets = for {
      year <- minYear to maxYear
      quarter <- 1 to 4
    } yield {
      year + "Q" + quarter.toString
    }

    val quarterToCount = usOnlyUFOSightWithDate
      .groupBy(_.yearAndQuarter.get).view
      .mapValues(_.size)
      .toMap

    val xData = quarterBuckets.toList
    val yData = quarterBuckets.map(x => quarterToCount.getOrElse(x, 0)).toList
    createHistogramChart(title, xData, yData, 10)
  }

  private def createHistogramChart[T](title: String, xData: List[T], yData: List[Int], maxLabel: Int): CategoryChart = {
    val chart = new CategoryChartBuilder()
      .title(title)
      .xAxisTitle("目擊時間")
      .yAxisTitle("次數")
      .width(1080)
      .height(720)
      .build()

    chart.getStyler.setXAxisMaxLabelCount(maxLabel)
    chart.getStyler.setLegendVisible(false)
    chart.addSeries("目擊次數", xData.asJava, yData.map(_.asInstanceOf[Integer]).asJava)
    chart
  }

}
