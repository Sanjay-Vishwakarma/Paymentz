<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger,com.directi.pg.Merchants" %>
<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%!
    private static Logger log   = new Logger("payoutLimitAmount.jsp");
%>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

<script>

</script>
<script type="text/javascript">

</script>
<html>
<head>
</head>
<title> Settings> Payout Amount Limit </title>
<body class="bodybackground">
<%
    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Transaction Log
            </div>
        </div>
    </div>
</div>
<div id="form" class="reporttable">
    <%--<div class="panel-heading">Payout Amount Details</div>--%>
    <%
        LinkedList<String> fileNameList      = null;
        String errorMessage                 = "";
        Functions functions                 = new Functions();
        if(request.getAttribute("fileNameList") != null){
            fileNameList = (LinkedList<String>) request.getAttribute("fileNameList");
        }
        if(request.getAttribute("error") != null){
            errorMessage = (String)request.getAttribute("error");
        }

        if(functions.isValueNull(errorMessage))
        {
            out.println("<div class=\"bg-info\" style=\"text-align:center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+ errorMessage +"</div>");
        }
        if(fileNameList != null && fileNameList.size() > 0)
        {
    %>

    <div style="width:100%; overflow: auto">
        <table border="1" cellpadding="5" cellspacing="0" width="750" align="center" class="table table-striped table-bordered table-hover table-green dataTable" style="width: 100%;overflow: auto;">
            <thead>
            <tr>
                <td class="th0"><b>File Name</b></td>
                <td class="th0"><b>Action</b></td>
            </tr>
            </thead>
            <tbody>
                <%
                    for( String logFileName : fileNameList){
                        String logggerDisplayName ="";

                        if(logFileName.equalsIgnoreCase("transaction.log") || logFileName.equalsIgnoreCase("facilero.log")){
                            String dateStr          = formater.format(new Date()).toString();
                            logggerDisplayName      = logFileName+"."+dateStr;
                        }else{
                            logggerDisplayName = logFileName;
                        }
                %>
                <tr>
                <td align=center> <%=ESAPI.encoder().encodeForHTML(logggerDisplayName)%></td>
                <td align=center>
                    <form action="/icici/servlet/TransactionsLogs?ctoken=<%=ctoken%>" method="post">
                        <input type="hidden" name="fileName" id="fileName" value="<%=logFileName%>" class="txtbox">
                        <input type="hidden" name="action" id="action" value="download">
                        <button type="submit" id="updateSingleRecord" class="goto" name="action" value="updateSingleRecord"> Download </button>
                    </form>
                </td>
                </tr>
                <%
                    }
                %>

            </tbody>
        </table>
    </div>
    <%}%>
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
