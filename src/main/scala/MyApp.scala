import zio.http.*
import zio.json.*
import zio.kafka.consumer.Consumer
import zio.{ZIO, ZIOAppDefault, ZLayer}

object MyApp extends ZIOAppDefault {

  private val routes = Routes(
    Method.GET / Root -> handler(Response.json("{\"Hello\": \"There\"}")),
    Method.GET / "lookup" ->
      handler { (req: Request) =>
        ListService.getCategories(req)
          .fold(e => Response.text(s"System failure: ${e.getCause}"), s => Response.text(s))
      },
    Method.GET / "mult" / string("first") / string("second") ->
      handler { (first: String, second: String, _: Request) =>
        val ret = Multiply(List(first, second), first.toInt * second.toInt)
        Response.json(ret.toJsonPretty)
      },
    Method.GET / "block" -> handler(ListService.blocking().fold(e => Response.text(s"System failure: ${e.getCause}"), s => Response.text(s))),
    Method.GET / "kafka" / string("id") -> handler { (id: String, _: Request) =>
      ZIO.fromOption(KafkaConsumer.db.get(id.toInt))
        .fold(e => Response.text(s"Nothing here"), s => Response.text(s))
    }
  )

  def run: ZIO[Any, Throwable, Unit] = for {
    _ <- Server.serve(routes).provide(Server.default).fork                    // Running Http responder in other thread, works but probably dirty
    _ <- KafkaConsumer.consumer.runDrain.provide(KafkaConsumer.consumerLayer) // Consume messages from topic
  } yield ()

  private case class Multiply(factors: List[String], product: Int)

  private object Multiply {
    implicit val encoder: JsonEncoder[Multiply] = DeriveJsonEncoder.gen[Multiply]
  }

}
