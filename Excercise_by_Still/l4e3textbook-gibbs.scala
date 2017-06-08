import breeze.stats.distributions._
import breeze.numerics._
import spire.implicits.cfor
import breeze.stats.distributions._
import scala.math._
import breeze.linalg._
import org.apache.commons.math3.special.Gamma

val epsilon = 1e-8
val amedian=0.5
val bmedian=0.5
val iter=0
val N = 100*100


//使用Metropolis-Hastings产生符合条件分布(y|x)的y
val burnIn1 = 100
val epsilon1 = 1e-8
def target1(y: Double):Double= math.log(bmedian+epsilon)-math.log(Gamma.gamma((bmedian*bmedian+1)/2))+math.log(2)-math.log(1+bmedian*bmedian)-2*(bmedian+epsilon)/(1+bmedian*bmedian)//条件分布(y|x)的密度函数
def proposal1(y: Double) = Gaussian(y, 1)//定义工具分布
def pullASample1(m1: Rand[Double]) = {
  var result1 = 0.0
  cfor(0)(i => i<burnIn1, i => i+1)(i => {
   result1 = m1.draw()
  })
  result1
}
//使用Metropolis-Hastings产生符合条件分布(x|y)的x
val burnIn2 = 100
val epsilon2 = 1e-8
//val a=amedian
def target2(x: Double):Double= (1/2)*math.log((amedian+1+epsilon)/(2*3.1415926))-(amedian+1)*(x-amedian/(1+amedian))*(x-amedian/(1+amedian))/2//条件分布(x|y)的密度函数
def proposal2(x: Double) = Gaussian(x, 1)//定义工具分布
def pullASample2(m2: Rand[Double]) = {
   var result2 = 0.0
   cfor(0)(i => i<burnIn2, i => i+1)(i => {
   result2 = m2.draw()
  })
  result2
}

//交替更新对方条件分布(因为此题中只有两个随机变量)的条件变量
for(i <- 0 until N) {
  val m1:Rand[Double]= MarkovChain.metropolisHastings(0.5, proposal1)(target1)
  val amedian = pullASample1(m1)//更新target21中的amedian，即对应于条件分布(x|y)的条件变量y
  def target2(x: Double) = (1/2)*math.log((amedian+1+epsilon)/(2*3.1415926))-(amedian+1)*(x-amedian/(1+amedian))*(x-amedian/(1+amedian))/2
  
  val m2:Rand[Double]= MarkovChain.metropolisHastings(0.5, proposal2)(target2)
  val bmedian = pullASample2(m2)//更新target21中的bmedian，即对应于条件分布(y|x)的条件变量x
  def target1(y: Double) = math.log(bmedian+epsilon)-math.log(Gamma.gamma((bmedian*bmedian+1)/2))+math.log(2)-math.log(1+bmedian*bmedian)-2*(bmedian+epsilon)/(1+bmedian*bmedian)//条件分布(y|x)的密度函数
  println(bmedian)}