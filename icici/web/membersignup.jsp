<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="org.owasp.esapi.reference.DefaultEncoder" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 4/19/14
  Time: 11:49 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>
    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>
    <script language="javascript">
        $(document).ready(function() {
            document.getElementById('submit').disabled =  false;
            $("#submit").click(function() {
                var encryptedString1 =  $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
                document.getElementById('passwd').value =  encryptedString1;

                var encryptedString2 =  $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
                document.getElementById('conpasswd').value =  encryptedString2;

                document.getElementById('isEncrypted').value =  true;
            });

        });
    </script>
    <script  language="javascript">
        function hideshowpass(spanid,inputid)
        {
            var x = document.getElementById(inputid);
            if (x.type === "password")
            {
                $("#"+spanid).removeClass('fa-eye-slash').addClass('fa-eye')
                x.type = "text";
            }
            else
            {
                $("#"+spanid).removeClass('fa-eye').addClass('fa-eye-slash')
                x.type = "password";
            }
        }
    </script>
    <title>Member Registration</title>
</head>
<body>
<%--<center><h2>Member Master</h2></center> --%>
<%
    String memberid=nullToStr(request.getParameter("memberid"));
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {

        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;

        String str="";
        Calendar rightNow = Calendar.getInstance();
        String currentyear= ""+rightNow.get(Calendar.YEAR);

        //rightNow.setTime(new Date());
        if(fdate==null)fdate=""+1;
        if(tdate==null)tdate=""+rightNow.get(Calendar.DATE);
        if(fmonth==null)fmonth=""+rightNow.get(Calendar.MONTH);
        if(tmonth==null)tmonth=""+rightNow.get(Calendar.MONTH);
        if(fyear==null)fyear=""+rightNow.get(Calendar.YEAR);
        if(tyear==null)tyear=""+rightNow.get(Calendar.YEAR);

        str=str+"&ctoken="+ctoken;
%>

<%
    MerchantDAO merchantDAO = new MerchantDAO();
    LinkedHashMap<Integer,String> memberMap = merchantDAO.listAllMember();
%>

<form name="form" method = "post" action="/icici/servlet/MerchantDetails?ctoken=<%=ctoken%>">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Merchant Master
                </div>
                <table  align="center" width="98%" cellpadding="2" cellspacing="2" style="margin-left:1.4%;margin-right: 2.4% ">
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
                                        <input type=text name="company_name" maxlength="50"  value="" class="txtbox">
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
                                            <option value="">All</option>
                                            <option value="N">Inactive</option>
                                            <option value="Y">Active</option>
                                            <option value="T">Test</option>
                                        </select>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Site Name</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="sitename" maxlength="50"  value=""  class="txtbox" size="20">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >is Merchant Interface Access</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="icici" class="txtbox">
                                            <option value="">All</option>
                                            <option value="N">Inactive</option>
                                            <option value="Y" >Active</option>
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

                                        <select size="1" name="memberid" class="txtbox">
                                            <option value="" selected>--Select Member ID--</option>
                                            <%
                                                for (Integer mId : memberMap.keySet())
                                                {
                                                    String companyname = memberMap.get(mId);
                                                    String isSelected = "";
                                                    if (String.valueOf(mId).equalsIgnoreCase(memberid))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                            %>
                                            <option value="<%=mId%>" <%=isSelected%>><%=mId+"-"+companyname%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Rows/pages</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type="text" maxlength="3"  value="" name="SRecords" size="2" class="txtbox"/>

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
                                    <td width="15%" class="textb">
                                        <%--<input type=checkbox name="perfectmatch" value="<%=ESAPI.encoder().encodeForHTMLAttribute(perfectMatch)%>" class="textb">--%>
                                        <select name="perfectmatch">
                                            <option value="No">NO</option>
                                            <option value="Yes" selected="">Yes</option>
                                        </select>&nbsp;&nbsp;Show Perfect Match
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
                                    <td width="8%" class="textb" >Contact&nbsp;Person&nbsp;Email</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="contact_emails" class="txtbox">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Username</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="username" class="txtbox" >
                                    </td>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Partner Name</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <%--<input  type="text" name="username" class="txtbox" >--%>
                                        <input type=text name="partnerName"class="txtbox">
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

<div class="reporttable">
    <%
        String username = "";
        String passwd = "";
        String conpasswd = "";
        String company_name = "";
        String sitename = "";
        String domain = "";

        String contact_emails = "";
        String contact_persons = "";
        String mainContact_cCmailid="";
        String mainContact_bccMailid="";
        String mainContact_phone="";

        String support_persons = "";
        String support_emails = "";
        String support_cCmailid="";
        String support_bccMailid="";
        String support_phone="";

        String refundContact_name="";
        String refundContact_mailId="";
        String refundContact_cCmail="";
        String refundContact_bccMail="";
        String refundContact_phone="";

        String cbContact_name="";
        String cbContact_mailId="";
        String cbContact_cCmailId="";
        String cbContact_bccMailId="";
        String cbContact_phone="";

        String salesContact_name="";
        String salesContact_mailid="";
        String salesContact_cCmailId="";
        String salesContact_bccMailId="";
        String salesContact_phone="";

        String billingContact_name="";
        String billingContact_mailid="";
        String billingContact_cCmailId="";
        String billingContact_bccMailId="";
        String billingContact_phone="";

        String fraudContact_name="";
        String fraudContact_mailid="";
        String fraudContact_cCmailId="";
        String fraudContact_bccMailId="";
        String fraudContact_phone="";

        String technicalContact_name="";
        String technicalContact_mailId="";
        String technicalContact_cCmailId="";
        String technicalContact_bccMailId="";
        String technicalContact_phone="";
        String etoken= ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
        String supportNo = "";
        String phonecc = "";
        String telno = "";
        String country = "";

        String errormsg=(String)request.getAttribute("error");
        Hashtable details = (Hashtable) request.getAttribute("details");

        if(errormsg!=null)
        {
            out.println("<center><font class=\"textb\" face=\"arial\">"+errormsg+"</font></center>");
        }
        if (details != null)
        {

            String mes = request.getParameter("MES");
            if (details.get("login") != null) username = (String) details.get("login");
            if (details.get("company_name") != null) company_name = (String) details.get("company_name");
            if (details.get("sitename") != null) sitename = (String) details.get("sitename");
            if (details.get("telno") != null) supportNo = (String) details.get("telno");
            if (!supportNo.equals("-"))
            {
                if(supportNo.contains("-"))
                {
                    String splitvalue[] =supportNo.split("-");
                    if (splitvalue.length == 2)
                    {
                        phonecc=splitvalue[0];
                        telno=splitvalue[1];
                    }
                    else
                    {
                        phonecc=splitvalue[0];
                        telno = "";
                    }
                }
            }
            if (details.get("country") != null) country = (String) details.get("country");
            if (details.get("domain") != null) domain = (String) details.get("domain");

            if (details.get("contact_emails") != null) contact_emails = (String) details.get("contact_emails");
            if (details.get("contact_persons") != null) contact_persons = (String) details.get("contact_persons");
            if (details.get("maincontact_ccmailid") != null) mainContact_cCmailid = (String) details.get("maincontact_ccmailid");
            if (details.get("maincontact_bccmailid") != null) mainContact_bccMailid = (String) details.get("maincontact_bccmailid");
            if (details.get("maincontact_phone") != null) mainContact_phone = (String) details.get("maincontact_phone");

            if (details.get("support_persons") != null) support_persons = (String) details.get("support_persons");
            if (details.get("support_emails") != null) support_emails = (String) details.get("support_emails");
            if (details.get("support_ccmailid") != null) support_cCmailid = (String) details.get("support_ccmailid");
            if (details.get("support_bccmailid") != null) support_bccMailid = (String) details.get("support_bccmailid");
            if (details.get("support_phone") != null) support_phone = (String) details.get("support_phone");

            if (details.get("salescontact_name") != null) salesContact_name = (String) details.get("salescontact_name");
            if (details.get("salescontact_mailid") != null) salesContact_mailid = (String) details.get("salescontact_mailid");
            if (details.get("salescontact_ccmailid") != null) salesContact_cCmailId = (String) details.get("salescontact_ccmailid");
            if (details.get("salescontact_bccmailid") != null) salesContact_bccMailId = (String) details.get("salescontact_bccmailid");
            if (details.get("salescontact_phone") != null) salesContact_phone = (String) details.get("salescontact_phone");

            if (details.get("refundcontact_name") != null) refundContact_name = (String) details.get("refundcontact_name");
            if (details.get("refundcontact_mailid") != null) refundContact_mailId = (String) details.get("refundcontact_mailid");
            if (details.get("refundcontact_ccmailid") != null) refundContact_cCmail = (String) details.get("refundcontact_ccmailid");
            if (details.get("refundcontact_bccmailid") != null) refundContact_bccMail = (String) details.get("refundcontact_bccmailid");
            if (details.get("refundcontact_phone") != null) refundContact_phone = (String) details.get("refundcontact_phone");

            if (details.get("cbcontact_name") != null) cbContact_name = (String) details.get("cbcontact_name");
            if (details.get("cbcontact_mailid") != null) cbContact_mailId = (String) details.get("cbcontact_mailid");
            if (details.get("cbcontact_ccmailid") != null) cbContact_cCmailId = (String) details.get("cbcontact_ccmailid");
            if (details.get("cbcontact_bccmailid") != null) cbContact_bccMailId = (String) details.get("cbcontact_bccmailid");
            if (details.get("cbcontact_phone") != null) cbContact_phone = (String) details.get("cbcontact_phone");

            if (details.get("billingcontact_name") != null) billingContact_name = (String) details.get("billingcontact_name");
            if (details.get("billingcontact_mailid") != null) billingContact_mailid = (String) details.get("billingcontact_mailid");
            if (details.get("billingContact_cCmailId") != null) billingContact_cCmailId = (String) details.get("billingContact_cCmailId");
            if (details.get("billingContact_bcCmailId") != null) billingContact_bccMailId = (String) details.get("billingContact_bcCmailId");
            if (details.get("bllingContact_phone") != null) billingContact_phone = (String) details.get("bllingContact_phone");

            if (details.get("fraudcontact_name") != null) fraudContact_name = (String) details.get("fraudcontact_name");
            if (details.get("fraudcontact_mailid") != null) fraudContact_mailid = (String) details.get("fraudcontact_mailid");
            if (details.get("fraudcontact_ccmailid") != null) fraudContact_cCmailId = (String) details.get("fraudcontact_ccmailid");
            if (details.get("fraudContact_bccMailId") != null) fraudContact_bccMailId = (String) details.get("fraudContact_bccMailId");
            if (details.get("fraudcontact_phone") != null) fraudContact_phone = (String) details.get("fraudcontact_phone");

            if (details.get("technicalcontact_name") != null) technicalContact_name = (String) details.get("technicalcontact_name");
            if (details.get("technicalcontact_mailid") != null) technicalContact_mailId = (String) details.get("technicalcontact_mailid");
            if (details.get("technicalcontact_ccmailid") != null) technicalContact_cCmailId = (String) details.get("technicalcontact_ccmailid");
            if (details.get("technicalcontact_bccmailid") != null) technicalContact_bccMailId = (String) details.get("technicalcontact_bccmailid");
            if (details.get("technicalcontact_phone") != null) technicalContact_phone = (String) details.get("technicalcontact_phone");


        }
    %>
    <form action="/icici/servlet/NewMemberSignUp?ctoken=<%=ctoken%>" method="post" name="form1">
        <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
        <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
        <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor=white align="center">
            <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
            <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
            <script language="javascript">

                function myjunk()
                {

                    var hat = this.document.form1.country.selectedIndex;
                    var hatto = this.document.form1.country.options[hat].value;
                    var countrycd = this.document.form1.phonecc.value = hatto.split("|")[1];
                    var telnumb = this.document.form1.telno.value;
                    // var cctel = countrycd.concat(telnumb);
                    if (hatto != 'Select one') {

                        this.document.form1.countrycode.value = hatto.split("|")[0];
                        this.document.form1.phonecc.value = hatto.split("|")[1];
                        this.document.form1.country.options[0].selected=false;

                    }


                }


            </script>

            <tr>
                <td>
                    <table border="0" cellpadding="5" cellspacing="0" width="90%"  align="center" style="margin-left:12%">
                        <tr><td colspan="5">&nbsp;</td></tr>
                        <tr><td colspan="4" class="textb" align="right"><h5><b><u>Merchant Information</u></b></h5></td> </tr>
                        <tr><td colspan="4">&nbsp;</td></tr>

                        <tr>
                            <td colspan="4" width="43%" class="textb"><span class="textb">Username or Email*</span> (Username Should Not Contain Special Characters like !#$%)</td>
                            <td width="20%" align="right"><input class="txtbox" style="align:right;" type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="35"></td>
                        </tr>
                        <tr><td colspan="5">&nbsp;</td></tr>
                        <tr>
                            <td colspan="4" class="textb"><span class="textb">Password*</span> (Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)</td>
                            <td align="right">
                                <input  id="passwd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');" value="<%=ESAPI.encoder().encodeForHTMLAttribute(passwd)%>" name="passwd" size="20" style="margin-right: -23px;">
                                <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','passwd')" style="position: relative;right: 5;margin-top: 3;"></span>
                            </td>
                        </tr>
                        <tr><td colspan="5">&nbsp;</td></tr>
                        <tr>
                            <td colspan="4" class="textb"><span class="textb">Confirm Password*</span> (Should be same as PASSWORD)</td>
                            <td align="right">
                                <input id="conpasswd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');" value="<%=ESAPI.encoder().encodeForHTMLAttribute(conpasswd)%>" name="conpasswd" size="20" style="margin-right: -23px;">
                                <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','conpasswd')" style="position: relative;right: 5;margin-top: 3;"></span>
                            </td>
                        </tr>
                        <tr><td colspan="5">&nbsp;</td></tr>
                        <tr>
                        <tr>
                            <td colspan="4" class="textb">Organisation Name*</td>
                            <td align="right"><input class="txtbox" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" name="company_name" size="35" placeholder Name="Org"></td>
                        </tr>

                        <tr><td colspan="5">&nbsp;</td></tr>
                        <tr>
                            <td colspan="4" class="textb"><span class="textb">Site URL*</span> (Ex. http://www.abc.com)</td>
                            <td align="right"><input class="txtbox" type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" name="sitename" size="35"></td>
                        </tr>

                        <tr><td colspan="5">&nbsp;</td></tr>
                        <tr>
                            <td colspan="4" class="textb"><span class="textb">Domain</span> (Ex. http://www.abc.com)</td>
                            <td align="right"><input class="txtbox" type="Text" maxlength="1000"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(domain)%>" name="domain" size="100"></td>
                        </tr>

                        <tr><td colspan="5">&nbsp;</td></tr>
                        <tr>
                            <td colspan="4" class="textb"><span class="textb">Country*</span></td>
                            <td align="right">
                                <%-- <input class="txtbox" type="Text" maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>" name="country" size="35" size="20"></td>--%>
                                <select name="country" placeholder="Country*" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>"  onchange="myjunk();"  >
                                    <option value="-|phonecc">Select a Country</option>
                                    <option value="AF|093">Afghanistan</option>
                                    <option value="AX|358">Aland Islands</option>
                                    <option value="AL|355">Albania</option>
                                    <option value="DZ|231">Algeria</option>
                                    <option value="AS|684">American Samoa</option>
                                    <option value="AD|376">Andorra</option>
                                    <option value="AO|244">Angola</option>
                                    <option value="AI|001">Anguilla</option>
                                    <option value="AQ|000">Antarctica</option>
                                    <option value="AG|001">Antigua and Barbuda</option>
                                    <option value="AR|054">Argentina</option>
                                    <option value="AM|374">Armenia</option>
                                    <option value="AW|297">Aruba</option>
                                    <option value="AU|061">Australia</option>
                                    <option value="AT|043">Austria</option>
                                    <option value="AZ|994">Azerbaijan</option>
                                    <option value="BS|001">Bahamas</option>
                                    <option value="BH|973">Bahrain</option>
                                    <option value="BD|880">Bangladesh</option>
                                    <option value="BB|001">Barbados</option>
                                    <option value="BY|375">Belarus</option>
                                    <option value="BE|032">Belgium</option>
                                    <option value="BZ|501">Belize</option>
                                    <option value="BJ|229">Benin</option>
                                    <option value="BM|001">Bermuda</option>
                                    <option value="BT|975">Bhutan</option>
                                    <option value="BO|591">Bolivia</option>
                                    <option value="BA|387">Bosnia and Herzegovina</option>
                                    <option value="BW|267">Botswana</option>
                                    <option value="BV|000">Bouvet Island</option>
                                    <option value="BR|055">Brazil</option>
                                    <option value="IO|246">British Indian Ocean Territory</option>
                                    <option value="VG|001">British Virgin Islands</option>
                                    <option value="BN|673">Brunei</option>
                                    <option value="BG|359">Bulgaria</option>
                                    <option value="BF|226">Burkina Faso</option>
                                    <option value="BI|257">Burundi</option>
                                    <option value="KH|855">Cambodia</option>
                                    <option value="CM|237">Cameroon</option>
                                    <option value="CA|001">Canada</option>
                                    <option value="CV|238">Cape Verde</option>
                                    <option value="KY|001">Cayman Islands</option>
                                    <option value="CF|236">Central African Republic</option>
                                    <option value="TD|235">Chad</option>
                                    <option value="CL|056">Chile</option>
                                    <option value="CN|086">China</option>
                                    <option value="CX|061">Christmas Island</option>
                                    <option value="CC|061">Cocos (Keeling) Islands</option>
                                    <option value="CO|057">Colombia</option>
                                    <option value="KM|269">Comoros</option>
                                    <option value="CK|682">Cook Islands</option>
                                    <option value="CR|506">Costa Rica</option>
                                    <option value="CI|225">Cote d'Ivoire</option>
                                    <option value="HR|385">Croatia</option>
                                    <option value="CU|053">Cuba</option>
                                    <option value="CW|599">Curacao</option>
                                    <option value="CY|357">Cyprus</option>
                                    <option value="CZ|420">Czech Republic</option>
                                    <option value="CD|243">Democratic Republic of the Congo</option>
                                    <option value="DK|045">Denmark</option>
                                    <option value="DJ|253">Djibouti</option>
                                    <option value="DM|001">Dominica</option>
                                    <option value="DO|001">Dominican Republic</option>
                                    <option value="EC|593">Ecuador</option>
                                    <option value="EG|020">Egypt</option>
                                    <option value="SV|503">El Salvador</option>
                                    <option value="GQ|240">Equatorial Guinea</option>
                                    <option value="ER|291">Eritrea</option>
                                    <option value="EE|372">Estonia</option>
                                    <option value="ET|251">Ethiopia</option>
                                    <option value="FK|500">Falkland Islands</option>
                                    <option value="FO|298">Faroe Islands</option>
                                    <option value="FJ|679">Fiji</option>
                                    <option value="FI|358">Finland</option>
                                    <option value="FR|033">France</option>
                                    <option value="GF|594">French Guiana</option>
                                    <option value="PF|689">French Polynesia</option>
                                    <option value="TF|000">French Southern and Antarctic Lands</option>
                                    <option value="GA|241">Gabon</option>
                                    <option value="GM|220">Gambia</option>
                                    <option value="GE|995">Georgia</option>
                                    <option value="DE|049">Germany</option>
                                    <option value="GH|233">Ghana</option>
                                    <option value="GI|350">Gibraltar</option>
                                    <option value="GR|030">Greece</option>
                                    <option value="GL|299">Greenland</option>
                                    <option value="GD|001">Grenada</option>
                                    <option value="GP|590">Guadeloupe</option>
                                    <option value="GU|001">Guam</option>
                                    <option value="GT|502">Guatemala</option>
                                    <option value="GG|000">Guernsey</option>
                                    <option value="GN|224">Guinea</option>
                                    <option value="GW|245">Guinea-Bissau</option>
                                    <option value="GY|592">Guyana</option>
                                    <option value="HT|509">Haiti</option>
                                    <option value="HM|672">Heard Island & McDonald Islands</option>
                                    <option value="HN|504">Honduras</option>
                                    <option value="HK|852">Hong Kong</option>
                                    <option value="HU|036">Hungary</option>
                                    <option value="IS|354">Iceland</option>
                                    <option value="IN|091">India</option>
                                    <option value="ID|062">Indonesia</option>
                                    <option value="IR|098">Iran</option>
                                    <option value="IQ|964">Iraq</option>
                                    <option value="IE|353">Ireland</option>
                                    <option value="IL|972">Israel</option>
                                    <option value="IT|039">Italy</option>
                                    <option value="JM|001">Jamaica</option>
                                    <option value="JP|081">Japan</option>
                                    <option value="JE|044">Jersey</option>
                                    <option value="JO|962">Jordan</option>
                                    <option value="KZ|007">Kazakhstan</option>
                                    <option value="KE|254">Kenya</option>
                                    <option value="KI|686">Kiribati</option>
                                    <option value="KW|965">Kuwait</option>
                                    <option value="KG|996">Kyrgyzstan</option>
                                    <option value="LA|856">Laos</option>
                                    <option value="LV|371">Latvia</option>
                                    <option value="LB|961">Lebanon</option>
                                    <option value="LS|266">Lesotho</option>
                                    <option value="LR|231">Liberia</option>
                                    <option value="LY|218">Libya</option>
                                    <option value="LI|423">Liechtenstein</option>
                                    <option value="LT|370">Lithuania</option>
                                    <option value="LU|352">Luxembourg</option>
                                    <option value="MO|853">Macau, China</option>
                                    <option value="MK|389">Macedonia</option>
                                    <option value="MG|261">Madagascar</option>
                                    <option value="MW|265">Malawi</option>
                                    <option value="MY|060">Malaysia</option>
                                    <option value="MV|960">Maldives</option>
                                    <option value="ML|223">Mali</option>
                                    <option value="MT|356">Malta</option>
                                    <option value="MH|692">Marshall Islands</option>
                                    <option value="MQ|596">Martinique</option>
                                    <option value="MR|222">Mauritania</option>
                                    <option value="MU|230">Mauritius</option>
                                    <option value="YT|269">Mayotte</option>
                                    <option value="MX|052">Mexico</option>
                                    <option value="FM|691">Micronesia, Federated States of</option>
                                    <option value="MD|373">Moldova</option>
                                    <option value="MC|377">Monaco</option>
                                    <option value="MN|976">Mongolia</option>
                                    <option value="ME|382">Montenegro</option>
                                    <option value="MS|001">Montserrat</option>
                                    <option value="MA|212">Morocco</option>
                                    <option value="MZ|258">Mozambique</option>
                                    <option value="MM|095">Myanmar</option>
                                    <option value="NA|264">Namibia</option>
                                    <option value="NR|674">Nauru</option>
                                    <option value="NP|977">Nepal</option>
                                    <option value="AN|599">Netherlands Antilles</option>
                                    <option value="NL|031">Netherlands</option>
                                    <option value="NC|687">New Caledonia</option>
                                    <option value="NZ|064">New Zealand</option>
                                    <option value="NI|505">Nicaragua</option>
                                    <option value="NE|227">Niger</option>
                                    <option value="NG|234">Nigeria</option>
                                    <option value="NU|683">Niue</option>
                                    <option value="NF|672">Norfolk Island</option>
                                    <option value="KP|850">North Korea</option>
                                    <option value="MP|001">Northern Mariana Islands</option>
                                    <option value="NO|047">Norway</option>
                                    <option value="OM|968">Oman</option>
                                    <option value="PK|092">Pakistan</option>
                                    <option value="PW|680">Palau</option>
                                    <option value="PS|970">Palestinian Authority</option>
                                    <option value="PA|507">Panama</option>
                                    <option value="PG|675">Papua New Guinea</option>
                                    <option value="PY|595">Paraguay</option>
                                    <option value="PE|051">Peru</option>
                                    <option value="PH|063">Philippines</option>
                                    <option value="PN|064">Pitcairn Islands</option>
                                    <option value="PL|048">Poland</option>
                                    <option value="PT|351">Portugal</option>
                                    <option value="PR|001">Puerto Rico</option>
                                    <option value="QA|974">Qatar</option>
                                    <option value="CG|242">Republic of the Congo</option>
                                    <option value="RE|262">Reunion</option>
                                    <option value="RO|040">Romania</option>
                                    <option value="RU|007">Russia</option>
                                    <option value="RW|250">Rwanda</option>
                                    <option value="BL|590">Saint Barthelemy</option>
                                    <option value="SH|290">Saint Helena, Ascension & Tristan daCunha</option>
                                    <option value="KN|001">Saint Kitts and Nevis</option>
                                    <option value="LC|001">Saint Lucia</option>
                                    <option value="MF|590">Saint Martin</option>
                                    <option value="PM|508">Saint Pierre and Miquelon</option>
                                    <option value="VC|001">Saint Vincent and Grenadines</option>
                                    <option value="WS|685">Samoa</option>
                                    <option value="SM|378">San Marino</option>
                                    <option value="ST|239">Sao Tome and Principe</option>
                                    <option value="SA|966">Saudi Arabia</option>
                                    <option value="SN|221">Senegal</option>
                                    <option value="RS|381">Serbia</option>
                                    <option value="SC|248">Seychelles</option>
                                    <option value="SL|232">Sierra Leone</option>
                                    <option value="SG|065">Singapore</option>
                                    <option value="SK|421">Slovakia</option>
                                    <option value="SI|386">Slovenia</option>
                                    <option value="SB|677">Solomon Islands</option>
                                    <option value="SO|252">Somalia</option>
                                    <option value="ZA|027">South Africa</option>
                                    <option value="GS|000">South Georgia & South Sandwich Islands</option>
                                    <option value="KR|082">South Korea</option>
                                    <option value="ES|034">Spain</option>
                                    <option value="LK|094">Sri Lanka</option>
                                    <option value="SD|249">Sudan</option>
                                    <option value="SR|597">Suriname</option>
                                    <option value="SJ|047">Svalbard and Jan Mayen</option>
                                    <option value="SZ|268">Swaziland</option>
                                    <option value="SE|046">Sweden</option>
                                    <option value="CH|041">Switzerland</option>
                                    <option value="SY|963">Syria</option>
                                    <option value="TW|886">Taiwan</option>
                                    <option value="TJ|992">Tajikistan</option>
                                    <option value="TZ|255">Tanzania</option>
                                    <option value="TH|066">Thailand</option>
                                    <option value="TL|670">Timor-Leste</option>
                                    <option value="TG|228">Togo</option>
                                    <option value="TK|690">Tokelau</option>
                                    <option value="TO|676">Tonga</option>
                                    <option value="TT|001">Trinidad and Tobago</option>
                                    <option value="TN|216">Tunisia</option>
                                    <option value="TR|090">Turkey</option>
                                    <option value="TM|993">Turkmenistan</option>
                                    <option value="TC|001">Turks and Caicos Islands</option>
                                    <option value="TV|688">Tuvalu</option>
                                    <option value="UG|256">Uganda</option>
                                    <option value="UA|380">Ukraine</option>
                                    <option value="AE|971">United Arab Emirates</option>
                                    <option value="GB|044">United Kingdom</option>
                                    <option value="US|001">United States</option>
                                    <option value="VI|001">United States Virgin Islands</option>
                                    <option value="UY|598">Uruguay</option>
                                    <option value="UZ|998">Uzbekistan</option>
                                    <option value="VU|678">Vanuatu</option>
                                    <option value="VA|379">Vatican City</option>
                                    <option value="VE|058">Venezuela</option>
                                    <option value="VN|084">Vietnam</option>
                                    <option value="WF|681">Wallis and Futuna</option>
                                    <option value="EH|212">Western Sahara</option>
                                    <option value="YE|967">Yemen</option>
                                    <option value="ZM|260">Zambia</option>
                                    <option value="ZW|263">Zimbabwe</option>
                                </select>

                                <input value='<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>' id="getcountval" type="hidden">

                                <script>

                                    var countryval = document.getElementById('getcountval').value;

                                    $('[name=country] option').filter(function() {
                                        if (countryval == 'GB') {
                                            return ($(this).text() == 'United Kingdom');
                                        } else if (countryval == 'US') {
                                            return ($(this).text() == 'United States');
                                        } else if (countryval == 'AF') {
                                            return ($(this).text() == 'Afghanistan');
                                        } else if (countryval == 'AX') {
                                            return ($(this).text() == 'Aland Islands');
                                        } else if (countryval == 'AL') {
                                            return ($(this).text() == 'Albania');
                                        } else if (countryval == 'DZ') {
                                            return ($(this).text() == 'Algeria');
                                        } else if (countryval == 'AS') {
                                            return ($(this).text() == 'American Samoa');
                                        } else if (countryval == 'AD') {
                                            return ($(this).text() == 'Andorra');
                                        } else if (countryval == 'AO') {
                                            return ($(this).text() == 'Angola');
                                        } else if (countryval == 'AI') {
                                            return ($(this).text() == 'Anguilla');
                                        } else if (countryval == 'AQ') {
                                            return ($(this).text() == 'Antarctica');
                                        } else if (countryval == 'AG') {
                                            return ($(this).text() == 'Antigua and Barbuda');
                                        } else if (countryval == 'AR') {
                                            return ($(this).text() == 'Argentina');
                                        } else if (countryval == 'AM') {
                                            return ($(this).text() == 'Armenia');
                                        } else if (countryval == 'AW') {
                                            return ($(this).text() == 'Aruba');
                                        } else if (countryval == 'AU') {
                                            return ($(this).text() == 'Australia');
                                        } else if (countryval == 'AT') {
                                            return ($(this).text() == 'Austria');
                                        } else if (countryval == 'AZ') {
                                            return ($(this).text() == 'Azerbaijan');
                                        } else if (countryval == 'BS') {
                                            return ($(this).text() == 'Bahamas');
                                        } else if (countryval == 'BH') {
                                            return ($(this).text() == 'Bahrain');
                                        } else if (countryval == 'BD') {
                                            return ($(this).text() == 'Bangladesh');
                                        } else if (countryval == 'BB') {
                                            return ($(this).text() == 'Barbados');
                                        } else if (countryval == 'BY') {
                                            return ($(this).text() == 'Belarus');
                                        } else if (countryval == 'BE') {
                                            return ($(this).text() == 'Belgium');
                                        } else if (countryval == 'BZ') {
                                            return ($(this).text() == 'Belize');
                                        } else if (countryval == 'BJ') {
                                            return ($(this).text() == 'Benin');
                                        } else if (countryval == 'BM') {
                                            return ($(this).text() == 'Bermuda');
                                        } else if (countryval == 'BT') {
                                            return ($(this).text() == 'Bhutan');
                                        } else if (countryval == 'BO') {
                                            return ($(this).text() == 'Bolivia');
                                        } else if (countryval == 'BA') {
                                            return ($(this).text() == 'Bosnia and Herzegovina');
                                        } else if (countryval == 'BW') {
                                            return ($(this).text() == 'Botswana');
                                        } else if (countryval == 'BV') {
                                            return ($(this).text() == 'Bouvet Island');
                                        } else if (countryval == 'BR') {
                                            return ($(this).text() == 'Brazil');
                                        } else if (countryval == 'IO') {
                                            return ($(this).text() == 'British Indian Ocean Territory');
                                        } else if (countryval == 'VG') {
                                            return ($(this).text() == 'British Virgin Islands');
                                        } else if (countryval == 'BN') {
                                            return ($(this).text() == 'Brunei');
                                        } else if (countryval == 'BG') {
                                            return ($(this).text() == 'Bulgaria');
                                        } else if (countryval == 'BF') {
                                            return ($(this).text() == 'Burkina Faso');
                                        } else if (countryval == 'BI') {
                                            return ($(this).text() == 'Burundi');
                                        } else if (countryval == 'KH') {
                                            return ($(this).text() == 'Cambodia');
                                        } else if (countryval == 'CM') {
                                            return ($(this).text() == 'Cameroon');
                                        } else if (countryval == 'CA') {
                                            return ($(this).text() == 'Canada');
                                        } else if (countryval == 'CV') {
                                            return ($(this).text() == 'Cape Verde');
                                        } else if (countryval == 'KY') {
                                            return ($(this).text() == 'Cayman Islands');
                                        } else if (countryval == 'CF') {
                                            return ($(this).text() == 'Central African Republic');
                                        } else if (countryval == 'TD') {
                                            return ($(this).text() == 'Chad');
                                        } else if (countryval == 'CL') {
                                            return ($(this).text() == 'Chile');
                                        } else if (countryval == 'CN') {
                                            return ($(this).text() == 'China');
                                        } else if (countryval == 'CX') {
                                            return ($(this).text() == 'Christmas Island');
                                        } else if (countryval == 'CC') {
                                            return ($(this).text() == 'Cocos (Keeling) Islands');
                                        } else if (countryval == 'CO') {
                                            return ($(this).text() == 'Colombia');
                                        } else if (countryval == 'KM') {
                                            return ($(this).text() == 'Comoros');
                                        } else if (countryval == 'CK') {
                                            return ($(this).text() == 'Cook Islands');
                                        } else if (countryval == 'CR') {
                                            return ($(this).text() == 'Costa Rica');
                                        } else if (countryval == 'CI') {
                                            return ($(this).text() == 'Cote d` Ivoire ');
                                        } else if (countryval == 'HR') {
                                            return ($(this).text() == 'Croatia');
                                        }
                                        else if (countryval == 'CU')
                                        {
                                            return ($(this).text() == 'Cuba');

                                        }
                                        else if (countryval == 'CW')
                                        {
                                            return ($(this).text() == 'Curacao');

                                        }
                                        else if (countryval == 'CY') {
                                            return ($(this).text() == 'Cyprus');
                                        } else if (countryval == 'CZ') {
                                            return ($(this).text() == 'Czech Republic');
                                        } else if (countryval == 'CD') {
                                            return ($(this).text() == 'Democratic Republic of the Congo');
                                        } else if (countryval == 'DK') {
                                            return ($(this).text() == 'Denmark');
                                        } else if (countryval == 'DJ') {
                                            return ($(this).text() == 'Djibouti');
                                        } else if (countryval == 'DM') {
                                            return ($(this).text() == 'Dominica');
                                        } else if (countryval == 'DO') {
                                            return ($(this).text() == 'Dominican Republic');
                                        } else if (countryval == 'EC') {
                                            return ($(this).text() == 'Ecuador');
                                        } else if (countryval == 'EG') {
                                            return ($(this).text() == 'Egypt');
                                        } else if (countryval == 'SV') {
                                            return ($(this).text() == 'El Salvador');
                                        } else if (countryval == 'GQ') {
                                            return ($(this).text() == 'Equatorial Guinea');
                                        } else if (countryval == 'ER') {
                                            return ($(this).text() == 'Eritrea');
                                        } else if (countryval == 'EE') {
                                            return ($(this).text() == 'Estonia');
                                        } else if (countryval == 'ET') {
                                            return ($(this).text() == 'Ethiopia');
                                        } else if (countryval == 'FK') {
                                            return ($(this).text() == 'Falkland Islands');
                                        } else if (countryval == 'FO') {
                                            return ($(this).text() == 'Faroe Islands');
                                        } else if (countryval == 'FJ') {
                                            return ($(this).text() == 'Fiji');
                                        } else if (countryval == 'FI') {
                                            return ($(this).text() == 'Finland');
                                        } else if (countryval == 'FR') {
                                            return ($(this).text() == 'France');
                                        } else if (countryval == 'GF') {
                                            return ($(this).text() == 'French Guiana');
                                        } else if (countryval == 'PF') {
                                            return ($(this).text() == 'French Polynesia');
                                        } else if (countryval == 'TF') {
                                            return ($(this).text() == 'French Southern and Antarctic Lands');
                                        } else if (countryval == 'GA') {
                                            return ($(this).text() == 'Gabon');
                                        } else if (countryval == 'GM') {
                                            return ($(this).text() == 'Gambia');
                                        } else if (countryval == 'GE') {
                                            return ($(this).text() == 'Georgia');
                                        } else if (countryval == 'DE') {
                                            return ($(this).text() == 'Germany');
                                        } else if (countryval == 'GH') {
                                            return ($(this).text() == 'Ghana');
                                        } else if (countryval == 'GI') {
                                            return ($(this).text() == 'Gibraltar');
                                        } else if (countryval == 'GR') {
                                            return ($(this).text() == 'Greece');
                                        } else if (countryval == 'GL') {
                                            return ($(this).text() == 'Greenland');
                                        } else if (countryval == 'GD') {
                                            return ($(this).text() == 'Grenada');
                                        } else if (countryval == 'GP') {
                                            return ($(this).text() == 'Guadeloupe');
                                        } else if (countryval == 'GU') {
                                            return ($(this).text() == 'Guam');
                                        } else if (countryval == 'GT') {
                                            return ($(this).text() == 'Guatemala');
                                        } else if (countryval == 'GG') {
                                            return ($(this).text() == 'Guernsey');
                                        } else if (countryval == 'GN') {
                                            return ($(this).text() == 'Guinea');
                                        } else if (countryval == 'GW') {
                                            return ($(this).text() == 'Guinea-Bissau');
                                        } else if (countryval == 'GY') {
                                            return ($(this).text() == 'Guyana');
                                        } else if (countryval == 'HT') {
                                            return ($(this).text() == 'Haiti');
                                        } else if (countryval == 'HM') {
                                            return ($(this).text() == 'Heard Island & McDonald Islands');
                                        } else if (countryval == 'HN') {
                                            return ($(this).text() == 'Honduras');
                                        } else if (countryval == 'HK') {
                                            return ($(this).text() == 'Hong Kong');
                                        } else if (countryval == 'HU') {
                                            return ($(this).text() == 'Hungary');
                                        } else if (countryval == 'IS') {
                                            return ($(this).text() == 'Iceland');
                                        } else if (countryval == 'IN') {
                                            return ($(this).text() == 'India');
                                        } else if (countryval == 'ID') {
                                            return ($(this).text() == 'Indonesia');
                                        } else if (countryval == 'IR') {
                                            return ($(this).text() == 'Iran');
                                        } else if (countryval == 'IQ') {
                                            return ($(this).text() == 'Iraq');
                                        } else if (countryval == 'IE') {
                                            return ($(this).text() == 'Ireland');
                                        } else if (countryval == 'IL') {
                                            return ($(this).text() == 'Israel');
                                        } else if (countryval == 'IT') {
                                            return ($(this).text() == 'Italy');
                                        } else if (countryval == 'JM') {
                                            return ($(this).text() == 'Jamaica');
                                        } else if (countryval == 'JP') {
                                            return ($(this).text() == 'Japan');
                                        } else if (countryval == 'JE') {
                                            return ($(this).text() == 'Jersey');
                                        } else if (countryval == 'JO') {
                                            return ($(this).text() == 'Jordan');
                                        } else if (countryval == 'KZ') {
                                            return ($(this).text() == 'Kazakhstan');
                                        } else if (countryval == 'KE') {
                                            return ($(this).text() == 'Kenya');
                                        } else if (countryval == 'KI') {
                                            return ($(this).text() == 'Kiribati');
                                        } else if (countryval == 'KW') {
                                            return ($(this).text() == 'Kuwait');
                                        } else if (countryval == 'KG') {
                                            return ($(this).text() == 'Kyrgyzstan');
                                        } else if (countryval == 'LA') {
                                            return ($(this).text() == 'Laos');
                                        } else if (countryval == 'LV') {
                                            return ($(this).text() == 'Latvia');
                                        } else if (countryval == 'LB') {
                                            return ($(this).text() == 'Lebanon');
                                        } else if (countryval == 'LS') {
                                            return ($(this).text() == 'Lesotho');
                                        } else if (countryval == 'LR') {
                                            return ($(this).text() == 'Liberia');
                                        } else if (countryval == 'LY') {
                                            return ($(this).text() == 'Libya');
                                        } else if (countryval == 'LI') {
                                            return ($(this).text() == 'Liechtenstein');
                                        } else if (countryval == 'LT') {
                                            return ($(this).text() == 'Lithuania');
                                        } else if (countryval == 'LU') {
                                            return ($(this).text() == 'Luxembourg');
                                        } else if (countryval == 'MO') {
                                            return ($(this).text() == 'Macau, China');
                                        } else if (countryval == 'MK') {
                                            return ($(this).text() == 'Macedonia');
                                        } else if (countryval == 'MG') {
                                            return ($(this).text() == 'Madagascar');
                                        } else if (countryval == 'MW') {
                                            return ($(this).text() == 'Malawi');
                                        } else if (countryval == 'MY') {
                                            return ($(this).text() == 'Malaysia');
                                        } else if (countryval == 'MV') {
                                            return ($(this).text() == 'Maldives');
                                        } else if (countryval == 'ML') {
                                            return ($(this).text() == 'Mali');
                                        } else if (countryval == 'MT') {
                                            return ($(this).text() == 'Malta');
                                        } else if (countryval == 'MH') {
                                            return ($(this).text() == 'Marshall Islands');
                                        } else if (countryval == 'MQ') {
                                            return ($(this).text() == 'Martinique');
                                        } else if (countryval == 'MR') {
                                            return ($(this).text() == 'Mauritania');
                                        } else if (countryval == 'MU') {
                                            return ($(this).text() == 'Mauritius');
                                        } else if (countryval == 'YT') {
                                            return ($(this).text() == 'Mayotte');
                                        } else if (countryval == 'MX') {
                                            return ($(this).text() == 'Mexico');
                                        } else if (countryval == 'FM') {
                                            return ($(this).text() == 'Micronesia, Federated States of');
                                        } else if (countryval == 'MD') {
                                            return ($(this).text() == 'Moldova');
                                        } else if (countryval == 'MC') {
                                            return ($(this).text() == 'Monaco');
                                        } else if (countryval == 'MN') {
                                            return ($(this).text() == 'Mongolia');
                                        } else if (countryval == 'ME') {
                                            return ($(this).text() == 'Montenegro');
                                        } else if (countryval == 'MS') {
                                            return ($(this).text() == 'Montserrat');
                                        } else if (countryval == 'MA') {
                                            return ($(this).text() == 'Morocco');
                                        } else if (countryval == 'MZ') {
                                            return ($(this).text() == 'Mozambique');
                                        } else if (countryval == 'MM') {
                                            return ($(this).text() == 'Myanmar');
                                        } else if (countryval == 'NA') {
                                            return ($(this).text() == 'Namibia');
                                        } else if (countryval == 'NR') {
                                            return ($(this).text() == 'Nauru');
                                        } else if (countryval == 'NP') {
                                            return ($(this).text() == 'Nepal');
                                        } else if (countryval == 'AN') {
                                            return ($(this).text() == 'Netherlands Antilles');
                                        } else if (countryval == 'NL') {
                                            return ($(this).text() == 'Netherlands');
                                        } else if (countryval == 'NC') {
                                            return ($(this).text() == 'New Caledonia');
                                        } else if (countryval == 'NZ') {
                                            return ($(this).text() == 'New Zealand');
                                        } else if (countryval == 'NI') {
                                            return ($(this).text() == 'Nicaragua');
                                        } else if (countryval == 'NE') {
                                            return ($(this).text() == 'Niger');
                                        } else if (countryval == 'NG') {
                                            return ($(this).text() == 'Nigeria');
                                        } else if (countryval == 'NU') {
                                            return ($(this).text() == 'Niue');
                                        } else if (countryval == 'NF') {
                                            return ($(this).text() == 'Norfolk Island');
                                        } else if (countryval == 'KP') {
                                            return ($(this).text() == 'North Korea');
                                        } else if (countryval == 'MP') {
                                            return ($(this).text() == 'Northern Mariana Islands');
                                        } else if (countryval == 'NO') {
                                            return ($(this).text() == 'Norway');
                                        } else if (countryval == 'OM') {
                                            return ($(this).text() == 'Oman');
                                        } else if (countryval == 'PK') {
                                            return ($(this).text() == 'Pakistan');
                                        } else if (countryval == 'PW') {
                                            return ($(this).text() == 'Palau');
                                        } else if (countryval == 'PS') {
                                            return ($(this).text() == 'Palestinian Authority');
                                        } else if (countryval == 'PA') {
                                            return ($(this).text() == 'Panama');
                                        } else if (countryval == 'PG') {
                                            return ($(this).text() == 'Papua New Guinea');
                                        } else if (countryval == 'PY') {
                                            return ($(this).text() == 'Paraguay');
                                        } else if (countryval == 'PE') {
                                            return ($(this).text() == 'Peru');
                                        } else if (countryval == 'PH') {
                                            return ($(this).text() == 'Philippines');
                                        } else if (countryval == 'PN') {
                                            return ($(this).text() == 'Pitcairn Islands');
                                        } else if (countryval == 'PL') {
                                            return ($(this).text() == 'Poland');
                                        } else if (countryval == 'PT') {
                                            return ($(this).text() == 'Portugal');
                                        } else if (countryval == 'PR') {
                                            return ($(this).text() == 'Puerto Rico');
                                        } else if (countryval == 'QA') {
                                            return ($(this).text() == 'Qatar');
                                        } else if (countryval == 'CG') {
                                            return ($(this).text() == 'Republic of the Congo');
                                        } else if (countryval == 'RE') {
                                            return ($(this).text() == 'Reunion');
                                        } else if (countryval == 'RO') {
                                            return ($(this).text() == 'Romania');
                                        } else if (countryval == 'RU') {
                                            return ($(this).text() == 'Russia');
                                        } else if (countryval == 'RW') {
                                            return ($(this).text() == 'Rwanda');
                                        } else if (countryval == 'BL') {
                                            return ($(this).text() == 'Saint Barthelemy');
                                        } else if (countryval == 'SH') {
                                            return ($(this).text() == 'Saint Helena, Ascension & Tristan daCunha');
                                        } else if (countryval == 'KN') {
                                            return ($(this).text() == 'Saint Kitts and Nevis');
                                        } else if (countryval == 'LC') {
                                            return ($(this).text() == 'Saint Lucia');
                                        } else if (countryval == 'MF') {
                                            return ($(this).text() == 'Saint Martin');
                                        } else if (countryval == 'PM') {
                                            return ($(this).text() == 'Saint Pierre and Miquelon');
                                        } else if (countryval == 'VC') {
                                            return ($(this).text() == 'Saint Vincent and Grenadines');
                                        } else if (countryval == 'WS') {
                                            return ($(this).text() == 'Samoa');
                                        } else if (countryval == 'SM') {
                                            return ($(this).text() == 'San Marino');
                                        } else if (countryval == 'ST') {
                                            return ($(this).text() == 'Sao Tome and Principe');
                                        } else if (countryval == 'SA') {
                                            return ($(this).text() == 'Saudi Arabia');
                                        } else if (countryval == 'SN') {
                                            return ($(this).text() == 'Senegal');
                                        } else if (countryval == 'RS') {
                                            return ($(this).text() == 'Serbia');
                                        } else if (countryval == 'SC') {
                                            return ($(this).text() == 'Seychelles');
                                        } else if (countryval == 'SL') {
                                            return ($(this).text() == 'Sierra Leone');
                                        } else if (countryval == 'SG') {
                                            return ($(this).text() == 'Singapore');
                                        } else if (countryval == 'SK') {
                                            return ($(this).text() == 'Slovakia');
                                        } else if (countryval == 'SI') {
                                            return ($(this).text() == 'Slovenia');
                                        } else if (countryval == 'SB') {
                                            return ($(this).text() == 'Solomon Islands');
                                        } else if (countryval == 'SO') {
                                            return ($(this).text() == 'Somalia');
                                        } else if (countryval == 'ZA') {
                                            return ($(this).text() == 'South Africa');
                                        } else if (countryval == 'GS') {
                                            return ($(this).text() == 'South Georgia & South Sandwich Islands');
                                        } else if (countryval == 'KR') {
                                            return ($(this).text() == 'South Korea');
                                        } else if (countryval == 'ES') {
                                            return ($(this).text() == 'Spain');
                                        } else if (countryval == 'LK') {
                                            return ($(this).text() == 'Sri Lanka');
                                        } else if (countryval == 'SD') {
                                            return ($(this).text() == 'Sudan');
                                        } else if (countryval == 'SR') {
                                            return ($(this).text() == 'Suriname');
                                        } else if (countryval == 'SJ') {
                                            return ($(this).text() == 'Svalbard and Jan Mayen');
                                        } else if (countryval == 'SZ') {
                                            return ($(this).text() == 'Swaziland');
                                        } else if (countryval == 'SE') {
                                            return ($(this).text() == 'Sweden');
                                        } else if (countryval == 'CH') {
                                            return ($(this).text() == 'Switzerland');
                                        } else if (countryval == 'SY') {
                                            return ($(this).text() == 'Syria');
                                        } else if (countryval == 'TW') {
                                            return ($(this).text() == 'Taiwan');
                                        } else if (countryval == 'TJ') {
                                            return ($(this).text() == 'Tajikistan');
                                        } else if (countryval == 'TZ') {
                                            return ($(this).text() == 'Tanzania');
                                        } else if (countryval == 'TH') {
                                            return ($(this).text() == 'Thailand');
                                        } else if (countryval == 'TL') {
                                            return ($(this).text() == 'Timor-Leste');
                                        } else if (countryval == 'TG') {
                                            return ($(this).text() == 'Togo');
                                        } else if (countryval == 'TK') {
                                            return ($(this).text() == 'Tokelau');
                                        } else if (countryval == 'TO') {
                                            return ($(this).text() == 'Tonga');
                                        } else if (countryval == 'TT') {
                                            return ($(this).text() == 'Trinidad and Tobago');
                                        } else if (countryval == 'TN') {
                                            return ($(this).text() == 'Tunisia');
                                        } else if (countryval == 'TR') {
                                            return ($(this).text() == 'Turkey');
                                        } else if (countryval == 'TM') {
                                            return ($(this).text() == 'Turkmenistan');
                                        } else if (countryval == 'TC') {
                                            return ($(this).text() == 'Turks and Caicos Islands');
                                        } else if (countryval == 'TV') {
                                            return ($(this).text() == 'Tuvalu');
                                        } else if (countryval == 'UG') {
                                            return ($(this).text() == 'Uganda');
                                        } else if (countryval == 'UA') {
                                            return ($(this).text() == 'Ukraine');
                                        } else if (countryval == 'AE') {
                                            return ($(this).text() == 'United Arab Emirates');
                                        } else if (countryval == 'VI') {
                                            return ($(this).text() == 'United States Virgin Islands');
                                        } else if (countryval == 'UY') {
                                            return ($(this).text() == 'Uruguay');
                                        } else if (countryval == 'UZ') {
                                            return ($(this).text() == 'Uzbekistan');
                                        } else if (countryval == 'VU') {
                                            return ($(this).text() == 'Vanuatu');
                                        } else if (countryval == 'VA') {
                                            return ($(this).text() == 'Vatican City');
                                        } else if (countryval == 'VE') {
                                            return ($(this).text() == 'Venezuela');
                                        } else if (countryval == 'VN') {
                                            return ($(this).text() == 'Vietnam');
                                        } else if (countryval == 'WF') {
                                            return ($(this).text() == 'Wallis and Futuna');
                                        } else if (countryval == 'EH') {
                                            return ($(this).text() == 'Western Sahara');
                                        } else if (countryval == 'YE') {
                                            return ($(this).text() == 'Yemen');
                                        } else if (countryval == 'ZM') {
                                            return ($(this).text() == 'Zambia');
                                        } else if (countryval == 'ZW') {
                                            return ($(this).text() == 'Zimbabwe');
                                        } else {
                                            return ($(this).text() == 'Select Country');
                                        }
                                    }).prop('selected', true);

                                </script>

                        </tr>
                        <tr><td colspan="5">&nbsp;</td></tr>
                        <tr>
                            <td colspan="4" class="textb"><span class="textb">Support Number*</span></td>
                            <td align="right">
                                <input class="txtbox" type="Text" maxlength="05"style="width: 50px" name="phonecc" placeholder="phonecc" size="35" size="20" value="<%=phonecc%>">
                                <input class="txtbox" type="Text" maxlength="11" style="width: 90px" name="telno" size="35" size="20" value="<%=telno%>">
                            </td>
                        </tr>

                        <input type="hidden" name="emailtoken" value="<%=etoken%>">




                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr><td colspan="4" class="textb" align="right"><h5><b><u>Contact Details</u></b></h5></td> </tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td align="left" class="textb">Main Contact*</td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>" name="contact_persons" size="35" placeholder="Name*" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" size="35" placeholder="Email*" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>" name="maincontact_ccmailid" size="35" placeholder="Email Cc"  >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_bccMailid)%>" name="maincontact_bccmailid" size="35" placeholder="Email BCC"  >
                            </td>
                            <td>
                                <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_phone)%>" name="maincontact_phone" size="35" placeholder="Phone">
                            </td>
                        </tr>

                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td align="left" class="textb">Customer Support*:</td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_persons)%>" name="support_persons" size="35" placeholder="Name*" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_emails)%>" name="support_emails" size="35" placeholder="Email*" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_cCmailid)%>" name="support_ccmailid" size="35" placeholder="Email Cc"  >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_bccMailid)%>" name="support_bccmailid" size="35" placeholder="Email BCC"  >
                            </td>
                            <td>
                                <input class="txtbox" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_phone)%>" name="support_phone" size="35" placeholder="Phone">
                            </td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td align="left" class="textb">Chargeback Contact:</td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_name)%>" name="cbcontact_name" size="35" placeholder="Name" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_mailId)%>" name="cbcontact_mailid" size="35" placeholder="Email" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>" name="cbcontact_ccmailid" size="35" placeholder="Email Cc"  >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_bccMailId)%>" name="cbcontact_bccmailid" size="35" placeholder="Email BCC"  >
                            </td>
                            <td>
                                <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_phone)%>" name="cbcontact_phone" size="35" placeholder="Phone">
                            </td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td align="left" class="textb">Refund Contact:</td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_name)%>" name="refundcontact_name" size="35" placeholder="Name" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_mailId)%>" name="refundcontact_mailid" size="35" placeholder="Email" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>" name="refundcontact_ccmailid"size="35" placeholder="Email Cc"  >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_bccMail)%>" name="refundcontact_bccmailid"size="35" placeholder="Email BCC"  >
                            </td>
                            <td>
                                <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_phone)%>" name="refundcontact_phone" size="35" placeholder="Phone">
                            </td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td align="left" class="textb">Sales Contact:</td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_name)%>" name="salescontact_name" size="35" placeholder="Name" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value=""<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_mailid)%> name="salescontact_mailid" size="35" placeholder="Email" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>" name="salescontact_ccmailid" size="35" placeholder="Email Cc"  >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_bccMailId)%>" name="salescontact_bccmailid" size="35" placeholder="Email BCC"  >
                            </td>
                            <td>
                                <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_phone)%>" name="salescontact_phone"size="35" placeholder="Phone">
                            </td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td align="left" class="textb">Billing Contact:</td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_name)%>" name="billingcontact_name" size="35" placeholder="Name" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_mailid)%>" name="billingcontact_mailid" size="35" placeholder="Email" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>" name="billingcontact_ccmailid" size="35" placeholder="Email Cc"  >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_bccMailId)%>" name="billingcontact_bccmailid" size="35" placeholder="Email BCC"  >
                            </td>
                            <td>
                                <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_phone)%>" name="billingcontact_phone" size="35" placeholder="Phone" >
                            </td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td align="left" class="textb">Fraud Contact:</td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_name)%>" name="fraudcontact_name"size="35" placeholder="Name" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_mailid)%>" name="fraudcontact_mailid" size="35" placeholder="Email" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>" name="fraudcontact_ccmailid" size="35" placeholder="Email Cc"  >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_bccMailId)%>" name="fraudcontact_bccmailid" size="35" placeholder="Email BCC"  >
                            </td>
                            <td>
                                <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_phone)%>" name="fraudcontact_phone" size="35" placeholder="Phone">
                            </td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td align="left" class="textb" >Technical Contact:</td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_name)%>" name="technicalcontact_name" size="35" placeholder="Name" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_mailId)%>"name="technicalcontact_mailid" size="35" placeholder="Email" >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>" name="technicalcontact_ccmailid"size="35" placeholder="Email Cc"  >
                            </td>
                            <td align="center" style="padding:10px;">
                                <input class="txtbox" type="text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_bccMailId)%>" name="technicalcontact_bccmailid"size="35" placeholder="Email BCC"  >
                            </td>
                            <td>
                                <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_phone)%>" name="technicalcontact_phone" size="35" placeholder="Phone">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6"></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <td colspan="4" align="right">

                            <input type="hidden" value="1" name="step">
                            <button type="submit" name="submit" class="buttonform" id="submit" disabled="disabled">
                                <i class="fa fa-save"></i>
                                Submit
                            </button> </td>
                        </tr>
                        <tr>
                            <td colspan="5"></td>
                       </tr>
                    </table>
                </td>
            </tr>
        </table>
    </form>
    <p class="textb" align="center" style="font-weight: bold;">KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES</p>
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