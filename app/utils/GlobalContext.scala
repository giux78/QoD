package utils

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import java.util.UUID

object GlobalContext {
    
       var contextGlobal :SparkContext = null 
  
    def init() = {
           {
    /*        val conf = new SparkConf(false) // skip loading external settings
        //  .setMaster("local[2]") // run locally with enough threads
          .setMaster("spark://ales-MacBook-Pro-2.local:7077") 
          .setAppName(UUID.randomUUID().toString())
          .setSparkHome("/Users/ale/Development/spark/spark-1.1.0")
          .set("spark.logConf", "true")
          .set("spark.driver.host", "localhost")

          conf.set("spark.scheduler.mode", "FAIR")
          conf.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem")
          val configuration = new org.apache.hadoop.conf.Configuration();
          configuration.set("fs.default.name", "hdfs://master");
          configuration.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem")
          configuration.set("fs.file.impl","org.apache.hadoop.fs.LocalFileSystem")
       
          val sc = new SparkContext(conf)
          sc.hadoopConfiguration.set("fs.file.impl","org.apache.hadoop.fs.LocalFileSystem")
          sc.addJar("/Users/ale/Development/spark/Play-Spark-Scala/target/scala-2.10/firstsparkapp_2.10-1.0-SNAPSHOT.jar")
          contextGlobal = sc */
  }
    }
  
    


}