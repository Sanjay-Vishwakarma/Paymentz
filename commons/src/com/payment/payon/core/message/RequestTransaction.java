package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("Transaction")
public class RequestTransaction extends Transaction
{
    @XStreamAsAttribute
    private String requestTimestamp ;

    @XStreamAlias("MerchantAccount")
    private MerchantAccount merchantAccount;
    @XStreamAlias("Payment")
    private Payment payment ;
    @XStreamAlias("CreditCardAccount")
    private CreditCardAccount creditCardAccount;
    @XStreamAlias("BankAccount")
    private BankAccount bankAccount;
    @XStreamAlias("VirtualAccount")
    private VirtualAccount virtualAccount;
    @XStreamAlias("Customer")
    private Customer customer ;
    @XStreamAlias("Authentication")
    private Authentication authentication ;
    @XStreamAlias("ReferencedTransaction")
    private ReferencedTransaction referencedTransaction;

    public String getRequestTimestamp()
    {
        return requestTimestamp;
    }

    public MerchantAccount getMerchantAccount()
    {
        return merchantAccount;
    }

    public ReferencedTransaction getReferencedTransaction()
    {
        return referencedTransaction;
    }

    public Payment getPayment()
    {
        return payment;
    }

    public CreditCardAccount getCreditCardAccount()
    {
        return creditCardAccount;
    }

    public BankAccount getBankAccount()
    {
        return bankAccount;
    }

    public VirtualAccount getVirtualAccount()
    {
        return virtualAccount;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public Recurrence getRecurrence()
    {
        return recurrence;
    }

    public ArrayList<Parameters> getParameters()
    {
        return parameters;
    }

    @XStreamAlias("Recurrence")
    private Recurrence recurrence ;

    public void setRequestTimestamp(String requestTimestamp)
    {
        this.requestTimestamp = requestTimestamp;
    }

    public void setMerchantAccount(MerchantAccount merchantAccount)
    {
        this.merchantAccount = merchantAccount;
    }

    public void setReferencedTransaction(ReferencedTransaction referencedTransaction)
    {
        this.referencedTransaction = referencedTransaction;
    }

    public void setBankAccount(BankAccount bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public void setVirtualAccount(VirtualAccount virtualAccount)
    {
        this.virtualAccount = virtualAccount;
    }

    public void setRecurrence(Recurrence recurrence)
    {
        this.recurrence = recurrence;
    }

    public void setParameters(ArrayList<Parameters> parameters)
    {
        this.parameters = parameters;
    }

    private ArrayList<Parameters> parameters;


    public void setMerchantAccountl(MerchantAccount merchantAccount)
    {
        this.merchantAccount = merchantAccount;
    }

    public void setPayment(Payment payment)
    {
        this.payment = payment;
    }

    public void setCreditCardAccount(CreditCardAccount creditCardAccount)
    {
        this.creditCardAccount = creditCardAccount;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }


}
