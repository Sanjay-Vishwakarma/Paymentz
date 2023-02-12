package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 7/28/14
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Charge_subKeyword
{
    Amount("Amount"),
    Count("Count"),
    ConsumerCardAmt("ConsumerCardAmt") ,
    CommercialCardAmt("CommercialCardAmt"),
    ConsumerCardCount("ConsumerCardCount"),
    CommercialCardCount("CommercialCardCount");

    private String subKeyword;


    Charge_subKeyword(String subKeyword)
    {
     this.subKeyword=subKeyword;
    }

    public String toString()
    {
        return subKeyword;
    }
}
