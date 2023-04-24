package regression.model

import scala.io.Source
import scala.util.Using

/**
 * 身高體重與性別
 *
 * @param gender  性別
 * @param height  身高，以英吋為單位
 * @param weight  體重，以磅為單位
 */
case class Record(gender: Gender, height: Double, weight: Double)

object Record {

  /**
   * 從 CSV 檔中讀取檔案
   *
   * @param filename  檔案名稱
   * @return          身高體重與性別記錄
   */
  def readFromCSVFile(filename: String): List[Record] = {
    Using.resource(Source.fromFile(filename)) { source =>
      source.getLines().drop(1).map(parseRecord).toList
    }
  }

  private def parseRecord(row: String): Record = {
    val columns = row.split(",")
    val gender = Gender(columns(0).replace("\"", ""))
    val height = columns(1).toDouble
    val weight = columns(2).toDouble
    Record(gender, height, weight)
  }
}