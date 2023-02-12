package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 8/18/13
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("token")
public class Token
{
    @XStreamAlias("ccBrand")
    @XStreamAsAttribute
    String ccBrand;

    @XStreamAlias("ownerLastName")
    @XStreamAsAttribute
    String ownerLastName;

    @XStreamAlias("ownerFirstName")
    @XStreamAsAttribute
    String ownerFirstName;

    @XStreamAlias("expireMonth")
    @XStreamAsAttribute
    String expireMonth;

    @XStreamAlias("expireYear")
    @XStreamAsAttribute
    String expireYear;

    @XStreamAlias("lead6")
    @XStreamAsAttribute
    String lead6;

    @XStreamAlias("trail4")
    @XStreamAsAttribute
    String trail4;

    @XStreamAlias("panAlias")
    @XStreamAsAttribute
    String panAlias;

    @XStreamAlias("value")
    @XStreamAsAttribute
    String value;
}
