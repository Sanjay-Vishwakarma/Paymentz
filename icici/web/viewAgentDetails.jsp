<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
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

    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Functions functions = new Functions();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Agent Details
                <div style="float: right;">
                    <form action="/icici/agentInterface.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Agent Master
                        </button>
                    </form>
                </div>
            </div>

            <%
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
                String agentName = "";
                String role="Admin";
                String username=(String)session.getAttribute("username");
                String actionExecutorId=(String)session.getAttribute("merchantid");
                String actionExecutorName=role+"-"+username;

                String errormsg=(String)request.getAttribute("error");
                //Hashtable innerhash = (Hashtable) request.getAttribute("details");

                if(errormsg!=null)
                {
                    out.println("<center><font class=\"textb\" face=\"arial\">"+errormsg+"</font></center>");
                }
            %>
            <form action="/icici/servlet/EditAgentDetails?ctoken=<%=ctoken%>" method="POST"> <input type="hidden" name="ctoken" value="<%=ctoken%>">
                <table border="1" bordercolor="#ffffff" align="center" style="width: 50%" class="table table-striped table-bordered table-green dataTable">
                    <%

                        String message=(String)request.getAttribute("message");
                        String agentid = (String) request.getAttribute("agentid");
                        String action = (String) request.getAttribute("action");
                        if(message!=null)
                        {
                            out.println("<center><font class=\"textb\">"+message+"</font></center>");
                        }

                        Hashtable hash = (Hashtable) request.getAttribute("agentdetails");
                        String isreadonly =(String) request.getAttribute("isreadonly");
                        String conf = " ";
                        Hashtable partner= (Hashtable)request.getAttribute("partners");
                        Hashtable innerhash = new Hashtable();
                        Hashtable temphash=null;
                        int records=0;
                        int records1= 0;
                        try
                        {
                            records1 = Integer.parseInt((String) partner.get("records1"));
                        }
                        catch (Exception ex)
                        {

                        }
                        if(isreadonly.equalsIgnoreCase("view"))
                        {
                            conf = "disabled";
                        }
                        if (hash != null && hash.size() > 0 && partner!=null && partner.size() > 0)
                        {
                            String style="class=tr0";
                            innerhash = (Hashtable) hash.get(1 + "");

                            if (innerhash.get("login") != null) username = (String) innerhash.get("login");
                            if (innerhash.get("agentName") != null) agentName = (String) innerhash.get("agentName");
                            if (innerhash.get("company_name") != null) company_name = (String) innerhash.get("company_name");
                            if (innerhash.get("siteurl") != null) sitename = (String) innerhash.get("siteurl");
                            if (innerhash.get("supporturl") != null) supporturl = (String) innerhash.get("supporturl");
                            if (innerhash.get("notifyemail") != null) notifyemail = (String) innerhash.get("notifyemail");
                            if (innerhash.get("telno") != null) telno = (String) innerhash.get("telno");
                            if (innerhash.get("country") != null) country = (String) innerhash.get("country");

                            if (innerhash.get("contact_emails") != null) contact_emails = (String) innerhash.get("contact_emails");
                            if (innerhash.get("contact_persons") != null) contact_persons = (String) innerhash.get("contact_persons");
                            if (innerhash.get("maincontact_ccmailid") != null) mainContact_cCmailid = (String) innerhash.get("maincontact_ccmailid");
                            if (innerhash.get("maincontact_phone") != null) mainContact_phone = (String) innerhash.get("maincontact_phone");

                            if (innerhash.get("salescontact_name") != null) salesContact_name = (String) innerhash.get("salescontact_name");
                            if (innerhash.get("salescontact_mailid") != null) salesContact_mailid = (String) innerhash.get("salescontact_mailid");
                            if (innerhash.get("salescontact_ccmailid") != null) salesContact_cCmailId = (String) innerhash.get("salescontact_ccmailid");
                            if (innerhash.get("salescontact_phone") != null) salesContact_phone = (String) innerhash.get("salescontact_phone");

                            if (innerhash.get("refundcontact_name") != null) refundContact_name = (String) innerhash.get("refundcontact_name");
                            if (innerhash.get("refundcontact_mailid") != null) refundContact_mailId = (String) innerhash.get("refundcontact_mailid");
                            if (innerhash.get("refundcontact_ccmailid") != null) refundContact_cCmail = (String) innerhash.get("refundcontact_ccmailid");
                            if (innerhash.get("refundcontact_phone") != null) refundContact_phone = (String) innerhash.get("refundcontact_phone");

                            if (innerhash.get("cbcontact_name") != null) cbContact_name = (String) innerhash.get("cbcontact_name");
                            if (innerhash.get("cbcontact_mailid") != null) cbContact_mailId = (String) innerhash.get("cbcontact_mailid");
                            if (innerhash.get("cbcontact_ccmailid") != null) cbContact_cCmailId = (String) innerhash.get("cbcontact_ccmailid");
                            if (innerhash.get("cbcontact_phone") != null) cbContact_phone = (String) innerhash.get("cbcontact_phone");

                            if (innerhash.get("billingcontact_name") != null) billingContact_name = (String) innerhash.get("billingcontact_name");
                            if (innerhash.get("billingcontact_mailid") != null) billingContact_mailid = (String) innerhash.get("billingcontact_mailid");
                            if (innerhash.get("billingcontact_ccmailid") != null) billingContact_cCmailId = (String) innerhash.get("billingcontact_ccmailid");
                            if (innerhash.get("billingcontact_phone") != null) billingContact_phone = (String) innerhash.get("billingcontact_phone");

                            if (innerhash.get("fraudcontact_name") != null) fraudContact_name = (String) innerhash.get("fraudcontact_name");
                            if (innerhash.get("fraudcontact_mailid") != null) fraudContact_mailid = (String) innerhash.get("fraudcontact_mailid");
                            if (innerhash.get("fraudcontact_ccmailid") != null) fraudContact_cCmailId = (String) innerhash.get("fraudcontact_ccmailid");
                            if (innerhash.get("fraudcontact_phone") != null) fraudContact_phone = (String) innerhash.get("fraudcontact_phone");

                            if (innerhash.get("technicalcontact_name") != null) technicalContact_name = (String) innerhash.get("technicalcontact_name");
                            if (innerhash.get("technicalcontact_mailid") != null) technicalContact_mailId = (String) innerhash.get("technicalcontact_mailid");
                            if (innerhash.get("technicalcontact_ccmailid") != null) technicalContact_cCmailId = (String) innerhash.get("technicalcontact_ccmailid");
                            if (innerhash.get("technicalcontact_phone") != null) technicalContact_phone = (String) innerhash.get("technicalcontact_phone");
                            if (innerhash.get("emailTemplateLang") != null) emailTemplateLang = (String) innerhash.get("emailTemplateLang");
                    %>
                    <br>
                    <tr <%=style%>>
                        <td class="th0" colspan="5"><b>Agent Details</b></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1" colspan="4">Username: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="login" value="<%=ESAPI.encoder().encodeForHTML(username)%>"disabled></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1" colspan="4">Agent Name:</td>
                        <td class="tr1"><input type="text1" class="txtbox1" name="agentName" value="<%=ESAPI.encoder().encodeForHTML(agentName)%>" disabled></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1"colspan="4">Agent Sitename* </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="sitename" value="<%=ESAPI.encoder().encodeForHTML(sitename)%>" <%=conf%>> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1" colspan="4">Support URL* </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="supporturl" value="<%=ESAPI.encoder().encodeForHTML(supporturl)%>" <%=conf%>> </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1" colspan="4">Notify Email Id* </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="notifyemail" value="<%=ESAPI.encoder().encodeForHTML(notifyemail)%>" <%=conf%>> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1" colspan="4">Country* </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="country" value="<%=ESAPI.encoder().encodeForHTML(country)%>" <%=conf%>> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1" colspan="4">Contact No* </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="telno" value="<%=ESAPI.encoder().encodeForHTML(telno)%>" <%=conf%>> </td>
                    </tr>
                    <tr <%=style%>>
                    <td class="tr1" colspan="4">Is IP Whitelisted: </td>
                    <td class="txtbox"><select class="txtbox" name='isipwhitelisted' <%=conf%>>
                        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("isIpWhitelisted"))));  %></select></td>
                </tr>

                    <tr <%=style%>>
                    <td class="tr1" colspan="4">Email Template Language: </td>
                    <td class="txtbox"><select class="txtbox" name='emailTemplateLang' <%=conf%>>
                        <%out.println(Functions.combovalLanguage(ESAPI.encoder().encodeForHTMLAttribute((String) innerhash.get("emailTemplateLang")))); %></select></td>
                </tr>
                    <tr <%=style%>>
                        <td class="tr1" colspan="4">Partner : </td>
                        <td class="txtbox"> <select name='partnerId' class="txtbox" <%=conf%>><%--class="<%=style%>"><select name='partnerId' <%=isReadOnly%>>&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("haspaid"))%>&ndash;%&gt;--%>

                            <%  Hashtable inner=new Hashtable();
                                for(int i=1;i<=records1;i++)
                                {
                                    String id1=Integer.toString(i);
                                    inner=(Hashtable)partner.get(id1);
                                    String selected="";
                                    if(innerhash.get("partnerId").equals(inner.get("partnerId")))
                                    {
                                        selected="selected";
                                    }

                                    out.println("<option value="+inner.get("partnerId")+" "+selected+">"+inner.get("partnerName")+"</option>");
                                }
                            if(functions.isValueNull((String)innerhash.get("actionExecutorId")))
                            {
                                actionExecutorId= (String)innerhash.get("actionExecutorId");
                            }
                                else
                            {
                                actionExecutorId="-";
                            }
                                if(functions.isValueNull((String) innerhash.get("actionExecutorName")))
                                {
                                    actionExecutorName=(String)innerhash.get("actionExecutorName");

                                }
                                else
                                {
                                    actionExecutorName="-";
                                }
                            %></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1" colspan="4">Action Executor Id</td>
                        <td class="tr1"><input type="text" class="txtbox1" name="country" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorId)%>" disabled> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1" colspan="4">Action Executor Name</td>
                        <td class="tr1"><input type="text" class="txtbox1" name="telno" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorName)%>" disabled> </td>
                    </tr>
                    <tr <%=style%>>
                        <td align="left" class="tr1" >Main Contact*</td>
                        <td align="center"class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>" name="contact_persons" size="35" placeholder="Name*" <%=conf%>>
                        </td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>"name="contact_emails" size="35" placeholder="Email*" <%=conf%> >
                        </td>
                        <td align="" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>"name="maincontact_ccmailid" size="35" placeholder="Cc" <%=conf%>>
                        </td>
                        <td class="tr1">
                            <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTML(mainContact_phone)%>" name="maincontact_phone" size="35" placeholder="Phone" <%=conf%>>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td align="left" class="tr1">Chargeback Contact:</td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(cbContact_name)%>" name="cbcontact_name" size="35" placeholder="Name" <%=conf%>>
                        </td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(cbContact_mailId)%>" name="cbcontact_mailid" size="35" placeholder="Email" <%=conf%>>
                        </td>
                        <td align="" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(cbContact_cCmailId)%>" name="cbcontact_ccmailid" size="35" placeholder="Cc"  <%=conf%>>
                        </td>
                        <td class="tr1">
                            <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTML(cbContact_phone)%>" name="cbcontact_phone" size="35" placeholder="Phone" <%=conf%>>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td align="left" class="tr1">Refund Contact:</td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(refundContact_name)%>" name="refundcontact_name" size="35" placeholder="Name" <%=conf%>>
                        </td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(refundContact_mailId)%>" name="refundcontact_mailid" size="35" placeholder="Email "<%=conf%>>
                        </td>
                        <td align="" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(refundContact_cCmail)%>" name="refundcontact_ccmailid"size="35" placeholder="Cc" <%=conf%> >
                        </td>
                        <td class="tr1">
                            <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTML(refundContact_phone)%>" name="refundcontact_phone" size="35" placeholder="Phone" <%=conf%>>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td align="left" class="tr1" c>Sales Contact:</td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(salesContact_name)%>" name="salescontact_name" size="35" placeholder="Name" <%=conf%> >
                        </td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(salesContact_mailid)%>" name="salescontact_mailid" size="35" placeholder="Email" <%=conf%> >
                        </td>
                        <td align="" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(salesContact_cCmailId)%>" name="salescontact_ccmailid" size="35" placeholder="Cc" <%=conf%> >
                        </td>
                        <td class="tr1">
                            <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTML(salesContact_phone)%>" name="salescontact_phone"size="35" placeholder="Phone" <%=conf%>>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td align="left" class="tr1">Billing Contact:</td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(billingContact_name)%>" name="billingcontact_name" size="35" placeholder="Name" <%=conf%>>
                        </td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(billingContact_mailid)%>" name="billingcontact_mailid" size="35" placeholder="Email" <%=conf%>>
                        </td>
                        <td align="" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(billingContact_cCmailId)%>" name="billingcontact_ccmailid" size="35" placeholder="Cc"  <%=conf%>>
                        </td>
                        <td class="tr1">
                            <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTML(billingContact_phone)%>" name="billingcontact_phone" size="35" placeholder="Phone" <%=conf%>>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td align="left" class="tr1">Fraud Contact:</td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(fraudContact_name)%>" name="fraudcontact_name"size="35" placeholder="Name" <%=conf%>>
                        </td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(fraudContact_mailid)%>" name="fraudcontact_mailid" size="35" placeholder="Email" <%=conf%>>
                        </td>
                        <td align="" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(fraudContact_cCmailId)%>" name="fraudcontact_ccmailid" size="35" placeholder="Cc" <%=conf%>>
                        </td>
                        <td class="tr1">
                            <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTML(fraudContact_phone)%>" name="fraudcontact_phone" size="35" placeholder="Phone" <%=conf%>>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td align="left" class="tr1" >Technical Contact:</td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(technicalContact_name)%>" name="technicalcontact_name" size="35" placeholder="Name" <%=conf%>>
                        </td>
                        <td align="center" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(technicalContact_mailId)%>"name="technicalcontact_mailid" size="35" placeholder="Email" <%=conf%>>
                        </td>
                        <td align="" class="tr1">
                            <input class="txtbox" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTML(technicalContact_cCmailId)%>" name="technicalcontact_ccmailid"size="35" placeholder="Cc" <%=conf%>>
                        </td>
                        <td class="tr1">
                            <input class="txtbox" type="Text" maxlength="20" value="<%=ESAPI.encoder().encodeForHTML(technicalContact_phone)%>" name="technicalcontact_phone" size="35" placeholder="Phone" <%=conf%>>
                        </td>
                    </tr>
                    <tr>
                        <td class="tr1" align=center colspan="5">
                            <input type="hidden" name="agentid" value="<%=agentid%>">
                            <input type="hidden" name="action" value=<%=action%>>
                            <input type="submit" align="center" class="gotoauto" name="modify" value="Save" <%=conf%>>
                        </td>
                    </tr>
                </table>
            </form>
            <%
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation("Message","No record found"));
                    }

                }
                else
                {
                    response.sendRedirect("/icici/logout.jsp");
                    return;
                }
            %>

        </div>
    </div>
</div>
</body>
</html>