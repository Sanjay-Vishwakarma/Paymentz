package com.manager.enums;

/**
 * Created by NIKET on 10/9/2015.
 */
public enum RuleTypeEnum
{
    COMPARATOR("COMPARATOR"),
    DATABASE("DATABASE"),
    REGULAR_EXPRESSION("REGULAR_EXPRESSION"),
    FLAT_FILE("FLAT_FILE");

    private String ruleType;

    RuleTypeEnum(String ruleType)
    {
        this.ruleType=ruleType;
    }

    public String toString()
    {
        return ruleType;
    }

}
