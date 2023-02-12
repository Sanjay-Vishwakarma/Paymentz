package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Passenger
{
    @XStreamAsAttribute
    private String restrictedTicket;

    public void setRestrictedTicket(String restrictedTicket)
    {
        this.restrictedTicket = restrictedTicket;
    }

    public void setPassengerName(String passengerName)
    {
        PassengerName = passengerName;
    }

    public void setOriginatingAirport(String originatingAirport)
    {
        OriginatingAirport = originatingAirport;
    }

    public void setTicketNumber(String ticketNumber)
    {
        TicketNumber = ticketNumber;
    }

    public void setAirlineCode(String airlineCode)
    {
        AirlineCode = airlineCode;
    }

    public void setAirlineName(String airlineName)
    {
        AirlineName = airlineName;
    }

    public void setCheckDigit(String checkDigit)
    {
        CheckDigit = checkDigit;
    }

    public void setAgentCode(String agentCode)
    {
        AgentCode = agentCode;
    }

    public void setAgentName(String agentName)
    {
        AgentName = agentName;
    }

    private String PassengerName="Bob Kosely";
    private String OriginatingAirport="BEG";
    private String TicketNumber="1234";
    private String AirlineCode="JAT";
    private String AirlineName="JJJ";
    private String CheckDigit="1234";
    private String AgentCode="JAJA";
    private String AgentName="JJJ";
    private Itinerary itinerary = new Itinerary();


}
