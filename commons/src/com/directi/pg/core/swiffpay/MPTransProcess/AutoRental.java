/**
 * AutoRental.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class AutoRental  implements java.io.Serializable {
    private java.lang.String rentalagreementnum;

    private java.lang.String rentalrate;

    private java.lang.String noshoworprogind;

    private java.lang.String extracharges;

    private java.lang.String rentaldate;

    private java.lang.String rentalreturndate;

    private java.lang.String rentername;

    private java.lang.String rentalreturncity;

    private java.lang.String rentalreturnstatecountry;

    private java.lang.String rentalreturnlocationid;

    public AutoRental() {
    }

    public AutoRental(
           java.lang.String rentalagreementnum,
           java.lang.String rentalrate,
           java.lang.String noshoworprogind,
           java.lang.String extracharges,
           java.lang.String rentaldate,
           java.lang.String rentalreturndate,
           java.lang.String rentername,
           java.lang.String rentalreturncity,
           java.lang.String rentalreturnstatecountry,
           java.lang.String rentalreturnlocationid) {
           this.rentalagreementnum = rentalagreementnum;
           this.rentalrate = rentalrate;
           this.noshoworprogind = noshoworprogind;
           this.extracharges = extracharges;
           this.rentaldate = rentaldate;
           this.rentalreturndate = rentalreturndate;
           this.rentername = rentername;
           this.rentalreturncity = rentalreturncity;
           this.rentalreturnstatecountry = rentalreturnstatecountry;
           this.rentalreturnlocationid = rentalreturnlocationid;
    }


    /**
     * Gets the rentalagreementnum value for this AutoRental.
     * 
     * @return rentalagreementnum
     */
    public java.lang.String getRentalagreementnum() {
        return rentalagreementnum;
    }


    /**
     * Sets the rentalagreementnum value for this AutoRental.
     * 
     * @param rentalagreementnum
     */
    public void setRentalagreementnum(java.lang.String rentalagreementnum) {
        this.rentalagreementnum = rentalagreementnum;
    }


    /**
     * Gets the rentalrate value for this AutoRental.
     * 
     * @return rentalrate
     */
    public java.lang.String getRentalrate() {
        return rentalrate;
    }


    /**
     * Sets the rentalrate value for this AutoRental.
     * 
     * @param rentalrate
     */
    public void setRentalrate(java.lang.String rentalrate) {
        this.rentalrate = rentalrate;
    }


    /**
     * Gets the noshoworprogind value for this AutoRental.
     * 
     * @return noshoworprogind
     */
    public java.lang.String getNoshoworprogind() {
        return noshoworprogind;
    }


    /**
     * Sets the noshoworprogind value for this AutoRental.
     * 
     * @param noshoworprogind
     */
    public void setNoshoworprogind(java.lang.String noshoworprogind) {
        this.noshoworprogind = noshoworprogind;
    }


    /**
     * Gets the extracharges value for this AutoRental.
     * 
     * @return extracharges
     */
    public java.lang.String getExtracharges() {
        return extracharges;
    }


    /**
     * Sets the extracharges value for this AutoRental.
     * 
     * @param extracharges
     */
    public void setExtracharges(java.lang.String extracharges) {
        this.extracharges = extracharges;
    }


    /**
     * Gets the rentaldate value for this AutoRental.
     * 
     * @return rentaldate
     */
    public java.lang.String getRentaldate() {
        return rentaldate;
    }


    /**
     * Sets the rentaldate value for this AutoRental.
     * 
     * @param rentaldate
     */
    public void setRentaldate(java.lang.String rentaldate) {
        this.rentaldate = rentaldate;
    }


    /**
     * Gets the rentalreturndate value for this AutoRental.
     * 
     * @return rentalreturndate
     */
    public java.lang.String getRentalreturndate() {
        return rentalreturndate;
    }


    /**
     * Sets the rentalreturndate value for this AutoRental.
     * 
     * @param rentalreturndate
     */
    public void setRentalreturndate(java.lang.String rentalreturndate) {
        this.rentalreturndate = rentalreturndate;
    }


    /**
     * Gets the rentername value for this AutoRental.
     * 
     * @return rentername
     */
    public java.lang.String getRentername() {
        return rentername;
    }


    /**
     * Sets the rentername value for this AutoRental.
     * 
     * @param rentername
     */
    public void setRentername(java.lang.String rentername) {
        this.rentername = rentername;
    }


    /**
     * Gets the rentalreturncity value for this AutoRental.
     * 
     * @return rentalreturncity
     */
    public java.lang.String getRentalreturncity() {
        return rentalreturncity;
    }


    /**
     * Sets the rentalreturncity value for this AutoRental.
     * 
     * @param rentalreturncity
     */
    public void setRentalreturncity(java.lang.String rentalreturncity) {
        this.rentalreturncity = rentalreturncity;
    }


    /**
     * Gets the rentalreturnstatecountry value for this AutoRental.
     * 
     * @return rentalreturnstatecountry
     */
    public java.lang.String getRentalreturnstatecountry() {
        return rentalreturnstatecountry;
    }


    /**
     * Sets the rentalreturnstatecountry value for this AutoRental.
     * 
     * @param rentalreturnstatecountry
     */
    public void setRentalreturnstatecountry(java.lang.String rentalreturnstatecountry) {
        this.rentalreturnstatecountry = rentalreturnstatecountry;
    }


    /**
     * Gets the rentalreturnlocationid value for this AutoRental.
     * 
     * @return rentalreturnlocationid
     */
    public java.lang.String getRentalreturnlocationid() {
        return rentalreturnlocationid;
    }


    /**
     * Sets the rentalreturnlocationid value for this AutoRental.
     * 
     * @param rentalreturnlocationid
     */
    public void setRentalreturnlocationid(java.lang.String rentalreturnlocationid) {
        this.rentalreturnlocationid = rentalreturnlocationid;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AutoRental)) return false;
        AutoRental other = (AutoRental) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rentalagreementnum==null && other.getRentalagreementnum()==null) || 
             (this.rentalagreementnum!=null &&
              this.rentalagreementnum.equals(other.getRentalagreementnum()))) &&
            ((this.rentalrate==null && other.getRentalrate()==null) || 
             (this.rentalrate!=null &&
              this.rentalrate.equals(other.getRentalrate()))) &&
            ((this.noshoworprogind==null && other.getNoshoworprogind()==null) || 
             (this.noshoworprogind!=null &&
              this.noshoworprogind.equals(other.getNoshoworprogind()))) &&
            ((this.extracharges==null && other.getExtracharges()==null) || 
             (this.extracharges!=null &&
              this.extracharges.equals(other.getExtracharges()))) &&
            ((this.rentaldate==null && other.getRentaldate()==null) || 
             (this.rentaldate!=null &&
              this.rentaldate.equals(other.getRentaldate()))) &&
            ((this.rentalreturndate==null && other.getRentalreturndate()==null) || 
             (this.rentalreturndate!=null &&
              this.rentalreturndate.equals(other.getRentalreturndate()))) &&
            ((this.rentername==null && other.getRentername()==null) || 
             (this.rentername!=null &&
              this.rentername.equals(other.getRentername()))) &&
            ((this.rentalreturncity==null && other.getRentalreturncity()==null) || 
             (this.rentalreturncity!=null &&
              this.rentalreturncity.equals(other.getRentalreturncity()))) &&
            ((this.rentalreturnstatecountry==null && other.getRentalreturnstatecountry()==null) || 
             (this.rentalreturnstatecountry!=null &&
              this.rentalreturnstatecountry.equals(other.getRentalreturnstatecountry()))) &&
            ((this.rentalreturnlocationid==null && other.getRentalreturnlocationid()==null) || 
             (this.rentalreturnlocationid!=null &&
              this.rentalreturnlocationid.equals(other.getRentalreturnlocationid())));
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
        if (getRentalagreementnum() != null) {
            _hashCode += getRentalagreementnum().hashCode();
        }
        if (getRentalrate() != null) {
            _hashCode += getRentalrate().hashCode();
        }
        if (getNoshoworprogind() != null) {
            _hashCode += getNoshoworprogind().hashCode();
        }
        if (getExtracharges() != null) {
            _hashCode += getExtracharges().hashCode();
        }
        if (getRentaldate() != null) {
            _hashCode += getRentaldate().hashCode();
        }
        if (getRentalreturndate() != null) {
            _hashCode += getRentalreturndate().hashCode();
        }
        if (getRentername() != null) {
            _hashCode += getRentername().hashCode();
        }
        if (getRentalreturncity() != null) {
            _hashCode += getRentalreturncity().hashCode();
        }
        if (getRentalreturnstatecountry() != null) {
            _hashCode += getRentalreturnstatecountry().hashCode();
        }
        if (getRentalreturnlocationid() != null) {
            _hashCode += getRentalreturnlocationid().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AutoRental.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "AutoRental"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rentalagreementnum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rentalagreementnum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rentalrate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rentalrate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("noshoworprogind");
        elemField.setXmlName(new javax.xml.namespace.QName("", "noshoworprogind"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extracharges");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extracharges"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rentaldate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rentaldate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rentalreturndate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rentalreturndate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rentername");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rentername"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rentalreturncity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rentalreturncity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rentalreturnstatecountry");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rentalreturnstatecountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rentalreturnlocationid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rentalreturnlocationid"));
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
