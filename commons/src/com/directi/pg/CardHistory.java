package com.directi.pg;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 5/4/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardHistory
{

    private int id;
    private String cardtype;
    private String firstSiz;
    private String lastFour;
    private int chargeback_count;
    private int reversal_count;


    private static final String REVERSE = "reversal";
    private static final String CHARGEBACK = "chargeback";



    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCardtype()
    {
        return cardtype;
    }

    public void setCardtype(String cardtype)
    {
        this.cardtype = cardtype;
    }

    public String getFirstSiz()
    {
        return firstSiz;
    }

    public void setFirstSiz(String firstSiz)
    {
        this.firstSiz = firstSiz;
    }

    public String getLastFour()
    {
        return lastFour;
    }

    public void setLastFour(String lastFour)
    {
        this.lastFour = lastFour;
    }

    public int getChargeback_count()
    {
        return chargeback_count;
    }

    public void setChargeback_count(int chargeback_count)
    {
        this.chargeback_count = chargeback_count;
    }

    public int getReversal_count()
    {
        return reversal_count;
    }

    public void setReversal_count(int reversal_count)
    {
        this.reversal_count = reversal_count;
    }

    public static void addCard(String cardNumber,String reason)
    {




    }


    public static CardHistory getCardDetails(String cardNumber)
    {
        return null;
    }




}
