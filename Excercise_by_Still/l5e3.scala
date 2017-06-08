import scala.math._
import scala.util.Random
import breeze.linalg.{norm => brzNorm, axpy => brzAxpy, Vector => BV}
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.mllib.linalg.{Vectors, Vector}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.optimization.{GradientDescent,LogisticGradient,SquaredL2Updater}


val gradient = new LogisticGradient()
val updater = new SquaredL2Updater()
val stepSize = 0.1
val numIterations = 10
val regParam = 1.0
val miniBatchFrac = 1.0
val n = 10000
val p = 10000
val NonZeroLength=100
val beta = (1 to p).map(_.toDouble).toArray[Double].map(i =>{if(i<NonZeroLength+1) 1.0 else 0.0})

val points = sc.parallelize(0 until n, 2).map {iter =>
val random = new Random()
val x = random.nextDouble()
val delta = random.nextDouble()
val y = beta(iter)*x + delta +1
(y, Vectors.dense(Array.fill(p)(random.nextDouble())))
}

@DeveloperApi
class SquaredL2Updater extends Updater {
override def compute(
weightsOld: Vector,
gradient: Vector,
stepSize: Double,
iter: Int,
regParam: Double): (Vector, Double) = {
val thisIterStepSize = stepSize / math.sqrt(iter)
val brzWeights: BV[Double] = weightsOld.toBreeze.toDenseVector
brzWeights :*= (1.0 - thisIterStepSize * regParam)
brzAxpy(-thisIterStepSize, gradient.toBreeze, brzWeights)
val norm = brzNorm(brzWeights, 2.0)
(Vectors.fromBreeze(brzWeights), 0.5 * regParam * norm * norm)
}
}

val (weights, loss) = GradientDescent.runMiniBatchSGD(
points,
gradient,
updater,
stepSize,
numIterations,
regParam,
miniBatchFrac,
Vectors.dense(new Array[Double](p)))
