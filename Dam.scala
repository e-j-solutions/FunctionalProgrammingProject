import scala.io.Source
import scala.util.{Try, Using}

object Dam {

  // Always 5 floodgates
  private var floodgates: Vector[Boolean] = Vector.fill(5)(false)

  // Get current state
  def getFloodgates: Vector[Boolean] = floodgates

  // Set a specific gate
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