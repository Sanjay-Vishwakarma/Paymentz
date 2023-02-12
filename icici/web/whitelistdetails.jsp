<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.PzEncryptor" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 9/2/13
  Time: 4:15 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"/>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
    <script src="/merchant/javascript/hidde.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/content.js"></script>
    <script language="javascript">
        function unMasking(spanid,inputid,msg)
        {
            var x=document.getElementById(inputid);
            $("#" + spanid).removeClass('fa-eye-slash').addClass('fa-eye');
            x.innerText=msg;
        }
        function masking(spanid,inputid,msg)
        {
            var x=document.getElementById(inputid);
            $("#" + spanid).removeClass('fa-eye').addClass('fa-eye-slash');
            x.innerText=msg;
        }
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("id");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
        function Delete()
        {
            var checkboxes = document.getElementsByName("id");
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
                alert("Select at least one record");
                return false;
            }
            if (confirm("Do you really want to update all selected Data."))
            {
                document.DeleteDetails.submit();
            }
        }
    </script>
    <style type="text/css">
        .eye-icon
        {
            float: right;
            z-index: 2;
            margin-right: 5px;
        }
    </style>
    <title>1.Whitelist Module> White List Module</title>
</head>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Functions functions=new Functions();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String expiryDate = "";
        String str="";
        String pgtypeid = "";
        String currency= "";
        str = str + "ctoken=" + ctoken;
        String lastFour=request.getParameter("lastfour");
        String firstSix=request.getParameter("firstsix");
        String accountId=request.getParameter("accountid");
        String memberId=request.getParameter("toid")==null?"":request.getParameter("toid");
        String emailAddr=request.getParameter("emailaddr")==null?"":request.getParameter("emailaddr");
        String name=request.getParameter("name")==null?"":request.getParameter("name");
        String ipAddress=request.getParameter("ipAddress")==null?"":request.getParameter("ipAddress");
        String istemp=request.getParameter("isTemp");
        expiryDate=Functions.checkStringNull(request.getParameter("expiryDate")) == null ? "" : request.getParameter("expiryDate");

        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid")) == null ? "" : request.getParameter("pgtypeid");

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

        String accountid2 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");

        if(lastFour!=null)
            str = str + "&lastfour="+lastFour;
        if(firstSix!=null)
            str = str + "&firstsix="+firstSix;
        if(accountId!=null)
            str = str + "&accountid="+accountId;
        if(memberId!=null)
            str = str + "&toid="+memberId;
        if(emailAddr!=null)
            str=str+"&emailaddr="+emailAddr;
        if(pgtypeid!=null)
            str = str + "&pgtypeid=" + pgtypeid;
        if(currency!=null)
            str = str + "&currency=" + currency;
        if(accountid2!=null)str = str + "&accountid=" + accountid2;
        if(name!=null)str = str + "&name=" + name;
        if(ipAddress!=null)str = str + "&ipAddress=" + ipAddress;
        if(expiryDate!=null)str = str + "&expiryDate=" + expiryDate;
        if(istemp!=null)str = str + "&isTemp=" + istemp;


