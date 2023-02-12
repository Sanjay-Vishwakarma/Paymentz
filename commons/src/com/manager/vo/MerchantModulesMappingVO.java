package com.manager.vo;

/**
 * Created by admin on 4/26/2016.
 */
public class MerchantModulesMappingVO
{
    String mappingId;
    String merchantId;
    String moduleId;
    String moduleName;
    String userId;
    String partnerId;
    String subModuleId;

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getMappingId()
    {
        return mappingId;
    }

    public void setMappingId(String mappingId)
    {
        this.mappingId = mappingId;
    }

    public String getMerchantId()
    {
        return merchantId;
    }

    public void setMerchantId(String merchantId)
    {
        this.merchantId = merchantId;
    }

    public String getModuleId()
    {
        return moduleId;
    }

    public void setModuleId(String moduleId)
    {
        this.moduleId = moduleId;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getSubModuleId()
    {
        return subModuleId;
    }

    public void setSubModuleId(String subModuleId)
    {
        this.subModuleId = subModuleId;
    }
}
