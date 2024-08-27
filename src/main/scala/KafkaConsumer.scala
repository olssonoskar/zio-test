import zio.kafka.consumer.{Consumer, ConsumerSettings, Subscription}
import zio.kafka.serde.Serde
import zio.*
import zio.stream.ZStream

import scala.collection.mutable

object KafkaConsumer {

  private var messageNum = 0
  val db: mutable.Map[Int, String] = collection.mutable.HashMap()

  val consumer: ZStream[Consumer, Throwable, Nothing] =
    Consumer.plainStream(Subscription.topics("testing"), Serde.int, Serde.string)
      .tap(r => zio.Console.printLine(r.value))
      .map(record => {
        db.put(messageNum, record.value)  //
        messageNum += 1
        record
      })
      .map(_.offset)
      .aggregateAsync(Consumer.offsetBatches)   // Batches commits to reduce overhead
      .mapZIO(_.commit)                         // Commit max kept from Aggregation?
      .drain

  val consumerLayer: ZLayer[Any, Throwable, Consumer] =
    ZLayer.scoped(
      Consumer.make(
        ConsumerSettings(List("localhost:29092")).withGroupId("new-app")
      )
    )

}
