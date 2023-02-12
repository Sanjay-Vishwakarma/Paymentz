<%--
  Created by IntelliJ IDEA.
  User: Vivek
  Date: 10/24/2018
  Time: 12:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="index.jsp" %>

<html>
<head>
  <title></title>\
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
  <script type="text/javascript">
    function goBack()
    {
      document.location.href = "/whitelistdetails.jsp";
    }
  </script>
  <script type="text/javascript" language="JavaScript">
    function check() {
      var  retpath = document.FIRCForm.File.value;
      var pos = retpath.lastIndexOf(".");
      var filename="";
      if (pos != -1)
        filename = retpath.substring(pos + 1);
      else
        filename = retpath;

      if (filename==('xls')) {

        return true;
      }
      alert('Please select a .xls file instead!');
      return false;

    }
  </script>
  <script type="text/javascript" language="JavaScript" src="/icici/javascript/Gateway.js"></script>
</head>
<body align="center">
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String memberId = nullToStr(request.getParameter("toid"));
    String pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Blacklist Bin File Upload
        <div style="float: right;">
          <form action="/icici/binBlacklist.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
            <button type="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Block Bin list
            </button>
          </form>
        </div>
      </div>
      <br>
      <form name = "FIRCForm" action="/icici/servlet/UploadBlacklistBinFile?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">CardDetails File</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input name="File" type="file" value="">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Account/Gateway</td>
                  <td class="textb">:</td>
                  <td>
                    <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Account ID*</td>
                  <td class="textb">:</td>
                  <td>
                    <input name="accountid" id="accountid1" value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Member ID*</td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb">
                    <input name="toid" id="memberid1" value="<%=memberId%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb">
                    <button name="mybutton" type="submit" value="Upload"  onclick="return check()"class="buttonform">Upload</button>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
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
<div class="reporttable">

  <%
    Functions functions = new Functions();
    if(functions.isValueNull(String.valueOf(request.getAttribute("sSuccessMessage"))) || functions.isValueNull(String.valueOf(request.getAttribute("sErrorMessage"))))
    {
      if(functions.isValueNull(String.valueOf(request.getAttribute("sSuccessMessage"))))
      {
        String successMessage = request.getAttribute("sSuccessMessage").toString();
        out.println(Functions.NewShowConfirmation("Success Updating", successMessage));
      }
      if(functions.isValueNull(String.valueOf(request.getAttribute("sErrorMessage"))))
      {
        String errorMessage = request.getAttribute("sErrorMessage").toString();
        out.println(Functions.NewShowConfirmation("Failed Updating", errorMessage));
      }
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Filter","please upload the Blacklist bin details File."));
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
%>
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