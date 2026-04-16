package com.scm.common.utils;

import java.util.Date;
import java.util.List;
import com.scm.common.core.domain.BaseEntity;
import com.scm.common.utils.SecurityUtils;

/**
 * Entity基类工具类
 *
 * 提供 BaseEntity 相关字段的自动填充功能
 *
 * @author scm
 */
public class BaseEntityUtils
{
    /**
     * 填充创建和更新信息
     * 新增操作时使用，同时设置 createBy/updateBy/createTime/updateTime
     *
     * @param entity BaseEntity 实体对象
     */
    public static void fillCreateInfo(BaseEntity entity)
    {
        if (entity == null)
        {
            return;
        }
        try
        {
            String username = SecurityUtils.getUsername();
            entity.setCreateBy(username);
            entity.setUpdateBy(username);
        }
        catch (Exception e)
        {
            // 获取用户名失败时，不设置 createBy/updateBy
        }
        Date now = DateUtils.getNowDate();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
    }

    /**
     * 填充更新信息（更新者、更新时间）
     * 修改操作时使用
     *
     * @param entity BaseEntity 实体对象
     */
    public static void fillUpdateInfo(BaseEntity entity)
    {
        if (entity == null)
        {
            return;
        }
        try
        {
            String username = SecurityUtils.getUsername();
            entity.setUpdateBy(username);
        }
        catch (Exception e)
        {
            // 获取用户名失败时，不设置 updateBy
        }
        entity.setUpdateTime(DateUtils.getNowDate());
    }

    /**
     * 批量填充创建信息
     *
     * @param entities BaseEntity 实体对象列表
     */
    public static <T extends BaseEntity> void fillCreateInfoForBatch(List<T> entities)
    {
        if (entities == null || entities.isEmpty())
        {
            return;
        }
        String username = null;
        try
        {
            username = SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            // 获取用户名失败时，username 为 null
        }
        Date now = DateUtils.getNowDate();

        for (T entity : entities)
        {
            if (entity != null)
            {
                if (username != null)
                {
                    entity.setCreateBy(username);
                    entity.setUpdateBy(username);
                }
                entity.setCreateTime(now);
                entity.setUpdateTime(now);
            }
        }
    }

    /**
     * 批量填充更新信息
     *
     * @param entities BaseEntity 实体对象列表
     */
    public static <T extends BaseEntity> void fillUpdateInfoForBatch(List<T> entities)
    {
        if (entities == null || entities.isEmpty())
        {
            return;
        }
        String username = null;
        try
        {
            username = SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            // 获取用户名失败时，username 为 null
        }
        Date now = DateUtils.getNowDate();

        for (T entity : entities)
        {
            if (entity != null)
            {
                if (username != null)
                {
                    entity.setUpdateBy(username);
                }
                entity.setUpdateTime(now);
            }
        }
    }
}
