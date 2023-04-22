package ufo.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.io.Source
import scala.util.{Try, Using}

/**
 * UFO 目擊記錄
 *
 * @param dateOccurred      發生時間
 * @param dateReported      報告日期
 * @param location          發生地點
 * @param shortDescription  簡易描述
 * @param duration          持續時間
 * @param longDescription   詳細描述
 */
case class UFOSight(
  dateOccurred: Option[LocalDate], dateReported: Option[LocalDate], 
  location: Option[Location], shortDescription: Option[String], 
  duration: Option[String], longDescription: Option[String]
) {

  /**
   * 發生年份與季度，以 1995Q1 代表 1995 年第一季度， 1996Q3 代表 1996 年第三季度此方式表示
   */
  val yearAndQuarter = dateOccurred.map(date => s"${date.getYear}Q${date.format(DateTimeFormatter.ofPattern("Q"))}")

  /**
   * 發生年份與月份，以 yyyy-MM 方式表示，例如 1995-05 代表 1995 年 5 月
   */
  val yearAndMonth = dateOccurred.map(date => date.format(DateTimeFormatter.ofPattern("yyyy-MM")))
}

object UFOSight {

  /**
   * 從 TSV 檔讀取 UFO 目擊資料
   *
   * TSV 中每一行為一筆目擊資料，以 TAB 鍵分隔
   *
   * @param fileName  檔案名稱
   * @return          目擊記錄列表
   */
  def readFromTSVFile(fileName: String): List[UFOSight] = {
    Using.resource(Source.fromFile(fileName)) { source =>
      source.getLines()
        .map(parseRow)
        .toList
    }
  }

  /**
   * 將 TSV 中的一行轉為目擊記錄
   *
   * 此函式會進行基本驗證，如果欄位因格式錯誤無法解析，則視為無該欄位
   *
   * @param row 原始資料字串
   * @return    目擊記錄
   */
  private def parseRow(row: String): UFOSight = {
    val columns = row.split("\t").toList
    val dateOccurred = Try(LocalDate.parse(columns(0), DateTimeFormatter.ofPattern("yyyyMMdd"))).toOption
    val dateReported = Try(LocalDate.parse(columns(1), DateTimeFormatter.ofPattern("yyyyMMdd"))).toOption
    val location = parseLocation(columns(2))
    val shortDescription = Try(columns(3)).map(_.trim).filter(!_.isEmpty).toOption
    val duration = Try(columns(4)).map(_.trim).filter(!_.isEmpty).toOption
    val longDescription = Try(columns(5)).map(_.trim).filter(!_.isEmpty).toOption

    UFOSight(dateOccurred, dateReported, location, shortDescription, duration, longDescription)
  }

  /**
   * 針對目擊地點的原始字串，將其轉換為 Location 物件
   *
   * 此函式會將例如 `"Redmond, WA"` 的字串，轉為 `Location(Redmond, WA)` 的物件，
   * 同時會將州的縮寫，一律轉為大寫，方便後續資料處理。
   *
   * 例如 `"Bessemer, al"` 將會被轉換為 `Location(Bessemer, AL)` 物件。
   *
   * @param rawData 原始資料字轉
   * @return        Location 物件
   */
  private def parseLocation(rawData: String): Option[Location] = {
    Try {
      val Array(city, state) = rawData.split(",")
      Location(city.trim, state.trim.toUpperCase)
    }.toOption
  }

}

