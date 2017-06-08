import breeze.numerics._
import breeze.linalg._
import scala.math._
val n = 100
var a = 0.1
var b = 2.8
var x = 3.0
var i = 0
var eps = 0.0
var median=0.0
var x1=0.0
var x2=0.0
var y=0.0
var y1=0.0
//double cut
do
{
    x1=a
    x2=b
    median=(x1+x2)/2
    y = median-1
    y1 = x1-1
    if((y<0&&y1<0)||(y>0&&y1>0))
    a=median
    else
    b=median
    eps=math.abs(y)
    println(eps)
}while(eps>0.01)

//newton
do
{
 var xmedian=x
 x=xmedian-((xmedian*xmedian+xmedian-xmedian*xmedian*math.log(xmedian))*(1+xmedian)*(1+xmedian))/((1+xmedian)*(1+xmedian)+2*(xmedian*xmedian)+xmedian-xmedian*xmedian*math.log(xmedian))
 var eps=math.abs(x-xmedian)
 i=i+1
 println(i)
 println(eps)
}while(eps>0.0000000000001)

//fixed point
object dataguruone {
  def abs(a:Double)={
    if(a<0) -a else a
  }
  var xouble=1
  def sqrt(y:Double):Double={
    if(abs(x*x-y)>1e-8) {x=(x+y/x)/2;sqrt(y);} else x
  }

  def main(args:Array[String]){
  println(sqrt(2))
  }
}










