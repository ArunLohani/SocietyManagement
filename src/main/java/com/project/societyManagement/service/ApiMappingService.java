package com.project.societyManagement.service;

import com.project.societyManagement.entity.ApiPermissionMapping;

public interface ApiMappingService {
    public ApiPermissionMapping findApiPermissionMappingByApiName(String api);
}
