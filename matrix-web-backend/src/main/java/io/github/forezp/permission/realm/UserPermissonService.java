package io.github.forezp.permission.realm;

import io.github.forezp.common.cache.caffine.UserRolePermissionCache;
import io.github.forezp.modules.system.entity.SysUser;
import io.github.forezp.modules.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPermissonService {

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    UserRolePermissionCache<SysUser> userRolePermissionCache;

    public SysUser getUserRolePerssion(String userId) {
        // 先去缓存中取
        SysUser sysUser = (SysUser) userRolePermissionCache.get(userId);
        // 缓存中没有，则查询数据库，并放入缓存中
        if (sysUser == null) {
            sysUser = sysUserMapper.selectUserRolePermission(userId);
            userRolePermissionCache.put(userId, sysUser);
        }
        return sysUser;
    }
}
