import scala.io.Source
import scala.util.{Try, Using}

class WindTurbine(private var direction: String, filePath: String) {

  // Get current direction
  def getDirection: String = direction

  // Update direction
  def setDirection(newDirection: String): Unit =
    direction = newDirection

  /**
   * Reads the file and returns all lines when prompted
   */
  def readData(): Either[String, List[String]] =
    Using(Source.fromFile(filePath)) { source =>
      source.getLines().toList
    }.toEither.left.map(_.getMessage)
}