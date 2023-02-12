package com.manager.vo;

/**
 * Created by admin on 10/17/2015.
 */
public class PayIfeTableInfo
{
    private String tableId;
    private String tableName;
    private String tableAliasName;

    private String fieldName;
    private String description;
    private String dataType;
    private String enumValue;


    public String getTableId()
    {
        return tableId;
    }

    public void setTableId(String tableId)
    {
        this.tableId = tableId;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getTableAliasName()
    {
        return tableAliasName;
    }

    public void setTableAliasName(String tableAliasName)
    {
        this.tableAliasName = tableAliasName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDataType()
    {
        return dataType;
    }

    public void setDataType(String dataType)
    {
        this.dataType = dataType;
    }

    public String getEnumValue()
    {
        return enumValue;
    }

    public void setEnumValue(String enumValue)
    {
        this.enumValue = enumValue;
    }
}
