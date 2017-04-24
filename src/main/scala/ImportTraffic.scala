/**
 * Created by kolovsky on 6.1.16.
 */
import java.sql.DriverManager
import java.sql.Connection

import OTN_RoadLink.OpenTransportNet.RoadLink.RoadLinkTransport

object ImportTraffic {
  //jdbc:postgresql://hostname:port/dbname
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
    connect()
    startImport()
    connection.close()
  }
  def connect(): Unit ={
    try{
      Class.forName("org.postgresql.Driver")
      connection = DriverManager.getConnection(url)
    }catch {
      case e => e.printStackTrace
    }

  }
  def startImport(): Unit ={
    val computeObject = new RoadLinkTransport(variationFileName, linkFileName, "FID","roadClass","VOL_DAY_KM",0)

    var i = 0
    while  (computeObject.hasNext()) {
      val trafficVolume = computeObject.processFeature()
      if(trafficVolume != null){
        rowsTrafficImport(trafficVolume)
      }

      i += 1
      println(i)
    }
  }
  def rowsTrafficImport(rows: Array[Array[String]]): Unit ={
    val sql_base = "INSERT INTO transport_network.trafficvolume_plzen_KM(ID, roadLinkID, trafficVolume, trafficVolumeTimePeriod, fromTime, toTime, vehicleType) VALUES \n"
    val groupFactor = 1000
    var i = 0
    var sql = sql_base
    for (row <- rows.slice(1, rows.length)){
      sql += getSQLRow(row) + ",\n"
      i += 1
      if(i % groupFactor == 0){
        sql = sql.substring(0,sql.length - 2) + ";"
        //println(sql)
        val statement = connection.prepareStatement(sql)
        statement.executeUpdate()
        sql = sql_base
        i = 0
      }
    }
    if (i > 0){
      sql = sql.substring(0,sql.length - 2) + ";"
      //println(sql)
      val statement = connection.prepareStatement(sql)
      statement.executeUpdate()
    }


  }
  def getSQLRow(row: Array[String]): String ={
    var out = "("

    out += (row(0).toInt) + ", " //paris 13912256 liberec 21561574 antwerp(smazano) 25181641 birgingnem 36661675 lotissko (smazano) 51798791 antwerp_new 55455200
    out += (row(1).toInt + 1) + ", "
    out += row(2) + ", "
    out += "'" + row(3) + "', "
    out += "'" + row(4) + "', "
    out += "'" + row(5) + "', "
    out += "'" + row(6) + "'"

    out += ")"
    //println(out)
    return out
  }
}
