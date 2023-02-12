package com.manager.vo.businessRuleVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Niket on 11/3/2015.
 */
@XmlRootElement(name = "RuleOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class RuleOperation
{
    private String id;

    private String operationType;

    private String inputName;

    private String regex;

    private boolean isMandatory;

    private String operator;

    private String filePath;

    private String value1;

    private String value2;

    private String comparator;

    private String dataType;

    private String enumValue;


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getOperationType()
    {
        return operationType;
    }

    public void setOperationType(String operationType)
    {
        this.operationType = operationType;
    }

    public String getInputName()
    {
        return inputName;
    }

    public void setInputName(String inputName)
    {
        this.inputName = inputName;
    }

    public String getRegex()
    {
        return regex;
    }

    public boolean isMandatory()
    {
        return isMandatory;
    }

    public void setMandatory(boolean isMandatory)
    {
        this.isMandatory = isMandatory;
    }

    public void setRegex(String regex)
    {
        this.regex = regex;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getValue1()
    {
        return value1;
    }

    public void setValue1(String value1)
    {
        this.value1 = value1;
    }

    public String getValue2()
    {
        return value2;
    }

    public void setValue2(String value2)
    {
        this.value2 = value2;
    }

    public String getComparator()
    {
        return comparator;
    }

    public void setComparator(String comparator)
    {
        this.comparator = comparator;
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
