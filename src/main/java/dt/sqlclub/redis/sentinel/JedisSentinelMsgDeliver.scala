package dt.sqlclub.redis.sentinel

import java.util

import dt.sqlclub.common.exception.SQLClubException
import dt.sqlclub.common.log.Logging
import dt.sqlclub.redis.RedisMsgDeliver
import dt.sqlclub.redis.constants.Constants
import org.apache.commons.lang3.StringUtils
import redis.clients.jedis.{Jedis, JedisPoolConfig, JedisSentinelPool}
import redis.clients.jedis.commands.JedisCommands

/**
  * redis sentinel model implement
  * Created by songgr on 2019/12/19.
  */
private[redis] class JedisSentinelMsgDeliver(conf:Map[String,Object]) extends RedisMsgDeliver with Logging{
  private val TIMEOUT = 10000
  @transient private var jedisPool: JedisSentinelPool = initPool

  override def clinet: JedisCommands = getJedis

  def initPool = {
    if (jedisPool != null) {
      logInfo("==== JedisSentinelMsgDeliver reconnect jedisPool... ====")
      jedisPool.close()
      jedisPool = null
    }
    logInfo("JedisPool init....")
    val minIdle = conf.getOrElse(Constants.JEDIS_MINIDLE, 5)
    val maxIdle = conf.getOrElse(Constants.JEDIS_MAXIDLE, 10)
    val maxTotal = conf.getOrElse(Constants.JEDIS_MAXTOTAL, 30)
    val address = conf.get(Constants.JEDIS_ADDRESSES)
    val password = conf.getOrElse(Constants.JEDIS_PASSWORD, null).asInstanceOf[String]
    val dbIndex = conf.getOrElse(Constants.JEDIS_DATABASE, 0)
    val master = conf.get(Constants.JEDIS_SENTINEL_MASTER).asInstanceOf[String]

    if (address == null || address.isEmpty || address.get.asInstanceOf[String].isEmpty) {
      throw new SQLClubException("redis address con't find!!!")
    }
    val _address = address.get.asInstanceOf[String].split(",")

    if (master == null || master.isEmpty){
      throw new SQLClubException("Can connect to sentinel,Master must be monitored!")
    }

    val config:JedisPoolConfig = new JedisPoolConfig()
    config.setMinIdle(Integer.valueOf(minIdle.toString))
    config.setMaxIdle(Integer.valueOf(maxIdle.toString))
    config.setMaxTotal(Integer.valueOf(maxTotal.toString))
    config.setTestWhileIdle(true)

    val sentinels = new util.HashSet[String]()
    for (address <- _address){
      if (StringUtils.isNotBlank(address))
        sentinels.add(address)
    }

    val pool = new JedisSentinelPool(master, sentinels, config, TIMEOUT, password, Integer.valueOf(dbIndex.toString))
    logInfo("JedisSentinelMsgDeliver create jedisPool succeed!")
    pool
  }

  override def destroy(): Unit = {
    if (jedisPool != null){
      jedisPool.destroy()
      jedisPool = null
    }
  }

  private def getJedis:Jedis = {
    if (jedisPool == null)
      jedisPool = initPool

    var jedis:Jedis = null
    jedis = jedisPool.getResource
    var retries = 0
    while (!jedis.isConnected || !(jedis.ping() == "PONG")) {
      retries += 1
      close(jedis)

      if (retries >=3)
        throw new SQLClubException("can not get resource from jedis pool...")

      jedis = jedisPool.getResource
    }

    jedis
  }

  override def getKeys(pattern: String): Set[String] = {
    var jedis:Jedis = null
    try {
      jedis = getJedis
      import scala.collection.JavaConverters._
      jedis.keys(pattern).asScala.toSet[String]
    } catch {
      case e:Exception => {
        logError(s"get keys error! pattern: $pattern" ,e)
        Set.empty[String]
      }
    } finally {
      close(jedis)
    }
  }

}
