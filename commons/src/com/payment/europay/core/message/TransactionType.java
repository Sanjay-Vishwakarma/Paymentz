
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PA"/>
 *     &lt;enumeration value="DB"/>
 *     &lt;enumeration value="CP"/>
 *     &lt;enumeration value="CD"/>
 *     &lt;enumeration value="RV"/>
 *     &lt;enumeration value="RF"/>
 *     &lt;enumeration value="RB"/>
 *     &lt;enumeration value="CB"/>
 *     &lt;enumeration value="RC"/>
 *     &lt;enumeration value="RG"/>
 *     &lt;enumeration value="RR"/>
 *     &lt;enumeration value="DR"/>
 *     &lt;enumeration value="CF"/>
 *     &lt;enumeration value="SD"/>
 *     &lt;enumeration value="RS"/>
 *     &lt;enumeration value="DS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransactionType")
@XmlEnum
public enum TransactionType {

    PA,
    DB,
    CP,
    CD,
    RV,
    RF,
    RB,
    CB,
    RC,
    RG,
    RR,
    DR,
    CF,
    SD,
    RS,
    DS;

    public String value() {
        return name();
    }

    public static TransactionType fromValue(String v) {
        return valueOf(v);
    }

}
