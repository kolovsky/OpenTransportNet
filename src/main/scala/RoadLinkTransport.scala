package OTN_RoadLink.OpenTransportNet.RoadLink

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Created by Daniel Beran on 27.8.2015.
 */
class RoadLinkTransport ()     {

  val variation = new Array[Array[String]](43) /** nacitam matici variaci pro parametry k vypoctum*/

  var trafficVolume = new Array[Array[String]](7) /** matice Traffic Volume pro jeden prvek RoadLink */

  var idParameter = 1

  var counter = 0

 /* var mainRoadCounter = 0;
  var firstClassCounter = 0
  var secondClassCounter = 0
  var thirdClassCounter = 0
  var thirdClassCounterBEFORE = 0
  var thirdClassCounterAFTER = 0
  var thirdClassCounterCORRECT = 0
  */
  /**
   *
   * @param fileName    nazev souboru csv s nacitanou matici RoadLink
   *
   * Nacte postupne kazdy radek matice RoadLink do pameti a pomoci metody processFeature jej zpracuje.
   */
  def loadMatrix (fileName: String) = {

    val source = io.Source.fromFile(fileName)("UTF-8")

    for  (line <- source.getLines) {
      val lineSegment = line.split(";").map(_.trim).map(_.replaceFirst("," , "."))//.map(_.toDouble)

      /*if (lineSegment (14) == "fifthClass") thirdClassCounterCORRECT += 1
      if (lineSegment (13) == "fifthClass") {thirdClassCounterBEFORE += 1; println("Chybne pred:"+lineSegment(2))}
      if (lineSegment (15) == "fifthClass") {thirdClassCounterAFTER += 1; println("Chybne po: "+lineSegment(2))}*/

      if ((lineSegment (14) == "mainRoad" ||lineSegment (14) == "firstClass" || lineSegment  (14) == "secondClass" || lineSegment (14) == "thirdClass" ) && lineSegment (25).toDouble != 0) {/*vyhozeni 4/5 tridy a nulovych segmentu*/
      trafficVolume = processFeature(lineSegment)

        /*if (lineSegment (14) == "mainRoad") mainRoadCounter += 1
        if (lineSegment (14) == "firstClass") firstClassCounter += 1
        if (lineSegment (14) == "secondClass") secondClassCounter += 1
        if (lineSegment (14) == "thirdClass") thirdClassCounter += 1*/

        counter = counter +1
      }

      //if (idParameter == 13912257) printMatrix(trafficVolume, ((24*7*4)+1), 6 )
    }
    //println(idParameter)
    //println("Pocet prvku (): "+counter)
    /*
    println("3CK korektni: "+thirdClassCounterCORRECT)
    println("3CK pred: "+thirdClassCounterBEFORE)
    println("3CK po: "+thirdClassCounterAFTER)*/

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
    if ((lineSegment (14) == "mainRoad" ||lineSegment (14) == "firstClass" || lineSegment  (14) == "secondClass" || lineSegment (14) == "thirdClass" ) && lineSegment (25).toDouble != 0) {/*vyhozeni 4/5 tridy a nulovych segmentu*/

    }else{
      return null
    }

    var trafficVolumeSingleFeature = new Array[Array[String]]((1*24*7*4)+2) // zaklada matici Traffic Volume pro jeden prvek RoadLink

    trafficVolumeSingleFeature = createFirstRow(trafficVolumeSingleFeature)
    trafficVolumeSingleFeature = createSecondRow(lineSegment, trafficVolumeSingleFeature)

    //println ("InspireID: "+lineSegment(2)+"; RoadClass: "+lineSegment(14)+"; Traffic Volume:"+ lineSegment(25))

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var dateTime = dateFormat.parse("2016-04-04 00:00:00")
    var seasonParameter = 0

    var sumTrafficVolumeCheck: Double = 0

    val roadClass = lineSegment (14)
    var roadClassParameter = 0

    if (roadClass == "mainRoad") roadClassParameter = 0
    if (roadClass == "firstClass") roadClassParameter = 1
    if (roadClass == "secondClass" || roadClass == "thirdClass" ) roadClassParameter = 2


    idParameter += 1
    for (i <- 1 to (1*24*7*4) ){
      val nextRow = new Array[String](7)

      nextRow(0) = idParameter.toString

      if (dateFormat.format(dateTime) == "2016-04-11 00:00:00"){ dateTime = dateFormat.parse("2016-07-04 00:00:00"); seasonParameter = 1}
      if (dateFormat.format(dateTime) == "2016-07-11 00:00:00"){ dateTime = dateFormat.parse("2016-09-05 00:00:00"); seasonParameter = 2}
      if (dateFormat.format(dateTime) == "2016-09-12 00:00:00"){ dateTime = dateFormat.parse("2016-12-05 00:00:00"); seasonParameter = 3}
      idParameter += 1
          nextRow(1) = lineSegment (2)//.substring(0,lineSegment(2).length - 3)

      val hourOfDay2: Int = dateTime.getHours
      var dayOfWeek2: Int = dateTime.getDay

      if (dayOfWeek2 == 0) dayOfWeek2 = 8
      else dayOfWeek2 += 1

      val trafficVolumeHour: Double = lineSegment (25).toDouble * (variation(26+roadClassParameter* 4 + seasonParameter )(dayOfWeek2).toDouble/100*variation(2+ seasonParameter* 6+roadClassParameter)(hourOfDay2+ 1).toDouble/100)

      /*println((26+roadClassParameter* 4 + seasonParameter )+" "+dayOfWeek2+";"+(2+ seasonParameter* 6+roadClassParameter)+" "+(hourOfDay2+ 1))
      println (roadClassParameter)
      println(seasonParameter)*/


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
    //println("sumTrafficVolumeCheck: " + (sumTrafficVolumeCheck/lineSegment(25).toDouble).toString.substring(0,6)) //sumu vypoctu vydelim trafficVolume pro jeden prumerny den a mel bych dostat 7 (dnu v tydnu) (28 (dny v tydnu krat 4 mesice))
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
    secondRow(1) = lineSegment (2)//.substring(0,lineSegment(2).length - 3)
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

    val source = io.Source.fromFile(fileName)("utf-8")

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
