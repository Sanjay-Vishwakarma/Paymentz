package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/9/15
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminModuleVO
{
    String moduleId;
    String moduleName;
    String moduleCreationTime;

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
    public String getModuleCreationTime()
    {
        return moduleCreationTime;
    }
    public void setModuleCreationTime(String moduleCreationTime)
    {
        this.moduleCreationTime = moduleCreationTime;
    }
}
