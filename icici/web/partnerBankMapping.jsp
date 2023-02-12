<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.manager.ApplicationManager"%>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 6/8/2017
  Time: 10:54 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Logger logger=new Logger("addBankPartnerMapping.jsp");
  Functions functions= new Functions();
%>
<html>
<head><title> Partner Bank Mapping</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
  <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>
</head>
<body>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Partner Bank Mapping
        <div style="float: right;">
          <form action="/icici/bankMapping.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New Gateway" name="submit">
              <i class="fa fa-arrow-left"></i>
              &nbsp;&nbsp;Go Back
            </button>
          </form>
        </div>
        <div style="float: right;">
          <form action="/icici/addNewBank.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New Gateway" name="submit">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Bank Template
            </button>
          </form>
        </div>
      </div>
      <%
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (!com.directi.pg.Admin.isLoggedIn(session))
        {
          response.sendRedirect("/icici/logout.jsp");
          return;
        }
        ApplicationManager applicationManager = new ApplicationManager();
        List<String> bankDetailList = applicationManager.getListOfBankId();
        List<String> partnerDetailsList = applicationManager.loadPartnerId();
      %>
      <form name="addtype" action="/icici/servlet/PartnerBankMapping?ctoken=<%=ctoken%>" method="post" name="form1">
        <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr>
                  <td colspan="4"></td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Bank Id *</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="bankId" class="txtbox">
                      <option value=""></option>
                      <%
                        for(String bankDetails : bankDetailList)
                        {
                          if(bankDetails != null)
                          {
                      %>
                      <option value="<%=bankDetails%>"> <%=bankDetails%></option>
                      <%
                          }
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Partner Id *</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px" class="textb">
                    <input name="partnerid" class="txtbox" id="allpid">
                    <%--<select name="partnerid" class="txtbox">
                      <option value=""></option>
                      <%
                        for (String partnerDetails : partnerDetailsList)
                        {
                          if (partnerDetails != null)
                          {
                      %>
                      <option value="<%=partnerDetails%>"> <%=partnerDetails%></option>
                      <%
                          }
                        }
                      %>
                    </select>--%>
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" class="buttonform" value="Save" style="width:150px ">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Save
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
  if(functions.isValueNull((String)request.getAttribute("error")))
  {
    out.println("<div class=\"reporttable\">");
    out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("error")));
    out.println("</div>");
  }
%>
</body>
</html>
