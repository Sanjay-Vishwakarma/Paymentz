<%@ page import="com.directi.pg.Functions" %>
<%@ include file="top.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.payment.IPEntry" %>
<%--
  Created by IntelliJ IDEA.
  User: Diksha
  Date: 18-May-20
  Time: 11:26 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private static Logger logger=new Logger("partnerIpWhitelist.jsp");
%>
<%
  String company=ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
%>
<html>
<head>
    <title><%=company%> Partner> Partner's Whitelist Details</title>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <style type="text/css">
    #main {
      background-color: #ffffff
    }

    :target:before {
      content: "";
      display: block;
      height: 50px;
      margin: -50px 0 0;
    }

    .table > thead > tr > th {
      font-weight: inherit;
    }

    :target:before {
      content: "";
      display: block;
      height: 90px;
      margin: -50px 0 0;
    }

    footer {
      border-top: none;
      margin-top: 0;
      padding: 0;
    }

    /********************Table Responsive Start**************************/
    @media (max-width: 640px) {
      table {
        border: 0;
      }

      table thead {
        display: none;
      }

      tr:nth-child(odd), tr:nth-child(even) {
        background: #ffffff;
      }

      table td {
        display: block;
        border-bottom: none;
        padding-left: 0;
        padding-right: 0;
      }

      table td:before {
        content: attr(data-label);
        float: left;
        width: 100%;
        font-weight: bold;
      }
    }

    table {
      width: 100%;
      max-width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
      display: table;
      border-collapse: separate;
      border-color: grey;
    }

    thead {
      display: table-header-group;
      vertical-align: middle;
      border-color: inherit;
    }

    tr:nth-child(odd) {
      background: #F9F9F9;
    }

    tr {
      display: table-row;
      vertical-align: inherit;
      border-color: inherit;
    }

    th {
      padding-right: 1em;
      text-align: left;
      font-weight: bold;
    }

    td, th {
      display: table-cell;
      vertical-align: inherit;
    }

    tbody {
      display: table-row-group;
      vertical-align: middle;
      border-color: inherit;
    }

    td {
      padding-top: 6px;
      padding-bottom: 6px;
      padding-left: 10px;
      padding-right: 10px;
      vertical-align: top;
      border-bottom: none;
    }

    .table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th, .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td {
      border-top: 1px solid #ddd;
    }

    /********************Table Responsive Ends**************************/
  </style>

  <link rel="stylesheet" href="/partner/NewCss/css/jquery.dataTables.min.css">
  <script src="/partner/NewCss/js/jquery.dataTables.min.js"></script>

  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <script>
    $(document).ready(function(){

      var w = $(window).width();

      //alert(w);

      if(w > 990){
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");
      }
      else{
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        $(".left ul").removeAttr("style");
      }
    });
  </script>
  <script>
    function ConfirmDelete()
    {
      var x = confirm("Are you sure you want to delete?");
      if (x)
        return true;
      else
        return false;
    }
  </script>

