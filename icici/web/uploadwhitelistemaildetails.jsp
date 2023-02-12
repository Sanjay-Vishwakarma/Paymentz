<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>

<html>
<head>
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
    function Update()
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
        alert("Select at least one MemberID");
        return false;
      }
      if (confirm("Do you really want to Update all selected."))
      {
        document.updateform.submit();
        return true;
      } else {
        return false;
      }
    }
  </script>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid.js"></script>
</head>
<title>1.Whitelist Module> Whitelist Email</title>
<%
  System.out.println("--inside jsp--");
  try
  {
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String str  = "";
    Functions functions = new Functions();
    int pageno          = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords     = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

    String accountId    = request.getParameter("accountid")==null?"":request.getParameter("accountid");
    String memberId     = request.getParameter("memberid")==null?"":request.getParameter("memberid");
    String firstSix     = request.getParameter("firstsix")==null?"":request.getParameter("firstsix");
    String lastFour     = request.getParameter("lastfour")==null?"":request.getParameter("lastfour");
    String emailAddress = request.getParameter("emailaddr")==null?"":request.getParameter("emailaddr");
    String isApproved   = request.getParameter("isApproved");
    String isTemp       = request.getParameter("isTemp");
    String memberid     = nullToStr(request.getParameter("merchantid"));

    if (accountId != null) str = str + "&accountid=" + accountId;
    // if (memberId != null) str = str + "&memberid=" + memberId;
    if (memberid != null) str = str + "&merchantid=" + memberid;
    if (firstSix != null) str = str + "&firstsix=" + firstSix;
    if (lastFour != null) str = str + "&lastfour=" + lastFour;
    if (emailAddress != null) str = str + "&emailaddr=" + emailAddress;
    if (isTemp!=null)str = str + "&isTemp=" + isTemp;
    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    str = str + "&SRecords=" + pagerecords;
%>
<body>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        WhiteList Details
      </div><br>
      <form action="/icici/servlet/WhiteListEmailDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <%
                String errorMsg = (String) request.getAttribute("cbmessage");
              %>
              <table border="0" cellpadding="3" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="3">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8" class="textb" for="mid">Merchant ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="merchantid" id="memberid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Account ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="accountid" id="accountid" value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" class="txtbox" autocomplete="on">
                   <%-- <input type="text" size="10"name="accountid"class="txtbox" value=<%=accountId%>>--%>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Email ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input type="text" size="35"name="emailaddr"class="txtbox" value=<%=emailAddress%>>
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
                  <td width="8%" class="textb" colspan="2">Card Number</td>
                  <td width="7%" class="textb">
                    <input type=text  name="firstsix" maxlength="6" size="5"  class="txtbox" style="width:60px" value="<%=firstSix%>">
                    <input type=text name="lastfour" maxlength="4" size="4" class="txtbox" style="width:60px" value="<%=lastFour%>">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >In Active</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="isTemp" class="txtbox">
                      <option value="N" <%if("N".equalsIgnoreCase(isTemp)){%>selected<%}%>>N</option>
                      <option value="Y" <%if("Y".equalsIgnoreCase(isTemp)){%>selected<%}%>>Y</option>
                    </select>
                    <%-- <input type="text" size="10"name="accountid"class="txtbox" value=<%=accountId%>>--%>
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="12%" class="textb">&nbsp;
                    <button type="submit" class="buttonform" value="Submit" style="margin-bottom: 10%">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
    <%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    if(hash!=null)
    {
        hash = (Hashtable)request.getAttribute("transdetails");
    }
     Hashtable temphash = null;
    int records         = 0;
    int totalrecords    = 0;
    String currentblock = request.getParameter("currentblock");

    if(currentblock == null)
        currentblock  = "1";
    try
    {
        records       = Integer.parseInt((String)hash.get("records"));
        totalrecords  = Integer.parseInt((String)hash.get("totalrecords"));
    }
    catch(Exception ex)
    {

    }
    if(records>0)
    {
%>
  <form name="exportform" method="post" action="ExportWhitelistDetails?ctoken=<%=ctoken%>" >
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="merchantid">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>" name="accountid">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstSix)%>" name="firstsix">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastFour)%>" name="lastfour">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddress)%>" name="emailaddr">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(isApproved)%>" name="isApproved">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(isTemp)%>" name="isTemp">
    <button type="submit" class="button3" style="width:15%;margin-left:85% ;margin-top:0px"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
  </form>

  <form name="updateform" action="CommonWhitelist?ctoken=<%=ctoken%>" method="post" onsubmit="Update">
    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td width="4%" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
        <td valign="middle" align="center" class="th0">Memberid</td>
        <td valign="middle" align="center" class="th0">AccountID</td>
        <td valign="middle" align="center" class="th0">FirstSix</td>
        <td valign="middle" align="center" class="th0">LastFour</td>
        <td valign="middle" align="center" class="th0">Email Address</td>
        <td valign="middle" align="center" class="th0">Is Approved</td>
        <td valign="middle" align="center" class="th0">In Active</td>
<td valign="middle" align="center" class="th0">Action Executor Id</td>
        <td valign="middle" align="center" class="th0">Action Executor Name</td>
      </tr>
      </thead>
      <%
        String role       ="Admin";
        String username   =(String)session.getAttribute("username");
        String actionExecutorId   = (String)session.getAttribute("merchantid");
        String actionExecutorName = role+"-"+username;

        String style  = "class=td1";
        String ext    = "light";
        for(int pos = 1;pos <= records;pos++)
        {
          String id = Integer.toString(pos);
          int srno  = Integer.parseInt(id)+ ((pageno-1)*pagerecords);
          if(pos%2 == 0)
          {
            style = "class=tr0";
            ext   = "dark";
          }
          else
          {
            style ="class=tr1";
            ext   ="light";
          }
          temphash  =(Hashtable)hash.get(id);
          out.println("<tr>");
          String dbid = (String) temphash.get("id");
          if(functions.isValueNull((String)temphash.get("actionExecutorId")))
          {
            actionExecutorId=(String)temphash.get("actionExecutorId");
          }
          else
          {
            actionExecutorId="-";
          }
          if(functions.isValueNull((String)temphash.get("actionExecutorName")))
          {

            actionExecutorName=(String)temphash.get("actionExecutorName");
          }
          else{
            actionExecutorName="-";
          }
          String emailAddr = "-";

          if(functions.isValueNull((String)temphash.get("emailAddr"))){
            emailAddr = (String)temphash.get("emailAddr") ;
          }

          out.println("<tr>");
          out.println("<td align=center "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value=\""+dbid+"\"></td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("accountid"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("firstsix"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("lastfour"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(emailAddr)+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isApproved"))+"<input type=\"hidden\" name=\"isApproved\" value=\""+temphash.get("isApproved")+"\"></td>");
          //out.println("<td align=center "+style+">&nbsp;<select name=\"isTemp_"+dbid+"\"><option value=\"N\" default>N</option><option value=\"Y\" default>Y</option></select></td>");
          out.println("<td align=center "+style+">&nbsp;<select name=\"isTemp_"+dbid+"\">"+Functions.comboval((String)temphash.get("isTemp"))+"</select></td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorId)+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorName)+"</td>");
       out.println("</tr>");
        }
      %>
      <thead>
      <tr>
        <td class="th0" colspan="14">
          <center><button type="submit" class="addnewmember" value="Update" onclick="return Update()">Update</button></center>
        </td>
      </tr>
      </thead>
    </table>
  </form>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="WhiteListEmailDetails"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
    <%
    }
    else if(functions.isValueNull(errorMsg))
    {
      out.println(Functions.NewShowConfirmation("Result",errorMsg));
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry","No Records Found."));
    }
%>
</body>
</div>
<%
  }
  else
  {
    System.out.println("inside logout");
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
  }
  catch (Exception e)
  {
    e.printStackTrace();
  }
%>
</html>
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>
