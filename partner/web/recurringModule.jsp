<%--
  Created by IntelliJ IDEA.
  User: Diksha
  Date: 15-Jan-21
  Time: 3:15 PM
  To change this template use File | Settings | File Templates.
--%>
<%--<%@ page errorPage="error.jsp" %>--%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.List" %>
<%@ include file="ietest.jsp" %>
<%@ include file="top.jsp" %>

<%
  String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
  String partnerid = session.getAttribute("partnerId").toString();
  session.setAttribute("submit","recurringModule");
%>
<html>
<head>
  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>

  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%> Recurring Module</title>

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
  <style type="text/css">
    #ui-id-2
    {
      overflow: auto;
      max-height: 350px;
    }
  </style>

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


<body <%--class="pace-done widescreen fixed-left-void "--%>>
<%
  PaginationVO paginationVO = new PaginationVO();

  String str = "";
  String memberid = nullToStr(request.getParameter("memberid"));
  String pid = nullToStr(request.getParameter("pid"));
  String Config =null;
  String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
  if(Roles.contains("superpartner"))
  {
    pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
  }
  else
  {
    Config = "disabled";
  }
  pid = String.valueOf(session.getAttribute("merchantid"));
  String rbid = nullToStr(request.getParameter("rbid"));
  String recurring_subscription_id = nullToStr(request.getParameter("recurring_subscription_id"));
  String firstsix = Functions.checkStringNull(request.getParameter("firstsix"));
  String lastfour = Functions.checkStringNull(request.getParameter("lastfour"));
  String trackingid = Functions.checkStringNull(request.getParameter("trackingid"));
  String terminalid = nullToStr(request.getParameter("terminalid"));
  String pTerminalBuffer = Functions.checkStringNull(request.getParameter("terminalBuffer"));
  String cardHolderName = Functions.checkStringNull(request.getParameter("name"));
  str =  str + "&ctoken=" + ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (pid != null) str = str + "&pid=" + pid;
  if (memberid != null) str = str + "&memberid=" + memberid;
  if (rbid != null) str = str + "&rbid=" + rbid;
  if (firstsix != null) str = str + "&firstsix=" + firstsix;
  if (lastfour != null) str = str + "&lastfour=" + lastfour;
  if (trackingid != null) str = str + "&trackingid=" + trackingid;
  if (terminalid != null) str = str + "&terminalid=" + terminalid;
  if (cardHolderName != null) str = str + "&name=" + cardHolderName;
  if (pTerminalBuffer != null) str = str + "&terminalBuffer=" + pTerminalBuffer;

  str = str + "&SRecords=" + paginationVO.getRecordsPerPage();

  int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
  int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

