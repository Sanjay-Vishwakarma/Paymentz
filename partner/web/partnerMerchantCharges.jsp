<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","partnerMerchantCharges");
  PartnerFunctions partner1=new PartnerFunctions();
  Functions functions = new Functions();
  String partnerid= String.valueOf(session.getAttribute("partnerId"));

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String partnerMerchantCharges_Add_New_Charge_Mapping = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Add_New_Charge_Mapping")) ? rb1.getString("partnerMerchantCharges_Add_New_Charge_Mapping") : "Add New Charge Mapping";
  String partnerMerchantCharges_Merchant_Charges = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Merchant_Charges")) ? rb1.getString("partnerMerchantCharges_Merchant_Charges") : "Merchant Charges";
  String partnerMerchantCharges_Partner_ID = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Partner_ID")) ? rb1.getString("partnerMerchantCharges_Partner_ID") : "Partner ID";
  String partnerMerchantCharges_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Merchant_ID")) ? rb1.getString("partnerMerchantCharges_Merchant_ID") : "Merchant ID";
  String partnerMerchantCharges_Terminal_ID = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Terminal_ID")) ? rb1.getString("partnerMerchantCharges_Terminal_ID") : "Terminal ID";
  String partnerMerchantCharges_Path = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Path")) ? rb1.getString("partnerMerchantCharges_Path") : "Path";
  String partnerMerchantCharges_Search = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Search")) ? rb1.getString("partnerMerchantCharges_Search") : "Search";
  String partnerMerchantCharges_Report_Table = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Report_Table")) ? rb1.getString("partnerMerchantCharges_Report_Table") : "Report Table";
  String partnerMerchantCharges_Sr_No = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Sr_No")) ? rb1.getString("partnerMerchantCharges_Sr_No") : "Sr No";
  String partnerMerchantCharges_Partner_ID1 = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Partner_ID1")) ? rb1.getString("partnerMerchantCharges_Partner_ID1") : "Partner ID";
  String partnerMerchantCharges_Merchant_ID1 = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Merchant_ID1")) ? rb1.getString("partnerMerchantCharges_Merchant_ID1") : "Merchant ID";
  String partnerMerchantCharges_Terminal_ID1 = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Terminal_ID1")) ? rb1.getString("partnerMerchantCharges_Terminal_ID1") : "Terminal ID";
  String partnerMerchantCharges_Charge_Name = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Charge_Name")) ? rb1.getString("partnerMerchantCharges_Charge_Name") : "Charge Name";
  String partnerMerchantCharges_Merchant_Rate = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Merchant_Rate")) ? rb1.getString("partnerMerchantCharges_Merchant_Rate") : "Merchant Rate";
  String partnerMerchantCharges_Agent_Commission = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Agent_Commission")) ? rb1.getString("partnerMerchantCharges_Agent_Commission") : "Agent Commission";
  String partnerMerchantCharges_Partner_Commission = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Partner_Commission")) ? rb1.getString("partnerMerchantCharges_Partner_Commission") : "Partner Commission";
  String partnerMerchantCharges_Seq_No = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Seq_No")) ? rb1.getString("partnerMerchantCharges_Seq_No") : "Seq No";
  String partnerMerchantCharges_Action = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Action")) ? rb1.getString("partnerMerchantCharges_Action") : "Action";
  String partnerMerchantCharges_Showing_Page = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Showing_Page")) ? rb1.getString("partnerMerchantCharges_Showing_Page") : "Showing Page";
  String partnerMerchantCharges_of = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_of")) ? rb1.getString("partnerMerchantCharges_of") : "of";
  String partnerMerchantCharges_records = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_records")) ? rb1.getString("partnerMerchantCharges_records") : "records";
  String partnerMerchantCharges_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Sorry")) ? rb1.getString("partnerMerchantCharges_Sorry") : "Sorry";
  String partnerMerchantCharges_No_Charges_Found = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_No_Charges_Found")) ? rb1.getString("partnerMerchantCharges_No_Charges_Found") : "No Charges Found.";
  String partnerMerchantCharges_Modify = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_Modify")) ? rb1.getString("partnerMerchantCharges_Modify") : "Modify";
  String partnerMerchantCharges_History = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_History")) ? rb1.getString("partnerMerchantCharges_History") : "History";
  String partnerMerchantCharges_page_no = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_page_no")) ? rb1.getString("partnerMerchantCharges_page_no") : "Page number";
  String partnerMerchantCharges_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("partnerMerchantCharges_total_no_of_records")) ? rb1.getString("partnerMerchantCharges_total_no_of_records") : "Total number of records";




