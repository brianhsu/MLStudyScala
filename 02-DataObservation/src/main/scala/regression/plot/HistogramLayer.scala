package regression.plot

import org.knowm.xchart.Histogram

import scala.jdk.CollectionConverters._

/**
 * 直方圖圖層資料結構
 * @param histogram 提供給 XChart 的直方圖資料結構
 * @param title     圖層標題
 */
case class HistogramLayer(histogram: Histogram, title: String)

object HistogramLayer {

  /**
   * 從資料建立直方圖圖層
   *
   * @param dataPoints  資料點
   * @param step        直方圖中每一組為幾個點
   * @param title       圖層標題
   * @return            直方圖圖層物件
   */
  def apply(dataPoints: List[Double], step: Double, title: String = "身高"): HistogramLayer = {
    val minValue = dataPoints.min
    val maxValue = dataPoints.max
    val numBin: Int = ((maxValue.floor.toInt - minValue.ceil.toInt) / step).toInt + 1

    val histogram: Histogram = new Histogram(
      dataPoints.map(_.asInstanceOf[java.lang.Double]).asJava,
      numBin,
      minValue.floor,
      maxValue.ceil
    )

    new HistogramLayer(histogram, title)
  }

}
