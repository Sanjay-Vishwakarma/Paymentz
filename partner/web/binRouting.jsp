<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger" %>
<%@ include file="routingTab.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  PartnerFunctions partnerFunctions=new PartnerFunctions();
  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);


  String binRouting_Bin_Routing_Settings = rb1.getString("binRouting_Bin_Routing_Settings");
  String binRouting_Partner_ID = rb1.getString("binRouting_Partner_ID");
  String binRouting_Merchant_ID = rb1.getString("binRouting_Merchant_ID");
  String binRouting_Account_ID = rb1.getString("binRouting_Account_ID");
  String binRouting_Start_Bin = rb1.getString("binRouting_Start_Bin");
  String binRouting_Single_Bin = rb1.getString("binRouting_Single_Bin");
  String binRouting_End_Bin = rb1.getString("binRouting_End_Bin");
  String binRouting_Search = rb1.getString("binRouting_Search");
  String binRouting_Upload = rb1.getString("binRouting_Upload");
  String binRouting_Report_Table = rb1.getString("binRouting_Report_Table");
  String binRouting_MemberId = rb1.getString("binRouting_MemberId");
  String binRouting_AccountId = rb1.getString("binRouting_AccountId");
  String binRouting_StartBin = rb1.getString("binRouting_StartBin");
  String binRouting_EndBin = rb1.getString("binRouting_EndBin");
  String binRouting_Showing_Page = rb1.getString("binRouting_Showing_Page");
  String binRouting_of = rb1.getString("binRouting_of");
  String binRouting_records = rb1.getString("binRouting_records");
  String binRouting_Sorry = rb1.getString("binRouting_Sorry");
  String binRouting_No = rb1.getString("binRouting_No");
  String binRouting_page_no = rb1.getString("binRouting_page_no");
  String binRouting_total_no_of_records = rb1.getString("binRouting_total_no_of_records");


%>
<%!
  private static Logger log=new Logger("binRouting.jsp");
%>
<html>
<head>
  <title><%=company%> Merchant Settings> Routing Settings </title>
  <%--<<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <style type="text/css">
    .center-block{
      display: inline-block;
      margin-right: auto;
      margin-left: auto;
    }

    #ui-id-2
    {
      overflow: auto;
      max-height: 350px;
    }
  </style>
</head>
<script language="javascript">
  function DoReverse(ctoken)
  {
    if (confirm("Do you really want to Delete this ?"))
    {
      return true;
    }
    else
      return false;
  }
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
  function Delete()
  {
    var checkboxes = document.getElementsByName("id");
    var total_boxes = checkboxes.length;
    flag = false;

    for(i=0; i<total_boxes; i++ )
    {
      if(checkboxes[i].checked)
      {
        flag= true;
        break;
      }
    }
    if(!flag)
    {
      alert("Select at least one record");
      return false;
    }
    if (confirm("Do you really want to Delete all selected Data."))
    {
      document.BinDelete.submit();
    }
  }
