package OTN_RoadLink.OpenTransportNet.RoadLink

/**
 * Created by Beran on 27.8.2015.
 */
object RoadLinkMain {

  def main (args: Array[String]) {

    val matrix = new RoadLinkTransport()

    matrix.loadMatrixVariation("../RoadLink/Variation.csv ")
    matrix.loadMatrix("../RoadLink/RoadLinkLiberec_new.csv")

  }

}

