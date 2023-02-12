<%@ page import="org.owasp.esapi.ESAPI,
                 java.util.Hashtable,
                 com.directi.pg.Functions"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Kanchan
  Date: 04-02-2021
  Time: 12:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String partnerid= (String.valueOf(session.getAttribute("partnerId")));
    session.setAttribute("submit","agentInterface");
%>
<html>
<head>
    <title></title>
</head>
<body>

<%
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
          pid= String.valueOf(session.getAttribute("merchantid"));
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
                      String str="ctoken=" + ctoken;
                      String agentid=(String)request.getParameter("agentid");
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

           <%

               String errormsg=(String) request.getAttribute("error");
               if (errormsg != null)
               {
                    out.println("<center><font class=\"textb\">"+errormsg+"</font></center>");
               }
               else
               {
                    errormsg= "";
               }
               Hashtable status_data=(Hashtable) request.getAttribute("status_report");
               String style="class=\"tr0\"";
               int records = 0;
               int totalrecords = 0;

               Hashtable temphash=new Hashtable();
               try
                {
                    records = Integer.parseInt((String) status_data.get("records"));
                    totalrecords = Integer.parseInt((String) status_data.get("totalrecords"));
                }
               catch (Exception ex)
                {
                }
               if (records > 0 && status_data!=null)
               {
       %>

       <table align=center width="50%">
       <tr>
         <td>
           <div align="center" class="widget-header transparent"><h3><b>Merchant Transaction Report</b></h3></div> </td>

       </tr>
       <tr>
         <td width="50%"><div align="center">

           <table align=center width="30%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
             <thead>
             <tr>
               <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Status</b></td>
               <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction</b></td>
               <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Total Amount</b></td>
             </tr>
             </thead>

               <%
                 for (int pos = 1; pos < records; pos++)
                    {
                        String id = Integer.toString(pos);

                        temphash = (Hashtable) status_data.get(id);

                        String status=(String)temphash.get("status");
                        String count=(String)temphash.get("count");
                        String amount=(String)temphash.get("amount");
               %>
             <tr>
               <td class="tr0" align="center"><%=status%> </td>
               <td class="tr1" align="center"><%=count%> </td>
               <td class="tr0" align="center"><%=amount%> </td>
             </tr>

       <%
                }
               out.println("<tr>");
               out.println("<td  class=\"textb\" colspan=\"3\" align=\"center\">TOTAL Transaction: <font color=\"red\">"+totalrecords+"</font> </td>");
               out.println("</tr>");
               out.println("<tr>");
               out.println("<td  class=\"textb\" colspan=\"3\" align=\"center\">GRAND Total Amount: <font color=\"red\">"+status_data.get("grandtotal")+"</font> </td>");
               out.println("</tr>");

       %>
                    </table>
                  </div>
                </table>
               <br>
             </div>
           </div>
         </div>
       </div>
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