<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.*" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 06/01/2020
  Time: 2:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));

  PartnerFunctions partner1=new PartnerFunctions();
%>
<html>
<head>
  <title><%=company%> | Bank Accounts</title>
  <style type="text/css">
    @media(max-width: 767px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }
  </style>
</head>
<META content="text/html; charset=windows-1252" http-equiv=Content-Type>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">

<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  session.setAttribute("submit","gatewayMaster");
  if (partner.isLoggedInPartner(session))
  {

    String str="";

    String accountid = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    String merchantid = Functions.checkStringNull(request.getParameter("merchantid"))==null?"":request.getParameter("merchantid");

    if (accountid != null) str = str + "&accountid=" + accountid;
    if (merchantid != null) str = str + "&merchantid=" + merchantid;

%>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<script type="text/javascript">
  $('#sandbox-container input').datepicker({
  });
</script>
<script>
  $(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
  });
</script>


<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/addGatewayType.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;">Add New Gateway
            </button>
          </form>
        </div>
      </div>
      <br>
      <br>
      <br>

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Gateway Master</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <form  name="forms" method="post" action="/partner/net/GatewayMasterDetails?ctoken=<%=ctoken%>">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                  <%
                    String gatewayName=Functions.checkStringNull(request.getParameter("merchantid"))==null?"":request.getParameter("merchantid");
                    String pgTypeId=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");

                    if(gatewayName!=null)str = str + "&merchantid=" + gatewayName;
                    else
                      gatewayName="";
                    if(gatewayName!=null)str = str + "&accountid=" + gatewayName;
                    else
                      gatewayName="";

                    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

                    str = str + "&SRecords=" + pagerecords;
                  %>
                  <%
                    String msg = (String) request.getAttribute("Error");
                    if(partner1.isValueNull(msg))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                    }

                    String successMsg = (String) request.getAttribute("success");
                    if(partner1.isValueNull(successMsg))
                    {
                      out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + successMsg + "</h5>");
                    }
                  %>
                  <div class="form-group col-md-4 has-feedback">
                    <label>PgType ID</label>
                    <input class="form-control" maxlength="22" type="text" name="pgtypeid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pgTypeId)%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Gateway Name</label>
                    <input class="form-control" maxlength="22" type="text" name="gateway" value="<%=ESAPI.encoder().encodeForHTMLAttribute(gatewayName)%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label style="color: transparent;">Search</label>
                    <button type="submit" class="btn btn-default" style="display: inherit;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;Search</button>
                  </div>

                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">

              <%  StringBuffer requestParameter = new StringBuffer();
                Enumeration<String> stringEnumeration = request.getParameterNames();
                while(stringEnumeration.hasMoreElements())
                {
                  String name=stringEnumeration.nextElement();
                  if("SPageno".equals(name) || "SRecords".equals(name))
                  {

                  }
                  else
                    requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                }
                requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");


                // Hashtable hash = (Hashtable)request.getAttribute("transdetails");
                Hashtable hash = (Hashtable)request.getAttribute("transdetails");

                Hashtable temphash=null;
                int records=0;
                int totalrecords=0;

                String errormsg=(String)request.getAttribute("message");
                String currentblock=request.getParameter("currentblock");
                if(currentblock==null)
                  currentblock="1";
                try
                {
                  records=Integer.parseInt((String)hash.get("records"));
                  totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
                }
                catch(Exception ex)
                {

                }
                if(hash!=null)
                {
                  hash = (Hashtable)request.getAttribute("transdetails");
                }
                if(records>0)
                {
              %>
              <table class="table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Sr No</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>PaymentGateway Id</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Gateway Name</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Currency</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Name	</b></td>
                </tr>
                </thead>
                <%
                  String style="class=td1";
                  String ext="light";

                  for(int pos=1;pos<=records;pos++)
                  {
                    String id = Integer.toString(pos);
                    style = "class=\"tr" + (pos + 1) % 2 + "\"";
                    int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
                    temphash=(Hashtable)hash.get(id);
                    out.println("<tr id=\"maindata\">");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Sr No\" align=\"center\">&nbsp;"+srno+ "</td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"PaymentGateway Id\" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("pgtypeid"))+"<input type=\"hidden\" name=\"pgtypeid\" value=\""+temphash.get("pgtypeid")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Gateway Name\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("gateway"))+"<input type=\"hidden\" name=\"gateway\" value=\""+temphash.get("gateway")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Currency\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("currency"))+"<input type=\"hidden\" name=\"currency\" value=\""+temphash.get("currency")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Name\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("name"))+"<input type=\"hidden\" name=\"name\" value=\""+temphash.get("name")+"\"></td>");
                    out.println("</tr>");
                  }
                %>
              </table>
              <div id="showingid"><strong>Showing Page <%=pageno%> of <%=totalrecords%> records</strong></div>
              <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="GatewayMasterDetails"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
              </jsp:include>
              <%
                }
                else
                {
                  out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                }
              %>
              <%
                }
                else
                {
                  response.sendRedirect("/partner/logout.jsp");
                  return;
                }
              %>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>