/**
 * SaleRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.twoGatePay.Interface_pkg;

public class SaleRequest  implements java.io.Serializable {
    private int loginid;

    private java.lang.String password;

    private java.lang.String purchaseid;

    private java.lang.String buyerip;

    private int amount;

    private java.lang.String currency;

    private java.lang.String cardnumber;

    private java.lang.String cvv;

    private java.lang.String expirymonth;

    private java.lang.String expiryyear;

    private java.lang.String buyername;

    private java.lang.String buyersurname;

    private java.lang.String buyeraddress;

    private java.lang.String buyerzipcode;

    private java.lang.String buyercity;

    private java.lang.String buyerstate;

    private java.lang.String buyercountrycode;

    private java.lang.String buyerphone;

    private java.lang.String buyeremail;

    private java.lang.String buyerdateofbirth;

    private java.lang.String buyeridverification;

    private java.lang.String customfield1;

    private java.lang.String customfield2;

    private java.lang.String customfield3;

    private java.lang.String customfield4;

    public SaleRequest() {
    }

    public SaleRequest(
           int loginid,
           java.lang.String password,
           java.lang.String purchaseid,
           java.lang.String buyerip,
           int amount,
           java.lang.String currency,
           java.lang.String cardnumber,
           java.lang.String cvv,
           java.lang.String expirymonth,
           java.lang.String expiryyear,
           java.lang.String buyername,
           java.lang.String buyersurname,
           java.lang.String buyeraddress,
           java.lang.String buyerzipcode,
           java.lang.String buyercity,
           java.lang.String buyerstate,
           java.lang.String buyercountrycode,
           java.lang.String buyerphone,
           java.lang.String buyeremail,
           java.lang.String buyerdateofbirth,
           java.lang.String buyeridverification,
           java.lang.String customfield1,
           java.lang.String customfield2,
           java.lang.String customfield3,
           java.lang.String customfield4) {
           this.loginid = loginid;
           this.password = password;
           this.purchaseid = purchaseid;
           this.buyerip = buyerip;
           this.amount = amount;
           this.currency = currency;
           this.cardnumber = cardnumber;
           this.cvv = cvv;
           this.expirymonth = expirymonth;
           this.expiryyear = expiryyear;
           this.buyername = buyername;
           this.buyersurname = buyersurname;
           this.buyeraddress = buyeraddress;
           this.buyerzipcode = buyerzipcode;
           this.buyercity = buyercity;
           this.buyerstate = buyerstate;
           this.buyercountrycode = buyercountrycode;
           this.buyerphone = buyerphone;
           this.buyeremail = buyeremail;
           this.buyerdateofbirth = buyerdateofbirth;
           this.buyeridverification = buyeridverification;
           this.customfield1 = customfield1;
           this.customfield2 = customfield2;
           this.customfield3 = customfield3;
           this.customfield4 = customfield4;
    }


    /**
     * Gets the loginid value for this SaleRequest.
     * 
     * @return loginid
     */
    public int getLoginid() {
        return loginid;
    }


    /**
     * Sets the loginid value for this SaleRequest.
     * 
     * @param loginid
     */
    public void setLoginid(int loginid) {
        this.loginid = loginid;
    }


    /**
     * Gets the password value for this SaleRequest.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this SaleRequest.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the purchaseid value for this SaleRequest.
     * 
     * @return purchaseid
     */
    public java.lang.String getPurchaseid() {
        return purchaseid;
    }


    /**
     * Sets the purchaseid value for this SaleRequest.
     * 
     * @param purchaseid
     */
    public void setPurchaseid(java.lang.String purchaseid) {
        this.purchaseid = purchaseid;
    }


    /**
     * Gets the buyerip value for this SaleRequest.
     * 
     * @return buyerip
     */
    public java.lang.String getBuyerip() {
        return buyerip;
    }


    /**
     * Sets the buyerip value for this SaleRequest.
     * 
     * @param buyerip
     */
    public void setBuyerip(java.lang.String buyerip) {
        this.buyerip = buyerip;
    }


    /**
     * Gets the amount value for this SaleRequest.
     * 
     * @return amount
     */
    public int getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this SaleRequest.
     * 
     * @param amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }


    /**
     * Gets the currency value for this SaleRequest.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this SaleRequest.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the cardnumber value for this SaleRequest.
     * 
     * @return cardnumber
     */
    public java.lang.String getCardnumber() {
        return cardnumber;
    }


    /**
     * Sets the cardnumber value for this SaleRequest.
     * 
     * @param cardnumber
     */
    public void setCardnumber(java.lang.String cardnumber) {
        this.cardnumber = cardnumber;
    }


    /**
     * Gets the cvv value for this SaleRequest.
     * 
     * @return cvv
     */
    public java.lang.String getCvv() {
        return cvv;
    }


    /**
     * Sets the cvv value for this SaleRequest.
     * 
     * @param cvv
     */
    public void setCvv(java.lang.String cvv) {
        this.cvv = cvv;
    }


    /**
     * Gets the expirymonth value for this SaleRequest.
     * 
     * @return expirymonth
     */
    public java.lang.String getExpirymonth() {
        return expirymonth;
    }


    /**
     * Sets the expirymonth value for this SaleRequest.
     * 
     * @param expirymonth
     */
    public void setExpirymonth(java.lang.String expirymonth) {
        this.expirymonth = expirymonth;
    }


    /**
     * Gets the expiryyear value for this SaleRequest.
     * 
     * @return expiryyear
     */
    public java.lang.String getExpiryyear() {
        return expiryyear;
    }


    /**
     * Sets the expiryyear value for this SaleRequest.
     * 
     * @param expiryyear
     */
    public void setExpiryyear(java.lang.String expiryyear) {
        this.expiryyear = expiryyear;
    }


    /**
     * Gets the buyername value for this SaleRequest.
     * 
     * @return buyername
     */
    public java.lang.String getBuyername() {
        return buyername;
    }


    /**
     * Sets the buyername value for this SaleRequest.
     * 
     * @param buyername
     */
    public void setBuyername(java.lang.String buyername) {
        this.buyername = buyername;
    }


    /**
     * Gets the buyersurname value for this SaleRequest.
     * 
     * @return buyersurname
     */
    public java.lang.String getBuyersurname() {
        return buyersurname;
    }


    /**
     * Sets the buyersurname value for this SaleRequest.
     * 
     * @param buyersurname
     */
    public void setBuyersurname(java.lang.String buyersurname) {
        this.buyersurname = buyersurname;
    }


    /**
     * Gets the buyeraddress value for this SaleRequest.
     * 
     * @return buyeraddress
     */
    public java.lang.String getBuyeraddress() {
        return buyeraddress;
    }


    /**
     * Sets the buyeraddress value for this SaleRequest.
     * 
     * @param buyeraddress
     */
    public void setBuyeraddress(java.lang.String buyeraddress) {
        this.buyeraddress = buyeraddress;
    }


    /**
     * Gets the buyerzipcode value for this SaleRequest.
     * 
     * @return buyerzipcode
     */
    public java.lang.String getBuyerzipcode() {
        return buyerzipcode;
    }


    /**
     * Sets the buyerzipcode value for this SaleRequest.
     * 
     * @param buyerzipcode
     */
    public void setBuyerzipcode(java.lang.String buyerzipcode) {
        this.buyerzipcode = buyerzipcode;
    }


    /**
     * Gets the buyercity value for this SaleRequest.
     * 
     * @return buyercity
     */
    public java.lang.String getBuyercity() {
        return buyercity;
    }


    /**
     * Sets the buyercity value for this SaleRequest.
     * 
     * @param buyercity
     */
    public void setBuyercity(java.lang.String buyercity) {
        this.buyercity = buyercity;
    }


    /**
     * Gets the buyerstate value for this SaleRequest.
     * 
     * @return buyerstate
     */
    public java.lang.String getBuyerstate() {
        return buyerstate;
    }


    /**
     * Sets the buyerstate value for this SaleRequest.
     * 
     * @param buyerstate
     */
    public void setBuyerstate(java.lang.String buyerstate) {
        this.buyerstate = buyerstate;
    }


    /**
     * Gets the buyercountrycode value for this SaleRequest.
     * 
     * @return buyercountrycode
     */
    public java.lang.String getBuyercountrycode() {
        return buyercountrycode;
    }


    /**
     * Sets the buyercountrycode value for this SaleRequest.
     * 
     * @param buyercountrycode
     */
    public void setBuyercountrycode(java.lang.String buyercountrycode) {
        this.buyercountrycode = buyercountrycode;
    }


    /**
     * Gets the buyerphone value for this SaleRequest.
     * 
     * @return buyerphone
     */
    public java.lang.String getBuyerphone() {
        return buyerphone;
    }


    /**
     * Sets the buyerphone value for this SaleRequest.
     * 
     * @param buyerphone
     */
    public void setBuyerphone(java.lang.String buyerphone) {
        this.buyerphone = buyerphone;
    }


    /**
     * Gets the buyeremail value for this SaleRequest.
     * 
     * @return buyeremail
     */
    public java.lang.String getBuyeremail() {
        return buyeremail;
    }


    /**
     * Sets the buyeremail value for this SaleRequest.
     * 
     * @param buyeremail
     */
    public void setBuyeremail(java.lang.String buyeremail) {
        this.buyeremail = buyeremail;
    }


    /**
     * Gets the buyerdateofbirth value for this SaleRequest.
     * 
     * @return buyerdateofbirth
     */
    public java.lang.String getBuyerdateofbirth() {
        return buyerdateofbirth;
    }


    /**
     * Sets the buyerdateofbirth value for this SaleRequest.
     * 
     * @param buyerdateofbirth
     */
    public void setBuyerdateofbirth(java.lang.String buyerdateofbirth) {
        this.buyerdateofbirth = buyerdateofbirth;
    }


    /**
     * Gets the buyeridverification value for this SaleRequest.
     * 
     * @return buyeridverification
     */
    public java.lang.String getBuyeridverification() {
        return buyeridverification;
    }


    /**
     * Sets the buyeridverification value for this SaleRequest.
     * 
     * @param buyeridverification
     */
    public void setBuyeridverification(java.lang.String buyeridverification) {
        this.buyeridverification = buyeridverification;
    }


    /**
     * Gets the customfield1 value for this SaleRequest.
     * 
     * @return customfield1
     */
    public java.lang.String getCustomfield1() {
        return customfield1;
    }


    /**
     * Sets the customfield1 value for this SaleRequest.
     * 
     * @param customfield1
     */
    public void setCustomfield1(java.lang.String customfield1) {
        this.customfield1 = customfield1;
    }


    /**
     * Gets the customfield2 value for this SaleRequest.
     * 
     * @return customfield2
     */
    public java.lang.String getCustomfield2() {
        return customfield2;
    }


    /**
     * Sets the customfield2 value for this SaleRequest.
     * 
     * @param customfield2
     */
    public void setCustomfield2(java.lang.String customfield2) {
        this.customfield2 = customfield2;
    }


    /**
     * Gets the customfield3 value for this SaleRequest.
     * 
     * @return customfield3
     */
    public java.lang.String getCustomfield3() {
        return customfield3;
    }


    /**
     * Sets the customfield3 value for this SaleRequest.
     * 
     * @param customfield3
     */
    public void setCustomfield3(java.lang.String customfield3) {
        this.customfield3 = customfield3;
    }


    /**
     * Gets the customfield4 value for this SaleRequest.
     * 
     * @return customfield4
     */
    public java.lang.String getCustomfield4() {
        return customfield4;
    }


    /**
     * Sets the customfield4 value for this SaleRequest.
     * 
     * @param customfield4
     */
    public void setCustomfield4(java.lang.String customfield4) {
        this.customfield4 = customfield4;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SaleRequest)) return false;
        SaleRequest other = (SaleRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.loginid == other.getLoginid() &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.purchaseid==null && other.getPurchaseid()==null) || 
             (this.purchaseid!=null &&
              this.purchaseid.equals(other.getPurchaseid()))) &&
            ((this.buyerip==null && other.getBuyerip()==null) || 
             (this.buyerip!=null &&
              this.buyerip.equals(other.getBuyerip()))) &&
            this.amount == other.getAmount() &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.cardnumber==null && other.getCardnumber()==null) || 
             (this.cardnumber!=null &&
              this.cardnumber.equals(other.getCardnumber()))) &&
            ((this.cvv==null && other.getCvv()==null) || 
             (this.cvv!=null &&
              this.cvv.equals(other.getCvv()))) &&
            ((this.expirymonth==null && other.getExpirymonth()==null) || 
             (this.expirymonth!=null &&
              this.expirymonth.equals(other.getExpirymonth()))) &&
            ((this.expiryyear==null && other.getExpiryyear()==null) || 
             (this.expiryyear!=null &&
              this.expiryyear.equals(other.getExpiryyear()))) &&
            ((this.buyername==null && other.getBuyername()==null) || 
             (this.buyername!=null &&
              this.buyername.equals(other.getBuyername()))) &&
            ((this.buyersurname==null && other.getBuyersurname()==null) || 
             (this.buyersurname!=null &&
              this.buyersurname.equals(other.getBuyersurname()))) &&
            ((this.buyeraddress==null && other.getBuyeraddress()==null) || 
             (this.buyeraddress!=null &&
              this.buyeraddress.equals(other.getBuyeraddress()))) &&
            ((this.buyerzipcode==null && other.getBuyerzipcode()==null) || 
             (this.buyerzipcode!=null &&
              this.buyerzipcode.equals(other.getBuyerzipcode()))) &&
            ((this.buyercity==null && other.getBuyercity()==null) || 
             (this.buyercity!=null &&
              this.buyercity.equals(other.getBuyercity()))) &&
            ((this.buyerstate==null && other.getBuyerstate()==null) || 
             (this.buyerstate!=null &&
              this.buyerstate.equals(other.getBuyerstate()))) &&
            ((this.buyercountrycode==null && other.getBuyercountrycode()==null) || 
             (this.buyercountrycode!=null &&
              this.buyercountrycode.equals(other.getBuyercountrycode()))) &&
            ((this.buyerphone==null && other.getBuyerphone()==null) || 
             (this.buyerphone!=null &&
              this.buyerphone.equals(other.getBuyerphone()))) &&
            ((this.buyeremail==null && other.getBuyeremail()==null) || 
             (this.buyeremail!=null &&
              this.buyeremail.equals(other.getBuyeremail()))) &&
            ((this.buyerdateofbirth==null && other.getBuyerdateofbirth()==null) || 
             (this.buyerdateofbirth!=null &&
              this.buyerdateofbirth.equals(other.getBuyerdateofbirth()))) &&
            ((this.buyeridverification==null && other.getBuyeridverification()==null) || 
             (this.buyeridverification!=null &&
              this.buyeridverification.equals(other.getBuyeridverification()))) &&
            ((this.customfield1==null && other.getCustomfield1()==null) || 
             (this.customfield1!=null &&
              this.customfield1.equals(other.getCustomfield1()))) &&
            ((this.customfield2==null && other.getCustomfield2()==null) || 
             (this.customfield2!=null &&
              this.customfield2.equals(other.getCustomfield2()))) &&
            ((this.customfield3==null && other.getCustomfield3()==null) || 
             (this.customfield3!=null &&
              this.customfield3.equals(other.getCustomfield3()))) &&
            ((this.customfield4==null && other.getCustomfield4()==null) || 
             (this.customfield4!=null &&
              this.customfield4.equals(other.getCustomfield4())));
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
        _hashCode += getLoginid();
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getPurchaseid() != null) {
            _hashCode += getPurchaseid().hashCode();
        }
        if (getBuyerip() != null) {
            _hashCode += getBuyerip().hashCode();
        }
        _hashCode += getAmount();
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getCardnumber() != null) {
            _hashCode += getCardnumber().hashCode();
        }
        if (getCvv() != null) {
            _hashCode += getCvv().hashCode();
        }
        if (getExpirymonth() != null) {
            _hashCode += getExpirymonth().hashCode();
        }
        if (getExpiryyear() != null) {
            _hashCode += getExpiryyear().hashCode();
        }
        if (getBuyername() != null) {
            _hashCode += getBuyername().hashCode();
        }
        if (getBuyersurname() != null) {
            _hashCode += getBuyersurname().hashCode();
        }
        if (getBuyeraddress() != null) {
            _hashCode += getBuyeraddress().hashCode();
        }
        if (getBuyerzipcode() != null) {
            _hashCode += getBuyerzipcode().hashCode();
        }
        if (getBuyercity() != null) {
            _hashCode += getBuyercity().hashCode();
        }
        if (getBuyerstate() != null) {
            _hashCode += getBuyerstate().hashCode();
        }
        if (getBuyercountrycode() != null) {
            _hashCode += getBuyercountrycode().hashCode();
        }
        if (getBuyerphone() != null) {
            _hashCode += getBuyerphone().hashCode();
        }
        if (getBuyeremail() != null) {
            _hashCode += getBuyeremail().hashCode();
        }
        if (getBuyerdateofbirth() != null) {
            _hashCode += getBuyerdateofbirth().hashCode();
        }
        if (getBuyeridverification() != null) {
            _hashCode += getBuyeridverification().hashCode();
        }
        if (getCustomfield1() != null) {
            _hashCode += getCustomfield1().hashCode();
        }
        if (getCustomfield2() != null) {
            _hashCode += getCustomfield2().hashCode();
        }
        if (getCustomfield3() != null) {
            _hashCode += getCustomfield3().hashCode();
        }
        if (getCustomfield4() != null) {
            _hashCode += getCustomfield4().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SaleRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "SaleRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loginid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "loginid"));
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
        elemField.setFieldName("purchaseid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "purchaseid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyerip");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyerip"));
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
        elemField.setFieldName("cardnumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardnumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cvv");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cvv"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expirymonth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "expirymonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expiryyear");
        elemField.setXmlName(new javax.xml.namespace.QName("", "expiryyear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyername");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyername"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyersurname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyersurname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyeraddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyeraddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyerzipcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyerzipcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyercity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyercity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyerstate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyerstate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyercountrycode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyercountrycode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyerphone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyerphone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyeremail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyeremail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyerdateofbirth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyerdateofbirth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buyeridverification");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buyeridverification"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customfield1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customfield1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customfield2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customfield2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customfield3");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customfield3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customfield4");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customfield4"));
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
