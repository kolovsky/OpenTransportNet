package OTN_RoadLink.OpenTransportNet.RoadLink

import java.io.{FileWriter, BufferedWriter, OutputStreamWriter, FileOutputStream}
import java.text.SimpleDateFormat
import java.util.Calendar


/**
 * Vytvoril Daniel Beran, 2015-2017.
  * 2017-05-10: Uprava pro kvetnova data Plzne s kapacitou.
 */
class RoadLinkTransport (calendarFileName: String, variationFileName: String, roadLinkFileName: String,
                         nameInspireID: String, nameRoadType: String, nameTrafficVolume: String, nameTrafficCapacity: String,
                         printToFile: Int )     {

  val variation = new Array[Array[String]](43) /** Nacitani casovych variacnich tabulek s paramatry k vypoctum hodinovych intenzit*/
  loadMatrixVariation()

  var numberOfDays = 0;
  val calendarMatrix = new Array[Array[String]](356) /** Nacitani casovych variacnich tabulek s paramatry k vypoctum hodinovych intenzit*/
  loadMatrixCalendar()


  val source = io.Source.fromFile(roadLinkFileName)("UTF-8")
  val line = source.getLines()
  var firstLine = line.next().split(";").map(_.trim).map(_.replaceFirst("," , "."))
  var indices = new Array[Int](3)/** pole pro indexy atributu v Roadlink*/
  indices = locateIndices(firstLine) /** Vyhledani indexu v poli dle jejich pojmednovani na prvni radce vstupniho souboru. */

  var idParameter = 1
  var firstFile = 1

  /**
   *
   * @return listIndices      pole s indexy
   *
   * listIndices(0) - odpovida cislo sloupce, v kterm je nameInspireID
   * listIndices(1) - odpovida cislo sloupce, v kterm je nameRoadType
   * listIndices(2) - odpovida cislo sloupce, v kterm je nameTrafficVolume
   */
  def locateIndices (attributeRow: Array[String]) : Array [Int] = {
    val listIndices = new Array[Int](4)
    val size = attributeRow.length


    for (i <- 0 to size - 1){

      if (attributeRow(i) == nameInspireID){
        listIndices(0) = i
        //println(nameInspireID+i)
      }
      if (attributeRow(i) == nameRoadType){
        listIndices(1) = i
        //println(nameRoadType+i)
      }
      if (attributeRow(i) == nameTrafficVolume){
        listIndices(2) = i
        //println(nameTrafficVolume+i)
      }
      if (attributeRow(i) == nameTrafficCapacity){
        listIndices(3) = i
        //println(nameTrafficVolume+i)
      }
    }
    //print(listIndices(3))
    return listIndices

  }

  /**
   *
   * @return Boolean
   *
   * Probiha kontrola podminek, musi byt splneny vsechny:
   * - Nenulova hodnota RPDI
   * - Kategorie komunikace neni fourth class
   * - Kategorie komunikace neni fifth class
   *
   * true - podminky jsou splneny
   * false - podminky nejsem splneny
   */
  def getConditions (lineSegment: Array[String]) : Boolean =  {
    if (lineSegment(indices(2)).toDouble == 0){ /*println("0: "+ lineSegment(indices(0)));*/ return false}
    //else if (lineSegment(indices(1)) == "fourthClass"){ /*println("fourthClass: "+ lineSegment(indices(0)));*/ return false}
    //else if (lineSegment(indices(1)) == "fifthClas"){ /*println("fifthClas: "+ lineSegment(indices(0)));*/ return false}

    else return true
  }

  /**
   *
   * @return trafficVolumeSingleFeature   vysledna tabulka TrafficVolume pro jeden prvek RoadLink
   *
   * Vypocte pomoci variacnich tabulek Variation vsechny casove varianty a vrati je v 2D poli.
   */
  def processFeaturePilsen () : Array[Array[String]] = {

    val lineSegment = line.next().split(";").map(_.trim).map(_.replaceFirst("," , "."))
    //print(lineSegment(3))

    //var trafficVolumeSingleFeature = new Array[Array[String]]((1*24*7*4)+2) // zaklada matici Traffic Volume pro jeden prvek RoadLink

    var trafficVolumeSingleFeature = new Array[Array[String]]((24*numberOfDays)+2) // zaklada matici Traffic Volume pro jeden prvek RoadLink
    trafficVolumeSingleFeature = createFirstRow(trafficVolumeSingleFeature)
    val condition = getConditions(lineSegment)
    if (!condition) return trafficVolumeSingleFeature

    else {
      trafficVolumeSingleFeature = createSecondRow(lineSegment, trafficVolumeSingleFeature)

      val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

      var dateTime = dateFormat.parse(calendarMatrix(1)(0))
      var seasonParameter = 0

      val roadClass = lineSegment (indices(1))
      var roadClassParameter = 0

      if (roadClass == "mainRoad") roadClassParameter = 0
      if (roadClass == "firstClass") roadClassParameter = 1
      if (roadClass == "secondClass" || roadClass == "thirdClass" ) roadClassParameter = 2
      if (roadClass == "fourthClass" || roadClass == "fifthClass" ) roadClassParameter = 3

      idParameter += 1
      var hourIterator = 0;
      var dayIterator = 1;
      for (i <- 1 to (24*numberOfDays) ){ //ukladani jednoho radku, tj. jedne hodiny

        val nextRow = new Array[String](8)

        nextRow(0) = idParameter.toString

        /*if (dateFormat.format(dateTime) == "2016-04-11 00:00:00"){ dateTime = dateFormat.parse("2016-07-04 00:00:00"); seasonParameter = 1}
        if (dateFormat.format(dateTime) == "2016-07-11 00:00:00"){ dateTime = dateFormat.parse("2016-09-05 00:00:00"); seasonParameter = 2}
        if (dateFormat.format(dateTime) == "2016-09-12 00:00:00"){ dateTime = dateFormat.parse("2016-12-05 00:00:00"); seasonParameter = 3}*/

        var dayOfWeek2 = 0

        if (calendarMatrix(dayIterator)(1) == "Po") dayOfWeek2 = 2
        if (calendarMatrix(dayIterator)(1) == "Ut") dayOfWeek2 = 3
        if (calendarMatrix(dayIterator)(1) == "St") dayOfWeek2 = 4
        if (calendarMatrix(dayIterator)(1) == "Ct") dayOfWeek2 = 5
        if (calendarMatrix(dayIterator)(1) == "Pa") dayOfWeek2 = 6
        if (calendarMatrix(dayIterator)(1) == "So") dayOfWeek2 = 7
        if (calendarMatrix(dayIterator)(1) == "Ne") dayOfWeek2 = 8
          //println(dayOfWeek2)
        if (calendarMatrix(dayIterator)(2) == "Jaro") seasonParameter = 0
        if (calendarMatrix(dayIterator)(2) == "Leto") seasonParameter = 1
        if (calendarMatrix(dayIterator)(2) == "Podzim") seasonParameter = 2
        if (calendarMatrix(dayIterator)(2) == "Zima") seasonParameter = 3
          //println(seasonParameter)

        idParameter += 1
            nextRow(1) = lineSegment (indices(0))

        val hourOfDay2: Int = dateTime.getHours
        //var dayOfWeek2: Int = dateTime.getDay

        //if (dayOfWeek2 == 0) dayOfWeek2 = 8
        //else dayOfWeek2 += 1

        val trafficVolumeHour: Double = lineSegment (indices(2)).toDouble * (variation(26+roadClassParameter* 4 + seasonParameter )(dayOfWeek2).toDouble/100*variation(2+ seasonParameter* 6+roadClassParameter)(hourOfDay2+ 1).toDouble/100)

       // println(lineSegment (indices(2)))
       // println(variation(26+roadClassParameter* 4 + seasonParameter )(dayOfWeek2))
       // println(variation(2+ seasonParameter* 6+roadClassParameter)(hourOfDay2+ 1))

        nextRow(2) = trafficVolumeHour.toString
        nextRow(3) = "hour"
        nextRow(4) = dateFormat.format(dateTime)

        val cal = Calendar.getInstance()
        cal.setTime(dateTime)
        cal.add(Calendar.HOUR_OF_DAY, 1)
        dateTime = cal.getTime

        nextRow(5) = dateFormat.format(dateTime)
        nextRow(6) = "allVehicles"
        nextRow(7) = lineSegment (indices(3))


        trafficVolumeSingleFeature(i+1) = nextRow


        hourIterator = hourIterator+1
        if (hourIterator == 24){ hourIterator=0; dayIterator=dayIterator+1}
        //println(hourIterator)
        //println(dayIterator)
      }
      if (printToFile == 1) {
        printToFile(trafficVolumeSingleFeature, firstFile)
        firstFile = 0
      }

      return trafficVolumeSingleFeature
    }
  }
  def hasNext(): Boolean ={
    return line.hasNext
  }
  /**
   *
   * @param trafficVolumeSingleFeature
   *
   * Vypise 2D pole do souboru.
   */
  def printToFile(trafficVolumeSingleFeature: Array[Array[String]], firstSegment: Int) : Any = {

    if (firstSegment == 1) {
      val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("TrafficVolume.csv")))
      for (x <- trafficVolumeSingleFeature(0)) {
        writer.write(x + ";")
      }
      writer.close()
    }
    for (i <- 1 to 1+24*numberOfDays) {
      val fw: FileWriter = new FileWriter("TrafficVolume.csv", true)
      fw.write(System.lineSeparator())
      for (x <- trafficVolumeSingleFeature(i)) {
        fw.write(x + ";")
      }
      fw.close()
    }
  }

  /**
   *
   * @param trafficVolumeSingleFeature
   *
   * Vytvori prvni radek s pojmenovanim atributu.
   */
  def createFirstRow (trafficVolumeSingleFeature: Array[Array[String]] ) : Array[Array[String]] = {

    val firstRow = new Array[String](8)
    firstRow(0) = "ID"
    firstRow(1) = "roadLinkID"
    firstRow(2) = "trafficVolume"
    firstRow(3) = "trafficVolumeTimePeriod"
    firstRow(4) = "fromTime"
    firstRow(5) = "toTime"
    firstRow(6) = "vehicleType"
    firstRow(7) = "trafficCapacity"


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
  def createSecondRow (lineSegment: Array[String] , trafficVolumeSingleFeature: Array[Array[String]] ) = {

    val secondRow = new Array[String](8)
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var dateTime = dateFormat.parse("2000-01-01 00:00:00")

    secondRow(0) = idParameter.toString
    secondRow(1) = lineSegment (indices(0))//.substring(0,lineSegment(indicies(0).length - 3)
    secondRow(2) = lineSegment (indices(2))
    secondRow(3) = "day"
    secondRow(4) = dateFormat.format(dateTime)

    val cal = Calendar.getInstance()
    cal.setTime(dateTime)
    cal.add(Calendar.DAY_OF_WEEK, 1)
    dateTime = cal.getTime

    secondRow(5) = dateFormat.format(dateTime)
    secondRow(6) = "allVehicles"
    secondRow(7) = lineSegment (indices(3))


    trafficVolumeSingleFeature(1) = secondRow

    trafficVolumeSingleFeature
  }

  /**
   *
   * Nacitani tabulek variaci.
   */
  def loadMatrixVariation () : Any = {

    val source = io.Source.fromFile(variationFileName)

    var i = 0
    for  (line <- source.getLines()) {
      val lineSegment = line.split(";").map(_.trim).map(_.replaceFirst("," , "."))//.map(_.toDouble)
      variation (i) = lineSegment
      i += 1
    }
    source.close()
  }
  def loadMatrixCalendar () : Any = {

    val source = io.Source.fromFile(calendarFileName)

    var i = 0
    for  (line <- source.getLines()) {
      val lineSegment = line.split(";").map(_.trim).map(_.replaceFirst("," , "."))//.map(_.toDouble)
      calendarMatrix (i) = lineSegment
      i += 1

    }
    numberOfDays = i-1
    source.close()
  }


}
