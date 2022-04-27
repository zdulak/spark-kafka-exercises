object SparkKafkaUpperApp extends App {
  if (args.length != 2) {
    import scala.sys.exit
    println("You must provide exactly two arguments. They are input and output topics")
    exit(-1)
  }

  val topicIn = args.head
  val topicOut = args.last

  import org.apache.spark.sql.SparkSession
  val spark = SparkSession
    .builder()
    .appName("SparkKafkaUpperApp")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._
  import org.apache.spark.sql.functions._

  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", topicIn)
    .load()
    .select($"value".cast("string"))
    .withColumn("value", upper($"value"))

  val ds = df
    .writeStream
    .format("kafka")
    .option("checkpointLocation", "checkpoint")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("topic", topicOut)
    .start()

    ds.awaitTermination()
}
