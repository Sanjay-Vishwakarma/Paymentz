<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 2/9/15
  Time: 1:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Logger logger=new Logger("manageGatewayAccount.jsp");
  Functions functions= new Functions();
%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","gatewayMaster");
%>
<html>
<head>
  <title><%=company%> | Add New Gateway Type</title>
  <script type="text/javascript">
    function check()
    {
      var msg = "" ;
      var flag = "false";
      if (document.getElementById("gateway").value.length == 0 || document.getElementById("gateway").value.length > 10)
      {
        msg = msg + "\nPlease enter Gateway/Gateway values should be less than 10 character";
        document.getElementById("gateway").focus();
        return;
      }
      if (document.getElementById("currency").value.length == 0 || document.getElementById("currency").value.length > 3)
      {
        msg = msg + "\nPlease enter currency/currency can be greater than 3 character.";
        alert(msg);
        document.getElementById("currency").focus();
        return;
      }
      if (document.getElementById("name").value.length == 0)
      {
        msg = msg + "\nPlease enter gateway name.";
        alert(msg);
        document.getElementById("name").focus();
        return;
      }
      document.addtype.submit();
    }
  </script>
  <script src="/merchant/javascript/hidde.js"></script>
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }

    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }


    /*    .textb{color: red!important;}*/

  </style>
  <style type="text/css">
    .field-icon
    {
      float:  right;
      margin-top: -25px;
      position: relative;
      z-index: 2;
    }


  </style>

</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String partnerId = (String) session.getAttribute("merchantid");
    String reqPartnerId = Functions.checkStringNull(request.getParameter("partnerid")) == null ? "" : request.getParameter("partnerid");
    PartnerFunctions partnerFunctions=new PartnerFunctions();
    TreeMap<String, String> subPartnersDetails = partnerFunctions.getPartnerDetailsForUI(partnerId);

    String gateway,currency,name,address,gatewaytable,pspcode,key,wsservice,wspassword;
    gateway = currency = name = address = gatewaytable = pspcode = key = wsservice = wspassword = "";
    Integer chargepercentage,taxpercentage,withdrawalcharge,reversalcharge,chargebackcharge,chargesaccount,taxaccount,highriskamount ;
    chargepercentage = taxpercentage = withdrawalcharge = reversalcharge = chargebackcharge = chargesaccount = taxaccount = highriskamount = 0;
    String timedifferencenormal,timedifferencedaylight;
    timedifferencenormal = timedifferencedaylight =  "00:00:00";
    String bankip = "";

    gateway=Functions.checkStringNull(request.getParameter("gateway")) == null ? "" : request.getParameter("gateway");
    currency=Functions.checkStringNull(request.getParameter("currency")) == null ? "" : request.getParameter("currency");
    name=Functions.checkStringNull(request.getParameter("name")) == null ? "" : request.getParameter("name");
    address=Functions.checkStringNull(request.getParameter("address")) == null ? "" : request.getParameter("address");
    gatewaytable=Functions.checkStringNull(request.getParameter("gatewaytablename")) == null ? "" : request.getParameter("gatewaytablename");
    pspcode=Functions.checkStringNull(request.getParameter("pspcode")) == null ? "" : request.getParameter("pspcode");
    key=Functions.checkStringNull(request.getParameter("key")) == null ? "" : request.getParameter("key");
    wsservice=Functions.checkStringNull(request.getParameter("wsservice")) == null ? "" : request.getParameter("wsservice");
    wspassword=Functions.checkStringNull(request.getParameter("wspassword")) == null ? "" : request.getParameter("wspassword");
    bankip=Functions.checkStringNull(request.getParameter("bankip")) == null ? "" : request.getParameter("bankip");

    //timedifferencenormal=Functions.checkStringNull(request.getParameter("timedifferencenormal")) == null ? "" : request.getParameter("timedifferencenormal");
    //timedifferencedaylight=Functions.checkStringNull(request.getParameter("timedifferencedaylight")) == null ? "" : request.getParameter("timedifferencedaylight");

