import breeze.stats.distributions._
import breeze.numerics._
import spire.implicits.cfor
import breeze.stats.distributions._
import scala.math._
import breeze.linalg._
import org.apache.commons.math3.special.Gamma

val epsilon = 1e-8
val xmedian=0.5
val ymedian=0.5
val iter=0
val N = 100*100




//使用Metropolis-Hastings产生符合条件分布(y|x)的y
val burnIn1 = 100
val epsilon1 = 1e-8
def target11(y: Double) = exp(-(0.25*xmedian*xmedian-xmedian*y+y*y)/1.5)/sqrt(1.5*3.1415926)//条件分布(y|x)的密度函数
def proposal11(y: Double) = Gaussian(y, 1)//定义工具分布
def pullASample1(m1: Rand[Double]) = {
  var result1 = 0.0
  cfor(0)(i => i<burnIn1, i => i+1)(i => {
   result1 = m1.draw()
  })
  result1
}




//使用Metropolis-Hastings产生符合条件分布(x|y)的y
val burnIn2 = 100
val epsilon2 = 1e-8
//val a=amedian
def target21(x: Double) = exp(-(0.25*ymedian*ymedian-ymedian*x+x*x)/1.5)/sqrt(1.5*3.1415926)//条件分布(x|y)的密度函数
def proposal21(x: Double) = Gaussian(x, 1)//定义工具分布
def pullASample2(m2: Rand[Double]) = {
  var result2 = 0.0
   cfor(0)(i => i<burnIn2, i => i+1)(i => {
   result2 = m2.draw()
  })
  result2
}



//交替更新对方条件分布(因为此题中只有两个随机变量)的条件变量
for(i <- 0 until N)
{
  val m1 = MarkovChain.metropolisHastings(0.5, proposal11 _)(target11 _)
  val y = pullASample1(m1)//更新target21中的ymedian，即对应于条件分布(x|y)的条件变量y
  println(y)
  val m2 = MarkovChain.metropolisHastings(0.5, proposal21 _)(target21 _)
  val x = pullASample2(m2)//更新target11中的xmedian，即对应于条件分布(y|x)的条件变量x
  println(x)
}