%>


    <div class="content-page">
      <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">


          <div class="row">
            <div class="col-sm-12 portlets ui-sortable">
              <div class="widget">

                <div class="widget-header transparent">
                  <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Recurring Module</strong></h2>
                  <div class="additional-btn">
                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                  </div>
                </div>

                <div class="widget-content padding">
                  <div id="horizontal-form">

                    <form action="/partner/net/RecurringModule?ctoken=<%=ctoken%>" name="form" method="post" >
                      <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                      <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">

                      <div class="form-group col-md-4 has-feedback">
                        <label>Partner ID*</label>
                        <input type=text name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                        <input type=hidden name="pid" value="<%=pid%>" >
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="member">Merchant ID</label>
                        <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label>Terminal ID</label>
                        <input name="terminalid" id="terminal" value="<%=terminalid%>" class="form-control" autocomplete="on">
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label >Recurring Billing ID</label>
                        <input name="recurring_subscription_id" size="10" class="form-control" value="<%=recurring_subscription_id%>">
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label >First Six</label>
                        <input name="firstsix" size="10" class="form-control">
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label >Last Four</label>
                        <input name="lastfour" size="10" class="form-control">
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label >Tracking ID</label>
                        <input name="trackingid" size="10" class="form-control">
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label >Card Holder Name</label>
                        <input name="name" size="10" class="form-control">
                      </div>

                      <div class="form-group col-md-4">
                      </div>
                      <div class="form-group col-md-4">
                      </div>

                      <div class="form-group col-md-4">
                        <button type="submit" class="btn btn-default">
                          <i class="fa fa-save"></i>
                          &nbsp;&nbsp;Search
                        </button>

                      </div>

                      <%-- <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">--%>
                      <%--<input type="hidden" name="terminalid" value="<%=terminalid%>">--%>
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
                  <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Recuring Deatils</strong></h2>

                  <div class="additional-btn">
                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                  </div>
                </div>
                <%
                  Functions functions = new Functions();
                  String error1 = (String) request.getAttribute("errormsg");

                  if (functions.isValueNull(error1))
                  {
                    out.println("<p><h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error1 + "</h5></p>");
                  }
                %>
                <div class="widget-content padding">

                  <%
                    if(request.getAttribute("recurringBillingVO")!=null)
                    {
                      List<RecurringBillingVO> rbList = (List<RecurringBillingVO>) request.getAttribute("recurringBillingVO");
                      paginationVO =(PaginationVO) request.getAttribute("paginationVO");
                      paginationVO.setInputs(paginationVO.getInputs() + "&ctoken="+ctoken);
                      paginationVO.setInputs(paginationVO.getInputs() + "&terminalbuffer="+request.getParameter("terminalbuffer"));

                      if(rbList.size() > 0)
                      {
                        int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;

                  %>

                  <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <thead>
                    <tr>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Sr No</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>First Transaction ID</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Partner ID</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Merchant ID</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Recurring Subscription Date</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Card Holder Name</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>First Six</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Last Four</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Recurring Type</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Status</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Terminal ID</b></td>
                    </tr>
                    </thead>
                    <tbody>
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">

                    <%

                      StringBuffer requestParameter= new StringBuffer();
                      Enumeration<String> requestName=request.getParameterNames();
                      while(requestName.hasMoreElements())
                      {
                        String name=requestName.nextElement();
                        if("SPageno".equals(name) || "SRecords".equals(name))
                        {

                        }
                        else
                          requestParameter.append("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                      }
                      requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                      requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                      String style="class=td1";
                      String ext="light";
                      int pos = 1;
                      for(RecurringBillingVO recurringBillingVO : rbList)
                      {
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

                        String origTrackingid = ESAPI.encoder().encodeForHTML(recurringBillingVO.getOriginTrackingId());
                        rbid = recurringBillingVO.getRbid();
                        String partnerId=ESAPI.encoder().encodeForHTML(recurringBillingVO.getPartnerId());
                        String memberId=ESAPI.encoder().encodeForHTML(recurringBillingVO.getMemberId());
                        String recStatus = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringStatus());
                        String registerDate = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringRegisterDate());
                        String recurringType = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringType());
                        String name = ESAPI.encoder().encodeForHTML(recurringBillingVO.getCardHolderName());
                        String firstSix = ESAPI.encoder().encodeForHTML(recurringBillingVO.getFirstSix());
                        String lastFour = ESAPI.encoder().encodeForHTML(recurringBillingVO.getLastFour());
                        terminalid = ESAPI.encoder().encodeForHTML(recurringBillingVO.getTerminalid());

                        if(functions.isValueNull(recurringBillingVO.getCardHolderName()))
                          name = ESAPI.encoder().encodeForHTML(recurringBillingVO.getCardHolderName());
                        if(functions.isValueNull(recurringBillingVO.getFirstSix()))
                          firstSix = ESAPI.encoder().encodeForHTML(recurringBillingVO.getFirstSix());
                        if(functions.isValueNull(recurringBillingVO.getLastFour()))
                          lastFour = ESAPI.encoder().encodeForHTML(recurringBillingVO.getLastFour());

                        out.println("<tr>");
                        out.println("<td align=center data-label=\"Sr.No\">" +srno+"</td>");
                        if(!functions.isValueNull(rbid))
                        {
                          rbid = "";
                        }
                        if(!functions.isValueNull(firstsix))
                        {
                          firstsix="";
                        }
                        if(!functions.isValueNull(lastfour))
                        {
                          lastfour="";
                        }
                        if(!functions.isValueNull(trackingid))
                        {
                          trackingid="";
                        }
                        if(!functions.isValueNull(name))
                        {
                          name="";
                        }
                        if(!functions.isValueNull(terminalid))
                        {
                          terminalid="";
                        }
                        if (!functions.isValueNull(recurringType))
                        {
                          recurringType= "";
                        }
                        out.println("<td align=center data-label=\"Recurring Billing\"><form action=\"RbidDetails?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"rbid\" value=\"" + rbid + "\"><input type=\"hidden\" name=\"recurring_subscription_id\" value=\"" + recurring_subscription_id + "\"><input type=\"hidden\" name=\"pid\" value=\"" + pid + "\"><input type=\"hidden\" name=\"memberid\" value=\"" + memberId + "\"><input type=\"hidden\" name=\"terminalid\" value=\"" + terminalid + "\"><input type=\"hidden\" name=\"SPageno\" value=\"" + pageno + "\" ><input type=\"hidden\" name=\"trackingid\" value=\"" + origTrackingid + "\" ><input type=\"hidden\" name=\"lastfour\" value=\"" + lastfour + "\" ><input type=\"submit\" class=\"btn btn-default\" name=\"submit\" value=\"" + origTrackingid + "\"></form></td>");

                        out.println(requestParameter);
                        out.println("</form></td>");

                        out.println("<td align=center data-label=\"Partner ID\">" +partnerId+"<input type=\"hidden\" name=\"partnerid\" value=\""+pid+"\""+"></td>");
                        out.println("<td align=center data-label=\"Merchant ID\">" +memberId+"<input type=\"hidden\" name=\"memberid\" value=\""+memberId+"\""+"></td>");

                        if (!functions.isValueNull(recurringType))
                        {
                          out.println("<td align=center "+style+">"+"-"+"</td>");
                        }
                        else
                        {
                          out.println("<td align=center data-label=\"Recurring Subscription Date\">"+registerDate+"</td>");
                        }
                        out.println("<td align=center data-label=\"Card Holder Name\">" +name+"<input type=\"hidden\" name=\"name\" value=\""+name+"\""+"></td>");

                        if (recurringBillingVO.getFirstSix()!=null)
                        {
                          out.println("<td align=center data-label=\"First Six\">" +firstSix+"<input type=\"hidden\" name=\"firstsix\" value=\""+firstSix+"\""+"></td>");
                        }
                        else if (recurringBillingVO.getFirstSix()==null)
                        {
                          out.println("<td align=center data-label=\"First Six\">" +"-"+"<input type=\"hidden\" name=\"firstsix\" value=\""+"-"+"\""+"></td>");
                        }

                        if (recurringBillingVO.getLastFour() !=null)
                        {
                          out.println("<td align=center data-label=\"Last Four\">" +lastFour+"<input type=\"hidden\" name=\"lastfour\" value=\""+lastFour+"\""+"></td>");
                        }

                        else if (recurringBillingVO.getLastFour()==null)
                        {
                          out.println("<td align=center data-label=\"Last Four\">" +"-"+"<input type=\"hidden\" name=\"lastfour\" value=\""+"-"+"\""+"></td>");
                        }

                        out.println("<td align=center data-label=\"Recurring Type\">" +recurringType+"<input type=\"hidden\" name=\"recurringType\" value=\""+recurringType+"\""+"></td>");
                        out.println("<td align=center data-label=\"Recurring Status\">" +recStatus+"<input type=\"hidden\" name=\"recStatus\" value=\""+recStatus+"\""+"></td>");
                        out.println("<td align=center data-label=\"Terminal Id\">" +terminalid+"<input type=\"hidden\" name=\"terminalid\" value=\""+terminalid+"\""+"></td>");
                        out.println(requestParameter);
                        out.println("</form></td>");
                        out.println("</tr>");
                        srno++;

                      }
                    %>


                    </tbody>
                  </table>

                </div>

              </div>
            </div>
          </div>

          <%
            int TotalPageNo;
            if(paginationVO.getTotalRecords()%paginationVO.getRecordsPerPage()!=0)
            {
              TotalPageNo =paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage()+1;
            }
            else
            {
              TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage();
            }
          %>
          <div id="showingid"><strong>Page No <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
          <div id="showingid"><strong>Total Records   <%=paginationVO.getTotalRecords()%> </strong></div>

          <div>
            <jsp:include page="page.jsp" flush="true">
              <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
              <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
              <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
              <jsp:param name="str" value="<%=str%>"/>
              <jsp:param name="page" value="RecurringModule"/>
              <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
              <jsp:param name="orderby" value=""/>
            </jsp:include>
          </div>
          <%
              }
              else
              {
                out.println(Functions.NewShowConfirmation1("Sorry", "No records found." ));
              }

            }
            else
            {

              String error=(String) request.getAttribute("error");
              if( error!= null)
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
              }
              else
                out.println(Functions.NewShowConfirmation1("Filter","Please provide the data to get List of Recurring Transactions."));
            }

          %>

          <%!
            public static String nullToStr(String str)
            {
              if(str == null)
                return "";
              return str;
            }
          %>

        </div>
      </div>
    </div>
</body>
</html>