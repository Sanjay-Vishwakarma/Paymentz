<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.*" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 20/7/15
  Time: 4:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud System Accounts</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    if(request.getAttribute("message")!=null)
    {
      out.println(request.getAttribute("message"));
    }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Fraud System Accounts
        <div style="float: right;">
          <form action="/icici/manageFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="New Fraud System Account" name="submit" class="addnewmember" style="width: 200px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;New Fraud System Account
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemAccountList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String str="ctoken=" + ctoken;
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

        %>
        <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4" height="20px">&nbsp;</td></tr>
                <tr>
                  <td width="1%" class="textb">&nbsp;</td>
                  <td width="9%" class="textb" align="center">Fraud System</td>
                  <td width="12%" class="textb">
                    <select name="fsid" class="txtbox" style="width: 200px;"><option value="" selected>ALL</option>
                      <%
                        Hashtable<String,String> fraudSystem = FraudSystemService.getFraudSystem();
                        Iterator it1 = fraudSystem.entrySet().iterator();
                        while (it1.hasNext())
                        {
                          Map.Entry pair = (Map.Entry)it1.next();
                          out.println("<option value=\""+pair.getKey()+"\">"+pair.getKey()+" - "+pair.getValue()+"</option>");
                        }
                      %>
                    </select>
                  </td>
                  <td width="10%" class="textb" align="center">Account Name</td>
                  <td width="12%" class="textb">
                    <input  maxlength="50" type="text" name="fsname"  value="" class="txtbox">
                  </td>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" align="right">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr><td colspan="4" height="20px">&nbsp;</td></tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="row" style="margin-top: 0px">
  <div class="col-lg-12">
    <div class="panel panel-default" style="margin-top: 0px">
      <div class="panel-heading" >
        Update Fraud System Account Configuration
      </div>
      <%
        Functions functions=new Functions();
        FraudSystemAccountVO accountVO = (FraudSystemAccountVO) request.getAttribute("accountDetails");
        StringBuffer updateMsg = (StringBuffer) request.getAttribute("updateMsg");
        if(accountVO != null)
        {
      %>
      <form action="/icici/servlet/ActionFraudSystemAccount?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="mappingid" value="<%=accountVO.getFraudSystemAccountId() %>">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud System ID</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="fsid" class="txtbox" value="<%=accountVO.getFraudSystemId()%>" disabled="true">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud System Account Id</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="fsaccountid" class="txtbox" value="<%=accountVO.getFraudSystemAccountId()%>" disabled="true">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud System Account Name</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="accountname" class="txtbox" value="<%=accountVO.getAccountName()%>" disabled="true">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb" >User Name</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="username" class="txtbox" maxlength="20" value="<%=accountVO.getUserName()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Password</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="pwd" class="txtbox" maxlength="20" value="<%=accountVO.getPassword()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Contact Name*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="contactname" class="txtbox" maxlength="100" value="<%=accountVO.getContactName()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Contact Email*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="contactemail" class="txtbox" maxlength="225" value="<%=accountVO.getContactEmail()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">isTest</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="isTest">
                      <% if(accountVO.getIsTest().equals("Y")){
                      %>
                      <option value="Y" selected> Y</option>
                      <option value="N"> N</option>
                      <%}else{%>
                      <option value="Y" > Y</option>
                      <option value="N" selected> N</option>
                      <%}%>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" class="buttonform" name="action" value="update">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Update
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </td>
          </tr>
          </tbody>
        </table>
      </form>

    </div>
  </div>
</div>
<%
      StringBuffer msg = (StringBuffer)request.getAttribute("msg");
      if(msg!=null && functions.isValueNull(msg.toString()))
      {
        out.println("<div class=\"reporttable\">");
        out.println(Functions.NewShowConfirmation("Result",msg.toString()));
        out.println("</div>");
      }
    }
    else if(updateMsg!=null && functions.isValueNull(updateMsg.toString()))
    {
      out.println(Functions.NewShowConfirmation("Result", updateMsg.toString()));
    }
    else
    {
      out.println("<div class=\"reportable\">");
      out.println(Functions.NewShowConfirmation("Result", "No Records Found."));
      out.println("</div>");
    }
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>
