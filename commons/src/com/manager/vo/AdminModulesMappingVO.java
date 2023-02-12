package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User:sandip
 * Date: 3/9/15
 * Time: 6:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminModulesMappingVO
{
    String mappingId;
    String adminId;
    String moduleId;
    String moduleName;

    public String getMappingId()
    {
        return mappingId;
    }
    public void setMappingId(String mappingId)
    {
        this.mappingId = mappingId;
    }
    public String getAdminId()
    {
        return adminId;
    }
    public void setAdminId(String adminId)
    {
        this.adminId = adminId;
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
}
