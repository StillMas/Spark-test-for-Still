/**
  * Created by Still on 2017/3/31.
  */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import java.util.concurrent.ThreadLocalRandom

object SampleFromRegression {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)
    val RowSize=sc.broadcast(200)
    val ColumnSize=sc.broadcast(5)
    val RowLength = sc.broadcast(2)
    val ColumnLength = sc.broadcast(1000)
    val NonZeroLength = 10
    val p = ColumnSize.value*ColumnLength.value
    val beta = (1 to p).map(_.toDouble).toArray[Double]
      .map(i =>{if(i<NonZeroLength+1) 2.0 else 0.0})
    val MyBeta = sc.broadcast(beta)
    var sigma = 1.0
    val Sigma = sc.broadcast(sigma)
    var indices = 0 until RowLength.value
    var ParallelIndices = sc.parallelize(indices, indices.length)
    val lines = ParallelIndices.map(s => {
      val r = ThreadLocalRandom.current
      def rn(n: Int) = (0 until n).map(x => r.nextGaussian).toArray[Double]
      val beta = MyBeta.value
      val sigma = Sigma.value
      val rowsize = RowSize.value
      val columnsize = ColumnSize.value
      val columnlength = ColumnLength.value
      var lines = new Array[String](rowsize)
      val p = columnsize*columnlength
      for (i <- 0 until rowsize)
      {
        var line = "";
        var y = 0.0;
        for(j <- 0 until columnlength)
        {
          var x = rn(columnsize)
          for(k <- 0 until columnsize) y += beta(j*columnsize+k)*x(k)
          var segment = x.map("%.4f" format _).reduce(_+" "+_)
          line = line + "," + segment
        }
        y += sigma*r.nextGaussian
        lines(i) = "%.4f".format(y) + line + "\n"
      }
      lines.reduce(_ + _)})
      //pnX¨óHDFSûß
      lines.foreach(println)
      }

}
