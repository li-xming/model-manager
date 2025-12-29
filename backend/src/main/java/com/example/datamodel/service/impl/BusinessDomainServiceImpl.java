package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.entity.BusinessDomain;
import com.example.datamodel.mapper.BusinessDomainMapper;
import com.example.datamodel.service.BusinessDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 业务域服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class BusinessDomainServiceImpl extends ServiceImpl<BusinessDomainMapper, BusinessDomain>
        implements BusinessDomainService {
}


