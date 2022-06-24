package io.github.forezp.permission.aspect;


import io.github.forezp.common.constant.OrderConstants;
import io.github.forezp.common.exception.AriesException;
import io.github.forezp.common.util.UserUtils;
import io.github.forezp.modules.system.entity.SysUser;
import io.github.forezp.permission.HasPermission;
import io.github.forezp.permission.realm.UserDetail;
import io.github.forezp.permission.realm.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static io.github.forezp.common.constant.CommonConstants.ROLE;
import static io.github.forezp.common.exception.ErrorCode.NO_PERMISSION;

/**
 * 权限切面
 * @author 王文渊
 */
@Aspect
@Component
@Slf4j
public class PermissionAspect implements Ordered {


    @Autowired
    UserDetailService userDetailService;


    /**
     * Pointcut切入点，切点为注解@HasPermission
     */
    @Pointcut("@annotation(io.github.forezp.permission.HasPermission)")
    public void permissionPointCut() {

    }

    /**
     * Around:环绕增强,也就是在permissionPointCut()方法前执行
     */
    @Around("permissionPointCut()")
    public Object before(ProceedingJoinPoint point) throws Throwable {
        //去掉白名单
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        Annotation[] methodAnnotations = method.getDeclaredAnnotations();
        for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof HasPermission) {
                HasPermission hasPermission = (HasPermission) annotation;
                if (!checkPermission(hasPermission)) {
                    throw new AriesException(NO_PERMISSION);
                }
            }
        }

        return point.proceed();
    }

    /**
     * 判断用户是否有某个权限
     * @param hasPermission 注解中设置的值
     * @return
     */
    private boolean checkPermission(HasPermission hasPermission) {
        // 根据token获取用户id
        String userId = UserUtils.getCurrentUser();
        if (StringUtils.isEmpty(userId)) {
            throw new AriesException(NO_PERMISSION);
        }
        // 根据用户id获取用户对应的角色集和权限集
        UserDetail userDetail = userDetailService.getUserDetail(userId);
        if (userDetail == null) {
            throw new AriesException(NO_PERMISSION);
        }
        String hasRole = hasPermission.hasRole();
        String permission = hasPermission.hasPermission();
        if (StringUtils.isEmpty(hasRole) && StringUtils.isEmpty(permission)) {
            log.info("HasPermission annotation is not correctly used");
            throw new AriesException(NO_PERMISSION);
        }
        if (!StringUtils.isEmpty(hasRole) && !hasRole.startsWith(ROLE)) {
            hasRole = ROLE + hasRole;
        }

        // 判断用户的角色集是否包含注解上的hasRole
        boolean checkPermissionPassed = false;
        List<String> userRoles = userDetail.getUserRoles();
        if (!CollectionUtils.isEmpty(userRoles) && !StringUtils.isEmpty(hasRole)) {
            for (String userRole : userRoles) {
                if (userRole.equals(hasRole)) {
                    checkPermissionPassed = true;
                    break;
                }
            }
        }

        // 判断用户的权限集是否包含注解上的hasPermission
        List<String> userPermissions = userDetail.getUserPermissions();
        if (!CollectionUtils.isEmpty(userPermissions) && !StringUtils.isEmpty(permission)) {
            for (String userPermission : userPermissions) {
                if (userPermission.equals(permission)) {
                    checkPermissionPassed = true;
                    break;
                }
            }
        }
        return checkPermissionPassed;

    }

    @Override
    public int getOrder() {
        return OrderConstants.PERMISSION_ORDOR;
    }
}
