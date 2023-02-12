<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/26/13
  Time: 4:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("trackingid");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }

        function DoReverse()
        {
            var checkboxes = document.getElementsByName("trackingid");
            var total_boxes = checkboxes.length;
            flag = false;

            for(i=0; i<total_boxes; i++ )
            {
                if(checkboxes[i].checked)
                {
                    flag= true;
                    break;
                }
            }

            if(!flag)
            {
                alert("Select at least one transaction for Reconcilation.");
                return false;
            }

            if (confirm("Do you really want to process all selected transaction."))
            {
                document.reversalform.submit();
            }

        }

    </script>
</head>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        Hashtable gatewayHash = GatewayTypeService.getCommonGatewayTypes();
        Hashtable accountidhash= GatewayAccountService.getCommonAccountDetails();
        String accountid = Functions.checkStringNull(request.getParameter("accountid"));
        String gateway = Functions.checkStringNull(request.getParameter("gateway"));
        Set<String> gatewaySet = new HashSet<String>();
        if (gateway == null)
            gateway = "";

        String str="";
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;

        try
        {
            fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
        }
        catch(ValidationException e)
        {

        }
        Calendar rightNow = Calendar.getInstance();
        String currentyear= "" + rightNow.get(rightNow.YEAR);
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
        str = str + "ctoken=" + ctoken;
        if (fdate != null) str = str + "&fdate=" + fdate;
        if (tdate != null) str = str + "&tdate=" + tdate;
        if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;
        String trackingid=request.getParameter("trackingid");
        String paymentid=request.getParameter("paymentid");
        String reqaccountid=request.getParameter("accountid");
        String toid=request.getParameter("toid");
        if(trackingid!=null)
        {
            str = str + "&trackingid="+trackingid;
        }
        if(paymentid!=null)
        {
            str = str + "&paymentid="+paymentid;
        }
        if(reqaccountid!=null)
        {
            str = str + "&accountid="+reqaccountid;
        }
        if(toid!=null)
        {
            str = str + "&toid="+toid;
        }
        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

%>
<body>
<center><h2>Reconcilation</h2></center>
<br>
<form action="/icici/servlet/CommonReconcilationList?ctoken=<%=ctoken%>" method="post" name="forms" >

    <table border="1" class="search" align=center  cellpadding="2" cellspacing="0" width="50%" bgcolor="CCE0FF" >
        <tr><td>
            <table class="search" align=center  cellpadding="2" cellspacing="0"  bgcolor="CCE0FF" width="100%">

                <tr>
                    <td width="692" bgcolor="#2379A5" colspan="3" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Common Reconcilation</b></font></td>
                </tr>
                <tr>
                    <td align="left" class="label">
                        <font color="black"> PaymentID:</font> <input maxlength="15" type="text" name="paymentid"  value="<%=request.getParameter("paymentid")==null?"":request.getParameter("paymentid")%>">
                    </td>
                    <td align="left" class="label">
                        <font color="black"> TrackingID:</font> <input maxlength="15" type="text" name="trackingid"  value="<%=request.getParameter("trackingid")==null?"":request.getParameter("trackingid")%>">
                    </td>
                </tr>
                <tr>
                    <td valign="middle" align="left"     class="label">
                        <font color="black"> From:</font><select size="1" name="fdate" class="textBoxes" value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                        <%
                            if (fdate != null)
                                out.println(Functions.dayoptions(1, 31, fdate));
                            else
                                out.println(Functions.printoptions(1, 31));
                        %>
                    </select>
                        <select size="1" name="fmonth" class="textBoxes" value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
                            <%
                                if (fmonth != null)
                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                else
                                    out.println(Functions.printoptions(1, 12));
                            %>
                        </select>
                        <select size="1" name="fyear" class="textBoxes" value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
                            <%
                                if (fyear != null)
                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                                else
                                    out.println(Functions.printoptions(2005, 2020));
                            %>
                        </select>
                        <%--<td valign="middle" align="right"  class="label"></td>--%>
                    <td valign="left" align="center" class="label">
                        <font color="black"> To:</font> <select size="1" name="tdate" class="textBoxes">

                        <%
                            if (tdate != null)
                                out.println(Functions.dayoptions(1, 31, tdate));
                            else
                                out.println(Functions.printoptions(1, 31));
                        %>
                    </select>

                        <select size="1" name="tmonth" class="textBoxes">

                            <%
                                if (tmonth != null)
                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                else
                                    out.println(Functions.printoptions(1, 12));
                            %>
                        </select>

                        <select size="1" name="tyear" class="textBoxes">

                            <%
                                if (tyear != null)
                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                else
                                    out.println(Functions.printoptions(2005, 2020));
                            %>
                        </select>

                    </td>

                </tr>
                <tr>
                    <td align="left" class="label">
                        <font color="black">
                            Account ID
                            <select size="1" name="accountid" class="textBoxes">
                                <option value="">Please Select</option>
                                <%
                                    Enumeration enu3 = accountidhash.keys();
                                    String selected3 = "";
                                    String key3 = "";
                                    String value3 = "";


                                    while (enu3.hasMoreElements())
                                    {
                                        key3 = (String) enu3.nextElement();
                                        value3 = (String) accountidhash.get(key3);


                                        if (key3.equals(accountid))
                                            selected3 = "selected";
                                        else
                                            selected3 = "";

                                %>
                                <option value="<%=key3%>" <%=selected3%>><%=value3%></option>
                                <%
                                    }
                                %>
                            </select>
                        </font>
                    </td>
                    <td align="left" class="label">

                    </td>
                </tr>
                <tr>

                    <td align="center" class="label" colspan="2">
                        <input type="submit" value="Search">
                    </td>
                </tr>
            </table>
        </td></tr>
    </table>
