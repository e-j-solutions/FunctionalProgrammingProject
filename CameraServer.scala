//Collects data from Cameras and analyses it. If there is an intruder detected, returns an IntruderAlert, same for personnel (PersonnelAlert=> required for a floodgate openning)
sealed trait Alert

case object IntruderAlert extends Alert
case object PersonnelAlert extends Alert
class CameraServer(camera: Camera) {

  def alerts: Either[String, Iterator[Alert]] =
    camera.stream().map { lines =>
      lines.collect {
        case line if line == "Intruder alert!" => IntruderAlert
        case line if line == "Personnel alert!" => PersonnelAlert
      }
    }
  def next(): Either[String, Option[Alert]] =
    alerts.map { it =>
      if (it.hasNext) Some(it.next())
      else None
    }
}
