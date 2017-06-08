import breeze.linalg._
import breeze.stats.distributions.MultivariateGaussian
import scala.math._

val mu = DenseVector(10.0,-10.0)
val sigma= DenseMatrix((1.0,0.5),(0.5,1.0)) 
val testmedian = new MultivariateGaussian(mu,sigma)
val N=10000
val test = testmedian.sample(N)
val result1 = (0 to 9999).map(i => {test(i)(0)*test(i)(0)*test(i)(1)* test(i)(1)}).reduce((x,y)=>(x+y)/9999)
val x=(0 to 9999).map(i=>{test(i)(0)})
val y=(0 to 9999).map(i=>{test(i)(1)})
val xy = DenseMatrix(x,y)
val r=new MultivariateGaussian(mu,sigma)
var mat=DenseMatrix.zeros[Double](2,N)
for (i<-0 until (N-1)) {
    val draw1=r.draw()
    mat(0,i)=draw1(0)
    mat(1,i)=draw1(1)
}
val m=DenseMatrix((10000.0,10000.0),(10000.0,10000.0))
val result2=(mat*mat.t):/m

val sinxx=(1 to 10000).map(i=>math.sin(r.draw()(0)*r.draw()(0))).reduce((x,y)=>(x+y)/N)
val sinxy=(1 to 10000).map(i=>math.sin(r.draw()(0))*r.draw()(1)).reduce((x,y)=>(x+y)/N)
val sinyy=(1 to 10000).map(i=>(r.draw()(1)*r.draw()(1))).reduce((x,y)=>(x+y)/N)
val result3=DenseMatrix((sinxx,sinxy),(sinxy,sinyy))