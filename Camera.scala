//Camera class reads a file through a lazy iteraitor (to emulate data streaming) and passes it to CameraServer for analysis
import scala.io.Source
import scala.util.Try

class Camera(path: String) {

  def stream(): Either[String, Iterator[String]] = //This is a lazy iteraitor
    Try {
      val source = Source.fromFile(path)

      // Wrap iterator so it closes automatically when exhausted
      new Iterator[String] {
        private val iter = source.getLines()

        override def hasNext: Boolean = {
          val hn = iter.hasNext
          if (!hn) source.close()
          hn
        }

        override def next(): String = iter.next()
      }
    }.toEither.left.map(_.getMessage)
}
