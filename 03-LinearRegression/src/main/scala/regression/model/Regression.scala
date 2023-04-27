package regression.model

/**
 * 一個代表以 y = theta0 * x + theta1 組成的直線方程式
 *
 * 我們的程式碼裡，將會試著求出一個盡量能夠代表我們的資料點的直線方程式
 *
 * @param theta0  直線方程式中的 x 的係數
 * @param theta1  直線方程式中的常數
 */
case class Regression(theta0: Double, theta1: Double) {

  /**
   *  使用 theta0 和 theta1 建立的直線方程式
   */
  val regressionFunction: Double => Double = (x: Double) => theta0 * x + theta1
}
