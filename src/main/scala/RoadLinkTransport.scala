package OTN_RoadLink.OpenTransportNet.RoadLink

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Created by Daniel Beran on 27.8.2015.
 */
class RoadLinkTransport ()     {

  //val roadLink = new Array[Array[String]](20)
  val variation = new Array[Array[String]](24)

  var trafficVolume = new Array[Array[String]](2 + (1 * 24))
  var j = 0


  def loadMatrix (fileName: String) : Any = {

    val source = io.Source.fromFile(fileName)

    for  (linie <- source.getLines) {
      val radek = linie.split(";").map(_.trim).map(_.replaceFirst("," , "."))//.map(_.toDouble)

      trafficVolume = processFeature(radek)

      if (j == 0) printMatrix(trafficVolume, (24*7)+1, 6 )
      //roadLink (j) = radek
      j += 1
    }
    source.close
  }

  def processFeature (lineSegment: Array [String] ) : Array[Array[String]] = {

    //println("TESTING METHOD")

    val trafficVolumeSinfleFeature = new Array[Array[String]]((1*24*7)+2)

    val firstRow = new Array[String](7)
    firstRow(0) = "ID"
    firstRow(1) = "inspireID"
    firstRow(2) = "trafficVolume"
    firstRow(3) = "trafficVolumeTimePeriod"
    firstRow(4) = "fromTime"
    firstRow(5) = "toTime"
    firstRow(6) = "vehicleType"

    trafficVolumeSinfleFeature(0) = firstRow

    val secondRow = new Array[String](7)

    secondRow(0) = "0"
    secondRow(1) = lineSegment (0)
    secondRow(2) = lineSegment (25)
    secondRow(3) = "day"
    secondRow(4) = "fromTime"
    secondRow(5) = "toTime"
    secondRow(6) = "vehicleType"

    trafficVolumeSinfleFeature(1) = secondRow

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var dateTime = dateFormat.parse("2016-01-04 00:00:00")

    //dateFrom.setHours(1)
    //println(dateFormat.format(dateFrom))

    var sumTrafficVolumeCheck: Double = 0

    for (i <- 1 to (1*24*7) ){
      val nextRow = new Array[String](7)

      nextRow(0) = i.toString
      nextRow(1) = lineSegment (0)

      var hourOfDay: Int = 0
      if (i%24 == 0) hourOfDay = 24-1
      else hourOfDay = (i%24)-1

      var dayOfWeek: Int = 0
      if ((i/24) -((i-1)/24) == 1 ) dayOfWeek = (i-1)/24
      else dayOfWeek = i/24

      val trafficVolumeHour: Double = lineSegment (25).toDouble * (variation(1)(dayOfWeek).toDouble*variation(2)(hourOfDay).toDouble/100)

      sumTrafficVolumeCheck = sumTrafficVolumeCheck + trafficVolumeHour

      nextRow(2) = trafficVolumeHour.toString
      nextRow(3) = "hour"
      nextRow(4) = dateFormat.format(dateTime)

      val cal = Calendar.getInstance()
      cal.setTime(dateTime)
      cal.add(Calendar.HOUR_OF_DAY, 1)
      dateTime = cal.getTime

      nextRow(5) = dateFormat.format(dateTime)
      nextRow(6) = "vehicleType"

      trafficVolumeSinfleFeature(i+1) = nextRow
    }
    //println(sumTrafficValumeCheck)



    trafficVolumeSinfleFeature
  }


  def loadMatrixVariation (fileName: String) : Any = {

    val source = io.Source.fromFile(fileName)

    var i = 0
    for  (linie <- source.getLines) {
      val radek = linie.split(";").map(_.trim).map(_.replaceFirst("," , "."))//.map(_.toDouble)
      variation (i) = radek
      i += 1
    }
    source.close
  }

  /*def createTrafficVolume () : Any = {

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    var dateFrom = dateFormat.parse("2016-01-04 00:00:00")



    //println(dateFrom)

   println(dateFormat.format(dateFrom))

    dateFrom.setHours(1)

    println(dateFormat.format(dateFrom))

    var cal = Calendar.getInstance()
    cal.setTime(dateFrom)
    cal.add(Calendar.HOUR_OF_DAY, 22)
    dateFrom = cal.getTime()

    println(dateFormat.format(dateFrom))

    cal.setTime(dateFrom)
    cal.add(Calendar.HOUR_OF_DAY, 1)
    dateFrom = cal.getTime()

    println(dateFormat.format(dateFrom))

    val firstRow = new Array[String](7)
    firstRow(0) = "ID"
    firstRow(1) = "inspireID"
    firstRow(2) = "trafficVolume"
    firstRow(3) = "trafficVolumeTimePeriod"
    firstRow(4) = "fromTime"
    firstRow(5) = "toTime"
    firstRow(6) = "vehicleType"

      trafficVolume(0) = firstRow

    val secondRow = new Array[String](7)

    secondRow(0) = "0"
    secondRow(1) = roadLink (1)(0)
    secondRow(2) = roadLink (1) (2)
    secondRow(3) = "day"
    secondRow(4) = "fromTime"
    secondRow(5) = "toTime"
    secondRow(6) = "vehicleType"

    trafficVolume(1) = secondRow

    

  }*/

  def printMatrix(matrix: Array[Array[String]], rows: Int, columns: Int ) {

    //var lengthOfArray = trafficVolume.length

    //println(lengthOfArray)

    for (m <- 0 to rows){
      for (n <- 0 to columns){
        if (n < columns) print(matrix(m)(n)+", ")
        if (n == columns) print(matrix(m)(n))
      }
      println()
    }


  }
}
