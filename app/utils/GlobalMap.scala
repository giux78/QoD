package utils

import org.apache.spark.sql.SchemaRDD

object GlobalMap {
    
  var TableMap:Map[String, SchemaRDD] = Map()
  

}