<%--
<%@ page language="java" import="com.logicboxes.util.ApplicationProperties,java.util.Hashtable" %>

<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="ietest.jsp" %>
<%String company = (String)session.getAttribute("company");
    session.setAttribute("submit","Organisation Profile");

%>


<html lang="en">
<head>

    <title><%=company%> Merchant Settings > Member Profile</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>

    <script type="text/javascript" src='/merchant/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/merchant/css/new/respond.min.js'></script>



    <script type="text/javascript" src='/merchant/css/new/html5.js'></script>

    <script language="javascript">
        function comName()
        {
            var hat = this.document.form1.comtype.selectedIndex
            var hatto = this.document.form1.comtype.options[hat].value

            if(hatto == 'Propritory')
            {
                document.getElementById('proprietor').style.display=""
                document.getElementById('partnership').style.display="none"
                document.getElementById('private').style.display="none"
                document.getElementById('submit').style.display=""
            }
            else if(hatto == 'Partnership')
            {
                document.getElementById('proprietor').style.display="none"
                document.getElementById('partnership').style.display=""
                document.getElementById('private').style.display="none"
                document.getElementById('submit').style.display=""
            }
            else if(hatto == 'Private')
            {
                document.getElementById('proprietor').style.display="none"
                document.getElementById('partnership').style.display="none"
                document.getElementById('private').style.display=""
                document.getElementById('submit').style.display=""
            }
            else
            {
                document.getElementById('proprietor').style.display="none"
                document.getElementById('partnership').style.display="none"
                document.getElementById('private').style.display="none"
                document.getElementById('submit').style.display="none"
            }

        }
    </script>

</head>

<body>

<%@ include file="Top.jsp" %>


