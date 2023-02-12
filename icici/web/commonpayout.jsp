<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 8/31/2020
  Time: 6:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="index.jsp" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<html>
<head>
    <title>Common Integration> Common Payouts </title>
    <script type="text/javascript" language="JavaScript">
        $(function ()
        {

            $('#File').change(function ()
            {
                document.getElementById("Upload").disabled = false;
                var retpath = document.FIRCForm.File.value;
                var pos = retpath.indexOf(".");
                var filename = "";
                if (pos != -1)
                    filename = retpath.substring(pos + 1);
                else
                    filename = retpath;
                if (filename == ('xls'))
                {
                    var files = $('#File').get(0).files;
                    if (files.length > 0)
                    {
                        var file = files[0];

                        var fileReader = new FileReader();
                        fileReader.onloadend = function (e)
                        {
                            var arr = (new Uint8Array(e.target.result)).subarray(0, 4);
                            var header = '';
                            for (var i = 0; i < arr.length; i++)
                            {
                                header += arr[i].toString(16);
                            }

                            if (header != "d0cf11e0")
                            {
                                alert('Please select a valid .xls file instead!');
                                document.getElementById("Upload").disabled = true;
                            }

                        };
                        fileReader.readAsArrayBuffer(file);
                    }
                }
                else
                {
                    alert('Please select a .xls file instead!');
                    document.getElementById("Upload").disabled = true;
                }
            });

        });
    </script>
</head>
<body>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        //Hashtable accountHash = GatewayAccountService.getCommonAccountDetail();
        String memberid = Functions.checkStringNull(request.getParameter("memberid")) == null ? "" : request.getParameter("memberid");
        String terminalid = Functions.checkStringNull(request.getParameter("terminalid")) == null ? "" : request.getParameter("terminlid");

%>
<form name="FIRCForm" action="/icici/servlet/CommonPayout?ctoken=<%=ctoken%>" method="post"
      ENCTYPE="multipart/form-data">
    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">

    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Common Payout Upload
                </div>
                <br>
                <table border="0" cellpadding="5" cellspacing="0" align="center">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" align="center">
                                <tr>
                                    <td>&nbsp;</td>
                                    <td>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">
                                        Member ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
                                    </td>
                                    <td>
                                        <input name="memberid" id="midy" value="<%=memberid%>" class="txtbox"
                                               autocomplete="on">
                                    </td>
                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                    <td>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">
                                        Terminal ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
                                    </td>
                                    <td>
                                        <input name="terminalid" id="tid" value="<%=terminalid%>" class="txtbox"
                                               autocomplete="on">
                                    </td>
                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                    <td>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">
                                        Payout Upload File :&nbsp;&nbsp;&nbsp;
                                    </td>
                                    <td>
                                        <input name="File" type="file" id="File" value="choose File">
                                    </td>
                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td colspan="2" align="right">
                                        <button name="mybutton" type="submit" value="Upload" id="Upload"
                                                class="buttonform">Upload
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                    <td>

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
<%
    String Msg = (String) request.getAttribute("msg");
    String Result = (String) request.getAttribute("Result");
    Functions functions = new Functions();
    if(functions.isValueNull(Result)&& functions.isValueNull(Result)){
%>
<div class="reporttable">

    <table align="center" width="80%" cellpadding="2" cellspacing="2" ><tr><td>

        <table bgcolor="#ecf0f1" width="100%" align="center" cellpadding="0" cellspacing="0">

            <tr height=30>
                <td colspan="3" bgcolor="#34495e"  class="texthead" align="center"><font color="#FFFFFF" size="2" face="Open Sans,Helvetica Neue,Helvetica,Arial,Palatino Linotype', 'Book Antiqua, Palatino, serif">  <%=Msg%></font></td>
            </tr>

            <tr><td>&nbsp;</td></tr>

            <tr><td align="center" class="textb"><%=Result%></td></tr>
            <tr><td>&nbsp;</td></tr>
        </table> </tr></td> </table>
</div>
<%
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