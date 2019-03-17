/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yaooqinn.kyuubi

import org.slf4j.LoggerFactory

trait Logging {

  // Method to get the logger name for this object
  protected def logName: String = {
    // Ignore trailing $'s in the class names for Scala objects
    this.getClass.getName.stripSuffix("$")
  }

  lazy val logger = LoggerFactory.getLogger(logName)

  def debug(message: => Any): Unit = {
    if (logger.isDebugEnabled) {
      logger.debug(message.toString)
    }
  }

  def info(message: => Any): Unit = {
    if (logger.isInfoEnabled) {
      logger.info(message.toString)
    }
  }

  def warn(message: => Any): Unit = {
    logger.warn(message.toString)
  }

  def warn(message: => Any, t: Throwable): Unit = {
    logger.warn(message.toString, t)
  }

  def error(message: => Any, t: Throwable): Unit = {
    logger.error(message.toString, t)
  }

  def error(message: => Any): Unit = {
    logger.error(message.toString)
  }
}
