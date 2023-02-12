package com.payment.deltapay.core;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 9/8/13
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class Elements
{
    //Sale/Authorize
    public final static String ELEM_AFFILIATE = "affiliate";
    public final static String ELEM_PAYMETHOD = "paymethod";
    public final static String ELEM_POSTMETHOD = "post_method";
    public final static String ELEM_PROCESSING_MODE = "processing_mode";
    public final static String ELEM_REDIRECT = "redirect";
    public final static String ELEM_NOTIFICATION_URL = "notification_url";
    public final static String ELEM_LOCATION = "location";
    public final static String ELEM_ORDERID = "order_id";
    public final static String ELEM_ROOMNAME = "room_name";
    public final static String ELEM_AGENTNAME = "agent_name";
    public final static String ELEM_CUSTOMER_ID = "customer_id";
    public final static String ELEM_FIRST_NAME = "first_name";
    public final static String ELEM_LAST_NAME = "last_name";
    public final static String ELEM_ADDRESS1 = "address1";
    public final static String ELEM_ADDRESS2 = "address2";
    public final static String ELEM_CITY = "city";
    public final static String ELEM_STATE = "state";
    public final static String ELEM_COUNTRY = "country";
    public final static String ELEM_ZIP = "zip";
    public final static String ELEM_TELEPHONE = "telephone";

    public final static String ELEM_AMOUNT = "amount";
    public final static String ELEM_CURRENCY = "currency";
    public final static String ELEM_EMAIL = "email";
    public final static String ELEM_CARD_TYPE = "card_type";
    public final static String ELEM_CC_NUM = "card_number";
    public final static String ELEM_CVV = "cvv";
    public final static String ELEM_EXP_MONTH = "expiry_mo";
    public final static String ELEM_EXP_YEAR  = "expiry_yr";
    public final static String ELEM_AFFILIATE_PIN = "affiliate_pin";      //OPTIONAL
    public final static String ELEM_IP = "customer_ip";                 //OPTIONAL
    public final static String ELEM_PRODUCT_ID = "product_id";          //OPTIONAL
    public final static String ELEM_PRODUCT_DESC = "product_description";  //OPTIONAL
    public final static String ELEM_REF_TRANS_NO = "reference_transaction_no";  //OPTIONAL

    //RESPONSE
    public final static String ELEM_TRANSACTION_NO = "transaction_no";         //The unique number assigned by The gateway.
    public final static String ELEM_APPROVAL_NO = "approval_no";               //The unique number assigned by the processing bank for approved transactions
    public final static String ELEM_DESCRIPTOR = "gateway_descriptor";
    public final static String ELEM_DESCRIPTION = "description";
    public final static String ELEM_STATUS_DESCRIPTION = "status_description";
    public final static String ELEM_STATUS = "status";
    public final static String ELEM_REASON = "reason";


}