</script>
<script language="javascript">
  $(document).ready(function(){
    $("#sameBin").on("click", function () {
      var startBin
      if ($(this).is(":checked")) {
        binStart = $('[name="startBin"]').val();
        $("#endBin").val(binStart ).prop("readonly", true);
      } else {
        $("#endBin").val("").prop("readonly", false);
      }
    });
  });
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
<body class="bodybackground">
<div class="content-page">
  <div class="content" style="padding:0px 20px ;margin: 0px;">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=binRouting_Bin_Routing_Settings%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
              session.setAttribute("submit","binrouting");
              String str = "";
              if (partner.isLoggedInPartner(session))
              {
                String memberid=nullToStr(request.getParameter("memberid"));
                String pid=nullToStr(request.getParameter("pid"));
                String Config =null;
                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                if(Roles.contains("superpartner")){

                }else{
                  pid = String.valueOf(session.getAttribute("merchantid"));
                  Config = "disabled";
                }
                String startBin=nullToStr(request.getParameter("startBin"));
                String endBin=nullToStr(request.getParameter("endBin"));
                String accountid=nullToStr(request.getParameter("accountid"));
                String partnerid = session.getAttribute("partnerId").toString();
                String role=(String)session.getAttribute("role");
                String username=(String)session.getAttribute("username");
                String actionExecutorId=(String)session.getAttribute("merchantid");
                String actionExecutorName=role+"-"+username;
                str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                if(memberid!=null)str = str + "&memberid=" + memberid;
                if(pid!=null)str = str + "&pid=" + pid;
                if(accountid!=null)str = str + "&accountid=" + accountid;
                if(startBin!=null)str = str + "&startBin=" + startBin;
                if(endBin!=null)str = str + "&endBin=" + endBin;
                else
                  memberid="";
            %>
            <div id="">
              <form action="/partner/net/BinRouting?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="" class="form-horizontal" style="text-align: center">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                <%
                  int pageno=partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                  int pagerecords=partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                  String errormsg1 = (String) request.getAttribute("error");
                  String success = (String) request.getAttribute("success");
                  if (partnerFunctions.isValueNull(errormsg1))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                  }
                  if(partnerFunctions.isValueNull(success))
                  {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+success+"</h5>");
                  }
                %>
                <div class="form-group col-md-1">
                </div>
                <div class="form-group col-md-12">
                  <div class="ui-widget">
                    <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                      <label for="pid" ><%=binRouting_Partner_ID%></label>
                    </div>
                    <div class="col-md-3" >
                      <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                      <input name="pid" type="hidden" value="<%=pid%>">
                    </div>
                  </div>
                </div>

                <div class="form-group col-md-12">
                  <div class="ui-widget">
                    <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                      <label for="member" ><%=binRouting_Merchant_ID%></label>
                    </div>
                    <div class="col-md-3" >
                      <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                    </div>
                  </div>
                </div>

                <div class="form-group col-md-12">
                  <div class="ui-widget">
                    <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                      <label><%=binRouting_Account_ID%></label>
                    </div>
                    <div class="col-md-3" >
                      <input name="accountid" id="account_id" value="<%=accountid%>"  class="form-control">
                    </div>
                  </div>
                </div>

                <div class="form-group col-md-12">
                  <div class="ui-widget">
                    <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                     <label><%=binRouting_Start_Bin%></label>
                    </div>
                    <div class="col-md-3" >
                      <input name="startBin" id="startBin" name="startBin" maxlength="6" size="6" value="<%=startBin%>"class="form-control">
                    </div>
                    <div class="col-md-2"style="margin-top:10px;">
                      <input type="checkbox" id="sameBin" style="width:40px"><%=binRouting_Single_Bin%>
                    </div>
                  </div>
                </div>

                <div class="form-group col-md-12">
                  <div class="ui-widget">
                    <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                      <label><%=binRouting_End_Bin%></label>
                    </div>
                    <div class="col-md-3" >
                      <input name="endBin" id="endBin" name="endBin" maxlength="6" size="6" value="<%=endBin%>"class="form-control">
                    </div>
                  </div>
                </div>

                <div class="form-group col-md-12">
                  <div class="col-md-6 text-right center-block">
                    <button type="submit" class="btn btn-default center-block"><i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=binRouting_Search%></button>
                  </div>
                  <div class="col-md-6 text-left center-block">
                    <button type="submit" name="block" value="block" class="btn btn-default center-block"><i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=binRouting_Upload%></button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=binRouting_Report_Table%></strong></h2>
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
                HashMap hash = (HashMap)request.getAttribute("memberdetails");

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
                  log.debug("Exception::");
                }
                if(hash!=null)
                {
                  hash = (HashMap)request.getAttribute("memberdetails");
                }
                if(records>0)
                {
              %>
              <form action="/partner/net/BinDelete?ctoken=<%=ctoken%>" method="POST" name="BinDelete">
              <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=binRouting_MemberId%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=binRouting_AccountId%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=binRouting_StartBin%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=binRouting_EndBin%></b></td>

<%--                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Member Id</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Account Id</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Start Bin</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>End Bin</b></td>--%>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Action Executor Id</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Action Executor Name</b></td>

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
                    String startbin="";
                    String endbin="";

                    if(functions.isValueNull((String)temphash.get("startBin")))
                      startbin=(String)temphash.get("startBin");
                    if(functions.isValueNull((String)temphash.get("endBin")))
                      endbin=(String)temphash.get("endBin");

                   if(functions.isValueNull((String)temphash.get("actionExecutorId")))
                    {
                      actionExecutorId=(String)temphash.get("actionExecutorId");
                    }
                    else{actionExecutorId="-";}
                    if(functions.isValueNull((String)temphash.get("actionExecutorName")))
                    {
                      actionExecutorName=(String)temphash.get("actionExecutorName");
                    }
                    else {actionExecutorName="-";}

                    out.println("<tr id=\"maindata\">");
                    out.println("<td valign=\"middle\" data-label=\"id\" align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value="+ESAPI.encoder().encodeForHTML((String)temphash.get("id"))+"></td>");
                    out.println("<td valign=\"middle\" data-label=\"Member Id\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"Account Id\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("accountid")) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Start Bin\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(startbin)+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"End Bin\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(endbin)+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Action Executor Id\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorId)+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Action Executor Name\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorName)+ "</td>");
                    out.println(requestParameter.toString());
                    out.println("</form></td>");
                    out.println("</tr>");
                  }
                %>
              </table>
                <table width="100%">
                  <thead>
                  <tr>
                    <td width="15%" align="center">
                      <input type="hidden" name="memberid" value="<%=memberid%>">
                      <input type="hidden" name="accountid" value="<%=accountid%>">
                      <input type="hidden" name="startBin" value="<%=startBin%>">
                      <input type="hidden" name="startBin" value="<%=endBin%>">
                      <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default gotoauto" value="Delete" onclick="return Delete();">
                    </td>
                  </tr>
                  </thead>
                </table>
              </form>
            </div>
          </div>
        </div>
      </div>

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
      <div id="showingid"><strong><%=binRouting_page_no%> <%=pageno%>  <%=binRouting_of%>  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
      <div id="showingid"><strong><%=binRouting_total_no_of_records%>   <%=totalrecords%> </strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="BinRouting"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(binRouting_Sorry, binRouting_No));
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
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>