package regression

import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle
import org.knowm.xchart._
import regression.model.Gender.{Female, Male}
import regression.model.Record
import regression.plot.HistogramLayer


/**
 * 用來觀察身高體重是否有相關性的作圖
 */
object CorrelationPlot {

  def main(args: Array[String]): Unit = {

    val rows: List[Record] = Record.readFromCSVFile("data/01_heights_weights_genders.csv")


  }


}
