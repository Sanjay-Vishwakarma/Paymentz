<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.vo.WLPartnerInvoiceVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: Supriya
  Date: 12/5/16
  Time: 3:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>WL Partner Invoice Report</title>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker();
        });
    </script>

</head>
<body>
<%!
    private static Logger logger = new Logger("actionWLInvoiceManager.jsp");
%>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (!com.directi.pg.Admin.isLoggedIn(session))
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                WL Partner Invoice Manager
            </div>
            <br>

            <form action="/icici/servlet/WLPartnerInvoiceList?ctoken=<%=ctoken%>" method="post" name="f1">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
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

                    System.out.println("partnerId---"+partnerId);

                    int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                    int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);

                    PartnerManager partnerManager=new PartnerManager();
                    List<PartnerDetailsVO> partnerList=partnerManager.getAllWhitelabelPartners();
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
                            <select name="partnerid" class="txtbox" >
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
                            </select>
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
        Functions functions = new Functions();
        String errorMessage = (String) request.getAttribute("message");
        WLPartnerInvoiceVO wlPartnerInvoiceVO = (WLPartnerInvoiceVO) request.getAttribute("wlPartnerInvoiceVO");
        String action = (String) request.getAttribute("action");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String conf = " ";

        if ("view".equalsIgnoreCase(action))
        {
            conf = "disabled";
        }
        if (wlPartnerInvoiceVO != null)
        {
            String setteledDate = "";
            String startDate = "";
            String endDate = "";
            String status = wlPartnerInvoiceVO.getStatus();
            if (functions.isValueNull(wlPartnerInvoiceVO.getSettledDate()) && "disabled".equalsIgnoreCase(conf))
            {
                setteledDate = simpleDateFormat.format(simpleDateFormat.parse(wlPartnerInvoiceVO.getSettledDate()));
            }
            if (functions.isValueNull(wlPartnerInvoiceVO.getStartDate()))
            {
                startDate = simpleDateFormat.format(simpleDateFormat.parse(wlPartnerInvoiceVO.getStartDate()));
            }
            if (functions.isValueNull(wlPartnerInvoiceVO.getEndDate()))
            {
                endDate = simpleDateFormat.format(simpleDateFormat.parse(wlPartnerInvoiceVO.getEndDate()));
            }
            String style = "class=tr1";

    %>
    <form action="/icici/servlet/UpdateWLInvoice?ctoken=<%=ctoken%>" method="post" name="form">
        <input type="hidden" name="invoiceid" value="<%=wlPartnerInvoiceVO.getInvoiceId()%>">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="true" name="isSubmitted">
        <table border="1" bordercolor="#ffffff" align="center" style="width:80%"
               class="table table-striped table-bordered table-green dataTable">
            <tr <%=style%>>
                <td class="th0" colspan="2">WL Partner Invoice Report:</td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Partner Id :</td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="partnerid"
                                       value="<%=ESAPI.encoder().encodeForHTML(wlPartnerInvoiceVO.getPartnerId())%>"
                                       disabled></td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Start Date :</td>
                <td class="tr1"><input type="text" class="txtbox1" size="50" name="startdate"
                                       value="<%=ESAPI.encoder().encodeForHTML(startDate)%>" disabled></td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">End Date :</td>
                <td class="tr1"><input type="text" class="txtbox1" size="50" name="enddate"
                                       value="<%=ESAPI.encoder().encodeForHTML(endDate)%>" disabled></td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Settle Date:</td>
                <td class="tr1">
                    <input type="text" size="50" readonly class="datepicker" name="settledate"
                           value="<%=setteledDate%>" <%=conf%>>
                </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Amount:</td>
                <td class="tr1"><input type="text" class="txtbox1" size="50" name="amount"
                                       value="<%=Functions.round(wlPartnerInvoiceVO.getAmount(),2)%>" <%=conf%>></td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">NetFinalAmount:</td>
                <td class="tr1"><input type="text" class="txtbox1" size="=50" name="netfinalamount"
                                       value="<%=Functions.round(wlPartnerInvoiceVO.getNetFinalAmount(),2)%>" <%=conf%>>
                </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Unpaid Amount:</td>
                <td class="tr1"><input type="text" class="txtbox1" size="50" name="unpaidamount"
                                       value="<%=Functions.round(wlPartnerInvoiceVO.getUnpaidAmount(),2)%>" <%=conf%>>
                </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Status:</td>
                <td class="tr1">
                    <select name="status" class="txtbox"  <%=conf%>>
                        <%
                            if ("paid".equalsIgnoreCase(status))
                            {
                        %>
                        <option value="paid" selected>Paid</option>
                        <option value="unpaid">Unpaid</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value="unpaid" selected>Unpaid</option>
                        <option value="paid">Paid</option>
                        <%
                            }
                        %>
                    </select>
                </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Report File Path:</td>
                <td class="tr1"><input type="text" class="txtbox1" size="50" name="reportfilepath"
                                       value="<%=functions.isEmptyOrNull(ESAPI.encoder().encodeForHTML(wlPartnerInvoiceVO.getReportFilePath()))?"":ESAPI.encoder().encodeForHTML(wlPartnerInvoiceVO.getReportFilePath())%>"
                                       disabled></td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Transaction File Path:</td>
                <td class="tr1"><input type="text" class="txtbox1" size="50" name="transactionfilepath"
                                       value="<%=functions.isEmptyOrNull(ESAPI.encoder().encodeForHTML(wlPartnerInvoiceVO.getTransactionFilePath()))?"":ESAPI.encoder().encodeForHTML(wlPartnerInvoiceVO.getTransactionFilePath())%>"
                                       disabled></td>
            </tr>
            <tr <%=style%>>
                <td class="tr1" colspan="2" align="center"><input type="submit" value="Update"
                                                                  class="buttonform" <%=conf%>></td>
            </tr>
        </table>
    </form>
    <%
        }
        else if (functions.isValueNull(errorMessage))
        {
            out.println(Functions.NewShowConfirmation("Result", errorMessage));
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Error", "No Records Found"));
        }
    %>
</div>
</body>
</html>

