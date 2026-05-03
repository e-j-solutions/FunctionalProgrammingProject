/*Main body of the program. Initialises all classes. It has an interactive user control panel, where you can choose to perform some operations. 
You can change turbine directions, change sun panel angle or open/close dam floodgates. Whenever a floodgate is opened, system checks camera, so "humans" wouldn't
be harmed. It performs a camera check every 10 minutes regardless, if the user interacts or not, as long as the program is still running. Files
for running this program are provided.
import java.util.concurrent.{Executors, TimeUnit}
import scala.io.StdIn

object MainServer extends App {

  private val camera = new Camera("camera.txt")
  private val cameraServer = new CameraServer(camera)
  private val turbine = new WindTurbine("North", "turbine.txt")
  private val sunPanel = new SunPanel("0°", "sunpanel.txt")

  // scheduler (runs every 10 minutes)

  private val scheduler = Executors.newScheduledThreadPool(1)

  private val task: Runnable = () => {
    println("\n[Auto Check] Running camera alert scan...")

    cameraServer.alerts match {
      case Left(err) =>
        println(s"[Auto Check] Error: $err")

      case Right(alerts) =>
        alerts.foreach{
          case IntruderAlert =>
            println(s"[Auto Check] ALERT: $alerts")
          case PersonnelAlert =>
            ()}
    }
  }

  private def controlFloodgate(index: Int, open: Boolean): Unit = {

    if (!open) {
      // Closing is always allowed
      Dam.setFloodgate(index, open) match {
        case Left(err) => println(s"Error: $err")
        case Right(_) => println(s"Floodgate $index closed")
      }

    } else {
      // Opening requires safety check
      println("Checking camera before opening floodgate...")

      cameraServer.alerts match {
        case Left(err) =>
          println(s"Error accessing camera: $err")

        case Right(alerts) =>
          val peopleDetected = alerts.take(1).nonEmpty // consumes stream

          if (peopleDetected) {
            println(" Cannot open floodgate: People detected!")
          } else {
            Dam.setFloodgate(index, open) match {
              case Left(err) =>
                println(s"Error: $err")

              case Right(_) =>
                println(s" Floodgate $index opened safely")
            }
          }
      }
    }
  }
  

  // Run every 10 minutes (600 seconds)
  scheduler.scheduleAtFixedRate(task, 0, 10, TimeUnit.MINUTES)

  // user command loop

  println("Server started. Commands:")
  println("1 -> Check camera alerts now")
  println("2 -> Show energy analysis")
  println("3 -> Open floodgate")
  println("4 -> Close floodgate")
  println("5 -> Turn the wind turbines")
  println("6 -> Adjust the sun panels")
  println("0 -> Quit")

  private var running = true

  while (running) {
    StdIn.readLine("> ") match {

      case "1" =>
        println("[Manual] Checking camera alerts...")

        cameraServer.alerts match {
          case Left(err) =>
            println(s"Error: $err")

          case Right(alerts) =>
            val found = alerts.toList
            if (found.isEmpty) println("No alerts detected")
            else found.foreach(a => println(s"ALERT: $a"))
        }

      case "2" =>
        println("[Manual] Running energy analysis...")

        EnergyData.analyze(Dam, turbine, sunPanel) match {
          case Left(err) =>
            println(s"Error: $err")

          case Right(stats) =>
            println(f"Mean: ${stats.mean}%.2f")
            println(f"Median: ${stats.median}%.2f")
            println(s"Mode: ${stats.mode}")
            println(f"Range: ${stats.range}%.2f")
            println(f"Midrange: ${stats.midrange}%.2f")
        }
      case "3" =>
        println("Enter floodgate index (0-4):")
        val idx = StdIn.readLine().toInt
        controlFloodgate(idx, open = true)

      case "4" =>
        println("Enter floodgate index (0-4):")
        val idx = StdIn.readLine().toInt
        controlFloodgate(idx, open = false)
      case "5" =>
        println("Enter new turbine direction:")
        val dir = StdIn.readLine()
        turbine.setDirection(dir)
        println(s"Turbine direction set to $dir")
      case "6" =>
        println("Enter new sun panel angle:")
        val dir = StdIn.readLine()
        sunPanel.setDirection(dir)
        println(s"Sun panel adjusted to $dir °")
      case "0" =>
        println("Shutting down...")
        running = false
        scheduler.shutdown()

      case _ =>
        println("Unknown command")
    }
  }
}
