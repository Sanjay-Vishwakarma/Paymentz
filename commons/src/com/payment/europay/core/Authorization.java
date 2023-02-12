package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 8/18/13
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("authorization")
public class Authorization
{
    @XStreamAlias("serviceIP")
    @XStreamAsAttribute
    String serviceIP;

    @XStreamAlias("serviceKey")
    @XStreamAsAttribute
    String serviceKey;

    @XStreamAlias("routingKey")
    @XStreamAsAttribute
    String routingKey;
}
