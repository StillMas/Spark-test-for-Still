import org.apache.spark.mllib.clustering.{LDA, DistributedLDAModel}
import org.apache.spark.mllib.linalg.Vectors
import breeze.stats.distributions.Binomial
val data= (1 to 3000).map(i=>DenseVector(0.0,0.0,0.0)).toArray
val datakind1=new Binomial(400,0.3)
val datakind2=new Binomial(600,0.4)
val datakind3=new Binomial(800,0.5)
for(i <- 0 until 2999)
{data(i)(0)=datakind1.sample(1)(0)
data(i)(1)=datakind1.sample(1)(0)
data(i)(2)=datakind1.sample(1)(0)
}
val datamedian1=(0 to 2999).map(i=>data(i).toArray)
val datamedian=(0 to 2999).map(i=>Vectors.dense(datamedian1(i)))
val parsedData=sc.parallelize(datamedian)
val corpus = parsedData.zipWithIndex.map(_.swap).cache()
val ldaModel = new LDA().setK(3).run(corpus)//利用LDA模型将数据分成三组（主题）
println("Learned topics (as distributions over vocab of " + ldaModel.vocabSize + " words):")// 输出三组（主题）信息
val topics = ldaModel.topicsMatrix
for (topic <- Range(0, 3)) {
print("Topic " + topic + ":")
for (word <- Range(0, ldaModel.vocabSize)) {
print(" " + topics(word, topic)); }
println()
}