<%

    String disabled = "disabled";
    int result = merchants.isAuthorised(session);

    if (result > 0)
    {
        disabled = "";

    }

    String company_type = "";
    String proprietor = "";
    String proprietorAddress = "";
    String proprietorPhNo = "";
    String OrganisationRegNo = "";
    String partnerNameAddress = "";
    String directorsNameAddress = "";
    String pan = "";
    String directors = "";
    String employees = "";
    String potentialbusiness = "";
    String registeredaddress = "";
    String bussinessaddress = "";
    String notifyemail = "";
    String acdetails = "";
    String bankname = "";
    String branch = "";
    String AcType = "";
    String AcNumber = "";

    Hashtable details = (Hashtable) request.getAttribute("details");
    if (details != null)


        if ((String) details.get("company_type") != null)
        {
            company_type = (String) details.get("company_type");

        }
    if ((String) details.get("proprietor") != null) proprietor =(String) details.get("proprietor");
    if ((String) details.get("proprietorAddress") != null)
        proprietorAddress = (String) details.get("proprietorAddress");
    if ((String) details.get("proprietorPhNo") != null) proprietorPhNo = (String) details.get("proprietorPhNo");
    if ((String) details.get("OrganisationRegNo") != null)
        OrganisationRegNo = (String) details.get("OrganisationRegNo");
    if ((String) details.get("partnerNameAddress") != null)
        partnerNameAddress = (String) details.get("partnerNameAddress");
    if ((String) details.get("directorsNameAddress") != null)
        directorsNameAddress = (String) details.get("directorsNameAddress");
    if ((String) details.get("pan") != null) pan = (String) details.get("pan");
    if ((String) details.get("directors") != null) directors = (String) details.get("directors");
    if ((String) details.get("employees") != null) employees = (String) details.get("employees");
    if ((String) details.get("potentialbusiness") != null)
        potentialbusiness = (String) details.get("potentialbusiness");
    if ((String) details.get("registeredaddress") != null)
        registeredaddress = (String) details.get("registeredaddress");
    if ((String) details.get("bankname") != null) bankname = (String) details.get("bankname");
    if ((String) details.get("branch") != null) branch =(String) details.get("branch");
    if ((String) details.get("acctype") != null) AcType = (String) details.get("acctype");
    if ((String) details.get("AcNumber") != null) AcNumber = (String) details.get("AcNumber");
    if ((String) details.get("acdetails") != null) acdetails = (String) details.get("acdetails");
    if ((String) details.get("bussinessaddress") != null) bussinessaddress =(String) details.get("bussinessaddress");
    if ((String) details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");
%>

<form action="/merchant/servlet/NewMerchant?ctoken=<%=ctoken%>" method="post" name="form1" >
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <%=company%>  Organization Information
                </div>
                <%

                    if (request.getAttribute("MES") != null)
                    {

                        String mes = (String) request.getAttribute("MES");
                        String errormsg = (String) request.getAttribute("error");
                        System.out.println(mes);
                        System.out.println(errormsg);
                        if (mes.equals("F"))
                        {
                            out.println("<table align=\"center\" width=\"80%\" ><tr><td><font class=\"text\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. Please fill all the details completely before going for next step.</font></td></tr>");
                            out.println("<tr><td align=\"center\"> <font class=\"text\"  >"+errormsg+"</font>");
                            out.println("</td></tr></table>");

                        }
                        else if (mes.equals("X"))
                        {
                            out.println("<table align=\"center\" width=\"80%\" ><tr><td><font class=\"text\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. Please fill all the details completely before going for next step.</font><br>");
                            out.println(errormsg);
                            out.println("</td></tr></table>");

                        }
                        else
                        {
                            out.println("<table align=\"center\" width=\"80%\" ><tr><td><font class=\"text\" ><br>");
                            out.println(mes);
                            out.println("</td></tr></table>");
                        }
                    }
                %>
                <table border="0" cellpadding="5" cellspacing="0" width="80%">

                    <tr>
                        <td width="18%" class="textb">&nbsp;</td>
                        <td width="40%" class="textb">&nbsp;</td>
                        <td width="8%" class="textb">&nbsp;</td>
                        <td  width="50%"  class="textb">&nbsp;</td>
                    </tr>
                    <tr>
                        <td width="18%" class="textb">&nbsp;</td>
                        <td width="40%" class="textb">Select Company Type*</td>
                        <td width="8%" class="textb">:</td>
                        <td  width="50%"  class="textb">
                            <select id="comtype" name="comtype" onchange="comName();">
                                <option value="null">Select Company Type</option>
                                <option value="Propritory">Proprietor</option>
                                <option value="Partnership">Partnership</option>
                                <option value="Private">Private</option>
                            </select>
                        </td>
                    </tr>

                    &lt;%&ndash;proprietor&ndash;%&gt;
                    <tr height="15"><td colspan="4">
                        <div id="proprietor" align="center" style="display:none;">
                            <table border="0" cellpadding="5" cellspacing="0" width="80%" style="margin-left: 80px">
                                <tr>
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="50%" class="textb" >Name of Proprietor *<br><font color="black">(Allowed only alphanumeric values)</font></td>
                                    <td width="10%" class="textb">:</td>
                                    <td width="50%" class="textb">
                                        <input class="txtbox"   type="text"   name="proprietor" value="<%=proprietor%>" size="35"
                                               tabindex=01 <%=disabled%>>
                                    </td>
                                </tr>
                                <tr height="15"><td colspan="4"></td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="25%" class="textb" >Residential Address & Pincode *</td>
                                    <td width="3%" class="textb">:</td>
                                    <td width="12%" class="textb">
                                        <textarea class="txtbox"   name="resAddress" rows="5" cols="35"
                                                  tabindex=02 <%=disabled%>><%=proprietorAddress%></textarea>
                                    </td>
                                </tr>
                                <tr height="15"><td colspan="4"></td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="25%" class="textb" >Residential Phone Number *<br><font color="black">(Allowed only numeric values)</font></td>
                                    <td width="3%" class="textb">:</td>
                                    <td width="10%" class="textb">
                                        <input class="txtbox"  type="text" name="Res_Ph_num" value="<%=proprietorPhNo%>" size="35"
                                               tabindex=03 <%=disabled%>>
                                    </td>

                                </tr>
                                <tr height="15"><td colspan="4"></td></tr>
                            </table>
                        </div>
                    </td></tr>

                    &lt;%&ndash;partnership&ndash;%&gt;
                    <tr height="15"><td colspan="4">
                        <div id="partnership" align="center" style="display: none;">
                            <table border="0" cellpadding="5" cellspacing="0" width="80%" style="margin-left: 80px;">
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="50%" class="textb" >Organisation  Registration Number *<br><font color="black">(with date)</font></td>
                                    <td width="10%" class="textb">:</td>
                                    <td width="50%" class="textb">
                                        <input type="text" class="txtbox"  name="OrgRegNumber" value="<%=OrganisationRegNo%>" size="35"
                                               tabindex=01 <%=disabled%>>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="25%" class="textb" >Full names & address of Partners *<br><font color="black">(with
                                        phone numbers.)</font></td>
                                    <td width="3%" class="textb">:</td>
                                    <td width="12%" class="textb">
                                        <textarea class="txtbox"  name="partners_data" rows="5" cols="35"
                                                  tabindex=02 <%=disabled%>><%=partnerNameAddress%></textarea>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </td></tr>
                    &lt;%&ndash;private&ndash;%&gt;
                    <tr height="15"><td colspan="4">
                        <div id="private" align="center" style="display: none;">
                            <table border="0" cellpadding="5" cellspacing="0" width="80%" style="margin-left: 80px;">
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="50%" class="textb" >Company Registration Number *<br><font color="black">(with date)</font></td>
                                    <td width="10%" >:</td>
                                    <td width="50%" class="textb">
                                        <input type="text" class="txtbox"  name="ComRegNumber" value="<%=OrganisationRegNo%>" size="35"
                                               tabindex=01 <%=disabled%>>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="50%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td  width="50%"  class="textb">&nbsp;</td>
                                </tr>
                            </table>
                        </div>
                    </td></tr>
                </table>
            </div>
        </div>
    </div>




    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default" style="margin-top:-30px;" >
                <table  align="center" width="80%" cellpadding="2" cellspacing="2">
                    <tr>
                        <td>

                            <%
                                String errormsg = (String) request.getParameter("error");
                                if (errormsg != null)
                                {
                                    out.println("<table align=\"center\" width=\"80%\" ><tr><td><font class=\"text\" ><b> Please fill all the details completely before going for next step.</font>");
                                    out.println(errormsg);
                                    out.println("</td></tr></table>");
                                }
                                String successmsg = (String) request.getParameter("success");
                                if (errormsg != null)
                                {
                                    out.println("<table align=\"center\" width=\"80%\" ><tr><td>");
                                    out.println("<font class=\"text\" >"+successmsg+"</font>");
                                    out.println("</td></tr></table>");
                                }

                            %>   <br>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">

                                <tr height="15"><td colspan="4"></td></tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>

                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb" valign="top"><span class="textb">Permanent Account Number(PAN) </span><br>
                                        (Allowed only alphanumeric values and only for Indian members)</td>
                                    <td class="textb" valign="top">:</td>
                                    <td valign="top">
                                        <input class="txtbox"  type="text" name="PAN" value="<%=pan%>" size="35" <%=disabled%>>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr height="15"><td colspan="4"></td></tr>
                                <td width="2%" class="textb">&nbsp;</td>
                                <td width="43%" class="textb" valign="top"><span class="textb">Full names & address of Directors *</span> <br>(with phone
                                    numbers.) </td>
                                <td width="5%" class="textb" valign="top">:</td>
                                <td width="50%" valign="top">
                                    <textarea class="txtbox"  name="Directors_data" rows="5"
                                              cols="35" <%=disabled%>><%=directorsNameAddress%></textarea>
                                </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb" valign="top">Name of Key Employees *</td>
                                    <td class="textb" valign="top">:</td>
                                    <td valign="top">
                                        <textarea class="txtbox"  name="employees" rows="5" cols="35" <%=disabled%>><%=employees%></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb" valign="top"><span class="textb">Potential Business Volumes per month*</span><br>(Amount of
                                        transactions per month in INR. Allowed only numeric values)</td>
                                    <td class="textb" valign="top">:</td>
                                    <td valign="top">
                                        <input type="text" class="txtbox"  name="potentialbusiness" value="<%=potentialbusiness%>"
                                               size="20" <%=disabled%>>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb" valign="top">Registered Address & Pincode *</td>
                                    <td class="textb" valign="top">:</td>
                                    <td valign="top">
                                        <textarea class="txtbox"  name="registeredaddress" rows="5"
                                                  cols="35" <%=disabled%>><%=registeredaddress%></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb" valign="top">Business Address & Pincode *</td>
                                    <td class="textb" valign="top">:</td>
                                    <td valign="top">
                                        <textarea class="txtbox"  name="bussinessaddress" rows="5"
                                                  cols="35" <%=disabled%>><%=bussinessaddress%></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb" valign="top">Notification email address *</td>
                                    <td class="textb" valign="top">:</td>
                                    <td valign="top">
                                        <input class="txtbox"  type="Text" name="notifyemail" value="<%=notifyemail%>" size="35"
                                               value="" <%=disabled%>>
                                    </td>
                                </tr>
                                <tr><td colspan="4"><hr class="hrnew"></td></tr>

                                <tr><td class="textb" colspan="4"><u>Bank Account Details</u></td></tr>
                                <!--
                                        String acdetails = "";
                                -->
                                <%
                                    if (result > 0)
                                    {
                                %>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Bank Name *</span> <br>(Allowed only alphanumeric values.) </td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox"  type="Text" value="<%=bankname%>" name="bankname" size="35" <%=disabled%>></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Branch Name *</span> <br>(Allowed only alphanumeric values.) </td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox"  type="Text" value="<%=branch%>" name="branch" size="35" <%=disabled%>></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <td class="textb">&nbsp;</td>
                                <td class="textb">Account Type *</td>
                                <td class="textb">:</td>
                                <td>
                                    <select name="acctype" class="txtbox"  <%=disabled%>>
                                        <%
                                            if (AcType.equals("CD"))
                                            {
                                        %>
                                        <option value="CD" selected>Current</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="CD">Current</option>
                                        <%
                                            }
                                            if (AcType.equals("SB"))
                                            {
                                        %>
                                        <option value="SB" selected>Savings</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="SB">Savings</option>
                                        <%
                                            }
                                            if (AcType.equals("OD"))
                                            {
                                        %>
                                        <option value="OD" selected>Over Draft</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="OD">Over Draft</option>
                                        <%
                                            }
                                        %>

                                    </select>
                                </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Account Number *</span> <br>(Allowed only numeric values.) </td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox"  type="Text" value="<%=AcNumber%>" name="AcNumber" size="35" <%=disabled%>></td>
                                </tr>
                                <%
                                }
                                else
                                {
                                %>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb" colspan="3"><%=acdetails%></td>
                                </tr>
                                <%
                                    }
                                %>
                                <tr>
                                    <td colspan="4" align="center">

                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="text" valign="top">
                                    </td>
                                    <td width="5%" class="textb" valign="top"></td>
                                    <td width="50%" valign="top">

                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb">
                                        <input type="hidden" value="2" name="step">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <div id="submit" align="center" style="display:none;">
                                            <input type="Submit" value="Submit" name="submit" class="buttonform"  style="background-color:#34495E;color: #ffffff;width:70px; "<%=disabled%>>
                                        </div>
                                    </td>
                                    <td colspan="4" class="label" align="left">


                                    </td>
                                </tr>
                                <tr height="15"><td colspan="4"></td></tr>

                            </table>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

    </div>
