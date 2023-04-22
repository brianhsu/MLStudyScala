package ufo.plotter

import org.knowm.xchart.{CategoryChart, CategoryChartBuilder}
import ufo.model.UFOSight

import scala.jdk.CollectionConverters._

/**
 * 目擊次數直方圖繪制
 *
 * 提供以每十年為單位，或以每季為單位的直方圖繪製功能。
 * 横軸為時間區段，縱軸為該時間區段內的目擊次數總計。
 *
 * @param ufoSights 目擊資料
 */
class HistogramPlotter(ufoSights: List[UFOSight]) {

  /**
   * 繪制以十年為時間區間單位的直方圖
   *
   * @param title 直方圖標題
   * @return 可以進行輸出的直方圖物件
   */
  def tenYearBuckets(title: String): CategoryChart = {
    val minYear = ufoSights.map(_.dateOccurred.get.getYear).min
    val maxYear = ufoSights.map(_.dateOccurred.get.getYear).max

    val tenYearBuckets = minYear to maxYear by 10
    val tenYearsToCount = ufoSights
      .groupBy(x => (x.dateOccurred.get.getYear / 10) * 10)
      .view
      .mapValues(_.size)
      .toMap

    val xData = tenYearBuckets.toList
    val yData = tenYearBuckets.map(x => tenYearsToCount.getOrElse(x, 0)).toList

    createHistogramChart(title, xData, yData, 20)
  }

  /**
   * 繪制以季度為時間區間單位的直方圖
   *
   * @param title 直方圖標題
   * @return 可以進行輸出的直方圖物件
   */
  def quarterBuckets(title: String): CategoryChart = {
    val minYear = ufoSights.map(_.dateOccurred.get.getYear).min
    val maxYear = ufoSights.map(_.dateOccurred.get.getYear).max

    val quarterBuckets = for {
      year <- minYear to maxYear
      quarter <- 1 to 4
    } yield {
      s"${year}Q${quarter}"
    }

    val quarterToCount = ufoSights
      .groupBy(_.yearAndQuarter.get).view
      .mapValues(_.size)
      .toMap

    val xData = quarterBuckets.toList
    val yData = quarterBuckets.map(x => quarterToCount.getOrElse(x, 0)).toList

    createHistogramChart(title, xData, yData, 10)
  }

  /**
   * 建立直方圖物件
   *
   * XChart 的繪圖方式為提供 xData 與 yData 的 List，且這兩個 List 的長度需要一致。
   *
   * 傳入 xData 與 yData 後，XChart 會在横軸上 xData(i) 的位置，將其縱軸的值設定為 yData(i)，
   * 以此進行繪圖。
   *
   * @param title    直方圖標題
   * @param xData    橫軸資料，長度需與 yData 的長度一致
   * @param yData    縱軸資料，長度需與 xData 的長度一致
   * @param maxLabel 橫軸的刻度標記最多有幾個
   * @tparam T 橫軸的資料型別
   * @return
   */
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
