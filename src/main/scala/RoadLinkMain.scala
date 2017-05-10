package OTN_RoadLink.OpenTransportNet.RoadLink

import java.io.{FileOutputStream, OutputStreamWriter, BufferedWriter, FileWriter}

//import OpenTransportNet.src.main.scala.Testing_methods
/**
  * Vytvoril Daniel Beran, 2015-2017.
  * 2017-05-10: Uprava pro kvetnova data Plzne s kapacitou.
  */
object RoadLinkMain {

  def main(args: Array[String]) {

    val matrix = new RoadLinkTransport("Harmonogram_kveten.csv", "Var_Volume.csv", "PDI_Kveten_newData.csv", "ROADLINKID" ,"functional" ,"NewVolume", "NewCapacit",1)
    //val matrix = new RoadLinkTransport("Var_Volume.csv", "RoadLink_Birmingham_ukazka.csv", "inspireid" ,"functional" ,"trafficVol", 1)

    //matrix.loadMatrixVariation("Variation.csv ")
    //matrix.calcRoadLink("RoadLinkAntwerp_firstRow.csv","inspireid" ,"functional" ,"trafficVol" )

    for (k <- 1 to 2) {
      val variation = matrix.processFeaturePilsen()
     // println("Hello, "+ args(0) +"!")

    }

  }
}

