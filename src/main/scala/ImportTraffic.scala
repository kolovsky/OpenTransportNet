/**
 * Created by kolovsky on 6.1.16.
 */
import java.sql.DriverManager
import java.sql.Connection

import OTN_RoadLink.OpenTransportNet.RoadLink.RoadLinkTransport

object ImportTraffic {/*
  //jdbc:postgresql://hostname:port/dbname
  //jdbc:postgresql://gis.lesprojekt.cz:5432/osm2po
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
    connect()
    startImport()
    connection.close()
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

    var i = 0
    for  (linie <- source.getLines) {
        val radek = linie.split(";").map(_.trim).map(_.replaceFirst("," , "."))
      /*if (i == 0){
        val trafficVolume = computeObject.processFeature(radek)
        //println("ssasas: "+ trafficVolume.length)
        rowsTrafficImport(trafficVolume)
        i = 1
      }*/
      val trafficVolume = computeObject.processFeature(radek)
      if(trafficVolume != null){
        rowsTrafficImport(trafficVolume)
      }

      i += 1
      println(i)


    }

    source.close()
  }
  def rowsTrafficImport(rows: Array[Array[String]]): Unit ={
    val sql_base = "INSERT INTO transport_network.trafficvolume(ID, roadLinkID, trafficVolume, trafficVolumeTimePeriod, fromTime, toTime, vehicleType) VALUES \n"
    val groupFactor = 100
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

    out += (row(0).toInt + 25181641) + ", " //paris 13912256,liberec 21561574 antwerp
    out += row(1) + ", "
    out += row(2) + ", "
    out += "'" + row(3) + "', "
    out += "'" + row(4) + "', "
    out += "'" + row(5) + "', "
    out += "'" + row(6) + "'"

    out += ")"
    //println(out)
    return out
  }
*/
}
