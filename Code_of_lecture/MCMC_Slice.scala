/**
  * Created by Still on 2017/4/21.
  */

import breeze.numerics._
import breeze.stats.distributions._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import spire.implicits.cfor

object Slice_Example {
  def main(args:Array[String]) {
    val conf = new SparkConf().setAppName("Slice_example").setMaster("local")
    val sc = new SparkContext(conf)

    val burnIn=1024*1024
    val epsilon=1e-8
    def target(x:Double)=2*log1p(1+epsilon-x/10) + 3*log1p(x*x*x/1000+epsilon)-log(9.085)

    def support(x:Double)=if(x>0.0 && x<20.0) true else false

    def pullASample(m:Rand[Double])={
      var result=0.0
      cfor(0)(i=>i<burnIn,i=>i+1)(i=>{
        result=m.draw()
      })
      result
    }
    val m=MarkovChain.slice(0.5,target _,support _)
    println(pullASample(m))
  }
}
