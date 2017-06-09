//csv file is already imported into HDFS
val data=sc.textFile("/user/2016210876/Data.csv")
val data1=data.map(line=>line.split(",")).map(i=>i(1)).map(_.toDouble)
//use MLE to estimate parameters mu and theta, given that data~U(mu-theta/2,mu+theta/2)
//according to MLE, mu=(max(x)+min(x))/2,and theta=max(x)-min(x)
val max_x=data1.reduce((x,y)=>if(x>y) x else y)
val min_x=data1.reduce((x,y)=>if(x>y) y else x)
val mu=(max_x+min_x)/2
val theta=max_x-min_x
val upper=mu+theta/2
val lower=mu-theta/2

println(s"according to MLE, data~U($upper,$lower)")
