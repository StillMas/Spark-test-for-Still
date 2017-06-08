import java.util.concurrent.ThreadLocalRandom
val r = ThreadLocalRandom.current
val OurExpRVs = (1 to 1000).map(x=> r.nextDouble).map(x => if(x>3/2) -x else x).toArray[Double]
