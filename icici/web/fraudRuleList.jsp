<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page import="com.directi.pg.Logger" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 13/7/15
  Time: 12:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private static Logger logger=new Logger("fraudRuleList.jsp");
%>
<%
  String ruleid =Functions.checkStringNull(request.getParameter("ruleid"));
  if (ruleid == null){ruleid = "";}

  String rulename = Functions.checkStringNull(request.getParameter("rulename"));
  if (rulename == null){rulename = "";}

  String rulegroup = Functions.checkStringNull(request.getParameter("rulegroup"));
  if (rulegroup == null)rulegroup = "";

  String score = Functions.checkStringNull(request.getParameter("score"));
  if (score == null)score = "";

  String str="ctoken=" + ctoken;

  if (ruleid != null){str = str + "&ruleid=" + ruleid;}
  if (rulename != null){str = str + "&rulename=" + rulename;}
  if (rulegroup != null){str = str + "&rulegroup=" + rulegroup;}
  if (score !=null){str = str + "&score=" + score;}

%>

<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud Rules> Fraud Rule Master</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    if(request.getAttribute("statusMsg")!=null)
    {
      out.println(request.getAttribute("statusMsg"));
    }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Fraud Rule Master
        <div style="float: right;">
          <form action="/icici/addNewFraudRule.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Fraud Rule" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Fraud Rule
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudRuleList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <%

          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Rule ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="15" type="text" name="ruleid"  value=""  class="txtbox">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Rule Name</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="50" type="text" name="rulename"  value="" class="txtbox">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Rule Group</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="rulegroup" size="1" class="txtbox">
                      <option value="" selected="">ALL</option>
                      <option value="HardCoded">HardCoded</option>
                      <option value="Dynamic">Dynamic</option>
                      <option value="Internal">Internal</option>
                      <option value="Other">Other</option>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Score</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="2" type="text" name="score"  value="" class="txtbox">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <td width="4%" class="textb">&nbsp;</td>
                <td width="8%" class="textb" ></td>
                <td width="3%" class="textb"></td>
                <td width="10%" class="textb">
                <td width="4%" class="textb">&nbsp;</td>
                <td width="8%" class="textb" ></td>
                <td width="3%" class="textb"></td>
                <td width="10%" class="textb">
                  <button type="submit" class="buttonform">
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;Search
                  </button>
                </td>
                </td>
                </tr>
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
  <%
    Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;

    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
      out.println(error);
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
    if(records>0)
    {
  %>
  <div id="containrecord"></div>
  <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="4%" class="th0">Sr No</td>
      <td width="5%"  class="th0">Rule ID</td>
      <td width="19%" class="th1">Rule Name</td>
      <td width="13%" class="th1">Rule Description</td>
      <td width="7%" class="th0">Rule Group</td>
      <td width="4%" class="th1">Score</td>
      <td width="5%" class="th1">Status</td>
      <td width="5%" class="th1">Action</td>
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
            out.println("<tr>");
            out.println("<td align=\"center\" "+style+">"+srno+ "</td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("ruleid"))+"</td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("rulename"))+"</td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("ruledescription"))+"</td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("rulegroup"))+"</td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("score"))+"<input type=\"hidden\" name=\"memberid\"value=\""+temphash.get("score")+"\"></td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
            out.println("<td align=\"center\" "+style+"><form action=\"/icici/servlet/ActionFraudRuleList?ctoken="+ctoken+"\" method=\"post\">" +
             "<input type=\"hidden\" name=\"ruleid\" value=\""+temphash.get("ruleid")+"\">" +
              "<input type=\"hidden\" name=\"action\" value=\"modify\">" +
               "<input type=\"submit\" class=\"gotoauto\" value=\"Modify\"></form></td>");
            out.println("</tr>");
        }
    %>
    <table align=center valign=top><tr>
      <td align=center>
        <jsp:include page="page.jsp" flush="true">
          <jsp:param name="numrecords" value="<%=totalrecords%>"/>
          <jsp:param name="numrows" value="<%=pagerecords%>"/>
          <jsp:param name="pageno" value="<%=pageno%>"/>
          <jsp:param name="str" value="<%=str%>"/>
          <jsp:param name="page" value="FraudRuleList"/>
          <jsp:param name="currentblock" value="<%=currentblock%>"/>
          <jsp:param name="orderby" value=""/>
        </jsp:include>
      </td>
    </tr>
    </table>

      <%
      }
      else
      {
         out.println(Functions.NewShowConfirmation("Result", "No Records Found."));
      }
  %>
</div>

<%
  }
%>

</body>
</html>
