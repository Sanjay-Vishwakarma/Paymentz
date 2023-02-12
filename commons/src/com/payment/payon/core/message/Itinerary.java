package com.payment.payon.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Itinerary
{
    public void setFlightLeg(FlightLeg flightLeg)
    {
        this.flightLeg = flightLeg;
    }

    FlightLeg flightLeg = new FlightLeg();

}