</form>

</body>
</html>
--%>
<%@ page import = "org.owasp.esapi.ESAPI" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.CompanyProfileVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.vo.applicationManagerVOs.BusinessProfileVO" %>
<%@ page import="com.vo.applicationManagerVOs.BankProfileVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ include file="Top.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","speedoption");

%>
<script src='/merchant/stylenew01/BeforeAppManager.js'></script>
<%!
    private Functions functions = new Functions();
    private CommonFunctionUtil commonFunctionUtil= new CommonFunctionUtil();
%>
<%
    ApplicationManagerVO applicationManagerVO=null;
    CompanyProfileVO companyProfileVO=null;
    BankProfileVO bankProfileVO=null;
    BusinessProfileVO businessProfileVO=null;
    ValidationErrorList validationErrorList=null;
    ActionVO actionVO = new ActionVO();

    if(session.getAttribute("actionVO")!=null)
    {
        actionVO= (ActionVO) session.getAttribute("actionVO");
    }

    if(session.getAttribute("applicationManagerVO")!=null)
    {
        applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
    }

    if(applicationManagerVO.getCompanyProfileVO()!=null)
    {
        companyProfileVO=applicationManagerVO.getCompanyProfileVO();
    }

    if(companyProfileVO==null)
    {
        companyProfileVO=new CompanyProfileVO();
    }
    if(applicationManagerVO.getBusinessProfileVO()!=null)
    {
        businessProfileVO=applicationManagerVO.getBusinessProfileVO();
    }
    if(businessProfileVO==null)
    {
        businessProfileVO=new BusinessProfileVO();
    }
    if(applicationManagerVO.getBankProfileVO()!=null)
    {
        bankProfileVO=applicationManagerVO.getBankProfileVO();
    }
    if(bankProfileVO==null)
    {
        bankProfileVO=new BankProfileVO();
    }
    if(session.getAttribute("validationErrorList")!=null)
    {
        validationErrorList = (ValidationErrorList) session.getAttribute("validationErrorList");
    }
    else if(request.getAttribute("validationErrorList")!=null)
    {
        validationErrorList = (ValidationErrorList) request.getAttribute("validationErrorList");
    }
    else
    {

        validationErrorList= new ValidationErrorList();
    }

