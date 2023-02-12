<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 13/7/15
  Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Partner Details</title>
  <script type="">
    function isNumber(evt) {
      evt = (evt) ? evt : window.event;
      var charCode = (evt.which) ? evt.which : evt.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
      }
      return true;
    }
  </script>
</head>
<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>
<body>
<%
  Logger log = new Logger("addContractualPartner.jsp");
  Functions functions= new Functions();

  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >

      <div class="panel-heading" >
        Update Contractual Partner
        <div style="float: right;">
          <form action="/icici/contractualPartner.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Contractual Partner" name="submit" class="addnewmember" style="width:300px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Contractual Partner
            </button>
          </form>
        </div>
      </div><br>

      <%
        HashMap hash = (HashMap)request.getAttribute("contractualDetails");
        if(hash!=null)
        {
          String style="class=tr0";
      %>

      <table  align="center" width="70%" cellpadding="2" cellspacing="2">
        <form action="/icici/servlet/UpdateContractualPartner?ctoken=<%=ctoken%>" method="post" name="forms" >
          <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
          <input type="hidden" name="action" value="update">

          <%
            String errormsg=(String)request.getAttribute("error");
            if(errormsg!=null)
            {
              out.println("<center><font class=\"textb\"><b>"+errormsg+"</b></font></center>");
            }

           /* String errorMsg=(String)request.getAttribute("errorMsg");
            if(errormsg!=null)
            {
              out.println("<center><font class=\"textb\"><b>"+errorMsg+"</b></font></center>");
            }*/
          %>


          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center" >

                <tr><td colspan="4">&nbsp;</td></tr>

                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Partner ID*</td>
                  <td class="textb">:</td>
                  <td class="textb">
                    <input type="text" size="30" name="partnerid" class="txtbox" disabled="true" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("partnerid"))%>" >
                    <input type="hidden" name="partnerid" value="<%=hash.get("partnerid")%>">
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Bank Name*</td>
                  <td class="textb">:</td>
                  <td class="textb">
                    <input type="text" size="30" name="bankname" class="txtbox" disabled="true" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("bankname"))%>" >
                    <input type="hidden" name="bankname" value="<%=hash.get("bankname")%>">
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Contractual Partner ID</td>
                  <td class="textb">:</td>
                  <td class="textb">
                    <input type="tel" onkeypress="return isNumber(event)" size="10" maxlength="10" class="txtbox" name="contractualpartid" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("contractual_partnerid"))%>">
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Contractual Partner Name</td>
                  <td class="textb">:</td>
                  <td class="textb">
                    <input type="text" maxlength="255" size="30" class="txtbox" name="contractualpartname" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("contractual_partnername"))%>">
                  </td>
                </tr>

                <tr><td colspan="4">&nbsp;</td></tr>

                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb"></td>
                  <td class="textb"></td>
                  <td>
                    <input type="Submit" value="Save" class="buttonform">
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>

              </table>

            </td>
          </tr>
        </form>
      </table>
    </div>
  </div>
</div>

<%
  }
  else if(request.getAttribute("statusMsg")!=null)
  {
    out.println("<div class=\"reportable\">");
    out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")) );
    out.println("</div>");

  }
  else
  {
    out.println("<div class=\"reportable\">");
    out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
    out.println("</div>");
  }
%>

</body>
</html>