<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.vo.TokenDetailsVO" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.vo.partnerMerchantVOs.TokenVo" %>
<%@ page import="com.directi.pg.PzEncryptor" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 6/2/17
  Time: 3:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>

    <script language="javascript">
        function getExcelFile(settleid)
        {
            if (confirm("Do you really want to downloads selected file."))
            {
                document.getElementById("viewsettletransfile"+settleid).submit();
            }

        }
        function getPdfFile(settleid)
        {
            if (confirm("Do you really want to downloads selected file."))
            {
                document.getElementById("pdfform"+settleid).submit();
            }

        }
    </script>
    <title>Token Management> Registration History</title>
</head>
<body>
    <%!
  private static Logger logger=new Logger("wirelist.jsp");
%>
    <%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
    <%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Registration History");

    Functions functions = new Functions();
%>
<html lang="en">
<head>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><%=company%>  Register Card List</title>

    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>

    <script language="javascript">
        function DoDelete()
        {
            if (confirm("Do you really want to delete this token."))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    </script>

    <script type="text/javascript">

        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
            /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
        });
    </script>
</head>
<body>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div name="form" method="post" action="/icici/servlet/ListOfMerchantRegistrations?ctoken=<%=ctoken%>">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="panel panel-default" >
                            <div class="panel-heading" >
                                Registration History
                                <div style="float: right;">
                                    </form>
                                </div>
                            </div><br>
                            <%
                                String message= (String)request.getAttribute("message");
                                if (functions.isValueNull(message))
                                {
                                    out.println(message);
                                }
                                String delmessage= (String)request.getAttribute("delmessage");
                                if (functions.isValueNull(delmessage))
                                {
                                   out.println("<center><font class=\"text\" face=\"arial\"><b>"+ delmessage + "</b></font></center>");
                                }
                            %>
                            <form action="/icici/servlet/ListOfMerchantRegistrations?ctoken=<%=ctoken%>" method="post" name="forms" >
                                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                <%
                                    String str="";
                                    String fdate=null;
                                    String tdate=null;
                                    String fmonth=null;
                                    String tmonth=null;
                                    String fyear=null;
                                    String tyear=null;

                                    String memberId1 = "";
                                    String firstName="";
                                    String lastName="";
                                    String email="";
                                    String city="";
                                    String street="";
                                    String state="";
                                    String zipCode="";
                                    String country="";
                                    String telnocc="";
                                    String telno="";
                                    memberId1 = nullToStr(request.getParameter("memberid"));
                                    String partnerid = nullToStr(request.getParameter("partnerid"));
                                    firstName = Functions.checkStringNull(request.getParameter("firstname"))==null?"":request.getParameter("firstname");
                                    lastName=Functions.checkStringNull(request.getParameter("lastname"))==null?"":request.getParameter("lastname");
                                    email=Functions.checkStringNull(request.getParameter("email"))==null?"":request.getParameter("email");
                                    city= Functions.checkStringNull(request.getParameter("city"))==null?"":request.getParameter("city");
                                    street= Functions.checkStringNull(request.getParameter("street"))==null?"":request.getParameter("street");
                                    state= Functions.checkStringNull(request.getParameter("state"))== null?"": request.getParameter("state");
                                    zipCode= Functions.checkStringNull(request.getParameter("zipCode"))==null?"":request.getParameter("zipCode");
                                    country= Functions.checkStringNull(request.getParameter("country"))==null?"": request.getParameter("country");
                                    telnocc= Functions.checkStringNull(request.getParameter("telnocc"))==null?"": request.getParameter("telnocc");
                                    telno= Functions.checkStringNull(request.getParameter("phone"))==null?"": request.getParameter("phone");

                                    //Get List of TerminalID
                                    TerminalManager terminalManager = new TerminalManager();
                                    List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
                                    TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();

                                    for(TerminalVO terminalVO : terminalList)
                                    {
                                        memberMap.put(terminalVO.getMemberId(),terminalVO);
                                    }

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
                                        logger.error("Date Format Exception while select",e);
                                    }


                                    MerchantDAO merchantDAO = new MerchantDAO();


                                    Calendar rightNow = Calendar.getInstance();
                                    str= str + "&ctoken="+ctoken;
                                    if (fdate == null) fdate = "" + 1;
                                    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
                                    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
                                    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
                                    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
                                    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
                                    if (fdate != null) str = str + "&fdate=" + fdate;
                                    if (tdate != null) str = str + "&tdate=" + tdate;
                                    if(firstName!=null)str = str + "&firstname=" + firstName;
                                    if(lastName!=null)str = str + "&lastname=" + lastName;
                                    if(email!=null)str = str + "&email=" + email;
                                    if (city!=null)str= str + "&city=" + city;
                                    if (street!=null)str= str + "&street=" + street;
                                    if (country!= null)str= str + "&country=" +country;
                                    if (state!= null) str= str + "&state=" + state;
                                    if (zipCode!= null)str= str + "&zipCode=" + zipCode;
                                    if (telnocc!= null)str= str + "&telnocc=" + telnocc;
                                    if (telno!= null)str= str + "&phone=" + telno;
                                    if (memberId1!=null)str =str + "&memberid=" +memberId1;
                                    if (partnerid!=null)str =str + "&partnerid=" +partnerid;

                                    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
                                    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
                                    int year = Calendar.getInstance().get(Calendar.YEAR);
                                %>

                                <%--<%
                                    String errormsg1 = (String)request.getAttribute("msg");
                                    if (errormsg1 == null)
                                    {
                                        errormsg1 = "";
                                    }
                                    else
                                    {
                                        out.println("<table align=\"center\" class=\"textb\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

                                        out.println(errormsg1);

                                        out.println("</font></td></tr></table>");
                                    }
                                %>--%>

                                <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">
                                    <tr><td colspan="12">&nbsp;</td></tr>
                                    <tr>
                                        <td colspan="2" class="textb" >From</td>
                                        <td colspan="2" class="textb">
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
                                                        out.println(Functions.yearoptions(2005, year, fyear));
                                                    else
                                                        out.println(Functions.printoptions(2005, year));
                                                %>
                                            </select>
                                        </td>
                                        <td colspan="2" class="textb" >To</td>
                                        <td colspan="2" class="textb">
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
                                                        out.println(Functions.yearoptions(2005, year, tyear));
                                                    else
                                                        out.println(Functions.printoptions(2005, year));
                                                %>
                                            </select>
                                        </td>

                                        <%
                                            PartnerDAO partnerDAO = new PartnerDAO();
                                            List<TokenVo> partneriddetails=partnerDAO.getpartnerTokenDetail();

                                        %>
                                        <td colspan="2" class="textb" for="pid">Partner ID</td>
                                        <td colspan="2" class="textb" >
                                            <input name="partnerid" id="pid" value="<%=partnerid%>" class="txtbox" autocomplete="on">
                                          <%--  <select name="partnerid" id="bank" class="txtbox">
                                                <option value="0" default>--All--</option>

                                                <%
                                                    String data = "";

                                                    for(TokenVo token : partneriddetails)
                                                    {

                                                        if(!data.equals(token.getPartnerid()))
                                                        {
                                                            String Selected="";
                                                            if (token.getPartnerid().equalsIgnoreCase(partnerid))
                                                            {
                                                                Selected="selected";
                                                            }
                                                %>
                                                <option value="<%=token.getPartnerid()%>"<%=Selected%>><%=token.getPartnerid()%>-<%=token.getPartnername()%></option>
                                                <%
                                                        }
                                                        data = token.getPartnerid();
                                                    }
                                                %>
                                            </select>--%>
                                        </td>

                                        <td colspan="2" class="textb" for="pmid">Member ID</td>
                                        <td colspan="2" class="textb" >
                                            <input name="memberid" id="pmid" value="<%=memberId1%>" class="txtbox" autocomplete="on" >
                                            <%--<select name="memberid" id="accountid" class="txtbox" >
                                                <option data-bank="all" value="0">---ALL--</option>
                                                <%

                                                    for(TokenVo token : partneriddetails)
                                                    {
                                                        String isSelected ="";
                                                        if(token.getMemberid().equalsIgnoreCase(memberId1))
                                                        {
                                                            isSelected="selected";
                                                        }
                                                %>
                                                <option data-bank="<%=token.getPartnerid()%>" <%=isSelected%>  value="<%=token.getMemberid()%>" &lt;%&ndash;<%=isSelected%>&ndash;%&gt;><%=token.getMemberid()+"-"+token.getCompanyname()%></option>
                                                <%
                                                    }
                                                %>
                                            </select>--%>
                                        </td>


                                    </tr>

                                    <tr><td colspan="12">&nbsp;</td></tr>

                                    <tr>
                                        <td colspan="12" class="textb">
                                            <button type="submit" class="buttonform" style=" margin-left: 112%;position: inherit;">
                                                <i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;Search
                                            </button>
                                        </td>
                                    </tr>
                                    <tr><td colspan="12">&nbsp;</td></tr>
                                </table>
                            </form>
                        </div>
                    </div>
                </div>

                <%--<div class="reporttable">
                    <%
                        String errormsg1 = (String)request.getAttribute("msg");
                        if (errormsg1 == null)
                        {
                            errormsg1 = "";
                        }
                        else
                        {
                            out.println("<table align=\"center\" class=\"textb\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

                            out.println(errormsg1);

                            out.println("</font></td></tr></table>");
                        }
                    %>--%>
                <div class="reporttable">
                    <%
                        List<TokenDetailsVO> tokenDetailsVOs = (List<TokenDetailsVO>) request.getAttribute("listOfMerchantRegistrations");
                        if(tokenDetailsVOs != null)
                        {
                            if(tokenDetailsVOs.size() > 0)
                            {
                                PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                                paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
                                int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
                    %>
                <div style="width: 100%; overflow: auto">
                    <table align=center width="80%" border="0" class="table table-striped table-bordered table-green dataTable">
                        <tr>
                            <td valign="middle" align="center" class="th0">Sr No</td>
                            <td valign="middle" align="center" class="th0">Registration time</td>
                            <td valign="middle" align="center" class="th0">Partner ID</td>
                            <td valign="middle" align="center" class="th0">Member ID</td>
                            <td valign="middle" align="center" class="th0">Customer ID</td>
                            <td valign="middle" align="center" class="th0">Card No</td>
                            <td valign="middle" align="center" class="th0">Token</td>
                            <td valign="middle" align="center" class="th0">First Name</td>
                            <td valign="middle" align="center" class="th0">Last Name</td>
                            <td valign="middle" align="center" class="th0">Email ID</td>
                            <td valign="middle" align="center" class="th0">City</td>
                            <td valign="middle" align="center" class="th0">State</td>
                            <td valign="middle" align="center" class="th0">Zip</td>
                            <td valign="middle" align="center" class="th0">Street</td>
                            <td valign="middle" align="center" class="th0">TelnoCC</td>
                            <td valign="middle" align="center" class="th0">Telno</td>
                            <td valign="middle" align="center" class="th0">Country</td>
                            <td valign="middle" align="center" class="th0">is Active</td>
                            <td valign="middle" align="center" class="th0">Generated By</td>
                            <td valign="middle" align="center" class="th0">Type</td>
                            <td valign="middle" align="center" class="th0">Action</td>
                        </tr>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">

                        <%
                            for (TokenDetailsVO tokenDetailsVO : tokenDetailsVOs)
                            {
                                String fname = tokenDetailsVO.getAddressDetailsVO().getFirstname();
                                String lname= tokenDetailsVO.getAddressDetailsVO().getLastname();
                                String mail= tokenDetailsVO.getAddressDetailsVO().getEmail();
                                String City= tokenDetailsVO.getAddressDetailsVO().getCity();
                                String State= tokenDetailsVO.getAddressDetailsVO().getState();
                                String Zip= tokenDetailsVO.getAddressDetailsVO().getZipCode();
                                String Street= tokenDetailsVO.getAddressDetailsVO().getStreet();
                                String Telnocc= tokenDetailsVO.getAddressDetailsVO().getTelnocc();
                                String Telno= tokenDetailsVO.getAddressDetailsVO().getPhone();
                                String Country= tokenDetailsVO.getAddressDetailsVO().getCountry();
                                String toid = tokenDetailsVO.getMemberId();
                                String partnerId = tokenDetailsVO.getPartnerId();
                                String generatedBy = tokenDetailsVO.getRegistrationGeneratedBy();
                                String tokenType = "";
                                String cardNum= PzEncryptor.decryptPAN(tokenDetailsVO.getCardNum());
                                String customerid=tokenDetailsVO.getAddressDetailsVO().getCustomerid();
                                if(functions.isValueNull(cardNum)){
                                    cardNum=Functions.getFirstSix(cardNum)+"******"+Functions.getLastFour(cardNum);
                                }

                                if(!functions.isValueNull(fname)) {fname = " - "; }
                                if(!functions.isValueNull(lname)) {lname = " - "; }
                                if(!functions.isValueNull(mail)) {mail = " - "; }
                                if(!functions.isValueNull(toid)) {toid = " - "; }
                                if(!functions.isValueNull(partnerId)) {partnerId = " - "; }
                                if(!functions.isValueNull(generatedBy)) {generatedBy = " - "; }
                                if(!functions.isValueNull(customerid)){customerid="-";}
                                if (!functions.isValueNull(City)){City = "-";}
                                if (!functions.isValueNull(State)){State = "-" ;}
                                if (!functions.isValueNull(Zip)){Zip = "-";}
                                if (!functions.isValueNull(Street)){Street = "-";}
                                if (!functions.isValueNull(Telnocc)) {Telnocc = "-";}
                                if (!functions.isValueNull(Telno)) {Telno = "-";}
                                if (!functions.isValueNull(Country)) {Country = "-";}

                                out.println("<tr>");
                                out.println("<td data-label=\"Sr No\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + srno + "</td>");
                                out.println("<td data-label=\"Registration time No\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + tokenDetailsVO.getCreationOn() + "</td>");
                                out.println("<td data-label=\"Token ID\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + partnerId + "</td>");
                                out.println("<td data-label=\"Member ID\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + toid + "</td>");
                                out.println("<td data-label=\"Customer ID\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + customerid + "</td>");
                                out.println("<td data-label=\"Card Num\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + cardNum + "</td>");
                                out.println("<td data-label=\"Token\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + tokenDetailsVO.getRegistrationToken() + "</td>");
                                out.println("<td data-label=\"First Name\" class=\"tr0\" style=\"text-align: center\">&nbsp;" +fname+ "</td>");
                                out.println("<td data-label=\"Last Name\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + lname + "</td>");
                                out.println("<td data-label=\"Email ID\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + mail + "</td>");
                                out.println("<td data-label=\"City\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + City + "</td>");
                                out.println("<td data-label=\"State\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + State + "</td>");
                                out.println("<td data-label=\"Zip\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + Zip + "</td>");
                                out.println("<td data-label=\"Street\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + Street + "</td>");
                                out.println("<td data-label=\"TelnoCC\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + Telnocc + "</td>");
                                out.println("<td data-label=\"Telno\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + Telno + "</td>");
                                out.println("<td data-label=\"Country\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + Country + "</td>");
                                out.println("<td data-label=\"is Active\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + tokenDetailsVO.getIsActive() + "</td>");
                                out.println("<td data-label=\"Generated By\" class=\"tr0\" style=\"text-align: center\">&nbsp;" + generatedBy+ "</td>");
                                if(functions.isValueNull(tokenDetailsVO.getBankAccountId())) {tokenType = "Account Token";}
                                else {tokenType = "Card Token";}
                                out.println("<td data-label=\"Type\" class=\"tr0\" style=\"text-align: center\">&nbsp;" +tokenType+ "</td>");
                                out.println("<td data-label=\"Action\" class=\"tr0\" style=\"text-align: center\">&nbsp;" +

                                        "<form action=\"/icici/servlet/DeleteRegistration?ctoken="+ctoken+"\" method=\"POST\">" +
                                        "<input type=\"hidden\" name=\"tokenid\" value=" +tokenDetailsVO.getRegistrationToken()+ ">" +
                                        "<input type=\"hidden\" name=\"fdate\" value=" +fdate+ ">" +
                                        "<input type=\"hidden\" name=\"fmonth\" value="+fmonth+">"+
                                        "<input type=\"hidden\" name=\"fyear\" value="+fyear+">"+
                                        "<input type=\"hidden\" name=\"tdate\" value="+tdate+">"+
                                        "<input type=\"hidden\" name=\"tmonth\" value="+tmonth+">"+
                                        "<input type=\"hidden\" name=\"tyear\" value="+tyear+">"+
                                        "<input type=\"hidden\" name=\"firstname\" value="+firstName+">"+
                                        "<input type=\"hidden\" name=\"lastname\" value="+lastName+">"+
                                        "<input type=\"hidden\" name=\"email\" value="+mail+">"+
                                        "<input type=\"hidden\" name=\"memberid\" value="+tokenDetailsVO.getMemberId()+">"+
                                        "<input type=\"hidden\" name=\"listOfMerchantRegistrations\" value="+tokenDetailsVOs+">"+
                                        "<input type=\"submit\" name=\"action\" value=\"Delete\" class=\"btn btn-default\" width=\"100\" onClick=\"return DoDelete()\" >" +
                                        "</form></td>");
                                out.println("</tr>");
                                srno++;
                            }
                        %>
                    </table>
                </div>
                    <table align=center valign=top>
                        <tr>
                            <td align=center>
                                <jsp:include page="page.jsp" flush="true">
                                    <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                                    <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                                    <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                                    <jsp:param name="page" value="ListOfMerchantRegistrations"/>
                                    <jsp:param name="str" value="<%=paginationVO.getInputs()%>" />
                                    <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                                    <jsp:param name="orderby" value=""/>
                                </jsp:include>

                            </td>
                        </tr>
                    </table>
                    <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation("Sorry", "No Records Found for given search criteria."));
                                }
                            }
                            else
                            {
                                    String errormsg2 = (String)request.getAttribute("msg");
                                if (functions.isValueNull(errormsg2))
                                {
                                    out.println(Functions.NewShowConfirmation("",errormsg2));
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation("Sorry", "No Records Found for given search criteria."));
                                }
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
    </div>
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