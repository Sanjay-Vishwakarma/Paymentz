<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: saurabh
  Date: 2/18/14
  Time: 9:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>
<title></title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
<script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>
<script language="javascript">
</script>
</head>

<%
    //ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<body onload="return test();">
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default">
<div class="panel-heading">
    Add New Agent
    <div style="float: right;">
        <form action="/icici/agentInterface.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" name="submit" class="addnewmember">
                <i class="fa fa-sign-in"></i>
                &nbsp;&nbsp;Agent Details
            </button>
        </form>
    </div>
</div>
    <div align="center" class="textb"><h5><b><u>Agent Information</u></b></h5></div>
            <%
        String username = "";
        String passwd = "";
        String conpasswd = "";
        String company_name = "";
        String sitename = "";
        String supporturl = "";
        String notifyemail = "";

        String contact_emails = "";
        String contact_persons = "";
        String mainContact_cCmailid="";
        String mainContact_phone="";

        String refundContact_name="";
        String refundContact_mailId="";
        String refundContact_cCmail="";
        String refundContact_phone="";

        String cbContact_name="";
        String cbContact_mailId="";
        String cbContact_cCmailId="";
        String cbContact_phone="";

        String salesContact_name="";
        String salesContact_mailid="";
        String salesContact_cCmailId="";
        String salesContact_phone="";

        String billingContact_name="";
        String billingContact_mailid="";
        String billingContact_cCmailId="";
        String billingContact_phone="";

        String fraudContact_name="";
        String fraudContact_mailid="";
        String fraudContact_cCmailId="";
        String fraudContact_phone="";

        String technicalContact_name="";
        String technicalContact_mailId="";
        String technicalContact_cCmailId="";
        String technicalContact_phone="";


        String telno = "";
        String logo = "";
        String country = "";
        String emailTemplateLang = "";

        String errormsg=(String)request.getAttribute("error");
        Hashtable details = (Hashtable) request.getAttribute("details");
                Map<String,String> erjbMap= new LinkedHashMap<String, String>();
                erjbMap.put("RO","RO");
                erjbMap.put("JA","JA");
                erjbMap.put("BG","BG");
                erjbMap.put("EN","EN");
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
            if (details.get("telno") != null) telno = (String) details.get("telno");
            if (details.get("country") != null) country = (String) details.get("country");
            if (details.get("supporturl") != null) supporturl = (String) details.get("supporturl");
            if (details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");

            if (details.get("logoName") != null) logo = (String) details.get("logoName");

            if (details.get("contact_emails") != null) contact_emails = (String) details.get("contact_emails");
            if (details.get("contact_persons") != null) contact_persons = (String) details.get("contact_persons");
            if (details.get("maincontact_ccmailid") != null) mainContact_cCmailid = (String) details.get("maincontact_ccmailid");
            if (details.get("maincontact_phone") != null) mainContact_phone = (String) details.get("maincontact_phone");

            if (details.get("salescontact_name") != null) salesContact_name = (String) details.get("salescontact_name");
            if (details.get("salescontact_mailid") != null) salesContact_mailid = (String) details.get("salescontact_mailid");
            if (details.get("salescontact_ccmailid") != null) salesContact_cCmailId = (String) details.get("salescontact_ccmailid");
            if (details.get("salescontact_phone") != null) salesContact_phone = (String) details.get("salescontact_phone");

            if (details.get("refundcontact_name") != null) refundContact_name = (String) details.get("refundcontact_name");
            if (details.get("refundcontact_mailid") != null) refundContact_mailId = (String) details.get("refundcontact_mailid");
            if (details.get("refundcontact_ccmailid") != null) refundContact_cCmail = (String) details.get("refundcontact_ccmailid");
            if (details.get("refundcontact_phone") != null) refundContact_phone = (String) details.get("refundcontact_phone");

            if (details.get("cbcontact_name") != null) cbContact_name = (String) details.get("cbcontact_name");
            if (details.get("cbcontact_mailid") != null) cbContact_mailId = (String) details.get("cbcontact_mailid");
            if (details.get("cbcontact_ccmailid") != null) cbContact_cCmailId = (String) details.get("cbcontact_ccmailid");
            if (details.get("cbcontact_phone") != null) cbContact_phone = (String) details.get("cbcontact_phone");

            if (details.get("billingcontact_name") != null) billingContact_name = (String) details.get("billingcontact_name");
            if (details.get("billingcontact_mailid") != null) billingContact_mailid = (String) details.get("billingcontact_mailid");
            if (details.get("billingcontact_ccmailId") != null) billingContact_cCmailId = (String) details.get("billingcontact_ccmailId");
            if (details.get("bllingcontact_phone") != null) billingContact_phone = (String) details.get("bllingcontact_phone");

            if (details.get("fraudcontact_name") != null) fraudContact_name = (String) details.get("fraudcontact_name");
            if (details.get("fraudcontact_mailid") != null) fraudContact_mailid = (String) details.get("fraudcontact_mailid");
            if (details.get("fraudcontact_ccmailid") != null) fraudContact_cCmailId = (String) details.get("fraudcontact_ccmailid");
            if (details.get("fraudcontact_phone") != null) fraudContact_phone = (String) details.get("fraudcontact_phone");

            if (details.get("technicalcontact_name") != null) technicalContact_name = (String) details.get("technicalcontact_name");
            if (details.get("technicalcontact_mailid") != null) technicalContact_mailId = (String) details.get("technicalcontact_mailid");
            if (details.get("technicalcontact_ccmailid") != null) technicalContact_cCmailId = (String) details.get("technicalcontact_ccmailid");
            if (details.get("technicalcontact_phone") != null) technicalContact_phone = (String) details.get("technicalcontact_phone");
            if (details.get("emailTemplateLang") != null) emailTemplateLang = (String) details.get("emailTemplateLang");
        }
    %>
        <form action="/icici/servlet/NewAgent?ctoken=<%=ctoken%>" method="post" name="form1">
            <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
            <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
            <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor=white align="center">
                <tr>
                    <td>
                        <table border="0" cellpadding="5" cellspacing="0" width="90%"  align="center" style="margin-left:100px">
                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                                <td colspan="4" width="43%" class="textb"><span class="textb">Username*</span>(Username Should Not Contain Special Characters like !@#$%)</td>
                                <td width="20%"><input class="txtbox" type="Text" maxlength="100"  maxlength = 100 value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="35"></td>
                            </tr>
                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                                <td colspan="4" class="textb"><span class="textb">Password*</span><br>(Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)</td>
                                <td><input  id="passwd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                            value="<%=ESAPI.encoder().encodeForHTMLAttribute(passwd)%>" name="passwd" size="20"></td>
                            </tr>
                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                                <td colspan="4" class="textb"><span class="textb">Confirm Password*</span>(Should be same as PASSWORD)</td>
                                <td><input id="conpasswd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(conpasswd)%>" name="conpasswd" size="20"></td>
                            </tr>
                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                            <tr>
                                <td colspan="4" class="textb">Agent Organisation Name*</td>
                                <td><input class="txtbox" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" name="company_name" size="35" placeholder Name="Org"></td>
                            </tr>

                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                                <td colspan="4" class="textb"><span class="textb">Site URL*</span>(Ex. http://www.abc.com)</td>
                                <td><input class="txtbox" type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" name="sitename" size="35"></td>
                            </tr>

                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                                <td colspan="4" class="textb"><span class="textb">Support Number*</span><br>
                                    (Telephone Number)</td>
                                <td><input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>" name="telno" size="35" size="20"></td>
                            </tr>

                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                                <td colspan="4" class="textb"><span class="textb">Support URL*</span></td>
                                <td><input class="txtbox" type="Text" maxlength="255" value="<%=ESAPI.encoder().encodeForHTMLAttribute(supporturl)%>" name="supporturl" size="35" size="20"></td>
                            </tr>

                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                                <td colspan="4" class="textb"><span class="textb">Notify Email Id*</span></td>
                                <td><input class="txtbox" type="Text" maxlength="255" value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>" name="notifyemail" size="35" size="20"></td>
                            </tr>

                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                                <td colspan="4" class="textb"><span class="textb">Email Template Language</span></td>
                                <td class="textb">
                                    <select name="emailTemplateLang" class="txtbox">
                                        <%
                                            if ("RO".equals(emailTemplateLang))
                                            {
                                        %>
                                        <option value="RO" selected>RO</option>
                                        <option value="JA">JA</option>
                                        <option value="BG">BG</option>
                                        <option value="EN">EN</option>
                                        <%
                                        }
                                        else if ("JA".equals(emailTemplateLang))
                                        {
                                        %>
                                        <option value="RO">RO</option>
                                        <option value="JA" selected>JA</option>
                                        <option value="BG">BG</option>
                                        <option value="EN">EN</option>
                                        <%
                                        }
                                        else if ("BG".equals(emailTemplateLang))
                                        {
                                        %>
                                        <option value="RO">RO</option>
                                        <option value="JA">JA</option>
                                        <option value="BG" selected>BG</option>
                                        <option value="EN">EN</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="RO">RO</option>
                                        <option value="JA">JA</option>
                                        <option value="BG">BG</option>
                                        <option value="EN" selected>EN</option>
                                        <% }
                                        %>
                                    </select>
                                </td>
                            </tr>

                            <tr><td colspan="5">&nbsp;</td></tr>
                            <tr>
                                <td colspan="4" class="textb"><span class="textb">Country*</span></td>
                                <td><input class="txtbox" type="Text" maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>" name="country" size="35" size="20"></td>
                            </tr>

                            <tr><td colspan="4">&nbsp;</td></tr>
                            <tr>
                            <td class="textb" colspan="4"><span class="textb">Logo Name*</span><br>
                            (Ex. logo.jpg)</td>
                            <td><input class="txtbox" type="Text" maxlength="30"   value="<%=ESAPI.encoder().encodeForHTMLAttribute(logo)%>" name="logoName" size="35"></td>
                            </tr>

                            <tr><td colspan="4">&nbsp;</td></tr>
                            <tr>
                            <td class="textb"colspan="4">Is IP Whitelisted*</td>
                            <td><select name="isipwhitelisted">
                            <option value="N">N</option>
                            <option value="Y">Y</option></select></td>
                            </tr>

                            <tr><td colspan="4">&nbsp;</td></tr>
                            <tr>
                            <td class="textb" colspan="4"><span class="">Partner Id*</span></td>
                            <td class="txtboxsignup">
                                <input name="partnerId" class="txtbox" id="allpid">
                            <%--<select name="partnerId" class="txtbox">
                            <%
                            Connection conn = Database.getConnection();
                            String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                            PreparedStatement pstmt = conn.prepareStatement(query);
                            ResultSet rs = pstmt.executeQuery();
                            StringBuffer partnerData = new StringBuffer();
                            while (rs.next())
                            {
                            partnerData.append("<option value='" + rs.getInt("partnerId") + "'>" + rs.getInt("partnerId") + " - " + rs.getString("partnerName") + "</option>");
                            }
                            %>
                            <%=partnerData.toString()%>
                            </select>--%>
                            </td>
                            </tr>

                            <tr>
                                <td class="textb">&nbsp;</td>
                                <td class="textb"></td>
                                <td class="textb"></td>
                                <td></td>
                            </tr>
                            <td  colspan="3" align="right" class="textb" style="margin-right: 50%"><h5><b><u>Contact Info</u></b></h5></td>
                            <tr>
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
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>" name="contact_persons" size="35" placeholder="Name*" >
                                </td>
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" size="35" placeholder="Email*" >
                                </td>
                                <td align="">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>" name="maincontact_ccmailid" size="35" placeholder="Cc"  >
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
                                <td align="left" class="textb">Chargeback Contact:</td>
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_name)%>" name="cbcontact_name" size="35" placeholder="Name" >
                                </td>
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_mailId)%>" name="cbcontact_mailid" size="35" placeholder="Email" >
                                </td>
                                <td align="">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>" name="cbcontact_ccmailid" size="35" placeholder="Cc"  >
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
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_name)%>" name="refundcontact_name" size="35" placeholder="Name" >
                                </td>
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_mailId)%>" name="refundcontact_mailid" size="35" placeholder="Email" >
                                </td>
                                <td align="">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>" name="refundcontact_ccmailid"size="35" placeholder="Cc"  >
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
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_name)%>" name="salescontact_name" size="35" placeholder="Name" >
                                </td>
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value=""<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_mailid)%> name="salescontact_mailid" size="35" placeholder="Email" >
                                </td>
                                <td align="">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>" name="salescontact_ccmailid" size="35" placeholder="Cc"  >
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
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_name)%>" name="billingcontact_name" size="35" placeholder="Name" >
                                </td>
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_mailid)%>" name="billingcontact_mailid" size="35" placeholder="Email" >
                                </td>
                                <td align="">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>" name="billingcontact_ccmailid" size="35" placeholder="Cc"  >
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
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_name)%>" name="fraudcontact_name"size="35" placeholder="Name" >
                                </td>
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_mailid)%>" name="fraudcontact_mailid" size="35" placeholder="Email" >
                                </td>
                                <td align="">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>" name="fraudcontact_ccmailid" size="35" placeholder="Cc"  >
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
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_name)%>" name="technicalcontact_name" size="35" placeholder="Name" >
                                </td>
                                <td align="center">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_mailId)%>"name="technicalcontact_mailid" size="35" placeholder="Email" >
                                </td>
                                <td align="">
                                    <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>" name="technicalcontact_ccmailid"size="35" placeholder="Cc"  >
                                </td>
                                <td>
                                    <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_phone)%>" name="technicalcontact_phone" size="35" placeholder="Phone">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5"></td>
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
                            <td colspan="3" align="right">

                                <input type="hidden" value="1" name="step">
                                    <button id="submit" type="Submit" value="submit" name="submit" class="buttonform">
                                    submit </button>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5"></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </form>
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