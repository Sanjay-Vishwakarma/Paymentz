<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp"%>
<%@ include file="top.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Kanchan
  Date: 03-02-2021
  Time: 14:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String partnerid=(String.valueOf(session.getAttribute("partnerId")));
    session.setAttribute("submit","agentInterface");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

    <title></title>
</head>
<body>

<%
  ctoken= ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
      String Config=null;
      String pid= nullToStr(request.getParameter("pid"));
      String Roles= partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
      if (Roles.contains("superpartner"))
      {

      }
      else
      {
          pid= (String.valueOf(session.getAttribute("merchantid")));
          Config="disabled";
      }
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/agentsignup.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" name="submit" style="background-color: #98A3A3; border-radius: 25px;color: white;font-size: 14px;cursor: pointer;"> Add New Agent
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
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Agent Master</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">

                <form action="/partner/net/ListAgentDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                    <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                    <%
                        String agentid=(String)request.getParameter("agentid");
                        String str="ctoken=" + ctoken;
                        if (agentid != null) str = str + "&agentid=" + agentid;

                        if (pid != null)str= str + "&pid=" + pid;
                        else
                            pid="";

                        if (partnerid != null)str= str + "&partnerid=" + partnerid;
                        else
                            partnerid="";

                        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                   %>
                    <div class="ui-widget form-group col-md-4 has-feedback">
                        <label for="pid">Partner Id</label>
                        <input type="text" name="pid" id="pid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>" class="form-control" <%=Config%>>
                        <input type="hidden" name="pid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>" >
                    </div>

                  <div class="ui-widget form-group col-md-4 has-feedback">
                    <label for="agnt">Agent Id</label>
                    <input type="text" name="agentid" id="agnt" value="" class="form-control">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Agent Name</label>
                    <input  type="text" name="agentname" class="form-control" value="">
                  </div>

                  <div class="form-group col-md-4">
                    <label style="color: transparent;">Path</label>
                    <button type="submit" class="btn btn-default" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
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
            <div class="widget-content padding" style="overflow-y: auto;">


            <%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

                 Hashtable temphash=null;
                 int records=0;
                 int totalrecords=0;


                 String errormsg=(String)request.getAttribute("message");
                 if(errormsg!=null)
                 {
                     out.println("<center><font class=\"textb\">"+errormsg+"</font></center>");
                 }

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
            <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
              <thead>
              <tr>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Sr No</b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Merchant Id</b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Merchant Name</b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Company Name</b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Company Email</b></td>
              </tr>
              </thead>

                <%
                   String style="class=td1";
                   String ext="light";

                  for(int pos=1;pos<=records;pos++)
                   {
                      String id=Integer.toString(pos);

                      int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

                      if(pos%2==0)
                      {
                        style="class=tr0";
                        ext="dark";
                      }
                     else
                      {
                          style="class=tr1";
                          ext="light";
                      }

                     temphash=(Hashtable)hash.get(id);

                       out.println("<tr>");
                       out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
                       out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"<input type=\"hidden\" name=\"memberid\" value=\""+temphash.get("memberid")+"\"></td>");
                       out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"<input type=\"hidden\" name=\"login\" value=\""+temphash.get("login")+"\"></td>");
                       out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("company_name"))+"<input type=\"hidden\" name=\"company_name\" value=\""+temphash.get("company_name")+"\"></td>");
                       out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"contact_emails\" value=\""+temphash.get("contact_emails")+"\"></td>");
                       out.println("</tr>");
                    }
    %>

              <thead>
                <tr>
                  <td  align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Total Records </b> <%=totalrecords%></td>
                  <td  align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Page No </b><%=pageno%></td>
                  <td   style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"></td>
                  <td   style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"></td>
                  <td   style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"></td>
                </tr>
              </thead>
          </table>
            </div>
          </div>
        </div>
      </div>
      <div id="showingid"><strong>Showing Page <%=pageno%> of <%=totalrecords%> records</strong></div>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="AgentMerchantDetails"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>


        <%
            }
              else
              {
                 out.println(Functions.NewShowConfirmation1("Sorry","No records found."));
              }
          }
              else

              {
                 response.sendRedirect("/partner/logout.jsp");
                 return;
             }
%>
</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>
