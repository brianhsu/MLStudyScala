package ufo.plotter

import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.{XYChart, XYChartBuilder}
import ufo.model.UFOSight

import java.text.SimpleDateFormat
import java.util.Date
import scala.jdk.CollectionConverters._

/**
 * 目擊次數折線圖繪製
 *
 * @param ufoSights 目擊記錄
 * @param startYear 橫軸開始年份
 * @param endYear   橫軸結束年份
 */
class LineChartPlotter(ufoSights: List[UFOSight], startYear: Int, endYear: Int) {

  private val dateFormatter = new SimpleDateFormat("yyyy-MM")

  /**
   * 繪製某州的折線圖
   *
   * @param   state 州簡稱
   * @return        可以進行輸出的圖形物件
   */
  def plot(state: String): XYChart = {

    // 先建立從 startYear 01 月到 endYear 12 月每月份的資料
    val dateMonthRange = for {
      year <- startYear to endYear
      month <- 1 to 12
    } yield {
      f"$year-$month%02d"
    }

    // 統計每月的目擊次數
    val monthToCount = ufoSights
      .filter(_.location.get.state == state)
      .groupBy(x => x.yearAndMonth.get)
      .view.mapValues(x => x.size)
      .toMap

    // 建立 XChart 要用的繪圖資料。
    // 横軸為從 startYear 01 月到 endYear 12 月，
    // 縱軸為該月份的目擊次數，若原始資料中該月份無目擊記錄，則以 0 替代。
    val xData = dateMonthRange.map(dateFormatter.parse).toList
    val yData = dateMonthRange.map(x => monthToCount.getOrElse(x, 0)).toList

    createLineChart(state, xData, yData)
  }

  /**
   * 建立折線圖
   * @param title 標題
   * @param xData 橫軸資料
   * @param yData 縱軸資料
   * @return      可以進行輸出的繪圖物件
   */
  private def createLineChart(title: String, xData: List[Date],yData: List[Int]) = {
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
    chart.getStyler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line)
    chart.addSeries("目擊次數", xData.asJava, yData.map(_.asInstanceOf[Integer]).asJava)

    chart
  }

}
