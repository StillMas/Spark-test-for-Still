abstract class Updater extends Serializable {
import org.apache.spark.mllib.linalg
import scala.math._
import breeze.linalg.{axpy => brzAxpy, norm => brzNorm, DenseVector => BV}
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.mllib.linalg._
  def compute(
      weightsOld: BV[Double],
      gradient: BV[Double],
      stepSize: Double,
      iter: Int,
      regParam: Double): (Vector, Double)
}
class SquaredL2Updater extends Updater {
import org.apache.spark.mllib.linalg
import scala.math._
import breeze.linalg.{axpy => brzAxpy, norm => brzNorm, DenseVector => BV}
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.mllib.linalg.{Vector,Vectors}
  override def compute(
      weightsOld: BV[Double],
      gradient: BV[Double],
      stepSize: Double,
      iter: Int,
      regParam: Double): (Vector, Double) = {
    val thisIterStepSize = stepSize / math.sqrt(iter)
    val brzWeights: BV[Double] = weightsOld.toDenseVector
    brzWeights :*= (1.0 - thisIterStepSize * regParam)
    brzAxpy(-thisIterStepSize, gradient, brzWeights)
    val norm = brzNorm(brzWeights, 2.0)
	val Weights=Vectors.dense(brzWeights.toArray)
    (Weights, 0.5 * regParam * norm * norm)
  }
}