%>
<%!
  private static Logger log=new Logger("partnerMerchantCharges.jsp");
%>
<style type="text/css">
  #ui-id-2
  {
    overflow: auto;
    max-height: 350px;
  }
</style>
<html>
<head>
</head>
<title><%=company%> Merchant Management> Merchant Charges</title>
<%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>&ndash;%&gt;
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
<script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

<html lang="en">
<head>
  <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/html5.js'></script>
  <title><%=company%> Merchant Delete</title>
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
  <script language="javascript">
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("id");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }
  </script>
    <script language="javascript">
    function Delete()
    {
      var checkboxes = document.getElementsByName("id");
      var checked=[];
      var total_boxes = checkboxes.length;
      flag = false;

      for(i=0; i<total_boxes; i++ )
      {
        if(checkboxes[i].checked)
        {
          flag= true;
          checked.push(checkboxes[i].value);
          checked.join(',');
        }
      }
      document.getElementById("ids").value=checked;
      if(!flag)
      {
        alert("Select at least one record");
        return false;
      }
      if (confirm("Do you really want to Delete all selected Data."))
      {
        document.delete.submit();
      }
    }
  </script></head>

<body>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/manageMemberAccountCharges.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="btn btn-default" id="submit" style="display: -webkit-box;"><i
                    class="fa fa-plus"></i>&nbsp;&nbsp;<%=partnerMerchantCharges_Add_New_Charge_Mapping%>
            </button>
          </form>
        </div>
      </div>
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=partnerMerchantCharges_Merchant_Charges%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <%
                  String errormsg2 = (String) request.getAttribute("error");
                  String msg = (String) request.getAttribute("message");
                  String success = (String) request.getAttribute("success");
                  String success1 = (String)request.getAttribute("success1");

                  if(partner1.isValueNull(msg))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                  }
                  if(partner1.isValueNull(success))
                  {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"></i>&nbsp;&nbsp;" + success + "</h5>");
                  }
                  if (functions.isValueNull(errormsg2))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg2 + "</h5>");
                  }
                  if (functions.isValueNull(success1))
                  {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\">&nbsp;&nbsp;" + success1 + "</h5>");
                  }
                %>
                <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                  if (partner.isLoggedInPartner(session))
                  {
                    String memberid=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
                    String pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
                    String Config="";
                    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                    if(Roles.contains("superpartner")){

                    }else{
                      pid = String.valueOf(session.getAttribute("merchantid"));
                      Config = "disabled";
                    }
                    String terminalid = nullToStr(request.getParameter("terminalid"));
                    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);

                    String str="ctoken="+ctoken;

                    if (memberid != null) str = str + "&memberid=" + memberid;
                    if (pid != null) str = str + "&pid=" + pid;
                    if (terminalid != null) str = str + "&terminalid=" + terminalid;
                    if (partnerid != null) str = str + "&partnerId=" + partnerid;
                %>
                <form action="/partner/net/PartnerMerchantCharges?ctoken=<%=ctoken%>" method="post">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partnerid%>" name="partnerId" id="partnerid">
                  <div class="widget-content padding">
                    <div id="horizontal-form1">
                      <div class="form-group col-md-4 has-feedback">
                        <div class="ui-widget">
                          <label for="pid"><%=partnerMerchantCharges_Partner_ID%></label>
                          <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                          <input type="hidden" name="pid"  value="<%=pid%>" >
                        </div>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <div class="ui-widget">
                          <label for="member"><%=partnerMerchantCharges_Merchant_ID%></label>
                          <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                        </div>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <div class="ui-widget">
                          <label for="terminalALL"><%=partnerMerchantCharges_Terminal_ID%></label>
                          <input name="terminalid" id="terminalALL" value="<%=terminalid%>" class="form-control" autocomplete="on">
                        </div>
                      </div>
                      <div class="form-group col-md-4">
                        <label style="color: transparent;"><%=partnerMerchantCharges_Path%></label>
                        <button type="submit" class="btn btn-default" style="display:block;">
                          <i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;<%=partnerMerchantCharges_Search%>
                        </button>
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
            <div class="row reporttable">
              <div class="col-md-12">
                <div class="widget">
                  <div class="widget-header">
                    <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerMerchantCharges_Report_Table%></strong></h2>
                    <div class="additional-btn">
                      <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                      <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                      <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                    </div>
                  </div>
                  <div class="widget-content padding" style="overflow-x: auto;">
                    <%
                      HashMap hash = (HashMap)request.getAttribute("transdetails");
                      HashMap temphash = null;

                      String error = (String) request.getAttribute("errormessage");
                      if (error != null)
                      {
                        out.println(error);
                      }
                      if (hash != null && hash.size() > 0)
                      {

                        int records = 0;
                        int totalrecords = 0;
                        String currentblock = request.getParameter("currentblock");

                        if (currentblock == null)
                          currentblock = "1";

                        try
                        {
                          records=Functions.convertStringtoInt((String)hash.get("records"),15);
                          totalrecords=Functions.convertStringtoInt((String)hash.get("totalrecords"),0);
                        }
                        catch (NumberFormatException ex)
                        {
                          log.error("Records & TotalRecords is found null",ex);
                        }
                        if (hash != null)
                        {
                          hash = (HashMap) request.getAttribute("transdetails");
                        }
                        if (records > 0)
                        {
                    %>
                    <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                      <thead>
                      <tr>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantCharges_Sr_No%></b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantCharges_Partner_ID1%></b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantCharges_Merchant_ID1%></b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantCharges_Terminal_ID1%></b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantCharges_Charge_Name%></b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantCharges_Merchant_Rate%></b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantCharges_Agent_Commission%></b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantCharges_Partner_Commission%></b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantCharges_Seq_No%></b></td>
                        <td colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                          <b><%=partnerMerchantCharges_Action%></b></td>
                      </tr>
                      </thead>
                      <%
                        String style = "class=td1";
                        String ext = "light";
                        for (int pos = 1; pos <= records; pos++)
                        {
                          String id = Integer.toString(pos);
                          int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                          if (pos % 2 == 0)
                          {
                            style="class=tr0";
                            ext="dark";
                          }
                          else
                          {
                            style="class=tr1";
                            ext="light";
                          }

                          temphash = (HashMap) hash.get(id);
                          out.println("<tr>");

                          out.println("<td valign=\"middle\" data-label=\"id\" align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value="+ESAPI.encoder().encodeForHTML((String)temphash.get("mappingid"))+"></td>");
                          out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\" " + style + ">&nbsp;" + srno + "</td>");
                          String memberId = (String) temphash.get("memberid");
                          String partnerId = (String) temphash.get("partnerid");
                          String terminalId = (String) temphash.get("terminalid");
                          out.println("<td valign=\"middle\" data-label=\"Partner ID\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(partnerId) + "</td>");
                          out.println("<td valign=\"middle\" data-label=\"Merchant ID\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(memberId) + "</td>");
                         String accountId = (String) temphash.get("accountid");
                          terminalId = (String) temphash.get("terminalid");
                          out.println("<td valign=\"middle\" data-label=\"Terminal ID\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(terminalId) + "</td>");
                          out.println("<td valign=\"middle\" data-label=\"Charge Name\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("chargename")) + "</td>");
                          out.println("<td valign=\"middle\" data-label=\"Merchant Charge Value\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("chargevalue")) + "</td>");
                          out.println("<td valign=\"middle\" data-label=\"Agent Charge Value\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("agentchargevalue")) + "</td>");
                          out.println("<td valign=\"middle\" data-label=\"Partner Charge Value\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("partnerchargevalue")) + "</td>");
                          out.println("<td valign=\"middle\" data-label=\"Seq No\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("sequencenum")) + "</td>");
                          out.println("<td valign=\"center\" " + style + "><form action=\"/partner/net/ActionPartnerAccountCharges?ctoken=" + ctoken + "\" method=\"POST\"><input type=\"hidden\" name=\"mappingid\" value=\"" + temphash.get("mappingid") + "\"><input type=\"hidden\" name=\"partnerId\" value=\""+request.getParameter("partnerId")+"\"><input type=\"hidden\" name=\"memberid\" value=\""+memberId+"\"><input type=\"hidden\" name=\"terminalid\" value=\""+terminalId+"\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"button btn btn-default\" value="+partnerMerchantCharges_Modify+"></form></td>");
                          out.println("<td valign=\"center\" " + style + "><form action=\"/partner/net/ActionPartnerAccountCharges?ctoken=" + ctoken + "\" method=\"POST\"><input type=\"hidden\" name=\"mappingid\" value=\"" + temphash.get("mappingid") + "\"><input type=\"hidden\" name=\"partnerId\" value=\""+request.getParameter("partnerId")+"\"><input type=\"hidden\" name=\"memberid\" value=\""+memberId+"\"><input type=\"hidden\" name=\"terminalid\" value=\""+terminalId+"\"><input type=\"hidden\" name=\"action\" value=\"history\"><input type=\"submit\" class=\"button btn btn-default\" value="+partnerMerchantCharges_History+"></form></td>");
                          out.println("</tr>");
                        }
                      %>
                    </table>
                    <form name="delete" action="/partner/net/PartnerMerchantCharges?ctoken=<%=ctoken%>" method="post">
                      <table width="100%">
                        <thead>
                        <tr>
                          <td width="15%" align="center">
                            <input type="hidden" name="delete" value="delete">
                            <input id="ids" type="hidden" name="ids" value="">
                            <input type="button" name="delete" class="btn btn-default gotoauto" value="Delete" onclick="return Delete();">
                          </td>
                        </tr>
                        </thead>
                      </table>
                    </form>
                    <% int TotalPageNo;
                      if(totalrecords%pagerecords!=0)
                      {
                        TotalPageNo=totalrecords/pagerecords+1;
                      }
                      else
                      {
                        TotalPageNo=totalrecords/pagerecords;
                      }
                    %>
                    <div id="showingid"><strong><%=partnerMerchantCharges_page_no%> <%=pageno%> <%=partnerMerchantCharges_of%> <%=TotalPageNo%></strong></div>  &nbsp;&nbsp;
                    <div id="showingid"><strong><%=partnerMerchantCharges_total_no_of_records%>   <%=totalrecords%> </strong></div>

                    <jsp:include page="page.jsp" flush="true">
                      <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                      <jsp:param name="numrows" value="<%=pagerecords%>"/>
                      <jsp:param name="pageno" value="<%=pageno%>"/>
                      <jsp:param name="str" value="<%=str%>"/>
                      <jsp:param name="page" value="PartnerMerchantCharges"/>
                      <jsp:param name="currentblock" value="<%=currentblock%>"/>
                      <jsp:param name="orderby" value=""/>
                    </jsp:include>
                    <%
                          }
                          else
                          {
                            out.println(Functions.NewShowConfirmation1(partnerMerchantCharges_Sorry, partnerMerchantCharges_No_Charges_Found));
                          }
                        }
                        else
                        {
                          out.println(Functions.NewShowConfirmation1(partnerMerchantCharges_Sorry, partnerMerchantCharges_No_Charges_Found));
                        }
                      }
                    %>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
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