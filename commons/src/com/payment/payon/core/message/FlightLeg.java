package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlightLeg
{

    @XStreamAsAttribute
    private String number="1";
    @XStreamAsAttribute
    private String stopOverAllowed="true";
    private String carrierCode="JT";
    private String destination="STR";
    private String flightNumber="JU347";

    public void setCarrierCode(String carrierCode)
    {
        this.carrierCode = carrierCode;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public void setFlightNumber(String flightNumber)
    {
        this.flightNumber = flightNumber;
    }

    public void setClassOfService(String classOfService)
    {
        this.classOfService = classOfService;
    }

    public void setLegDepartureDate(String legDepartureDate)
    {
        this.legDepartureDate = legDepartureDate;
    }

    private String classOfService="F";
    private String legDepartureDate="2012-12-12";
}
