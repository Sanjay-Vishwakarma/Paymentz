<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.manager.vo.FileDetailsVO" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.vo.applicationManagerVOs.AppFileDetailsVO" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 6/5/2017
  Time: 2:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title></title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>

  <script type="text/javascript" language="JavaScript">
    function check() {
      var  retpath = document.form1.file.value;
      var pos = retpath.lastIndexOf(".");
      var filename="";
      if (pos != -1)
        filename = retpath.substring(pos + 1);
      else
        filename = retpath;

      if (filename==('pdf')) {

        return true;
      }
      alert('Please select a .pdf file instead!');
      return false;

    }
  </script>
</head>

<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Bank Template File Upload
        <div style="float: right;">
          <form action="/icici/bankMapping.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" name="submit" class="addnewmember">
              <i class="fa fa-arrow-left"></i>
              &nbsp;&nbsp;Go Back
            </button>
          </form>
        </div>
        <%--<div style="float: right;">
          <form action="/icici/partnerBankMapping.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Charge" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Partner Bank Mapping
            </button>
          </form>
        </div>--%>
      </div>
      <br>
      <%
        Map<String,AppFileDetailsVO> fileDetailsVOMap = (Map<String,AppFileDetailsVO>) request.getAttribute("fileDetailsVOMap");
        AppFileDetailsVO fileDetailsVO = null;
        String errormsg = "";
        if (fileDetailsVOMap != null)
        {
          for (Map.Entry<String, AppFileDetailsVO> entry : fileDetailsVOMap.entrySet())
          {
            fileDetailsVO = entry.getValue();
            if (fileDetailsVO.isSuccess())
            {
              out.println("<table align=\"center\"><tr><td align=center ><font class=\"textb\" ><b>");
              out.println("</b></font></td></tr><tr><td algin=\"center\" >");
              out.println("<font class=\"textb\" ><b>");
              errormsg = errormsg + "File Uploaded Successfully";
              out.println(errormsg);
              out.println("</b></font></td></tr></table>");
            }
            else
            {
              out.println("<table align=\"center\"><tr><td align=center><font class=\"textb\" ><b>" +fileDetailsVO.getReasonOfFailure());
              out.println("</b></font></td></tr><tr><td algin=\"center\" >");
              out.println("</td></tr></table>");
            }
          }
        }
      %>
      <form action="/icici/servlet/AddNewBank?ctoken=<%=ctoken%>" name="form1" method="post" ENCTYPE="multipart/form-data">
        <table  align="center" width="70%" cellpadding="2" cellspacing="2">
          <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center" >
                <tr><td colspan="4">&nbsp;</td></tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Bank Name*</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <label>
                      <input type="text" id="bankname" name="bankname" maxlength="15" class="txtbox">
                    </label>
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb"><span class="textb">Bank Template File*</span><br>
                    (Ex. abc.pdf)</td>
                  <td class="textb">:</td>
                  <td class="textb">
                    <input  type="file" maxlength="30" value="Select pdf File" name= "<%--<%=request.getParameter("bankname")%>_--%>file" size="35"></td>
                </tr>

                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb"></td>
                  <td class="textb"></td>
                  <td>
                    <input id="submit" type="Submit" value="Upload" name="submit" class="buttonform" onclick="return check()">
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>

              </table>

            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
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
