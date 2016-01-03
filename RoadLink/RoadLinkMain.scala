package OTN_RoadLink.OpenTransportNet.RoadLink

/**
 * Created by Bery on 27.8.2015.
 */
object RoadLinkMain {

  def main (args: Array[String]) {

    val matrix = new RoadLinkTransport()


    matrix.loadMatrixVariation("Variation.csv")
    matrix.loadMatrix("RoadLink.csv")

    matrix.createTrafficVolume()

    //matrix.printTrafficVolumeMatrix(1, 6)
  }

}

