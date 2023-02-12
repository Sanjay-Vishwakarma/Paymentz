<%--
  Created by IntelliJ IDEA.
  User: Sanjay
  Date: 1/13/2022
  Time: 1:21 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="index.jsp" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String style="class=tr1";

%>
<html>
<head>
    <title>BD Payout Details Update</title>
  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>



  <script>


    $(function() {
      $( ".datepicker" ).datepicker();

    });
</script>
<script>
    function validateInput(){

      var error           = "";
      var bankBatchId    =  document.getElementById("bankBatchId").value;
      console.log("bankBatchId ",+bankBatchId);

      if(bankBatchId == ""){
        error += "Please Enter Bank BatchId \n";
      }

      console.log("validateInput "+error);
      if (error != "")
      {
        alert(error);
        return false;
      }
      else
      {
        document.getElementById("updateform").submit();
      }
    }
  </script>


</head>
<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
       Payout Batch Update
        <div style="float:right;">
          <form action="/icici/bdPayoutBatch.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="back" name="submit" style="width: 150px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Back
            </button>
          </form>
        </div>
      </div>

      <%
//        User user       =  (User)session.getAttribute("ESAPIUserSessionKey");
//        String ctoken   = null;
        if(user != null)
        {
          ctoken = user.getCSRFToken();
        }
        String bankBatchId                     = Functions.checkStringNull(request.getParameter("bankBatchId"))==null?"":request.getParameter("bankBatchId");
        String bankCount                     = Functions.checkStringNull(request.getParameter("bankCount"))==null?"":request.getParameter("bankCount");
        String bankAmount                     = Functions.checkStringNull(request.getParameter("bankCount"))==null?"":request.getParameter("bankCount");
        String sysBatchid=request.getParameter("sysBatchid");
      //  System.out.println("sysbatch id--"+sysBatchid);
      %>
      <form id="updateform" action="/icici/servlet/BdPayoutBatch?ctoken=<%=ctoken%>" name="updateform" method="post">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <input type="hidden" value="<%=sysBatchid%>" name="sysBatchid" id="sysBatchid">

        <br>
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <%

                String errormsg = (String) request.getAttribute("message");
                if (errormsg == null)
                  errormsg = "";
                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                out.println(errormsg);
                out.println("</b></font></td></tr></table>");
              %>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                </tr>
                <tr>
                  <td width="43%" class="textb">Bank Batch ID *</td>
                  <td width="5%" class="textb">:</td>
                  <td>
                    <input type=text name="bankBatchId" id="bankBatchId" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankBatchId)%>" class="txtbox" autocomplete="on">

                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="43%" class="textb">Bank Count *</td>
                  <td width="5%" class="textb">:</td>
                  <td>
                    <input type=text name="bankCount" id="bankCount" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankCount)%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>

                <tr>
                  <td width="43%" class="textb">Bank Amount *</td>
                  <td width="5%" class="textb">:</td>
                  <td>
                    <input type=text name="bankAmount" id="bankAmount" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankAmount)%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
               <%-- <tr>
                  <td width="43%" class="textb">Account Id*</td>
                  <td width="5%" class="textb">:</td>
                  <td>
                    &lt;%&ndash;<input name="accountId" id="accid" value="<%=accountid%>"  class="txtbox" autocomplete="on">&ndash;%&gt;
                    <input name="accountId"  id="accountid1" value="<%=accountid%>"  class="txtbox" autocomplete="on">
                  </td>
                </tr>--%>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td>&nbsp;&nbsp;</td>
                  <td>&nbsp;&nbsp;</td>
                  <td style="display: inline-flex">
                    <button <%--onclick="validateInput()"--%> type="submit"  id="updateBatchdetails" name="action" value="updateBatch"
                            class="btn btn-default center-block"
                            style="display: inline-block!important;min-width:30%;">
                      &nbsp;&nbsp;Update
                    </button>
                    &nbsp;&nbsp;
                  </td>
                </tr>
                </tbody>
              </table>
            </td>
          </tr>
          </tbody>
        </table>
      </form>
    </div>
  </div>
</div>

</body>
</html>