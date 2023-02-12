import com.directi.pg.CustomerSupport;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TransactionDetail extends HttpServlet
{
    static Logger log=new Logger("logger1");
    static final String classname= TransactionDetail.class.getName();


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        if (!CustomerSupport.isLoggedIn(session))
        {
            response.sendRedirect("/support/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        ServletContext application = getServletContext();
        String perfectmatch = null;
        String desc = null;
        String orderdesc = null;
        String pod = null;
        Set tableName = null;
        String podbatch = null;
        String trackingId = null;
        String email = null;
        String datasource = null;
        String amount = null;
        String name = null;
        RequestDispatcher rd;
        String lastfourcc = null;
        String firstsixcc = null;
        String status = null;
        String fdate = null;
        String tdate = null;
        String fmonth = null;
        String tmonth = null;
        String fyear = null;
        String tyear = null;
        PrintWriter out = response.getWriter();
        perfectmatch = request.getParameter("perfectmatch");
        int pageno = 1;
        int records = 30;
        String tablec = "transaction_common";
        String tableq = "transaction_qwipi";
        String tablee = "transaction_ecore";
        Hashtable error = null, error2 = null;
        Hashtable erroroneM=null;
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.DATASOURCE);
        error = CustomerSupport.validateMandatoryParameters(request, inputFieldsListMandatory);

        try{
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.LASTFOURCC);
        inputFieldsListOptional.add(InputFields.FIRSTSIXCC);
        inputFieldsListOptional.add(InputFields.FROMDATE);
        inputFieldsListOptional.add(InputFields.FROMMONTH);
        inputFieldsListOptional.add(InputFields.FROMYEAR);
        inputFieldsListOptional.add(InputFields.TODATE);
        inputFieldsListOptional.add(InputFields.TOMONTH);
        inputFieldsListOptional.add(InputFields.TOYEAR);
        inputFieldsListOptional.add(InputFields.STATUS);
        inputFieldsListOptional.add(InputFields.DESC);
        inputFieldsListOptional.add(InputFields.ORDERDESC);
        inputFieldsListOptional.add(InputFields.STRACKINGID);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.NAME_TRA);
        inputFieldsListOptional.add(InputFields.AMOUNT);
        inputFieldsListOptional.add(InputFields.SHIPPINGPODBATCH);
        inputFieldsListOptional.add(InputFields.SHIPPINGPOD);
        error2 = CustomerSupport.validateOptionalParameters(request, inputFieldsListOptional);

        Hashtable<InputFields, String> inputFieldsListOneMandatory = new Hashtable<InputFields, String>();
        inputFieldsListOneMandatory.put(InputFields.DESC, request.getParameter("desc"));
        inputFieldsListOneMandatory.put(InputFields.SHIPPINGPOD, request.getParameter("Shippingid"));
        inputFieldsListOneMandatory.put(InputFields.AMOUNT, request.getParameter("amount"));
        inputFieldsListOneMandatory.put(InputFields.EMAILADDR, request.getParameter("emailaddr"));
        inputFieldsListOneMandatory.put(InputFields.NAME_TRA, request.getParameter("name"));
        inputFieldsListOneMandatory.put(InputFields.STRACKINGID, request.getParameter("STrakingid"));
        inputFieldsListOneMandatory.put(InputFields.FIRSTSIXCC,request.getParameter("firstfourofccnum"));
        inputFieldsListOneMandatory.put(InputFields.LASTFOURCC,request.getParameter("lastfourofccnum"));
        erroroneM = CustomerSupport.validateMandatoryParameters(inputFieldsListOneMandatory);
        log.debug(" errorone::"+erroroneM.size());
        }
        catch(Exception e)
        {
            log.error(" validation error", e);
        }
        if (!error.isEmpty() || !error2.isEmpty())
        {
            request.setAttribute("errorO", error2);
            request.setAttribute("errorM", error);
            rd = request.getRequestDispatcher("/transactionTrack.jsp?MES=X&ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        try
        {
            if (erroroneM.size() == 8)
            {

                rd = request.getRequestDispatcher("/transactionTrack.jsp?MES=C&ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
            }
            else
            {
                desc = request.getParameter("desc");
                log.debug(classname + "desc" + request.getParameter("desc") + " oredrdesc::" + request.getParameter("orderdesc") + " Shippingid::" + request.getParameter("Shippingid") + " Shippingbno::" + request.getParameter("Shippingbno") + " STrakingid::" + request.getParameter("STrakingid") + " emailaddr::" + request.getParameter("emailaddr") + " table::" + request.getParameter("table") + " amount::" + request.getParameter("amount") + " name::" + request.getParameter("name") + " status" + request.getParameter("status") + " perfectmatch" + request.getParameter("perfectmatch"));
                if (desc.equals(""))
                {
                    desc = null;
                }

                orderdesc = request.getParameter("orderdesc");
                if (orderdesc.equals(""))
                {
                    orderdesc = null;
                }
                pod = request.getParameter("Shippingid");
                if (pod.equals(""))
                {
                    pod = null;
                }
                podbatch = request.getParameter("Shippingbno");
                if (podbatch.equals(""))
                {
                    podbatch = null;
                }
                trackingId = request.getParameter("STrakingid");
                if (trackingId.equals(""))
                {
                    trackingId = null;
                }

                email = request.getParameter("emailaddr");
                if (email.equals(""))
                {
                    email = null;
                }

                datasource = request.getParameter("table");
                if (datasource.equals(""))
                {
                    datasource = null;
                }

                amount = request.getParameter("amount");
                if (amount.equals(""))
                {
                    amount = null;
                }
                name = request.getParameter("name");
                if (name.equals(""))
                {
                    name = null;
                }
                status = request.getParameter("status");
                if (status.equals(""))
                {
                    status = null;
                }
                if("".equals(perfectmatch))
                {
                    perfectmatch=null;
                }
                session.setAttribute("tableName", datasource);
                session.setAttribute("trackingid", trackingId);

                Calendar rightNow = Calendar.getInstance();

                lastfourcc = request.getParameter("lastfourofccnum");
                if (lastfourcc.equals(""))
                {
                    lastfourcc = null;
                }


                firstsixcc = request.getParameter("firstfourofccnum");
                log.debug(classname + " firstfourccno::" + firstsixcc);
                if (firstsixcc.equals(""))
                {
                    firstsixcc = null;
                }
                fdate = request.getParameter("fdate");
                fmonth = request.getParameter("fmonth");
                fyear = request.getParameter("fyear");
                tdate = request.getParameter("tdate");
                tmonth = request.getParameter("tmonth");
                tyear = request.getParameter("tyear");


                log.debug(classname + " fdate::" + fdate + " fmonth::" + fmonth + " fYear" + fyear);
                if (fdate == null) fdate = "" + 1;
                if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

                if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
                if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

                if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
                if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
                String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
                String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
                log.debug(classname + " fdtstamp::" + fdtstamp + " tdtstamp::" + tdtstamp);
                CustomerSupport csc = new CustomerSupport();
                log.debug("datasource entered: " + datasource);
                if ("all".equals(datasource))
                {
                    tableName = new HashSet();
                    tableName.add(tablec);
                    tableName.add(tableq);
                    tableName.add(tablee);
                }
                else
                {
                    if ("common".equals(datasource))
                    {
                        tableName = new HashSet();
                        tableName.add(tablec);
                    }
                    else
                    {
                        if ("qwipi".equals(datasource))
                        {
                            tableName = new HashSet();
                            tableName.add(tableq);
                        }
                        else
                        {
                            if ("ecore".equals(datasource))
                            {
                                tableName = new HashSet();
                                tableName.add(tablee);
                            }
                            else
                            {
                                if ("all_details".equals(datasource))
                                {
                                    tableName = new HashSet();
                                    tableName.add(tablec + "_details");
                                    tableName.add(tableq + "_details");
                                    tableName.add(tablee + "_details");
                                    tableName.add(tablec);
                                    tableName.add(tableq);
                                    tableName.add(tablee);
                                }
                                else
                                {
                                    if ("common_details".equals(datasource))
                                    {
                                        tableName = new HashSet();
                                        tableName.add(tablec + "_details");
                                        tableName.add(tablec);
                                    }
                                    else
                                    {
                                        if ("qwipi_details".equals(datasource))
                                        {
                                            tableName = new HashSet();
                                            tableName.add(tableq + "_details");
                                            tableName.add(tableq);
                                        }
                                        else
                                        {
                                            if ("ecore_details".equals(datasource))
                                            {
                                                tableName = new HashSet();
                                                tableName.add(tablee + "_details");
                                                tableName.add(tablee);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                try
                {
                    pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno", request.getParameter("SPageno"), "Numbers", 3, true), 1);
                    records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord", request.getParameter("SRecords"), "Numbers", 3, true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
                }
                catch (ValidationException e)
                {
                    log.error("Invalid page no or records", e);
                    pageno = 1;
                    records = 30;
                }

                try
                {
                    HashMap Details = csc.listTransactions(trackingId, name, amount, firstsixcc, lastfourcc, email, tdtstamp, fdtstamp, status, tableName, pod, podbatch, desc, orderdesc, perfectmatch, pageno, records);
                    request.setAttribute("totalrecords", Details.get("totalrecords"));
                    request.setAttribute("records", Details.get("records"));
                    Details.remove("totalrecords");
                    Details.remove("records");
                    if (Details.isEmpty())
                    {
                        log.debug(classname + "NO DATA FOUND for search in transaction for search");
                        log.debug("trackingid::" + trackingId + " Name::" + name + " amount::" + amount + " firstsixccnum::" + firstsixcc + " lastfourccnum::" + lastfourcc + " email::" + email + " fromdate:" + fdate + "/" + fmonth + "/" + fyear + " ToDate::" + tdate + "/" + tmonth + "/" + tyear + " TABLE REFERENCE::" + tableName + " Status::" + status + " pod::" + pod + " podbatch::" + podbatch + " description::" + desc + " OrderDescription::" + orderdesc + " PerfectMatch" + perfectmatch);

                        request.setAttribute("data", "no");
                        rd = request.getRequestDispatcher("/transactionTrack.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                    }
                    else
                    {
                        if (Details.containsKey("true"))
                        {
                            Details.remove("true");
                            Long time = new Long(Calendar.getInstance().get(Calendar.YEAR));
                            Long time2 = new Long(Calendar.getInstance().getTime().getTime());
                            String ftime = String.valueOf(time);
                            String ttime = String.valueOf(time2);
                            Hashtable callerList = csc.callerList(trackingId, null, ftime, ttime, pageno, records);
                            log.debug(classname + " ACTION HISTORY FOUND");
                            log.debug("trackingid::" + trackingId + " Name::" + name + " amount::" + amount + " firstsixccnum::" + firstsixcc + " lastfourccnum::" + lastfourcc + " email::" + email + " fromdate:" + fdate + "/" + fmonth + "/" + fyear + " ToDate::" + tdate + "/" + tmonth + "/" + tyear + " TABLE REFERENCE::" + tableName + " Status::" + status + " pod::" + pod + " podbatch::" + podbatch + " description::" + desc + " OrderDescription::" + orderdesc + " PerfectMatch" + perfectmatch);
                            /*Details.clear();*/
                            Details = csc.listTransactions(trackingId, name, amount, firstsixcc, lastfourcc, email, null, null, status, tableName, pod, podbatch, desc, orderdesc, perfectmatch, 1, 30);
                            Details.remove("totalrecords");
                            Details.remove("records");
                            Details.remove("true");
                            request.setAttribute("callerList", callerList);
                            request.setAttribute("transactionDetails", Details);
                            rd = request.getRequestDispatcher("/actionHistory.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(request, response);
                        }
                        else
                        {
                            log.debug(classname + " TRANSACTION DETAIL FOUND");
                            log.debug("trackingid::" + trackingId + " Name::" + name + " amount::" + amount + " firstsixccnum::" + firstsixcc + " lastfourccnum::" + lastfourcc + " email::" + email + " fromdate:" + fdate + "/" + fmonth + "/" + fyear + " ToDate::" + tdate + "/" + tmonth + "/" + tyear + " TABLE REFERENCE::" + tableName + " Status::" + status + " pod::" + pod + " podbatch::" + podbatch + " description::" + desc + " OrderDescription::" + orderdesc + " PerfectMatch" + perfectmatch);
                            request.setAttribute("transactionDetails", Details);
                            rd = request.getRequestDispatcher("/transactionTrack.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(request, response);
                        }
                    }
                }
                catch (Exception e)
                {
                    log.error(classname + " MAIN CLASS EXCEPTION::", e);
                }
            }
        }
        catch (Exception e)
        {
            log.error(classname + " main class Exception::", e);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
