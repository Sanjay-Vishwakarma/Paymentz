
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.function.Function" %>
<%@ page import="com.payment.FrickBank.core.message.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="blocktab.jsp"%>
<html>
<head>
    <title> Blocked Card List</title>
    <script>
        function ToggleAll(checkbox)
        {
            flag=checkbox.checked;
            var checkboxes = document.getElementsByName("id");
            var total_boxes = checkboxes.length;
            for(i=0;i<total_boxes;i++)
            {
                checkboxes[i].checked=flag;
            }
        }
        function Unblock()
        {
            var checkboxes=document.getElementsByName("id");
            var total_boxes=checkboxes.length;
            flag=false;
            for(i=0;i<total_boxes;i++)
            {
                if(checkboxes[i].checked)
                {
                    flag=true;
                    break;
                }
            }
            if(!flag)
            {
                alert("Select at least one record");
                return false;
            }
            if (confirm("Do you really want to unblock all selected Data."))
            {
                document.BlackListDetails.submit();
            }
        }
    </script>
        </head>

<%
    session.setAttribute("submit","Block Cards");
    if(buttonvalue=null)
    {
        buttonvalue=(String)session.getAttribute("submit");
    }
    ctoken=((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>
<body>
<div class ="row" style="margin-top:0px">
    <div class ="col-lg-12">
        <div class ="panel panel-default" style="margin-top: 0px">
            <div class ="panel-heading">
                BLACKLISTED CARD DETAILS
                <div style="float:right;">
                    <form action="/icici/uploadBlacklistCardDetails.jsp?ctoken"
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>