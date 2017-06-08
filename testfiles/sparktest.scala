import org.apache.spark.{SparkContext, SparkConf}

object Boostrap1 {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setAppName("Bootstrap1").setMaster("local")
    val sc=new SparkContext(conf)
  }
}
