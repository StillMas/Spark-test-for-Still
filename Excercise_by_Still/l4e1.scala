import breeze.stats.distributions._
import breeze.numerics._
import spire.implicits.cfor
import scala.math._
import breeze.linalg._
import java.util.concurrent.ThreadLocalRandom
val burnIn = 10000
val epsilon = 1e-8
val N=100
var part=DenseVector.zeros[Double](N)
def target1(x: Double) = exp(-(x-7)*(x-7))/(0.5*math.sqrt(2*3.1415926))
//用来定义目标分布概率密度函数的log变换，可以是一个和pdf成正比的函数；
//epsilon是用来防止出现0或者1的极端情况，log1p(x)=log(x+1)
def proposal1(x: Double) = Gaussian(x, 1)//定义工具分布
def pullASample1(m1: Rand[Double]) = {
var result1 = 0.0
cfor(0)(i => i<burnIn, i => i+1)(i => {
result1 = m1.draw()
})
result1
}//输出一个MCMC链稳定之后产生的随机数


def target2(x: Double) = exp(-(x-10)*(x-10))/(0.5*math.sqrt(2*3.1415926))
//用来定义目标分布概率密度函数的log变换，可以是一个和pdf成正比的函数；
//epsilon是用来防止出现0或者1的极端情况，log1p(x)=log(x+1)
def proposal2(x: Double) = Gaussian(x, 1)//定义工具分布
def pullASample2(m2: Rand[Double]) = {
var result2 = 0.0
cfor(0)(i => i<burnIn, i => i+1)(i => {
result2 = m2.draw()
})
result2
}//输出一个MCMC链稳定之后产生的随机数

val i=0
for(i <- 0 until 99)
{
val m1 = MarkovChain.metropolisHastings(0.5, proposal1 _)(target1 _)
val a= pullASample2(m1)
val m2 = MarkovChain.metropolisHastings(0.5, proposal2 _)(target2 _)
val b =pullASample2(m2)
val median=a+b
part(i)=median
}



//设置基本参数
val N = 10000
val MU1 = 7.0
val Sig1 = 0.5
val MU2 = 10.0
val Sig2 = 0.5
val P = 0.2
val NumOfSlaves = 5
//产生数据

var ParData = sc.parallelize (part.toArray, NumOfSlaves)
val ParDataStr = ParData.map("%.4f" format _)
ParData = ParDataStr.map(_.toDouble)
//设置估计的初始值
val InitialP = 0.5
var Nk = N.toDouble*InitialP
var EstMu1 = ParData.reduce((x,y)=>x+y)/N.toDouble
var EstSig1 = math.sqrt(ParData.map(x=>x*x)
.reduce((x,y)=>x+y)/N.toDouble - EstMu1*EstMu1)
var EstMu2 = EstMu1-1.0
var EstSig2 = EstSig1

var Diff = 0.0
var OldEstMu1 = 0.0
var OldEstMu2 = 0.0
var OldEstSig1 = 0.0
var OldEstSig2 = 0.0
var ii = 0
//EM算法迭代
do
{
ii += 1
OldEstMu1 = EstMu1
OldEstMu2 = EstMu2
OldEstSig1 = EstSig1
OldEstSig2 = EstSig2
/*分布式计算充分统计量所需的值，包括原始数据、条件概率、
所有充分统计量对应于
每个观察值的数量*/
var SufficientStatistics = ParData.map(line =>{
val x1 = -math.pow((line-EstMu2)/EstSig2,2)/2.0
val x2 = -math.pow((line-EstMu1)/EstSig1,2)/2.0
val gamma = Nk*EstSig2/(Nk*EstSig2+(N-Nk)*EstSig1*math.exp(x1-x2))
(line,gamma,line*gamma,line*line*gamma,1-gamma,
line*(1-gamma),line*line*(1-gamma))
})
//分布式计算的数量加以汇总得到期望充分统计量
val Results = SufficientStatistics.reduce((x,y)=>
(x._1+y._1,x._2+y._2,x._3+y._3,
x._4+y._4,x._5+y._5,x._6+y._6,x._7+y._7))
Nk = Results._2
EstMu1 = Results._3/Nk
EstSig1 = math.sqrt(Results._4/Nk - EstMu1*EstMu1)
EstMu2 = Results._6/Results._5
EstSig2 = math.sqrt(Results._7/Results._5 - EstMu2*EstMu2)
记录参数估计更新的变化以判断是否终止循环
Diff = math.abs(EstMu1-OldEstMu1) + math.abs(EstMu2-OldEstMu2)
Diff += math.abs(EstSig1-OldEstSig1) + math.abs(EstSig2-OldEstSig2)
}while(Diff > 0.5)



val N = 10000
val MU1 = 5.0
val Sig1 = 2.0
val MU2 = 0.0
val Sig2 = 1.0
var P = 0.2
val NumOfSlaves = 5
val random = ThreadLocalRandom.current
val data = Array.ofDim[Double](N)
for(i <- 0 until N)
val sign=random.nextDouble<=P
data(i) = if(sign=True) MU1 + Sig1*random.nextGaussian
else MU2 + Sig2*random.nextGaussian
/*数据产生后先转化成RDD格式，然后将其转化为文本格式，再转化成Double型，
这是为了防止直接产生的随机数无法序列化（serialization）带来的问题*/
var ParData = sc.parallelize (data, NumOfSlaves)
val ParDataStr = ParData.map("%.4f" format _)
ParData = ParDataStr.map(_.toDouble)
