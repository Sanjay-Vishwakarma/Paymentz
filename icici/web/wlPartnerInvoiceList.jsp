<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Supriya
  Date: 12/5/16
  Time: 3:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="index.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<html>
<head>
    <title>Reports> White Label Partner Invoice </title>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <script type="application/javascript">
        function selectPartner(ctoken)
        {
            document.f1.action = "/icici/wlPartnerInvoiceList.jsp?ctoken=" + ctoken;
            document.f1.submit();
        }
        function getPdfFile(settleid)
        {
            if (confirm("Do you really want to download selected file."))
            {
                document.getElementById("pdfform" + settleid).submit();
            }
        }
    </script>
</head>
<body>
<%!
    private static Logger logger = new Logger("wlPartnerInvoiceList.jsp");
    Functions functions = new Functions();
%>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {

%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                White Label Invoice Manager
                <div style="float: right;">
                    <form action="/icici/whitelabelinvoice.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Add New Wire" name="submit" class="addnewmember"
                                style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Invoice
                        </button>
                    </form>
                </div>
            </div>
            <br>

            <form action="/icici/servlet/WLPartnerInvoiceList?ctoken=<%=ctoken%>" method="post" name="f1">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%
                    String str = "";
                    String fdate = null;
                    String tdate = null;
                    String fmonth = null;
                    String tmonth = null;
                    String fyear = null;
                    String tyear = null;
                    String partnerId = null;
                    String pgtypeId = null;
                    String ispaid = null;

                    try
                    {
                        fdate = ESAPI.validator().getValidInput("fdate", request.getParameter("fdate"), "Days", 2, true);
                        tdate = ESAPI.validator().getValidInput("tdate", request.getParameter("tdate"), "Days", 2, true);
                        fmonth = ESAPI.validator().getValidInput("fmonth", request.getParameter("fmonth"), "Months", 2, true);
                        tmonth = ESAPI.validator().getValidInput("tmonth", request.getParameter("tmonth"), "Months", 2, true);
                        fyear = ESAPI.validator().getValidInput("fyear", request.getParameter("fyear"), "Years", 4, true);
                        tyear = ESAPI.validator().getValidInput("tyear", request.getParameter("tyear"), "Years", 4, true);

                        partnerId = ESAPI.validator().getValidInput("partnerid", request.getParameter("partnerid"), "Numbers", 10, true);
                        pgtypeId = ESAPI.validator().getValidInput("pgtypeid", request.getParameter("pgtypeid"), "Numbers", 10, true);
                        ispaid = ESAPI.validator().getValidInput("ispaid", request.getParameter("ispaid"), "SafeString", 2, true);
                    }
                    catch (ValidationException e)
                    {
                        logger.error("Date Format Exception while select", e);
                    }
                    Calendar rightNow = Calendar.getInstance();

                    if (fdate == null) fdate = "" + 1;
                    if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
                    if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
                    if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
                    if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
                    if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

                    str = str + "ctoken=" + ctoken;

                    if (fdate != null) str = str + "&fdate=" + fdate;
                    if (tdate != null) str = str + "&tdate=" + tdate;
                    if (fmonth != null) str = str + "&fmonth=" + fmonth;
                    if (tmonth != null) str = str + "&tmonth=" + tmonth;
                    if (fyear != null) str = str + "&fyear=" + fyear;
                    if (tyear != null) str = str + "&tyear=" + tyear;

                    if (partnerId != null) str = str + "&partnerid=" + partnerId;
                    else partnerId = "";
                    if (pgtypeId != null) str = str + "&pgtypeid=" + pgtypeId;
                    else pgtypeId = "";
                    if (ispaid != null) str = str + "&ispaid=" + ispaid;
                    else ispaid = "";

                    int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                    int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);


                    PartnerManager partnerManager = new PartnerManager();
                    List<PartnerDetailsVO> partnerList = partnerManager.getAllWhitelabelPartners();
                    int year = Calendar.getInstance().get(Calendar.YEAR);

                %>
                <table border="0" align="center" width="95%" cellpadding="2" cellspacing="2"
                       style="margin-left:2.5%;margin-right: 2.5% ">
                    <tr>
                        <td class="textb" colspan="12">&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="textb" colspan="2" align="center">From</td>
                        <td colspan="2" class="txtbox">
                            <select size="1" name="fdate"
                                    value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>">
                                <%
                                    if (fdate != null)
                                        out.println(Functions.dayoptions(1, 31, fdate));
                                    else
                                        out.println(Functions.printoptions(1, 31));
                                %>
                            </select>
                            <select size="1" name="fmonth"
                                    value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>">
                                <%
                                    if (fmonth != null)
                                        out.println(Functions.newmonthoptions(1, 12, fmonth));
                                    else
                                        out.println(Functions.printoptions(1, 12));
                                %>
                            </select>
                            <select size="1" name="fyear"
                                    value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>">
                                <%
                                    if (fyear != null)
                                        out.println(Functions.yearoptions(2005, year, fyear));
                                    else
                                        out.println(Functions.printoptions(2005, year));
                                %>
                            </select>
                        </td>
                        <td colspan="2" class="textb" align="center">To</td>
                        <td colspan="2" class="textb">
                            <select size="1" name="tdate">
                                <%
                                    if (tdate != null)
                                        out.println(Functions.dayoptions(1, 31, tdate));
                                    else
                                        out.println(Functions.printoptions(1, 31));
                                %>
                            </select>

                            <select size="1" name="tmonth">
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
                                        out.println(Functions.yearoptions(2005, year, tyear));
                                    else
                                        out.println(Functions.printoptions(2005, year));
                                %>
                            </select>
                        </td>
                        <td class="textb" colspan="2" align="center">Is Paid</td>
                        <td class="textb" colspan="2">
                            <select name="ispaid" style="width:120px">
                                <%
                                    if ("Y".equals(ispaid))
                                    {
                                %>
                                <option value="">All</option>
                                <option value="Y" selected>Paid</option>
                                <option value="N">UnPaid</option>
                                <%
                                }
                                else if ("N".equals(ispaid))
                                {
                                %>
                                <option value="">All</option>
                                <option value="N" selected>Unpaid</option>
                                <option value="Y">Paid</option>
                                <%
                                }
                                else
                                {
                                %>
                                <option value="" selected>All</option>
                                <option value="N">Unpaid</option>
                                <option value="Y">Paid</option>
                                <% }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="12">&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="textb" colspan="2" align="center">Partner ID</td>
                        <td colspan="2" class="textb">
                            <input name="partnerid" id="pid1" value="<%=partnerId%>" class="txtbox" autocomplete="on">
                            <%-- <select name="partnerid" class="txtbox" >
                                 <option value="" selected>Select Partner ID</option>
                                 <%
                                     for(PartnerDetailsVO partnerDetailsVO:partnerList)
                                     {
                                         String isSelected="";
                                         String partnerID=partnerDetailsVO.getPartnerId()+"-"+partnerDetailsVO.getCompanyName();
                                         if(partnerDetailsVO.getPartnerId().equalsIgnoreCase(partnerId))
                                             isSelected="selected";
                                         else
                                             isSelected="";

                                 %>
                                 <option value="<%=partnerDetailsVO.getPartnerId()%>" <%=isSelected%>><%=partnerID%></option>
                                 <%
                                     }
                                 %>
                             </select>--%>
                        </td>
                        <td colspan="2" class="textb" align="center"></td>
                        <td colspan="2" class="textb">
                        </td>
                        <td colspan="4" class="textb" align="center">
                            <button type="submit" class="buttonform">
                                <i class="fa fa-clock-o"></i>
                                &nbsp;&nbsp;Search
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="12">&nbsp;</td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>

