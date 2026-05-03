//Collects data from all energy sources and performs analysis, then returns values
object EnergyData {

  // converters string to double

  private def damToDoubles(dam: Dam.type): Either[String, List[Double]] =
    Dam.readData("dam.txt").map { lines =>
      lines.flatMap(line => scala.util.Try(line.toDouble).toOption)
    }
  private def turbineToDoubles(turbine: WindTurbine): Either[String, List[Double]] =
    turbine.readData().map { lines =>
      lines.flatMap(line => scala.util.Try(line.toDouble).toOption)
    }

  private def sunPanelToDoubles(sunPanel: SunPanel): Either[String, List[Double]] =
    sunPanel.readData().map { lines =>
      lines.flatMap(line => scala.util.Try(line.toDouble).toOption)
    }

  // collect all data

  def collect(dam: Dam.type, turbine: WindTurbine, sunPanel: SunPanel): Either[String, List[Double]] =
    for {
      turbineData <- turbineToDoubles(turbine)
      sunPanelData <- sunPanelToDoubles(sunPanel)
      damData <- damToDoubles(Dam)
    } yield damData ++ turbineData ++ sunPanelData

  // analysis functions

  def mean(data: List[Double]): Double =
    if (data.isEmpty) 0.0 else data.sum / data.length

  def median(data: List[Double]): Double = {
    val sorted = data.sorted
    val n = sorted.length
    if (n == 0) 0.0
    else if (n % 2 == 1) sorted(n / 2)
    else (sorted(n / 2 - 1) + sorted(n / 2)) / 2
  }

  def mode(data: List[Double]): Option[Double] =
    data.groupBy(identity).toList
      .sortBy(-_._2.size)
      .headOption
      .map(_._1)

  def range(data: List[Double]): Double =
    if (data.isEmpty) 0.0 else data.max - data.min

  def midrange(data: List[Double]): Double =
    if (data.isEmpty) 0.0 else (data.max + data.min) / 2

  // running full analysis

  case class Stats(
                    mean: Double,
                    median: Double,
                    mode: Option[Double],
                    range: Double,
                    midrange: Double
                  )

  def analyze(dam: Dam.type, turbine: WindTurbine, sunPanel: SunPanel): Either[String, Stats] =
    collect(dam, turbine, sunPanel).map { data =>
      Stats(
        mean(data),
        median(data),
        mode(data),
        range(data),
        midrange(data)
      )
    }
}
