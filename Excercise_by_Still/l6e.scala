import breeze.stats.distributions.MultivariateGaussian
import breeze.linalg._
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.regression.LinearRegressionWithSGD;
import org.apache.spark.mllib.regression.LinearRegressionModel
import java.util.concurrent.ThreadLocalRandom
val N=1000
val mu=DenseVector(0.0,0.0,0.0)
val sigma=DenseMatrix((1.0,0.0,0.0),(0.0,1.0,0.0),(0.0,0.0,1.0))
val seed=new MultivariateGaussian(mu,sigma)
val datatest=(1 to 1000).map(i=>DenseVector(0.0,0.0,0.0)).toArray
for(i <- 0 until (N-1))
{
datatest(i)=(seed.draw())
}

val datamedian = (0 to (N-1)).map(i=> datatest(i).toArray)
val NewData=sc.parallelize(datamedian)
var parsedData = NewData.map{line =>
LabeledPoint(line(0).toDouble,
Vectors.dense(line.drop(1).map(_.toDouble)))
}
var numIterations = 100//随机梯度算法的循环次数
var stepSize = 1.0 //随机梯度算法的步长
var regParam = 0.0 //Regularization parameter for Lasso and ridge
val algorithm = new LinearRegressionWithSGD()
algorithm.optimizer.setNumIterations(numIterations)
.setStepSize(stepSize)
.setRegParam(regParam)
algorithm.setIntercept(true)

val model = algorithm.run(parsedData)
val Coefficients = DenseVector(model.weights.toArray)
val Intercept = model.intercept
var lines = NewData.map(line =>{
val r = ThreadLocalRandom.current
val fitted = DenseVector(line.drop(1)).t*Coefficients+Intercept
val residual = line(0) - fitted
Array(fitted+r.nextGaussian*residual)++line.drop(1)
})
