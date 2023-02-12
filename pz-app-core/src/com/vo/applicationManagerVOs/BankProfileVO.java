package com.vo.applicationManagerVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/20/15
 * Time: 5:44 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name ="BankProfileVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class BankProfileVO
{
    @XmlElement(name = "application_id")
    String application_id;

    @XmlElement(name = "currencyrequested_productssold")
    String currencyrequested_productssold;

    @XmlElement(name = "currencyrequested_bankaccount")
    String currencyrequested_bankaccount;

    @XmlElement(name = "bankinfo_bic")
    String bankinfo_bic;

    @XmlElement(name = "bankinfo_bank_name")
    String bankinfo_bank_name;

    @XmlElement(name = "bankinfo_bankaddress")
    String bankinfo_bankaddress;

    @XmlElement(name = "bankinfo_bankTelCC")
    String bankinfo_bankTelCC;
    @XmlElement(name = "bankinfo_bankphonenumber")
    String bankinfo_bankphonenumber;

    @XmlElement(name = "bankinfo_aba_routingcode")
    String bankinfo_aba_routingcode;

    @XmlElement(name = "bankinfo_accountholder")
    String bankinfo_accountholder;

    @XmlElement(name = "bankinfo_IBAN")
    String bankinfo_IBAN;

    @XmlElement(name = "bankinfo_accountnumber")
    String bankinfo_accountnumber;

    @XmlElement(name = "bankinfo_currency")
    String bankinfo_currency;

    @XmlElement(name = "currency_products_INR")
    String currency_products_INR;

    @XmlElement(name = "currency_products_USD")
    String currency_products_USD;

    @XmlElement(name = "currency_products_EUR")
    String currency_products_EUR;

    @XmlElement(name = "currency_products_GBP")
    String currency_products_GBP;

    @XmlElement(name = "currency_products_JPY")
    String currency_products_JPY;

    @XmlElement(name = "currency_products_PEN")
    String currency_products_PEN;

    @XmlElement(name = "currency_payments_INR")
    String currency_payments_INR;

    @XmlElement(name = "currency_payments_USD")
    String currency_payments_USD;

    @XmlElement(name = "currency_payments_EUR")
    String currency_payments_EUR;

    @XmlElement(name = "currency_payments_GBP")
    String currency_payments_GBP;

    @XmlElement(name = "currency_payments_JPY")
    String currency_payments_JPY;

    @XmlElement(name = "currency_payments_PEN")
    String currency_payments_PEN;

    @XmlElement(name = "bank_account_currencies")
    String bank_account_currencies;

    @XmlElement(name = "product_sold_currencies")
    String product_sold_currencies;

    //ADD new
    @XmlElement(name = "aquirer")
    String aquirer;
    @XmlElement(name = "reasonaquirer")
    String reasonaquirer;

    @XmlElement(name = "customer_trans_data")
    String customer_trans_data;


    //Add new
    @XmlElement(name = "currency_products_HKD")
    String currency_products_HKD;
    @XmlElement(name = "currency_products_AUD")
    String currency_products_AUD;
    @XmlElement(name = "currency_products_CAD")
    String currency_products_CAD;
    @XmlElement(name = "currency_products_DKK")
    String currency_products_DKK;
    @XmlElement(name = "currency_products_SEK")
    String currency_products_SEK;
    @XmlElement(name = "currency_products_NOK")
    String currency_products_NOK;

    //Add new
    @XmlElement(name = "currency_payments_HKD")
    String currency_payments_HKD;
    @XmlElement(name = "currency_payments_AUD")
    String currency_payments_AUD;
    @XmlElement(name = "currency_payments_CAD")
    String currency_payments_CAD;
    @XmlElement(name = "currency_payments_DKK")
    String currency_payments_DKK;
    @XmlElement(name = "currency_payments_SEK")
    String currency_payments_SEK;
    @XmlElement(name = "currency_payments_NOK")
    String currency_payments_NOK;
    @XmlElement(name = "bankinfo_contactperson")
    String bankinfo_contactperson;


    @XmlElement(name = "salesvolume_lastmonth")
    String salesvolume_lastmonth;

    @XmlElement(name = "salesvolume_2monthsago")
    String salesvolume_2monthsago;

    @XmlElement(name = "salesvolume_3monthsago")
    String salesvolume_3monthsago;

    @XmlElement(name = "salesvolume_4monthsago")
    String salesvolume_4monthsago;

    @XmlElement(name = "salesvolume_5monthsago")
    String salesvolume_5monthsago;

    @XmlElement(name = "salesvolume_6monthsago")
    String salesvolume_6monthsago;

    @XmlElement(name = "salesvolume_12monthsago")
    String salesvolume_12monthsago;

    @XmlElement(name = "salesvolume_year2")
    String salesvolume_year2;

    @XmlElement(name = "salesvolume_year3")
    String salesvolume_year3;

    @XmlElement(name = "numberoftransactions_lastmonth")
    String numberoftransactions_lastmonth;

    @XmlElement(name = "numberoftransactions_2monthsago")
    String numberoftransactions_2monthsago;

    @XmlElement(name = "numberoftransactions_3monthsago")
    String numberoftransactions_3monthsago;

    @XmlElement(name = "numberoftransactions_4monthsago")
    String numberoftransactions_4monthsago;

    @XmlElement(name = "numberoftransactions_5monthsago")
    String numberoftransactions_5monthsago;

    @XmlElement(name = "numberoftransactions_6monthsago")
    String numberoftransactions_6monthsago;

    @XmlElement(name = "numberoftransactions_12monthsago")
    String numberoftransactions_12monthsago;

    @XmlElement(name = "numberoftransactions_year2")
    String numberoftransactions_year2;

    @XmlElement(name = "numberoftransactions_year3")
    String numberoftransactions_year3;

    @XmlElement(name = "chargebackvolume_lastmonth")
    String chargebackvolume_lastmonth;

    @XmlElement(name = "chargebackvolume_2monthsago")
    String chargebackvolume_2monthsago;

    @XmlElement(name = "chargebackvolume_3monthsago")
    String chargebackvolume_3monthsago;

    @XmlElement(name = "chargebackvolume_4monthsago")
    String chargebackvolume_4monthsago;

    @XmlElement(name = "chargebackvolume_5monthsago")
    String chargebackvolume_5monthsago;

    @XmlElement(name = "chargebackvolume_6monthsago")
    String chargebackvolume_6monthsago;

    @XmlElement(name = "chargebackvolume_12monthsago")
    String chargebackvolume_12monthsago;

    @XmlElement(name = "chargebackvolume_year2")
    String chargebackvolume_year2;

    @XmlElement(name = "chargebackvolume_year3")
    String chargebackvolume_year3;

    @XmlElement(name = "numberofchargebacks_lastmonth")
    String numberofchargebacks_lastmonth;

    @XmlElement(name = "numberofchargebacks_2monthsago")
    String numberofchargebacks_2monthsago;

    @XmlElement(name = "numberofchargebacks_3monthsago")
    String numberofchargebacks_3monthsago;

    @XmlElement(name = "numberofchargebacks_4monthsago")
    String numberofchargebacks_4monthsago;

    @XmlElement(name = "numberofchargebacks_5monthsago")
    String numberofchargebacks_5monthsago;

    @XmlElement(name = "numberofchargebacks_6monthsago")
    String numberofchargebacks_6monthsago;

    @XmlElement(name = "numberofchargebacks_12monthsago")
    String numberofchargebacks_12monthsago;

    @XmlElement(name = "numberofchargebacks_year2")
    String numberofchargebacks_year2;

    @XmlElement(name = "numberofchargebacks_year3")
    String numberofchargebacks_year3;

    @XmlElement(name = "refundsvolume_lastmonth")
    String refundsvolume_lastmonth;

    @XmlElement(name = "refundsvolume_2monthsago")
    String refundsvolume_2monthsago;

    @XmlElement(name = "refundsvolume_3monthsago")
    String refundsvolume_3monthsago;

    @XmlElement(name = "refundsvolume_4monthsago")
    String refundsvolume_4monthsago;

    @XmlElement(name = "refundsvolume_5monthsago")
    String refundsvolume_5monthsago;

    @XmlElement(name = "refundsvolume_6monthsago")
    String refundsvolume_6monthsago;

    @XmlElement(name = "refundsvolume_12monthsago")
    String refundsvolume_12monthsago;

    @XmlElement(name = "refundsvolume_year2")
    String refundsvolume_year2;

    @XmlElement(name = "refundsvolume_year3")
    String refundsvolume_year3;

    @XmlElement(name = "numberofrefunds_lastmonth")
    String numberofrefunds_lastmonth;

    @XmlElement(name = "numberofrefunds_2monthsago")
    String numberofrefunds_2monthsago;

    @XmlElement(name = "numberofrefunds_3monthsago")
    String numberofrefunds_3monthsago;

    @XmlElement(name = "numberofrefunds_4monthsago")
    String numberofrefunds_4monthsago;

    @XmlElement(name = "numberofrefunds_5monthsago")
    String numberofrefunds_5monthsago;

    @XmlElement(name = "numberofrefunds_6monthsago")
    String numberofrefunds_6monthsago;

    @XmlElement(name = "numberofrefunds_12monthsago")
    String numberofrefunds_12monthsago;

    @XmlElement(name = "numberofrefunds_year2")
    String numberofrefunds_year2;

    @XmlElement(name = "numberofrefunds_year3")
    String numberofrefunds_year3;

    @XmlElement(name = "chargebackratio_lastmonth")
    String chargebackratio_lastmonth;

    @XmlElement(name = "chargebackratio_2monthsago")
    String chargebackratio_2monthsago;

    @XmlElement(name = "chargebackratio_3monthsago")
    String chargebackratio_3monthsago;

    @XmlElement(name = "chargebackratio_4monthsago")
    String chargebackratio_4monthsago;

    @XmlElement(name = "chargebackratio_5monthsago")
    String chargebackratio_5monthsago;

    @XmlElement(name = "chargebackratio_6monthsago")
    String chargebackratio_6monthsago;

    @XmlElement(name = "chargebackratio_12monthsago")
    String chargebackratio_12monthsago;

    @XmlElement(name = "chargebackratio_year2")
    String chargebackratio_year2;

    @XmlElement(name = "chargebackratio_year3")
    String chargebackratio_year3;

    @XmlElement(name = "refundratio_lastmonth")
    String refundratio_lastmonth;

    @XmlElement(name = "refundratio_2monthsago")
    String refundratio_2monthsago;

    @XmlElement(name = "refundratio_3monthsago")
    String refundratio_3monthsago;

    @XmlElement(name = "refundratio_4monthsago")
    String refundratio_4monthsago;

    @XmlElement(name = "refundratio_5monthsago")
    String refundratio_5monthsago;

    @XmlElement(name = "refundratio_6monthsago")
    String refundratio_6monthsago;

    @XmlElement(name = "refundratio_12monthsago")
    String refundratio_12monthsago;

    @XmlElement(name = "refundratio_year2")
    String refundratio_year2;

    @XmlElement(name = "refundratio_year3")
    String refundratio_year3;

    @XmlElement(name = "iscurrencywisebankinfo")
    String iscurrencywisebankinfo;

    @XmlElement(name = "bankProfileSaved")
    String bankProfileSaved;

    @XmlElement(name = "isProcessingHistory")
    String isProcessingHistory;

    @XmlElement(name = "currency")
    String currency;

    @XmlElement(name = "currencywisebankinfo_id")
    String currencywisebankinfo_id;

    @XmlElement(name = "processinghistory_id")
    String processinghistory_id;

    @XmlElement(name = "processinghistory_creation_time")
    String processinghistory_creation_time;

    @XmlElement(name = "processinghistory_updation_time")
    String processinghistory_updation_time;

    public String getProcessinghistory_creation_time()
    {
        return processinghistory_creation_time;
    }

    public String getProduct_sold_currencies()
    {
        return product_sold_currencies;
    }

    public void setProduct_sold_currencies(String product_sold_currencies)
    {
        this.product_sold_currencies = product_sold_currencies;
    }

    public String getBank_account_currencies()
    {
        return bank_account_currencies;
    }

    public void setBank_account_currencies(String bank_account_currencies)
    {
        this.bank_account_currencies = bank_account_currencies;
    }

    public void setProcessinghistory_creation_time(String processinghistory_creation_time)
    {
        this.processinghistory_creation_time = processinghistory_creation_time;
    }

    public String getProcessinghistory_updation_time()
    {
        return processinghistory_updation_time;
    }

    public void setProcessinghistory_updation_time(String processinghistory_updation_time)
    {
        this.processinghistory_updation_time = processinghistory_updation_time;
    }

    public String getProcessinghistory_id()
    {
        return processinghistory_id;
    }

    public void setProcessinghistory_id(String processinghistory_id)
    {
        this.processinghistory_id = processinghistory_id;
    }

    public String getCurrencywisebankinfo_id()
    {
        return currencywisebankinfo_id;
    }

    public void setCurrencywisebankinfo_id(String currencywisebankinfo_id)
    {
        this.currencywisebankinfo_id = currencywisebankinfo_id;
    }

    public String getApplication_id()
    {
        return application_id;
    }

    public void setApplication_id(String application_id)
    {
        this.application_id = application_id;
    }

    public String getCurrencyrequested_productssold()
    {
        return currencyrequested_productssold;
    }

    public void setCurrencyrequested_productssold(String currencyrequested_productssold)
    {
        this.currencyrequested_productssold = currencyrequested_productssold;
    }

    public String getCurrencyrequested_bankaccount()
    {
        return currencyrequested_bankaccount;
    }

    public void setCurrencyrequested_bankaccount(String currencyrequested_bankaccount)
    {
        this.currencyrequested_bankaccount = currencyrequested_bankaccount;
    }

    public String getBankinfo_bic()
    {
        return bankinfo_bic;
    }

    public void setBankinfo_bic(String bankinfo_bic)
    {
        this.bankinfo_bic = bankinfo_bic;
    }

    public String getBankinfo_bank_name()
    {
        return bankinfo_bank_name;
    }

    public void setBankinfo_bank_name(String bankinfo_bank_name)
    {
        this.bankinfo_bank_name = bankinfo_bank_name;
    }

    public String getBankinfo_bankaddress()
    {
        return bankinfo_bankaddress;
    }

    public void setBankinfo_bankaddress(String bankinfo_bankaddress)
    {
        this.bankinfo_bankaddress = bankinfo_bankaddress;
    }

    public String getBankinfo_bankTelCC()
    {
        return bankinfo_bankTelCC;
    }

    public void setBankinfo_bankTelCC(String bankinfo_bankTelCC)
    {
        this.bankinfo_bankTelCC = bankinfo_bankTelCC;
    }

    public String getBankinfo_bankphonenumber()
    {
        return bankinfo_bankphonenumber;
    }

    public void setBankinfo_bankphonenumber(String bankinfo_bankphonenumber)
    {
        this.bankinfo_bankphonenumber = bankinfo_bankphonenumber;
    }

    public String getBankinfo_aba_routingcode()
    {
        return bankinfo_aba_routingcode;
    }

    public void setBankinfo_aba_routingcode(String bankinfo_aba_routingcode)
    {
        this.bankinfo_aba_routingcode = bankinfo_aba_routingcode;
    }

    public String getBankinfo_accountholder()
    {
        return bankinfo_accountholder;
    }

    public void setBankinfo_accountholder(String bankinfo_accountholder)
    {
        this.bankinfo_accountholder = bankinfo_accountholder;
    }

    public String getBankinfo_IBAN()
    {
        return bankinfo_IBAN;
    }

    public void setBankinfo_IBAN(String bankinfo_IBAN)
    {
        this.bankinfo_IBAN = bankinfo_IBAN;
    }

    public String getBankinfo_contactperson()
    {
        return bankinfo_contactperson;
    }

    public void setBankinfo_contactperson(String bankinfo_contactperson)
    {
        this.bankinfo_contactperson = bankinfo_contactperson;
    }

    public String getBankinfo_accountnumber()
    {
        return bankinfo_accountnumber;
    }

    public void setBankinfo_accountnumber(String bankinfo_accountnumber)
    {
        this.bankinfo_accountnumber = bankinfo_accountnumber;
    }

    public String getSalesvolume_lastmonth()
    {
        return salesvolume_lastmonth;
    }

    public void setSalesvolume_lastmonth(String salesvolume_lastmonth)
    {
        this.salesvolume_lastmonth = salesvolume_lastmonth;
    }

    public String getSalesvolume_2monthsago()
    {
        return salesvolume_2monthsago;
    }

    public void setSalesvolume_2monthsago(String salesvolume_2monthsago)
    {
        this.salesvolume_2monthsago = salesvolume_2monthsago;
    }

    public String getSalesvolume_3monthsago()
    {
        return salesvolume_3monthsago;
    }

    public void setSalesvolume_3monthsago(String salesvolume_3monthsago)
    {
        this.salesvolume_3monthsago = salesvolume_3monthsago;
    }

    public String getSalesvolume_4monthsago()
    {
        return salesvolume_4monthsago;
    }

    public void setSalesvolume_4monthsago(String salesvolume_4monthsago)
    {
        this.salesvolume_4monthsago = salesvolume_4monthsago;
    }

    public String getSalesvolume_5monthsago()
    {
        return salesvolume_5monthsago;
    }

    public void setSalesvolume_5monthsago(String salesvolume_5monthsago)
    {
        this.salesvolume_5monthsago = salesvolume_5monthsago;
    }

    public String getSalesvolume_6monthsago()
    {
        return salesvolume_6monthsago;
    }

    public void setSalesvolume_6monthsago(String salesvolume_6monthsago)
    {
        this.salesvolume_6monthsago = salesvolume_6monthsago;
    }

    public String getSalesvolume_12monthsago()
    {
        return salesvolume_12monthsago;
    }

    public void setSalesvolume_12monthsago(String salesvolume_12monthsago)
    {
        this.salesvolume_12monthsago = salesvolume_12monthsago;
    }

    public String getSalesvolume_year2()
    {
        return salesvolume_year2;
    }

    public void setSalesvolume_year2(String salesvolume_year2)
    {
        this.salesvolume_year2 = salesvolume_year2;
    }

    public String getSalesvolume_year3()
    {
        return salesvolume_year3;
    }

    public void setSalesvolume_year3(String salesvolume_year3)
    {
        this.salesvolume_year3 = salesvolume_year3;
    }

    public String getNumberoftransactions_lastmonth()
    {
        return numberoftransactions_lastmonth;
    }

    public void setNumberoftransactions_lastmonth(String numberoftransactions_lastmonth)
    {
        this.numberoftransactions_lastmonth = numberoftransactions_lastmonth;
    }

    public String getNumberoftransactions_2monthsago()
    {
        return numberoftransactions_2monthsago;
    }

    public void setNumberoftransactions_2monthsago(String numberoftransactions_2monthsago)
    {
        this.numberoftransactions_2monthsago = numberoftransactions_2monthsago;
    }

    public String getNumberoftransactions_3monthsago()
    {
        return numberoftransactions_3monthsago;
    }

    public void setNumberoftransactions_3monthsago(String numberoftransactions_3monthsago)
    {
        this.numberoftransactions_3monthsago = numberoftransactions_3monthsago;
    }

    public String getNumberoftransactions_4monthsago()
    {
        return numberoftransactions_4monthsago;
    }

    public void setNumberoftransactions_4monthsago(String numberoftransactions_4monthsago)
    {
        this.numberoftransactions_4monthsago = numberoftransactions_4monthsago;
    }

    public String getNumberoftransactions_5monthsago()
    {
        return numberoftransactions_5monthsago;
    }

    public void setNumberoftransactions_5monthsago(String numberoftransactions_5monthsago)
    {
        this.numberoftransactions_5monthsago = numberoftransactions_5monthsago;
    }

    public String getNumberoftransactions_6monthsago()
    {
        return numberoftransactions_6monthsago;
    }

    public void setNumberoftransactions_6monthsago(String numberoftransactions_6monthsago)
    {
        this.numberoftransactions_6monthsago = numberoftransactions_6monthsago;
    }

    public String getNumberoftransactions_12monthsago()
    {
        return numberoftransactions_12monthsago;
    }

    public void setNumberoftransactions_12monthsago(String numberoftransactions_12monthsago)
    {
        this.numberoftransactions_12monthsago = numberoftransactions_12monthsago;
    }

    public String getNumberoftransactions_year2()
    {
        return numberoftransactions_year2;
    }

    public void setNumberoftransactions_year2(String numberoftransactions_year2)
    {
        this.numberoftransactions_year2 = numberoftransactions_year2;
    }

    public String getNumberoftransactions_year3()
    {
        return numberoftransactions_year3;
    }

    public void setNumberoftransactions_year3(String numberoftransactions_year3)
    {
        this.numberoftransactions_year3 = numberoftransactions_year3;
    }

    public String getChargebackvolume_lastmonth()
    {
        return chargebackvolume_lastmonth;
    }

    public void setChargebackvolume_lastmonth(String chargebackvolume_lastmonth)
    {
        this.chargebackvolume_lastmonth = chargebackvolume_lastmonth;
    }

    public String getChargebackvolume_2monthsago()
    {
        return chargebackvolume_2monthsago;
    }

    public void setChargebackvolume_2monthsago(String chargebackvolume_2monthsago)
    {
        this.chargebackvolume_2monthsago = chargebackvolume_2monthsago;
    }

    public String getChargebackvolume_3monthsago()
    {
        return chargebackvolume_3monthsago;
    }

    public void setChargebackvolume_3monthsago(String chargebackvolume_3monthsago)
    {
        this.chargebackvolume_3monthsago = chargebackvolume_3monthsago;
    }

    public String getChargebackvolume_4monthsago()
    {
        return chargebackvolume_4monthsago;
    }

    public void setChargebackvolume_4monthsago(String chargebackvolume_4monthsago)
    {
        this.chargebackvolume_4monthsago = chargebackvolume_4monthsago;
    }

    public String getChargebackvolume_5monthsago()
    {
        return chargebackvolume_5monthsago;
    }

    public void setChargebackvolume_5monthsago(String chargebackvolume_5monthsago)
    {
        this.chargebackvolume_5monthsago = chargebackvolume_5monthsago;
    }

    public String getChargebackvolume_6monthsago()
    {
        return chargebackvolume_6monthsago;
    }

    public void setChargebackvolume_6monthsago(String chargebackvolume_6monthsago)
    {
        this.chargebackvolume_6monthsago = chargebackvolume_6monthsago;
    }

    public String getChargebackvolume_12monthsago()
    {
        return chargebackvolume_12monthsago;
    }

    public void setChargebackvolume_12monthsago(String chargebackvolume_12monthsago)
    {
        this.chargebackvolume_12monthsago = chargebackvolume_12monthsago;
    }

    public String getChargebackvolume_year2()
    {
        return chargebackvolume_year2;
    }

    public void setChargebackvolume_year2(String chargebackvolume_year2)
    {
        this.chargebackvolume_year2 = chargebackvolume_year2;
    }

    public String getChargebackvolume_year3()
    {
        return chargebackvolume_year3;
    }

    public void setChargebackvolume_year3(String chargebackvolume_year3)
    {
        this.chargebackvolume_year3 = chargebackvolume_year3;
    }

    public String getNumberofchargebacks_lastmonth()
    {
        return numberofchargebacks_lastmonth;
    }

    public void setNumberofchargebacks_lastmonth(String numberofchargebacks_lastmonth)
    {
        this.numberofchargebacks_lastmonth = numberofchargebacks_lastmonth;
    }

    public String getNumberofchargebacks_2monthsago()
    {
        return numberofchargebacks_2monthsago;
    }

    public void setNumberofchargebacks_2monthsago(String numberofchargebacks_2monthsago)
    {
        this.numberofchargebacks_2monthsago = numberofchargebacks_2monthsago;
    }

    public String getNumberofchargebacks_3monthsago()
    {
        return numberofchargebacks_3monthsago;
    }

    public void setNumberofchargebacks_3monthsago(String numberofchargebacks_3monthsago)
    {
        this.numberofchargebacks_3monthsago = numberofchargebacks_3monthsago;
    }

    public String getNumberofchargebacks_4monthsago()
    {
        return numberofchargebacks_4monthsago;
    }

    public void setNumberofchargebacks_4monthsago(String numberofchargebacks_4monthsago)
    {
        this.numberofchargebacks_4monthsago = numberofchargebacks_4monthsago;
    }

    public String getNumberofchargebacks_5monthsago()
    {
        return numberofchargebacks_5monthsago;
    }

    public void setNumberofchargebacks_5monthsago(String numberofchargebacks_5monthsago)
    {
        this.numberofchargebacks_5monthsago = numberofchargebacks_5monthsago;
    }

    public String getNumberofchargebacks_6monthsago()
    {
        return numberofchargebacks_6monthsago;
    }

    public void setNumberofchargebacks_6monthsago(String numberofchargebacks_6monthsago)
    {
        this.numberofchargebacks_6monthsago = numberofchargebacks_6monthsago;
    }

    public String getNumberofchargebacks_12monthsago()
    {
        return numberofchargebacks_12monthsago;
    }

    public void setNumberofchargebacks_12monthsago(String numberofchargebacks_12monthsago)
    {
        this.numberofchargebacks_12monthsago = numberofchargebacks_12monthsago;
    }

    public String getNumberofchargebacks_year2()
    {
        return numberofchargebacks_year2;
    }

    public void setNumberofchargebacks_year2(String numberofchargebacks_year2)
    {
        this.numberofchargebacks_year2 = numberofchargebacks_year2;
    }

    public String getNumberofchargebacks_year3()
    {
        return numberofchargebacks_year3;
    }

    public void setNumberofchargebacks_year3(String numberofchargebacks_year3)
    {
        this.numberofchargebacks_year3 = numberofchargebacks_year3;
    }

    public String getRefundsvolume_lastmonth()
    {
        return refundsvolume_lastmonth;
    }

    public void setRefundsvolume_lastmonth(String refundsvolume_lastmonth)
    {
        this.refundsvolume_lastmonth = refundsvolume_lastmonth;
    }

    public String getRefundsvolume_2monthsago()
    {
        return refundsvolume_2monthsago;
    }

    public void setRefundsvolume_2monthsago(String refundsvolume_2monthsago)
    {
        this.refundsvolume_2monthsago = refundsvolume_2monthsago;
    }

    public String getRefundsvolume_3monthsago()
    {
        return refundsvolume_3monthsago;
    }

    public void setRefundsvolume_3monthsago(String refundsvolume_3monthsago)
    {
        this.refundsvolume_3monthsago = refundsvolume_3monthsago;
    }

    public String getRefundsvolume_4monthsago()
    {
        return refundsvolume_4monthsago;
    }

    public void setRefundsvolume_4monthsago(String refundsvolume_4monthsago)
    {
        this.refundsvolume_4monthsago = refundsvolume_4monthsago;
    }

    public String getRefundsvolume_5monthsago()
    {
        return refundsvolume_5monthsago;
    }

    public void setRefundsvolume_5monthsago(String refundsvolume_5monthsago)
    {
        this.refundsvolume_5monthsago = refundsvolume_5monthsago;
    }

    public String getRefundsvolume_6monthsago()
    {
        return refundsvolume_6monthsago;
    }

    public void setRefundsvolume_6monthsago(String refundsvolume_6monthsago)
    {
        this.refundsvolume_6monthsago = refundsvolume_6monthsago;
    }

    public String getRefundsvolume_12monthsago()
    {
        return refundsvolume_12monthsago;
    }

    public void setRefundsvolume_12monthsago(String refundsvolume_12monthsago)
    {
        this.refundsvolume_12monthsago = refundsvolume_12monthsago;
    }

    public String getRefundsvolume_year2()
    {
        return refundsvolume_year2;
    }

    public void setRefundsvolume_year2(String refundsvolume_year2)
    {
        this.refundsvolume_year2 = refundsvolume_year2;
    }

    public String getRefundsvolume_year3()
    {
        return refundsvolume_year3;
    }

    public void setRefundsvolume_year3(String refundsvolume_year3)
    {
        this.refundsvolume_year3 = refundsvolume_year3;
    }

    public String getNumberofrefunds_lastmonth()
    {
        return numberofrefunds_lastmonth;
    }

    public void setNumberofrefunds_lastmonth(String numberofrefunds_lastmonth)
    {
        this.numberofrefunds_lastmonth = numberofrefunds_lastmonth;
    }

    public String getNumberofrefunds_2monthsago()
    {
        return numberofrefunds_2monthsago;
    }

    public void setNumberofrefunds_2monthsago(String numberofrefunds_2monthsago)
    {
        this.numberofrefunds_2monthsago = numberofrefunds_2monthsago;
    }

    public String getNumberofrefunds_3monthsago()
    {
        return numberofrefunds_3monthsago;
    }

    public void setNumberofrefunds_3monthsago(String numberofrefunds_3monthsago)
    {
        this.numberofrefunds_3monthsago = numberofrefunds_3monthsago;
    }

    public String getNumberofrefunds_4monthsago()
    {
        return numberofrefunds_4monthsago;
    }

    public void setNumberofrefunds_4monthsago(String numberofrefunds_4monthsago)
    {
        this.numberofrefunds_4monthsago = numberofrefunds_4monthsago;
    }

    public String getNumberofrefunds_5monthsago()
    {
        return numberofrefunds_5monthsago;
    }

    public void setNumberofrefunds_5monthsago(String numberofrefunds_5monthsago)
    {
        this.numberofrefunds_5monthsago = numberofrefunds_5monthsago;
    }

    public String getNumberofrefunds_6monthsago()
    {
        return numberofrefunds_6monthsago;
    }

    public void setNumberofrefunds_6monthsago(String numberofrefunds_6monthsago)
    {
        this.numberofrefunds_6monthsago = numberofrefunds_6monthsago;
    }

    public String getNumberofrefunds_12monthsago()
    {
        return numberofrefunds_12monthsago;
    }

    public void setNumberofrefunds_12monthsago(String numberofrefunds_12monthsago)
    {
        this.numberofrefunds_12monthsago = numberofrefunds_12monthsago;
    }

    public String getNumberofrefunds_year2()
    {
        return numberofrefunds_year2;
    }

    public void setNumberofrefunds_year2(String numberofrefunds_year2)
    {
        this.numberofrefunds_year2 = numberofrefunds_year2;
    }

    public String getNumberofrefunds_year3()
    {
        return numberofrefunds_year3;
    }

    public void setNumberofrefunds_year3(String numberofrefunds_year3)
    {
        this.numberofrefunds_year3 = numberofrefunds_year3;
    }

    public String getChargebackratio_lastmonth()
    {
        return chargebackratio_lastmonth;
    }

    public void setChargebackratio_lastmonth(String chargebackratio_lastmonth)
    {
        this.chargebackratio_lastmonth = chargebackratio_lastmonth;
    }

    public String getChargebackratio_2monthsago()
    {
        return chargebackratio_2monthsago;
    }

    public void setChargebackratio_2monthsago(String chargebackratio_2monthsago)
    {
        this.chargebackratio_2monthsago = chargebackratio_2monthsago;
    }

    public String getChargebackratio_3monthsago()
    {
        return chargebackratio_3monthsago;
    }

    public void setChargebackratio_3monthsago(String chargebackratio_3monthsago)
    {
        this.chargebackratio_3monthsago = chargebackratio_3monthsago;
    }

    public String getChargebackratio_4monthsago()
    {
        return chargebackratio_4monthsago;
    }

    public void setChargebackratio_4monthsago(String chargebackratio_4monthsago)
    {
        this.chargebackratio_4monthsago = chargebackratio_4monthsago;
    }

    public String getChargebackratio_5monthsago()
    {
        return chargebackratio_5monthsago;
    }

    public void setChargebackratio_5monthsago(String chargebackratio_5monthsago)
    {
        this.chargebackratio_5monthsago = chargebackratio_5monthsago;
    }

    public String getChargebackratio_6monthsago()
    {
        return chargebackratio_6monthsago;
    }

    public void setChargebackratio_6monthsago(String chargebackratio_6monthsago)
    {
        this.chargebackratio_6monthsago = chargebackratio_6monthsago;
    }

    public String getChargebackratio_12monthsago()
    {
        return chargebackratio_12monthsago;
    }

    public void setChargebackratio_12monthsago(String chargebackratio_12monthsago)
    {
        this.chargebackratio_12monthsago = chargebackratio_12monthsago;
    }

    public String getChargebackratio_year2()
    {
        return chargebackratio_year2;
    }

    public void setChargebackratio_year2(String chargebackratio_year2)
    {
        this.chargebackratio_year2 = chargebackratio_year2;
    }

    public String getChargebackratio_year3()
    {
        return chargebackratio_year3;
    }

    public void setChargebackratio_year3(String chargebackratio_year3)
    {
        this.chargebackratio_year3 = chargebackratio_year3;
    }

    public String getRefundratio_lastmonth()
    {
        return refundratio_lastmonth;
    }

    public void setRefundratio_lastmonth(String refundratio_lastmonth)
    {
        this.refundratio_lastmonth = refundratio_lastmonth;
    }

    public String getRefundratio_2monthsago()
    {
        return refundratio_2monthsago;
    }

    public void setRefundratio_2monthsago(String refundratio_2monthsago)
    {
        this.refundratio_2monthsago = refundratio_2monthsago;
    }

    public String getRefundratio_3monthsago()
    {
        return refundratio_3monthsago;
    }

    public void setRefundratio_3monthsago(String refundratio_3monthsago)
    {
        this.refundratio_3monthsago = refundratio_3monthsago;
    }

    public String getRefundratio_4monthsago()
    {
        return refundratio_4monthsago;
    }

    public void setRefundratio_4monthsago(String refundratio_4monthsago)
    {
        this.refundratio_4monthsago = refundratio_4monthsago;
    }

    public String getRefundratio_5monthsago()
    {
        return refundratio_5monthsago;
    }

    public void setRefundratio_5monthsago(String refundratio_5monthsago)
    {
        this.refundratio_5monthsago = refundratio_5monthsago;
    }

    public String getRefundratio_6monthsago()
    {
        return refundratio_6monthsago;
    }

    public void setRefundratio_6monthsago(String refundratio_6monthsago)
    {
        this.refundratio_6monthsago = refundratio_6monthsago;
    }

    public String getRefundratio_12monthsago()
    {
        return refundratio_12monthsago;
    }

    public void setRefundratio_12monthsago(String refundratio_12monthsago)
    {
        this.refundratio_12monthsago = refundratio_12monthsago;
    }

    public String getRefundratio_year2()
    {
        return refundratio_year2;
    }

    public void setRefundratio_year2(String refundratio_year2)
    {
        this.refundratio_year2 = refundratio_year2;
    }

    public String getRefundratio_year3()
    {
        return refundratio_year3;
    }

    public void setRefundratio_year3(String refundratio_year3)
    {
        this.refundratio_year3 = refundratio_year3;
    }

    public String getCurrency_products_USD()
    {
        return currency_products_USD;
    }

    public void setCurrency_products_USD(String currency_products_USD)
    {
        this.currency_products_USD = currency_products_USD;
    }

    public String getCurrency_products_EUR()
    {
        return currency_products_EUR;
    }

    public void setCurrency_products_EUR(String currency_products_EUR)
    {
        this.currency_products_EUR = currency_products_EUR;
    }

    public String getCurrency_products_GBP()
    {
        return currency_products_GBP;
    }

    public void setCurrency_products_GBP(String currency_products_GBP)
    {
        this.currency_products_GBP = currency_products_GBP;
    }

    public String getCurrency_products_JPY()
    {
        return currency_products_JPY;
    }

    public void setCurrency_products_JPY(String currency_products_JPY)
    {
        this.currency_products_JPY = currency_products_JPY;
    }

    public String getCurrency_products_PEN()
    {
        return currency_products_PEN;
    }

    public void setCurrency_products_PEN(String currency_products_PEN)
    {
        this.currency_products_PEN = currency_products_PEN;
    }

    public String getCurrency_payments_INR()
    {
        return currency_payments_INR;
    }

    public void setCurrency_payments_INR(String currency_payments_INR)
    {
        this.currency_payments_INR = currency_payments_INR;
    }

    public String getCurrency_payments_USD()
    {
        return currency_payments_USD;
    }

    public void setCurrency_payments_USD(String currency_payments_USD)
    {
        this.currency_payments_USD = currency_payments_USD;
    }

    public String getCurrency_payments_EUR()
    {
        return currency_payments_EUR;
    }

    public void setCurrency_payments_EUR(String currency_payments_EUR)
    {
        this.currency_payments_EUR = currency_payments_EUR;
    }

    public String getCurrency_payments_GBP()
    {
        return currency_payments_GBP;
    }

    public void setCurrency_payments_GBP(String currency_payments_GBP)
    {
        this.currency_payments_GBP = currency_payments_GBP;
    }

    public String getCurrency_payments_JPY()
    {
        return currency_payments_JPY;
    }

    public void setCurrency_payments_JPY(String currency_payments_JPY)
    {
        this.currency_payments_JPY = currency_payments_JPY;
    }

    public String getCurrency_payments_PEN()
    {
        return currency_payments_PEN;
    }

    public void setCurrency_payments_PEN(String currency_payments_PEN)
    {
        this.currency_payments_PEN = currency_payments_PEN;
    }

    public String getAquirer()
    {
        return aquirer;
    }

    public void setAquirer(String aquirer)
    {
        this.aquirer = aquirer;
    }

    public String getReasonaquirer()
    {
        return reasonaquirer;
    }

    public void setReasonaquirer(String reasonaquirer)
    {
        this.reasonaquirer = reasonaquirer;
    }

    public String getCurrency_products_HKD()
    {
        return currency_products_HKD;
    }

    public void setCurrency_products_HKD(String currency_products_HKD)
    {
        this.currency_products_HKD = currency_products_HKD;
    }

    public String getCurrency_products_AUD()
    {
        return currency_products_AUD;
    }

    public void setCurrency_products_AUD(String currency_products_AUD)
    {
        this.currency_products_AUD = currency_products_AUD;
    }

    public String getCurrency_products_CAD()
    {
        return currency_products_CAD;
    }

    public void setCurrency_products_CAD(String currency_products_CAD)
    {
        this.currency_products_CAD = currency_products_CAD;
    }

    public String getCurrency_products_DKK()
    {
        return currency_products_DKK;
    }

    public void setCurrency_products_DKK(String currency_products_DKK)
    {
        this.currency_products_DKK = currency_products_DKK;
    }

    public String getCurrency_products_SEK()
    {
        return currency_products_SEK;
    }

    public void setCurrency_products_SEK(String currency_products_SEK)
    {
        this.currency_products_SEK = currency_products_SEK;
    }

    public String getCurrency_products_NOK()
    {
        return currency_products_NOK;
    }

    public void setCurrency_products_NOK(String currency_products_NOK)
    {
        this.currency_products_NOK = currency_products_NOK;
    }

    public String getCurrency_payments_HKD()
    {
        return currency_payments_HKD;
    }

    public void setCurrency_payments_HKD(String currency_payments_HKD)
    {
        this.currency_payments_HKD = currency_payments_HKD;
    }

    public String getCurrency_payments_AUD()
    {
        return currency_payments_AUD;
    }

    public void setCurrency_payments_AUD(String currency_payments_AUD)
    {
        this.currency_payments_AUD = currency_payments_AUD;
    }

    public String getCurrency_payments_CAD()
    {
        return currency_payments_CAD;
    }

    public void setCurrency_payments_CAD(String currency_payments_CAD)
    {
        this.currency_payments_CAD = currency_payments_CAD;
    }

    public String getCurrency_payments_DKK()
    {
        return currency_payments_DKK;
    }

    public void setCurrency_payments_DKK(String currency_payments_DKK)
    {
        this.currency_payments_DKK = currency_payments_DKK;
    }

    public String getCurrency_payments_SEK()
    {
        return currency_payments_SEK;
    }

    public void setCurrency_payments_SEK(String currency_payments_SEK)
    {
        this.currency_payments_SEK = currency_payments_SEK;
    }

    public String getCurrency_payments_NOK()
    {
        return currency_payments_NOK;
    }

    public void setCurrency_payments_NOK(String currency_payments_NOK)
    {
        this.currency_payments_NOK = currency_payments_NOK;
    }


    public String getBankProfileSaved()
    {
        return bankProfileSaved;
    }

    public void setBankProfileSaved(String bankProfileSaved)
    {
        this.bankProfileSaved = bankProfileSaved;
    }

    public String getIsProcessingHistory()
    {
        return isProcessingHistory;
    }

    public void setIsProcessingHistory(String isProcessingHistory)
    {
        this.isProcessingHistory = isProcessingHistory;
    }

    public String getCurrency_products_INR()
    {
        return currency_products_INR;
    }

    public void setCurrency_products_INR(String currency_products_INR)
    {
        this.currency_products_INR = currency_products_INR;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getBankinfo_currency()
    {
        return bankinfo_currency;
    }

    public void setBankinfo_currency(String bankinfo_currency)
    {
        this.bankinfo_currency = bankinfo_currency;
    }

    public String getIscurrencywisebankinfo()
    {
        return iscurrencywisebankinfo;
    }

    public void setIscurrencywisebankinfo(String iscurrencywisebankinfo)
    {
        this.iscurrencywisebankinfo = iscurrencywisebankinfo;
    }

    public String getCustomer_trans_data()
    {
        return customer_trans_data;
    }

    public void setCustomer_trans_data(String customer_trans_data)
    {
        this.customer_trans_data = customer_trans_data;
    }




}