import breeze.linalg._
import scala.math._
val data=sc.textFile("C:/Users/zqlhust/Desktop/Data.txt")






val New=data.map{line=>
val parts=line.split(',')
val k=parts(1).toDouble
(k)
}
val a=New.reduce((a,b)=>Math.min(a,b))
val b=New.reduce((a,b)=>Math.max(a,b))
println(min)
println(max)
