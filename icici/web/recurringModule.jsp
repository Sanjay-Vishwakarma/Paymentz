<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 23/6/18
  Time: 2:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Recurring Module</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
</head>
<%
  Functions functions = new Functions();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String pid = Functions.checkStringNull(request.getParameter("pid"));
  String rbid = Functions.checkStringNull(request.getParameter("recurring_subscription_id"));
  String memberid=Functions.checkStringNull(request.getParameter("memberid"));
  String terminalid = Functions.checkStringNull(request.getParameter("terminalid"));
  String recurring_subscription_id = Functions.checkStringNull(request.getParameter("recurring_subscription_id"));
  System.out.println("recurring_subscription_idjsp"+recurring_subscription_id);
  String firstsix = Functions.checkStringNull(request.getParameter("firstsix"));
  String lastfour=Functions.checkStringNull(request.getParameter("lastfour"));
  String name = Functions.checkStringNull(request.getParameter("name"));
  String pTerminalBuffer = Functions.checkStringNull(request.getParameter("terminalBuffer"));
  String cardHolderName = Functions.checkStringNull(request.getParameter("name"));
  String trackingid = request.getParameter("trackingid")==null?"":request.getParameter("trackingid");

  int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
  int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

  String str = "";
  if (pid == null) pid = "";
  if (memberid == null)memberid = "";
  if (terminalid == null)terminalid = "";
  if (recurring_subscription_id == null) recurring_subscription_id = "";
  if (firstsix == null) firstsix = "";
  if (lastfour == null) lastfour = "";
  if (trackingid == null) trackingid = "";
  if (name == null) name = "";
  if (pTerminalBuffer == null) pTerminalBuffer = "";
  if (cardHolderName == null) cardHolderName = "";


  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Recurring Module
      </div>
      <form action="/icici/servlet/RecurringModule?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
          <%
            String errorMsg=(String) request.getAttribute("errorMsg");
              if(errorMsg !=null)
              {
                  out.println("<center><font class=\"textb\"><b>"+errorMsg+"</b></font></center>");
              }
          %>
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5%">
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Partner ID*</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text"value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>"
                           name="pid" id="allpid" class="txtbox" autocomplete="on">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Merchant Id</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text"value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>"
                           name="memberid" id="pmid2" class="txtbox" autocomplete="on">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Terminal Id</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text"value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%>"
                           name="terminalid"  id="mtid1" class="txtbox" autocomplete="on">
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Recurring Billing ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text"value="<%=ESAPI.encoder().encodeForHTMLAttribute(recurring_subscription_id)%>"
                           name="recurring_subscription_id" class="txtbox">
                  </td>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">First Six</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="firstsix" maxlength="6" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstsix)%>">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Last Four</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">
                    <input type=text name="lastfour" maxlength="4"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfour)%>" class="txtbox" size="10">
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>

                <tr>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="4%" class="textb" >Tracking ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="trackingid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" class="txtbox" size="10">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="4%" class="textb" >Card Holder Name</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="name" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" class="txtbox" size="20">
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>

                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <button type="submit" class="buttonform">
                      Search
                    </button>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
    </div>
  </div>
