package tech.sqlclub.redis.constants

object Constants {

  val REDIS_DELIVER = "redis"
  val MSG_PIPER_CLASS = "msg.piper.class"
  val JEDIS_MINIDLE = "minidle"
  val JEDIS_MAXIDLE = "maxidle"
  val JEDIS_MAXTOTAL = "maxtotal"
  val JEDIS_PASSWORD = "password"
  val JEDIS_DATABASE = "database"
  val JEDIS_ADDRESSES = "addresses"
  val JEDIS_SENTINEL_MASTER = "sentinel.master"
  val MSG_PIPER_DEFAULT_CLASS = "tech.sqlclub.redis.single.JedisMsgDeliver"
  val MSG_MAXLENGTH = "msg.maxlength"

  /**
    * 成功码
    */
  val SUCC_CODE = 0

  /**
    * 发送失败码
    */
  val FAIL_CODE = -1

  /**
    * 消息超长码
    */
  val MSG_TOO_LONG = -2


}

