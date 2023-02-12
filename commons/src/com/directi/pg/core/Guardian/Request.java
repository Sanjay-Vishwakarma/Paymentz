/**
 * Request.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.Guardian;

public class Request  implements java.io.Serializable {
    private int merchant_id;

    private String merchant_token;

    private String hashkey;

    private String traceid;

    private float amount;

    private String currency;

    private String customer_firstname;

    private String customer_lastname;

    private String customer_email;

    private String customer_phone;

    private String customer_ip;

    private String billing_street;

    private String billing_postalcode;

    private String billing_city;

    private String billing_state;

    private String billing_country;

    private String cc_number;

    private String cc_type;

    private String cc_cardholder;

    private int cc_ex_month;

    private int cc_ex_year;

    private String cc_ccv;

    public Request() {
    }

    public Request(
           int merchant_id,
           String merchant_token,
           String hashkey,
           String traceid,
           float amount,
           String currency,
           String customer_firstname,
           String customer_lastname,
           String customer_email,
           String customer_phone,
           String customer_ip,
           String billing_street,
           String billing_postalcode,
           String billing_city,
           String billing_state,
           String billing_country,
           String cc_number,
           String cc_type,
           String cc_cardholder,
           int cc_ex_month,
           int cc_ex_year,
           String cc_ccv) {
           this.merchant_id = merchant_id;
           this.merchant_token = merchant_token;
           this.hashkey = hashkey;
           this.traceid = traceid;
           this.amount = amount;
           this.currency = currency;
           this.customer_firstname = customer_firstname;
           this.customer_lastname = customer_lastname;
           this.customer_email = customer_email;
           this.customer_phone = customer_phone;
           this.customer_ip = customer_ip;
           this.billing_street = billing_street;
           this.billing_postalcode = billing_postalcode;
           this.billing_city = billing_city;
           this.billing_state = billing_state;
           this.billing_country = billing_country;
           this.cc_number = cc_number;
           this.cc_type = cc_type;
           this.cc_cardholder = cc_cardholder;
           this.cc_ex_month = cc_ex_month;
           this.cc_ex_year = cc_ex_year;
           this.cc_ccv = cc_ccv;
    }


    /**
     * Gets the merchant_id value for this Request.
     * 
     * @return merchant_id
     */
    public int getMerchant_id() {
        return merchant_id;
    }


    /**
     * Sets the merchant_id value for this Request.
     * 
     * @param merchant_id
     */
    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }


    /**
     * Gets the merchant_token value for this Request.
     * 
     * @return merchant_token
     */
    public String getMerchant_token() {
        return merchant_token;
    }


    /**
     * Sets the merchant_token value for this Request.
     * 
     * @param merchant_token
     */
    public void setMerchant_token(String merchant_token) {
        this.merchant_token = merchant_token;
    }


    /**
     * Gets the hashkey value for this Request.
     * 
     * @return hashkey
     */
    public String getHashkey() {
        return hashkey;
    }


    /**
     * Sets the hashkey value for this Request.
     * 
     * @param hashkey
     */
    public void setHashkey(String hashkey) {
        this.hashkey = hashkey;
    }


    /**
     * Gets the traceid value for this Request.
     * 
     * @return traceid
     */
    public String getTraceid() {
        return traceid;
    }


    /**
     * Sets the traceid value for this Request.
     * 
     * @param traceid
     */
    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }


    /**
     * Gets the amount value for this Request.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this Request.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the currency value for this Request.
     * 
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this Request.
     * 
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }


    /**
     * Gets the customer_firstname value for this Request.
     * 
     * @return customer_firstname
     */
    public String getCustomer_firstname() {
        return customer_firstname;
    }


    /**
     * Sets the customer_firstname value for this Request.
     * 
     * @param customer_firstname
     */
    public void setCustomer_firstname(String customer_firstname) {
        this.customer_firstname = customer_firstname;
    }


    /**
     * Gets the customer_lastname value for this Request.
     * 
     * @return customer_lastname
     */
    public String getCustomer_lastname() {
        return customer_lastname;
    }


    /**
     * Sets the customer_lastname value for this Request.
     * 
     * @param customer_lastname
     */
    public void setCustomer_lastname(String customer_lastname) {
        this.customer_lastname = customer_lastname;
    }


    /**
     * Gets the customer_email value for this Request.
     * 
     * @return customer_email
     */
    public String getCustomer_email() {
        return customer_email;
    }


    /**
     * Sets the customer_email value for this Request.
     * 
     * @param customer_email
     */
    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }


    /**
     * Gets the customer_phone value for this Request.
     * 
     * @return customer_phone
     */
    public String getCustomer_phone() {
        return customer_phone;
    }


    /**
     * Sets the customer_phone value for this Request.
     * 
     * @param customer_phone
     */
    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }


    /**
     * Gets the customer_ip value for this Request.
     * 
     * @return customer_ip
     */
    public String getCustomer_ip() {
        return customer_ip;
    }


    /**
     * Sets the customer_ip value for this Request.
     * 
     * @param customer_ip
     */
    public void setCustomer_ip(String customer_ip) {
        this.customer_ip = customer_ip;
    }


    /**
     * Gets the billing_street value for this Request.
     * 
     * @return billing_street
     */
    public String getBilling_street() {
        return billing_street;
    }


    /**
     * Sets the billing_street value for this Request.
     * 
     * @param billing_street
     */
    public void setBilling_street(String billing_street) {
        this.billing_street = billing_street;
    }


    /**
     * Gets the billing_postalcode value for this Request.
     * 
     * @return billing_postalcode
     */
    public String getBilling_postalcode() {
        return billing_postalcode;
    }


    /**
     * Sets the billing_postalcode value for this Request.
     * 
     * @param billing_postalcode
     */
    public void setBilling_postalcode(String billing_postalcode) {
        this.billing_postalcode = billing_postalcode;
    }


    /**
     * Gets the billing_city value for this Request.
     * 
     * @return billing_city
     */
    public String getBilling_city() {
        return billing_city;
    }


    /**
     * Sets the billing_city value for this Request.
     * 
     * @param billing_city
     */
    public void setBilling_city(String billing_city) {
        this.billing_city = billing_city;
    }


    /**
     * Gets the billing_state value for this Request.
     * 
     * @return billing_state
     */
    public String getBilling_state() {
        return billing_state;
    }


    /**
     * Sets the billing_state value for this Request.
     * 
     * @param billing_state
     */
    public void setBilling_state(String billing_state) {
        this.billing_state = billing_state;
    }


    /**
     * Gets the billing_country value for this Request.
     * 
     * @return billing_country
     */
    public String getBilling_country() {
        return billing_country;
    }


    /**
     * Sets the billing_country value for this Request.
     * 
     * @param billing_country
     */
    public void setBilling_country(String billing_country) {
        this.billing_country = billing_country;
    }


    /**
     * Gets the cc_number value for this Request.
     * 
     * @return cc_number
     */
    public String getCc_number() {
        return cc_number;
    }


    /**
     * Sets the cc_number value for this Request.
     * 
     * @param cc_number
     */
    public void setCc_number(String cc_number) {
        this.cc_number = cc_number;
    }


    /**
     * Gets the cc_type value for this Request.
     * 
     * @return cc_type
     */
    public String getCc_type() {
        return cc_type;
    }


    /**
     * Sets the cc_type value for this Request.
     * 
     * @param cc_type
     */
    public void setCc_type(String cc_type) {
        this.cc_type = cc_type;
    }


    /**
     * Gets the cc_cardholder value for this Request.
     * 
     * @return cc_cardholder
     */
    public String getCc_cardholder() {
        return cc_cardholder;
    }


    /**
     * Sets the cc_cardholder value for this Request.
     * 
     * @param cc_cardholder
     */
    public void setCc_cardholder(String cc_cardholder) {
        this.cc_cardholder = cc_cardholder;
    }


    /**
     * Gets the cc_ex_month value for this Request.
     * 
     * @return cc_ex_month
     */
    public int getCc_ex_month() {
        return cc_ex_month;
    }


    /**
     * Sets the cc_ex_month value for this Request.
     * 
     * @param cc_ex_month
     */
    public void setCc_ex_month(int cc_ex_month) {
        this.cc_ex_month = cc_ex_month;
    }


    /**
     * Gets the cc_ex_year value for this Request.
     * 
     * @return cc_ex_year
     */
    public int getCc_ex_year() {
        return cc_ex_year;
    }


    /**
     * Sets the cc_ex_year value for this Request.
     * 
     * @param cc_ex_year
     */
    public void setCc_ex_year(int cc_ex_year) {
        this.cc_ex_year = cc_ex_year;
    }


    /**
     * Gets the cc_ccv value for this Request.
     * 
     * @return cc_ccv
     */
    public String getCc_ccv() {
        return cc_ccv;
    }


    /**
     * Sets the cc_ccv value for this Request.
     * 
     * @param cc_ccv
     */
    public void setCc_ccv(String cc_ccv) {
        this.cc_ccv = cc_ccv;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Request)) return false;
        Request other = (Request) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.merchant_id == other.getMerchant_id() &&
            ((this.merchant_token==null && other.getMerchant_token()==null) || 
             (this.merchant_token!=null &&
              this.merchant_token.equals(other.getMerchant_token()))) &&
            ((this.hashkey==null && other.getHashkey()==null) || 
             (this.hashkey!=null &&
              this.hashkey.equals(other.getHashkey()))) &&
            ((this.traceid==null && other.getTraceid()==null) || 
             (this.traceid!=null &&
              this.traceid.equals(other.getTraceid()))) &&
            this.amount == other.getAmount() &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.customer_firstname==null && other.getCustomer_firstname()==null) || 
             (this.customer_firstname!=null &&
              this.customer_firstname.equals(other.getCustomer_firstname()))) &&
            ((this.customer_lastname==null && other.getCustomer_lastname()==null) || 
             (this.customer_lastname!=null &&
              this.customer_lastname.equals(other.getCustomer_lastname()))) &&
            ((this.customer_email==null && other.getCustomer_email()==null) || 
             (this.customer_email!=null &&
              this.customer_email.equals(other.getCustomer_email()))) &&
            ((this.customer_phone==null && other.getCustomer_phone()==null) || 
             (this.customer_phone!=null &&
              this.customer_phone.equals(other.getCustomer_phone()))) &&
            ((this.customer_ip==null && other.getCustomer_ip()==null) || 
             (this.customer_ip!=null &&
              this.customer_ip.equals(other.getCustomer_ip()))) &&
            ((this.billing_street==null && other.getBilling_street()==null) || 
             (this.billing_street!=null &&
              this.billing_street.equals(other.getBilling_street()))) &&
            ((this.billing_postalcode==null && other.getBilling_postalcode()==null) || 
             (this.billing_postalcode!=null &&
              this.billing_postalcode.equals(other.getBilling_postalcode()))) &&
            ((this.billing_city==null && other.getBilling_city()==null) || 
             (this.billing_city!=null &&
              this.billing_city.equals(other.getBilling_city()))) &&
            ((this.billing_state==null && other.getBilling_state()==null) || 
             (this.billing_state!=null &&
              this.billing_state.equals(other.getBilling_state()))) &&
            ((this.billing_country==null && other.getBilling_country()==null) || 
             (this.billing_country!=null &&
              this.billing_country.equals(other.getBilling_country()))) &&
            ((this.cc_number==null && other.getCc_number()==null) || 
             (this.cc_number!=null &&
              this.cc_number.equals(other.getCc_number()))) &&
            ((this.cc_type==null && other.getCc_type()==null) || 
             (this.cc_type!=null &&
              this.cc_type.equals(other.getCc_type()))) &&
            ((this.cc_cardholder==null && other.getCc_cardholder()==null) || 
             (this.cc_cardholder!=null &&
              this.cc_cardholder.equals(other.getCc_cardholder()))) &&
            this.cc_ex_month == other.getCc_ex_month() &&
            this.cc_ex_year == other.getCc_ex_year() &&
            ((this.cc_ccv==null && other.getCc_ccv()==null) || 
             (this.cc_ccv!=null &&
              this.cc_ccv.equals(other.getCc_ccv())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getMerchant_id();
        if (getMerchant_token() != null) {
            _hashCode += getMerchant_token().hashCode();
        }
        if (getHashkey() != null) {
            _hashCode += getHashkey().hashCode();
        }
        if (getTraceid() != null) {
            _hashCode += getTraceid().hashCode();
        }
        _hashCode += new Float(getAmount()).hashCode();
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getCustomer_firstname() != null) {
            _hashCode += getCustomer_firstname().hashCode();
        }
        if (getCustomer_lastname() != null) {
            _hashCode += getCustomer_lastname().hashCode();
        }
        if (getCustomer_email() != null) {
            _hashCode += getCustomer_email().hashCode();
        }
        if (getCustomer_phone() != null) {
            _hashCode += getCustomer_phone().hashCode();
        }
        if (getCustomer_ip() != null) {
            _hashCode += getCustomer_ip().hashCode();
        }
        if (getBilling_street() != null) {
            _hashCode += getBilling_street().hashCode();
        }
        if (getBilling_postalcode() != null) {
            _hashCode += getBilling_postalcode().hashCode();
        }
        if (getBilling_city() != null) {
            _hashCode += getBilling_city().hashCode();
        }
        if (getBilling_state() != null) {
            _hashCode += getBilling_state().hashCode();
        }
        if (getBilling_country() != null) {
            _hashCode += getBilling_country().hashCode();
        }
        if (getCc_number() != null) {
            _hashCode += getCc_number().hashCode();
        }
        if (getCc_type() != null) {
            _hashCode += getCc_type().hashCode();
        }
        if (getCc_cardholder() != null) {
            _hashCode += getCc_cardholder().hashCode();
        }
        _hashCode += getCc_ex_month();
        _hashCode += getCc_ex_year();
        if (getCc_ccv() != null) {
            _hashCode += getCc_ccv().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Request.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:ccgwwsdl", "Request"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchant_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchant_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchant_token");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchant_token"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hashkey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "hashkey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("traceid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "traceid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer_firstname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customer_firstname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer_lastname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customer_lastname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer_email");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customer_email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer_phone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customer_phone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customer_ip");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customer_ip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billing_street");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billing_street"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billing_postalcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billing_postalcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billing_city");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billing_city"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billing_state");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billing_state"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billing_country");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billing_country"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cc_number");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cc_number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cc_type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cc_type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cc_cardholder");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cc_cardholder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cc_ex_month");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cc_ex_month"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cc_ex_year");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cc_ex_year"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cc_ccv");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cc_ccv"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
