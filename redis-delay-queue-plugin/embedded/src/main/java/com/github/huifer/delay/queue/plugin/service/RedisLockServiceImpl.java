package com.github.huifer.delay.queue.plugin.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Service
public class RedisLockServiceImpl {
	private static final byte[] SET_IF_NOT_EXIST = "NX".getBytes();
	/**
	 * EX单位秒，PX单位毫秒
	 */
	private static final byte[] SET_WITH_EXPIRE_MS = "PX".getBytes();

	private final StringRedisTemplate redisTemplate;

	public RedisLockServiceImpl(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public boolean get(String lockKey, String lockId, int time) {
		boolean result = false;
		byte[] argTime = String.valueOf(time * 1000).getBytes();
		if (!StringUtils.isEmpty(lockKey) && !StringUtils.isEmpty(lockId)) {
			RedisSerializer keySerializer = this.redisTemplate.getKeySerializer();
			RedisSerializer valueSerializer = this.redisTemplate.getValueSerializer();
			result =
					this.redisTemplate.execute(
							(RedisCallback<Boolean>)
									connection -> {
										Object obj =
												connection.execute(
														"set",
														keySerializer.serialize(lockKey),
														valueSerializer.serialize(lockId),
														SET_IF_NOT_EXIST,
														SET_WITH_EXPIRE_MS,
														argTime);
										return obj != null;
									});
		}
		return result;
	}

	public boolean release(String lockKey, String lockId) {
		String script =
				"if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

		DefaultRedisScript<Long> rs = new DefaultRedisScript<>();
		rs.setScriptText(script);
		rs.setResultType(Long.class);

		Object obj = this.redisTemplate.execute(rs, Collections.singletonList(lockKey), lockId);
		if (obj != null) {
			return Long.valueOf(obj.toString()).longValue() > 0;
		}
		return false;
	}
}