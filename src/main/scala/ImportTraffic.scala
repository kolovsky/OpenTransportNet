/**
 * Created by kolovsky on 6.1.16.
 */
import java.sql.DriverManager
import java.sql.Connection

import OTN_RoadLink.OpenTransportNet.RoadLink.RoadLinkTransport

object ImportTraffic {

  var linkFileName: String = null
  var variationFileName: String = null
  var connection: Connection = null
  var username: String = null
  var password: String = null
  var url: String = null

  def main(args: Array[String]) {
    linkFileName = args(0)
    variationFileName = args(1)
    url = args(2)
    username = args(3)
    password = args(4)
    //connect()
    startImport()
    //connection.close()
  }
  def connect(): Unit ={
    try{
      Class.forName("org.postgresql.Driver")
      connection = DriverManager.getConnection(url, username, password)
    }catch {
      case e => e.printStackTrace
    }

  }
  def startImport(): Unit ={
    val computeObject = new RoadLinkTransport()
    computeObject.loadMatrixVariation(variationFileName)

    val source = io.Source.fromFile(linkFileName)

    for  (linie <- source.getLines) {
      val radek = linie.split(";").map(_.trim).map(_.replaceFirst("," , "."))

      val trafficVolume = computeObject.processFeature(radek)
      rowTrafficImport(trafficVolume)


    }
    source.close()
  }
  def rowTrafficImport(rows: Array[Array[String]]): Unit ={
    val groupFactor = 10
    var i = 0
    var sql = "INSERT INTO road_network.traffic(ID, inspireID, trafficVolume, trafficVolumeTimePeriod, fromTime, toTime, vehicleType) VALUES \n"
    for (row <- rows.slice(0, rows.length - 1)){
      sql += getSQLRow(row) + ",\n"
      i += 1
      if(i == groupFactor){
        println(sql)
        sql = "INSERT INTO road_network.traffic(ID, inspireID, trafficVolume, trafficVolumeTimePeriod, fromTime, toTime, vehicleType) VALUES \n"
        i = 0
      }
    }
    /*val sql = "INSERT INTO ..."
    val statement = connection.prepareStatement(sql)
    statement.setString("aa")
    statement.executeUpdate()*/
  }
  def getSQLRow(row: Array[String]): String ={
    var out = "("

    out += row(0) + ", "
    out += row(1) + ", "
    out += row(2) + ", "
    out += "'" + row(3) + "', "
    out += "'" + row(4) + "', "
    out += "'" + row(5) + "', "
    out += "'" + row(6) + "'"

    out += ")"

    return out
  }

}
