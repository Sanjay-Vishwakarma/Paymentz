<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ include file="functions.jsp"%>

<%@ include file="ecoretab.jsp"%>
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
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title></title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<%
    session.setAttribute("submit","Inquiry");
%>
<form action="/icici/servlet/EcoreCheckList?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
    <%
        //Hashtable statushash = new Hashtable();
        HashMap<String,String> statushash = new LinkedHashMap<String, String>();

        statushash.put("authstarted", "Auth Started");
        statushash.put("authstarted_3D", "Auth Started 3D");
        statushash.put("authsuccessful", "Auth Successful");
        statushash.put("authfailed", "Auth Failed");
        statushash.put("authcancelled", "Authorisation Cancelled");
        statushash.put("begun", "Begun Processing");
        statushash.put("capturestarted", "Capture Started");
        statushash.put("capturesuccess", "Capture Successful");
        statushash.put("capturefailed", "Capture Failed");
        statushash.put("cancelled", "Cancelled Transaction");
        statushash.put("cancelstarted","Cancel Initiated");
        statushash.put("chargeback", "Chargeback Reversed");
        statushash.put("failed", "Validation Failed");
        statushash.put("podsent", "POD Sent ");
        statushash.put("proofrequired", "Proof Required");
        statushash.put("reversed", "Reversed");
        statushash.put("markedforreversal", "Reversal Request Sent");
        statushash.put("settled", "Settled");

        String str="";
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        String trackingid=null;
        /*String status=null;*/
        String toid=null;
        String mid=null;
        String memberid=nullToStr(request.getParameter("toid"));

        trackingid=Functions.checkStringNull(request.getParameter("trackingid"));
        /*status=Functions.checkStringNull(request.getParameter("status"));*/
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
       /* toid=Functions.checkStringNull(request.getParameter("toid"));*/
        mid=Functions.checkStringNull(request.getParameter("mid"));

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
        if (trackingid!= null) str = str + "&trackingid=" + trackingid;
        if (status!= null) str = str + "&status=" + status;
        if (toid!= null) str = str + "&toid=" + status;
        if (mid!= null) str = str + "&mid=" + status;

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);



    %>
    <div class="row" style="margin-top: 0px">
        <div class="col-lg-12">
            <div class="panel panel-default"style="margin-top: 0px">
                <div class="panel-heading" >

                Ecore Inquiry
                </div>

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
                                        <select size="1" name="fdate"  value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth"  value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear"  value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
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
                                        <select size="1" name="tdate">

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

                                        <select size="1" name="tyear">

                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Tracking ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input maxlength="15" type="text" name="trackingid" class="txtbox" value="">

                                    </td>
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
                                    <td width="8%" class="textb" >Status</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select id = "status" size="1" name="status" class="txtbox">
                                            <option value="">All</option>
                                            <%
                                                //java.util.Enumeration enu = statushash.keys();
                                                Set statusSet = statushash.keySet();
                                                Iterator iterator = statusSet.iterator();
                                                String key = "";
                                                String value = "";


                                                while (iterator.hasNext())
                                                {
                                                    key = (String) iterator.next();
                                                    value = (String) statushash.get(key);

                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>"><%=ESAPI.encoder().encodeForHTML(value)%></option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">To ID</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="toid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">MID</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">

                                        <input maxlength="15" type="text" name="mid" class="txtbox" value="">

                                    </td>


                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">

                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">

                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td style="padding:10px " width="12%" class="textb">
                                        <button type="submit" class="buttonform">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>

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
<%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;

    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
        out.println(error);

    }

    String currentblock=request.getParameter("currentblock");

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
    if(hash!=null)
    {
        hash = (Hashtable)request.getAttribute("transdetails");
    }
    if(records>0)
    {
%>
<table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr no</td>
            <td valign="middle" align="center" class="th0">TrackingID</td>
            <td valign="middle" align="center" class="th0">ToID</td>
            <td valign="middle" align="center" class="th0">MID</td>
            <td valign="middle" align="center" class="th0">Description</td>
            <td valign="middle" align="center" class="th0">status</td>
            <td valign="middle" align="center" class="th0">Time</td>
            <td valign="middle" align="center" class="th0">Payment Order No</td>
            <td valign="middle" align="center" class="th0">Amount</td>
            <td valign="middle" align="center" class="th0">INQUIRY</td>

            </td>
        </tr>
    </thead>
    <%
        String style="class=tr0";
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
            Functions functions = new Functions();
            temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
            out.println("<form action=\"/icici/servlet/EcoreInquiry\" method=\"POST\">");
            out.println("<td align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"<input type=\"hidden\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"><input type=\"hidden\" name=\"accountid\" value=\""+temphash.get("accountid")+"\"></td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"<input type=\"hidden\" name=\"toid\" value=\""+temphash.get("toid")+"\"></td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fromid"))+"<input type=\"hidden\" name=\"fromid\" value=\""+temphash.get("fromid")+"\"></td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"<input type=\"hidden\" name=\"description\" value=\""+temphash.get("description")+"\"></td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
            if (functions.isValueNull((String) temphash.get("ecorePaymentOrderNumber")))
            {
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("ecorePaymentOrderNumber"))+"<input type=\"hidden\" name=\"transId\" value=\""+temphash.get("ecorePaymentOrderNumber")+"\"></td>");
            }
            else
            {
                out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
            }

            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"<input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\"></td>");
            out.println("<td align=\"center\" "+style+ ">&nbsp;<button type=submit class=\"gotoauto\" value=Transection Inquiry>Transaction</button></td>");
            out.println("</form>");
            out.println("</tr>");
        }
    %>
</table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="EcoreCheckList"/>
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
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }


%>

</div>

</body>
<script language="javascript">
    $(document).ready(function ()
    {
        var dd = document.getElementById("status");
        for (var i = 0; i < dd.options.length; i++)
        {
            if (dd.options[i].value === "<%=status%>")
            {
                dd.selectedIndex = i;
                break;
            }
        }
    });
</script>

</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>
