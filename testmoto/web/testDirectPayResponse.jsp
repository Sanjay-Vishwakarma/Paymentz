<%

    String responseParams = request.getParameter("responseparams");

    String [] paramArray = responseParams.split("\\|");


    out.println("responseParams "+responseParams+"<BR><BR>");
    out.println("DirectPay Ref ID "+paramArray[0]+"<BR>");
    out.println("Flag "+paramArray[1]+"<BR>");
    out.println("Country "+paramArray[2]+"<BR>");
    out.println("Curency "+paramArray[3]+"<BR>");
    out.println("Amount "+paramArray[6]+"<BR>");
    out.println("Merchant Order No "+paramArray[5]+"<BR>");
    out.println("Other Details "+paramArray[4]+"<BR>");

%>