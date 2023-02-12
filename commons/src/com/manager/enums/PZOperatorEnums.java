package com.manager.enums;

import com.directi.pg.Logger;

/**
 * Created by Pradeep on 12/09/2015.
 */
public enum  PZOperatorEnums
{
    EQUAL_TO("=="),
    NOT_EQUAL_TO("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),    //Verified
    GREATER_THAN_EQUALS_TO(">="),
    LESS_THAN_EQUALS_TO("<="),
    BETWEEN("=><="),
    CONTAINS("IN"),
    NOT_CONTAINS("NOT IN");

    private String pzOperatorEnums;

    PZOperatorEnums(String PZOperatorEnums)
    {
        this.pzOperatorEnums=PZOperatorEnums;
    }

    public String toString()
    {
        return pzOperatorEnums;
    }

    public static PZOperatorEnums getEnum(String value)
    {
        Logger logger= new Logger(PZOperatorEnums.class.getName());
        try
        {
            return PZOperatorEnums.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            logger.error("IllegalArgumentException"+value+",",iae);
            return null;
        }
    }
}
