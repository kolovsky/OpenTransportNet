package OTN_RoadLink.OpenTransportNet.RoadLink

import java.io.{FileOutputStream, OutputStreamWriter, BufferedWriter, FileWriter}

import OpenTransportNet.src.main.scala.Testing_methods

object RoadLinkMain {

  def main(args: Array[String]) {

    val matrix = new RoadLinkTransport("Var_Volume.csv", "Latvia_data_24_5.csv", "inspireid" ,"functional" ,"trafficVol", 1)

    //matrix.loadMatrixVariation("Variation.csv ")
    //matrix.calcRoadLink("RoadLinkAntwerp_firstRow.csv","inspireid" ,"functional" ,"trafficVol" )

    for (k <- 1 to 1000) {
      val variation = matrix.processFeature()

    }

  }
}

