<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Iterator" %>
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
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
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
  String pid = "";
  String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
  String Config ="";
  if(Roles.contains("superpartner")){
    pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
  }else{
    Config = "disabled";
    pid = String.valueOf(session.getAttribute("merchantid"));
  }
  List<String> memberidDetails = applicationManager.getPartnerBankDetail((String) session.getAttribute("merchantid"));
  String bankName = Functions.checkStringNull(request.getParameter("bankName"))==null?"":request.getParameter("bankName");
  String contractualpartid = Functions.checkStringNull(request.getParameter("contractualpartid"))==null?"":request.getParameter("contractualpartid");
  String contractualpartname = Functions.checkStringNull(request.getParameter("contractualpartname"))==null?"":request.getParameter("contractualpartname");
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String addContractualPartner_Add_New_Contractual = StringUtils.isNotEmpty(rb1.getString("addContractualPartner_Add_New_Contractual")) ? rb1.getString("addContractualPartner_Add_New_Contractual") : "Add New Contractual Partner";
  String addContractualPartner_Partner_ID = StringUtils.isNotEmpty(rb1.getString("addContractualPartner_Partner_ID")) ? rb1.getString("addContractualPartner_Partner_ID") : "Partner ID* :";
  String addContractualPartner_Bank_Name = StringUtils.isNotEmpty(rb1.getString("addContractualPartner_Bank_Name")) ? rb1.getString("addContractualPartner_Bank_Name") : "Bank Name* :";
  String addContractualPartner_Select_Bank = StringUtils.isNotEmpty(rb1.getString("addContractualPartner_Select_Bank")) ? rb1.getString("addContractualPartner_Select_Bank") : "Select Bank";
  String addContractualPartner_Contractual_PartnerID = StringUtils.isNotEmpty(rb1.getString("addContractualPartner_Contractual_PartnerID")) ? rb1.getString("addContractualPartner_Contractual_PartnerID") : "Contractual Partner ID* :";
  String addContractualPartner_Contractual_Name = StringUtils.isNotEmpty(rb1.getString("addContractualPartner_Contractual_Name")) ? rb1.getString("addContractualPartner_Contractual_Name") : "Contractual Partner Name* :";
  String addContractualPartner_Button = StringUtils.isNotEmpty(rb1.getString("addContractualPartner_Button")) ? rb1.getString("addContractualPartner_Button") : "Button";
  String addContractualPartner_Add_Contractual = StringUtils.isNotEmpty(rb1.getString("addContractualPartner_Add_Contractual")) ? rb1.getString("addContractualPartner_Add_Contractual") : "Add Contractual Partner";
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <%--<form action="/partner/contractualPartner.jsp?ctoken=<%=ctoken%>" name="form" method="post">
        <div class="pull-right">
          <div class="btn-group">


            <%

              Enumeration<String> aName=request.getParameterNames();
              while(aName.hasMoreElements())
              {
                String name=aName.nextElement();
                String value = request.getParameter(name);
                if(value==null || value.equals("null"))
                {
                  value = "";
                }
            %>
            <input type=hidden name=<%=name%> value=<%=value%>>
            <%
              }

            %>
            <button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>


          </div>
        </div>
      </form>--%>


      <form action="/partner/contractualPartner.jsp" name="form" method="post">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addContractualPartner_Add_New_Contractual%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br>

            <form action="/partner/net/AddContractualPartner?ctoken=<%=ctoken%>" method="post" class="form-horizontal">

              <input type="hidden" value="<%=ctoken%>" name="ctoken">
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <input type="hidden" id="partnerid" value="<%=partnerid%>" name="ctoken">
              <div class="widget-content padding">

                <%
                  String status = (String) request.getAttribute("status");
                  if(functions.isValueNull(status))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + status + "</h5>");
                  }

                  String statusMsg = (String) request.getAttribute("statusMsg");
                  if(functions.isValueNull(statusMsg))
                  {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + statusMsg + "</h5>");
                  }

                  String message = (String) request.getAttribute("message");
                  if(functions.isValueNull(message))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                  }

                  String error = (String) request.getAttribute("error");
                  if(functions.isValueNull(error))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                  }

                %>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label"><%=addContractualPartner_Partner_ID%></label>
                  <div class="col-md-4">
                    <input type="text" value="<%=pid%>" name="pid"  id="pid"  class="form-control" autocomplete="on" <%=Config%>>
                    <input type="hidden" value="<%=pid%>" name="pid" >
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label"><%=addContractualPartner_Bank_Name%></label>
                  <div class="col-md-4">
                    <select size="1" name="bankName" class="form-control">
                      <option value=""><%=addContractualPartner_Select_Bank%></option>
                      <%
                        if(memberidDetails.size()>0)
                        {
                          Iterator iterator = memberidDetails.iterator();

                          while (iterator.hasNext())
                          {
                            //Map.Entry<String,String> memberEntry = (Map.Entry<String, String>) iterator.next();
                            //int memberId = Integer.parseInt(memberEntry.getKey());
                            String memberName = (String) iterator.next();

                            String select = "";
                            if(memberName.equalsIgnoreCase(bankName))
                              select = "selected";
                            else
                              select = "";
                      %>
                      <option value="<%=memberName%>" <%=select%>><%=memberName%></option>
                      <%
                          }
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label"><%=addContractualPartner_Contractual_PartnerID%></label>
                  <div class="col-md-4">
                    <input  size="10" maxlength="10" type="text"  name="contractualpartid"  class="form-control" value="<%=contractualpartid%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label"><%=addContractualPartner_Contractual_Name%></label>
                  <div class="col-md-4">
                    <input  size="10" type="text" name="contractualpartname"  class="form-control" value="<%=contractualpartname%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-3 control-label" style="visibility:hidden;"><%=addContractualPartner_Button%></label>
                  <div class="col-md-4">
                    <button type="submit" class="buttonform btn btn-default" name="add">
                      <i class="fa fa-save"></i>&nbsp;
                      <%=addContractualPartner_Add_Contractual%>
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

</body>
</html>
