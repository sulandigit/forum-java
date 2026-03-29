package pub.developers.forum.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.developers.forum.common.exception.BizException;

/**
 * 用户角色枚举
 *
 * @author Qiangqiang.Bian
 * @create 20/7/23
 * @desc 定义系统中的用户角色类型及权限判断
 **/
@Getter
@AllArgsConstructor
public enum UserRoleEn {
    /**
     * 普通用户
     */
    USER("USER", "用户"),
    /**
     * 管理员
     */
    ADMIN("ADMIN", "管理员"),
    /**
     * 超级管理员
     */
    SUPER_ADMIN("SUPER_ADMIN", "超级管理员"),
    ;

    /**
     * 角色值
     */
    private String value;
    /**
     * 角色描述
     */
    private String desc;

    /**
     * 根据角色值获取枚举实例
     *
     * @param value 角色值
     * @return 对应的枚举实例,未找到时返回null
     */
    public static UserRoleEn getEntity(String value) {
        for (UserRoleEn entity : values()) {
            if (entity.getValue().equalsIgnoreCase(value)) {
                return entity;
            }
        }

        return null;
    }

    /**
     * 判断当前角色是否有权限操作目标角色
     *
     * @param value 目标角色值
     * @return true-有权限, false-无权限
     */
    public boolean hasPermission(String value) {
        if (USER.equals(this)) {
            return false;
        }

        UserRoleEn authRole = getEntity(value);
        if (ADMIN.equals(this) && (ADMIN.equals(authRole) || SUPER_ADMIN.equals(authRole))) {
            return false;
        }

        if (SUPER_ADMIN.equals(this) && SUPER_ADMIN.equals(authRole)) {
            return false;
        }

        return true;
    }
}