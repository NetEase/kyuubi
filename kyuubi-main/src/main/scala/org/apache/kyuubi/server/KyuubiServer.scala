/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kyuubi.server

import scala.util.Properties

import org.apache.kyuubi._
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.ha.HighAvailabilityConf._
import org.apache.kyuubi.ha.server.EmbeddedZkServer
import org.apache.kyuubi.service.{AbstractBackendService, Serverable}
import org.apache.kyuubi.util.SignalRegister

object KyuubiServer extends Logging {
  private val zkServer = new EmbeddedZkServer()

  def startServer(conf: KyuubiConf): KyuubiServer = {
    val zkEnsemble = conf.get(HA_ZK_QUORUM)
    if (zkEnsemble == null || zkEnsemble.isEmpty) {
      zkServer.initialize(conf)
      zkServer.start()
      sys.addShutdownHook(zkServer.stop())
      conf.set(HA_ZK_QUORUM, zkServer.getConnectString)
    }

    val server = new KyuubiServer()
    server.initialize(conf)
    server.start()
    sys.addShutdownHook(server.stop())
    server
  }

  def main(args: Array[String]): Unit = {
    info(
       """
         |                  Welcome to
         |  __  __                           __
         | /\ \/\ \                         /\ \      __
         | \ \ \/'/'  __  __  __  __  __  __\ \ \____/\_\
         |  \ \ , <  /\ \/\ \/\ \/\ \/\ \/\ \\ \ '__`\/\ \
         |   \ \ \\`\\ \ \_\ \ \ \_\ \ \ \_\ \\ \ \L\ \ \ \
         |    \ \_\ \_\/`____ \ \____/\ \____/ \ \_,__/\ \_\
         |     \/_/\/_/`/___/> \/___/  \/___/   \/___/  \/_/
         |                /\___/
         |                \/__/
       """.stripMargin)
    info(s"Version: $KYUUBI_VERSION, Revision: $REVISION, Branch: $BRANCH," +
      s" Java: $JAVA_COMPILE_VERSION, Scala: $SCALA_COMPILE_VERSION," +
      s" Spark: $SPARK_COMPILE_VERSION, Hadoop: $HADOOP_COMPILE_VERSION," +
      s" Hive: $HIVE_COMPILE_VERSION")
    info(s"Using Scala ${Properties.versionString}, ${Properties.javaVmName}," +
      s" ${Properties.javaVersion}")
    SignalRegister.registerLogger(logger)
    val conf = new KyuubiConf().loadFileDefaults()
    startServer(conf)
  }
}

class KyuubiServer(name: String) extends Serverable(name) {

  def this() = this(classOf[KyuubiServer].getSimpleName)

  override private[kyuubi] val backendService: AbstractBackendService = new KyuubiBackendService()

  override protected def stopServer(): Unit = KyuubiServer.zkServer.stop()
}
