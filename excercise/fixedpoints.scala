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