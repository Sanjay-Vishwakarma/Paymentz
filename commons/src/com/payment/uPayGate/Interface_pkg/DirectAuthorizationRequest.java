/**
 * DirectAuthorizationRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.uPayGate.Interface_pkg;

public class DirectAuthorizationRequest  implements java.io.Serializable {
    private String accountID;

    private String password;

    private String orderID;

    private String customerIP;

    private int amount;

    private String currency;

    private String customerID;

    private String cardHolderName;

    private String cardHolderAddress;

    private String cardHolderZipcode;

    private String cardHolderCity;

    private String cardHolderState;

    private String cardHolderCountryCode;

    private String cardHolderPhone;

    private String cardHolderEmail;

    private String cardNumber;

    private String cardSecurityCode;

    private String cardExpireMonth;

    private String cardExpireYear;

    private String AVSPolicy;

    private String FSPolicy;

    private String userVar1;

    private String userVar2;

    private String userVar3;

    private String userVar4;

    public DirectAuthorizationRequest() {
    }

    public DirectAuthorizationRequest(
           String accountID,
           String password,
           String orderID,
           String customerIP,
           int amount,
           String currency,
           String customerID,
           String cardHolderName,
           String cardHolderAddress,
           String cardHolderZipcode,
           String cardHolderCity,
           String cardHolderState,
           String cardHolderCountryCode,
           String cardHolderPhone,
           String cardHolderEmail,
           String cardNumber,
           String cardSecurityCode,
           String cardExpireMonth,
           String cardExpireYear,
           String AVSPolicy,
           String FSPolicy,
           String userVar1,
           String userVar2,
           String userVar3,
           String userVar4) {
           this.accountID = accountID;
           this.password = password;
           this.orderID = orderID;
           this.customerIP = customerIP;
           this.amount = amount;
           this.currency = currency;
           this.customerID = customerID;
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
     * Gets the accountID value for this DirectAuthorizationRequest.
     * 
     * @return accountID
     */
    public String getAccountID() {
        return accountID;
    }


    /**
     * Sets the accountID value for this DirectAuthorizationRequest.
     * 
     * @param accountID
     */
    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }


    /**
     * Gets the password value for this DirectAuthorizationRequest.
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this DirectAuthorizationRequest.
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Gets the orderID value for this DirectAuthorizationRequest.
     * 
     * @return orderID
     */
    public String getOrderID() {
        return orderID;
    }


    /**
     * Sets the orderID value for this DirectAuthorizationRequest.
     * 
     * @param orderID
     */
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }


    /**
     * Gets the customerIP value for this DirectAuthorizationRequest.
     * 
     * @return customerIP
     */
    public String getCustomerIP() {
        return customerIP;
    }


    /**
     * Sets the customerIP value for this DirectAuthorizationRequest.
     * 
     * @param customerIP
     */
    public void setCustomerIP(String customerIP) {
        this.customerIP = customerIP;
    }


    /**
     * Gets the amount value for this DirectAuthorizationRequest.
     * 
     * @return amount
     */
    public int getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this DirectAuthorizationRequest.
     * 
     * @param amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }


    /**
     * Gets the currency value for this DirectAuthorizationRequest.
     * 
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this DirectAuthorizationRequest.
     * 
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }


    /**
     * Gets the customerID value for this DirectAuthorizationRequest.
     * 
     * @return customerID
     */
    public String getCustomerID() {
        return customerID;
    }


    /**
     * Sets the customerID value for this DirectAuthorizationRequest.
     * 
     * @param customerID
     */
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }


    /**
     * Gets the cardHolderName value for this DirectAuthorizationRequest.
     * 
     * @return cardHolderName
     */
    public String getCardHolderName() {
        return cardHolderName;
    }


    /**
     * Sets the cardHolderName value for this DirectAuthorizationRequest.
     * 
     * @param cardHolderName
     */
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }


    /**
     * Gets the cardHolderAddress value for this DirectAuthorizationRequest.
     * 
     * @return cardHolderAddress
     */
    public String getCardHolderAddress() {
        return cardHolderAddress;
    }


    /**
     * Sets the cardHolderAddress value for this DirectAuthorizationRequest.
     * 
     * @param cardHolderAddress
     */
    public void setCardHolderAddress(String cardHolderAddress) {
        this.cardHolderAddress = cardHolderAddress;
    }


    /**
     * Gets the cardHolderZipcode value for this DirectAuthorizationRequest.
     * 
     * @return cardHolderZipcode
     */
    public String getCardHolderZipcode() {
        return cardHolderZipcode;
    }


    /**
     * Sets the cardHolderZipcode value for this DirectAuthorizationRequest.
     * 
     * @param cardHolderZipcode
     */
    public void setCardHolderZipcode(String cardHolderZipcode) {
        this.cardHolderZipcode = cardHolderZipcode;
    }


    /**
     * Gets the cardHolderCity value for this DirectAuthorizationRequest.
     * 
     * @return cardHolderCity
     */
    public String getCardHolderCity() {
        return cardHolderCity;
    }


    /**
     * Sets the cardHolderCity value for this DirectAuthorizationRequest.
     * 
     * @param cardHolderCity
     */
    public void setCardHolderCity(String cardHolderCity) {
        this.cardHolderCity = cardHolderCity;
    }


    /**
     * Gets the cardHolderState value for this DirectAuthorizationRequest.
     * 
     * @return cardHolderState
     */
    public String getCardHolderState() {
        return cardHolderState;
    }


    /**
     * Sets the cardHolderState value for this DirectAuthorizationRequest.
     * 
     * @param cardHolderState
     */
    public void setCardHolderState(String cardHolderState) {
        this.cardHolderState = cardHolderState;
    }


    /**
     * Gets the cardHolderCountryCode value for this DirectAuthorizationRequest.
     * 
     * @return cardHolderCountryCode
     */
    public String getCardHolderCountryCode() {
        return cardHolderCountryCode;
    }


    /**
     * Sets the cardHolderCountryCode value for this DirectAuthorizationRequest.
     * 
     * @param cardHolderCountryCode
     */
    public void setCardHolderCountryCode(String cardHolderCountryCode) {
        this.cardHolderCountryCode = cardHolderCountryCode;
    }


    /**
     * Gets the cardHolderPhone value for this DirectAuthorizationRequest.
     * 
     * @return cardHolderPhone
     */
    public String getCardHolderPhone() {
        return cardHolderPhone;
    }


    /**
     * Sets the cardHolderPhone value for this DirectAuthorizationRequest.
     * 
     * @param cardHolderPhone
     */
    public void setCardHolderPhone(String cardHolderPhone) {
        this.cardHolderPhone = cardHolderPhone;
    }


    /**
     * Gets the cardHolderEmail value for this DirectAuthorizationRequest.
     * 
     * @return cardHolderEmail
     */
    public String getCardHolderEmail() {
        return cardHolderEmail;
    }


    /**
     * Sets the cardHolderEmail value for this DirectAuthorizationRequest.
     * 
     * @param cardHolderEmail
     */
    public void setCardHolderEmail(String cardHolderEmail) {
        this.cardHolderEmail = cardHolderEmail;
    }


    /**
     * Gets the cardNumber value for this DirectAuthorizationRequest.
     * 
     * @return cardNumber
     */
    public String getCardNumber() {
        return cardNumber;
    }


    /**
     * Sets the cardNumber value for this DirectAuthorizationRequest.
     * 
     * @param cardNumber
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }


    /**
     * Gets the cardSecurityCode value for this DirectAuthorizationRequest.
     * 
     * @return cardSecurityCode
     */
    public String getCardSecurityCode() {
        return cardSecurityCode;
    }


    /**
     * Sets the cardSecurityCode value for this DirectAuthorizationRequest.
     * 
     * @param cardSecurityCode
     */
    public void setCardSecurityCode(String cardSecurityCode) {
        this.cardSecurityCode = cardSecurityCode;
    }


    /**
     * Gets the cardExpireMonth value for this DirectAuthorizationRequest.
     * 
     * @return cardExpireMonth
     */
    public String getCardExpireMonth() {
        return cardExpireMonth;
    }


    /**
     * Sets the cardExpireMonth value for this DirectAuthorizationRequest.
     * 
     * @param cardExpireMonth
     */
    public void setCardExpireMonth(String cardExpireMonth) {
        this.cardExpireMonth = cardExpireMonth;
    }


    /**
     * Gets the cardExpireYear value for this DirectAuthorizationRequest.
     * 
     * @return cardExpireYear
     */
    public String getCardExpireYear() {
        return cardExpireYear;
    }


    /**
     * Sets the cardExpireYear value for this DirectAuthorizationRequest.
     * 
     * @param cardExpireYear
     */
    public void setCardExpireYear(String cardExpireYear) {
        this.cardExpireYear = cardExpireYear;
    }


    /**
     * Gets the AVSPolicy value for this DirectAuthorizationRequest.
     * 
     * @return AVSPolicy
     */
    public String getAVSPolicy() {
        return AVSPolicy;
    }


    /**
     * Sets the AVSPolicy value for this DirectAuthorizationRequest.
     * 
     * @param AVSPolicy
     */
    public void setAVSPolicy(String AVSPolicy) {
        this.AVSPolicy = AVSPolicy;
    }


    /**
     * Gets the FSPolicy value for this DirectAuthorizationRequest.
     * 
     * @return FSPolicy
     */
    public String getFSPolicy() {
        return FSPolicy;
    }


    /**
     * Sets the FSPolicy value for this DirectAuthorizationRequest.
     * 
     * @param FSPolicy
     */
    public void setFSPolicy(String FSPolicy) {
        this.FSPolicy = FSPolicy;
    }


    /**
     * Gets the userVar1 value for this DirectAuthorizationRequest.
     * 
     * @return userVar1
     */
    public String getUserVar1() {
        return userVar1;
    }


    /**
     * Sets the userVar1 value for this DirectAuthorizationRequest.
     * 
     * @param userVar1
     */
    public void setUserVar1(String userVar1) {
        this.userVar1 = userVar1;
    }


    /**
     * Gets the userVar2 value for this DirectAuthorizationRequest.
     * 
     * @return userVar2
     */
    public String getUserVar2() {
        return userVar2;
    }


    /**
     * Sets the userVar2 value for this DirectAuthorizationRequest.
     * 
     * @param userVar2
     */
    public void setUserVar2(String userVar2) {
        this.userVar2 = userVar2;
    }


    /**
     * Gets the userVar3 value for this DirectAuthorizationRequest.
     * 
     * @return userVar3
     */
    public String getUserVar3() {
        return userVar3;
    }


    /**
     * Sets the userVar3 value for this DirectAuthorizationRequest.
     * 
     * @param userVar3
     */
    public void setUserVar3(String userVar3) {
        this.userVar3 = userVar3;
    }


    /**
     * Gets the userVar4 value for this DirectAuthorizationRequest.
     * 
     * @return userVar4
     */
    public String getUserVar4() {
        return userVar4;
    }


    /**
     * Sets the userVar4 value for this DirectAuthorizationRequest.
     * 
     * @param userVar4
     */
    public void setUserVar4(String userVar4) {
        this.userVar4 = userVar4;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof DirectAuthorizationRequest)) return false;
        DirectAuthorizationRequest other = (DirectAuthorizationRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.accountID==null && other.getAccountID()==null) || 
             (this.accountID!=null &&
              this.accountID.equals(other.getAccountID()))) &&
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
            ((this.customerID==null && other.getCustomerID()==null) || 
             (this.customerID!=null &&
              this.customerID.equals(other.getCustomerID()))) &&
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
        if (getAccountID() != null) {
            _hashCode += getAccountID().hashCode();
        }
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
        if (getCustomerID() != null) {
            _hashCode += getCustomerID().hashCode();
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
        new org.apache.axis.description.TypeDesc(DirectAuthorizationRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "DirectAuthorizationRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("customerID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderZipcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderZipcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderCity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardHolderState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FSPolicy");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FSPolicy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userVar1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userVar1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userVar2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userVar2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userVar3");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userVar3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userVar4");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userVar4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
