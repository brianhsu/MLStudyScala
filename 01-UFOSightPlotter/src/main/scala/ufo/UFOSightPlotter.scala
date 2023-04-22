package ufo

import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart._
import ufo.model.UFOSight
import ufo.model.USStates.usStates
import ufo.plotter.{HistogramPlotter, LineChartPlotter}

import scala.jdk.CollectionConverters._

object UFOSightPlotter {

  def main(args: Array[String]): Unit = {
    val allUFOSight: List[UFOSight] = UFOSight.readFromTSVFile("data/ufo_awesome.tsv")

    // 拿到資料後，第一步需要清理資料，以這個例子來看，由於部份資料沒有「發生日期」這個資訊。
    // 而我們最終要分析的東西是「美國各州的 UFO 目擊次數是否有季節性規律」，所以我們需要進行
    // 以下的資料清理：
    //   1. 清除沒有目擊日期的資料
    //   2. 清除美國以外的資料
    val usOnlyUFOSightWithDate = allUFOSight
      .filterNot(sight => sight.dateOccurred.isEmpty || sight.dateReported.isEmpty)
      .filterNot(_.location.isEmpty)
      .filter(x => usStates.contains(x.location.get.state))

    // 先進行初步的資料探堪，可以發現最早於西元 1400 年就有 UFO 目擊報告。
    println("最舊一次的目擊日期：" + usOnlyUFOSightWithDate.map(_.dateOccurred.get).min)
    println("最新一次的目擊日期：" + usOnlyUFOSightWithDate.map(_.dateOccurred.get).max)

    // 首先我們針對所有美國的目擊資料進行直方圖作圖，從圖中可以看出資料集中在 1990 年後，
    //
    // 此圖片存放在 images/allSightsInUS.png 此檔案中。
    val allSights = new HistogramPlotter(usOnlyUFOSightWithDate).tenYearBuckets("所有目擊資料")
    BitmapEncoder.saveBitmap(allSights, "images/allSightsInUS.png", BitmapFormat.PNG)

    // 接著我們針對 1990 年後的目擊資料，以季為單位進行直方圖作圖。
    // 從圖中我們可以看出，目擊次數似乎有隨著時間升高的趨勢。
    //
    // 此圖片存放在 images/modernSightsInUS.png 檔案中
    val usModernUFOSight = usOnlyUFOSightWithDate.filter(_.dateOccurred.get.getYear >= 1990)
    val modernSights = new HistogramPlotter(usModernUFOSight).quarterBuckets("1990 後目擊資料")
    BitmapEncoder.saveBitmap(modernSights, "images/modernSightsInUS.png", BitmapFormat.PNG)

    // 最後，針對美國各州 1990 後的目擊事件，我們分開進行折線圖作圖，並將其放到同一張大圖中。
    //
    // 從圖中可以看出，加州 (CA) 與華聖頓州 (WA) 的目擊記錄明顯比其他州多，
    // 而某些州有明顯的高峰期。
    //
    // 此圖片存放在 images/sightGirdByStates.png 檔案中
    val startYear = usModernUFOSight.map(_.dateOccurred.get.getYear).min
    val endYear = usModernUFOSight.map(_.dateOccurred.get.getYear).max
    val plotter = new LineChartPlotter(usModernUFOSight, startYear, endYear)
    val sightGridByStates = usStates.toList.sorted.map(plotter.plot)

    BitmapEncoder.saveBitmap(sightGridByStates.asJava, 10, 5, "images/sightGirdByStates.png", BitmapFormat.PNG)

    // 將圖片以 Swing 應用程式方式顯示
    new SwingWrapper(allSights).setTitle("所有目擊資料").displayChart()
    new SwingWrapper(modernSights).setTitle("1990 後目擊資料").displayChart()
    new SwingWrapper(sightGridByStates.asJava, 5, 10).setTitle("美國各州目擊比較圖").displayChartMatrix()
  }
}

