object SparkStreamKafkaApp extends App {
  val topic = args.head

  import org.apache.spark.sql.SparkSession
  val spark = SparkSession
    .builder()
    .appName("SparkStreamKafkaApp")
    .master("local[*]")
    .getOrCreate()

  import org.apache.spark.sql.types.StringType
  import spark.implicits._
//  import org.apache.spark.sql.functions._

  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", topic)
    .load()
    .select('value.cast(StringType))

    df
    .writeStream
    .format("console")
    .start
//  Necessary line. Without it we have error.
//  awaitTermination blocks closing threads which are needed by parts of application communicating with kafka
    .awaitTermination()
}
