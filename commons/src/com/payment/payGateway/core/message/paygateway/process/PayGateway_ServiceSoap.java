/**
 * PayGateway_ServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payGateway.core.message.paygateway.process;

public interface PayGateway_ServiceSoap extends java.rmi.Remote {

    /**
     * Process a Transaction
     */
    public TTransacResult processTx(java.lang.String account_id, java.lang.String account_password, java.lang.String account_sha, java.lang.String action_type, java.lang.String account_gateway, java.lang.String merchant_payment_id, java.lang.String cust_email, java.lang.String cust_billing_last_name, java.lang.String cust_billing_first_name, java.lang.String cust_billing_address, java.lang.String cust_billing_city, java.lang.String cust_billing_zipcode, java.lang.String cust_billing_state, java.lang.String cust_billing_country, java.lang.String cust_billing_phone, java.lang.String cust_shipping_last_name, java.lang.String cust_shipping_first_name, java.lang.String cust_shipping_address, java.lang.String cust_shipping_city, java.lang.String cust_shipping_zipcode, java.lang.String cust_shipping_state, java.lang.String cust_shipping_country, java.lang.String cust_shipping_phone, java.lang.String transac_products_name, java.lang.String transac_amount, java.lang.String transac_currency_code, java.lang.String transac_cc_type, java.lang.String transac_cc_number, java.lang.String transac_cc_month, java.lang.String transac_cc_year, java.lang.String transac_cc_cvc, java.lang.String customer_ip, java.lang.String merchant_data1, java.lang.String merchant_data2, java.lang.String merchant_data3, java.lang.String merchant_data4, java.lang.String option) throws java.rmi.RemoteException;

    /**
     * Adjust a Preauthorization
     */
    public TTransacResult processAdjust(java.lang.String account_id, java.lang.String account_password, java.lang.String account_sha, java.lang.String trans_id, java.lang.String merchant_data1, java.lang.String merchant_data2, java.lang.String merchant_data3, java.lang.String merchant_data4, java.lang.String option) throws java.rmi.RemoteException;

    /**
     * Refund a Payment
     */
    public TTransacResult processRefund(java.lang.String account_id, java.lang.String account_password, java.lang.String account_sha, java.lang.String trans_id, java.lang.String trans_amount, java.lang.String merchant_data1, java.lang.String merchant_data2, java.lang.String merchant_data3, java.lang.String merchant_data4, java.lang.String option) throws java.rmi.RemoteException;
}
