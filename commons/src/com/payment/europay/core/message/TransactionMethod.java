
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionMethod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionMethod">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DD"/>
 *     &lt;enumeration value="CT"/>
 *     &lt;enumeration value="PP"/>
 *     &lt;enumeration value="IV"/>
 *     &lt;enumeration value="CC"/>
 *     &lt;enumeration value="DC"/>
 *     &lt;enumeration value="OT"/>
 *     &lt;enumeration value="VA"/>
 *     &lt;enumeration value="UA"/>
 *     &lt;enumeration value="RM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransactionMethod")
@XmlEnum
public enum TransactionMethod {

    DD,
    CT,
    PP,
    IV,
    CC,
    DC,
    OT,
    VA,
    UA,
    RM;

    public String value() {
        return name();
    }

    public static TransactionMethod fromValue(String v) {
        return valueOf(v);
    }

}
