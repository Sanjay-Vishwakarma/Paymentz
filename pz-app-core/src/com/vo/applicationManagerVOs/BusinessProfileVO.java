package com.vo.applicationManagerVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 9/2/2017.
 */
@XmlRootElement(name="BusinessProfileRequestVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessProfileVO
{
    @XmlElement(name="application_id")
    String application_id;

    @XmlElement(name="foreigntransactions_us")
    String foreigntransactions_us;

    @XmlElement(name="foreigntransactions_Europe")
    String foreigntransactions_Europe;

    @XmlElement(name="foreigntransactions_Asia")
    String foreigntransactions_Asia;

    @XmlElement(name="foreigntransactions_RestoftheWorld")
    String foreigntransactions_RestoftheWorld;

    @XmlElement(name="methodofacceptance_moto")
    String methodofacceptance_moto;

    @XmlElement(name="methodofacceptance_internet")
    String methodofacceptance_internet;

    @XmlElement(name="methodofacceptance_swipe")
    String methodofacceptance_swipe;

    @XmlElement(name="averageticket")
    String averageticket;

    @XmlElement(name="highestticket")
    String highestticket;

    @XmlElement(name="urls")
    String urls ;

    @XmlElement(name="descriptor")
    String descriptor;

    @XmlElement(name="descriptionofproducts")
    String descriptionofproducts;

    @XmlElement(name="product_sold_currencies")
    String product_sold_currencies;

    @XmlElement(name="recurringservices")
    String recurringservices;

    @XmlElement(name="recurringservicesyes")
    String recurringservicesyes;

    @XmlElement(name="isacallcenterused")
    String isacallcenterused;

    @XmlElement(name="isacallcenterusedyes")
    String isacallcenterusedyes;

    @XmlElement(name="isafulfillmenthouseused")
    String isafulfillmenthouseused;

    @XmlElement(name="isafulfillmenthouseused_yes")
    String isafulfillmenthouseused_yes;

    @XmlElement(name="cardtypesaccepted_visa")
    String cardtypesaccepted_visa;

    @XmlElement(name="cardtypesaccepted_mastercard")
    String cardtypesaccepted_mastercard;

    @XmlElement(name="cardtypesaccepted_americanexpress")
    String cardtypesaccepted_americanexpress;

    @XmlElement(name="cardtypesaccepted_discover")
    String cardtypesaccepted_discover;

    @XmlElement(name="cardtypesaccepted_diners")
    String cardtypesaccepted_diners;

    @XmlElement(name="cardtypesaccepted_jcb")
    String cardtypesaccepted_jcb;

    @XmlElement(name="cardtypesaccepted_other")
    String cardtypesaccepted_other;

    @XmlElement(name="cardtypesaccepted_other_yes")
    String cardtypesaccepted_other_yes;

    @XmlElement(name="sizeofcustomer_Database")
    String sizeofcustomer_Database;

    @XmlElement(name="topfivecountries")
    String topfivecountries;

    @XmlElement(name="kyc_processes")
    String kyc_processes;

    @XmlElement(name="customer_account")
    String customer_account;

    @XmlElement(name="visa_cardlogos")
    String visa_cardlogos;

    @XmlElement(name="master_cardlogos")
    String master_cardlogos;

    @XmlElement(name="threeD_secure_compulsory")
    String threeD_secure_compulsory;

    @XmlElement(name="price_displayed")
    String price_displayed ;

    @XmlElement(name="transaction_currency")
    String transaction_currency ;

    @XmlElement(name="cardholder_asked")
    String  cardholder_asked;

    @XmlElement(name="dynamic_descriptors")
    String  dynamic_descriptors;

    @XmlElement(name="shopping_cart")
    String  shopping_cart;

    @XmlElement(name="shopping_cart_details")
    String  shopping_cart_details;

    @XmlElement(name="pricing_policies_website")
    String  pricing_policies_website;

    @XmlElement(name="pricing_policies_website_yes")
    String  pricing_policies_website_yes;

    @XmlElement(name="fulfillment_timeframe")
    String  fulfillment_timeframe;

    @XmlElement(name="goods_policy")
    String  goods_policy;

    @XmlElement(name="MCC_Ctegory")
    String  MCC_Ctegory;

    @XmlElement(name="traffic_countries_us")
    String  traffic_countries_us;

    @XmlElement(name="traffic_countries_Europe")
    String  traffic_countries_Europe;

    @XmlElement(name="traffic_countries_Asia")
    String traffic_countries_Asia;

    @XmlElement(name="traffic_countries_CIS")
    String traffic_countries_CIS;

    @XmlElement(name="traffic_countries_canada")
    String traffic_countries_canada;

    @XmlElement(name="traffic_countries_restworld")
    String traffic_countries_restworld;

    @XmlElement(name="countries_blocked")
    String countries_blocked;

    @XmlElement(name="countries_blocked_details")
    String countries_blocked_details;

    @XmlElement(name="customer_support")
    String customer_support;

    @XmlElement(name="customer_support_details")
    String customer_support_details;

    @XmlElement(name="affiliate_programs")
    String affiliate_programs;

    @XmlElement(name="affiliate_programs_details")
    String affiliate_programs_details;

    @XmlElement(name="listfraudtools")
    String listfraudtools;

    @XmlElement(name="listfraudtools_yes")
    String listfraudtools_yes;

    @XmlElement(name="customers_identification")
    String customers_identification;

    @XmlElement(name="customers_identification_yes")
    String customers_identification_yes;

    @XmlElement(name="coolingoffperiod")
    String coolingoffperiod;

    @XmlElement(name="customersupport_email")
    String customersupport_email;

    @XmlElement(name="custsupportwork_hours")
    String custsupportwork_hours;

    @XmlElement(name="technical_contact")
    String technical_contact;

    @XmlElement(name="foreigntransactions_cis")
    String foreigntransactions_cis;

    @XmlElement(name="foreigntransactions_canada")
    String foreigntransactions_canada;

    @XmlElement(name="securitypolicy")
    String securitypolicy;

    @XmlElement(name="confidentialitypolicy")
    String confidentialitypolicy;

    @XmlElement(name="applicablejurisdictions")
    String applicablejurisdictions;

    @XmlElement(name="privacy_anonymity_dataprotection")
    String privacy_anonymity_dataprotection;

    @XmlElement(name="App_Services")
    String App_Services;

    @XmlElement(name="product_requires")
    String product_requires;

    @XmlElement(name="agency_employed")
    String agency_employed;

    @XmlElement(name="agency_employed_yes")
    String agency_employed_yes;

    //Add new
    @XmlElement(name="loginId")
    String loginId;

    @XmlElement(name="passWord")
    String passWord;

    @XmlElement(name="companyIdentifiable")
    String companyIdentifiable;

    @XmlElement(name="clearlyPresented")
    String clearlyPresented;

    @XmlElement(name="trackingNumber")
    String trackingNumber;

    @XmlElement(name="domainsOwned")
    String domainsOwned;

    @XmlElement(name="domainsOwned_no")
    String domainsOwned_no;

    @XmlElement(name="sslSecured")
    String sslSecured;

    @XmlElement(name="copyright")
    String copyright;

    @XmlElement(name="sourceContent")
    String sourceContent;

    @XmlElement(name="directMail")
    String directMail;

    @XmlElement(name="yellowPages")
    String yellowPages;

    @XmlElement(name="radioTv")
    String radioTv;

    @XmlElement(name="internet")
    String internet;

    @XmlElement(name="networking")
    String networking;

    @XmlElement(name="outboundTelemarketing")
    String outboundTelemarketing;

    @XmlElement(name="inHouseLocation")
    String inHouseLocation;

    @XmlElement(name="contactPerson")
    String contactPerson;

    @XmlElement(name="shippingContactemail")
    String shippingContactemail;

    @XmlElement(name="otherLocation")
    String otherLocation;

    @XmlElement(name="warehouseLocation")
    String warehouseLocation;

    @XmlElement(name="mainSuppliers")
    String mainSuppliers;

    @XmlElement(name="shipmentAssured")
    String shipmentAssured;

    //Add new
    @XmlElement(name="billingModel")
    String billingModel;

    @XmlElement(name="billingTimeFrame")
    String billingTimeFrame;

    @XmlElement(name="recurringAmount")
    String recurringAmount;

    @XmlElement(name="multipleMembership")
    String multipleMembership;

    @XmlElement(name="freeMembership")
    String freeMembership;

    @XmlElement(name="creditCardRequired")
    String creditCardRequired;

    @XmlElement(name="automaticallyBilled")
    String automaticallyBilled;

    @XmlElement(name="preAuthorization")
    String preAuthorization;

    @XmlElement(name="automaticRecurring")
    String automaticRecurring;

    @XmlElement(name="merchantCode")
    String merchantCode;

    //Add new
    @XmlElement(name="Lowestticket")
    String  Lowestticket;

    @XmlElement(name="timeframe")
    String  timeframe;

    @XmlElement(name="livechat")
    String  livechat;

    @XmlElement(name="ipaddress")
    String ipaddress;

    // Wirecard requirement added in Business Profile
    @XmlElement(name="shopsystem_plugin")
    String shopsystem_plugin;

    @XmlElement(name="direct_debit_sepa")
    String direct_debit_sepa;

    @XmlElement(name="alternative_payments")
    String alternative_payments;

    @XmlElement(name="risk_management")
    String risk_management;

    @XmlElement(name="payment_engine")
    String payment_engine;

    @XmlElement(name="webhost_company_name")
    String webhost_company_name;

    @XmlElement(name="webhost_phone")
    String webhost_phone;

    @XmlElement(name="webhost_email")
    String webhost_email;

    @XmlElement(name="webhost_website")
    String webhost_website;

    @XmlElement(name="webhost_address")
    String webhost_address;

    @XmlElement(name="payment_company_name")
    String payment_company_name;

    @XmlElement(name="payment_phone")
    String payment_phone;

    @XmlElement(name="payment_email")
    String payment_email;

    @XmlElement(name="payment_website")
    String payment_website;

    @XmlElement(name="payment_address")
    String payment_address;

    @XmlElement(name="callcenter_phone")
    String callcenter_phone;

    @XmlElement(name="callcenter_email")
    String callcenter_email;

    @XmlElement(name="callcenter_website")
    String callcenter_website;

    @XmlElement(name="callcenter_address")
    String callcenter_address;

    @XmlElement(name="shoppingcart_company_name")
    String shoppingcart_company_name;

    @XmlElement(name="shoppingcart_phone")
    String shoppingcart_phone;

    @XmlElement(name="shoppingcart_email")
    String shoppingcart_email;

    @XmlElement(name="shoppingcart_website")
    String shoppingcart_website;

    @XmlElement(name="shoppingcart_address")
    String shoppingcart_address;

    @XmlElement(name="seasonal_fluctuating")
    String seasonal_fluctuating;

    @XmlElement(name="paymenttype_credit")
    String paymenttype_credit;

    @XmlElement(name="paymenttype_debit")
    String paymenttype_debit;

    @XmlElement(name="paymenttype_netbanking")
    String paymenttype_netbanking;

    @XmlElement(name="paymenttype_wallet")
    String paymenttype_wallet;

    @XmlElement(name="paymenttype_alternate")
    String paymenttype_alternate;

    @XmlElement(name="creditor_id")
    String creditor_id;

    @XmlElement(name="payment_delivery")
    String payment_delivery;

    @XmlElement(name="payment_delivery_otheryes")
    String payment_delivery_otheryes;

    @XmlElement(name="goods_delivery")
    String goods_delivery;

    @XmlElement(name="terminal_type")
    String terminal_type;

    @XmlElement(name="terminal_type_otheryes")
    String terminal_type_otheryes;

    @XmlElement(name="terminal_type_other")
    String terminal_type_other;

    @XmlElement(name="one_time_percentage")
    String one_time_percentage;

    @XmlElement(name="moto_percentage")
    String moto_percentage;

    @XmlElement(name="recurring_percentage")
    String recurring_percentage;

    @XmlElement(name="threedsecure_percentage")
    String threedsecure_percentage;

    @XmlElement(name="internet_percentage")
    String internet_percentage;

    @XmlElement(name="swipe_percentage")
    String swipe_percentage;

    @XmlElement(name="cardvolume_visa")
    String cardvolume_visa;

    @XmlElement(name="cardvolume_mastercard")
    String cardvolume_mastercard;

    @XmlElement(name="cardvolume_americanexpress")
    String cardvolume_americanexpress;

    @XmlElement(name="cardvolume_dinner")
    String cardvolume_dinner;

    @XmlElement(name="cardvolume_other")
    String cardvolume_other;

    @XmlElement(name="cardvolume_discover")
    String cardvolume_discover;

    @XmlElement(name="payment_type_yes")
    String payment_type_yes;

    @XmlElement(name="businessProfileSaved")
    String businessProfileSaved;

    @XmlElement(name="orderconfirmation_post")
    String orderconfirmation_post;

    @XmlElement(name="orderconfirmation_email")
    String orderconfirmation_email;

    @XmlElement(name="orderconfirmation_sms")
    String orderconfirmation_sms;

    @XmlElement(name="orderconfirmation_other")
    String orderconfirmation_other;

    @XmlElement(name="orderconfirmation_other_yes")
    String orderconfirmation_other_yes;

    @XmlElement(name="physicalgoods_delivered")
    String physicalgoods_delivered;

    @XmlElement(name="viainternetgoods_delivered")
    String viainternetgoods_delivered;

    @XmlElement(name="cardtypesaccepted_rupay")
    String cardtypesaccepted_rupay;

    @XmlElement(name="cardvolume_rupay")
    String cardvolume_rupay;

    @XmlElement(name="cardvolume_jcb")
    String cardvolume_jcb;

    @XmlElement(name="foreigntransactions_uk")
    String foreigntransactions_uk;

    @XmlElement(name="seasonal_fluctuating_yes")
    String seasonal_fluctuating_yes;

    @XmlElement(name="is_website_live")
    String is_website_live;

    @XmlElement(name="test_link")
    String test_link;

    public String getSeasonal_fluctuating_yes()
    {
        return seasonal_fluctuating_yes;
    }

    public void setSeasonal_fluctuating_yes(String seasonal_fluctuating_yes)
    {
        this.seasonal_fluctuating_yes = seasonal_fluctuating_yes;
    }

    public String getCardtypesaccepted_rupay()
    {
        return cardtypesaccepted_rupay;
    }

    public void setCardtypesaccepted_rupay(String cardtypesaccepted_rupay)
    {
        this.cardtypesaccepted_rupay = cardtypesaccepted_rupay;
    }

    public String getCardvolume_rupay()
    {
        return cardvolume_rupay;
    }

    public void setCardvolume_rupay(String cardvolume_rupay)
    {
        this.cardvolume_rupay = cardvolume_rupay;
    }

    public String getCardvolume_jcb()
    {
        return cardvolume_jcb;
    }

    public void setCardvolume_jcb(String cardvolume_jcb)
    {
        this.cardvolume_jcb = cardvolume_jcb;
    }

    public String getForeigntransactions_uk()
    {
        return foreigntransactions_uk;
    }

    public void setForeigntransactions_uk(String foreigntransactions_uk)
    {
        this.foreigntransactions_uk = foreigntransactions_uk;
    }

    public String getApplication_id()
    {
        return application_id;
    }

    public void setApplication_id(String application_id)
    {
        this.application_id = application_id;
    }
    public String getForeigntransactions_us()
    {
        return foreigntransactions_us;
    }

    public void setForeigntransactions_us(String foreigntransactions_us)
    {
        this.foreigntransactions_us = foreigntransactions_us;
    }

    public String getForeigntransactions_Europe()
    {
        return foreigntransactions_Europe;
    }

    public void setForeigntransactions_Europe(String foreigntransactions_Europe)
    {
        this.foreigntransactions_Europe = foreigntransactions_Europe;
    }

    public String getForeigntransactions_Asia()
    {
        return foreigntransactions_Asia;
    }

    public void setForeigntransactions_Asia(String foreigntransactions_Asia)
    {
        this.foreigntransactions_Asia = foreigntransactions_Asia;
    }

    public String getForeigntransactions_RestoftheWorld()
    {
        return foreigntransactions_RestoftheWorld;
    }

    public void setForeigntransactions_RestoftheWorld(String foreigntransactions_RestoftheWorld)
    {
        this.foreigntransactions_RestoftheWorld = foreigntransactions_RestoftheWorld;
    }

    public String getMethodofacceptance_moto()
    {
        return methodofacceptance_moto;
    }

    public void setMethodofacceptance_moto(String methodofacceptance_moto)
    {
        this.methodofacceptance_moto = methodofacceptance_moto;
    }

    public String getMethodofacceptance_internet()
    {
        return methodofacceptance_internet;
    }

    public void setMethodofacceptance_internet(String methodofacceptance_internet)
    {
        this.methodofacceptance_internet = methodofacceptance_internet;
    }

    public String getMethodofacceptance_swipe()
    {
        return methodofacceptance_swipe;
    }

    public void setMethodofacceptance_swipe(String methodofacceptance_swipe)
    {
        this.methodofacceptance_swipe = methodofacceptance_swipe;
    }
    public String getAverageticket()
    {
        return averageticket;
    }

    public void setAverageticket(String averageticket)
    {
        this.averageticket = averageticket;
    }

    public String getHighestticket()
    {
        return highestticket;
    }

    public void setHighestticket(String highestticket)
    {
        this.highestticket = highestticket;
    }

    public String getUrls()
    {
        return urls;
    }

    public void setUrls(String urls)
    {
        this.urls = urls;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }

    public String getDescriptionofproducts()
    {
        return descriptionofproducts;
    }

    public void setDescriptionofproducts(String descriptionofproducts)
    {
        this.descriptionofproducts = descriptionofproducts;
    }

    public String getProduct_sold_currencies()
    {
        return product_sold_currencies;
    }

    public void setProduct_sold_currencies(String product_sold_currencies)
    {
        this.product_sold_currencies = product_sold_currencies;
    }

    public String getRecurringservices()
    {
        return recurringservices;
    }

    public void setRecurringservices(String recurringservices)
    {
        this.recurringservices = recurringservices;
    }

    public String getRecurringservicesyes()
    {
        return recurringservicesyes;
    }

    public void setRecurringservicesyes(String recurringservicesyes)
    {
        this.recurringservicesyes = recurringservicesyes;
    }

    public String getIsacallcenterused()
    {
        return isacallcenterused;
    }

    public void setIsacallcenterused(String isacallcenterused)
    {
        this.isacallcenterused = isacallcenterused;
    }

    public String getIsacallcenterusedyes()
    {
        return isacallcenterusedyes;
    }

    public void setIsacallcenterusedyes(String isacallcenterusedyes)
    {
        this.isacallcenterusedyes = isacallcenterusedyes;
    }

    public String getIsafulfillmenthouseused()
    {
        return isafulfillmenthouseused;
    }

    public void setIsafulfillmenthouseused(String isafulfillmenthouseused)
    {
        this.isafulfillmenthouseused = isafulfillmenthouseused;
    }

    public String getIsafulfillmenthouseused_yes()
    {
        return isafulfillmenthouseused_yes;
    }

    public void setIsafulfillmenthouseused_yes(String isafulfillmenthouseused_yes)
    {
        this.isafulfillmenthouseused_yes = isafulfillmenthouseused_yes;
    }

    public String getCardtypesaccepted_visa()
    {
        return cardtypesaccepted_visa;
    }

    public void setCardtypesaccepted_visa(String cardtypesaccepted_visa)
    {
        this.cardtypesaccepted_visa = cardtypesaccepted_visa;
    }

    public String getCardtypesaccepted_mastercard()
    {
        return cardtypesaccepted_mastercard;
    }

    public void setCardtypesaccepted_mastercard(String cardtypesaccepted_mastercard)
    {
        this.cardtypesaccepted_mastercard = cardtypesaccepted_mastercard;
    }

    public String getCardtypesaccepted_americanexpress()
    {
        return cardtypesaccepted_americanexpress;
    }

    public void setCardtypesaccepted_americanexpress(String cardtypesaccepted_americanexpress)
    {
        this.cardtypesaccepted_americanexpress = cardtypesaccepted_americanexpress;
    }

    public String getCardtypesaccepted_discover()
    {
        return cardtypesaccepted_discover;
    }

    public void setCardtypesaccepted_discover(String cardtypesaccepted_discover)
    {
        this.cardtypesaccepted_discover = cardtypesaccepted_discover;
    }

    public String getCardtypesaccepted_diners()
    {
        return cardtypesaccepted_diners;
    }

    public void setCardtypesaccepted_diners(String cardtypesaccepted_diners)
    {
        this.cardtypesaccepted_diners = cardtypesaccepted_diners;
    }

    public String getCardtypesaccepted_jcb()
    {
        return cardtypesaccepted_jcb;
    }

    public void setCardtypesaccepted_jcb(String cardtypesaccepted_jcb)
    {
        this.cardtypesaccepted_jcb = cardtypesaccepted_jcb;
    }

    public String getCardtypesaccepted_other()
    {
        return cardtypesaccepted_other;
    }

    public void setCardtypesaccepted_other(String cardtypesaccepted_other)
    {
        this.cardtypesaccepted_other = cardtypesaccepted_other;
    }

    public String getCardtypesaccepted_other_yes()
    {
        return cardtypesaccepted_other_yes;
    }

    public void setCardtypesaccepted_other_yes(String cardtypesaccepted_other_yes)
    {
        this.cardtypesaccepted_other_yes = cardtypesaccepted_other_yes;
    }
    //Add details
    public String getSizeofcustomer_Database()
    {
        return sizeofcustomer_Database;
    }

    public void setSizeofcustomer_Database(String sizeofcustomer_Database)
    {
        this.sizeofcustomer_Database = sizeofcustomer_Database;
    }

    public String getTopfivecountries()
    {
        return topfivecountries;
    }

    public void setTopfivecountries(String topfivecountries)
    {
        this.topfivecountries = topfivecountries;
    }

    public String getKyc_processes()
    {
        return kyc_processes;
    }

    public void setKyc_processes(String kyc_processes)
    {
        this.kyc_processes = kyc_processes;
    }

    public String getCustomer_account()
    {
        return customer_account;
    }

    public void setCustomer_account(String customer_account)
    {
        this.customer_account = customer_account;
    }

    public String getVisa_cardlogos()
    {
        return visa_cardlogos;
    }

    public void setVisa_cardlogos(String visa_cardlogos)
    {
        this.visa_cardlogos = visa_cardlogos;
    }

    public String getMaster_cardlogos()
    {
        return master_cardlogos;
    }

    public void setMaster_cardlogos(String master_cardlogos)
    {
        this.master_cardlogos = master_cardlogos;
    }

    public String getThreeD_secure_compulsory()
    {
        return threeD_secure_compulsory;
    }

    public void setThreeD_secure_compulsory(String threeD_secure_compulsory)
    {
        this.threeD_secure_compulsory = threeD_secure_compulsory;
    }

    public String getPrice_displayed()
    {
        return price_displayed;
    }

    public void setPrice_displayed(String price_displayed)
    {
        this.price_displayed = price_displayed;
    }

    public String getTransaction_currency()
    {
        return transaction_currency;
    }

    public void setTransaction_currency(String transaction_currency)
    {
        this.transaction_currency = transaction_currency;
    }

    public String getCardholder_asked()
    {
        return cardholder_asked;
    }

    public void setCardholder_asked(String cardholder_asked)
    {
        this.cardholder_asked = cardholder_asked;
    }

    public String getDynamic_descriptors()
    {
        return dynamic_descriptors;
    }

    public void setDynamic_descriptors(String dynamic_descriptors)
    {
        this.dynamic_descriptors = dynamic_descriptors;
    }

    public String getShopping_cart()
    {
        return shopping_cart;
    }

    public void setShopping_cart(String shopping_cart)
    {
        this.shopping_cart = shopping_cart;
    }

    public String getShopping_cart_details()
    {
        return shopping_cart_details;
    }

    public void setShopping_cart_details(String shopping_cart_details)
    {
        this.shopping_cart_details = shopping_cart_details;
    }

    public String getPricing_policies_website()
    {
        return pricing_policies_website;
    }

    public void setPricing_policies_website(String pricing_policies_website)
    {
        this.pricing_policies_website = pricing_policies_website;
    }

    public String getPricing_policies_website_yes()
    {
        return pricing_policies_website_yes;
    }

    public void setPricing_policies_website_yes(String pricing_policies_website_yes)
    {
        this.pricing_policies_website_yes = pricing_policies_website_yes;
    }

    public String getFulfillment_timeframe()
    {
        return fulfillment_timeframe;
    }

    public void setFulfillment_timeframe(String fulfillment_timeframe)
    {
        this.fulfillment_timeframe = fulfillment_timeframe;
    }

    public String getGoods_policy()
    {
        return goods_policy;
    }

    public void setGoods_policy(String goods_policy)
    {
        this.goods_policy = goods_policy;
    }

    public String getMCC_Ctegory()
    {
        return MCC_Ctegory;
    }

    public void setMCC_Ctegory(String MCC_Ctegory)
    {
        this.MCC_Ctegory = MCC_Ctegory;
    }

    public String getTraffic_countries_us()
    {
        return traffic_countries_us;
    }

    public void setTraffic_countries_us(String traffic_countries_us)
    {
        this.traffic_countries_us = traffic_countries_us;
    }

    public String getTraffic_countries_Europe()
    {
        return traffic_countries_Europe;
    }

    public void setTraffic_countries_Europe(String traffic_countries_Europe)
    {
        this.traffic_countries_Europe = traffic_countries_Europe;
    }

    public String getTraffic_countries_Asia()
    {
        return traffic_countries_Asia;
    }

    public void setTraffic_countries_Asia(String traffic_countries_Asia)
    {
        this.traffic_countries_Asia = traffic_countries_Asia;
    }

    public String getTraffic_countries_CIS()
    {
        return traffic_countries_CIS;
    }

    public void setTraffic_countries_CIS(String traffic_countries_CIS)
    {
        this.traffic_countries_CIS = traffic_countries_CIS;
    }

    public String getTraffic_countries_canada()
    {
        return traffic_countries_canada;
    }

    public void setTraffic_countries_canada(String traffic_countries_canada)
    {
        this.traffic_countries_canada = traffic_countries_canada;
    }

    public String getTraffic_countries_restworld()
    {
        return traffic_countries_restworld;
    }

    public void setTraffic_countries_restworld(String traffic_countries_restworld)
    {
        this.traffic_countries_restworld = traffic_countries_restworld;
    }

    public String getCountries_blocked()
    {
        return countries_blocked;
    }

    public void setCountries_blocked(String countries_blocked)
    {
        this.countries_blocked = countries_blocked;
    }

    public String getCountries_blocked_details()
    {
        return countries_blocked_details;
    }

    public void setCountries_blocked_details(String countries_blocked_details)
    {
        this.countries_blocked_details = countries_blocked_details;
    }

    public String getCustomer_support()
    {
        return customer_support;
    }

    public void setCustomer_support(String customer_support)
    {
        this.customer_support = customer_support;
    }

    public String getCustomer_support_details()
    {
        return customer_support_details;
    }

    public void setCustomer_support_details(String customer_support_details)
    {
        this.customer_support_details = customer_support_details;
    }

    public String getAffiliate_programs()
    {
        return affiliate_programs;
    }

    public void setAffiliate_programs(String affiliate_programs)
    {
        this.affiliate_programs = affiliate_programs;
    }

    public String getAffiliate_programs_details()
    {
        return affiliate_programs_details;
    }

    public void setAffiliate_programs_details(String affiliate_programs_details)
    {
        this.affiliate_programs_details = affiliate_programs_details;
    }

    public String getListfraudtools()
    {
        return listfraudtools;
    }

    public void setListfraudtools(String listfraudtools)
    {
        this.listfraudtools = listfraudtools;
    }

    public String getListfraudtools_yes()
    {
        return listfraudtools_yes;
    }

    public void setListfraudtools_yes(String listfraudtools_yes)
    {
        this.listfraudtools_yes = listfraudtools_yes;
    }

    public String getCustomers_identification()
    {
        return customers_identification;
    }

    public void setCustomers_identification(String customers_identification)
    {
        this.customers_identification = customers_identification;
    }

    public String getCoolingoffperiod()
    {
        return coolingoffperiod;
    }

    public void setCoolingoffperiod(String coolingoffperiod)
    {
        this.coolingoffperiod = coolingoffperiod;
    }

    public String getCustomersupport_email()
    {
        return customersupport_email;
    }

    public void setCustomersupport_email(String customersupport_email)
    {
        this.customersupport_email = customersupport_email;
    }

    public String getCustsupportwork_hours()
    {
        return custsupportwork_hours;
    }

    public void setCustsupportwork_hours(String custsupportwork_hours)
    {
        this.custsupportwork_hours = custsupportwork_hours;
    }

    public String getTechnical_contact()
    {
        return technical_contact;
    }

    public void setTechnical_contact(String technical_contact)
    {
        this.technical_contact = technical_contact;
    }

    public String getBusinessProfileSaved()
    {
        return businessProfileSaved;
    }

    public String getForeigntransactions_cis()
    {
        return foreigntransactions_cis;
    }

    public void setForeigntransactions_cis(String foreigntransactions_cis)
    {
        this.foreigntransactions_cis = foreigntransactions_cis;
    }

    public String getForeigntransactions_canada()
    {
        return foreigntransactions_canada;
    }

    public void setForeigntransactions_canada(String foreigntransactions_canada)
    {
        this.foreigntransactions_canada = foreigntransactions_canada;
    }

    public void setBusinessProfileSaved(String businessProfileSaved)
    {
        this.businessProfileSaved = businessProfileSaved;
    }

    public String getSecuritypolicy()
    {
        return securitypolicy;
    }

    public void setSecuritypolicy(String securitypolicy)
    {
        this.securitypolicy = securitypolicy;
    }

    public String getConfidentialitypolicy()
    {
        return confidentialitypolicy;
    }

    public void setConfidentialitypolicy(String confidentialitypolicy)
    {
        this.confidentialitypolicy = confidentialitypolicy;
    }

    public String getApplicablejurisdictions()
    {
        return applicablejurisdictions;
    }

    public void setApplicablejurisdictions(String applicablejurisdictions)
    {
        this.applicablejurisdictions = applicablejurisdictions;
    }

    public String getPrivacy_anonymity_dataprotection()
    {
        return privacy_anonymity_dataprotection;
    }

    public void setPrivacy_anonymity_dataprotection(String privacy_anonymity_dataprotection)
    {
        this.privacy_anonymity_dataprotection = privacy_anonymity_dataprotection;
    }

    public String getApp_Services()
    {
        return App_Services;
    }

    public void setApp_Services(String app_Services)
    {
        App_Services = app_Services;
    }

    public String getProduct_requires()
    {
        return product_requires;
    }

    public void setProduct_requires(String product_requires)
    {
        this.product_requires = product_requires;
    }


    //ADD new


    public String getLoginId()
    {
        return loginId;
    }

    public void setLoginId(String loginId)
    {
        this.loginId = loginId;
    }

    public String getPassWord()
    {
        return passWord;
    }

    public void setPassWord(String passWord)
    {
        this.passWord = passWord;
    }

    public String getCompanyIdentifiable()
    {
        return companyIdentifiable;
    }

    public void setCompanyIdentifiable(String companyIdentifiable)
    {
        this.companyIdentifiable = companyIdentifiable;
    }

    public String getClearlyPresented()
    {
        return clearlyPresented;
    }

    public void setClearlyPresented(String clearlyPresented)
    {
        this.clearlyPresented = clearlyPresented;
    }

    public String getTrackingNumber()
    {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber)
    {
        this.trackingNumber = trackingNumber;
    }

    public String getDomainsOwned()
    {
        return domainsOwned;
    }

    public void setDomainsOwned(String domainsOwned)
    {
        this.domainsOwned = domainsOwned;
    }

    public String getDomainsOwned_no()
    {
        return domainsOwned_no;
    }

    public void setDomainsOwned_no(String domainsOwned_no)
    {
        this.domainsOwned_no = domainsOwned_no;
    }

    public String getSslSecured()
    {
        return sslSecured;
    }

    public void setSslSecured(String sslSecured)
    {
        this.sslSecured = sslSecured;
    }

    public String getCopyright()
    {
        return copyright;
    }

    public void setCopyright(String copyright)
    {
        this.copyright = copyright;
    }

    public String getSourceContent()
    {
        return sourceContent;
    }

    public void setSourceContent(String sourceContent)
    {
        this.sourceContent = sourceContent;
    }

    //ADD new

    public String getDirectMail()
    {
        return directMail;
    }

    public void setDirectMail(String directMail)
    {
        this.directMail = directMail;
    }

    public String getYellowPages()
    {
        return yellowPages;
    }

    public void setYellowPages(String yellowPages)
    {
        this.yellowPages = yellowPages;
    }

    public String getRadioTv()
    {
        return radioTv;
    }

    public void setRadioTv(String radioTv)
    {
        this.radioTv = radioTv;
    }

    public String getInternet()
    {
        return internet;
    }

    public void setInternet(String internet)
    {
        this.internet = internet;
    }

    public String getNetworking()
    {
        return networking;
    }

    public void setNetworking(String networking)
    {
        this.networking = networking;
    }

    public String getOutboundTelemarketing()
    {
        return outboundTelemarketing;
    }

    public void setOutboundTelemarketing(String outboundTelemarketing)
    {
        this.outboundTelemarketing = outboundTelemarketing;
    }

    public String getInHouseLocation()
    {
        return inHouseLocation;
    }

    public void setInHouseLocation(String inHouseLocation)
    {
        this.inHouseLocation = inHouseLocation;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getOtherLocation()
    {
        return otherLocation;
    }

    public void setOtherLocation(String otherLocation)
    {
        this.otherLocation = otherLocation;
    }

    public String getMainSuppliers()
    {
        return mainSuppliers;
    }

    public void setMainSuppliers(String mainSuppliers)
    {
        this.mainSuppliers = mainSuppliers;
    }

    public String getShipmentAssured()
    {
        return shipmentAssured;
    }

    public void setShipmentAssured(String shipmentAssured)
    {
        this.shipmentAssured = shipmentAssured;
    }

    public String getBillingModel()
    {
        return billingModel;
    }

    public void setBillingModel(String billingModel)
    {
        this.billingModel = billingModel;
    }

    public String getBillingTimeFrame()
    {
        return billingTimeFrame;
    }

    public void setBillingTimeFrame(String billingTimeFrame)
    {
        this.billingTimeFrame = billingTimeFrame;
    }

    public String getRecurringAmount()
    {
        return recurringAmount;
    }

    public void setRecurringAmount(String recurringAmount)
    {
        this.recurringAmount = recurringAmount;
    }

    public String getMultipleMembership()
    {
        return multipleMembership;
    }

    public void setMultipleMembership(String multipleMembership)
    {
        this.multipleMembership = multipleMembership;
    }

    public String getFreeMembership()
    {
        return freeMembership;
    }

    public void setFreeMembership(String freeMembership)
    {
        this.freeMembership = freeMembership;
    }

    public String getCreditCardRequired()
    {
        return creditCardRequired;
    }

    public void setCreditCardRequired(String creditCardRequired)
    {
        this.creditCardRequired = creditCardRequired;
    }

    public String getAutomaticallyBilled()
    {
        return automaticallyBilled;
    }

    public void setAutomaticallyBilled(String automaticallyBilled)
    {
        this.automaticallyBilled = automaticallyBilled;
    }

    public String getPreAuthorization()
    {
        return preAuthorization;
    }

    public void setPreAuthorization(String preAuthorization)
    {
        this.preAuthorization = preAuthorization;
    }

    public String getAutomaticRecurring()
    {
        return automaticRecurring;
    }

    public void setAutomaticRecurring(String automaticRecurring)
    {
        this.automaticRecurring = automaticRecurring;
    }

    //ADD new

    public String getLowestticket()
    {
        return Lowestticket;
    }

    public void setLowestticket(String lowestticket)
    {
        Lowestticket = lowestticket;
    }
    //add

    public String getTimeframe()
    {
        return timeframe;
    }

    public void setTimeframe(String timeframe)
    {
        this.timeframe = timeframe;
    }

    public String getLivechat()
    {
        return livechat;
    }

    public void setLivechat(String livechat)
    {
        this.livechat = livechat;
    }

    public String getMerchantCode()
    {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode)
    {
        this.merchantCode = merchantCode;
    }

    public String getIpaddress()
    {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress)
    {
        this.ipaddress = ipaddress;
    }

    public String getWarehouseLocation()
    {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation)
    {
        this.warehouseLocation = warehouseLocation;
    }

    public String getShopsystem_plugin()
    {
        return shopsystem_plugin;
    }

    public void setShopsystem_plugin(String shopsystem_plugin)
    {
        this.shopsystem_plugin = shopsystem_plugin;
    }

    public String getDirect_debit_sepa()
    {
        return direct_debit_sepa;
    }

    public void setDirect_debit_sepa(String direct_debit_sepa)
    {
        this.direct_debit_sepa = direct_debit_sepa;
    }

    public String getAlternative_payments()
    {
        return alternative_payments;
    }

    public void setAlternative_payments(String alternative_payments)
    {
        this.alternative_payments = alternative_payments;
    }

    public String getRisk_management()
    {
        return risk_management;
    }

    public void setRisk_management(String risk_management)
    {
        this.risk_management = risk_management;
    }

    public String getPayment_engine()
    {
        return payment_engine;
    }

    public void setPayment_engine(String payment_engine)
    {
        this.payment_engine = payment_engine;
    }

    public String getWebhost_company_name()
    {
        return webhost_company_name;
    }

    public void setWebhost_company_name(String webhost_company_name)
    {
        this.webhost_company_name = webhost_company_name;
    }

    public String getWebhost_phone()
    {
        return webhost_phone;
    }

    public void setWebhost_phone(String webhost_phone)
    {
        this.webhost_phone = webhost_phone;
    }

    public String getWebhost_email()
    {
        return webhost_email;
    }

    public void setWebhost_email(String webhost_email)
    {
        this.webhost_email = webhost_email;
    }

    public String getWebhost_address()
    {
        return webhost_address;
    }

    public void setWebhost_address(String webhost_address)
    {
        this.webhost_address = webhost_address;
    }

    public String getPayment_company_name()
    {
        return payment_company_name;
    }

    public void setPayment_company_name(String payment_company_name)
    {
        this.payment_company_name = payment_company_name;
    }

    public String getPayment_phone()
    {
        return payment_phone;
    }

    public void setPayment_phone(String payment_phone)
    {
        this.payment_phone = payment_phone;
    }

    public String getPayment_email()
    {
        return payment_email;
    }

    public void setPayment_email(String payment_email)
    {
        this.payment_email = payment_email;
    }

    public String getPayment_address()
    {
        return payment_address;
    }

    public void setPayment_address(String payment_address)
    {
        this.payment_address = payment_address;
    }

    public String getCallcenter_phone()
    {
        return callcenter_phone;
    }

    public void setCallcenter_phone(String callcenter_phone)
    {
        this.callcenter_phone = callcenter_phone;
    }

    public String getCallcenter_email()
    {
        return callcenter_email;
    }

    public void setCallcenter_email(String callcenter_email)
    {
        this.callcenter_email = callcenter_email;
    }

    public String getCallcenter_address()
    {
        return callcenter_address;
    }

    public void setCallcenter_address(String callcenter_address)
    {
        this.callcenter_address = callcenter_address;
    }

    public String getShoppingcart_company_name()
    {
        return shoppingcart_company_name;
    }

    public void setShoppingcart_company_name(String shoppingcart_company_name)
    {
        this.shoppingcart_company_name = shoppingcart_company_name;
    }

    public String getShoppingcart_phone()
    {
        return shoppingcart_phone;
    }

    public void setShoppingcart_phone(String shoppingcart_phone)
    {
        this.shoppingcart_phone = shoppingcart_phone;
    }

    public String getShoppingcart_email()
    {
        return shoppingcart_email;
    }

    public void setShoppingcart_email(String shoppingcart_email)
    {
        this.shoppingcart_email = shoppingcart_email;
    }

    public String getShoppingcart_address()
    {
        return shoppingcart_address;
    }

    public void setShoppingcart_address(String shoppingcart_address)
    {
        this.shoppingcart_address = shoppingcart_address;
    }

    public String getSeasonal_fluctuating()
    {
        return seasonal_fluctuating;
    }

    public void setSeasonal_fluctuating(String seasonal_fluctuating)
    {
        this.seasonal_fluctuating = seasonal_fluctuating;
    }

    public String getPaymenttype_credit()
    {
        return paymenttype_credit;
    }

    public void setPaymenttype_credit(String paymenttype_credit)
    {
        this.paymenttype_credit = paymenttype_credit;
    }

    public String getPaymenttype_debit()
    {
        return paymenttype_debit;
    }

    public void setPaymenttype_debit(String paymenttype_debit)
    {
        this.paymenttype_debit = paymenttype_debit;
    }

    public String getPaymenttype_netbanking()
    {
        return paymenttype_netbanking;
    }

    public void setPaymenttype_netbanking(String paymenttype_netbanking)
    {
        this.paymenttype_netbanking = paymenttype_netbanking;
    }

    public String getPaymenttype_wallet()
    {
        return paymenttype_wallet;
    }

    public void setPaymenttype_wallet(String paymenttype_wallet)
    {
        this.paymenttype_wallet = paymenttype_wallet;
    }

    public String getPaymenttype_alternate()
    {
        return paymenttype_alternate;
    }

    public void setPaymenttype_alternate(String paymenttype_alternate)
    {
        this.paymenttype_alternate = paymenttype_alternate;
    }

    public String getCreditor_id()
    {
        return creditor_id;
    }

    public void setCreditor_id(String creditor_id)
    {
        this.creditor_id = creditor_id;
    }

    public String getPayment_delivery()
    {
        return payment_delivery;
    }

    public void setPayment_delivery(String payment_delivery)
    {
        this.payment_delivery = payment_delivery;
    }

    public String getPayment_delivery_otheryes()
    {
        return payment_delivery_otheryes;
    }

    public void setPayment_delivery_otheryes(String payment_delivery_otheryes)
    {
        this.payment_delivery_otheryes = payment_delivery_otheryes;
    }

    public String getGoods_delivery()
    {
        return goods_delivery;
    }

    public void setGoods_delivery(String goods_delivery)
    {
        this.goods_delivery = goods_delivery;
    }

    public String getTerminal_type()
    {
        return terminal_type;
    }

    public void setTerminal_type(String terminal_type)
    {
        this.terminal_type = terminal_type;
    }

    public String getTerminal_type_otheryes()
    {
        return terminal_type_otheryes;
    }

    public void setTerminal_type_otheryes(String terminal_type_otheryes)
    {
        this.terminal_type_otheryes = terminal_type_otheryes;
    }

    public String getTerminal_type_other()
    {
        return terminal_type_other;
    }

    public void setTerminal_type_other(String terminal_type_other)
    {
        this.terminal_type_other = terminal_type_other;
    }

    public String getOne_time_percentage()
    {
        return one_time_percentage;
    }

    public void setOne_time_percentage(String one_time_percentage)
    {
        this.one_time_percentage = one_time_percentage;
    }

    public String getMoto_percentage()
    {
        return moto_percentage;
    }

    public void setMoto_percentage(String moto_percentage)
    {
        this.moto_percentage = moto_percentage;
    }

    public String getRecurring_percentage()
    {
        return recurring_percentage;
    }

    public void setRecurring_percentage(String recurring_percentage)
    {
        this.recurring_percentage = recurring_percentage;
    }

    public String getThreedsecure_percentage()
    {
        return threedsecure_percentage;
    }

    public void setThreedsecure_percentage(String threedsecure_percentage)
    {
        this.threedsecure_percentage = threedsecure_percentage;
    }

    public String getInternet_percentage()
    {
        return internet_percentage;
    }

    public void setInternet_percentage(String internet_percentage)
    {
        this.internet_percentage = internet_percentage;
    }

    public String getSwipe_percentage()
    {
        return swipe_percentage;
    }

    public void setSwipe_percentage(String swipe_percentage)
    {
        this.swipe_percentage = swipe_percentage;
    }

    public String getCardvolume_visa()
    {
        return cardvolume_visa;
    }

    public void setCardvolume_visa(String cardvolume_visa)
    {
        this.cardvolume_visa = cardvolume_visa;
    }

    public String getCardvolume_mastercard()
    {
        return cardvolume_mastercard;
    }

    public void setCardvolume_mastercard(String cardvolume_mastercard)
    {
        this.cardvolume_mastercard = cardvolume_mastercard;
    }

    public String getCardvolume_americanexpress()
    {
        return cardvolume_americanexpress;
    }

    public void setCardvolume_americanexpress(String cardvolume_americanexpress)
    {
        this.cardvolume_americanexpress = cardvolume_americanexpress;
    }

    public String getCardvolume_dinner()
    {
        return cardvolume_dinner;
    }

    public void setCardvolume_dinner(String cardvolume_dinner)
    {
        this.cardvolume_dinner = cardvolume_dinner;
    }

    public String getCardvolume_other()
    {
        return cardvolume_other;
    }

    public void setCardvolume_other(String cardvolume_other)
    {
        this.cardvolume_other = cardvolume_other;
    }

    public String getCardvolume_discover()
    {
        return cardvolume_discover;
    }

    public void setCardvolume_discover(String cardvolume_discover)
    {
        this.cardvolume_discover = cardvolume_discover;
    }

    public String getPayment_type_yes()
    {
        return payment_type_yes;
    }

    public void setPayment_type_yes(String payment_type_yes)
    {
        this.payment_type_yes = payment_type_yes;
    }

    public String getWebhost_website()
    {
        return webhost_website;
    }

    public void setWebhost_website(String webhost_website)
    {
        this.webhost_website = webhost_website;
    }

    public String getPayment_website()
    {
        return payment_website;
    }

    public void setPayment_website(String payment_website)
    {
        this.payment_website = payment_website;
    }

    public String getCallcenter_website()
    {
        return callcenter_website;
    }

    public void setCallcenter_website(String callcenter_website)
    {
        this.callcenter_website = callcenter_website;
    }

    public String getShoppingcart_website()
    {
        return shoppingcart_website;
    }

    public void setShoppingcart_website(String shoppingcart_website)
    {
        this.shoppingcart_website = shoppingcart_website;
    }

    public String getOrderconfirmation_post()
    {
        return orderconfirmation_post;
    }

    public void setOrderconfirmation_post(String orderconfirmation_post)
    {
        this.orderconfirmation_post = orderconfirmation_post;
    }

    public String getOrderconfirmation_email()
    {
        return orderconfirmation_email;
    }

    public void setOrderconfirmation_email(String orderconfirmation_email)
    {
        this.orderconfirmation_email = orderconfirmation_email;
    }

    public String getOrderconfirmation_sms()
    {
        return orderconfirmation_sms;
    }

    public void setOrderconfirmation_sms(String orderconfirmation_sms)
    {
        this.orderconfirmation_sms = orderconfirmation_sms;
    }

    public String getOrderconfirmation_other()
    {
        return orderconfirmation_other;
    }

    public void setOrderconfirmation_other(String orderconfirmation_other)
    {
        this.orderconfirmation_other = orderconfirmation_other;
    }

    public String getOrderconfirmation_other_yes()
    {
        return orderconfirmation_other_yes;
    }

    public void setOrderconfirmation_other_yes(String orderconfirmation_other_yes)
    {
        this.orderconfirmation_other_yes = orderconfirmation_other_yes;
    }

    public String getPhysicalgoods_delivered()
    {
        return physicalgoods_delivered;
    }

    public void setPhysicalgoods_delivered(String physicalgoods_delivered)
    {
        this.physicalgoods_delivered = physicalgoods_delivered;
    }

    public String getViainternetgoods_delivered()
    {
        return viainternetgoods_delivered;
    }

    public void setViainternetgoods_delivered(String viainternetgoods_delivered)
    {
        this.viainternetgoods_delivered = viainternetgoods_delivered;
    }

    public String getShippingContactemail()
    {
        return shippingContactemail;
    }

    public void setShippingContactemail(String shippingContactemail)
    {
        this.shippingContactemail = shippingContactemail;
    }

    public String getIs_website_live()
    {
        return is_website_live;
    }

    public void setIs_website_live(String is_website_live)
    {
        this.is_website_live = is_website_live;
    }

    public String getTest_link()
    {
        return test_link;
    }

    public void setTest_link(String test_link)
    {
        this.test_link = test_link;
    }

    public String getAgency_employed()
    {
        return agency_employed;
    }

    public void setAgency_employed(String agency_employed)
    {
        this.agency_employed = agency_employed;
    }

    public String getAgency_employed_yes()
    {
        return agency_employed_yes;
    }

    public void setAgency_employed_yes(String agency_employed_yes)
    {
        this.agency_employed_yes = agency_employed_yes;
    }

    public String getCustomers_identification_yes()
    {
        return customers_identification_yes;
    }

    public void setCustomers_identification_yes(String customers_identification_yes)
    {
        this.customers_identification_yes = customers_identification_yes;
    }

}
