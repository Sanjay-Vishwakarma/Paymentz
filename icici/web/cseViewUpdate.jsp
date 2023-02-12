<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css">

<%--
  Created by IntelliJ IDEA.
  User: saurabh
  Date: 2/12/14
  Time: 11:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title>CUSTOMER SUPPORT INFO</title>
    <script type="text/javascript">
        function validate()
        {
            var email=document.form.emailid.value;
            var contactnumber=document.form.contactNumber.value;
            var csContactNumber=document.form.phoneno.value;
            var csEmail=document.form.emailaddr.value;
            if(csContactNumber.length==0 ||csEmail.length==0)
            {
                var Mes="please fill in the Data required:-\n"
                if(csContactNumber.length==0)
                {
                    Mes=Mes+" Contact Number \n";
                }
                if(csEmail.length==0)
                {
                    Mes=Mes+" Email Id"
                }
                alert(Mes);
                return false;
            }
           var flag = false;
            var mes="Please Enter Updated :-\n";
            if(email==csEmail && contactnumber==csContactNumber)
            {
            if(email==csEmail)
            {
              mes=mes+" Email Id \n";
                flag=true;
            }


            if(contactnumber==csContactNumber)
            {
                mes=mes+" Contact Number\n";
                flag=true;
            }
            if(flag)
            {
                alert(mes);
                return false;
            }
            }
          return true;
        }
    </script>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Customer Support Executive Detail's
                <div style="float: right;">
                    <form action="/icici/cseSignup.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" value="Add New Customer Support" name="submit" class="addnewmember" style="width: 250px;">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Customer Support
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/SearchCSE?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">

                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Executive Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="Eid"  value="" class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Executive Name</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="Ename" class="txtbox" value="">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform" >
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
<form action="/icici/servlet/EditCSEDetails?ctoken=<%=ctoken%>" method="POST" onsubmit="return validate()" name="form"> <input type="hidden" name="ctoken" value="<%=ctoken%>">
    <table border="1" bordercolor="#ffffff" style="width:50% " align="center" class="table table-striped table-bordered  table-green dataTable">


        <%
            String mes=request.getParameter("MES");
            if(mes!=null)
            {
                Hashtable error=(Hashtable) request.getAttribute("error");

                if(error!=null && !error.isEmpty() )
                {

                    Enumeration enu = error.keys();
                    String key = "";
                    String value = "";
                    while (enu.hasMoreElements())
                    {
                        key = (String) enu.nextElement();
                        value = (String) error.get(key);
                        out.println("<center><li><font color=\"red\"><b>"+value+"</b></font></li></center>");
                    }
                }
            }
            Hashtable hash = (Hashtable) request.getAttribute("CSEinfo");
            boolean isreadonly = Boolean.parseBoolean(request.getAttribute("readonly").toString());
            String conf = null;
            if(isreadonly)
            {
                conf = "disabled";
            }
            else if(!isreadonly)
            {
                conf = "";
            }
            else{
                conf = "disabled";
            }

            Hashtable innerhash;
            int totalrecords=0;
            int records=0;
            try
            {


                records = Integer.parseInt(hash.get("records").toString());
                totalrecords=Integer.parseInt((hash.get("totalrecords").toString()));

            }
            catch (Exception ex)
            {

            }
            if (hash != null && hash.size() > 0)
            {
                String style="class=tr0";
                innerhash = (Hashtable) hash.get(""+1);

                String currentblock=request.getParameter("currentblock");
                String csId =Functions.checkStringNull(request.getParameter("csId"))==null?"":request.getParameter("csId");
                if(currentblock==null)
                    currentblock="1";
                String str="";
                str="csId="+csId+"&ctoken="+ctoken+"";
                int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),10);

        %>


        <tr <%=style%>>
            <td class="th0" colspan="2"><b>Customer Support EXEC Detail's:</b></td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Executive Id: </td>
            <td class="tr1"><input type="text" class="txtbox1"  name="Eid" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("csId"))%>" readonly="true"></td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Executive Name: </td>
            <td class="tr1"><input type="text" class="txtbox1"  name="csName" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("csName"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Executive Username: </td>
            <td class="tr1"><input type="text" class="txtbox1"  name="csLogin" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("csLogin"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Exeutive Email Id: </td>
            <td class="tr1"><input type="text" class="txtbox1"  name="emailaddr" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("csEmail"))%>" <%=conf%>> <input type="hidden" name="emailid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("csEmail"))%>">  </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Executive Contact No:</td>
            <td class="tr1"><input type="text" class="txtbox1"  name="phoneno" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("csContactNumber"))%>" <%=conf%>><input type="hidden" name="contactNumber" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("csContactNumber"))%>">  </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Executive Creation Date:</td>
            <td class="tr1"><input type="text" class="txtbox1"  name="csCreationDate" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("csCreationDate"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Executive Last Login:</td>
            <td class="tr1"><input type="text" class="txtbox1"  name="csLastLogin" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("csLastLogin"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1"></td>
            <td class="tr1" align="left">
                <%if(!isreadonly)
                {
                    out.println("<left><input type=\"submit\" class=\"gotopage\" Value=\"update\" name=\"submit\"></center>");
                }
                %>
                <%if(isreadonly)
                {
                %>

            </td>
        </tr>

    </table>

    <table align="center" style="width: 50%" class="table table-striped table-bordered table-green dataTable">

            <tr>
                <td  class="th0">
                    Login Date
                </td>
                <td class="th0">
                    Logout Date
                </td>
                <td class="th0">
                    Ip Address
                </td>
            </tr>



        <tbody>

        <%for(int i=1;i<=records;i++)
        {
            innerhash=(Hashtable)hash.get(""+i);
            String csLoginDetails,csLogoutDetails,csIPaddress;
            if(!innerhash.containsKey("csLoginDetails") || !innerhash.containsKey("csLogoutDetails") || !innerhash.containsKey("csIPaddress"))
            {
                csLoginDetails="N.A";
                csLogoutDetails="N.A";
                csIPaddress="N.A";
            }
            else{
                csLoginDetails=ESAPI.encoder().encodeForHTML( innerhash.get("csLoginDetails").toString());
                csLogoutDetails=ESAPI.encoder().encodeForHTML( innerhash.get("csLogoutDetails").toString());
                csIPaddress=ESAPI.encoder().encodeForHTML( innerhash.get("csIPaddress").toString());
            }
        %>
        <tr <%=style%>>

            <td class="tr1"><input type="text" class="txtbox" name="csLoginDetails" value="<%=csLoginDetails%>" <%=conf%>> </td>
            <td class="tr1"><input type="text" class="txtbox" name="csLogoutDetails" value="<%=csLogoutDetails%>" <%=conf%>> </td>
            <td class="tr1"><input type="text" class="txtbox" name="csIPaddress" value="<%=csIPaddress%>" <%=conf%>> </td>
        </tr>

        <%}%>
        </tbody>

    </table>
</form>
<table align=center valign=top><tr>
    <td align=center>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="CSEviewupdate"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </td>
</tr>
</table>
<% }

    //out.println(Functions.ShowMessage("Message","No record found"));
}
}
else
{
    response.sendRedirect("/icici/logout.jsp");
    return;
}
%>
</div>
</body>
</html>