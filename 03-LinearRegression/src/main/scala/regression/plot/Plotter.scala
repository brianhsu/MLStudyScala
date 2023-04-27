package regression.plot

import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.{XYChart, XYChartBuilder}
import regression.model.{Record, Regression}

import scala.jdk.CollectionConverters._

class Plotter(dataPoints: List[Record], regression: Regression, title: String) {

  private val minHeight = dataPoints.map(_.height).min
  private val maxHeight = dataPoints.map(_.height).max

  def plot(): XYChart = {
    val chart = new XYChartBuilder()
      .title(title)
      .xAxisTitle("身高")
      .yAxisTitle("體重")
      .build()

    // 繪製樣本的散佈圖
    val sampleXData = dataPoints.map(_.height.asInstanceOf[java.lang.Double])
    val sampleYData = dataPoints.map(_.weight.asInstanceOf[java.lang.Double])
    chart.addSeries("樣本值", sampleXData.asJava, sampleYData.asJava).setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter)

    // 繪製預測的直線方程式代表的直線
    val estimateXData = (minHeight.floor.toInt to maxHeight.ceil.toInt).map(_.toDouble.asInstanceOf[java.lang.Double])
    val estimateYData = (minHeight.floor.toInt to maxHeight.ceil.toInt).map(x => regression.regressionFunction(x).asInstanceOf[java.lang.Double])
    chart.addSeries("預測值", estimateXData.asJava, estimateYData.asJava).setXYSeriesRenderStyle(XYSeriesRenderStyle.Line).setLineWidth(5.0f)

    chart
  }
}