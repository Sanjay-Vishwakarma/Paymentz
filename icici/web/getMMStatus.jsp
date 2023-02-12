<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 11/8/12
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <title></title>
</head>
<body>
    <% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<center><h2>MyMonedero Interface </h2></center>
<br>
<center><a href="/icici/myMonederoActionHistory.jsp?ctoken=<%=ctoken%>">Action History</a> &nbsp;&nbsp;&nbsp;<b>Get Status</b>

    <%
        String searchtype=(String) request.getAttribute("searchtype");
        if(searchtype ==null)
        {
        searchtype = "trackingid";
        }
        
        String def1="",def2="";
        if(searchtype.equals("trackingid"))
        {
        def1="selected";
        def2="";
        }
        else
        {
        def1="";
        def2="selected";
        }
        
    %>
    <form action="/icici/servlet/GetMMStatus?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="50%" bordercolorlight="#000000" bordercolordark="#FFFFFF">

            <tr>
                <td width="692" bgcolor="#2379A5" colspan="5" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Get Status At MyMonedero</b></font></td>
            </tr>
            <tr>
              
                <td><input type=radio value="trackingid" name="searchtype" <%=def1%>> </td>
                <td colspan=4>
                    <input type=text name=trackingid size="20"> : Tracking Id
                </td>
            </tr>
            <tr>
             <td colspan=5 >&nbsp;</td>
            </tr>
            <tr>
                <td><input type=radio value="details" name="searchtype" <%=def2%>> </td>
                <td><input type="text" size="20" name="wctxnid" > : WC-TXNID</td>
                <td><input type="text" size="4" name="currency" > : Currency</td>
                <td><input type="text" size="20" name="amount" > : Amount( Upto 2 Decimal places)</td>
                <td><input type="text" size="4" name="accountid" > : Account ID</td>

            </tr>
            <tr>
                <td colspan="5" align=center> <input type="submit" value="Submit"> </td>
            </tr>
            
        
        </table>

    </form>


    <%
Logger log=new Logger("jsp");
String error=(String ) request.getAttribute("error");
if(error !=null)
{
out.println(error);
}
Hashtable temp=(Hashtable) request.getAttribute("details");
       try{

        if(temp!=null){

        %>
    <table align=center border="1" cellpadding="2" cellspacing="0" width="50%" bordercolorlight="#880000" bordercolordark="#FF0000">

        <tr>
            <td width="692" bgcolor="#2379A5" colspan="4" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Tracking ID : <%=temp.get("trackingid")%></b></font></td>
        </tr>
        <tr>
            <td width="692" bgcolor="#2379A5" colspan="4" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>WC - TXN ID : <%=temp.get("wctxnid")%></b></font></td>
        </tr>
        <tr>
            <td width="692" bgcolor="#2379A5" colspan="4" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Source ID : <%=temp.get("sourceid")%></b></font></td>
        </tr>
        <tr>
            <td width="692" bgcolor="#2379A5" colspan="4" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Destination ID : <%=temp.get("destid")%></b></font></td>
        </tr>
        <tr>
            <td width="692" bgcolor="#2379A5" colspan="4" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Error : <%=temp.get("error")%></b></font></td>
        </tr>
        <tr>
            <td width="692" bgcolor="#2379A5" colspan="4" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Transaction Date : <%=temp.get("date")%></b></font></td>
        </tr>
        </table>


        <%

                       }

             }catch(Exception e)
             {
             log.error("EXCEPTION OCCURED",e);
             }
             

    %>


    
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