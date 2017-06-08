import breeze.stats.distributions._
import breeze.numerics._
import org.apache.spark.{SparkContext, SparkConf}
import spire.implicits.cfor


/**
  * Created by Still on 2017/4/14.
  */
object MCMC_example {
  def main(args:Array[String]) {
    val conf = new SparkConf().setAppName("MCMC_example").setMaster("local")
    val sc = new SparkContext(conf)
    val burnIn = 1024*1024
    val Epsilon = 1e-8
    def target(x:Double) = 2 * log1p(1+Epsilon-x) + 3*log1p(x*x*x+Epsilon)
    def proposal(x:Double) = Gaussian(x,1)
    def pullASample(m:Rand[Double]) = {
      var Result=0.0
      cfor(0)(i=>i<burnIn, i=>i+1)(i => {
        Result=m.draw()
      })
      Result
    }
  val m=MarkovChain.metropolisHastings(0.5, proposal)(target)
  print(pullASample(m))
  }
}
