package com.directi.pg.core.jpbanktransfer;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Sagar Sonar on 21-April-2020.
 */
public class JPBankTransferVO extends CommResponseVO
{

    String nameId;
    String bankName;
    String shitenName;
    String kouzaType;
    String kouzaNm;
    String kouzaMeigi;
    String transferId;
    String data ;
    String isTest;
    String currency;
    String items;
    String bid;
    String tel;
    String email;
    String company;
    String result;
    String item;
    String shitenNm;

    public String getNameId()
    {
        return nameId;
    }

    public void setNameId(String nameId)
    {
        this.nameId = nameId;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getShitenName()
    {
        return shitenName;
    }

    public void setShitenName(String shitenName)
    {
        this.shitenName = shitenName;
    }

    public String getKouzaType()
    {
        return kouzaType;
    }

    public void setKouzaType(String kouzaType)
    {
        this.kouzaType = kouzaType;
    }

    public String getKouzaNm()
    {
        return kouzaNm;
    }

    public void setKouzaNm(String kouzaNm)
    {
        this.kouzaNm = kouzaNm;
    }

    public String getKouzaMeigi()
    {
        return kouzaMeigi;
    }

    public void setKouzaMeigi(String kouzaMeigi)
    {
        this.kouzaMeigi = kouzaMeigi;
    }

    public String getTransferId()
    {
        return transferId;
    }

    public void setTransferId(String transferId)
    {
        this.transferId = transferId;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getIsTest()
    {
        return isTest;
    }

    public void setIsTest(String isTest)
    {
        this.isTest = isTest;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getItems()
    {
        return items;
    }

    public void setItems(String items)
    {
        this.items = items;
    }

    public String getBid()
    {
        return bid;
    }

    public void setBid(String bid)
    {
        this.bid = bid;
    }

    public String getTel()
    {
        return tel;
    }

    public void setTel(String tel)
    {
        this.tel = tel;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public String getShitenNm()
    {
        return shitenNm;
    }

    public void setShitenNm(String shitenNm)
    {
        this.shitenNm = shitenNm;
    }


}
