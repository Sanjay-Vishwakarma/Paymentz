/**
 * AuthorizeRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.gold24.core.message;

public class AuthorizeRequest  implements java.io.Serializable {
    private int merchantid;

    private java.lang.String password;

    private java.lang.String cardholderip;

    private java.lang.String merchantorderid;

    private int amount;

    private java.lang.String currency;

    private java.lang.String cardholderfirstname;

    private java.lang.String cardholdersurname;

    private java.lang.String cardholderaddress;

    private java.lang.String cardholderzipcode;

    private java.lang.String cardholdercity;

    private java.lang.String cardholderstate;

    private java.lang.String cardholdercountrycode;

    private java.lang.String cardholderphone;

    private java.lang.String cardholderemail;

    private java.lang.String cardnumber;

    private java.lang.String cardsecuritycode;

    private java.lang.String cardexpiremonth;

    private java.lang.String cardexpireyear;

    public AuthorizeRequest() {
    }

    public AuthorizeRequest(
           int merchantid,
           java.lang.String password,
           java.lang.String cardholderip,
           java.lang.String merchantorderid,
           int amount,
           java.lang.String currency,
           java.lang.String cardholderfirstname,
           java.lang.String cardholdersurname,
           java.lang.String cardholderaddress,
           java.lang.String cardholderzipcode,
           java.lang.String cardholdercity,
           java.lang.String cardholderstate,
           java.lang.String cardholdercountrycode,
           java.lang.String cardholderphone,
           java.lang.String cardholderemail,
           java.lang.String cardnumber,
           java.lang.String cardsecuritycode,
           java.lang.String cardexpiremonth,
           java.lang.String cardexpireyear) {
           this.merchantid = merchantid;
           this.password = password;
           this.cardholderip = cardholderip;
           this.merchantorderid = merchantorderid;
           this.amount = amount;
           this.currency = currency;
           this.cardholderfirstname = cardholderfirstname;
           this.cardholdersurname = cardholdersurname;
           this.cardholderaddress = cardholderaddress;
           this.cardholderzipcode = cardholderzipcode;
           this.cardholdercity = cardholdercity;
           this.cardholderstate = cardholderstate;
           this.cardholdercountrycode = cardholdercountrycode;
           this.cardholderphone = cardholderphone;
           this.cardholderemail = cardholderemail;
           this.cardnumber = cardnumber;
           this.cardsecuritycode = cardsecuritycode;
           this.cardexpiremonth = cardexpiremonth;
           this.cardexpireyear = cardexpireyear;
    }


    /**
     * Gets the merchantid value for this AuthorizeRequest.
     * 
     * @return merchantid
     */
    public int getMerchantid() {
        return merchantid;
    }


    /**
     * Sets the merchantid value for this AuthorizeRequest.
     * 
     * @param merchantid
     */
    public void setMerchantid(int merchantid) {
        this.merchantid = merchantid;
    }


    /**
     * Gets the password value for this AuthorizeRequest.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this AuthorizeRequest.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the cardholderip value for this AuthorizeRequest.
     * 
     * @return cardholderip
     */
    public java.lang.String getCardholderip() {
        return cardholderip;
    }


    /**
     * Sets the cardholderip value for this AuthorizeRequest.
     * 
     * @param cardholderip
     */
    public void setCardholderip(java.lang.String cardholderip) {
        this.cardholderip = cardholderip;
    }


    /**
     * Gets the merchantorderid value for this AuthorizeRequest.
     * 
     * @return merchantorderid
     */
    public java.lang.String getMerchantorderid() {
        return merchantorderid;
    }


    /**
     * Sets the merchantorderid value for this AuthorizeRequest.
     * 
     * @param merchantorderid
     */
    public void setMerchantorderid(java.lang.String merchantorderid) {
        this.merchantorderid = merchantorderid;
    }


    /**
     * Gets the amount value for this AuthorizeRequest.
     * 
     * @return amount
     */
    public int getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this AuthorizeRequest.
     * 
     * @param amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }


    /**
     * Gets the currency value for this AuthorizeRequest.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this AuthorizeRequest.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the cardholderfirstname value for this AuthorizeRequest.
     * 
     * @return cardholderfirstname
     */
    public java.lang.String getCardholderfirstname() {
        return cardholderfirstname;
    }


    /**
     * Sets the cardholderfirstname value for this AuthorizeRequest.
     * 
     * @param cardholderfirstname
     */
    public void setCardholderfirstname(java.lang.String cardholderfirstname) {
        this.cardholderfirstname = cardholderfirstname;
    }


    /**
     * Gets the cardholdersurname value for this AuthorizeRequest.
     * 
     * @return cardholdersurname
     */
    public java.lang.String getCardholdersurname() {
        return cardholdersurname;
    }


    /**
     * Sets the cardholdersurname value for this AuthorizeRequest.
     * 
     * @param cardholdersurname
     */
    public void setCardholdersurname(java.lang.String cardholdersurname) {
        this.cardholdersurname = cardholdersurname;
    }


    /**
     * Gets the cardholderaddress value for this AuthorizeRequest.
     * 
     * @return cardholderaddress
     */
    public java.lang.String getCardholderaddress() {
        return cardholderaddress;
    }


    /**
     * Sets the cardholderaddress value for this AuthorizeRequest.
     * 
     * @param cardholderaddress
     */
    public void setCardholderaddress(java.lang.String cardholderaddress) {
        this.cardholderaddress = cardholderaddress;
    }


    /**
     * Gets the cardholderzipcode value for this AuthorizeRequest.
     * 
     * @return cardholderzipcode
     */
    public java.lang.String getCardholderzipcode() {
        return cardholderzipcode;
    }


    /**
     * Sets the cardholderzipcode value for this AuthorizeRequest.
     * 
     * @param cardholderzipcode
     */
    public void setCardholderzipcode(java.lang.String cardholderzipcode) {
        this.cardholderzipcode = cardholderzipcode;
    }


    /**
     * Gets the cardholdercity value for this AuthorizeRequest.
     * 
     * @return cardholdercity
     */
    public java.lang.String getCardholdercity() {
        return cardholdercity;
    }


    /**
     * Sets the cardholdercity value for this AuthorizeRequest.
     * 
     * @param cardholdercity
     */
    public void setCardholdercity(java.lang.String cardholdercity) {
        this.cardholdercity = cardholdercity;
    }


    /**
     * Gets the cardholderstate value for this AuthorizeRequest.
     * 
     * @return cardholderstate
     */
    public java.lang.String getCardholderstate() {
        return cardholderstate;
    }


    /**
     * Sets the cardholderstate value for this AuthorizeRequest.
     * 
     * @param cardholderstate
     */
    public void setCardholderstate(java.lang.String cardholderstate) {
        this.cardholderstate = cardholderstate;
    }


    /**
     * Gets the cardholdercountrycode value for this AuthorizeRequest.
     * 
     * @return cardholdercountrycode
     */
    public java.lang.String getCardholdercountrycode() {
        return cardholdercountrycode;
    }


    /**
     * Sets the cardholdercountrycode value for this AuthorizeRequest.
     * 
     * @param cardholdercountrycode
     */
    public void setCardholdercountrycode(java.lang.String cardholdercountrycode) {
        this.cardholdercountrycode = cardholdercountrycode;
    }


    /**
     * Gets the cardholderphone value for this AuthorizeRequest.
     * 
     * @return cardholderphone
     */
    public java.lang.String getCardholderphone() {
        return cardholderphone;
    }


    /**
     * Sets the cardholderphone value for this AuthorizeRequest.
     * 
     * @param cardholderphone
     */
    public void setCardholderphone(java.lang.String cardholderphone) {
        this.cardholderphone = cardholderphone;
    }


    /**
     * Gets the cardholderemail value for this AuthorizeRequest.
     * 
     * @return cardholderemail
     */
    public java.lang.String getCardholderemail() {
        return cardholderemail;
    }


    /**
     * Sets the cardholderemail value for this AuthorizeRequest.
     * 
     * @param cardholderemail
     */
    public void setCardholderemail(java.lang.String cardholderemail) {
        this.cardholderemail = cardholderemail;
    }


    /**
     * Gets the cardnumber value for this AuthorizeRequest.
     * 
     * @return cardnumber
     */
    public java.lang.String getCardnumber() {
        return cardnumber;
    }


    /**
     * Sets the cardnumber value for this AuthorizeRequest.
     * 
     * @param cardnumber
     */
    public void setCardnumber(java.lang.String cardnumber) {
        this.cardnumber = cardnumber;
    }


    /**
     * Gets the cardsecuritycode value for this AuthorizeRequest.
     * 
     * @return cardsecuritycode
     */
    public java.lang.String getCardsecuritycode() {
        return cardsecuritycode;
    }


    /**
     * Sets the cardsecuritycode value for this AuthorizeRequest.
     * 
     * @param cardsecuritycode
     */
    public void setCardsecuritycode(java.lang.String cardsecuritycode) {
        this.cardsecuritycode = cardsecuritycode;
    }


    /**
     * Gets the cardexpiremonth value for this AuthorizeRequest.
     * 
     * @return cardexpiremonth
     */
    public java.lang.String getCardexpiremonth() {
        return cardexpiremonth;
    }


    /**
     * Sets the cardexpiremonth value for this AuthorizeRequest.
     * 
     * @param cardexpiremonth
     */
    public void setCardexpiremonth(java.lang.String cardexpiremonth) {
        this.cardexpiremonth = cardexpiremonth;
    }


    /**
     * Gets the cardexpireyear value for this AuthorizeRequest.
     * 
     * @return cardexpireyear
     */
    public java.lang.String getCardexpireyear() {
        return cardexpireyear;
    }


    /**
     * Sets the cardexpireyear value for this AuthorizeRequest.
     * 
     * @param cardexpireyear
     */
    public void setCardexpireyear(java.lang.String cardexpireyear) {
        this.cardexpireyear = cardexpireyear;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AuthorizeRequest)) return false;
        AuthorizeRequest other = (AuthorizeRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.merchantid == other.getMerchantid() &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.cardholderip==null && other.getCardholderip()==null) || 
             (this.cardholderip!=null &&
              this.cardholderip.equals(other.getCardholderip()))) &&
            ((this.merchantorderid==null && other.getMerchantorderid()==null) || 
             (this.merchantorderid!=null &&
              this.merchantorderid.equals(other.getMerchantorderid()))) &&
            this.amount == other.getAmount() &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.cardholderfirstname==null && other.getCardholderfirstname()==null) || 
             (this.cardholderfirstname!=null &&
              this.cardholderfirstname.equals(other.getCardholderfirstname()))) &&
            ((this.cardholdersurname==null && other.getCardholdersurname()==null) || 
             (this.cardholdersurname!=null &&
              this.cardholdersurname.equals(other.getCardholdersurname()))) &&
            ((this.cardholderaddress==null && other.getCardholderaddress()==null) || 
             (this.cardholderaddress!=null &&
              this.cardholderaddress.equals(other.getCardholderaddress()))) &&
            ((this.cardholderzipcode==null && other.getCardholderzipcode()==null) || 
             (this.cardholderzipcode!=null &&
              this.cardholderzipcode.equals(other.getCardholderzipcode()))) &&
            ((this.cardholdercity==null && other.getCardholdercity()==null) || 
             (this.cardholdercity!=null &&
              this.cardholdercity.equals(other.getCardholdercity()))) &&
            ((this.cardholderstate==null && other.getCardholderstate()==null) || 
             (this.cardholderstate!=null &&
              this.cardholderstate.equals(other.getCardholderstate()))) &&
            ((this.cardholdercountrycode==null && other.getCardholdercountrycode()==null) || 
             (this.cardholdercountrycode!=null &&
              this.cardholdercountrycode.equals(other.getCardholdercountrycode()))) &&
            ((this.cardholderphone==null && other.getCardholderphone()==null) || 
             (this.cardholderphone!=null &&
              this.cardholderphone.equals(other.getCardholderphone()))) &&
            ((this.cardholderemail==null && other.getCardholderemail()==null) || 
             (this.cardholderemail!=null &&
              this.cardholderemail.equals(other.getCardholderemail()))) &&
            ((this.cardnumber==null && other.getCardnumber()==null) || 
             (this.cardnumber!=null &&
              this.cardnumber.equals(other.getCardnumber()))) &&
            ((this.cardsecuritycode==null && other.getCardsecuritycode()==null) || 
             (this.cardsecuritycode!=null &&
              this.cardsecuritycode.equals(other.getCardsecuritycode()))) &&
            ((this.cardexpiremonth==null && other.getCardexpiremonth()==null) || 
             (this.cardexpiremonth!=null &&
              this.cardexpiremonth.equals(other.getCardexpiremonth()))) &&
            ((this.cardexpireyear==null && other.getCardexpireyear()==null) || 
             (this.cardexpireyear!=null &&
              this.cardexpireyear.equals(other.getCardexpireyear())));
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
        _hashCode += getMerchantid();
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getCardholderip() != null) {
            _hashCode += getCardholderip().hashCode();
        }
        if (getMerchantorderid() != null) {
            _hashCode += getMerchantorderid().hashCode();
        }
        _hashCode += getAmount();
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getCardholderfirstname() != null) {
            _hashCode += getCardholderfirstname().hashCode();
        }
        if (getCardholdersurname() != null) {
            _hashCode += getCardholdersurname().hashCode();
        }
        if (getCardholderaddress() != null) {
            _hashCode += getCardholderaddress().hashCode();
        }
        if (getCardholderzipcode() != null) {
            _hashCode += getCardholderzipcode().hashCode();
        }
        if (getCardholdercity() != null) {
            _hashCode += getCardholdercity().hashCode();
        }
        if (getCardholderstate() != null) {
            _hashCode += getCardholderstate().hashCode();
        }
        if (getCardholdercountrycode() != null) {
            _hashCode += getCardholdercountrycode().hashCode();
        }
        if (getCardholderphone() != null) {
            _hashCode += getCardholderphone().hashCode();
        }
        if (getCardholderemail() != null) {
            _hashCode += getCardholderemail().hashCode();
        }
        if (getCardnumber() != null) {
            _hashCode += getCardnumber().hashCode();
        }
        if (getCardsecuritycode() != null) {
            _hashCode += getCardsecuritycode().hashCode();
        }
        if (getCardexpiremonth() != null) {
            _hashCode += getCardexpiremonth().hashCode();
        }
        if (getCardexpireyear() != null) {
            _hashCode += getCardexpireyear().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AuthorizeRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "AuthorizeRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholderip");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholderip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantorderid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantorderid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholderfirstname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholderfirstname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholdersurname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholdersurname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholderaddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholderaddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholderzipcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholderzipcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholdercity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholdercity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholderstate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholderstate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholdercountrycode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholdercountrycode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholderphone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholderphone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardholderemail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardholderemail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardnumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardnumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardsecuritycode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardsecuritycode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardexpiremonth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardexpiremonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardexpireyear");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardexpireyear"));
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
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
