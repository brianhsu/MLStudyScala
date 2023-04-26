package regression

import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.{SwingWrapper, XYChartBuilder}
import regression.model.Record
import regression.model.Gender._

import scala.jdk.CollectionConverters._

object HelloWorld {

  /**
   * 計算樣本當中的資料點與利用推測出來的公式之間的距離（cost）
   *
   * @param   record 樣本的資料點
   * @param   regressionFunction 推測出的線性迴歸公式
   * @return  將 `x` 的值代入 regressionFunction 後的結果，與 `y` 值的差距的絕對值
   */
  def calculateCostForDataPoint(record: Record, regressionFunction: Double => Double): Double = {
    def estimatedWeight = regressionFunction(record.height)
    def distance = estimatedWeight - record.weight
    distance * distance
  }

  def calcuateSumOfCost(records: List[Record], regressionFunction: Double => Double): Double = {
    val sumOfSquares = records.map(x => calculateCostForDataPoint(x, regressionFunction)).sum
    0.5 * records.size * sumOfSquares
  }


  def main(args: Array[String]): Unit = {

    val maleData = Record.readFromCSVFile("data/01_heights_weights_genders.csv").filter(_.gender == Male)

    var minCost = Double.MaxValue
    var finalTheta0 = Double.NaN
    var finalTheta1 = Double.NaN
    var regressionLine: Option[Double => Double] = None

    for {
      i <- BigDecimal(0) to BigDecimal(10) by BigDecimal(0.01)
      j <- BigDecimal(-300) to BigDecimal(300) by BigDecimal(1)
    } {
      val currentFunction = (x: Double) => (i * x + j).toDouble
      val currentCost = calcuateSumOfCost(maleData, currentFunction)

      println(f"Y = ${i}X + $j, cost: ${currentCost}%.5f")

      if (minCost > currentCost) {
        println(f"Found a better function! cost=${currentCost}%.5f")
        minCost = currentCost
        regressionLine = Some(currentFunction)
        finalTheta0 = i.toDouble
        finalTheta1 = j.toDouble
      }
    }
    println("Final Result")
    println(s"theta0 = $finalTheta0, theta1 = $finalTheta1, cost = $minCost")


    /*
    val theta0 = 5.96
    val theta1 = -224
    val func = (x: Double) => theta0 * x + theta1
    val dataPointX = (55 to 80).toList.map(_.toDouble).map(_.asInstanceOf[java.lang.Double])
    val dataPointY = (55 to 80).toList.map(_.toDouble).map(func).map(_.asInstanceOf[java.lang.Double])

    val chart = new XYChartBuilder()
      .xAxisTitle("身高")
      .yAxisTitle("體重")
      .build()


    val maleHeight = maleData.map(_.height.asInstanceOf[java.lang.Double])
    val maleWeight = maleData.map(_.weight.asInstanceOf[java.lang.Double])
    chart.addSeries("樣本值", maleHeight.asJava, maleWeight.asJava).setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter)
    chart.addSeries("預測值", dataPointX.asJava, dataPointY.asJava).setXYSeriesRenderStyle(XYSeriesRenderStyle.Line)

    new SwingWrapper(chart).displayChart()

     */

  }
}
