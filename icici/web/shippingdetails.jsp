<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Calendar" %>
<%@ include file="index.jsp"%>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 3/4/14
  Time: 12:48 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Customer Support> Shipping Details </title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<%
    String memberid=nullToStr(request.getParameter("toid"));
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        //Hashtable gatewayHash = GatewayTypeService.getGatewayTypesShipping();
        Hashtable gatewayHash = GatewayTypeService.getGatewayTypes();

        String gateway = Functions.checkStringNull(request.getParameter("gateway"));
        if (gateway == null)
            gateway = "";

        String trackingid = Functions.checkStringNull(request.getParameter("trackingid"));
        if (trackingid == null)
            trackingid = "";

        String toid = Functions.checkStringNull(request.getParameter("toid"));
        if (toid == null)
            toid = "";

        String desc = Functions.checkStringNull(request.getParameter("desc"));
        if (desc == null)
            desc = "";

        String fromid = Functions.checkStringNull(request.getParameter("fromid"));
        if (fromid == null)
            fromid = "";

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
        if (tyear != null) str = str + "&trackingid=" + trackingid;
        if (tyear != null) str = str + "&toid=" + toid;
        if (tyear != null) str = str + "&fromid=" + fromid;
        str = str + "&gateway=" + gateway;
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Shipping Details
                <div style="float: right;">
                    <form action="/icici/editshippingdetails.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" value="Edit shipping details" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Edit shipping details
                        </button>
                    </form>
                </div>
            </div><br><br>
            <form action="/icici/servlet/ShippingDetailList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >From</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="fdate" value="" >
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" value="" >
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" value="" >
                                            <%
                                                if (fyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="tdate" >

                                            <%
                                                if (tdate != null)
                                                    out.println(Functions.dayoptions(1, 31, tdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>

                                        <select size="1" name="tmonth" >

                                            <%
                                                if (tmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>

                                        <select size="1" name="tyear" >

                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" for="mid">MemberID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input name="toid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">

                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Tracking ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="trackingid" class="txtbox" value="<%=request.getParameter("trackingid")==null?"":request.getParameter("trackingid")%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">From id</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="fromid" class="txtbox" value="<%=request.getParameter("fromid")==null?"":request.getParameter("fromid")%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Description</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="description" class="txtbox" value="<%=request.getParameter("description")==null?"":request.getParameter("description")%>">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" colspan="2">Gateway</td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="gateway" class="txtbox" style="width: 145px">
                                            <option value="">All</option>
                                            <%
                                                Enumeration enu2 =gatewayHash.keys();
                                                String selected2 = "";
                                                String key2 = "";
                                                String value2 = "";


                                                while (enu2.hasMoreElements())
                                                {
                                                    key2 = (String) enu2.nextElement();
                                                    value2 = (String) gatewayHash.get(key2);

                                                    if (key2.equals(gateway))
                                                        selected2 = "selected";
                                                    else
                                                        selected2 = "";

                                            %>
                                            <option value="<%=key2%>" <%=selected2%>><%=value2%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb"><button type="submit" class="buttonform">
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Search
                                    </button>

                                    </td>

                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

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
    <%  String errormsg1 = (String)request.getAttribute("message");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
            out.println(errormsg1);
            out.println("</font></td></tr></table>");
        }
    %>
    <%
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");

        Hashtable temphash=null;

        String currentblock=request.getParameter("currentblock");
        int records=0;
        int totalrecords=0;

        if(currentblock==null)
            currentblock="1";

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
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
    <center>
        <form name="exportform" method="post" action="ExportShippingTransaction?ctoken=<%=ctoken%>" >

            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
            <input type="hidden" value="<%=fromid%>" name="fromid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toid)%>" name="toid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(gateway)%>" name="gateway">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fdate">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="tdate">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">
            <input type="hidden" value="<%=ctoken%>" name="ctoken">
            <input type="submit" name="submit" value="Export to Excel Shipping Details" onclick="javascript:document.exportform.submit();">
            <%--<a href="javascript:document.exportform.submit();" ><font size="2">Export to Excel Shipping Details</font></a>--%>
        </form>
    </center>
    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr no</td>
            <td valign="middle" align="center" class="th0">TrackingID</td>
            <td valign="middle" align="center" class="th0">Fromid</td>
            <td valign="middle" align="center" class="th0">Toid</td>
            <td valign="middle" align="center" class="th0">Description</td>
            <td valign="middle" align="center" class="th0">Amount</td>
            <td valign="middle" align="center" class="th0">status</td>
            <td valign="middle" align="center" class="th0">Date</td>
            <td valign="middle" align="center" class="th0">CardHolder Name</td>
            <td valign="middle" align="center" class="th0">Paymentid</td>
            <td valign="middle" align="center" class="th0">POD</td>
            <td valign="middle" align="center" class="th0">Site Address</td>
        </tr>
        </thead>
        <%
            String style="class=td1";
            String ext="light";
            for(int pos=1;pos<=records;pos++)
            {
                String id=Integer.toString(pos);

                int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

                if(pos%2==0)
                {
                    style="class=tr0";
                    ext="dark";
                }
                else
                {
                    style="class=tr1";
                    ext="light";
                }
                temphash=(Hashtable)hash.get(id);
                out.println("<tr>");
                out.println("<td "+style+">&nbsp;"+srno+ "</td>");
                out.println("<td "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fromid"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"<input type=\"hidden\" name=\"toid_"+temphash.get("trackingid")+"\" value=\""+temphash.get("toid")+"\" ></td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"<input type=\"hidden\" name=\"description_"+temphash.get("trackingid")+"\" value=\""+temphash.get("description")+"\"></td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"<input type=\"hidden\" name=\"amount_"+temphash.get("trackingid")+"\" value=\""+temphash.get("amount")+"\" ></td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("dt"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("name"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("paymentid"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("pod"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("podbatch"))+"</td>");
                out.println("</tr>");

            }
        %>
        <tr><td class="th0" colspan="14"></td></tr>
    </table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="ShippingDetailList"/>
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
            out.println(Functions.NewShowConfirmation("Sorry", "No record Found"));
        }
    %>
    <BR>

    <%
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
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>
