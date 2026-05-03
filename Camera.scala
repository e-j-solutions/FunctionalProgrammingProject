import scala.io.Source
import scala.util.Try

class Camera(path: String) {

  /**
   * Returns a lazy iterator over lines.
   * The file is opened and safely closed after consumption.
   */
  def stream(): Either[String, Iterator[String]] =
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