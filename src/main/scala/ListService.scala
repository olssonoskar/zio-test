import zio.{Task, ZIO}
import zio.http.Request

object ListService {

  private val Movies = List("Alien", "Aliens", "Alien3", "Resurrection", "Prometheus", "Covenant")
  private val Music = List("Bring me the Horizon", "Sleep Token", "You Me At Six")

  def getCategories(request: Request): Task[String] = {
    val category = request.queryParam("type")
    val id = request.queryParam("id").getOrElse("-1").toInt
    ZIO.fromEither(
      category match
        case Some("movie") => Right(Movies(id))
        case Some("artist") => Right(Music(id))
        case _ => Left(IllegalArgumentException("Unknown Category"))
    )
  }

  // Runs on a thread pool meant for blocking workloads
  def blocking(): Task[String] = {
    ZIO.attemptBlocking {
      Thread.sleep(3000L)
      "Phew, tough work..."
    }
  }

}
