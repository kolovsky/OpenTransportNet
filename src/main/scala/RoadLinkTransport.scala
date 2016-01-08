package OTN_RoadLink.OpenTransportNet.RoadLink

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Created by Daniel Beran on 27.8.2015.
 */
class RoadLinkTransport ()     {

  val variation = new Array[Array[String]](43) /** nacitam matici variaci pro parametry k vypoctum*/

  var trafficVolume = new Array[Array[String]](2 + (1 * 24)) /** matice Traffic Volume pro jeden prvek RoadLink */

  var idParameter = 1

  /**
   *
   * @param fileName    nazev souboru csv s nacitanou matici RoadLink
   *
   * Nacte postupne kazdy radek matice RoadLink do pameti a pomoci metody processFeature jej zpracuje.
   */
  def loadMatrix (fileName: String) = {

    val source = io.Source.fromFile(fileName)

    for  (line <- source.getLines) {
      val lineSegment = line.split(";").map(_.trim).map(_.replaceFirst("," , "."))//.map(_.toDouble)

      trafficVolume = processFeature(lineSegment)

      if (idParameter <= 170) printMatrix(trafficVolume, (24*7)+1, 6 )

    }
    source.close
  }

  /**
   *
   * @param lineSegment                   jeden radek matice RoadLink
   * @return trafficVolumeSingleFeature   vysledna matice TrafficVolume pro jeden prvek RoadLink
   *
   * Vypocte pomoci matice Variation vsechny varianty dnu v tydnu a hodin v nich
   *
   * Zatim je vsechno povazovano za dalnici.
   * Zatim je vsechno povazovano za zimu.
   */
  def processFeature (lineSegment: Array [String] ) : Array[Array[String]] = {

    var trafficVolumeSingleFeature = new Array[Array[String]]((1*24*7)+2) // zaklada matici Traffic Volume pro jeden prvek RoadLink

    trafficVolumeSingleFeature = createFirstRow(trafficVolumeSingleFeature)
    trafficVolumeSingleFeature = createSecondRow(lineSegment, trafficVolumeSingleFeature)

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var dateTime = dateFormat.parse("2016-01-04 00:00:00")

    var sumTrafficVolumeCheck: Double = 0

    idParameter += 1
    for (i <- 1 to (1*24*7) ){
      val nextRow = new Array[String](7)

      nextRow(0) = idParameter.toString
      idParameter += 1
      nextRow(1) = lineSegment (0)

      val hourOfDay2: Int = dateTime.getHours
      var dayOfWeek2: Int = dateTime.getDay

      if (dayOfWeek2 == 0) dayOfWeek2 = 8
      else dayOfWeek2 += 1

      val trafficVolumeHour: Double = lineSegment (25).toDouble * (variation(29)(dayOfWeek2).toDouble/100*variation(20)(hourOfDay2 + 1).toDouble/100)

      sumTrafficVolumeCheck = sumTrafficVolumeCheck + trafficVolumeHour // zkouska vypoctu

      nextRow(2) = trafficVolumeHour.toString
      nextRow(3) = "hour"
      nextRow(4) = dateFormat.format(dateTime)

      val cal = Calendar.getInstance()
      cal.setTime(dateTime)
      cal.add(Calendar.HOUR_OF_DAY, 1)
      dateTime = cal.getTime

      nextRow(5) = dateFormat.format(dateTime)
      nextRow(6) = "allVehicles"

      trafficVolumeSingleFeature(i+1) = nextRow
    }
    println("sumTrafficVolumeCheck: " + sumTrafficVolumeCheck/lineSegment(25).toDouble) //sumu vypoctu vydelim trafficVolume pro jeden prumerny den a mel bych dostat 7 (dnu v tydnu) (28 (dny v tydnu krat 4 mesice))
    trafficVolumeSingleFeature
  }

  /**
   *
   * @param trafficVolumeSingleFeature
   *
   * Vytvori prvni radek s pojmenovanim atributu.
   */
  def createFirstRow (trafficVolumeSingleFeature: Array[Array[String]] ) : Array[Array[String]] = {

    val firstRow = new Array[String](7)
    firstRow(0) = "ID"
    firstRow(1) = "roadLinkID"
    firstRow(2) = "trafficVolume"
    firstRow(3) = "trafficVolumeTimePeriod"
    firstRow(4) = "fromTime"
    firstRow(5) = "toTime"
    firstRow(6) = "vehicleType"

    trafficVolumeSingleFeature(0) = firstRow

    trafficVolumeSingleFeature
  }

  /**
   *
   * @param lineSegment
   * @param trafficVolumeSingleFeature
   *
   * Vytvori druhy radek s trafficVolume z puvodni tabulky RoadLink a tedy intenzitou pro cely den - toTime a fromTime odpovidaji 1.1. 2000
   */
  def createSecondRow (lineSegment: Array[String] , trafficVolumeSingleFeature: Array[Array[String]] ) : Array[Array[String]] = {

    val secondRow = new Array[String](7)
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var dateTime = dateFormat.parse("2000-01-01 00:00:00")

    secondRow(0) = idParameter.toString
    secondRow(1) = lineSegment (0)
    secondRow(2) = lineSegment (25)
    secondRow(3) = "day"
    secondRow(4) = dateFormat.format(dateTime)

    val cal = Calendar.getInstance()
    cal.setTime(dateTime)
    cal.add(Calendar.DAY_OF_WEEK, 1)
    dateTime = cal.getTime

    secondRow(5) = dateFormat.format(dateTime)
    secondRow(6) = "allVehicles"

    trafficVolumeSingleFeature(1) = secondRow

    trafficVolumeSingleFeature
  }

  /**
   *
   * @param fileName nazev souboru csv s nacitanou matici Variation
   *
   * Nacitani matice variaci.
   */
  def loadMatrixVariation (fileName: String) : Any = {

    val source = io.Source.fromFile(fileName)

    var i = 0
    for  (line <- source.getLines) {
      val lineSegment = line.split(";").map(_.trim).map(_.replaceFirst("," , "."))//.map(_.toDouble)
      variation (i) = lineSegment
      i += 1
    }
    source.close
  }

  /**
   *
   * @param matrix
   * @param rows
   * @param columns
   *
   * Tiskne zadanou matici se zadanym poctem radku a sloupcu na obrazovku.
   */
  def printMatrix(matrix: Array[Array[String]], rows: Int, columns: Int ) {

    for (m <- 0 to rows){
      for (n <- 0 to columns){
        if (n < columns) print(matrix(m)(n)+", ")
        if (n == columns) print(matrix(m)(n))
      }
      println()
    }


  }
}
