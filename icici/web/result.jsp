
<html>
<head>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<title>Result</title>

</head>

<body>
<%
     ServletContext ctx=config.getServletContext();

     String ccnum=(String)ctx.getAttribute("PAN");
     String Expirydate=(String)ctx.getAttribute("Expirydate");
     String PAmount=(String)ctx.getAttribute("PurchaseAmount");
     String orderdesc=(String)ctx.getAttribute("orderdesc");

     ctx.removeAttribute("PAN");
     ctx.removeAttribute("Expirydate");
     ctx.removeAttribute("PurchaseAmount");
     ctx.removeAttribute("orderdesc");

     out.println(ccnum+" "+Expirydate+" "+PAmount+" "+orderdesc);
     String SHOPPING_CONTEXT=request.getParameter("shoppingcontext");
     String STATUS= request.getParameter("status");
     String CAVV= request.getParameter("cavv");
     String ECI= request.getParameter("eci");
     String Xid= request.getParameter("xid");
     String PurchaseAmount= request.getParameter("purchaseamount");
     String Currency= request.getParameter("currency");

    String MID1="00000558",MID2="00000555",MID3="00000554";

    if(SHOPPING_CONTEXT==null)
        SHOPPING_CONTEXT="";
    if(CAVV==null)
        CAVV="";
    if(STATUS==null)
        STATUS="";
    if(ECI==null)
        ECI="";
    if(Xid==null)
        Xid="";
    if(PurchaseAmount==null)
        PurchaseAmount="";
    if(Currency==null)
        Currency="";


    out.println("<BR>SHOPPINGCONTEXT "+SHOPPING_CONTEXT);
    out.println("<BR>STATUS "+STATUS);
    out.println("<BR>CAVV "+CAVV);
    out.println("<BR>ECI "+ECI);
    out.println("<BR>Xid "+Xid);
    out.println("<BR>PurchaseAmount "+PurchaseAmount);
    out.println("<BR>Currency "+Currency);
    out.println("<BR><BR><BR>Assuming below MIDs for corresponding ECI ");
    out.println("<BR>ECI-7  MID1 "+MID1);
    out.println("<BR>ECI-6  MID2 "+MID2);
    out.println("<BR>ECI-5  MID3 "+MID3);

     String MID="";
     String message="";
    if(STATUS!=null || !STATUS.equals(""))
    {
    if(STATUS.equalsIgnoreCase("U"))
        {
            message="Authentication Could Not Be Performed due to technical or other problems";
            MID=MID1;
        }
    else if(STATUS.equalsIgnoreCase("X"))
        {
            message="Got Status X";
            MID=MID2;
        }
    else if(STATUS.equalsIgnoreCase("Y"))
    {
            message="Authentication Successful";
            if(CAVV.equalsIgnoreCase("Received"))
                MID=MID1;
            else if(CAVV.equalsIgnoreCase("Not Received"))
                MID=MID3;
     }
    else if(STATUS.equalsIgnoreCase("E"))
    {
            message="Cardholder not Enrolled, authentication failed";
            MID=MID1;
        }
    else if(STATUS.equalsIgnoreCase("N"))
    {
            message="Cardholder enrolled but authentication failed Customer failed authentication";
            MID=MID1;
        }
    else
    {
            message="Invalid status";
            MID="MID0";
        }
    }
    out.println("<br><BR>message-<font color=\"red\"> "+message+"</font>");
    out.println("<BR>MID- "+ MID);
     ccnum=null;
     Expirydate=null;
%>
<%--
<jsp:forward page="confirmation.jsp" >
  	<jsp:param name="status" value="<%=STATUS%>"/>
  	<jsp:param name="orderdesc" value="<%=orderdesc%>"/>
</jsp:forward>
 --%>
<%--
<jsp:forward page="/icici/servlet/TestServlet" >

<jsp:param name="merchantID" value="<%=MID%>"/>
  	<jsp:param name="PAN" value="<%=ccnum%>"/>
  	<jsp:param name="Expirydate" value="<%=Expirydate%>"/>
  	<jsp:param name="PurchaseAmount" value="<%=PurchaseAmount%>"/>
  	<jsp:param name="message" value="<%=message%>"/>

</jsp:forward>


<jsp:param name="merchantID" value="00000554"/>
  	<jsp:param name="PAN" value="4864100002401003"/>
  	<jsp:param name="Expirydate" value="0703" />
  	<jsp:param name="PurchaseAmount" value="123456"/>


  --%>

<p>&nbsp;&nbsp; </p>
</body>

</html>
