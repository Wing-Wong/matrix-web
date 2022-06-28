package io.github.forezp.common.cache.caffine;


import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;


import java.util.concurrent.TimeUnit;

/**
 * 用户信息缓存类
 * 包括：用户信息、用户角色信息、用户权限信息
 * @param <SysUser>
 */
public class UserRolePermissionCache<SysUser> extends AbstractCaffineCache {

    @Override
    LoadingCache createLoadingCache() {

        LoadingCache loadingCache = Caffeine.newBuilder()
                // 写缓存后过期时间 10min
                .expireAfterWrite(10 , TimeUnit.MINUTES)
                // 初始容量
                .initialCapacity(10)
                // 最大容量
                .maximumSize(99999999)
                // 开启统计功能。可以统计缓存的相关性能指标，包括命中率、错过率等。
                .recordStats()
                .build(new CacheLoader<String, SysUser>() {
                    @Override
                    public SysUser load(String key) throws Exception {
                        return null;
                    }
                });
        return loadingCache;
    }
}