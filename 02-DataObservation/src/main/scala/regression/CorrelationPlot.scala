package regression

import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart._
import regression.model.Gender.{Female, Male}
import regression.model.Record

import java.awt.Color
import scala.jdk.CollectionConverters._


/**
 * 用來觀察身高體重是否有相關性的作圖
 */
object CorrelationPlot {

  case class ScatterLayer(xData: List[Double], yData: List[Double], title: String, color: Option[Color] = None)

  def main(args: Array[String]): Unit = {

    val allData: List[Record] = Record.readFromCSVFile("data/01_heights_weights_genders.csv")


    // 針對全體身高體重進行繪圖，可以看出似乎身高與體重有相關性。
    // 也就是說，我們之後應該可以用線性迴歸的方式，以身高預測體重。
    // 圖片儲存於 images/allGenderScatter.png 中
    val allHeights = allData.map(_.height)
    val allWeights = allData.map(_.weight)
    val allGenderScatter = createScatterChart(ScatterLayer(allHeights, allWeights, "全體身高體重"))
    BitmapEncoder.saveBitmap(allGenderScatter, "images/allGenderScatter.png", BitmapFormat.PNG)

    // 針對男性身高體重進行繪圖，可以看出似乎身高與體重有相關性。
    // 也就是說，我們之後應該可以用線性迴歸的方式，以身高預測體重。
    // 圖片儲存於 images/maleScatter.png 中
    val maleHeights = allData.filter(_.gender == Male).map(_.height)
    val maleWeights = allData.filter(_.gender == Male).map(_.weight)
    val maleScatter = createScatterChart(ScatterLayer(maleHeights, maleWeights, "男性身高體重"))
    BitmapEncoder.saveBitmap(maleScatter, "images/maleScatter.png", BitmapFormat.PNG)

    // 針對女性身高體重進行繪圖，可以看出似乎身高與體重有相關性。
    // 也就是說，我們之後應該可以用線性迴歸的方式，以身高預測體重。
    // 圖片儲存於 images/femaleScatter.png 中
    val femaleHeights = allData.filter(_.gender == Female).map(_.height)
    val femaleWeights = allData.filter(_.gender == Female).map(_.weight)
    val femaleScatter = createScatterChart(ScatterLayer(femaleHeights, femaleWeights, "女性身高體重", Some(Color.RED)))
    BitmapEncoder.saveBitmap(femaleScatter, "images/femaleScatter.png", BitmapFormat.PNG)

    // 針對男性女性分組的身高體重進行繪圖，可以看出似乎身高與體重有相關性，
    // 且男女性似組別似乎在圖上是有明顯的分區的。
    //
    // 也就是說，我們之後有機會用機器學習建立分類演算法，給定一個身高體重，
    // 來判定這個人是男性還是女性。
    //
    // 圖片儲存於 images/groupScatter.png 中
    val groupScatter = createScatterChart(
      ScatterLayer(maleHeights, maleWeights, "男性身高體重"),
      ScatterLayer(femaleHeights, femaleWeights, "女性身高體重")
    )
    BitmapEncoder.saveBitmap(groupScatter, "images/groupScatter.png", BitmapFormat.PNG)

    new SwingWrapper(List(allGenderScatter, maleScatter, femaleScatter, groupScatter).asJava).displayChartMatrix()
  }

  /**
   * 繪製身高體重分佈圖
   *
   * @param   layers  圖層列表
   * @return          可以給 XChart 進行繪圖的物件
   */
  private def createScatterChart(layers: ScatterLayer*): XYChart = {
    val chart = new XYChartBuilder()
      .xAxisTitle("身高（英吋）")
      .yAxisTitle("體重（磅）")
      .build()

    layers.foreach { layer =>
      chart.addSeries(layer.title, layer.xData.toArray, layer.yData.toArray)
    }

    if (layers.exists(x => x.color.isDefined)) {
      chart.getStyler.setSeriesColors(layers.flatMap(_.color).toArray)
    }

    chart.getStyler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter)
    chart

  }



}
