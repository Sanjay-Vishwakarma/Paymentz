/**
 * RequestBillingType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class RequestBillingType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestBillingType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.BillingType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressUnitNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "AddressUnitNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("birthDateYYYYMMDD");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "BirthDateYYYYMMDD"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cityName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CityName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CountryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Fax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "FullName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Phone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postalCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "PostalCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("streetName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "StreetName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("streetNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "StreetNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("territoryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "TerritoryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String addressUnitNumber;
    private String birthDateYYYYMMDD;
    private String cityName;
    private String countryCode;
    private String email;
    private String fax;
    private String fullName;
    private String phone;
    private String postalCode;
    private String streetName;
    private String streetNumber;
    private String territoryCode;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public RequestBillingType() {
    }


    public RequestBillingType(
           String addressUnitNumber,
           String birthDateYYYYMMDD,
           String cityName,
           String countryCode,
           String email,
           String fax,
           String fullName,
           String phone,
           String postalCode,
           String streetName,
           String streetNumber,
           String territoryCode) {
           this.addressUnitNumber = addressUnitNumber;
           this.birthDateYYYYMMDD = birthDateYYYYMMDD;
           this.cityName = cityName;
           this.countryCode = countryCode;
           this.email = email;
           this.fax = fax;
           this.fullName = fullName;
           this.phone = phone;
           this.postalCode = postalCode;
           this.streetName = streetName;
           this.streetNumber = streetNumber;
           this.territoryCode = territoryCode;
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

    /**
     * Gets the addressUnitNumber value for this RequestBillingType.
     *
     * @return addressUnitNumber
     */
    public String getAddressUnitNumber() {
        return addressUnitNumber;
    }

    /**
     * Sets the addressUnitNumber value for this RequestBillingType.
     *
     * @param addressUnitNumber
     */
    public void setAddressUnitNumber(String addressUnitNumber) {
        this.addressUnitNumber = addressUnitNumber;
    }

    /**
     * Gets the birthDateYYYYMMDD value for this RequestBillingType.
     *
     * @return birthDateYYYYMMDD
     */
    public String getBirthDateYYYYMMDD() {
        return birthDateYYYYMMDD;
    }

    /**
     * Sets the birthDateYYYYMMDD value for this RequestBillingType.
     *
     * @param birthDateYYYYMMDD
     */
    public void setBirthDateYYYYMMDD(String birthDateYYYYMMDD) {
        this.birthDateYYYYMMDD = birthDateYYYYMMDD;
    }

    /**
     * Gets the cityName value for this RequestBillingType.
     *
     * @return cityName
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Sets the cityName value for this RequestBillingType.
     *
     * @param cityName
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * Gets the countryCode value for this RequestBillingType.
     *
     * @return countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the countryCode value for this RequestBillingType.
     *
     * @param countryCode
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Gets the email value for this RequestBillingType.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email value for this RequestBillingType.
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the fax value for this RequestBillingType.
     *
     * @return fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the fax value for this RequestBillingType.
     *
     * @param fax
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * Gets the fullName value for this RequestBillingType.
     *
     * @return fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the fullName value for this RequestBillingType.
     *
     * @param fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the phone value for this RequestBillingType.
     *
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone value for this RequestBillingType.
     *
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the postalCode value for this RequestBillingType.
     *
     * @return postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postalCode value for this RequestBillingType.
     *
     * @param postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the streetName value for this RequestBillingType.
     *
     * @return streetName
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * Sets the streetName value for this RequestBillingType.
     *
     * @param streetName
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * Gets the streetNumber value for this RequestBillingType.
     *
     * @return streetNumber
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * Sets the streetNumber value for this RequestBillingType.
     *
     * @param streetNumber
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Gets the territoryCode value for this RequestBillingType.
     *
     * @return territoryCode
     */
    public String getTerritoryCode() {
        return territoryCode;
    }

    /**
     * Sets the territoryCode value for this RequestBillingType.
     *
     * @param territoryCode
     */
    public void setTerritoryCode(String territoryCode) {
        this.territoryCode = territoryCode;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestBillingType)) return false;
        RequestBillingType other = (RequestBillingType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.addressUnitNumber==null && other.getAddressUnitNumber()==null) ||
             (this.addressUnitNumber!=null &&
              this.addressUnitNumber.equals(other.getAddressUnitNumber()))) &&
            ((this.birthDateYYYYMMDD==null && other.getBirthDateYYYYMMDD()==null) ||
             (this.birthDateYYYYMMDD!=null &&
              this.birthDateYYYYMMDD.equals(other.getBirthDateYYYYMMDD()))) &&
            ((this.cityName==null && other.getCityName()==null) ||
             (this.cityName!=null &&
              this.cityName.equals(other.getCityName()))) &&
            ((this.countryCode==null && other.getCountryCode()==null) ||
             (this.countryCode!=null &&
              this.countryCode.equals(other.getCountryCode()))) &&
            ((this.email==null && other.getEmail()==null) ||
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.fax==null && other.getFax()==null) ||
             (this.fax!=null &&
              this.fax.equals(other.getFax()))) &&
            ((this.fullName==null && other.getFullName()==null) ||
             (this.fullName!=null &&
              this.fullName.equals(other.getFullName()))) &&
            ((this.phone==null && other.getPhone()==null) ||
             (this.phone!=null &&
              this.phone.equals(other.getPhone()))) &&
            ((this.postalCode==null && other.getPostalCode()==null) ||
             (this.postalCode!=null &&
              this.postalCode.equals(other.getPostalCode()))) &&
            ((this.streetName==null && other.getStreetName()==null) ||
             (this.streetName!=null &&
              this.streetName.equals(other.getStreetName()))) &&
            ((this.streetNumber==null && other.getStreetNumber()==null) ||
             (this.streetNumber!=null &&
              this.streetNumber.equals(other.getStreetNumber()))) &&
            ((this.territoryCode==null && other.getTerritoryCode()==null) ||
             (this.territoryCode!=null &&
              this.territoryCode.equals(other.getTerritoryCode())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAddressUnitNumber() != null) {
            _hashCode += getAddressUnitNumber().hashCode();
        }
        if (getBirthDateYYYYMMDD() != null) {
            _hashCode += getBirthDateYYYYMMDD().hashCode();
        }
        if (getCityName() != null) {
            _hashCode += getCityName().hashCode();
        }
        if (getCountryCode() != null) {
            _hashCode += getCountryCode().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getFax() != null) {
            _hashCode += getFax().hashCode();
        }
        if (getFullName() != null) {
            _hashCode += getFullName().hashCode();
        }
        if (getPhone() != null) {
            _hashCode += getPhone().hashCode();
        }
        if (getPostalCode() != null) {
            _hashCode += getPostalCode().hashCode();
        }
        if (getStreetName() != null) {
            _hashCode += getStreetName().hashCode();
        }
        if (getStreetNumber() != null) {
            _hashCode += getStreetNumber().hashCode();
        }
        if (getTerritoryCode() != null) {
            _hashCode += getTerritoryCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
