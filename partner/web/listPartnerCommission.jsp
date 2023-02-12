<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.TreeMap" %>
<%--
  Created by IntelliJ IDEA.
  User: Ajit.k
  Date: 18/09/2019
  Time: 1:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
  session.setAttribute("submit","PartnerReports");
%>
<%@ include file="top.jsp"%>
<html>
<head>
  <%--
    <script src="/partner/NewCss/js/jquery-1.12.4.min.js"></script>
  --%>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>

  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%> | Partner Master</title>
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }
  </style>
  <script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
  </script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });
  </script>
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
</head>
<body>

<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String str = "";
    String partid = (String) session.getAttribute("merchantid");
    PartnerFunctions partnerFunctions=new PartnerFunctions();
    String partnerId=Functions.checkStringNull(request.getParameter("partnerId"))==null?"":request.getParameter("partnerId");;
    String memberid=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
    TreeMap<String, String> subPartnersDetails = partnerFunctions.getPartnerDetailsForMap(partid);

    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if(partnerId!=null)str = str + "&partnerId=" + partnerId;
    else
      partnerId="";
    if(memberid!=null)str = str + "&memberid=" + memberid;
    else
      memberid="";

    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String listPartnerCommission_Add_Partner_Commission = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Add_Partner_Commission")) ? rb1.getString("listPartnerCommission_Add_Partner_Commission") : "Add Partner Commission";
    String listPartnerCommission_Partner_Commission = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Partner_Commission")) ? rb1.getString("listPartnerCommission_Partner_Commission") : "Partner Commission";
    String listPartnerCommission_PartnerID = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_PartnerID")) ? rb1.getString("listPartnerCommission_PartnerID") : "Partner ID";
    String listPartnerCommission_MerchantID = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_MerchantID")) ? rb1.getString("listPartnerCommission_MerchantID") : "Merchant ID";
    String listPartnerCommission_Path = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Path")) ? rb1.getString("listPartnerCommission_Path") : "Path";
    String listPartnerCommission_Search = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Search")) ? rb1.getString("listPartnerCommission_Search") : "Search";
    String listPartnerCommission_Report_Table = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Report_Table")) ? rb1.getString("listPartnerCommission_Report_Table") : "Report Table";
    String listPartnerCommission_SrNo = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_SrNo")) ? rb1.getString("listPartnerCommission_SrNo") : "Sr No";
    String listPartnerCommission_MemberID = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_MemberID")) ? rb1.getString("listPartnerCommission_MemberID") : "Member ID";
    String listPartnerCommission_TerminalID = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_TerminalID")) ? rb1.getString("listPartnerCommission_TerminalID") : "Terminal ID";
    String listPartnerCommission_Commission_ON = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Commission_ON")) ? rb1.getString("listPartnerCommission_Commission_ON") : "Commission ON";
    String listPartnerCommission_Commission_Value = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Commission_Value")) ? rb1.getString("listPartnerCommission_Commission_Value") : "Commission Value";
    String listPartnerCommission_Start_Date = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Start_Date")) ? rb1.getString("listPartnerCommission_Start_Date") : "Start Date";
    String listPartnerCommission_End_Date = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_End_Date")) ? rb1.getString("listPartnerCommission_End_Date") : "End Date";
    String listPartnerCommission_Sequence_NO = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Sequence_NO")) ? rb1.getString("listPartnerCommission_Sequence_NO") : "Sequence NO";
    String listPartnerCommission_Action = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Action")) ? rb1.getString("listPartnerCommission_Action") : "Action";
    String listPartnerCommission_Showing_Page = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Showing_Page")) ? rb1.getString("listPartnerCommission_Showing_Page") : "Showing Page";
    String listPartnerCommission_of = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_of")) ? rb1.getString("listPartnerCommission_of") : "of";
    String listPartnerCommission_records = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_records")) ? rb1.getString("listPartnerCommission_records") : "records";
    String listPartnerCommission_Sorry = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_Sorry")) ? rb1.getString("listPartnerCommission_Sorry") : "Sorry";
    String listPartnerCommission_no = StringUtils.isNotEmpty(rb1.getString("listPartnerCommission_no")) ? rb1.getString("listPartnerCommission_no") : "No records found.";

%>
<div class="content-page">
  <div class="content">
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/managePartnerCommission.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;"><%=listPartnerCommission_Add_Partner_Commission%>
            </button>
          </form>
        </div>
      </div>

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 49px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>

      <br><br><br>

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=listPartnerCommission_Partner_Commission%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/ListPartnerCommission?ctoken=<%=ctoken%>" method="post" name="forms">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partid%>" name="superAdminId" id="partnerid">
                  <%
                    if(request.getAttribute("error")!=null)
                    {
                      String message = (String) request.getAttribute("error");
                      if(message != null)
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                    }
                    if(request.getAttribute("success")!=null)
                    {
                      String success = (String) request.getAttribute("success");
                      if(success != null)
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + success + "</h5>");
                    }
                  %>

                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                  <div class="ui-widget form-group col-md-4 has-feedback">
                    <label><%=listPartnerCommission_PartnerID%></label>
                    <input name="partnerId" id="PID" value="<%=partnerId%>" class="form-control" autocomplete="on" onkeyup="document.getElementById('pid').value = this.value">
                    <input type="hidden" id="pid" value="">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=listPartnerCommission_MerchantID%></label>
                    <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4">
                    <label style="color: transparent;"><%=listPartnerCommission_Path%></label>
                    <button type="submit" class="btn btn-default" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=listPartnerCommission_Search%>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=listPartnerCommission_Report_Table%></strong></h2>
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
              <table align=center width="100%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_SrNo%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_PartnerID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_MemberID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_TerminalID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_Commission_ON%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_Commission_Value%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_Start_Date%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_End_Date%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_Sequence_NO%></b></td>
                  <td  colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listPartnerCommission_Action%></b></td>

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

                    //System.out.println("partnerid::::::"+temphash.get("partnerid"));

                    out.println("<tr id=\"maindata\">");
                    out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("terminalid"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("chargename"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("commission_value"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("startdate"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("enddate"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("sequence_no"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/net/ActionPartnerCommission?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"commissionid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("id"))+"\"><input type=\"submit\" name=\"chk\" value=\"Edit\" class=\"gotoauto btn btn-default\"><input type=\"hidden\" name=\"action\" value=\"modify\">");
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
      <div id="showingid"><strong><%=listPartnerCommission_Showing_Page%> <%=pageno%> <%=listPartnerCommission_of%> <%=totalrecords%> <%=listPartnerCommission_records%></strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="ListPartnerCommission"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(listPartnerCommission_Sorry,listPartnerCommission_no ));
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