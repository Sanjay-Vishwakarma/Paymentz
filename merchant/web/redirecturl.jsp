<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/9/13
  Time: 1:46 AM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head/>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">
<br><br>
<br>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center" colspan="2">

            <img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">

        </td>
    </tr>
    <tr>
        <td align="center" colspan="2"><h3>

<%


	for(Object key: request.getParameterMap().keySet()){

    }
    String trackingId=request.getParameter("trackingid");
    String desc=request.getParameter("desc");
    String amount=request.getParameter("amount");
    String status=request.getParameter("status");
    String newchecksum=request.getParameter("newchecksum");
    String key="wD8XzyuRV0UFrpOXiOiHzVo0TH0YBoqb" ;
	//String key="w4WtDhrYpaxRvhuFWLEt1MXdIfGJguwD";
    if( status.equals("Y"))
    {
        status ="Approved";
        out.print("Thank you for shopping with us. Your credit card has been charged and your transaction is successful. We will be shipping your order to you soon.");
    }
    else if( status.equals("N"))
    {
        status ="Failed";
        out.print("Thank you for shopping with us. However it seems your credit card transaction failed.");

    }
    else if(status.equals("P"))
    {
        status ="Flagged as High Risk";
        out.print("Your Transaction has been classified as a HIGH RISK Transaction by our Credit Card Processor.This requires you to Fax us an Authorisation for this transaction in order to complete the processing. This process is required by our Credit Card Processor to ensure that this transaction is being done by a genuine card-holder. The transaction will NOT be completed (and your card will NOT be charged) if you do not fax required documents.");

    }
    else
    {
        status ="Failed";
        out.print("------------Security Error. Illegal access detected--------------");

        //Here you need to put in routines that handle other errors
        //to perform any operation in this condition

    }
%>
            </h3>
    </td></tr>
    <tr>
        <td align="center" width="40%"><b>Tracking Id =</b></td>
        <td align="left"><%=trackingId%></td>
    </tr>
    <tr>
        <td align="center" width="40%"><b>Order Id =</b></td>
        <td align="left"><%=desc%></td>
    </tr>
    <tr>
        <td align="center" width="40%"><b>Amount =</b></td>
        <td align="left"><%=amount%></td>
    </tr>
    <tr>
        <td align="center" width="40%"><b>Status =</b></td>
        <td align="left"><%=status%></td>
    </tr>
</table>
</body>
</html>