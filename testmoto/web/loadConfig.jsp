<%@ page import="com.directi.pg.core.GatewayAccountService"%>
<%

    out.println("Loading Gateway account </BR>");
   GatewayAccountService.loadGatewayAccounts();
    try
    {
        out.println("gatewayAccounts="+GatewayAccountService.getMerchants());
        GatewayAccountService.getMerchantDetails();
    }
    catch (Exception e)
    {
        //To change body of catch statement use File | Settings | File Templates.
    }

    out.println("Gateway account loaded </BR>");

%>