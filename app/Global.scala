

import play.api._
import com.mongodb.casbah.Imports._
import play.api.GlobalSettings
import utils.GlobalContext
//import org.apache.spark._
//import org.apache.spark.streaming._
//import org.apache.spark.streaming.StreamingContext._



object Global extends GlobalSettings {


 
  
  override def onStart(app: Application) {
    Logger.info("Application has started ciao")
 //   GlobalContext.init()


// Create a local StreamingContext with two working thread and batch interval of 1 second
 //   val conf = new SparkConf().setMaster("local[4]").setAppName("NetworkWordCount")
 //   val ssc = new StreamingContext(conf, Seconds(5))
 //   val lines = ssc.socketTextStream("localhost", 9999)
 //   lines.print
 //   ssc.start()             // Start the computation
 //   ssc.awaitTermination() 
         
 /*       val mongoClient = MongoClient("localhost", 27017)
        val db = mongoClient("meta_ckan")
        val coll = db("dataset")
        //  val queryObject = MongoDBObject()
        val results = coll.find("resources" $elemMatch (MongoDBObject("resource_format" -> "JSON")), MongoDBObject("resources" -> 1)).toList
        println(results.length)
        Logger.info("Ciao")
        mongoClient.close
        //    val results = coll.find("resources" $elemMatch (MongoDBObject("resource_format" -> "JSON"))).toList
        println("ciao")
        val resultsJSON = results.map(x => Json.parse(x.toString()))
        val res = resultsJSON.map(x => {
          val resourses = (x \ "resources").as[List[JsValue]]
          val filteredResult = resourses.filter(x => (x \ "resource_format").as[String].equals("JSON"))
          println(filteredResult)
          filteredResult
        })

        val conf = new SparkConf(false) // skip loading external settings
          .setMaster("local[4]") // run locally with enough threads
          .setAppName("firstSparkApp")
          .set("spark.logConf", "true")
          .set("spark.driver.host", "localhost")

        val sc = new SparkContext(conf)
        val sqlContext = new org.apache.spark.sql.SQLContext(sc)

        for (r <- res) {
          val json = r(0)
          var resourceName = (json \ "resource_name").as[String]

          if (resourceName.contains("/")) {
            resourceName = resourceName.replace("/", "")
          } 
          if (resourceName.contains(",")) {
            resourceName = resourceName.replace(",", "")
          } 
          if (resourceName.contains(" ")) {
            resourceName = resourceName.replace(" ", "_")
          }  
          if (resourceName.contains("-")) {
            resourceName = resourceName.replace("-", "_")
          }
          println(resourceName)
         
          if (!resourceName.equals("POI__Valsugana")) {
            val path = "/Users/ale/Development/python/PrepareOpenData/prepare_json/spark/" + resourceName + ".JSON"
       //     println(path)
            val loadedFile = sqlContext.jsonFile(path)
            loadedFile.registerTempTable(resourceName)
            println(resourceName)
            GlobalMap.TableMap += (resourceName -> loadedFile)
       //     println(GlobalMap.TableMap)
          }
        }*/
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }
}
