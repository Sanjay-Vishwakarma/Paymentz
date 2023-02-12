package com.fraud.at;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/28/14
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ATTransactionStatus
{
    UNDEFINED("0"),
    APPROVED("1"),
    PRE_AUTH("2"),
    SETTLED("3"),

    VOID("4"),
    REJECTED("5"),
    DECLINED_BY_BANK_GATWAY("6"),
    CHARGEBACK("7"),
    RETURN("8"),

    PENDING("9"),
    PASS_VALIDATION("10"),
    FAILED_VALIDATION("11"),
    REFUND("12"),
    APPROVED_REVIEW("13"),
    ABANDON("14");


    private String status;

    ATTransactionStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return status;
    }

}
