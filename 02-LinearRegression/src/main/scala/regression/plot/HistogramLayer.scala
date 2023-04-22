package regression.plot

import org.knowm.xchart.Histogram

import scala.jdk.CollectionConverters._

case class HistogramLayer(histogram: Histogram, title: String)

object HistogramLayer {
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