%>
<html lang="en">
<head>

    <title><%=company%> Organisation Profile</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap.min.css">--%>
    <link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>

</head>

<body class="bodybackground">
<form action="/merchant/servlet/SpeedOption?ctoken=<%=ctoken%>" method="post" name="myformname" id="myformname">
    <div class="container-fluid ">
        <div class="row" style="margin-top: 100px;background-color: #ffffff">
            <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">

                <div class="form foreground bodypanelfont_color panelbody_color">

                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Company Information</h2>

                    <div class="form-group col-md-4 has-feedback">
                        <label for="merchantname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Legal Name & Form*</label>
                        <input type="text"  class="form-control"  id="merchantname" name="merchantname" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getMerchantName())==true?companyProfileVO.getMerchantName():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("merchantname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label for="corporatename" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Trading As(DBA)*</label>
                        <input type="text" class="form-control"   id="corporatename" name="corporatename"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCorporateName())==true?companyProfileVO.getCorporateName():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("corporatename")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label for="companyregistrationnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Number*</label>
                        <input type="text" class="form-control"  id="companyregistrationnumber" name="companyregistrationnumber"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyRegistrationNumber())==true?companyProfileVO.getCompanyRegistrationNumber():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("companyregistrationnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Registration*</label>
                        <input type="text" name="Company_Date_Registration" class="form-control datepicker" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  value="<%=functions.isValueNull(companyProfileVO.getCompany_Date_Registration())==true?commonFunctionUtil.convertTimestampToDatepicker(companyProfileVO.getCompany_Date_Registration()):""%>"/><%if(validationErrorList.getError("Company_Date_Registration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label for="contactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Legal Representative</label>
                        <input type="text" class="form-control"  id="contactname" name="contactname"style="border: 1px solid #b2b2b2;font-weight:bold"value="<%=functions.isValueNull(companyProfileVO.getContactName())==true?companyProfileVO.getContactName():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("contactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label for="locationaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Address(no p/o)*</label>
                        <input type="text" class="form-control"  id="locationaddress" name="locationaddress"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getLocationAddress())==true?StringEscapeUtils.escapeHtml(companyProfileVO.getLocationAddress()):""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("locationaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4">
                        <label for="vatidentification" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">VAT Number</label>
                        <input type="text" class="form-control"  id="vatidentification" name="vatidentification" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getVatIdentification())==true?companyProfileVO.getVatIdentification():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("vatidentification")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label for="merchantzipcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip Code</label>
                        <input type="text" class="form-control"  id="merchantzipcode" name="merchantzipcode" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getMerchantZipCode())==true?companyProfileVO.getMerchantZipCode():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("merchantzipcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label for="merchantcountry" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                        <select  class="form-control" id="merchantcountry" name="merchantcountry" style="border: 1px solid #b2b2b2;font-weight:bold" <%=actionVO.isView()?"disabled":""%>  onchange="mycountrycode('merchantcountry','Companyphonecc1');">
                            <%=CommonFunctionUtil.getCountryDetails(functions.isValueNull(companyProfileVO.getMerchantCountry())==true?companyProfileVO.getMerchantCountry():"")%>
                        </select><%if(validationErrorList.getError("merchantcountry")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>

                    </div>

                    <div class="form-group col-md-2 has-feedback">
                        <label for="Companyphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone CC*</label>
                        <input type="text" class="form-control"  id="Companyphonecc1" name="Companyphonecc1" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyphonecc1())==true?companyProfileVO.getCompanyphonecc1():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("Companyphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-2 has-feedback">
                        <label for="CompanyTelephoneNO" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone No*</label>
                        <input type="text" class="form-control"   id="CompanyTelephoneNO" name="CompanyTelephoneNO" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getCompanyTelephoneNO())==true?companyProfileVO.getCompanyTelephoneNO():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("CompanyTelephoneNO")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label for="CompanyEmailAddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                        <input type="text" class="form-control"   id="CompanyEmailAddress" name="CompanyEmailAddress" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyEmailAddress())==true?companyProfileVO.getCompanyEmailAddress():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("CompanyEmailAddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label for="urls" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Website URL(S)*</label>
                        <input type="text" class="form-control"  id="urls" name="urls" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(businessProfileVO.getUrls())==true?businessProfileVO.getUrls():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("urls")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>

                    <div class="form-group col-md-4 has-feedback">
                        <label for="FederalTaxID" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">TIC/TIN Number/ Federal Tax ID/ PAN</label>
                        <input type="text" class="form-control"  id="FederalTaxID" name="FederalTaxID" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getFedraltaxid())==true?companyProfileVO.getFedraltaxid():""%>" /><%if(validationErrorList.getError("FederalTaxID")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>

                    <div class="form-group col-md-12">
                        <label for="FederalTaxID" style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Type of Business :-</u></label>
                    </div>
                    <div class="form-group col-md-2 has-feedback">
                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                            <input type="radio"   name="company_typeofbusiness" style="width:30%;"  value="Corporation" <%="Corporation".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
                            Corporation</label>
                    </div>
                    <div class="form-group col-md-3">
                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                            <input type="radio"  name="company_typeofbusiness" style="width:20%;"  value="LimitedLiabilityCompany" <%="LimitedLiabilityCompany".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
                            Limited Liability Company</label>
                    </div>
                    <div class="form-group col-md-2 has-feedback">
                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;">
                            <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="SoleProprietor"<%="SoleProprietor".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
                            Sole&nbsp;Proprietor</label>
                    </div>
                    <div class="form-group col-md-2 has-feedback">
                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                            <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="Partnership" <%="Partnership".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
                            Partnership</label>
                    </div>
                    <div class="form-group col-md-3">
                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                            <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="NotforProfit" <%="NotforProfit".equals(companyProfileVO.getCompanyTypeOfBusiness()) || !functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()) ?"checked":""%>  />
                            Nonprofit Organization</label>
                    </div>

                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Bank Information</h2>

                    <div class="form-group col-md-4 has-feedback">
                        <label for="bankinfo_bank_name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Bank Name</label>
                        <input type="text"  class="form-control"  id="bankinfo_bank_name" name="bankinfo_bank_name" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bank_name())==true?bankProfileVO.getBankinfo_bank_name():""%>" /><%if(validationErrorList.getError("bankinfo_bank_name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>

                    <div class="form-group col-md-4 has-feedback">
                        <label for="bank_accountnumber_IBAN" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">IBAN/ Account Number</label>
                        <input type="text"  class="form-control"  id="bank_accountnumber_IBAN" name="bank_accountnumber_IBAN" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBank_accountnumber_IBAN())==true?bankProfileVO.getBank_accountnumber_IBAN():""%>"/><%if(validationErrorList.getError("bank_accountnumber_IBAN")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>

                    <div class="form-group col-md-4 has-feedback">
                        <label for="bankinfo_bic" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">SWIFT/ IFSC/ BIC (Bank Identifier Code)</label>
                        <input type="text" class="form-control"   id="bankinfo_bic" name="bankinfo_bic" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bic())==true?bankProfileVO.getBankinfo_bic():""%>" <%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("bankinfo_bic")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                        <label for="bankinfo_accountholder" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Account Holder</label>
                        <input type="text" class="form-control" id="bankinfo_accountholder" name="bankinfo_accountholder" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(bankProfileVO.getBankinfo_accountholder())==true?bankProfileVO.getBankinfo_accountholder():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("bankinfo_accountholder")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>


                    <div class="form-group col-md-12">
                        <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;" >In which currency are your products sold?</label>
                    </div>

                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">USD</label>
                        <input type="checkbox" class="form-control" name="currency_products_USD" style="width: 50%;" value="Y" value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_USD())?"checked":""%>/>
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">GBP</label>
                        <input type="checkbox" class="form-control" id="currency_products_GBP" name="currency_products_GBP" style="width: 50%;"  value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_GBP())?"checked":""%> />
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">EUR</label>
                        <input type="checkbox" class="form-control"   id="currency_products_EUR" name="currency_products_EUR" style="width: 50%;" value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_EUR())?"checked":""%>/>
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">JPY</label>
                        <input type="checkbox" class="form-control"   id="currency_products_JPY" name="currency_products_JPY" style="width: 50%;"  value="Y"  <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_JPY())?"checked":""%>  />
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">PEN</label>
                        <input type="checkbox" class="form-control"   id="currency_products_PEN" name="currency_products_PEN" style="width: 50%;"  value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_PEN())?"checked":""%> />
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">HKD</label>
                        <input type="checkbox" class="form-control"   id="currency_products_HKD" name="currency_products_HKD" style="width: 50%;"  value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_HKD())?"checked":""%>/>
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">AUD</label>
                        <input type="checkbox" class="form-control"   id="currency_products_AUD" name="currency_products_AUD" style="width: 50%;"  value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_AUD())?"checked":""%> />
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">CAD</label>
                        <input type="checkbox" class="form-control"   id="currency_products_CAD" name="currency_products_CAD" style="width: 50%;"  value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_CAD())?"checked":""%> />
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">DKK</label>
                        <input type="checkbox" class="form-control"   id="currency_products_DKK" name="currency_products_DKK" style="width: 50%;"  value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_DKK())?"checked":""%> />
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">SEK</label>
                        <input type="checkbox" class="form-control"   id="currency_products_SEK" name="currency_products_SEK" style="width: 50%;"  value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_SEK())?"checked":""%>  />
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">NOK</label>
                        <input type="checkbox" class="form-control"   id="currency_products_NOK" name="currency_products_NOK" style="width: 50%;"  value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_NOK())?"checked":""%> />
                    </div>
                    <div class="form-group col-md-1">
                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">INR</label>
                        <input type="checkbox" class="form-control"   id="currency_products_INR" name="currency_products_INR" style="width: 50%;" value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_INR())?"checked":""%>  />
                    </div>

                </div>

                <br>
                <br>
            </div>
            <div align="center" class="textb">
                <%
                    if(!actionVO.isView())
                    {
                %>
                <button class="btn btn-default" type="submit" name="action" value="Save">Save</button>
                <button class="btn btn-default" type="submit" name="action" value="Submit">Submit</button>
                <%
                    }
                %>
            </div>
            <br>
            <br>
        </div>
    </div>
</form>
<br>
<br>
<br>

</body>
</html>
<script src='/merchant/stylenew01/BeforeAppManager.js'></script>
