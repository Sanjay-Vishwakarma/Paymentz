package com.manager.enums;

/**
 * Created by admin on 10/24/2015.
 */
public enum JoinEnum
{
    JOIN("JOIN"),
    LEFT("LEFT JOIN"),
    RIGHT("RIGHT JOIN");



    private String joinType;

    JoinEnum(String joinType)
    {
        this.joinType=joinType;
    }

    public String toString()
    {
        return joinType;
    }
}
