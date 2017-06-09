object dataguruone {
  def abs(a:Double)={
    if(a<0) -a else a
  }
  var x:Double=1
  def sqrt(y:Double):Double={
    if(abs((math.log(x)/(1+x))-y)>1e-8) {
	x=x+((x*x+x-x*x*log(x))*(1+x)*(1+x))/(log(x)*x*(1+x)*(1+x)-(3*x*x+4*x+1)*(x+1-x*log(x)));sqrt(y);
	}else x
  }


  def main(args:Array[String]){
  println(sqrt(2))
  }
}