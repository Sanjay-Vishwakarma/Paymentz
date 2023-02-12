<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 9/9/15
  Time: 3:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  Logger logger = new Logger("partnerMerchantFraudAccount.jsp");
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);

  String partnerMerchantFraudAccount_Partner_Merchant_Fraud_Accounts = rb1.getString("partnerMerchantFraudAccount_Partner_Merchant_Fraud_Accounts");
  String partnerMerchantFraudAccount_PartnerId = rb1.getString("partnerMerchantFraudAccount_PartnerId");
  String partnerMerchantFraudAccount_MerchantId = rb1.getString("partnerMerchantFraudAccount_MerchantId");
  String partnerMerchantFraudAccount_Search = rb1.getString("partnerMerchantFraudAccount_Search");
  String partnerMerchantFraudAccount_ReportTable = rb1.getString("partnerMerchantFraudAccount_ReportTable");
  String partnerMerchantFraudAccount_Srno = rb1.getString("partnerMerchantFraudAccount_Srno");
  String partnerMerchantFraudAccount_MerchantID = rb1.getString("partnerMerchantFraudAccount_MerchantID");
  String partnerMerchantFraudAccount_Fraud_Account = rb1.getString("partnerMerchantFraudAccount_Fraud_Account");
  String partnerMerchantFraudAccount_Fraud = rb1.getString("partnerMerchantFraudAccount_Fraud");
  String partnerMerchantFraudAccount_IsActive = rb1.getString("partnerMerchantFraudAccount_IsActive");
  String partnerMerchantFraudAccount_IsVisible = rb1.getString("partnerMerchantFraudAccount_IsVisible");
  String partnerMerchantFraudAccount_Action = rb1.getString("partnerMerchantFraudAccount_Action");
  String partnerMerchantFraudAccount_Showing_Page = rb1.getString("partnerMerchantFraudAccount_Showing_Page");
  String partnerMerchantFraudAccount_of = rb1.getString("partnerMerchantFraudAccount_of");
  String partnerMerchantFraudAccount_records = rb1.getString("partnerMerchantFraudAccount_records");
  String partnerMerchantFraudAccount_page_no = rb1.getString("partnerMerchantFraudAccount_page_no");
  String partnerMerchantFraudAccount_total_no_of_records = rb1.getString("partnerMerchantFraudAccount_total_no_of_records");
