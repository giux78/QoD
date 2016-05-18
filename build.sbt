name := "QoD"

version := "1.0-SNAPSHOT"

resolvers ++= Seq(
 // "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
)

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.apache.spark"  %% "spark-core"              % "1.1.0",
  "com.typesafe.akka" %% "akka-actor"              % "2.2.3", 
  "com.typesafe.akka" %% "akka-slf4j"              % "2.2.3",
  "org.apache.spark"  %% "spark-streaming-twitter" % "1.1.0",
  "org.apache.spark"  %% "spark-streaming" % "1.1.0",
  "org.apache.spark"  %% "spark-sql"               % "1.1.0",
  "org.apache.spark"  %% "spark-mllib"             % "1.1.0",
  "redis.clients"     % "jedis" % "2.4.1",
  "org.mongodb" %% "casbah" % "2.7.0",
  "com.github.jsqlparser" % "jsqlparser" % "0.9.1",
  "ws.securesocial" %% "securesocial" % "2.1.4",
  "org.apache.hadoop" % "hadoop-client" % "2.0.0-mr1-cdh4.0.1"
  // "com.foundationdb" % "fdb-sql-parser" % "1.0.17"
  )     

play.Project.playScalaSettings
