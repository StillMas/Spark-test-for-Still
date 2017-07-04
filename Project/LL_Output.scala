import java.util.concurrent.ThreadLocalRandom
import breeze.numerics._
import org.apache.spark.{SparkContext, SparkConf}
import breeze.stats.distributions._
import breeze.stats._

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Still on 2017/5/26.
  */
object Students_t_EM extends Serializable {
  def main(args:Array[String]): Unit = {
    val Conf=new SparkConf().setAppName("Students_t_EM").setMaster("local")
    val sc=new SparkContext(Conf)

    //Generate Random data of student's t distribution
    val N=100000
    val mu= -1.0
    val Sigma=1.0
    val v=2  //degree of freedom
    /**
    Choice1:Generate From StudentsT distribution
    @transient val RanStudentsT=new StudentsT(v)
    @transient val Random=ThreadLocalRandom.current()
    @transient val RandNum=RanStudentsT.sample(N).map(i=>i+mu)
    */

    //Choice2:Generate From Normal and Gamma Distribution
    @transient val RandChiSq=new ChiSquared(v);
    @transient val Random=ThreadLocalRandom.current()
    @transient val RandNum=Array.ofDim[Double](N)
    for (i<-1 until N) {
      RandNum(i)=Random.nextGaussian()*Sigma/math.sqrt(RandChiSq.draw()/v)+mu}

    val Stats=sc.parallelize(Array(mean(RandNum),variance(RandNum)))
      Stats.foreach(println)

    //Using EM Algorithm to make Approximation
    val ParData=sc.parallelize(RandNum)
    var Estmu=ParData.reduce((x,y)=>x+y)/N.toDouble
    var Estv=2+2/((ParData.map(x=>x*x).reduce((x,y)=>x+y)/N.toDouble-Estmu*Estmu)-1)
    var EstSigma=1.0
    var ii=1;var DiffLL=0.0;var Difftheta=0.0;val Stop=0.001
    var OldEstmu=0.0;var OldEstv=0.0;var OldEstSigma=0.0;var EstLLReduce=0.0;var OldLL=0.0
	
	//var a Recorder as an arraybuffer, that could append 1 every time
    var LLRecorder=new ArrayBuffer[Double]()
    def LogLikelihood(v:Double,lambda:Double,yi:Double,Sigma:Double,Mu:Double): Double={
      @transient val Compute= (-log(2*math.Pi)/2)-(log(Sigma*Sigma)/2)-log(exp(lgamma(v/2)))-(v/2)*log(2/v)-((v+3)/2)*log(lambda)-(1/lambda)*(((yi-mu)*(yi-mu)/(2*Sigma*Sigma))+v/2)
      Compute}
    do {
      ii+=1
      OldEstmu=Estmu
      OldEstv=Estv
      OldEstSigma=EstSigma
      OldLL=EstLLReduce
      //loop for Maximize Expectation of Log Likelihood of mu, Sigma and v
      val SS=ParData.map(line=>{
        //tau1 and tau2 are two important values in computing
        val tau1= -digamma((OldEstv+1)/2) - log(2*OldEstSigma*OldEstSigma/((line-OldEstmu)*(line-OldEstmu)+OldEstv*OldEstSigma*OldEstSigma))
        val tau2= (OldEstv+1)*OldEstSigma*OldEstSigma/((line-OldEstmu)*(line-OldEstmu)+OldEstv*OldEstSigma*OldEstSigma)
        //computing data for reducing Mu and Sigma
        val ReduceMu1=tau2*line;val ReduceMu2=tau2
        val ReduceSigmaSq=tau2*(line-OldEstmu)*(line-OldEstmu)/N.toDouble
        val lambda=OldEstv/(OldEstv-2);val LL=LogLikelihood(OldEstv,lambda,line,OldEstSigma,OldEstmu)
        (ReduceMu1,ReduceMu2,ReduceSigmaSq,tau1,LL)
      })
      val Results=SS.reduce((x,y)=>(x._1+y._1,x._2+y._2,x._3+y._3,x._4+y._4,x._5+y._5))
      Estmu=Results._1/Results._2
      EstSigma=math.sqrt(Results._3)
      EstLLReduce=Results._5
      LLRecorder+=EstLLReduce

      //Solution for v: Method1, Using zero point method to compute v
      val V_Constant=(Results._2+Results._4)/N.toDouble
      var Upper=5*Estv;var Lower=0.0
      var Middle=(Upper+Lower)/2;var OldMiddle=0.0
      //define the Function for Solution
      def v_Solution(Value:Double): Double={
        val Output=digamma(Value/2)-log(Value/2)-1+V_Constant
        Output}
      val epsilon=0.01;var Iter=0  //Threshold for Solution
      do {
        Iter+=1
        OldMiddle=Middle
        if (v_Solution(Upper)*v_Solution(OldMiddle)<=0) {Lower=OldMiddle}else {Upper=OldMiddle}
        Middle=(Upper+Lower)/2
      } while((Upper-Lower)>epsilon)
      Estv=Middle  //Get the final Middle as the Solution of v
      //Solution for v: Method2, Using Taylor equation and Newton method
      //Diff=math.abs(Estmu-OldEstmu)+math.abs(EstSigma-OldEstSigma)+math.abs(Estv-OldEstv)
      Difftheta=math.abs(OldEstmu-Estmu)+math.abs(OldEstSigma-EstSigma)+math.abs(OldEstv-Estv)
      DiffLL=math.abs(EstLLReduce-OldLL)
      println("Estmu,EstSigma,Estv,ii,Iter,Upper,Lower,V_Constant")
      println(Estmu,EstSigma,Estv,ii,Iter,Upper,Lower,V_Constant)
      println("Results",Results)
      SS.take(5).foreach(println)}while((math.min(Difftheta,DiffLL)>Stop)&&(ii<1000))
      //loop for Maximize Expectation of Log Likelihood of v
    val SigmaFinal=EstSigma*EstSigma*Estv
    println(LLRecorder)
	
	//change LLRecorder(ArrayBuffer) into RDD, then use saveAsTextFile to output
    val LLCollect=sc.parallelize(LLRecorder)
    LLCollect.saveAsTextFile("C:///data/spark/v_2")
    println("final","mu=",Estmu,"Sigma=",EstSigma,"v=",Estv,"times",ii)
  }
}