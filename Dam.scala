/*Object Dam has 5 floodgates stored in a vector. For the energy output values Dam reads text file (File specified in EnergyData)*/
import scala.io.Source
import scala.util.{Try, Using}

object Dam {

  private var floodgates: Vector[Boolean] = Vector.fill(5)(false)

  def getFloodgates: Vector[Boolean] = floodgates

  def setFloodgate(index: Int, open: Boolean): Either[String, Unit] =
    if (index < 0 || index >= floodgates.length)
      Left(s"Invalid index: $index")
    else {
      floodgates = floodgates.updated(index, open)
      Right(())
    }

  def readData(filePath: String): Either[String, List[String]] =
    Using(Source.fromFile(filePath)) { source =>
      source.getLines().toList
    }.toEither.left.map(_.getMessage)
}