</div>
</form>
<div class="reporttable">
  <%
    String error=(String) request.getAttribute("error");
    if(error !=null)
    {
      out.println("<center><font class=\"textb\"><b>"+error+"</b></font></center>");
    }
    String refundMsg=(String) request.getAttribute("refundMsg");
    if(refundMsg !=null)
    {
      out.println("<center><font class=\"textb\"><b>"+refundMsg+"</b></font></center>");
    }
    String currentblock=request.getParameter("currentblock");

    if(currentblock==null)
      currentblock="1";

    if(request.getAttribute("recurringBillingVO")!=null)
    {
      List<RecurringBillingVO> rbList = (List<RecurringBillingVO>) request.getAttribute("recurringBillingVO");
      PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
      paginationVO.setInputs(paginationVO.getInputs() + "&ctoken="+ctoken);
      paginationVO.setInputs(paginationVO.getInputs() + "&terminalbuffer="+request.getParameter("terminalbuffer"));

      if(rbList.size() > 0)
      {

  %>
    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td valign="middle" align="center" class="th0">Sr. No</td>
        <td valign="middle" align="center" class="th0">First Transaction ID</td>
        <td valign="middle" align="center" class="th0">Partner ID</td>
        <td valign="middle" align="center" class="th0">Merchant ID</td>
        <td valign="middle" align="center" class="th0">Recurring Subscription Date</td>
        <td valign="middle" align="center" class="th0">Card Holder Name</td>
        <td valign="middle" align="center" class="th0">First Six</td>
        <td valign="middle" align="center" class="th0">Last Four</td>
        <td valign="middle" align="center" class="th0">Recurring Type</td>
        <td valign="middle" align="center" class="th0">Status</td>
        <td valign="middle" align="center" class="th0">Terminal</td>
      </tr>
      </thead>
      <%
        int sno = 1;
        String style="class=td1";
        String ext="light";
        Hashtable temphash=null;
        for(RecurringBillingVO recurringBillingVO : rbList)
        {
          /*String accountID="";
          String cardNO="";
          String nameStr="";*/

          String origTrackingid = ESAPI.encoder().encodeForHTML(recurringBillingVO.getOriginTrackingId());
          rbid = recurringBillingVO.getRbid();
          String partnerId=ESAPI.encoder().encodeForHTML(recurringBillingVO.getPartnerId());
          String memberId=ESAPI.encoder().encodeForHTML(recurringBillingVO.getMemberId());
          String recStatus = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringStatus());
          String registerDate = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringRegisterDate());
          String recurringType = ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringType());
          name = ESAPI.encoder().encodeForHTML(recurringBillingVO.getCardHolderName());
          String firstSix = ESAPI.encoder().encodeForHTML(recurringBillingVO.getFirstSix());
          String lastFour = ESAPI.encoder().encodeForHTML(recurringBillingVO.getLastFour());
          terminalid = ESAPI.encoder().encodeForHTML(recurringBillingVO.getTerminalid());

          if(functions.isValueNull(recurringBillingVO.getCardHolderName()))
            name = ESAPI.encoder().encodeForHTML(recurringBillingVO.getCardHolderName());
          if(functions.isValueNull(recurringBillingVO.getFirstSix()))
            firstSix = ESAPI.encoder().encodeForHTML(recurringBillingVO.getFirstSix());
          if(functions.isValueNull(recurringBillingVO.getLastFour()))
            lastFour = ESAPI.encoder().encodeForHTML(recurringBillingVO.getLastFour());


          int pos=1;
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
          int srno=sno+ ((pageno-1)*pagerecords);
          out.println("<tr>");
          //out.println("<td align=center "+style+">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+transactionVO.getTrackingId()+"\"></td>");
          out.println("<td align=center "+style+">&nbsp;"+srno+"</td>");
          if(!functions.isValueNull(rbid))
          {
            rbid = "";
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

                  out.println("<td align=center "+style+">"+
                  "<form action=\"RbidDetails?ctoken="+ctoken+"\" method=\"post\">" +"<input type=\"hidden\" name=\"rbid\" value=\"" + rbid + "\"><input type=\"hidden\" name=\"recurring_subscription_id\" value=\"" + recurring_subscription_id + "\"><input type=\"hidden\" name=\"pid\" value=\"" + pid + "\"><input type=\"hidden\" name=\"memberid\" value=\"" + memberId + "\"><input type=\"hidden\" name=\"terminalid\" value=\"" + terminalid + "\"><input type=\"hidden\" name=\"SPageno\" value=\"" + pageno + "\" ><input type=\"hidden\" name=\"trackingid\" value=\"" + origTrackingid + "\" ><input type=\"hidden\" name=\"lastfour\" value=\"" + lastfour + "\" ><input type=\"submit\" class=\"btn btn-default\" name=\"submit\" value=\"" + origTrackingid + "\"></form></td>");


          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(partnerId)+"<input type=\"hidden\" name=\"pid_"+recurringBillingVO.getPartnerId()+"\" value=\""+recurringBillingVO.getPartnerId()+"\"></td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(memberId)+"<input type=\"hidden\" name=\"memberid_" +recurringBillingVO.getMemberId()+"\" value=\""+memberId+"\"></td>");
          if (!functions.isValueNull(recurringType))
          {
            out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
          }
          else
          {
            out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringRegisterDate()) + "</td>");
          }
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(name)+"<input type=\"hidden\" name=\"name_" +recurringBillingVO.getCardHolderName()+"\" value=\""+recurringBillingVO.getCardHolderName()+"\"></td>");
          if (firstSix!=null)
          {
            out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getFirstSix()) + "</td>");
          }
          else if(firstSix==null)
          {
            out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
          }
          if (recurringBillingVO.getLastFour()!=null)
          {
            out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getLastFour()) + "</td>");
          }
          else if(recurringBillingVO.getLastFour()==null)
          {
            out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
          }
          if (recurringBillingVO.getRecurringType()!=null)
          {
            out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringType()) + "</td>");
          }
          else if(recurringBillingVO.getRecurringType()==null)
          {
            out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
          }
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(recStatus)+"<input type=\"hidden\" name=\"status_"+recurringBillingVO.getRecurringStatus()+"\" value=\""+recurringBillingVO.getRecurringStatus()+"\"></td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(terminalid)+"<input type=\"hidden\" name=\"terminalid_"+recurringBillingVO.getTerminalid()+"\" value=\""+recurringBillingVO.getTerminalid()+"\"></td>");
          out.println("</tr>");
          sno++;
        }
      %>
    </table>
  </form>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
        <jsp:param name="page" value="RecurringModule"/>
        <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>

  <%
      }
      else
      {
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
      }
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }
  %>
</div>
<%
    }
    else
    {
      response.sendRedirect("/icici/logout.jsp");
      return;
    }
  }
%>
</body>
</html>