</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  //String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","partnerIpWhitelist");
  logger.error("Inside partnerIpWhitelist----");
  PartnerFunctions partnerFunctions=new PartnerFunctions();
  Functions functions=new Functions();
  IPEntry ipEntry = new IPEntry();
  String partnerId=null;
  String pid=null;
  String username=(String)session.getAttribute("username");
  String role=(String)session.getAttribute("role");
  String actionExecutorId=(String)session.getAttribute("merchantid");
  String actionExecutorName=role+"-"+username;
  String IPv4 = "", IPv6 = "";
  String str="ctoken=" + ctoken;
  String Config="";
  int pageno=partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
  int pagerecords=partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
  partnerId= (String) session.getAttribute("merchantid");
  String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("partnerId")));
    if(Roles.contains("superpartner") || Roles.contains("childsuperpartner"))
    {
      pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
    }
    str=str+"&pid="+pid;

    /*else
    {
      pid = String.valueOf(session.getAttribute("merchantid"));
      Config="disabled";
    }*/
  if (partner.isLoggedInPartner(session))
  {
%>
<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;IP Whitelisting Configuration</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content">
              <div id="horizontal-form">
                <form action="/partner/net/PartnerIpWhitelistConfig?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                  <input type="hidden" value="<%=partnerId%>" name="partnerid" id="partnerid">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <%

                    String errormsg = (String) request.getAttribute("error");
                    if (functions.isValueNull(errormsg))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                    }
                  %>
                  <div class="form-group col-md-8">
                    <label class="col-sm-3 control-label">&nbsp;&nbsp;Partner ID</label>
                    <div class="col-sm-6">
                      <input name="pid" id="PID" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                      <input type="hidden" name="pid" value="<%=pid%>">
                    </div>
                  </div>
                  <div class="form-group col-md-3">
                    <div class="col-sm-offset-2 col-sm-3">
                      <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                      <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                      &nbsp;Search</button>
                    </div>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>WhiteList IP List</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <%
              Hashtable recordHash = (Hashtable)request.getAttribute("recordHash");
              Hashtable temphash=null;
              int records=-1;
              int totalrecords=0;
              String currentblock=request.getParameter("currentblock");
              if(currentblock==null)
                currentblock="1";
              try
              {
                records=Integer.parseInt((String)recordHash.get("records"));
                totalrecords=Integer.parseInt((String)recordHash.get("totalrecords"));
              }
              catch(Exception ex)
              {

              }
              errormsg=(String)request.getAttribute("message");
              String success=(String)request.getAttribute("success");
              if(errormsg!=null)
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
              }
              if(success!=null)
              {
                out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+success+"</h5>");
              }
              String msg = (String) request.getAttribute("msg");
              if(msg!=null)
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
              }
              if(recordHash!=null && recordHash.size()>0)
              {
                recordHash = (Hashtable)request.getAttribute("recordHash");
              }
              String style="class=tr1";
              String ext="light";
              String ipAddress = "";
              if(records > -1)
              {
            %>
            <div class="widget-content padding" style="overflow-y: auto;">
                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="background-color: #7eccad !important;color: white;">
                    <th valign="middle" align="center" style="text-align: center">Partner Start IP</th>
                    <th valign="middle" align="center" style="text-align: center">IP Type</th>
                    <th valign="middle" align="center" style="text-align: center">Action Executor ID</th>
                    <th valign="middle" align="center" style="text-align: center">Action Executor Name</th>
                    <th valign="middle" align="center" colspan="5" style="text-align: center">Operation</th>
                  </tr>
                  </thead>
                  <%
                    StringBuffer requestParameter = new StringBuffer();
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

                    for(int pos=1;pos<=records;pos++)
                    {
                      String id = Integer.toString(pos);
                      if (pos % 2 == 0)
                      {
                        style = "class=tr0";
                        ext = "dark";
                      }
                      else
                      {
                        style = "class=tr1";
                        ext = "light";
                      }
                      if (records > 0)
                      {
                        temphash = (Hashtable) recordHash.get(id);
                        if (functions.isValueNull((String) temphash.get("actionExecutorId")))
                        {
                          actionExecutorId = (String) temphash.get("actionExecutorId");
                        }
                        else
                        {
                          actionExecutorId = "-";
                        }
                        if (functions.isValueNull((String) temphash.get("actionExecutorName")))
                        {
                          actionExecutorName = (String) temphash.get("actionExecutorName");
                        }
                        else
                        {
                          actionExecutorName = "-";
                        }
                        out.println("<tr>");
                        out.println("<td align=center "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("ipAddress"))+"</td>");
                        out.println("<td align=center "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("type"))+"</td>");
                        out.println("<td align=center "+style+">"+ESAPI.encoder().encodeForHTML(actionExecutorId)+"</td>");
                        out.println("<td align=center "+style+">"+ESAPI.encoder().encodeForHTML(actionExecutorName)+"</td>");
                        out.println("<td align=center "+style+"><form action=\"/partner/net/PartnerIpWhitelistConfig?ctoken="+ctoken+"\" method=\"POST\">" +
                                "<input type=\"hidden\" name=\"pid\" value=\""+pid+"\">"+
                                "<input type=\"hidden\" name=\"ipAddress\" value=\""+temphash.get("ipAddress")+"\">"+
                                "<input type=\"hidden\" name=\"action\" value=\"delete\">"+
                                "<input type=\"submit\"  Onclick=\"return ConfirmDelete();\" class=\"btn btn-default gotoauto\" name=\"submit\" value=\"Delete\">");
                        out.println("</form></td>");
                        out.println("</tr>");

                      }
                    }

                    out.println("<tr>");
                    out.println("<td align=center "+style+"><form action=\"/partner/net/PartnerIpWhitelistConfig?ctoken="+ctoken+"\" method=\"POST\">"+
                            "<input type=\"hidden\" name=\"pid\" value=\""+pid+"\">"+
                            "<input type=\"text\"class=\"txtbox \"  size=\"15\" name=\"ipAddress\"></td>");
                    out.println("<td align=center "+style+">&nbsp;<select class=\"txtboxtabel\" name=\"type\"><option value=\"IPv4\" default>IPv4</option><option value=\"IPv6\">IPv6</option> </select></td>");
                    out.println("<td align=center "+style+"></td>");
                    out.println("<td align=center "+style+"></td>");
                    out.println("<td align=center "+style+"><input type=\"hidden\" name=\"action\" value=\"add\"><input type=\"submit\" class=\"btn btn-default gotoauto\" name=\"submit\" value=\"Add\">");
                    out.println("</form></td>");
                    out.println("</tr>");
                  %>
                </table>
            </div>
          </div>
          <div><strong>Showing Page <%=pageno%> of <%=records%> records</strong></div>
          <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="PartnerIpWhitelistConfig"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
          </jsp:include>
          <%
            }
            else
              {
                out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
              }
            }
            else
            {
            out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
            }
          %>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
