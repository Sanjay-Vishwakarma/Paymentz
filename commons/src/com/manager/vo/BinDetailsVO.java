package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 4/2/15
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class BinDetailsVO
{
    private String first_six;
    private String last_four;
    private String name;
    private String bin_brand;
    private String bin_transaction_type;
    private String bin_card_type;
    private String bin_card_category;
    private String bin_usage_type;

    public String getFirst_six()
    {
        return first_six;
    }

    public void setFirst_six(String first_six)
    {
        this.first_six = first_six;
    }

    public String getLast_four()
    {
        return last_four;
    }

    public void setLast_four(String last_four)
    {
        this.last_four = last_four;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getBin_brand()
    {
        return bin_brand;
    }

    public void setBin_brand(String bin_brand)
    {
        this.bin_brand = bin_brand;
    }

    public String getBin_transaction_type()
    {
        return bin_transaction_type;
    }

    public void setBin_transaction_type(String bin_transaction_type)
    {
        this.bin_transaction_type = bin_transaction_type;
    }

    public String getBin_card_type()
    {
        return bin_card_type;
    }

    public void setBin_card_type(String bin_card_type)
    {
        this.bin_card_type = bin_card_type;
    }

    public String getBin_card_category()
    {
        return bin_card_category;
    }

    public void setBin_card_category(String bin_card_category)
    {
        this.bin_card_category = bin_card_category;
    }

    public String getBin_usage_type()
    {
        return bin_usage_type;
    }

    public void setBin_usage_type(String bin_usage_type)
    {
        this.bin_usage_type = bin_usage_type;
    }
}
