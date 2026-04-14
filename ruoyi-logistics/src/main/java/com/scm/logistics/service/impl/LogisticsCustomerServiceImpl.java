package com.scm.logistics.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.common.exception.ServiceException;
import com.scm.common.utils.StringUtils;
import com.scm.logistics.domain.LogisticsCustomer;
import com.scm.logistics.mapper.LogisticsCustomerMapper;
import com.scm.logistics.service.ILogisticsCustomerService;

/**
 * 客户信息Service业务层处理
 *
 * @author scm
 * @date 2026-04-14
 */
@Service
public class LogisticsCustomerServiceImpl implements ILogisticsCustomerService
{
    @Autowired
    private LogisticsCustomerMapper customerMapper;

    /**
     * 查询客户信息
     *
     * @param customerId 客户信息主键
     * @return 客户信息
     */
    @Override
    public LogisticsCustomer selectCustomerById(Long customerId)
    {
        return customerMapper.selectCustomerById(customerId);
    }

    /**
     * 查询客户信息列表
     *
     * @param logisticsCustomer 客户信息
     * @return 客户信息
     */
    @Override
    public List<LogisticsCustomer> selectCustomerList(LogisticsCustomer logisticsCustomer)
    {
        return customerMapper.selectCustomerList(logisticsCustomer);
    }

    /**
     * 新增客户信息
     *
     * @param logisticsCustomer 客户信息
     * @return 结果
     */
    @Override
    public int insertCustomer(LogisticsCustomer logisticsCustomer)
    {
        if (!checkCustomerCodeUnique(logisticsCustomer))
        {
            throw new ServiceException("新增客户'" + logisticsCustomer.getCustomerName() + "'失败，客户编码已存在");
        }
        return customerMapper.insertCustomer(logisticsCustomer);
    }

    /**
     * 修改客户信息
     *
     * @param logisticsCustomer 客户信息
     * @return 结果
     */
    @Override
    public int updateCustomer(LogisticsCustomer logisticsCustomer)
    {
        if (!checkCustomerCodeUnique(logisticsCustomer))
        {
            throw new ServiceException("修改客户'" + logisticsCustomer.getCustomerName() + "'失败，客户编码已存在");
        }
        return customerMapper.updateCustomer(logisticsCustomer);
    }

    /**
     * 批量删除客户信息
     *
     * @param customerIds 需要删除的客户信息主键
     * @return 结果
     */
    @Override
    public int deleteCustomerByIds(Long[] customerIds)
    {
        return customerMapper.deleteCustomerByIds(customerIds);
    }

    /**
     * 删除客户信息信息
     *
     * @param customerId 客户信息主键
     * @return 结果
     */
    @Override
    public int deleteCustomerById(Long customerId)
    {
        return customerMapper.deleteCustomerById(customerId);
    }

    /**
     * 校验客户编码是否唯一
     *
     * @param logisticsCustomer 客户信息
     * @return 结果
     */
    @Override
    public boolean checkCustomerCodeUnique(LogisticsCustomer logisticsCustomer)
    {
        Long customerId = StringUtils.isNull(logisticsCustomer.getCustomerId()) ? -1L : logisticsCustomer.getCustomerId();
        LogisticsCustomer info = customerMapper.checkCustomerCodeUnique(logisticsCustomer.getCustomerCode());
        if (StringUtils.isNotNull(info) && info.getCustomerId().longValue() != customerId.longValue())
        {
            return false;
        }
        return true;
    }
}
