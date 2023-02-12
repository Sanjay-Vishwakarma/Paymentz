<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Ajit.k
  Date: 18/09/2019
  Time: 1:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  String partnerid= String.valueOf(session.getAttribute("partnerId"));
  session.setAttribute("submit","partnerlist");
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
  <title><%=company%> Partner> Partner Master</title>
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
      var x = document.getElementById("Breackdiv");

      if(w > 990){
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");
        x.style.display = "none";

      }
      else{
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        x.style.display = "block";
        $(".left ul").removeAttr("style");
      }
    });

  </script>
  <script>
    function ConfirmUnblock()
    {
      var x = confirm("Do you really want to Unblock this User ?");
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
  if (partner.isLoggedInPartner(session))
  {
    String str = "";
    String partnerid1 = String.valueOf(session.getAttribute("partnerId"));
    String partnerId=Functions.checkStringNull(request.getParameter("partnerId"))==null?"":request.getParameter("partnerId");;
    String partnerName=Functions.checkStringNull(request.getParameter("partnerName"))==null?"":request.getParameter("partnerName");;

    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if(partnerId!=null)str = str + "&partnerId=" + partnerId;
    else
      partnerId="";
    if(partnerName!=null)str = str + "&partnerName=" + partnerName;
    else
      partnerName="";

    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerlist_Upload_Partner_Icon = StringUtils.isNotEmpty(rb1.getString("partnerlist_Upload_Partner_Icon")) ? rb1.getString("partnerlist_Upload_Partner_Icon") : "Upload Partner Icon";
    String partnerlist_Upload_Partner_Logo = StringUtils.isNotEmpty(rb1.getString("partnerlist_Upload_Partner_Logo")) ? rb1.getString("partnerlist_Upload_Partner_Logo") : "Upload Partner Logo";
    String partnerlist_Upload_Partner_Favicon = StringUtils.isNotEmpty(rb1.getString("partnerlist_Upload_Partner_Favicon")) ? rb1.getString("partnerlist_Upload_Partner_Favicon") : "Upload Partner Favicon";
    String partnerlist_Blocked_Partner_Account = StringUtils.isNotEmpty(rb1.getString("partnerlist_Blocked_Partner_Account")) ? rb1.getString("partnerlist_Blocked_Partner_Account") : "Blocked Partner's Account";
    String partnerlist_Add_New_Partner = StringUtils.isNotEmpty(rb1.getString("partnerlist_Add_New_Partner")) ? rb1.getString("partnerlist_Add_New_Partner") : "Add New Partner";
    String partnerlist_Partner_Master = StringUtils.isNotEmpty(rb1.getString("partnerlist_Partner_Master")) ? rb1.getString("partnerlist_Partner_Master") : "Partner Master";
    String partnerlist_Partner_ID = StringUtils.isNotEmpty(rb1.getString("partnerlist_Partner_ID")) ? rb1.getString("partnerlist_Partner_ID") : "Partner ID";
    String partnerlist_Partner_Name = StringUtils.isNotEmpty(rb1.getString("partnerlist_Partner_Name")) ? rb1.getString("partnerlist_Partner_Name") : "Partner Name";
    String partnerlist_Path = StringUtils.isNotEmpty(rb1.getString("partnerlist_Path")) ? rb1.getString("partnerlist_Path") : "Path";
    String partnerlist_Search = StringUtils.isNotEmpty(rb1.getString("partnerlist_Search")) ? rb1.getString("partnerlist_Search") : "Search";
    String partnerlist_Report_Table = StringUtils.isNotEmpty(rb1.getString("partnerlist_Report_Table")) ? rb1.getString("partnerlist_Report_Table") : "Report Table";
    String partnerlist_SrNo = StringUtils.isNotEmpty(rb1.getString("partnerlist_SrNo")) ? rb1.getString("partnerlist_SrNo") : "Sr No";
    String partnerlist_Super_PartnerID = StringUtils.isNotEmpty(rb1.getString("partnerlist_Super_PartnerID")) ? rb1.getString("partnerlist_Super_PartnerID") : "Super Partner ID";
    String partnerlist_Action = StringUtils.isNotEmpty(rb1.getString("partnerlist_Action")) ? rb1.getString("partnerlist_Action") : "Action";
    String partnerlist_ShowingPage = StringUtils.isNotEmpty(rb1.getString("partnerlist_ShowingPage")) ? rb1.getString("partnerlist_ShowingPage") : "Showing Page";
    String partnerlist_of = StringUtils.isNotEmpty(rb1.getString("partnerlist_of")) ? rb1.getString("partnerlist_of") : "of";
    String partnerlist_records = StringUtils.isNotEmpty(rb1.getString("partnerlist_records")) ? rb1.getString("partnerlist_records") : "records";
    String partnerlist_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerlist_Sorry")) ? rb1.getString("partnerlist_Sorry") : "Sorry";
    String partnerlist_no = StringUtils.isNotEmpty(rb1.getString("partnerlist_no")) ? rb1.getString("partnerlist_no") : "No records found.";

%>
<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/addTemplateColors.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" style="background-color: #98A3A3; border-radius: 25px;color: white;font-size: 14px;cursor: pointer;">Add Template Colors
            </button>
          </form>
        </div>
      </div>

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerIcon.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" style="background-color: #98A3A3; border-radius: 25px;color: white;font-size: 14px;cursor: pointer;"><%=partnerlist_Upload_Partner_Icon%>
            </button>
          </form>
        </div>
      </div>

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerLogo.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit"  style="background-color: #98A3A3; border-radius: 25px;color: white;font-size: 14px;cursor: pointer;"><%=partnerlist_Upload_Partner_Logo%>
            </button>
          </form>
        </div>
      </div>

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerFavicon.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" style="background-color: #98A3A3; border-radius: 25px;color: white;font-size: 14px;cursor: pointer;"><%=partnerlist_Upload_Partner_Favicon%>
            </button>
          </form>
        </div>
      </div>

     <%-- <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/net/PartnerUnblockedAccount?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" style="background-color: #98A3A3; border-radius: 25px;color: white;font-size: 14px;cursor: pointer;"><%=partnerlist_Blocked_Partner_Account%>
            </button>
          </form>
        </div>
      </div>--%>

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnersignup.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" style="background-color: #98A3A3; border-radius: 25px;color: white;font-size: 14px;cursor: pointer;"><%=partnerlist_Add_New_Partner%>
            </button>
          </form>
        </div>
      </div>
    </div>
    <div id="Breackdiv">
      <br><br><br><br><br><br><br><br><br><br><br>
    </div>
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerlist_Partner_Master%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/PartnerDetailList?ctoken=<%=ctoken%>" method="post" name="forms">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partnerid%>" name="superAdminId" id="partnerid">
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
                    <label for="PID"><%=partnerlist_Partner_ID%></label>
                    <input name="partnerId" id="PID" value="<%=partnerId%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=partnerlist_Partner_Name%></label>
                    <input  type="text" name="partnerName" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerName)%>">
                  </div>

                  <div class="form-group col-md-4">
                    <label style="color: transparent;"><%=partnerlist_Path%></label>
                    <button type="submit" class="btn btn-default" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=partnerlist_Search%>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerlist_Report_Table%></strong></h2>
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
                HashMap hash = (HashMap)request.getAttribute("transdetails");

                HashMap temphash=null;
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
                  hash = (HashMap)request.getAttribute("transdetails");
                }
                if (request.getAttribute("msg") != null)
                {
                  String msg = (String) request.getAttribute("msg");
                  if (msg != null)
                  {
                    out.println(Functions.NewShowConfirmation1("Sorry", msg));
                  }
                }
                else if(records>0)
                {
              %>
              <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Sr No</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Partner ID</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Super Partner ID</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Partner Name</b></td>
                  <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Action</b></td>

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
                    temphash=(HashMap)hash.get(id);

                    String login=ESAPI.encoder().encodeForHTML((String) temphash.get("login"));
                    String unblock=partner.getUnblockStatus(login);
                    String disabled="";
                    if ("unlocked".equalsIgnoreCase(unblock)){
                      disabled="disabled";
                    }
                    //System.out.println("partnerid::::::"+temphash.get("partnerid"));

                    out.println("<tr id=\"maindata\">");
                    out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("superadminid"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerName"))+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/net/UpdatePartnerDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"partnerID\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+"\"><input type=\"submit\" name=\"action\" value=\"View\" class=\"gotoauto btn btn-default\" ><input type=\"hidden\" name=\"action\" value=\"View\">");
                    out.println(requestParameter.toString());
                    out.println("</form></td>");
                    out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/net/UpdatePartnerDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"partnerID\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+"\"><input type=\"submit\" name=\"chk\" value=\"Edit\" class=\"gotoauto btn btn-default\"><input type=\"hidden\" name=\"action\" value=\"modify\">");
                    out.println(requestParameter.toString());
                    out.println("</form></td>");
                    out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/partnerModuleAllocation.jsp?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"partnerID\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+"\"><input type=\"hidden\" name=\"partnerName\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerName"))+"\"><input type=\"submit\" name=\"chk\" value=\"Module Allocation\" class=\"gotoauto btn btn-default\"><input type=\"hidden\" name=\"action\" value=\"Module\">");
                    out.println(requestParameter.toString());
                    out.println("</form></td>");
                    out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\"" + style + "><form action=\"/partner/net/PartnerAccountUnblock?ctoken=" + ctoken + "\" method=\"POST\"><input type=\"hidden\" name=\"login\" value=\"" + ESAPI.encoder().encodeForHTML((String) temphash.get("login")) + "\"><input type=\"submit\" name=\"Unblock\" value=\"Unblock\"  Onclick=\"return ConfirmUnblock();\" class=\"gotoauto btn btn-default\" "+disabled+"><input type=\"hidden\" name=\"action\" value=\"Unblock\">");
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
      <div id="showingid"><strong><%=partnerlist_ShowingPage%> <%=pageno%> <%=partnerlist_of%> <%=totalrecords%> <%=partnerlist_records%></strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="PartnerDetailList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(partnerlist_Sorry,partnerlist_no ));
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