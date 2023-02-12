package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarketData
{
    @XStreamAsAttribute
    private String type="Airline";
    private PassengerData passengerData = new PassengerData();



}
