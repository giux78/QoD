package controllers

import play.api._
import play.api.mvc._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import org.apache.hadoop.fs._
import org.apache.hadoop.conf._
import java.io.File
import play.api.libs.json.Json
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import java.util.Date
import play.api.libs.json.JsUndefined
import scala.collection.mutable.MutableList

object OpenMemoriesController extends Controller {

  val hadoopPath = "hdfs://master/user/open_memories/"

  def openMemoriesDataset() = Action {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("open_memories")
    val coll = db("dataset")
    //  val queryObject = MongoDBObject()
    val results = coll.find().toList
    //  println(results.length)
    mongoClient.close
    val json = com.mongodb.util.JSON.serialize(results)
    Ok(json)
  }

  def downloadFileFromHadoop(dirName: String, resourceName: String, fileName: String) = Action {
    val configuration = new org.apache.hadoop.conf.Configuration();
    configuration.set("fs.default.name", "hdfs://master");
    val hdfs = FileSystem.get(configuration);
    val srcPath = new Path(hadoopPath + dirName + "/" + resourceName + "/"+ fileName);
    println(srcPath)
    if (hdfs.exists(srcPath)) {
      val tmpPath = Play.current.path.getAbsolutePath()
      println(tmpPath)
      val tmpFileDir = tmpPath + "/tmpToDownload"
      val tmpDir = new File(tmpFileDir)
      if (tmpDir.exists()) {
        tmpDir.mkdirs()
      }
      val tmpFilePath = tmpFileDir + "/" + fileName
      val dstFilePath = new Path(tmpFilePath)
      println(dstFilePath)
      hdfs.copyToLocalFile(srcPath, dstFilePath)
      val content = new File(dstFilePath.toString())

      //   Ok.sendFile(content = content , inline = true, fileName = _ => dstFilePath.getName())
      Ok.sendFile(content = content, fileName = _ => dstFilePath.getName())

    } else {
      Ok(Json.toJson(Map("result" -> "problem")))
    }
  }

  def downloadZipFileHadoop(resourceId: String) = Action {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("open_memories")
    val coll = db("dataset")
    val projObject = MongoDBObject("resources.$" -> 1)
    //   val elemMatch = $elemMatch(MongoDBObject("resource_id" -> resourceId))
    val results = coll.findOne("resources" $elemMatch (MongoDBObject("resource_id" -> resourceId)), projObject).getOrElse(None)
    if (results != None) {
      val jsonResults = Json.parse(results.toString())
      val resources = (jsonResults \ "resources").as[List[JsValue]]
      val mainResource = resources(0)
      val mainResourceId = (mainResource \ "resource_id").as[String]
      val mainResourceName = (mainResource \ "resource_name").as[String]
      val hdfsPath = (mainResource \ "resource_hadoop_path").as[String]
      val timestamp = new Date().getTime().toString()
      val rHistory = (mainResource \ "resource_history")
      println("ciao")
      println(rHistory)
      val configuration = new org.apache.hadoop.conf.Configuration();
      configuration.set("fs.default.name", "hdfs://master");
      val hdfs = FileSystem.get(configuration);
      val srcPath = new Path(hdfsPath);
      if (hdfs.exists(srcPath)) {
        val tmpPath = Play.current.path.getAbsolutePath()
        println(tmpPath)
        val tmpFileDir = tmpPath + "/tmpToDownload/" + timestamp
        val tmpDir = new File(tmpFileDir)
        if (!tmpDir.exists()) {
          tmpDir.mkdirs()
        }
        val tmpFilePath = tmpFileDir + "/" + srcPath.getName()
        val dstFilePath = new Path(tmpFilePath)
        println(dstFilePath)
        hdfs.copyToLocalFile(srcPath, dstFilePath)
        val dirToZip = new File(tmpFileDir + "/"+ mainResourceName +".zip")
        //  if(!dirToZip.exists()){
        //    dirToZip
        //  }
     //   val content = new File(dstFilePath.toString())
        val listPathFile = MutableList[String]()
        if (!rHistory.isInstanceOf[JsUndefined]) {
          val rHistoryList = rHistory.as[List[JsValue]]
          rHistoryList.foreach(obj => {
            val hdfsP = (obj \ "resource_hadoop_path").as[String]
            var hdfsCreatedOn = (obj \ "resource_createdOn").as[String]
            hdfsCreatedOn = hdfsCreatedOn.replaceAll(" ", "").replaceAll(":", "")
       //     val tmpFileDir = tmpPath + "/tmpToDownload/" + timestamp
            val tmpFilePathZipped = tmpFileDir + "/" + hdfsCreatedOn + "_" +srcPath.getName()
            val dstFilePathZipped = new Path(tmpFilePathZipped)
            println(dstFilePathZipped)
            hdfs.copyToLocalFile(new Path(hdfsP), dstFilePathZipped)
            listPathFile += tmpFilePathZipped
          })
        }
        listPathFile += dstFilePath.toString()
        zip(dirToZip.getAbsolutePath(), listPathFile)
        val content = new File(dirToZip.getAbsolutePath()) 
        Ok.sendFile(content = content, fileName = _ => content.getName())
      } else {
        Ok("")
      }
    } else {
      Ok("")
    }
  }

  def zip(out: String, files: Iterable[String]) = {
    import java.io.{ BufferedInputStream, FileInputStream, FileOutputStream }
    import java.util.zip.{ ZipEntry, ZipOutputStream }

    val zip = new ZipOutputStream(new FileOutputStream(out))

    files.foreach { name =>
      val file = new File(name)
      zip.putNextEntry(new ZipEntry(file.getName()))
      val in = new BufferedInputStream(new FileInputStream(name))
      var b = in.read()
      while (b > -1) {
        zip.write(b)
        b = in.read()
      }
      in.close()
      zip.closeEntry()
    }
    zip.close()
  }

}