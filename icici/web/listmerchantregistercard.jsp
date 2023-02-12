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
    <title>Token Management> Card Registration</title>
</head>
<body>
    <%!
  private static Logger logger=new Logger("ListMerchantRegisterCard.jsp");
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

            <div name="form" method="post" action="/icici/servlet/ListMerchantRegisterCard?ctoken=<%=ctoken%>">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="panel panel-default" >
                            <div class="panel-heading" >
                              Card  Registration
                                <div style="float: right;">
                                    </form>
                                </div>
                            </div>
                            <br>
                            <form action="/icici/servlet/ListMerchantRegisterCard?ctoken=<%=ctoken%>" method="post" name="forms" >
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
                                    memberId1 = nullToStr(request.getParameter("memberid"));
                                    String partnerid = nullToStr(request.getParameter("partnerid"));

                                    firstName = Functions.checkStringNull(request.getParameter("firstname"))==null?"":request.getParameter("firstname");
                                    lastName=Functions.checkStringNull(request.getParameter("lastname"))==null?"":request.getParameter("lastname");
                                    email=Functions.checkStringNull(request.getParameter("email"))==null?"":request.getParameter("email");

                                    //Get List of TerminalID
                                    TerminalManager terminalManager = new TerminalManager();
                                    PaginationVO paginationVO = new PaginationVO();
                                    paginationVO = (PaginationVO) request.getAttribute("paginationVO");
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
                                    if (fmonth != null) str = str + "&fmonth=" + fmonth;
                                    if (tmonth != null) str = str + "&tmonth=" + tmonth;
                                    if (fyear != null) str = str + "&fyear=" + fyear;
                                    if (tyear != null) str = str + "&tyear=" + tyear;

                                    if (memberId1!=null)str =str + "&memberid=" +memberId1;
                                    if (partnerid!=null)str =str + "&partnerid=" +partnerid;

                                    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
                                    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
                                    int year = Calendar.getInstance().get(Calendar.YEAR);
                                %>
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
                                            List<TokenVo> partneriddetails=partnerDAO.getpartnerTokenDetails();

                                        %>
                                        <td colspan="2" class="textb">Partner ID</td>
                                        <td colspan="2" class="textb" >
                                            <input name="partnerid" id="pid" value="<%=partnerid%>" class="txtbox" autocomplete="on">
                                        <%--    <select name="partnerid" id="bank" class="txtbox">
                                                <option value="0" default>--All--</option>

                                                <%
                                                    String data = "";
                                                    for(TokenVo token : partneriddetails)
                                                    {
                                                        if(!data.equals(token.getPartnerid()))
                                                        {
                                                            String Selected="";
                                                            if (String.valueOf(token.getPartnerid()).equalsIgnoreCase(partnerid))
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

                                        <td colspan="2" class="textb">Member ID</td>
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
                                            <button type="submit" class="buttonform" style="margin-left: 112%">
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
                <div class="reporttable">
                    <%
                        if(request.getAttribute("listmerchantregistercard")!=null)
                        {
                            Hashtable hash = (Hashtable) request.getAttribute("listmerchantregistercard");
                            Hashtable temphash = null;
                            int records = 0;
                            int totalrecords = 0;
                            int currentblock = 1;
                            try
                            {
                                records = Integer.parseInt((String) hash.get("records"));
                                totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                currentblock = Integer.parseInt(request.getParameter("currentblock"));
                            }
                            catch (Exception ex)
                            {

                            }
                            String style = "class=tr0";
                            String ext = "light";
                            String style1 = "class=\"textb\"";

                            if (records > 0)
                            {
                    %>

                    <table align=center width="80%" border="0" class="table table-striped table-bordered table-green dataTable">
                        <tr>
                            <td valign="middle" align="center" class="th0">Sr No</td>
                            <td valign="middle" align="center" class="th1">Registration Time</td>
                            <td valign="middle" align="center" class="th0">Token ID</td>
                            <td valign="middle" align="center" class="th1">Token</td>
                            <td valign="middle" align="center" class="th0">First Name</td>
                            <td valign="middle" align="center" class="th1">Last Name</td>
                            <td valign="middle" align="center" class="th0">Email ID</td>
                            <td valign="middle" align="center" class="th1">is Active</td>
                            <td valign="middle" align="center" class="th0">Generated By</td>
                        </tr>
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">

                        <%

                            for (int pos = 1; pos <= records; pos++)
                            {
                                String id = Integer.toString(pos);
                                int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                                style = "class=\"tr" + (pos + 1) % 2 + "\"";

                                temphash = (Hashtable) hash.get(id);

                                String date =(String)temphash.get("creationtime");
                                String tokenid =(String) temphash.get("tokenid");
                                String token=(String)temphash.get("token");
                                String firstname=(String)temphash.get("cardholder_firstname");
                                String lastname=(String)temphash.get("cardholder_lastname");
                                String email1=(String)temphash.get("cardholderemail");
                                String isActive=(String)temphash.get("isactive");
                                String useraccname=(String)temphash.get("generatedBy");
                                if (!functions.isValueNull(useraccname))
                                {
                                    useraccname="-";
                                }

                                out.println("<tr>");
                                out.println("<td data-label=\"Sr No\"  class=\"tr0\" style=\"text-align: center\">&nbsp;" + srno + "</td>");
                                out.println("<td data-label=\"Registration Time\"  class=\"tr0\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(date) + "</td>");
                                out.println("<td data-label=\"Token ID\"  class=\"tr0\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(tokenid) + "</td>");
                                out.println("<td data-label=\"Token\"  class=\"tr0\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(token) + "</td>");
                                out.println("<td data-label=\"First Name\"  class=\"tr0\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(firstname) + "</td>");
                                out.println("<td data-label=\"Last Name\"  class=\"tr0\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(lastname) + "</td>");
                                out.println("<td data-label=\"Email ID\"  class=\"tr0\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(email1) + "</td>");
                                out.println("<td data-label=\"is Active\"  class=\"tr0\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(isActive) + "</td>");
                                out.println("<td data-label=\"Generated By\"  class=\"tr0\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(useraccname) + "</td>");
                                out.println("</tr>");
                            }
                        %>


                    </table>

                    <%
                        currentblock = 1;
                        try
                        {
                            currentblock = Integer.parseInt(request.getParameter("currentblock"));
                        }
                        catch (Exception ex)
                        {
                            currentblock = 1;
                        }

                    %>


                    <jsp:include page="page.jsp" flush="true">
                        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                        <jsp:param name="page" value="ListMerchantRegisterCard"/>
                        <jsp:param name="currentblock" value="<%=currentblock%>"/>
                        <jsp:param name="orderby" value=""/>
                    </jsp:include>
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