%>
<body>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                WhiteList Card Details
                <div style="float: right;">
                    <form action="/icici/uploadsingledetail.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Upload Card Details" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Upload Single Card Details
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/uploadwhitelistcarddetails.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Upload Card Details" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Upload Bulk Card Details
                        </button>
                    </form>
                </div>
            </div><br>
            <form action="/icici/servlet/WhiteListDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%
                    String error=(String ) request.getAttribute("error");
                    if(error !=null)
                    {
                        out.println("<center><font class=\"textb\"><b>"+error+"</b></font></center>");
                    }
                %>
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >Gateway</td>
                                    <td width="2%" class="textb"></td>
                                    <td width="18%" class="textb">
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">AccountId</td>
                                    <td width="2%" class="textb"></td>
                                    <td width="18%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" class="txtbox" autocomplete="on">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >Member ID</td>
                                    <td width="2%" class="textb"></td>
                                    <td width="18%" class="textb">
                                        <input name="toid" id="memberid1" value="<%=memberId%>" class="txtbox" autocomplete="on">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >&nbsp;</td>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="18%" class="textb">&nbsp;</td>

                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >&nbsp;</td>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="18%" class="textb">&nbsp;</td>

                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >&nbsp;</td>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="18%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >Email Address</td>
                                    <td width="2%" class="textb"></td>
                                    <td width="18%" class="textb">
                                        <input type="text" size="35"name="emailaddr"class="txtbox" value=<%=emailAddr%>>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >First_SIX & Last_Four</td>
                                    <td width="2%" class="textb"></td>
                                    <td width="18%" class="textb">
                                        <input size="8" maxlength="6" type="text"  class="txtboxsmall" name="firstsix"  value="<%=request.getParameter("firstsix")==null?"":request.getParameter("firstsix")%>">******<input maxlength="4" size="6" type="text" name="lastfour" class="txtboxsmall" value="<%=request.getParameter("lastfour")==null?"":request.getParameter("lastfour")%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >Card Holder Name</td>
                                    <td width="2%" class="textb"></td>
                                    <td width="18%" class="textb">
                                        <input type="text" size="35" name="name" class="txtbox" value=<%=name%>>
                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >IP Address</td>
                                    <td width="2%" class="textb"></td>
                                    <td width="18%" class="textb">
                                        <input type="text" size="35" name="ipAddress" class="txtbox" value=<%=ipAddress%>>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >InActive</td>
                                    <td width="2%" class="textb"></td>
                                    <td width="18%" class="textb">
                                        <select name="isTemp" class="txtbox">
                                            <option value="N" <%if("N".equalsIgnoreCase(istemp)){%>selected<%}%>>N</option>
                                            <option value="Y" <%if("Y".equalsIgnoreCase(istemp)){%>selected<%}%>>Y</option>
                                        </select>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="18%" class="textb">&nbsp;
                                        <button type="submit" class="buttonform" value="Submit">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;&nbsp;</td>
                                    <td width="2%" class="textb">&nbsp;&nbsp;</td>
                                    <td width="18%" class="textb">&nbsp;&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;&nbsp;</td>
                                    <td width="2%" class="textb">&nbsp;&nbsp;</td>
                                    <td width="18%" class="textb">&nbsp;&nbsp;</td>

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
        <%
         String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

    Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    if(hash!=null)
    {
        hash = (Hashtable)request.getAttribute("transdetails");
    }
    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;
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
    if(records>0)
    {
%>
    <form name="DeleteDetails" action="/icici/servlet/DeleteDetails?ctoken=<%=ctoken%>" method="post">
        <table align=center width="100%" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
            <tr>
                <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                <td valign="middle" align="center" class="th0">Memberid</td>
                <td valign="middle" align="center" class="th0">AccountID</td>
                <td valign="middle" align="center" class="th0">FirstSix</td>
                <td valign="middle" align="center" class="th0">LastFour</td>
                <td valign="middle" align="center" class="th0">Email Address</td>
                <td valign="middle" align="center" class="th0">Name</td>
                <td valign="middle" align="center" class="th0">IP Address</td>
                <td valign="middle" align="center" class="th0">Expiry Date</td>
                <td valign="middle" align="center" class="th0">InActive</td>
                <td valign="middle" align="center" class="th0">Action Executor Id</td>
                <td valign="middle" align="center" class="th0">Action Executor Name</td>
                <td valign="middle" align="center" class="th0">Timestamp</td>
            </tr>
            </thead>
            <%
                String style="class=td1";
                String ext="light";
                for(int pos=1;pos<=records;pos++)
                {

                    String memberIdStr = "";
                    String accountIdStr = "";
                    String firstSixStr = "";
                    String lastFourStr = "";
                    String emailAddrStr = "";
                    String nameStr = "";
                    String enNameStr = "";
                    String ipAddressStr = "";
                    String expiryDateStr = "";
                    String isTemp = "";
                    String timestamp = "";

                    String id = Integer.toString(pos);
                    int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                    if (pos % 2 == 0)
                    {
                        style = "class=tr0";
                        ext = "dark";
                    }
                    else
                    {
                        style = "class=tr1";
                        ext = "light";
                    }
                    temphash = (Hashtable) hash.get(id);
                    String dbid = (String) temphash.get("id");

                    if (functions.isValueNull((String) temphash.get("memberid")))
                        memberIdStr = ((String) temphash.get("memberid"));
                    if (functions.isValueNull((String) temphash.get("accountid")))
                        accountIdStr = ((String) temphash.get("accountid"));
                    if (functions.isValueNull((String) temphash.get("firstsix")))
                        firstSixStr = ((String) temphash.get("firstsix"));
                    if (functions.isValueNull((String) temphash.get("lastfour")))
                        lastFourStr = ((String) temphash.get("lastfour"));
                    if (functions.isValueNull((String) temphash.get("emailAddr")))
                        emailAddrStr = ((String) temphash.get("emailAddr"));
                    if (functions.isValueNull((String) temphash.get("name")))
                    {
                        nameStr = ((String) temphash.get("name"));
                        enNameStr = functions.getNameMasking(nameStr);
                    }
                    if (functions.isValueNull((String) temphash.get("ipAddress")))
                        ipAddressStr = ((String) temphash.get("ipAddress"));
                    if (functions.isValueNull((String) temphash.get("expiryDate")))
                    {
                        expiryDateStr = ((String) temphash.get("expiryDate"));
                    }
                    isTemp = (String) temphash.get("isTemp");
                    if (functions.isValueNull((String) temphash.get("actionExecutorId")))
                    {
                        actionExecutorId = (String) temphash.get("actionExecutorId");
                    }
                    else
                    {
                        actionExecutorId = "-";

                    }
                    if (functions.isValueNull((String) temphash.get("actionExecutorName")))
                    {
                        actionExecutorName = (String) temphash.get("actionExecutorName");
                    }
                    else
                    {
                        actionExecutorName = "-";
                    }
                    if (functions.isValueNull((String) temphash.get("registration_date")))
                    {
                        timestamp = ((String) temphash.get("registration_date"));
                    }
                    out.println("<tr>");
                    out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"id\" value=\"" + dbid + "\"></td>");
                    out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(memberIdStr) + "</td>");
                    out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(accountIdStr) + "</td>");
                    out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(firstSixStr) + "</td>");
                    out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(lastFourStr) + "</td>");
                    out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(emailAddrStr) + "</td>");
                    out.println("<td " + style + ">");
                    if (functions.isValueNull(nameStr) || !nameStr.equals(""))
                    {
                        out.println("&nbsp; <label id=\"name" + dbid + "\">" + enNameStr + "</label><span style=\"z-index: 2;margin-left: 30px;margin-top:5px;font-size:13px;\" toggle=\"#password-field\" class=\"far fa-eye-slash toggle-password eye-icon\" onmousedown=\"unMasking('showHidepass1" + dbid + "','name" + dbid + "','" + nameStr + "')\" onmouseup=\"masking('showHidepass1" + dbid + "','name" + dbid + "','" + enNameStr + "')\" id=\"showHidepass1" + dbid + "\" ></span>");
                    }
                    out.println("</td>");
                    out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(ipAddressStr) + "</td>");
                    out.println("<td " + style + ">");
                    if (functions.isValueNull(expiryDateStr) || !"".equals(expiryDateStr))
                    {
                        out.println("&nbsp;<input class=\"textbox\" id=\"expirydate" + dbid + "\" style=\"width:60%\" type=\"text\" size=\"30\" value=\"" + expiryDateStr + "\" disabled \">");
                    }
                    out.println("<td " + style + ">&nbsp;<select class=\"textbox\" name=\"isTemp_" + dbid + "\">" + Functions.comboval(isTemp) + "</select></td>");
                    out.println("</td>");
                    out.println("<td " + style + " align=\"center\">&nbsp;" + actionExecutorId + "</td>");
                    out.println("<td " + style + " align=\"center\">&nbsp;" + actionExecutorName + "</td>");
                    if (functions.isValueNull(timestamp) || !"".equals(timestamp))
                    {
                        out.println("<td " + style + ">" + ESAPI.encoder().encodeForHTML(timestamp) + "</td>");
                    }
                    out.println("</tr>");
                }
            %>
        </table>
        <table width="100%">
            <thead>
            <tr>
                <td width="15%" align="center">
                    <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="addnewmember" value="Update" onclick="return Delete();">
                </td>
            </tr>
            </thead>
        </table>
    </form>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="WhiteListDetails"/>
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
        out.println(Functions.NewShowConfirmation("Sorry","No records found."));
    }
%>
</body>
</div>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>