</form>
</body>
<%  String error=(String ) request.getAttribute("error");
    if(error !=null)
    {
        out.println("<BR>");
        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" ><b>");
        out.println(error);
        out.println("</b></font></td></tr></table>");
        out.println("<BR>");
    }

    String reconmessage = (String)request.getAttribute("cbmessage");

    if (reconmessage != null && !reconmessage.equals(""))
    {
        out.println(Functions.ShowMessage("Message",reconmessage));
    }

    Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    Hashtable temphash=null;
    String currentblock=request.getParameter("currentblock");
    int records=0;
    int totalrecords=0;

    if(currentblock==null)
        currentblock="1";

    try
    {
        records=Integer.parseInt((String)hash.get("records"));
        totalrecords=Integer.parseInt((String)hash.get("totalrecords"));

    }
    catch(Exception ex)
    {

    }
    if(records>0)
    {
%>
<form name="form" action="/icici/servlet/CommonReconcilationProcess?ctoken=<%=ctoken%>" method="post">

    <input type="hidden" name="accountid" value=<%=accountid%>>
    <table align=center width="50%">
        <tr>
            <td valign="middle" align="center" bgcolor="#008BBA"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
            <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
            <td valign="middle" align="center" bgcolor="#008BBA">TrackingID</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Fromid</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Toid</td>
            <td valign="middle" align="center" bgcolor="#008BBA">PaymentID</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Description</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Amount</td>
            <td valign="middle" align="center" bgcolor="#008BBA">status</td>
            <td valign="middle" align="center" bgcolor="#008BBA">TimeStamp</td>


        </tr>
        <%
            String style="class=td1";
            String ext="light";
            for(int pos=1;pos<=records;pos++)
            {
                String id=Integer.toString(pos);

                int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

                if(pos%2==0)
                {
                    style="class=td2";
                    ext="dark";
                }
                else
                {
                    style="class=td1";
                    ext="light";
                }
                temphash=(Hashtable)hash.get(id);
                out.println("<tr>");
                out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
                out.println("<td "+style+">&nbsp;"+srno+ "</td>");
                out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fromid"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"<input type=\"hidden\" name=\"toid_"+temphash.get("trackingid")+"\" value=\""+temphash.get("toid")+"\" ></td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("paymentid"))+"<input type=\"hidden\" name=\"paymentid_"+temphash.get("trackingid")+"\" value=\""+temphash.get("paymentid")+"\"></td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"<input type=\"hidden\" name=\"description_"+temphash.get("trackingid")+"\" value=\""+temphash.get("description")+"\"></td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"<input type=\"hidden\" name=\"amount_"+temphash.get("trackingid")+"\" value=\""+temphash.get("amount")+"\" ></td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"<input type=\"hidden\" name=\"status_"+temphash.get("trackingid")+"\" value=\""+temphash.get("status")+"\"></td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
                out.println("</tr>");

            }
        %>

        <tr><td bgcolor="#008BBA" colspan="14"><center><input type="submit" value="PROCESS" onClick="return DoReverse();"></center></td></tr>
    </table>

</form>
<%
    }
    else
    {
        out.println(Functions.ShowMessage("Sorry","No record Found"));
    }
%>
<BR>
<table align=center valign=top><tr>
    <td align=center>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="CommonReconcilationList"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </td>
</tr>
</table>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</html>