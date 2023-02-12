/**
 * DirectRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.ugspay.message;

public class DirectRequest  implements java.io.Serializable {
    private int websiteID;

    private java.lang.String password;

    private java.lang.String orderID;

    private java.lang.String customerIP;

    private int amount;

    private java.lang.String currency;

    private java.lang.String cardHolderName;

    private java.lang.String cardHolderAddress;

    private java.lang.String cardHolderZipcode;

    private java.lang.String cardHolderCity;

    private java.lang.String cardHolderState;

    private java.lang.String cardHolderCountryCode;

    private java.lang.String cardHolderPhone;

    private java.lang.String cardHolderEmail;

    private java.lang.String cardNumber;

    private java.lang.String cardSecurityCode;

    private java.lang.String cardExpireMonth;

    private java.lang.String cardExpireYear;

    private java.lang.String AVSPolicy;

    private java.lang.String FSPolicy;

    private java.lang.String userVar1;

    private java.lang.String userVar2;

    private java.lang.String userVar3;

    private java.lang.String userVar4;

    public DirectRequest() {
    }

    public DirectRequest(
           int websiteID,
           java.lang.String password,
           java.lang.String orderID,
           java.lang.String customerIP,
           int amount,
           java.lang.String currency,
           java.lang.String cardHolderName,
           java.lang.String cardHolderAddress,
           java.lang.String cardHolderZipcode,
           java.lang.String cardHolderCity,
           java.lang.String cardHolderState,
           java.lang.String cardHolderCountryCode,
           java.lang.String cardHolderPhone,
           java.lang.String cardHolderEmail,
           java.lang.String cardNumber,
           java.lang.String cardSecurityCode,
           java.lang.String cardExpireMonth,
           java.lang.String cardExpireYear,
           java.lang.String AVSPolicy,
           java.lang.String FSPolicy,
           java.lang.String userVar1,
           java.lang.String userVar2,
           java.lang.String userVar3,
           java.lang.String userVar4) {
           this.websiteID = websiteID;
           this.password = password;
           this.orderID = orderID;
           this.customerIP = customerIP;
           this.amount = amount;
           this.currency = currency;
           this.cardHolderName = cardHolderName;
           this.cardHolderAddress = cardHolderAddress;
           this.cardHolderZipcode = cardHolderZipcode;
           this.cardHolderCity = cardHolderCity;
           this.cardHolderState = cardHolderState;
           this.cardHolderCountryCode = cardHolderCountryCode;
           this.cardHolderPhone = cardHolderPhone;
           this.cardHolderEmail = cardHolderEmail;
           this.cardNumber = cardNumber;
           this.cardSecurityCode = cardSecurityCode;
           this.cardExpireMonth = cardExpireMonth;
           this.cardExpireYear = cardExpireYear;
           this.AVSPolicy = AVSPolicy;
           this.FSPolicy = FSPolicy;
           this.userVar1 = userVar1;
           this.userVar2 = userVar2;
           this.userVar3 = userVar3;
           this.userVar4 = userVar4;
    }


    /**
     * Gets the websiteID value for this DirectRequest.
     * 
     * @return websiteID
     */
    public int getWebsiteID() {
        return websiteID;
    }


    /**
     * Sets the websiteID value for this DirectRequest.
     * 
     * @param websiteID
     */
    public void setWebsiteID(int websiteID) {
        this.websiteID = websiteID;
    }


    /**
     * Gets the password value for this DirectRequest.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this DirectRequest.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the orderID value for this DirectRequest.
     * 
     * @return orderID
     */
    public java.lang.String getOrderID() {
        return orderID;
    }


    /**
     * Sets the orderID value for this DirectRequest.
     * 
     * @param orderID
     */
    public void setOrderID(java.lang.String orderID) {
        this.orderID = orderID;
    }


    /**
     * Gets the customerIP value for this DirectRequest.
     * 
     * @return customerIP
     */
    public java.lang.String getCustomerIP() {
        return customerIP;
    }


    /**
     * Sets the customerIP value for this DirectRequest.
     * 
     * @param customerIP
     */
    public void setCustomerIP(java.lang.String customerIP) {
        this.customerIP = customerIP;
    }


    /**
     * Gets the amount value for this DirectRequest.
     * 
     * @return amount
     */
    public int getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this DirectRequest.
     * 
     * @param amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }


    /**
     * Gets the currency value for this DirectRequest.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this DirectRequest.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the cardHolderName value for this DirectRequest.
     * 
     * @return cardHolderName
     */
    public java.lang.String getCardHolderName() {
        return cardHolderName;
    }


    /**
     * Sets the cardHolderName value for this DirectRequest.
     * 
     * @param cardHolderName
     */
    public void setCardHolderName(java.lang.String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }


    /**
     * Gets the cardHolderAddress value for this DirectRequest.
     * 
     * @return cardHolderAddress
     */
    public java.lang.String getCardHolderAddress() {
        return cardHolderAddress;
    }


    /**
     * Sets the cardHolderAddress value for this DirectRequest.
     * 
     * @param cardHolderAddress
     */
    public void setCardHolderAddress(java.lang.String cardHolderAddress) {
        this.cardHolderAddress = cardHolderAddress;
    }


    /**
     * Gets the cardHolderZipcode value for this DirectRequest.
     * 
     * @return cardHolderZipcode
     */
    public java.lang.String getCardHolderZipcode() {
        return cardHolderZipcode;
    }


    /**
     * Sets the cardHolderZipcode value for this DirectRequest.
     * 
     * @param cardHolderZipcode
     */
    public void setCardHolderZipcode(java.lang.String cardHolderZipcode) {
        this.cardHolderZipcode = cardHolderZipcode;
    }


    /**
     * Gets the cardHolderCity value for this DirectRequest.
     * 
     * @return cardHolderCity
     */
    public java.lang.String getCardHolderCity() {
        return cardHolderCity;
    }


    /**
     * Sets the cardHolderCity value for this DirectRequest.
     * 
     * @param cardHolderCity
     */
    public void setCardHolderCity(java.lang.String cardHolderCity) {
        this.cardHolderCity = cardHolderCity;
    }


    /**
     * Gets the cardHolderState value for this DirectRequest.
     * 
     * @return cardHolderState
     */
    public java.lang.String getCardHolderState() {
        return cardHolderState;
    }


    /**
     * Sets the cardHolderState value for this DirectRequest.
     * 
     * @param cardHolderState
     */
    public void setCardHolderState(java.lang.String cardHolderState) {
        this.cardHolderState = cardHolderState;
    }


    /**
     * Gets the cardHolderCountryCode value for this DirectRequest.
     * 
     * @return cardHolderCountryCode
     */
    public java.lang.String getCardHolderCountryCode() {
        return cardHolderCountryCode;
    }


    /**
     * Sets the cardHolderCountryCode value for this DirectRequest.
     * 
     * @param cardHolderCountryCode
     */
    public void setCardHolderCountryCode(java.lang.String cardHolderCountryCode) {
        this.cardHolderCountryCode = cardHolderCountryCode;
    }


    /**
     * Gets the cardHolderPhone value for this DirectRequest.
     * 
     * @return cardHolderPhone
     */
    public java.lang.String getCardHolderPhone() {
        return cardHolderPhone;
    }


    /**
     * Sets the cardHolderPhone value for this DirectRequest.
     * 
     * @param cardHolderPhone
     */
    public void setCardHolderPhone(java.lang.String cardHolderPhone) {
        this.cardHolderPhone = cardHolderPhone;
    }


    /**
     * Gets the cardHolderEmail value for this DirectRequest.
     * 
     * @return cardHolderEmail
     */
    public java.lang.String getCardHolderEmail() {
        return cardHolderEmail;
    }


    /**
     * Sets the cardHolderEmail value for this DirectRequest.
     * 
     * @param cardHolderEmail
     */
    public void setCardHolderEmail(java.lang.String cardHolderEmail) {
        this.cardHolderEmail = cardHolderEmail;
    }


    /**
     * Gets the cardNumber value for this DirectRequest.
     * 
     * @return cardNumber
     */
    public java.lang.String getCardNumber() {
        return cardNumber;
    }


    /**
     * Sets the cardNumber value for this DirectRequest.
     * 
     * @param cardNumber
     */
    public void setCardNumber(java.lang.String cardNumber) {
        this.cardNumber = cardNumber;
    }


    /**
     * Gets the cardSecurityCode value for this DirectRequest.
     * 
     * @return cardSecurityCode
     */
    public java.lang.String getCardSecurityCode() {
        return cardSecurityCode;
    }


    /**
     * Sets the cardSecurityCode value for this DirectRequest.
     * 
     * @param cardSecurityCode
     */
    public void setCardSecurityCode(java.lang.String cardSecurityCode) {
        this.cardSecurityCode = cardSecurityCode;
    }


    /**
     * Gets the cardExpireMonth value for this DirectRequest.
     * 
     * @return cardExpireMonth
     */
    public java.lang.String getCardExpireMonth() {
        return cardExpireMonth;
    }


    /**
     * Sets the cardExpireMonth value for this DirectRequest.
     * 
     * @param cardExpireMonth
     */
    public void setCardExpireMonth(java.lang.String cardExpireMonth) {
        this.cardExpireMonth = cardExpireMonth;
    }


    /**
     * Gets the cardExpireYear value for this DirectRequest.
     * 
     * @return cardExpireYear
     */
    public java.lang.String getCardExpireYear() {
        return cardExpireYear;
    }


    /**
     * Sets the cardExpireYear value for this DirectRequest.
     * 
     * @param cardExpireYear
     */
    public void setCardExpireYear(java.lang.String cardExpireYear) {
        this.cardExpireYear = cardExpireYear;
    }


    /**
     * Gets the AVSPolicy value for this DirectRequest.
     * 
     * @return AVSPolicy
     */
    public java.lang.String getAVSPolicy() {
        return AVSPolicy;
    }


    /**
     * Sets the AVSPolicy value for this DirectRequest.
     * 
     * @param AVSPolicy
     */
    public void setAVSPolicy(java.lang.String AVSPolicy) {
        this.AVSPolicy = AVSPolicy;
    }


    /**
     * Gets the FSPolicy value for this DirectRequest.
     * 
     * @return FSPolicy
     */
    public java.lang.String getFSPolicy() {
        return FSPolicy;
    }


    /**
     * Sets the FSPolicy value for this DirectRequest.
     * 
     * @param FSPolicy
     */
    public void setFSPolicy(java.lang.String FSPolicy) {
        this.FSPolicy = FSPolicy;
    }


    /**
     * Gets the userVar1 value for this DirectRequest.
     * 
     * @return userVar1
     */
    public java.lang.String getUserVar1() {
        return userVar1;
    }


    /**
     * Sets the userVar1 value for this DirectRequest.
     * 
     * @param userVar1
     */
    public void setUserVar1(java.lang.String userVar1) {
        this.userVar1 = userVar1;
    }


    /**
     * Gets the userVar2 value for this DirectRequest.
     * 
     * @return userVar2
     */
    public java.lang.String getUserVar2() {
        return userVar2;
    }


    /**
     * Sets the userVar2 value for this DirectRequest.
     * 
     * @param userVar2
     */
    public void setUserVar2(java.lang.String userVar2) {
        this.userVar2 = userVar2;
    }


    /**
     * Gets the userVar3 value for this DirectRequest.
     * 
     * @return userVar3
     */
    public java.lang.String getUserVar3() {
        return userVar3;
    }


    /**
     * Sets the userVar3 value for this DirectRequest.
     * 
     * @param userVar3
     */
    public void setUserVar3(java.lang.String userVar3) {
        this.userVar3 = userVar3;
    }


    /**
     * Gets the userVar4 value for this DirectRequest.
     * 
     * @return userVar4
     */
    public java.lang.String getUserVar4() {
        return userVar4;
    }


    /**
     * Sets the userVar4 value for this DirectRequest.
     * 
     * @param userVar4
     */
    public void setUserVar4(java.lang.String userVar4) {
        this.userVar4 = userVar4;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DirectRequest)) return false;
        DirectRequest other = (DirectRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.websiteID == other.getWebsiteID() &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.orderID==null && other.getOrderID()==null) || 
             (this.orderID!=null &&
              this.orderID.equals(other.getOrderID()))) &&
            ((this.customerIP==null && other.getCustomerIP()==null) || 
             (this.customerIP!=null &&
              this.customerIP.equals(other.getCustomerIP()))) &&
            this.amount == other.getAmount() &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.cardHolderName==null && other.getCardHolderName()==null) || 
             (this.cardHolderName!=null &&
              this.cardHolderName.equals(other.getCardHolderName()))) &&
            ((this.cardHolderAddress==null && other.getCardHolderAddress()==null) || 
             (this.cardHolderAddress!=null &&
              this.cardHolderAddress.equals(other.getCardHolderAddress()))) &&
            ((this.cardHolderZipcode==null && other.getCardHolderZipcode()==null) || 
             (this.cardHolderZipcode!=null &&
              this.cardHolderZipcode.equals(other.getCardHolderZipcode()))) &&
            ((this.cardHolderCity==null && other.getCardHolderCity()==null) || 
             (this.cardHolderCity!=null &&
              this.cardHolderCity.equals(other.getCardHolderCity()))) &&
            ((this.cardHolderState==null && other.getCardHolderState()==null) || 
             (this.cardHolderState!=null &&
              this.cardHolderState.equals(other.getCardHolderState()))) &&
            ((this.cardHolderCountryCode==null && other.getCardHolderCountryCode()==null) || 
             (this.cardHolderCountryCode!=null &&
              this.cardHolderCountryCode.equals(other.getCardHolderCountryCode()))) &&
            ((this.cardHolderPhone==null && other.getCardHolderPhone()==null) || 
             (this.cardHolderPhone!=null &&
              this.cardHolderPhone.equals(other.getCardHolderPhone()))) &&
            ((this.cardHolderEmail==null && other.getCardHolderEmail()==null) || 
             (this.cardHolderEmail!=null &&
              this.cardHolderEmail.equals(other.getCardHolderEmail()))) &&
            ((this.cardNumber==null && other.getCardNumber()==null) || 
             (this.cardNumber!=null &&
              this.cardNumber.equals(other.getCardNumber()))) &&
            ((this.cardSecurityCode==null && other.getCardSecurityCode()==null) || 
             (this.cardSecurityCode!=null &&
              this.cardSecurityCode.equals(other.getCardSecurityCode()))) &&
            ((this.cardExpireMonth==null && other.getCardExpireMonth()==null) || 
             (this.cardExpireMonth!=null &&
              this.cardExpireMonth.equals(other.getCardExpireMonth()))) &&
            ((this.cardExpireYear==null && other.getCardExpireYear()==null) || 
             (this.cardExpireYear!=null &&
              this.cardExpireYear.equals(other.getCardExpireYear()))) &&
            ((this.AVSPolicy==null && other.getAVSPolicy()==null) || 
             (this.AVSPolicy!=null &&
              this.AVSPolicy.equals(other.getAVSPolicy()))) &&
            ((this.FSPolicy==null && other.getFSPolicy()==null) || 
             (this.FSPolicy!=null &&
              this.FSPolicy.equals(other.getFSPolicy()))) &&
            ((this.userVar1==null && other.getUserVar1()==null) || 
             (this.userVar1!=null &&
              this.userVar1.equals(other.getUserVar1()))) &&
            ((this.userVar2==null && other.getUserVar2()==null) || 
             (this.userVar2!=null &&
              this.userVar2.equals(other.getUserVar2()))) &&
            ((this.userVar3==null && other.getUserVar3()==null) || 
             (this.userVar3!=null &&
              this.userVar3.equals(other.getUserVar3()))) &&
            ((this.userVar4==null && other.getUserVar4()==null) || 
             (this.userVar4!=null &&
              this.userVar4.equals(other.getUserVar4())));
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
        _hashCode += getWebsiteID();
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getOrderID() != null) {
            _hashCode += getOrderID().hashCode();
        }
        if (getCustomerIP() != null) {
            _hashCode += getCustomerIP().hashCode();
        }
        _hashCode += getAmount();
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getCardHolderName() != null) {
            _hashCode += getCardHolderName().hashCode();
        }
        if (getCardHolderAddress() != null) {
            _hashCode += getCardHolderAddress().hashCode();
        }
        if (getCardHolderZipcode() != null) {
            _hashCode += getCardHolderZipcode().hashCode();
        }
        if (getCardHolderCity() != null) {
            _hashCode += getCardHolderCity().hashCode();
        }
        if (getCardHolderState() != null) {
            _hashCode += getCardHolderState().hashCode();
        }
        if (getCardHolderCountryCode() != null) {
            _hashCode += getCardHolderCountryCode().hashCode();
        }
        if (getCardHolderPhone() != null) {
            _hashCode += getCardHolderPhone().hashCode();
        }
        if (getCardHolderEmail() != null) {
            _hashCode += getCardHolderEmail().hashCode();
        }
        if (getCardNumber() != null) {
            _hashCode += getCardNumber().hashCode();
        }
        if (getCardSecurityCode() != null) {
            _hashCode += getCardSecurityCode().hashCode();
        }
        if (getCardExpireMonth() != null) {
            _hashCode += getCardExpireMonth().hashCode();
        }
        if (getCardExpireYear() != null) {
            _hashCode += getCardExpireYear().hashCode();
        }
        if (getAVSPolicy() != null) {
            _hashCode += getAVSPolicy().hashCode();
        }
        if (getFSPolicy() != null) {
            _hashCode += getFSPolicy().hashCode();
        }
        if (getUserVar1() != null) {
            _hashCode += getUserVar1().hashCode();
        }
        if (getUserVar2() != null) {
            _hashCode += getUserVar2().hashCode();
        }
        if (getUserVar3() != null) {
            _hashCode += getUserVar3().hashCode();
        }
        if (getUserVar4() != null) {
            _hashCode += getUserVar4().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DirectRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "DirectRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("websiteID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "websiteID"));
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
        elemField.setFieldName("orderID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "orderID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerIP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customerIP"));
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
        elemField.setFieldName("cardHolderName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderZipcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderZipcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderCity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderCountryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderCountryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderPhone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderPhone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderEmail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderEmail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardSecurityCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardSecurityCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardExpireMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardExpireMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardExpireYear");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardExpireYear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AVSPolicy");
        elemField.setXmlName(new javax.xml.namespace.QName("", "AVSPolicy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FSPolicy");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FSPolicy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userVar1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userVar1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userVar2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userVar2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userVar3");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userVar3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userVar4");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userVar4"));
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
