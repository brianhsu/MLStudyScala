package regression

import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle
import org.knowm.xchart._
import org.nspl._
import org.nspl.awtrenderer._
import regression.model.Gender.{Female, Male}
import regression.model.Record
import regression.plot.HistogramLayer

import java.io.File

/**
 * 用來觀察資料集內的身高體重分佈的作圖
 */
object DistributionPlot {

  def main(args: Array[String]): Unit = {

    val rows: List[Record] = Record.readFromCSVFile("data/01_heights_weights_genders.csv")

    // 以一英吋為單位，製作身高直方圖。
    // 我們可以從圖中可以看出來，似乎有接近常態分佈。
    // 圖片存在 images/histogramChart1Inch.png 這個檔案中
    val histogram1Inch = HistogramLayer(rows.map(_.height), 1)
    val histogramChart1Inch = plot(List(histogram1Inch))
    BitmapEncoder.saveBitmap(histogramChart1Inch, "images/histogramChart1Inch.png", BitmapFormat.PNG)

    // 若刻度太粗糙，則可能會失真，觀察資料時應注意是否在合適的刻度上。
    // 下述以 5 英吋為單位，製作身高直方圖。
    // 這張圖已經看不太出來常態分佈的感覺了
    val histogram5Inch = HistogramLayer(rows.map(_.height), 5)
    val histogramChart5Inch = plot(List(histogram5Inch))
    BitmapEncoder.saveBitmap(histogramChart5Inch, "images/histogramChart5Inch.png", BitmapFormat.PNG)

    // 若刻度過細，則可能會難以解讀資料，觀察資料時應注意是否在合適的刻度上。
    // 下述以 0.001 英吋為單位，製作身高直方圖。
    // 這張圖已經看不太出來常態分佈的感覺了
    val histogram001Inch = HistogramLayer(rows.map(_.height), 0.001)
    val histogramChart001Inch = plot(List(histogram001Inch))
    BitmapEncoder.saveBitmap(histogramChart5Inch, "images/histogramChart001Inch.png", BitmapFormat.PNG)

    // 製作以 1 英吋為單位的身高密度圖，可以觀察到接近常態分佈，但頂部似乎有些平，
    // 這可能代表資料有隱藏的結構。
    //
    // 註：由於 XChart 繪圖函式庫的關係，此處並非真的密度圖，僅示意。
    val densityChart1Inch = plot(List(histogram1Inch), CategorySeriesRenderStyle.Area)
    val allDensityInXChart = new XYChartBuilder().xAxisTitle("身高").build()
    allDensityInXChart.addSeries("HelloWorld", histogram1Inch.histogram.getxAxisData(), histogram1Inch.histogram.getyAxisData())
    BitmapEncoder.saveBitmap(allDensityInXChart, "images/allDensityInXChart.png", BitmapFormat.PNG)

    // 使用 NSPL 繪制真正的 10000 人體身高密度圖
    // 檔案儲存在 images/allHeightDensityInNSPL.png 當中
    val allDensityInNPSL = xyplot
      { density(rows.map(_.height).toIndexedSeq, 1) -> line() }
      { par.withXLab("身高").withYLab("密度").withMain("10000 人身高密度圖") }

    pngToFile(new File("images/allHeightDensityInNSPL.png"), allDensityInNPSL)

    // 因為我們懷疑資料有隱藏的結構，在這種情況下，我們可以試著將資料分組。
    // 以這個資料集而言，最簡單的分組方式就是以性別分組，所以我們可以試著針對不同性別分別作圖。
    // 從這張圖來看，確實有兩個集群。
    val maleHeight = rows.filter(_.gender == Male).map(_.height)
    val femaleHeight = rows.filter(_.gender == Female).map(_.height)
    val maleHeightHistogram = HistogramLayer(maleHeight, 1, "男性身高")
    val femaleHeightHistogram = HistogramLayer(femaleHeight, 1, "女性身高")
    val heightByGender = plot(List(maleHeightHistogram, femaleHeightHistogram), CategorySeriesRenderStyle.Area)
    BitmapEncoder.saveBitmap(histogramChart5Inch, "images/heightByGender.png", BitmapFormat.PNG)

    // 使用 NSPL 繪制男女分組的身高密度圖
    // 檔案儲存在 images/genderHeightDensityInNSPL.png 當中
    val heightDensityInNPSL = xyplot(
      density(maleHeight.toIndexedSeq, 1) -> line(color = Color.BLUE),
      density(femaleHeight.toIndexedSeq, 1) -> line(color = Color.RED),
    ) {
      par.withXLab("身高").withYLab("密度").withMain("10000 人身高密度圖")
    }
    pngToFile(new File("images/genderHeightDensityInNSPL.png"), heightDensityInNPSL)

    // 針對體重，同樣依性別分開作圖，觀察是否有類似的現象。
    val maleWeight = rows.filter(_.gender == Male).map(_.weight)
    val femaleWeight = rows.filter(_.gender == Female).map(_.weight)
    val maleWeightHistogram = HistogramLayer(maleWeight, 5, "男性體重")
    val femaleWeightHistogram = HistogramLayer(femaleWeight, 5, "女性體重")
    val weightByGender = plot(List(maleWeightHistogram, femaleWeightHistogram), CategorySeriesRenderStyle.Area)
    BitmapEncoder.saveBitmap(weightByGender, "images/weightByGender.png", BitmapFormat.PNG)

    // 使用 NSPL 繪制男女分組的身高密度圖
    // 檔案儲存在 images/genderWeightDensityInNSPL.png 當中
    val weightDensityInNPSL = xyplot(
      density(maleWeight.toIndexedSeq, 5) -> line(color = Color.BLUE),
      density(femaleWeight.toIndexedSeq, 5) -> line(color = Color.RED),
    ) {
      par.withXLab("體重").withYLab("密度").withMain("10000 人身體重度圖")
    }
    pngToFile(new File("images/genderWeightDensityInNSPL.png"), weightDensityInNPSL)

    new SwingWrapper(histogramChart1Inch).displayChart()
    new SwingWrapper(histogramChart5Inch).displayChart()
    new SwingWrapper(histogramChart001Inch).displayChart()
    new SwingWrapper(densityChart1Inch).displayChart()
    new SwingWrapper(heightByGender).displayChart()
    new SwingWrapper(weightByGender).displayChart()
  }


  def plot(histograms: List[HistogramLayer], style: CategorySeriesRenderStyle = CategorySeriesRenderStyle.Bar): CategoryChart = {
    val chart = new CategoryChartBuilder().xAxisTitle("身高").yAxisTitle("數量").build()

    histograms.foreach { histogram =>
      chart.addSeries(histogram.title, histogram.histogram.getxAxisData(), histogram.histogram.getyAxisData())
    }

    chart.getStyler.setXAxisMaxLabelCount(10)
    chart.getStyler.setDefaultSeriesRenderStyle(style)
    chart.getStyler.setAntiAlias(true)
    chart
  }

}
