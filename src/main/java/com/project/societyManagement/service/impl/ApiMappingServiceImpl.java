package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.ApiPermissionMapping;
import com.project.societyManagement.queryBuilder.apiPermissionMapping.ApiPermissionMappingFilter;
import com.project.societyManagement.queryBuilder.apiPermissionMapping.ApiPermissionMappingQueryBuilder;
import com.project.societyManagement.service.ApiMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiMappingServiceImpl implements ApiMappingService {

    private final ApiPermissionMappingQueryBuilder apiPermissionMappingQueryBuilder;

    public ApiPermissionMapping findApiPermissionMappingByApiName(String api){
        ApiPermissionMappingFilter apiPermissionMappingFilter = new ApiPermissionMappingFilter();
        apiPermissionMappingFilter.setApi(api);
        ApiPermissionMapping apiPermissionMapping = apiPermissionMappingQueryBuilder.findById(apiPermissionMappingFilter);
        return apiPermissionMapping;
    }

}
