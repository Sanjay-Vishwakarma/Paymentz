<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 27/4/2017
  Time: 1:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<html>
<head>
  <title>Add Contractual Partner</title>

  <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/html5.js'></script>
  <script language="javascript">
    function isint(form)
    {
      if (isNaN(form.numrows.value))
        return false;
      else
        return true;
    }

    function isNumber(evt) {
      evt = (evt) ? evt : window.event;
      var charCode = (evt.which) ? evt.which : evt.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
      }
      return true;
    }

    <%
      String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
      session.setAttribute("submit","contractualPartner");
    %>
  </script>
  <style>

    @media (min-width: 768px) {
      .form-horizontal .control-label {
        text-align: left !important;
      }
    }
  </style>
</head>
<body>

<%
  ApplicationManager applicationManager = new ApplicationManager();
  Functions functions = new Functions();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String partnerid=(String)session.getAttribute("merchantid");
  List<String> memberidDetails=applicationManager.getPartnerBankDetail((String) session.getAttribute("merchantid"));

  String partnerId = Functions.checkStringNull(request.getParameter("partnerId"));
  String bankName = Functions.checkStringNull(request.getParameter("bankName"))==null?"":request.getParameter("bankName");
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String updateContractualPartner_Update_Contractual = StringUtils.isNotEmpty(rb1.getString("updateContractualPartner_Update_Contractual")) ? rb1.getString("updateContractualPartner_Update_Contractual") : "Update Contractual Partner";
  String updateContractualPartner_PartnerID = StringUtils.isNotEmpty(rb1.getString("updateContractualPartner_PartnerID")) ? rb1.getString("updateContractualPartner_PartnerID") : "Partner ID* :";
  String updateContractualPartner_Bank_Name = StringUtils.isNotEmpty(rb1.getString("updateContractualPartner_Bank_Name")) ? rb1.getString("updateContractualPartner_Bank_Name") : "Bank Name* :";
  String updateContractualPartner_Contractual_Partner = StringUtils.isNotEmpty(rb1.getString("updateContractualPartner_Contractual_Partner")) ? rb1.getString("updateContractualPartner_Contractual_Partner") : "Contractual Partner ID* :";
  String updateContractualPartner_Contractual_Name = StringUtils.isNotEmpty(rb1.getString("updateContractualPartner_Contractual_Name")) ? rb1.getString("updateContractualPartner_Contractual_Name") : "Contractual Partner Name* :";
  String updateContractualPartner_Button = StringUtils.isNotEmpty(rb1.getString("updateContractualPartner_Button")) ? rb1.getString("updateContractualPartner_Button") : "Button";

%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <form action="/partner/net/ContractualPartner" name="form" method="post">
        <div class="pull-right">
          <div class="btn-group">

            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();
                if("partnerid".equals(name))
                {
                  if("bankname".equals(name))
                  {
                    out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                  }
                }
                else
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
              }
            %>

            <button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>


          </div>
        </div>
      </form>
      <br><br>

      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=updateContractualPartner_Update_Contractual%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br>
            <%
              HashMap hash = (HashMap)request.getAttribute("contractualDetails");
              if(hash!=null)
              {
                String style="class=tr0";
            %>

            <form action="/partner/net/UpdateContractualPartner?ctoken=<%=ctoken%>" method="post" class="form-horizontal">

              <input type="hidden" value="<%=ctoken%>" name="ctoken">
              <input type="hidden" name="action" value="update">

              <div class="widget-content padding">

                <%
                  String error = (String) request.getAttribute("error");
                  if(functions.isValueNull(error))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                  }
                %>


                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label"><%=updateContractualPartner_PartnerID%></label>
                  <div class="col-md-4">
                    <input type="text" size="30" name="partnerid" class="form-control" disabled="true" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("partnerid"))%>" >
                    <input type="hidden" name="partnerid" value="<%=hash.get("partnerid")%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label"><%=updateContractualPartner_Bank_Name%></label>
                  <div class="col-md-4">
                    <input type="text" size="30" name="bankname" class="form-control" disabled="true" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("bankname"))%>" >
                    <input type="hidden" name="bankname" value="<%=hash.get("bankname")%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label"><%=updateContractualPartner_Contractual_Partner%></label>
                  <div class="col-md-4">
                    <input type="tel" onkeypress="return isNumber(event)" size="10" maxlength="10" class="form-control" name="contractualpartid" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("contractual_partnerid"))%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label"><%=updateContractualPartner_Contractual_Name%></label>
                  <div class="col-md-4">
                    <input type="text" maxlength="255" size="30" class="form-control" name="contractualpartname" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("contractual_partnername"))%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label" style="visibility:hidden;"><%=updateContractualPartner_Button%></label>
                  <div class="col-md-4">
                    <button type="submit" class="buttonform btn btn-default" name="add">
                      <i class="fa fa-save"></i>&nbsp;
                     <%=updateContractualPartner_Update_Contractual%>
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
  else if(request.getAttribute("statusMsg")!=null)
  {
    out.println("<div class=\"reportable\">");
    out.println(Functions.NewShowConfirmation1("Result",(String)request.getAttribute("statusMsg")) );
    out.println("</div>");
  }
  else
  {
    out.println("<div class=\"reportable\">");
    out.println(Functions.NewShowConfirmation1("Sorry", "No Records Found."));
    out.println("</div>");
  }
%>
</body>
</html>
