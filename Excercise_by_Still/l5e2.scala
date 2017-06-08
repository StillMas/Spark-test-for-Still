val n=100
val k=20
val dmz=DenseMatrix.zeros[Double](n,k)
val ranunif=new Uniform(0.0,1.0)
val ranbinom=new Binomial(1,0.7)
val Z=dmz.map(i=>ranunif.draw())
val data=(1 to n).map(i=>ranbinom.draw().toDouble).toArray
val y=DenseVector(data)
    //compute
def iterfunc(oldbeta:DenseVector[Double],y:DenseVector[Double]): DenseVector[Double]={
    val pi = DenseVector.zeros[Double](n)
    val pii=pi
    for (i<-0 until n) {
      pi(i)=math.exp(Z(i,::).t dot oldbeta)/(1+math.exp(Z(i,::).t dot oldbeta))
      pii(i)=pi(i)*(1-pi(i))
    }
    val W=diag(pii)
    oldbeta-(inv(-Z.t * W * Z) * (Z.t * (y-pii)))
}
var Oldbeta=DenseVector.ones[Double](k)
var beta=DenseVector.zeros[Double](k)
var ii=0
val epsilon=0.1
var L2norm=0.0
do {
    ii+=1
    Oldbeta = beta
    beta=iterfunc(Oldbeta,y)
    var L2compute=0.0
    for (i<-0 until k) {
       L2compute=(beta(i)-Oldbeta(i))*(beta(i)-Oldbeta(i))
    }
    L2norm=math.sqrt(L2compute)
    println(s"step$ii",beta)
}while(L2norm > epsilon)
    println("final",beta)