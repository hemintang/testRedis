package com.xm4399

import java.util

import redis.clients.jedis.{JedisPoolConfig, JedisShardInfo, ShardedJedisPool}
import redis.clients.util.{Hashing, Sharded}

import scala.collection.mutable.ListBuffer


/**
  * Created by hemintang on 17-4-28.
  */
object RedisUtil {
  private val urlStr = "127.0.0.1:6379"
  val pool: ShardedJedisPool = init()

  private def init(): ShardedJedisPool ={
    val config: JedisPoolConfig = new JedisPoolConfig()
    config.setMaxTotal(5000)
    config.setMaxIdle(5000)
    config.setMaxWaitMillis(10000)
    config.setTestOnBorrow(true)
    val jedisShardInfos = getJedisShardInfos(urlStr)
    val pool = new ShardedJedisPool(config, jedisShardInfos, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN)
    pool
  }

  private def getJedisShardInfos(urlStr: String): util.List[JedisShardInfo] ={
    val jedisShardInfos = new util.ArrayList[JedisShardInfo]()
    urlStr.split(";").foreach(url => {
      val arr = url.split(":")
      val (host, port) = (arr(0), arr(1).toInt)
      jedisShardInfos.add(new JedisShardInfo(host, port))
    })
    jedisShardInfos
  }

  def main(args: Array[String]): Unit = {
    val jedis = RedisUtil.pool.getResource
    println(jedis.hgetAll("我"))
  }

}

