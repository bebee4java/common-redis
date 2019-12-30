package tech.sqlclub.redis

import tech.sqlclub.common.utils.ConfigUtils


/**
  *
  * Created by songgr on 2019/12/19.
  */
object MsgPiperTest {

 def main(args: Array[String]): Unit = {

  val map = Map("xxx" -> "xxxxx")

  ConfigUtils.configBuilder(map)

  println(ConfigUtils.getStringValue("xxx"))

  val clinet = RedisMsgDeliver.getInstance


  for (i <- 1 to 10) {
   clinet.sendMsg("test2", "1111test")
   println(clinet.getKeys("*"))
   println(clinet.getListLen("test2"))
  }


  clinet.destroy



 }


}