<div class="reporttable">
    <%
        String errormsg1 = (String) request.getAttribute("message");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println("<table align=\"center\" class=\"textb\" ><tr><td valign=\"middle\"><font class=\"text\" >");
            out.println(errormsg1);
            out.println("</font></td></tr></table>");
        }

        Hashtable hash = (Hashtable) request.getAttribute("transdetails");
        Hashtable temphash = null;
        if (hash != null && hash.size() > 0)
        {
            int records = 0;
            int totalrecords = 0;
            String currentblock = request.getParameter("currentblock");

            if (currentblock == null)
                currentblock = "1";

            try
            {
                records = Functions.convertStringtoInt((String) hash.get("records"), 15);
                totalrecords = convertStringtoInt((String) hash.get("totalrecords"), 0);
            }
            catch (NumberFormatException ex)
            {
                logger.error("Records & TotalRecords is found null", ex);
            }
            if (records > 0)
            {
    %>

    <table align=center width="90%" border="1"
           class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr No</td>
            <td valign="middle" align="center" class="th0">Partner ID</td>
            <td valign="middle" align="center" class="th0">Wire Creation On</td>
            <td valign="middle" align="center" class="th0">Start Date</td>
            <td valign="middle" align="center" class="th0">End Date</td>
            <td valign="middle" align="center" class="th1">Settle Date</td>
            <td valign="middle" align="center" class="th0">NetFinalAmount</td>
            <td valign="middle" align="center" class="th0">Unpaid Amount</td>
            <td valign="middle" align="center" class="th0">Currency</td>
            <td valign="middle" align="center" class="th0">Status</td>
            <td valign="middle" align="center" class="th0">PDF Report</td>
            <td valign="middle" align="center" class="th0" colspan="3">Action</td>
            </td>
        </tr>
        </thead>
        <%
            String style = "class=td1";
            String ext = "light";
            Hashtable inner = null;
            for (int pos = 1; pos <= records; pos++)
            {
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
                String invoiceId = ESAPI.encoder().encodeForHTML((String) temphash.get("id"));
                partnerId = ESAPI.encoder().encodeForHTML((String) temphash.get("partner_id"));
                String invoiceCreationTime = (String) temphash.get("creationtime");
                String firstDate = (String) temphash.get("start_date");
                String lastDate = (String) temphash.get("end_date");
                String settleDate = functions.isEmptyOrNull((String) temphash.get("setteled_date")) ? "" : (String) temphash.get("setteled_date");
                String netFinalAmount = ESAPI.encoder().encodeForHTML((String) temphash.get("netfinal_amount"));
                String unpaidAmount = ESAPI.encoder().encodeForHTML((String) temphash.get("unpaid_amount"));
                String currency = functions.isEmptyOrNull((String) temphash.get("currency")) ? "-" : (String) temphash.get("currency");
                String status = functions.isEmptyOrNull((String) temphash.get("status")) ? "-" : (String) temphash.get("status");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //settleDate = simpleDateFormat.format(simpleDateFormat.parse(settleDate));
                if (functions.isValueNull(invoiceCreationTime))
                {
                    invoiceCreationTime = simpleDateFormat.format(simpleDateFormat.parse(invoiceCreationTime));
                }
                if (functions.isValueNull(settleDate))
                {
                    settleDate = simpleDateFormat.format(simpleDateFormat.parse(settleDate));
                }
                if (functions.isValueNull(firstDate))
                {
                    firstDate = simpleDateFormat.format(simpleDateFormat.parse(firstDate));
                }
                if (functions.isValueNull(lastDate))
                {
                    lastDate = simpleDateFormat.format(simpleDateFormat.parse(lastDate));
                }
                if (functions.isValueNull(settleDate))
                {
                    settleDate = simpleDateFormat.format(simpleDateFormat.parse(settleDate));
                }
        %>
        <tr>
            <td align="center" <%=style%>><%=srno%>
            </td>
            <td align="center" <%=style%>><%=partnerId%>
            </td>
            </td>
            <td align="center"<%=style%>><%=invoiceCreationTime%>
            </td>
            <td align="center"<%=style%>><%=firstDate%>
            </td>
            <td align="center"<%=style%>><%=lastDate%>
            </td>
            <td align="center"<%=style%>><%=settleDate%>
            </td>
            <td align="center"<%=style%>><%=netFinalAmount%>
            </td>
            <td align="center"<%=style%>><%=unpaidAmount%>
            </td>
            <td align="center"<%=style%>><%=currency%>
            </td>
            <td align="center"<%=style%>><%=status%>
            </td>

            <td align="center"<%=style%>>
                <form id="pdfform<%=invoiceId%>" action="/icici/servlet/ActionWLInvoiceManager?ctoken=<%=ctoken%>" method="post">
                    <input type="hidden" name="invoiceid" value=<%=invoiceId%>>
                    <input type="hidden" name="action" value="sendPdfFile">
                </form>
                <a href="javascript: getPdfFile(<%=invoiceId%>)">
                    <img width="20" height="28" border="0" src="/icici/images/pdflogo.jpg">
                </a>
            </td>
            <td <%=style%>>&nbsp;
                <form action="/icici/servlet/ActionWLInvoiceManager?ctoken=<%=ctoken%>" method="post">
                    <input type="hidden" name="invoiceid" value="<%=invoiceId%>">
                    <input type="hidden" name="action" value="view">
                    <input type="hidden" name="partnerid" value="<%=partnerId%>">
                    <input type="hidden" name="ispaid" value="<%=ispaid%>">
                    <input type="submit" class="gotoauto" value="View">
                </form>
            </td>
            <td <%=style%>>&nbsp;
                <form action="/icici/servlet/ActionWLInvoiceManager?ctoken=<%=ctoken%>" method="post">
                    <input type="hidden" name="invoiceid" value="<%=invoiceId%>">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="partnerid" value="<%=partnerId%>">
                    <input type="hidden" name="ispaid" value="<%=ispaid%>">
                    <input type="submit" class="gotoauto" value="Update">
                </form>
            </td>
            <td <%=style%>>&nbsp;
                <form action="/icici/servlet/ActionWLInvoiceManager?ctoken=<%=ctoken%>" method="post">
                    <input type="hidden" name="invoiceid" value="<%=invoiceId%>" />
                    <input type="hidden" name="action" value="delete" />
                    <input type="hidden" name="partnerid" value="<%=partnerId%>" />
                    <input type="hidden" name="ispaid" value="<%=ispaid%>" />
                    <input type="submit" class="gotoauto" value="Delete" />
                </form>
            </td>
        </tr>
        <%
            }
        %>
    </table>
    <table align=center valign=top>
        <tr>
            <td align=center>
                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                    <jsp:param name="numrows" value="<%=pagerecords%>"/>
                    <jsp:param name="pageno" value="<%=pageno%>"/>
                    <jsp:param name="str" value="<%=str%>"/>
                    <jsp:param name="page" value="WLPartnerInvoiceList"/>
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
                    out.println(Functions.NewShowConfirmation("Sorry", "No Record Found."));
                }
            }
            else if ("success".equals(request.getAttribute("wireStatus")))
            {
                out.println(Functions.NewShowConfirmation("Thank You", "Wire Generated Successfully."));
            }
            else if ("exists".equals(request.getAttribute("wireStatus")))
            {
                out.println(Functions.NewShowConfirmation("Sorry", "This Wire You Have Alleady Generated."));
            }
            else if ("failure".equals(request.getAttribute("wireStatus")))
            {
                out.println(Functions.NewShowConfirmation("Sorry", "Wire Generated Failure."));
            }
            else if ("error".equals(request.getAttribute("wireStatus")))
            {
                    out.println(functions.NewShowConfirmation("Sorry","User tries to generate the same date range report againo Please go to Search Optoin."));
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry", "No Record Found."));
            }
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