%>
<html>
<head>
  <title><%=company%> Fraud Management> Merchant Fraud Accounts </title>
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }
    #ui-id-2
    {
      overflow: auto;
      max-height: 350px;
    }
  </style>
 <%-- <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  session.setAttribute("submit","Merchant Fraud Accounts");
  if (partner.isLoggedInPartner(session))
  {

    String partnerId=(String)session.getAttribute("merchantid");
    String memberId=nullToStr(request.getParameter("memberid"));

    String str="ctoken=" + ctoken;

    PartnerFunctions partner1=new PartnerFunctions();
    int pageno=partner1.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=partner1.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
    String pid=nullToStr(request.getParameter("pid"));

    if(memberId!=null){str=str+"&memberid="+memberId;}
    if(partnerId!=null){str=str+"&partnerid="+partnerId;}
    if(pid!=null){str=str+"&pid="+pid;}

    PartnerManager partnerManager = new PartnerManager();
     String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    String Config =" ";
    if(Roles.contains("superpartner")){
      pid=nullToStr(request.getParameter("pid"));
    }else{
      Config = "disabled";
      pid = String.valueOf(session.getAttribute("merchantid"));
    }
    //List<MerchantDetailsVO> memberList = partnerManager.getMemberListForFraud(partnerId);
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/addNewPartnerMerchantFraudAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" value="Merchant Fraud Accounts" name="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 55px;width: 245px;" src="/partner/images/addmerchantfraudaccount.png">
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
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerMerchantFraudAccount_Partner_Merchant_Fraud_Accounts%></strong></h2>
              <div class="additional-btn">

                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>


            <div class="widget-content padding">
              <div id="horizontal-form">

                <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken"id="ctoken">
                  <input type="hidden" value="<%=partnerId%>" name="partnerid" id="partnerid">
                  <%  String errormsg1 = (String)request.getAttribute("error");
                    if (errormsg1 == null)
                    {
                      errormsg1 = "";
                    }
                    else
                    {
                      /*out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");*/
                       out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                      //out.println(errormsg1);
                      //out.println("</font></td></tr></table>");
                    }
                  %>
                  <div class="form-group col-md-4">
                    <label class="col-sm-4 control-label"><%=partnerMerchantFraudAccount_PartnerId%></label>
                    <div class="col-sm-8">
                      <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                      <input type="hidden" name="pid" value="<%=pid%>">
                    </div>
                  </div>

                  <div class="form-group col-md-4">
                    <label class="col-sm-4 control-label"><%=partnerMerchantFraudAccount_MerchantId%></label>
                    <div class="col-sm-8">
                      <input name="memberid" id="member" value="<%=memberId%>" class="form-control" autocomplete="on">
                      <%--<select name="mid" class="form-control">
                        <option value="" selected>ALL</option>
                        <%
                          for(MerchantDetailsVO merchantDetailsVO : memberList)
                          {
                            String isSelected="";
                            if(merchantDetailsVO.getMemberId().equalsIgnoreCase(memberId))
                              isSelected="selected";
                            else
                              isSelected="";

                            String activation="N";
                            if("Y".equals(merchantDetailsVO.getActivation()))activation="Y";
                        %>
                        <option value="<%=merchantDetailsVO.getMemberId()%>" <%=isSelected%>><%=merchantDetailsVO.getMemberId()+"-"+merchantDetailsVO.getCompany_name()+"-"+activation%></option>
                        <%
                          }
                        %>
                      </select>--%>
                    </div>
                  </div>

                  <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                  <div class="form-group col-md-3">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=partnerMerchantFraudAccount_Search%></button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <%
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");

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
        if(records > 0)
        {

      %>

      <div class="row reporttable" id="containrecord">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerMerchantFraudAccount_ReportTable%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">

              <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantFraudAccount_Srno%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantFraudAccount_PartnerId%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantFraudAccount_MerchantID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantFraudAccount_Fraud_Account%>/b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantFraudAccount_Fraud%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantFraudAccount_IsActive%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantFraudAccount_IsVisible%></b></td>
                  <td  colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantFraudAccount_Action%></b></td>
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
                    PartnerFunctions partnerFunctions = new PartnerFunctions();

                    temphash=(Hashtable)hash.get(id);
                    out.println("<tr id=\"maindata\">");
                    out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Partner ID\" align=\"center\" "+style+">&nbsp;" +partnerFunctions.getPartnerId(ESAPI.encoder().encodeForHTML((String) temphash.get("memberid")))+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"Merchant ID\" align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Account/Website\" align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("subaccountname"))+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud System SubMerchant Username\" align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("submerchantUsername")==null?"-":(String)temphash.get("submerchantUsername"))+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"IsActive\" align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("isactive"))+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"IsVisible\" align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("isvisible"))+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\" "+style+"> <form action=\"/partner/net/ActionPartnerMerchantFraudAccount?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"merchantfraudserviceid\" value=\""+temphash.get("merchantfraudserviceid")+"\"><input type=\"hidden\" name=\"fssubaccountid\" value=\""+temphash.get("fssubaccountid")+"\"> <input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"gotoauto btn btn-default\" value=\"Modify\" >");
                    out.println(requestParameter);
                    out.println("</form></td></td>");
                    out.println("</tr>");
                  }
                %>
              </table>

              <%--<table align=center valign=top style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="background-color: #7eccad !important;color: white;">
                  <td  align="left" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Total Records : <%=totalrecords%></td>
                  <td  align="right" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Page No : <%=pageno%></td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                </tr>
                </thead>

                <tbody>
                <tr>
                  <td align=center>
                    <jsp:include page="page.jsp" flush="true">
                      <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                      <jsp:param name="numrows" value="<%=pagerecords%>"/>
                      <jsp:param name="pageno" value="<%=pageno%>"/>
                      <jsp:param name="str" value="<%=str%>"/>
                      <jsp:param name="page" value="PartnerMerchantFraudAccountList"/>
                      <jsp:param name="currentblock" value="<%=currentblock%>"/>
                      <jsp:param name="orderby" value=""/>
                    </jsp:include>
                  </td>
                </tr>
                </tbody>
              </table>--%>
              <%
                int TotalPageNo;
                if(totalrecords%pagerecords!=0)
                {
                  TotalPageNo=totalrecords/pagerecords+1;
                }
                else
                {
                  TotalPageNo=totalrecords/pagerecords;
                }
              %>
              <div id="showingid"><strong><%=partnerMerchantFraudAccount_page_no%> <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
              <div id="showingid"><strong><%=partnerMerchantFraudAccount_total_no_of_records%>   <%=totalrecords%> </strong></div>
              <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="PartnerMerchantFraudAccountList"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
              </jsp:include>
              <%
                }
                else if (request.getAttribute("updateMsg") != null)
                {
                  out.println("<div class=\"widget-content padding\">");
                  out.println(Functions.NewShowConfirmation1("Result", (String)request.getAttribute("updateMsg")));
                  out.println("</div>");
                }
                else
                {
                  /*out.println("<div class=reporttable>");
                  out.println(Functions.NewShowConfirmation1("Result", "No Records Found."));
                  out.println("</div>");*/
                  out.println("<div class=\"row reporttable\">");
                  out.println("<div class=\"col-md-12\">\n" +
                          "    <div class=\"widget\">\n" +
                          "      <div class=\"widget-header\">\n" +
                          "        <h2><i class=\"fa fa-table\"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>\n" +
                          "        <div class=\"additional-btn\">\n" +
                          "          <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                          "          <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                          "          <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                          "        </div>\n" +
                          "      </div>\n" +
                          "      <div class=\"widget-content padding\">");
                  out.println(Functions.NewShowConfirmation1("Result", "No Records Found."));
                  out.println("</div>");
                  out.println("</div>\n" +
                          "  </div>\n" +
                          "</div>");
                }
              %>
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
  public static String nullToStr(String str){
    if (str==null)
    return "";
    return str;
  }
%>