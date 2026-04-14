package com.scm.logistics.service;

import java.util.List;
import com.scm.logistics.domain.LogisticsCustomer;

/**
 * 客户信息Service接口
 *
 * @author scm
 * @date 2026-04-14
 */
public interface ILogisticsCustomerService
{
    /**
     * 查询客户信息
     *
     * @param customerId 客户信息主键
     * @return 客户信息
     */
    public LogisticsCustomer selectCustomerById(Long customerId);

    /**
     * 查询客户信息列表
     *
     * @param logisticsCustomer 客户信息
     * @return 客户信息集合
     */
    public List<LogisticsCustomer> selectCustomerList(LogisticsCustomer logisticsCustomer);

    /**
     * 新增客户信息
     *
     * @param logisticsCustomer 客户信息
     * @return 结果
     */
    public int insertCustomer(LogisticsCustomer logisticsCustomer);

    /**
     * 修改客户信息
     *
     * @param logisticsCustomer 客户信息
     * @return 结果
     */
    public int updateCustomer(LogisticsCustomer logisticsCustomer);

    /**
     * 批量删除客户信息
     *
     * @param customerIds 需要删除的客户信息主键集合
     * @return 结果
     */
    public int deleteCustomerByIds(Long[] customerIds);

    /**
     * 删除客户信息信息
     *
     * @param customerId 客户信息主键
     * @return 结果
     */
    public int deleteCustomerById(Long customerId);

    /**
     * 校验客户编码是否唯一
     *
     * @param logisticsCustomer 客户信息
     * @return 结果
     */
    public boolean checkCustomerCodeUnique(LogisticsCustomer logisticsCustomer);
}
