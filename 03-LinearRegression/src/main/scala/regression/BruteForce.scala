package regression

import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.{BitmapEncoder, SwingWrapper}
import regression.model.Gender._
import regression.model.{Record, Regression}
import regression.plot.Plotter

import scala.jdk.CollectionConverters._
import scala.util.Random

object BruteForce {

  def main(args: Array[String]): Unit = {

    val startTime = System.currentTimeMillis()
    // 首先我們讀取所有的 5000 筆的男性資料，並且將其打亂
    // 最後挑選 2500 筆做為訓練資料，2500 筆做為驗證資料
    val allMaleData = Record.readFromCSVFile("data/01_heights_weights_genders.csv").filter(_.gender == Male)
    val shuffledData = Random.shuffle(allMaleData)
    val (trainingData, validationData) = shuffledData.splitAt(2500)

    // 針對訓練用的 2500 筆資料，進行暴力法求線性迴歸解
    val (regression, cost) = bruteForce(allMaleData)

    // 求出的結果
    println(s"==> Regression: $regression")
    println(s"==> MSE: $cost")
    println(s"==> duration: ${(System.currentTimeMillis() - startTime) / 1000} seconds")

    // 分別針對訓練資料、驗證資料以及所有的資料進行散佈圖繪圖，
    // 並在圖上加上我們預測出的線性迴歸的直線
    val trainingDataChart = new Plotter(trainingData, regression, "訓練資料").plot()
    val validationDataChart = new Plotter(validationData, regression, "驗證資料").plot()
    val allDataChart = new Plotter(validationData, regression, "所有資料").plot()

    BitmapEncoder.saveBitmap(trainingDataChart, "images/trainingDataChart.png", BitmapFormat.PNG)
    BitmapEncoder.saveBitmap(validationDataChart, "images/validationDataChart.png", BitmapFormat.PNG)
    BitmapEncoder.saveBitmap(allDataChart, "images/allDataChart.png", BitmapFormat.PNG)

    val charts = List(trainingDataChart, validationDataChart, allDataChart)


    new SwingWrapper(charts.asJava).displayChartMatrix()
  }

  /**
   * 使用暴力法求解線性迴歸方程式
   *
   * 用單純的暴力法求解線性迴歸方程式。
   *
   * 我們假設一定有一條直線方程式 Y = theta0 * X + theta1 的中的兩個系數，落在下面這個區間，
   * 而且可以用來代表我們的資料點。
   *
   * theta0: 0 ~ 10
   * theta1: -300 ~ 300
   *
   * 接著我們非常非常暴力的，針對每一個可能性列舉出來，並且在這裡，theta0 我們每次以 0.1 為一單位前進，
   * theta1 每次以 1 為單位前進。
   *
   * 也就是說，我們會依序列出下的線性方程式：
   *
   * Y = 0.00X - 300
   * Y = 0.00X - 299
   * Y = 0.00X + 0
   * ...
   * Y = 0.00X + 300
   * Y = 0.01X - 300
   * ...
   * Y = 0.01X + 300
   * ...
   * Y = 10.0X - 300
   * ...
   * Y = 10.0X + 300
   *
   * 接著針對每一個方程式，將訓練資料的資料點代入，並計算該方程式針對這份資料的 Mean Square Error (MSE)
   * 的值，這個值代表這個方程式有多靠近我們的訓練資料。
   *
   * 接著我們挑一個 MSE 最小的公式，做為我們訓練的結果。我們可以假設，這個公式可以用來預測沒有出現過的資料。
   *
   * @param   dataPoints  訓練資料的資料點
   * @return              預測出的最佳線性迴歸方程式
   */
  def bruteForce(dataPoints: List[Record]): (Regression, Double) = {
    var minCost = Double.MaxValue
    var regression = Regression(0, -300)

    for {
      i <- BigDecimal(0) to BigDecimal(10) by BigDecimal(0.01)
      j <- BigDecimal(-300) to BigDecimal(300) by BigDecimal(1)
    } {
      val currentRegression = Regression(i.toDouble, j.toDouble)
      val currentCost = calculateSumOfCosts(dataPoints, currentRegression)

      println(f"Y = ${i}%.2fX + $j, MSE: ${currentCost}%.5f")

      if (minCost > currentCost) {
        println(f"Found a better function! MSE=${currentCost}%.5f")
        minCost = currentCost
        regression = currentRegression
      }
    }

    (regression, minCost)
  }

  /**
   * 計算樣本當中的資料點與利用推測出來的公式之間的距離 / 成本（cost）
   *
   * 注意我們返回的值是「差距的平方」，理由是當我們用樣本中的 Y 值（體重），
   * 減去透過推測出的線性迴歸的公式計算的 Y 值時，得到的結果有可能是正數或負數。
   *
   * 我們不希望之後在計算所有樣本點與資料的差距的時候，正負數互相扺消，造成我們低估
   * 我們的公式實際上與樣本點之間的差距，所以先進行平方。
   *
   * @param record     樣本的資料點
   * @param regression 推測出的線性迴歸公式
   * @return 將 `x` 的值代入 regressionFunction 後的結果，與 `y` 值的差距的平方
   */
  def calculateCostForDataPoint(record: Record, regression: Regression): Double = {
    def estimatedWeight = regression.regressionFunction(record.height)

    def distance = estimatedWeight - record.weight

    distance * distance
  }

  /**
   * 計算所有樣本點針對線性迴歸公式的成本的 mean of square
   *
   * 這個東西在 Machine Learning 裡稱為 loss function，指的我們用來估算
   * 我們推測出的公式，和資料點之間的差距（錯誤 / 損失）。
   *
   * 這邊使用的是統計學中的 mean of square，亦即針對先計算出每個資料點的實際值，
   * 與代入我們推測出的公式後的值的差的平方和，再除以樣本數。
   *
   * 實務上，有可能會依照需求的不同，選用不同的計算方式來估算整體的損失。
   *
   * @param records    資料點的集合
   * @param regression 推測的線性迴歸公式
   * @return
   */
  def calculateSumOfCosts(records: List[Record], regression: Regression): Double = {
    val sumOfSquares = records.map(x => calculateCostForDataPoint(x, regression)).sum
    sumOfSquares / records.size
  }

}

