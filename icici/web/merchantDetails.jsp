<%@ page import="com.directi.pg.Admin,com.directi.pg.Functions,com.directi.pg.Logger,org.owasp.esapi.ESAPI,org.owasp.esapi.errors.ValidationException,java.util.Calendar"%>
<%@ include file="index.jsp"%>
<%@ page import="java.util.Hashtable"%>

<%!
    private static Logger log=new Logger("merchantDetails.jsp");

%>
<%  String memberid=nullToStr(request.getParameter("memberid"));
    String icici=nullToStr(request.getParameter("icici"));
    String contact_emails=nullToStr(request.getParameter("contact_emails"));
    String contact_persons=nullToStr(request.getParameter("contact_persons"));
    String login=nullToStr(request.getParameter("login"));

    String fdate=null,tdate=null,fmonth=null,tmonth=null,fyear=null,tyear=null;
    Calendar rightNow = Calendar.getInstance();
    String currentyear= ""+rightNow.get(rightNow.YEAR);

    //rightNow.setTime(new Date());
    fdate=""+1;
    tdate=""+rightNow.get(rightNow.DATE);
    fmonth=""+rightNow.get(rightNow.MONTH);
    tmonth=""+rightNow.get(rightNow.MONTH);
    fyear=""+rightNow.get(rightNow.YEAR);
    tyear=""+rightNow.get(rightNow.YEAR);

    int pageno =1;
    int pagerecords=30;
    try
    {
        pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",request.getParameter("SPageno"),"Numbers",3,true), 1);
        pagerecords = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",request.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    }
    catch(ValidationException e)
    {
        pageno = 1;
        pagerecords = 30;
    }
%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>Merchants</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <%--
    <link rel="stylesheet" type="text/css" href="/merchant/style.css" />
    --%>
</head>
<body class="bodybackground" >
<%-----------------Start filter panel-----------------%>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (Admin.isLoggedIn(session))
    {
%>
<%try{
%>
<form name="form" method = "post" action="MerchantDetails?ctoken=<%=ctoken%>">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Merchant Master
                </div>

                <%
                    String errormsg = (String) request.getAttribute("error");
                    if(errormsg!=null)
                    {
                        out.println("<br><font face=\"arial\" color=\"red\" size=\"2\">");
                        errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
                        out.println(errormsg);
                        out.println("</font>");
                        out.println("</td></tr></table>");
                    }
                %>
                <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.4%;margin-right: 2.4% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >From</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="fdate" class="textb">
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" class="textb">
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" class="textb">
                                            <%
                                                if (fyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2013));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="tdate" class="textb">

                                            <%
                                                if (tdate != null)
                                                    out.println(Functions.dayoptions(1, 31, tdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>

                                        <select size="1" name="tmonth" class="textb">

                                            <%
                                                if (tmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>

                                        <select size="1" name="tyear" class="textb" style="width:54px;">

                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2013));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Company Name</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="company_name" maxlength="50" class="txtbox">
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
                                    <td width="8%" class="textb" >Transaction Status</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select size="1" name="activation" class="txtbox">
                                            <option value="" >All</option>
                                            <option value="N">Inactive</option>
                                            <option value="Y">Active</option>
                                            <option value="T">Test</option>
                                        </select>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Site Name</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="sitename" value="" maxlength="50"  class="txtbox" size="20">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >Is Merchant Interface Access</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="icici" class="txtbox">
                                            <option value="" <%=icici.equals("")?"selected":""%>>All</option>
                                            <option value="N" <%=icici.equals("N")?"selected":""%>>Inactive</option>
                                            <option value="Y" <%=icici.equals("Y")?"selected":""%>>Active</option>
                                        </select>
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
                                    <td width="8%" class="textb" colspan="2">Merchant ID</td>
                                    <td width="12%" class="textb">
                                        <input name="memberid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Rows/pages</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type="text" maxlength="3"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(new Integer(pagerecords).toString())%>" name="SRecords" size="2" class="txtbox"/>

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >
                                        <%
                                            if(request.getParameter("ignoredates")!=null)
                                            {
                                                //System.out.println("ignore value"+request.getParameter("ignoredates"));
                                        %>
                                        <input type=checkbox name="ignoredates" value="yes" checked>
                                        <%
                                        }
                                        else
                                        {
                                            //System.out.println("ignore value"+request.getParameter("ignoredates"));
                                        %>
                                        <input type=checkbox name="ignoredates">
                                        <%

                                            }
                                        %>
                                        &nbsp;&nbsp;Ignore Dates</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb">
                                        <input type=checkbox name="perfectmatch"  class="textb">&nbsp;&nbsp;Show Perfect Match
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
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Contact&nbsp;Person&nbsp;Email</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="contact_emails" class="txtbox">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Username</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <%--<input  type="text" name="username" class="txtbox" >--%>
                                        <input type=text name="login" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(login)%>"  class="txtbox" size="20">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" value="Submit">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Submit
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

            </div>
        </div>
    </div>
</form>
<%-----------------End filter panel-----------------%>

<div class="reporttable" style="margin-left: 360px;margin-right: 200px">

    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
            <%  Hashtable hash=(Hashtable)request.getAttribute("merchantdetails");
	Hashtable innerhash=null;
	//out.println(hash);
    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
        out.println(error);

    }
 if(hash!=null){
	String style="class=tr0";
    String value="";
    innerhash=(Hashtable)hash.get(1+"");
    int pos=0;
    value=(String)innerhash.get("memberid");
%>

        <thead>
        <tr <%=style%>>
            <td class="texthead" colspan="2" align="center">&nbsp;&nbsp;&nbsp;Merchant Details</td>
        </tr>
        </thead>
        <tr <%=style%>>
            <td class="td0" align="center">Member ID: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
        style="class=\"tr"+pos%2+"\"";
        value=nullToStr((String)innerhash.get("login"));
    %>
        <tr <%=style%>>
            <td class="td0" align="center">Username: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
                <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("company_name"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Company Name: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("activation"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Merchant Activation Status: </td>
            <td class="td0" align="center"><%=getStatus(ESAPI.encoder().encodeForHTML(value))%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("icici"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Transaction Activation Status: </td>
            <td class="td0" align="center"><%=getStatus(ESAPI.encoder().encodeForHTML(value))%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("contact_persons"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Contact person: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("contact_emails"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Merchant's Email: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("notifyemail"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Notify Email: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("sitename"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Site URL: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("brandname"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Brand Name: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("telno"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Telephone Number: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("faxno"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Fax Number: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("address"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Address: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("city"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">City: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("state"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">State: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("zip"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Zip: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("country"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Country: </td>
            <td class="td0" align="center" ><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("actDate"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Activation Date: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("modDate"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Last Modified Date: </td>
            <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
        </tr>
            <%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)hash.get("balance"));
%>
        <tr <%=style%>>
            <td class="td0" align="center">Current Balance: </td>
            <td class="td0" align="center"><font color="red"><%=ESAPI.encoder().encodeForHTML(value)%></font></td>
        </tr>

            <%  }else{
	    out.println(Functions.NewShowConfirmation("Sorry","No data found for Member Id :"+ESAPI.encoder().encodeForHTML(memberid)));
    }
  }catch(Exception e){
     log.error("Exception while getting merchant details",e);
  }
%>
</div>
</table>

<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
    public static String getStatus(String str)
    {
        if(str.equals("Y"))
            return "Active";
        else if(str.equals("N"))
            return "Inactive";
        else if(str.equals("T"))
            return "Test";

        return str;
    }
%>
</html>
