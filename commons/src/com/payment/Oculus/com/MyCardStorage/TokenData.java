/**
 * TokenData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class TokenData  implements java.io.Serializable {
    private java.lang.String token;

    private int tokenType;

    private java.lang.String last4;

    private java.lang.String cardNumber;

    private int cardType;

    private int expirationMonth;

    private int expirationYear;

    private java.lang.String nickName;

    private java.lang.String firstName;

    private java.lang.String lastName;

    private java.lang.String streetAddress;

    private java.lang.String zipCode;

    private java.lang.String CVV;

    private java.lang.String XID;

    private java.lang.String CAVV;

    public TokenData() {
    }

    public TokenData(
           java.lang.String token,
           int tokenType,
           java.lang.String last4,
           java.lang.String cardNumber,
           int cardType,
           int expirationMonth,
           int expirationYear,
           java.lang.String nickName,
           java.lang.String firstName,
           java.lang.String lastName,
           java.lang.String streetAddress,
           java.lang.String zipCode,
           java.lang.String CVV,
           java.lang.String XID,
           java.lang.String CAVV) {
           this.token = token;
           this.tokenType = tokenType;
           this.last4 = last4;
           this.cardNumber = cardNumber;
           this.cardType = cardType;
           this.expirationMonth = expirationMonth;
           this.expirationYear = expirationYear;
           this.nickName = nickName;
           this.firstName = firstName;
           this.lastName = lastName;
           this.streetAddress = streetAddress;
           this.zipCode = zipCode;
           this.CVV = CVV;
           this.XID = XID;
           this.CAVV = CAVV;
    }


    /**
     * Gets the token value for this TokenData.
     * 
     * @return token
     */
    public java.lang.String getToken() {
        return token;
    }


    /**
     * Sets the token value for this TokenData.
     * 
     * @param token
     */
    public void setToken(java.lang.String token) {
        this.token = token;
    }


    /**
     * Gets the tokenType value for this TokenData.
     * 
     * @return tokenType
     */
    public int getTokenType() {
        return tokenType;
    }


    /**
     * Sets the tokenType value for this TokenData.
     * 
     * @param tokenType
     */
    public void setTokenType(int tokenType) {
        this.tokenType = tokenType;
    }


    /**
     * Gets the last4 value for this TokenData.
     * 
     * @return last4
     */
    public java.lang.String getLast4() {
        return last4;
    }


    /**
     * Sets the last4 value for this TokenData.
     * 
     * @param last4
     */
    public void setLast4(java.lang.String last4) {
        this.last4 = last4;
    }


    /**
     * Gets the cardNumber value for this TokenData.
     * 
     * @return cardNumber
     */
    public java.lang.String getCardNumber() {
        return cardNumber;
    }


    /**
     * Sets the cardNumber value for this TokenData.
     * 
     * @param cardNumber
     */
    public void setCardNumber(java.lang.String cardNumber) {
        this.cardNumber = cardNumber;
    }


    /**
     * Gets the cardType value for this TokenData.
     * 
     * @return cardType
     */
    public int getCardType() {
        return cardType;
    }


    /**
     * Sets the cardType value for this TokenData.
     * 
     * @param cardType
     */
    public void setCardType(int cardType) {
        this.cardType = cardType;
    }


    /**
     * Gets the expirationMonth value for this TokenData.
     * 
     * @return expirationMonth
     */
    public int getExpirationMonth() {
        return expirationMonth;
    }


    /**
     * Sets the expirationMonth value for this TokenData.
     * 
     * @param expirationMonth
     */
    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }


    /**
     * Gets the expirationYear value for this TokenData.
     * 
     * @return expirationYear
     */
    public int getExpirationYear() {
        return expirationYear;
    }


    /**
     * Sets the expirationYear value for this TokenData.
     * 
     * @param expirationYear
     */
    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }


    /**
     * Gets the nickName value for this TokenData.
     * 
     * @return nickName
     */
    public java.lang.String getNickName() {
        return nickName;
    }


    /**
     * Sets the nickName value for this TokenData.
     * 
     * @param nickName
     */
    public void setNickName(java.lang.String nickName) {
        this.nickName = nickName;
    }


    /**
     * Gets the firstName value for this TokenData.
     * 
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this TokenData.
     * 
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the lastName value for this TokenData.
     * 
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this TokenData.
     * 
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the streetAddress value for this TokenData.
     * 
     * @return streetAddress
     */
    public java.lang.String getStreetAddress() {
        return streetAddress;
    }


    /**
     * Sets the streetAddress value for this TokenData.
     * 
     * @param streetAddress
     */
    public void setStreetAddress(java.lang.String streetAddress) {
        this.streetAddress = streetAddress;
    }


    /**
     * Gets the zipCode value for this TokenData.
     * 
     * @return zipCode
     */
    public java.lang.String getZipCode() {
        return zipCode;
    }


    /**
     * Sets the zipCode value for this TokenData.
     * 
     * @param zipCode
     */
    public void setZipCode(java.lang.String zipCode) {
        this.zipCode = zipCode;
    }


    /**
     * Gets the CVV value for this TokenData.
     * 
     * @return CVV
     */
    public java.lang.String getCVV() {
        return CVV;
    }


    /**
     * Sets the CVV value for this TokenData.
     * 
     * @param CVV
     */
    public void setCVV(java.lang.String CVV) {
        this.CVV = CVV;
    }


    /**
     * Gets the XID value for this TokenData.
     * 
     * @return XID
     */
    public java.lang.String getXID() {
        return XID;
    }


    /**
     * Sets the XID value for this TokenData.
     * 
     * @param XID
     */
    public void setXID(java.lang.String XID) {
        this.XID = XID;
    }


    /**
     * Gets the CAVV value for this TokenData.
     * 
     * @return CAVV
     */
    public java.lang.String getCAVV() {
        return CAVV;
    }


    /**
     * Sets the CAVV value for this TokenData.
     * 
     * @param CAVV
     */
    public void setCAVV(java.lang.String CAVV) {
        this.CAVV = CAVV;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TokenData)) return false;
        TokenData other = (TokenData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.token==null && other.getToken()==null) || 
             (this.token!=null &&
              this.token.equals(other.getToken()))) &&
            this.tokenType == other.getTokenType() &&
            ((this.last4==null && other.getLast4()==null) || 
             (this.last4!=null &&
              this.last4.equals(other.getLast4()))) &&
            ((this.cardNumber==null && other.getCardNumber()==null) || 
             (this.cardNumber!=null &&
              this.cardNumber.equals(other.getCardNumber()))) &&
            this.cardType == other.getCardType() &&
            this.expirationMonth == other.getExpirationMonth() &&
            this.expirationYear == other.getExpirationYear() &&
            ((this.nickName==null && other.getNickName()==null) || 
             (this.nickName!=null &&
              this.nickName.equals(other.getNickName()))) &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            ((this.streetAddress==null && other.getStreetAddress()==null) || 
             (this.streetAddress!=null &&
              this.streetAddress.equals(other.getStreetAddress()))) &&
            ((this.zipCode==null && other.getZipCode()==null) || 
             (this.zipCode!=null &&
              this.zipCode.equals(other.getZipCode()))) &&
            ((this.CVV==null && other.getCVV()==null) || 
             (this.CVV!=null &&
              this.CVV.equals(other.getCVV()))) &&
            ((this.XID==null && other.getXID()==null) || 
             (this.XID!=null &&
              this.XID.equals(other.getXID()))) &&
            ((this.CAVV==null && other.getCAVV()==null) || 
             (this.CAVV!=null &&
              this.CAVV.equals(other.getCAVV())));
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
        if (getToken() != null) {
            _hashCode += getToken().hashCode();
        }
        _hashCode += getTokenType();
        if (getLast4() != null) {
            _hashCode += getLast4().hashCode();
        }
        if (getCardNumber() != null) {
            _hashCode += getCardNumber().hashCode();
        }
        _hashCode += getCardType();
        _hashCode += getExpirationMonth();
        _hashCode += getExpirationYear();
        if (getNickName() != null) {
            _hashCode += getNickName().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getStreetAddress() != null) {
            _hashCode += getStreetAddress().hashCode();
        }
        if (getZipCode() != null) {
            _hashCode += getZipCode().hashCode();
        }
        if (getCVV() != null) {
            _hashCode += getCVV().hashCode();
        }
        if (getXID() != null) {
            _hashCode += getXID().hashCode();
        }
        if (getCAVV() != null) {
            _hashCode += getCAVV().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TokenData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TokenData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("token");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Token"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tokenType");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TokenType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("last4");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Last4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CardNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardType");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CardType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expirationMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ExpirationMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expirationYear");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ExpirationYear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nickName");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "NickName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "FirstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "LastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("streetAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "StreetAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zipCode");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ZipCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CVV");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CVV"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "XID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CAVV");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CAVV"));
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
