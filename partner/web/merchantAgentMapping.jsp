<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ include file="top.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Wallet
  Date: 01/03/2021
  Time: 16:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String partnerid=String.valueOf(session.getAttribute("partnerId"));
  session.setAttribute("submit", "merchantagentMapping");
%>

<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <%
    String company= (String) session.getAttribute("partnername");
  %>
  <title><%=company%> Agent Management> Merchant Agent Mapping</title>

  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <script type="text/javascript">

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
  <style>
    #ui-id-2
    {
      overflow: auto;
      max-height: 350px;
    }
  </style>
</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String str="";
    String agentId= Functions.checkStringNull(request.getParameter("agentid"))==null?"":request.getParameter("agentid");
    String memberId=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");

    String pid=nulltoStr(request.getParameter("pid"));
    String Config=null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
      pid = String.valueOf(session.getAttribute("merchantid"));
      Config = "disabled";
    }
    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if(agentId!=null)str=str +"&agentname=" + agentId;
    else
      agentId="";
    if(memberId!=null)str=str +"&memberid=" + memberId;
    else
      memberId="";
    if (pid != null)str= str + "&pid=" + pid;
    else
      pid="";
    if (partnerid != null)str= str + "&partnerid=" + partnerid;
    else
      partnerid="";
    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);

    str = str + "&SRecords=" + pagerecords;
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/merchantMapping.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" name="submit" style="background-color: #98A3A3; border-radius: 25px;color: white;font-size: 14px;cursor: pointer;"> Add New Mapping
            </button>
          </form>
        </div>
      </div>
      <br><br><br>

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Agent Mapping</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">

                <form action="/partner/net/MerchantAgentMapping?ctoken=<%=ctoken%>" method="post" name="forms" >
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                  <%
                    if(request.getAttribute("error")!=null)
                    {
                      String message = (String) request.getAttribute("error");
                      if(message != null)
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                    }

                    if (request.getAttribute("success")!= null)
                    {
                      String successmsg= (String)request.getAttribute("success");
                      if (successmsg != null)
                        out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-check-circle\" style=\" font-size:initial !important;\" ></i>&nbsp;&nbsp;" + successmsg + "</h5>");
                    }
                  %>
                  <div class="ui-widget form-group col-md-4 has-feedback">
                    <label for="pid">Partner Id</label>
                    <input type="text" name="pid" id="pid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>" class="form-control" autocomplete="on" <%=Config%>>
                    <input type="hidden" name="pid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>" >
                  </div>

                  <div class="ui-widget form-group col-md-4 has-feedback">
                    <label >Agent Id</label>
                    <input type="text" name="agentid" id="agntpartner" value="<%=agentId%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Member Id</label>
                    <input  type="text" name="memberid" id="member" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>" autocomplete="on">
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

                Hashtable hash = (Hashtable)request.getAttribute("transdetails");

                Hashtable temphash=null;
                int records=0;
                int totalrecords=0;

                String errormsg=(String) request.getAttribute("errormessage");
                if (errormsg != null)
                {
                  out.println("<center><div class=\"bg-info\">" + errormsg + "</div></center>");
                }
                str = str + "&agentid=" + agentId;
                str = str + "&memberid=" + memberId;

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
                  hash = (Hashtable) request.getAttribute("transdetails");
                }
                if (request.getAttribute("msg") != null)
                {
                  String msg = (String) request.getAttribute("msg");
                  if (msg != null)
                  {
                    out.println(Functions.NewShowConfirmation1("Sorry", msg));
                  }
                }
                if(records>0)
                {
              %>
              <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Sr No</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Partner ID</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Merchant Id</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Company Name</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Agent ID</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Agent Name</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Creation On</b></td>
                </tr>
                </thead>
                <%
                  Functions functions = new Functions();
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

                    out.println("<tr id=\"maindata\">");
                    out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Partner ID\" align=\"center\" "+style+">&nbsp;"+ ESAPI.encoder().encodeForHTML((String) temphash.get("partnerId"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Member ID\" align=\"center\" "+style+">&nbsp;"+ ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Company Name\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("merchantname"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Agent Id\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("agentid"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Agent Name\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("agentname"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Creation On\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("mappingon"))+ "</td>");
                    out.println(requestParameter.toString());
                    out.println("</form></td>");
                    out.println("</tr>");
                  }
                %>
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
        <jsp:param name="page" value="MerchantAgentMapping"/>
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
    </div>
  </div>
</div>

<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
<%!
  public static String nulltoStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>
