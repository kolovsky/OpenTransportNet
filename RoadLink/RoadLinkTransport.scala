package OTN_RoadLink.OpenTransportNet.RoadLink

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Created by Daniel Beran on 27.8.2015.
 */
class RoadLinkTransport ()     {

  val roadLink = new Array[Array[String]](20)
  val variation = new Array[Array[String]](24)

  val trafficVolume = new Array[Array[String]](2+(1*24))
  var j = 0


  def loadMatrix (fileName: String) : Any = {

    val source = io.Source.fromFile(fileName)

    for  (linie <- source.getLines) {
      val radek = linie.split(";").map(_.trim).map(_.replaceFirst("," , "."))//.map(_.toDouble)
      roadLink (j) = radek
      j += 1
    }
    source.close
  }                                                 //vracet zakladni matici

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

  def createTrafficVolume () : Any = {

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

    

  }

  def printTrafficVolumeMatrix(rows: Int, columns: Int ) {

    //var lengthOfArray = trafficVolume.length

    //println(lengthOfArray)

    for (m <- 0 to rows){
      for (n <- 0 to columns){
        if (n < columns) print(trafficVolume(m)(n)+", ")
        if (n == columns) print(trafficVolume(m)(n))
      }
      println()
    }


  }
}
