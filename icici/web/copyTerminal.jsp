<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger,com.directi.pg.Merchants" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ include file="index.jsp" %>
<%!
    private static Logger log   = new Logger("copyTerminal.jsp");
%>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

<script>
    function validateInput(){

        var error           = "";
        var memberid        =  document.getElementById("mid").value;
        var terminalId      =  document.getElementById("terminalid").value;

        console.log("memberid ",+memberid);
        console.log("terminalId ",+terminalId);

        if(memberid == ""){
            error += "Please Enter Member Id \n";
        }

        if(terminalId == ""){
            error += "Please Enter Teminal Id \n";
        }

        console.log("validateInput "+error);
        if (error != "")
        {
            alert(error);
            return false;
        }
        else
        {
            document.getElementById("copyTerminal").submit();
        }
    }
</script>
<html>
<head>
</head>
<title> Copy Terminal </title>
<body class="bodybackground">
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Copy Terminal
            </div>
            <%
                ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                Merchants merchants = new Merchants();
                if (merchants.isLoggedIn(session))
                {

                    String memberId         = Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
                    String pgtypeid         = "";
                    String terminalId       = "";

                    if(request.getParameter("pgtypeid") != null){
                        pgtypeid = request.getParameter("pgtypeid");
                    }
                    if(request.getParameter("memberId") != null ){
                        memberId     =  request.getParameter("memberId");
                    }
                    if(request.getParameter("terminalid") != null){
                        terminalId    =   request.getParameter("terminalid");
                    }

            %>
            <form id="copyTerminal" action="/icici/servlet/CopyTerminal?ctoken=<%=ctoken%>" name="copyTerminal" method="post">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <br>
                <table align="center" width="65%" cellpadding="2" cellspacing="2">
                    <tbody>
                    <tr>
                        <td>
                            <%
                                String errormsg1 = (String) request.getAttribute("error");
                                if (errormsg1 != null)
                                {
                                    out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                                }
                                String errormsg = (String) request.getAttribute("cbmessage");
                                if (errormsg == null)
                                    errormsg = "";
                                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                                out.println(errormsg);
                                out.println("</b></font></td></tr></table>");
                            %>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Member ID*</td>
                                    <td width="5%" class="textb">:</td>
                                    <td>
                                        <input name="memberid" id="mid" value="<%=memberId%>" class="txtbox" autocomplete="on" >
                                    </td>
                                </tr>
                                <tr><td>&nbsp;&nbsp;</td></tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Gateway</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <input name="pgtypeid" id="pgtypeid" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                    </td>
                                </tr>
                                <tr><td>&nbsp;&nbsp;</td></tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Terminal ID*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <input name="terminalid" id="terminalid" value="<%=terminalId%>" class="txtbox" autocomplete="on">
                                    </td>
                                </tr>
                                <tr><td>&nbsp;&nbsp;</td></tr>
                                <tr>
                                    <td>&nbsp;&nbsp;</td>
                                    <td>&nbsp;&nbsp;</td>
                                    <td>
                                        <button align=center type="button" onclick="validateInput()" value="CREATE" class="buttonform">
                                            CREATE
                                        </button>
                                    </td>
                                </tr>
                                <tr><td>&nbsp;&nbsp;</td></tr>
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

<%--<br>
<%
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
        out.println("</div>");
    }
%>--%>
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
