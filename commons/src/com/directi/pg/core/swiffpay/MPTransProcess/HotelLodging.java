/**
 * HotelLodging.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class HotelLodging  implements java.io.Serializable {
    private java.lang.String chargetypeamx;

    private java.lang.String roomrateamt;

    private java.lang.String checkindate;

    private java.lang.String checkoutdate;

    private java.lang.String purchaseid;

    private java.lang.String pproperty;

    private java.lang.String extracharges;

    public HotelLodging() {
    }

    public HotelLodging(
           java.lang.String chargetypeamx,
           java.lang.String roomrateamt,
           java.lang.String checkindate,
           java.lang.String checkoutdate,
           java.lang.String purchaseid,
           java.lang.String pproperty,
           java.lang.String extracharges) {
           this.chargetypeamx = chargetypeamx;
           this.roomrateamt = roomrateamt;
           this.checkindate = checkindate;
           this.checkoutdate = checkoutdate;
           this.purchaseid = purchaseid;
           this.pproperty = pproperty;
           this.extracharges = extracharges;
    }


    /**
     * Gets the chargetypeamx value for this HotelLodging.
     * 
     * @return chargetypeamx
     */
    public java.lang.String getChargetypeamx() {
        return chargetypeamx;
    }


    /**
     * Sets the chargetypeamx value for this HotelLodging.
     * 
     * @param chargetypeamx
     */
    public void setChargetypeamx(java.lang.String chargetypeamx) {
        this.chargetypeamx = chargetypeamx;
    }


    /**
     * Gets the roomrateamt value for this HotelLodging.
     * 
     * @return roomrateamt
     */
    public java.lang.String getRoomrateamt() {
        return roomrateamt;
    }


    /**
     * Sets the roomrateamt value for this HotelLodging.
     * 
     * @param roomrateamt
     */
    public void setRoomrateamt(java.lang.String roomrateamt) {
        this.roomrateamt = roomrateamt;
    }


    /**
     * Gets the checkindate value for this HotelLodging.
     * 
     * @return checkindate
     */
    public java.lang.String getCheckindate() {
        return checkindate;
    }


    /**
     * Sets the checkindate value for this HotelLodging.
     * 
     * @param checkindate
     */
    public void setCheckindate(java.lang.String checkindate) {
        this.checkindate = checkindate;
    }


    /**
     * Gets the checkoutdate value for this HotelLodging.
     * 
     * @return checkoutdate
     */
    public java.lang.String getCheckoutdate() {
        return checkoutdate;
    }


    /**
     * Sets the checkoutdate value for this HotelLodging.
     * 
     * @param checkoutdate
     */
    public void setCheckoutdate(java.lang.String checkoutdate) {
        this.checkoutdate = checkoutdate;
    }


    /**
     * Gets the purchaseid value for this HotelLodging.
     * 
     * @return purchaseid
     */
    public java.lang.String getPurchaseid() {
        return purchaseid;
    }


    /**
     * Sets the purchaseid value for this HotelLodging.
     * 
     * @param purchaseid
     */
    public void setPurchaseid(java.lang.String purchaseid) {
        this.purchaseid = purchaseid;
    }


    /**
     * Gets the pproperty value for this HotelLodging.
     * 
     * @return pproperty
     */
    public java.lang.String getPproperty() {
        return pproperty;
    }


    /**
     * Sets the pproperty value for this HotelLodging.
     * 
     * @param pproperty
     */
    public void setPproperty(java.lang.String pproperty) {
        this.pproperty = pproperty;
    }


    /**
     * Gets the extracharges value for this HotelLodging.
     * 
     * @return extracharges
     */
    public java.lang.String getExtracharges() {
        return extracharges;
    }


    /**
     * Sets the extracharges value for this HotelLodging.
     * 
     * @param extracharges
     */
    public void setExtracharges(java.lang.String extracharges) {
        this.extracharges = extracharges;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof HotelLodging)) return false;
        HotelLodging other = (HotelLodging) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.chargetypeamx==null && other.getChargetypeamx()==null) || 
             (this.chargetypeamx!=null &&
              this.chargetypeamx.equals(other.getChargetypeamx()))) &&
            ((this.roomrateamt==null && other.getRoomrateamt()==null) || 
             (this.roomrateamt!=null &&
              this.roomrateamt.equals(other.getRoomrateamt()))) &&
            ((this.checkindate==null && other.getCheckindate()==null) || 
             (this.checkindate!=null &&
              this.checkindate.equals(other.getCheckindate()))) &&
            ((this.checkoutdate==null && other.getCheckoutdate()==null) || 
             (this.checkoutdate!=null &&
              this.checkoutdate.equals(other.getCheckoutdate()))) &&
            ((this.purchaseid==null && other.getPurchaseid()==null) || 
             (this.purchaseid!=null &&
              this.purchaseid.equals(other.getPurchaseid()))) &&
            ((this.pproperty==null && other.getPproperty()==null) || 
             (this.pproperty!=null &&
              this.pproperty.equals(other.getPproperty()))) &&
            ((this.extracharges==null && other.getExtracharges()==null) || 
             (this.extracharges!=null &&
              this.extracharges.equals(other.getExtracharges())));
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
        if (getChargetypeamx() != null) {
            _hashCode += getChargetypeamx().hashCode();
        }
        if (getRoomrateamt() != null) {
            _hashCode += getRoomrateamt().hashCode();
        }
        if (getCheckindate() != null) {
            _hashCode += getCheckindate().hashCode();
        }
        if (getCheckoutdate() != null) {
            _hashCode += getCheckoutdate().hashCode();
        }
        if (getPurchaseid() != null) {
            _hashCode += getPurchaseid().hashCode();
        }
        if (getPproperty() != null) {
            _hashCode += getPproperty().hashCode();
        }
        if (getExtracharges() != null) {
            _hashCode += getExtracharges().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(HotelLodging.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "HotelLodging"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chargetypeamx");
        elemField.setXmlName(new javax.xml.namespace.QName("", "chargetypeamx"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomrateamt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "roomrateamt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkindate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "checkindate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkoutdate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "checkoutdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaseid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "purchaseid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pproperty");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pproperty"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extracharges");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extracharges"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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
