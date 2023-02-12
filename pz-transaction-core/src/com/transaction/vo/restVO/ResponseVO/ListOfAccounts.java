package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 8/20/2016.
 */
@XmlRootElement(name = "bankaccounts")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListOfAccounts
{
   private List<BankAccount> bankaccount = new ArrayList<BankAccount>();

    public void addListOfAccounts(BankAccount bankAccount)
    {
        this.bankaccount.add(bankAccount);
    }
}
