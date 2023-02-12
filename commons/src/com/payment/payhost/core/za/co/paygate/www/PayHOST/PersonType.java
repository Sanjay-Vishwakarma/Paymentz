/**
 * PersonType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class PersonType  implements java.io.Serializable {
    private org.apache.axis.types.Token title;

    private org.apache.axis.types.Token firstName;

    private org.apache.axis.types.Token middleName;

    private org.apache.axis.types.Token lastName;

    private org.apache.axis.types.Token[] telephone;

    private org.apache.axis.types.Token[] mobile;

    private org.apache.axis.types.Token[] fax;

    private org.apache.axis.types.Token[] email;

    private java.util.Date dateOfBirth;

    private org.apache.axis.types.Token nationality;

    private org.apache.axis.types.Token idNumber;

    private org.apache.axis.types.Token idType;

    private org.apache.axis.types.Token socialSecurityNumber;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.AddressType address;

    public PersonType() {
    }

    public PersonType(
           org.apache.axis.types.Token title,
           org.apache.axis.types.Token firstName,
           org.apache.axis.types.Token middleName,
           org.apache.axis.types.Token lastName,
           org.apache.axis.types.Token[] telephone,
           org.apache.axis.types.Token[] mobile,
           org.apache.axis.types.Token[] fax,
           org.apache.axis.types.Token[] email,
           java.util.Date dateOfBirth,
           org.apache.axis.types.Token nationality,
           org.apache.axis.types.Token idNumber,
           org.apache.axis.types.Token idType,
           org.apache.axis.types.Token socialSecurityNumber,
           com.payment.payhost.core.za.co.paygate.www.PayHOST.AddressType address) {
           this.title = title;
           this.firstName = firstName;
           this.middleName = middleName;
           this.lastName = lastName;
           this.telephone = telephone;
           this.mobile = mobile;
           this.fax = fax;
           this.email = email;
           this.dateOfBirth = dateOfBirth;
           this.nationality = nationality;
           this.idNumber = idNumber;
           this.idType = idType;
           this.socialSecurityNumber = socialSecurityNumber;
           this.address = address;
    }


    /**
     * Gets the title value for this PersonType.
     * 
     * @return title
     */
    public org.apache.axis.types.Token getTitle() {
        return title;
    }


    /**
     * Sets the title value for this PersonType.
     * 
     * @param title
     */
    public void setTitle(org.apache.axis.types.Token title) {
        this.title = title;
    }


    /**
     * Gets the firstName value for this PersonType.
     * 
     * @return firstName
     */
    public org.apache.axis.types.Token getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this PersonType.
     * 
     * @param firstName
     */
    public void setFirstName(org.apache.axis.types.Token firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the middleName value for this PersonType.
     * 
     * @return middleName
     */
    public org.apache.axis.types.Token getMiddleName() {
        return middleName;
    }


    /**
     * Sets the middleName value for this PersonType.
     * 
     * @param middleName
     */
    public void setMiddleName(org.apache.axis.types.Token middleName) {
        this.middleName = middleName;
    }


    /**
     * Gets the lastName value for this PersonType.
     * 
     * @return lastName
     */
    public org.apache.axis.types.Token getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this PersonType.
     * 
     * @param lastName
     */
    public void setLastName(org.apache.axis.types.Token lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the telephone value for this PersonType.
     * 
     * @return telephone
     */
    public org.apache.axis.types.Token[] getTelephone() {
        return telephone;
    }


    /**
     * Sets the telephone value for this PersonType.
     * 
     * @param telephone
     */
    public void setTelephone(org.apache.axis.types.Token[] telephone) {
        this.telephone = telephone;
    }

    public org.apache.axis.types.Token getTelephone(int i) {
        return this.telephone[i];
    }

    public void setTelephone(int i, org.apache.axis.types.Token _value) {
        this.telephone[i] = _value;
    }


    /**
     * Gets the mobile value for this PersonType.
     * 
     * @return mobile
     */
    public org.apache.axis.types.Token[] getMobile() {
        return mobile;
    }


    /**
     * Sets the mobile value for this PersonType.
     * 
     * @param mobile
     */
    public void setMobile(org.apache.axis.types.Token[] mobile) {
        this.mobile = mobile;
    }

    public org.apache.axis.types.Token getMobile(int i) {
        return this.mobile[i];
    }

    public void setMobile(int i, org.apache.axis.types.Token _value) {
        this.mobile[i] = _value;
    }


    /**
     * Gets the fax value for this PersonType.
     * 
     * @return fax
     */
    public org.apache.axis.types.Token[] getFax() {
        return fax;
    }


    /**
     * Sets the fax value for this PersonType.
     * 
     * @param fax
     */
    public void setFax(org.apache.axis.types.Token[] fax) {
        this.fax = fax;
    }

    public org.apache.axis.types.Token getFax(int i) {
        return this.fax[i];
    }

    public void setFax(int i, org.apache.axis.types.Token _value) {
        this.fax[i] = _value;
    }


    /**
     * Gets the email value for this PersonType.
     * 
     * @return email
     */
    public org.apache.axis.types.Token[] getEmail() {
        return email;
    }


    /**
     * Sets the email value for this PersonType.
     * 
     * @param email
     */
    public void setEmail(org.apache.axis.types.Token[] email) {
        this.email = email;
    }

    public org.apache.axis.types.Token getEmail(int i) {
        return this.email[i];
    }

    public void setEmail(int i, org.apache.axis.types.Token _value) {
        this.email[i] = _value;
    }


    /**
     * Gets the dateOfBirth value for this PersonType.
     * 
     * @return dateOfBirth
     */
    public java.util.Date getDateOfBirth() {
        return dateOfBirth;
    }


    /**
     * Sets the dateOfBirth value for this PersonType.
     * 
     * @param dateOfBirth
     */
    public void setDateOfBirth(java.util.Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    /**
     * Gets the nationality value for this PersonType.
     * 
     * @return nationality
     */
    public org.apache.axis.types.Token getNationality() {
        return nationality;
    }


    /**
     * Sets the nationality value for this PersonType.
     * 
     * @param nationality
     */
    public void setNationality(org.apache.axis.types.Token nationality) {
        this.nationality = nationality;
    }


    /**
     * Gets the idNumber value for this PersonType.
     * 
     * @return idNumber
     */
    public org.apache.axis.types.Token getIdNumber() {
        return idNumber;
    }


    /**
     * Sets the idNumber value for this PersonType.
     * 
     * @param idNumber
     */
    public void setIdNumber(org.apache.axis.types.Token idNumber) {
        this.idNumber = idNumber;
    }


    /**
     * Gets the idType value for this PersonType.
     * 
     * @return idType
     */
    public org.apache.axis.types.Token getIdType() {
        return idType;
    }


    /**
     * Sets the idType value for this PersonType.
     * 
     * @param idType
     */
    public void setIdType(org.apache.axis.types.Token idType) {
        this.idType = idType;
    }


    /**
     * Gets the socialSecurityNumber value for this PersonType.
     * 
     * @return socialSecurityNumber
     */
    public org.apache.axis.types.Token getSocialSecurityNumber() {
        return socialSecurityNumber;
    }


    /**
     * Sets the socialSecurityNumber value for this PersonType.
     * 
     * @param socialSecurityNumber
     */
    public void setSocialSecurityNumber(org.apache.axis.types.Token socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }


    /**
     * Gets the address value for this PersonType.
     * 
     * @return address
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.AddressType getAddress() {
        return address;
    }


    /**
     * Sets the address value for this PersonType.
     * 
     * @param address
     */
    public void setAddress(com.payment.payhost.core.za.co.paygate.www.PayHOST.AddressType address) {
        this.address = address;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PersonType)) return false;
        PersonType other = (PersonType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            ((this.middleName==null && other.getMiddleName()==null) || 
             (this.middleName!=null &&
              this.middleName.equals(other.getMiddleName()))) &&
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            ((this.telephone==null && other.getTelephone()==null) || 
             (this.telephone!=null &&
              java.util.Arrays.equals(this.telephone, other.getTelephone()))) &&
            ((this.mobile==null && other.getMobile()==null) || 
             (this.mobile!=null &&
              java.util.Arrays.equals(this.mobile, other.getMobile()))) &&
            ((this.fax==null && other.getFax()==null) || 
             (this.fax!=null &&
              java.util.Arrays.equals(this.fax, other.getFax()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              java.util.Arrays.equals(this.email, other.getEmail()))) &&
            ((this.dateOfBirth==null && other.getDateOfBirth()==null) || 
             (this.dateOfBirth!=null &&
              this.dateOfBirth.equals(other.getDateOfBirth()))) &&
            ((this.nationality==null && other.getNationality()==null) || 
             (this.nationality!=null &&
              this.nationality.equals(other.getNationality()))) &&
            ((this.idNumber==null && other.getIdNumber()==null) || 
             (this.idNumber!=null &&
              this.idNumber.equals(other.getIdNumber()))) &&
            ((this.idType==null && other.getIdType()==null) || 
             (this.idType!=null &&
              this.idType.equals(other.getIdType()))) &&
            ((this.socialSecurityNumber==null && other.getSocialSecurityNumber()==null) || 
             (this.socialSecurityNumber!=null &&
              this.socialSecurityNumber.equals(other.getSocialSecurityNumber()))) &&
            ((this.address==null && other.getAddress()==null) || 
             (this.address!=null &&
              this.address.equals(other.getAddress())));
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
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getMiddleName() != null) {
            _hashCode += getMiddleName().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getTelephone() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTelephone());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTelephone(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMobile() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMobile());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMobile(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFax() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFax());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFax(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEmail() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEmail());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEmail(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDateOfBirth() != null) {
            _hashCode += getDateOfBirth().hashCode();
        }
        if (getNationality() != null) {
            _hashCode += getNationality().hashCode();
        }
        if (getIdNumber() != null) {
            _hashCode += getIdNumber().hashCode();
        }
        if (getIdType() != null) {
            _hashCode += getIdType().hashCode();
        }
        if (getSocialSecurityNumber() != null) {
            _hashCode += getSocialSecurityNumber().hashCode();
        }
        if (getAddress() != null) {
            _hashCode += getAddress().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PersonType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PersonType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "FirstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("middleName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "MiddleName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "LastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telephone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Telephone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobile");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Mobile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Fax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateOfBirth");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "DateOfBirth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nationality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Nationality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "IdNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "IdType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("socialSecurityNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SocialSecurityNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "AddressType"));
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
