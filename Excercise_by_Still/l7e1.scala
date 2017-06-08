import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.{Vector=>V,Vectors=>VS}
import breeze.stats.distributions.MultivariateGaussian
import breeze.linalg._

val mu1=DenseVector(1.0,-1.0)
val sigma1=DenseMatrix((1.0,0.5),(0.5,1.0))
val dataclass1 = new MultivariateGaussian(mu1,sigma1)

val mu2=DenseVector(2.0,-2.0)
val sigma2=DenseMatrix((1.0,0.2),(0.2,1.0))
val dataclass2 = new MultivariateGaussian(mu1,sigma1)

val mu3=DenseVector(3.0,-3.0)
val sigma3=DenseMatrix((1.0,0.6),(0.6,1.0))
val dataclass3 = new MultivariateGaussian(mu1,sigma1)
val data= (1 to 3000).map(i=>DenseVector(0.0,0.0)).toArray

for(i <- 0 until 999){
   data(i)=dataclass1.sample(1)(0)
}
for(i <- 1000 until 1999){
   data(i)=dataclass2.sample(1)(0)
}
for(i <- 2000 until 2999){
   data(i)=dataclass3.sample(1)(0)
}

val datamedian1=(0 to 2999).map(i=>data(i).toArray)
val datamedian=(0 to 2999).map(i=>Vectors.dense(datamedian1(i)))
val NewData=sc.parallelize(datamedian)
val numClusters = 2
val numIterations = 30// 设置初始参数，并用Kmeans算法将数据分成两组
val clusters = KMeans.train(NewData, numClusters, numIterations)
val WSSSE = clusters.computeCost(NewData)// 计算组内差
println("Input data rows: " + NewData.count())
println("K-means Cost:" + WSSSE)
clusters.clusterCenters.foreach{println}//显示中心点
val clusterRddInt = clusters.predict(NewData)//预测数据分组
val clusterCount = clusterRddInt.countByValue
clusterCount.toList.foreach{println}
