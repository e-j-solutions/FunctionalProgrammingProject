import scala.io.Source
import scala.util.{Try, Using}


class SunPanel(private var direction: String, filePath: String) {
  
  def getDirection: String = direction
  
  def setDirection(newDirection: String): Unit =
    direction = newDirection


  def readData(): Either[String, List[String]] = {
    Using(Source.fromFile(filePath)) { source =>
      source.getLines().toList
    }.toEither.left.map(_.getMessage)
  }
}