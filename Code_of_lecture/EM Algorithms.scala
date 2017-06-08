/**
  * Created by Still on 2017/4/7.
  */

import breeze.numerics.pow
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import java.util.concurrent.ThreadLocalRandom


object EM1 {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("EMofMixedGaussian").setMaster("local")
    val sc = new SparkContext(conf)
    //generate 10000 samples from mixed gaussian: 0.2N(5,4)+0.8N(0,1)
    val N=10000
    val MU1 = 5.0
    val Sig1=2.0
    val MU2=0.0
    val Sig2=1.0
    val P=0.2
    val NumOfSlaves = 5
    //generating data
    val random=ThreadLocalRandom.current()
    val data=Array.ofDim[Double](N)  //let data to be an empty array of Double
    //20 pct of random will generate N(5,4) data and others will generate N(0,1) data
    for(i<-0 until N) {
      data(i) = if(random.nextDouble<=P)
        MU1 + Sig1*random.nextGaussian()
      else MU2 + Sig2*random.nextGaussian()
    }
    var ParData = sc.parallelize(data,NumOfSlaves)   //turn data into RDD
    val ParDataStr = ParData.map("%.4f" format _)  //format data as 4 digits of str
    ParData = ParDataStr.map(_.toDouble)  //map data as double
    val InitialP = 0.5  //set initial value of P as 0.5
    var Nk=N.toDouble*InitialP
    var EstMu1 = ParData.reduce((x,y)=>x+y)/N.toDouble  //initial estimation of mu1 has been set as mean
    var EstSig1 = math.sqrt(ParData.map(x=>x*x).reduce((x,y)=>x+y)/N.toDouble-EstMu1*EstMu1)
    var EstMu2=EstMu1-1.0
    var EstSig2=EstSig1
    var Diff = 0.0
    var OldEstMu1 = 0.0
    var OldEstMu2 = 0.0
    var OldEstSig1 = 0.0
    var OldEstSig2 = 0.0
    var ii = 0
    //EM Algorithms loop
    val eps=0.001
    do {
      ii+=1
      OldEstMu1 = EstMu1
      OldEstMu2 = EstMu2
      OldEstSig1 = EstSig1
      OldEstSig2 = EstSig2
      val SufficientStatistics=ParData.map(line=>{
        val x1 = - math.pow((line-EstMu2)/EstSig2,2)/2.0
        val x2 = - math.pow((line-EstMu1)/EstSig1,2)/2.0
        val gamma = Nk*EstSig2/(Nk*EstSig2+(N-Nk)*EstSig1*math.exp(x1-x2))
        (line,gamma,line*gamma,line*line*gamma,1-gamma,line*(1-gamma),line*line*(1-gamma))
      })
      //Distributed
      val Results = SufficientStatistics.reduce((x,y)=>(x._1+y._1,x._2+y._2,x._3+y._3,x._4+y._4,x._5+y._5,x._6+y._6,x._7+y._7))

      Nk=Results._2
      EstMu1=Results._3/Nk
      EstSig1=math.sqrt(Results._4/Nk-EstMu1*EstMu1)
      EstMu2=Results._6/Results._5
      EstSig2=math.sqrt(Results._7/Results._5-EstMu2*EstMu2)
      Diff=math.abs(EstMu1-OldEstMu1)+math.abs(EstMu2-OldEstMu2)
      Diff+= math.abs(EstSig1-OldEstSig1)+math.abs(EstSig2-OldEstSig2)
    }while(Diff>eps)
    println(Nk,EstMu1,EstSig1,EstMu2,EstSig2)
  }
}
