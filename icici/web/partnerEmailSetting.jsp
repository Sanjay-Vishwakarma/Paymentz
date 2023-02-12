<%@ page import="com.directi.pg.Functions" %>
<%@ include file="functions.jsp" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>

<%!
  private static Logger log=new Logger("partnerEmailSetting.jsp");
%>

<html>
<head>
</head>
<title> Default Configuration </title>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Partner Email Notification
      <div style="float: right;">
      <form action="/icici/servlet/ListPartnerDetails?ctoken=<%=ctoken%>" name="form" method="post">
      <% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        String partnerid = request.getParameter("partnerid")==null?"":request.getParameter("partnerid");
      %>
        <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i
                class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;Go Back
        </button>
      </form>
      </div>
      </div>

      <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <br>
        <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>
              <%
                String errormsg1 = (String) request.getAttribute("error");
                if (errormsg1 != null)
                {
                  out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                }
                String errormsg = (String) request.getAttribute("cbmessage");
                /*if (errormsg == null)
                  errormsg = "";
                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                out.println(errormsg);
                out.println("</b></font></td></tr></table>");*/
                Hashtable temphash = null;

              %>

                <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb">Partner Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <input name="partnerid" id="pid1" value="<%=partnerid%>" class="txtbox" disabled>
                    <%--<input type="text" size="10"name="partnerid"class="txtbox" value=<%=partnerId1%>>--%>
                  </td>
                </tr>
                </table>
                </form>
            </td>
          </tr>
          </table>
      <form action="/icici/servlet/UpdateEmailSetting?ctoken=<%=ctoken%>" method=post>
        <input type="hidden" value="<%=partnerid%>" name="partnerid">

      <%
      Hashtable hash = (Hashtable) request.getAttribute("memberdetails");
      int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
      int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
      int records = 0;
      int totalrecords = 0;
      if ((hash != null && hash.size() > 0))
      {
        try
        {
          records = Integer.parseInt((String) hash.get("records"));
          totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
        }
        catch (Exception ex)
        {
          log.error("Records & TotalRecords is found null", ex);
        }
      }
      if (records > 0)
      {
    %>
    <br><br>
    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <thead>
      <tr class="th0">
        <td colspan="8" style="height: 30px">
          <center><b>Sales Contact</b></center>
        </td>
      </tr>
      </thead>
      <%
        String style = "td1";
        for (int pos = 1; pos <= records; pos++)
        {
          String id = Integer.toString(pos);
          int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
          if (pos % 2 == 0)
            style = "tr0";
          else
            style = "tr0";
          temphash = (Hashtable) hash.get(id);
          String partnerId = (String) temphash.get("partnerid");
          String partnerName = (String) temphash.get("partnerName");
          String siteUrl=(String) temphash.get("siteurl");
          String isReadOnly = "";
      %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Descriptor Update</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Sales Transaction</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Admin Failed Transactions</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Partner Card Registration</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Card Registration</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Payout Transaction</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Failed Transactions</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Reject Transaction</td>
        </tr>
        <tr>
          <td valign="middle" data-label="Billing Descriptor Update" align="center"

              class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                         name='billingDescriptor' <%=isReadOnly%>>
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesBillingDescriptor")))); %></select>
          </td>
          <td valign="middle" data-label="Merchant Sales Transaction" align="center"

              class="<%=style%>">
            <select class="form-control" style="background: #ffffff" name='salesTransaction' <%=isReadOnly%>>
              <%
                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchantSalesTransaction")))); %>
            </select>
          </td>
          <td valign="middle" data-label="AuthFailed Transactions" align="center"

              class="<%=style%>">
            <select class="form-control" style="background: #ffffff" name='adminFailedTransaction' <%=isReadOnly%>>
              <%
                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesAdminFailedTransaction")))); %></select>
          </td>
          <td valign="middle" data-label="Partner Card Registration" align="center"

              class="<%=style%>">
            <select class="form-control" style="background: #ffffff" name='partnerCardRegistration' <%=isReadOnly%>>
              <%
                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesPartnerCardRegistration")))); %></select>
          </td>
          <td valign="middle" data-label="Merchant Card Registration" align="center"

              class="<%=style%>">
            <select class="form-control" style="background: #ffffff" name='merchantCardRegistration' <%=isReadOnly%>>
              <%
                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesMerchantCardRegistration")))); %></select>
          </td>
          <td valign="middle" data-label="Payout Transaction" align="center"

              class="<%=style%>">
            <select class="form-control" style="background: #ffffff" name='payoutTransaction' <%=isReadOnly%>>
              <%
                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesPayoutTransaction")))); %></select>
          </td>
          <td valign="middle" data-label="Failed Transaction" align="center"

              class="<%=style%>">
            <select class="form-control" style="background: #ffffff" name='failedTransaction' <%=isReadOnly%>>
              <%
                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("failedTransaction")))); %></select>
          </td>
          <td valign="middle" data-label="Reject Email" align="center"

              class="<%=style%>">
            <select class="form-control" style="background: #ffffff" name='rejectTransaction' <%=isReadOnly%>>
              <%
                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("rejectTransaction")))); %></select>
          </td>
        </tr>
      </table>

    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="8" style="height: 30px">
          <center><b>Fraud Contact</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >  Fraud Failed Transaction  </td>
      </tr>
      <tr>
        <td valign="middle" data-label="Fraud Failed Transaction" align="center"

            class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                       name='fraudFailedTransaction' <%=isReadOnly%>>
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("fraudFailedTransaction")))); %></select>
        </td>
      </tr>


    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="th0">
        <td colspan="12" style="height: 30px">
          <center><b>Chargeback Contact</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Transaction</td>
      </tr>
      <tr>
        <td valign="middle" data-label="Chargeback Transaction" align="center"
            class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                       name='chargebackTransaction' <%=isReadOnly%>>
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("chargebackTransaction")))); %></select>
        </td>
      </tr>

    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <tr class="th0"><td colspan="5" style="height: 30px"><center><b>Refund Contact</b></center></td></tr>
      <tr>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Refund Transaction</td>
      </tr>
      <tr>
        <td valign="middle" data-label="Refund Transaction" align="center"
            class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                       name='refundTransaction' <%=isReadOnly%>>
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("refundTransaction")))); %></select>
        </td>
      </tr>
    </table>
      </table>
      </table>

        <%
    }
  %>
  <%--<table align="center">
    <tr>
      <td>
        <button type="submit" value="Save" class="buttonform" style="margin-left: 76%">
          Save
        </button>
      </td>
    </tr>
  </table>--%>
        <table align="center">
          <center>
            <label>&nbsp;</label>
            <input type="hidden" value="1" name="step">
            <button type="submit" class="btn btn-default" id="submit" name="action"
                    value="Save" style="display: -webkit-box;"><i
                    class="fa fa-save"></i>&nbsp;&nbsp;Save
            </button>
          </center>
          </table>
    </table>
    </table>
    </table>
      </form>
<br>
<%
  }
  else if (errormsg!= null)
  {
    out.println(Functions.NewShowConfirmation("", errormsg));
    out.println("</div>");
  }
  else
  {
    out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    out.println("</div>");
  }
%>

    </div>
</div>
</div>
</div>
</body>
</html>