%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">

      <div class="btn-group">
        <form action="/partner/gatewayMaster.jsp?ctoken=<%=ctoken%>" method="POST">
          <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;">Gateway Master
          </button>
        </form>
      </div>
      <br><br><br>

      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Add New Bank Account</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br>
            <form action="/partner/net/AddGatewayType?ctoken=<%=ctoken%>" method="post" name="addbankaccount" class="form-horizontal">
              <input type="hidden" value="true" name="isSubmitted">
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <div class="widget-content padding">

                <%
                  if(request.getAttribute("success")!=null)
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("success") + "</h5>");
                  }
                  if(request.getAttribute("errormsg")!=null)
                  {
                    ValidationErrorList error = (ValidationErrorList) request.getAttribute("errormsg");
                    for (Object errorList : error.errors())
                    {
                      ValidationException ve = (ValidationException) errorList;
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                    }
                  }
                %>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Gateway *(max 10 characters):</label>
                  <div class="col-md-4">
                    <input  size="10" type="text" name="gateway"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(gateway)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Name* :</label>
                  <div class="col-md-4">
                    <input type="text"   size="50" id="name" name="name"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Currency *(3 characters) :</label>
                  <div class="col-md-4">
                    <input type="text" name="currency" size="10" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(currency)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Is Cvv Optional* :</label>
                  <div class="col-md-4">
                    <select name="isCvvOptional" id="isCvvOptional">
                      <option value='N' selected >N</option>
                      <option value='Y'>Y</option>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Charge Percentage :</label>
                  <div class="col-md-4">
                    <input type="text" name="chargepercentage" size="10" id="chargepercentage" class="form-control" value="<%= chargepercentage%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Tax Percentage :</label>
                  <div class="col-md-4">
                    <input type="text" name="taxpercentage" id="taxpercentage" size="10" class="form-control" value="<%= taxpercentage%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">WithDraw Charge :</label>
                  <div class="col-md-4">
                    <input type="text" name="withdrawalcharge" id="withdrawalcharge" size="10" class="form-control" value="<%=withdrawalcharge%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Reversal Charge :</label>
                  <div class="col-md-4">
                    <input type="text" name="reversalcharge" id="reversalcharge" size="10" class="form-control" value="<%= reversalcharge%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">ChargeBackCharges :</label>
                  <div class="col-md-4">
                    <input type="text" name="chargebackcharge" id="chargebackcharge" size="10" class="form-control" value="<%= chargebackcharge%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Charges Account :</label>
                  <div class="col-md-4">
                    <input type="text" name="chargesaccount" id="chargesaccount" size="10" class="form-control" value="<%= chargesaccount%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Tax Account :</label>
                  <div class="col-md-4">
                    <input type="text"  name="taxaccount" id = "taxaccount" size="10" class="form-control" value="<%= taxaccount%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">HighRisk Amount :</label>
                  <div class="col-md-4">
                    <input type="text" name="highriskamount" id="highriskamount" size="10" class="form-control" value="<%= highriskamount%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Address :</label>
                  <div class="col-md-4">
                    <input type="textarea" name="address" id="address" size="10" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(address)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Gateway Table Name :</label>
                  <div class="col-md-4">
                    <input type="textarea" name="gatewaytablename" id="gatewaytable" size="10" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(gatewaytable)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Time Difference Normal :</label>
                  <div class="col-md-4">
                    <input type="textarea" name="timedifferencenormal" id="timedifferencenormal" size="10" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(timedifferencenormal)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Time Difference Day Light :</label>
                  <div class="col-md-4">
                    <input type="textarea" name="timedifferencedaylight" id="timedifferencedaylight" size="10" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(timedifferencedaylight)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Partner :</label>
                  <div class="col-md-4">
                    <select name="partnerid" class="form-control">
                      <%--<option value="">Select Partner Id</option>--%>
                      <%
                        for(String pid : subPartnersDetails.keySet())
                        {
                          String isSelected = "";
                          if (pid.equals(reqPartnerId))
                          {
                            isSelected = "selected";
                          }
                      %>
                      <option value="<%=pid%>" <%=isSelected%>><%=subPartnersDetails.get(pid)%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Bank IP Address :</label>
                  <div class="col-md-4">
                    <input type="textarea" name="bankip" id="bankip" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(bankip)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">PSP Code :</label>
                  <div class="col-md-4">
                    <input type="textarea" name="pspcode" id="pspcode" size="25" maxlength="255" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(pspcode)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Key :</label>
                  <div class="col-md-4">
                    <input type="textarea" name="key" id="key" size="25" maxlength="255" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(key)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">WS Service :</label>
                  <div class="col-md-4">
                    <input type="textarea" name="wsservice" id="wsservice" size="25" maxlength="255" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(wsservice)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">WS Password :</label>
                  <div class="col-md-4">
                    <input type="textarea" name="wspassword" id="wspassword" size="25" maxlength="255" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(wspassword)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label" style="visibility:hidden;">Button</label>
                  <div class="col-md-4">
                    <button type="submit" class="buttonform btn btn-default" name="add" value="Add Gateway Type" onclick="check();">
                      <i class="fa fa-save"></i>&nbsp;
                      Add Gateway Type
                    </button>
                  </div>
                  <div class="col-md-6"></div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<%
  }
  else
  {
    response.sendRedirect("/partner/logout.jsp");
    return;
  }
%>
</body>
</html>