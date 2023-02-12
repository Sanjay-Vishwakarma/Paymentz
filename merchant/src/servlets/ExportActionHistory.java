import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by Sanjay on 9/24/2021.
 */
public class ExportActionHistory extends HttpServlet
{

    private static Logger log = new Logger(ExportActionHistory.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        log.debug("---inside ExportActionHIstory.java");
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();
        Functions function = new Functions();

        if (!merchants.isLoggedIn(session))
        {
            resp.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        List<String> trackingList = (List<String>) session.getAttribute("TrackingIDList");
        log.debug("TRACKING_LIST:::" + trackingList);
        log.debug("TRACKING_LIST_SIZE:::" + trackingList.size());
        String merchantid = (String) session.getAttribute("merchantid");
        String trackingid = Functions.checkStringNull(req.getParameter("trackingid"));
        String fromdate = Functions.checkStringNull(req.getParameter("fdate"));
        String todate = Functions.checkStringNull(req.getParameter("tdate"));
        String status = Functions.checkStringNull(req.getParameter("status"));
        log.debug("Export Trackingid ++" + trackingid);
        log.debug("Export fromdate ++" + fromdate);
        log.debug("Export todate ++" + todate);

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        int pageno = 1;
        int records = 10000;


        try
        {
            // From Date
            date = sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate = String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear = String.valueOf(rightNow.get(Calendar.YEAR));

            // To date
            date = sdf.parse(todate);
            rightNow.setTime(date);
            String tdate = String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear = String.valueOf(rightNow.get(Calendar.YEAR));

            Hashtable hash = null;
            ActionEntry entry = new ActionEntry();
            String gateway = "";
            int totalRecords = 0;
            int srNo = 0;
            Hashtable actionhash = null;
            String accountid = null;


            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "Action_History-" + merchantid + "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            OutputStream os = new FileOutputStream(exportPath + "/" + fileName);
            os.write(239);
            os.write(187);
            os.write(191);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
            printHeader(writer);

            int i = 0;
            Iterator itr = trackingList.iterator();
            StringBuffer trackingIds = new StringBuffer();


            while (itr.hasNext())
            {
                if (i != 0)
                {
                    trackingIds.append(",");
                }
                trackingIds.append("" + itr.next() + "");
                i++;
            }
            try
            {
                System.out.println("traking id ++++" + trackingIds.toString());
                if (function.isValueNull(trackingIds.toString()))
                {
                    hash = entry.getExportActionHistoryByTrackingId(trackingIds.toString());
                }
                else
                {
                    hash = entry.getExportActionHistoryByTrackingId(trackingIds.toString());
                }
                totalRecords = Integer.parseInt((String) hash.get("records"));
                log.debug("totalrecord -----" + totalRecords);
                for (int pos = 1; pos <= totalRecords; pos++)
                {
                    actionhash = (Hashtable) hash.get(pos + "");
                    srNo = srNo + 1;
                    printTransaction(writer, actionhash, srNo, pos);
                }
                log.debug("actionhash, srNo++++" + actionhash + " " + srNo);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            writer.close();
            User user = (User) session.getAttribute("ESAPIUserSessionKey");
            log.debug("TOKEN_VALUE :: " + user.getCSRFToken());
            sendFile(exportPath + "/" + fileName, fileName, resp);
            return;

        }
        catch (Exception e)
        {
            log.error("Error", e);
            Functions.NewShowConfirmation("Error !", "Invalid Transaction");

        }

    }

    private void printHeader(PrintWriter writer)
    {
        print(writer, " Sr no");
        print(writer, "Tracking ID");
        print(writer, "Action");
        print(writer, "Amount");
        print(writer, "Currency");
        print(writer, "Error Code");
        print(writer, "Error Description");
        // print(writer, "Response Transactionid");
        print(writer, "Arn");
        print(writer, "Rrn");
        print(writer, "Remark");
        print(writer, "Timestamp");
        print(writer, "Billing Descriptor");
        print(writer, "Response Code");
        print(writer, "Response Description");
        print(writer, "Action Executor");
        print(writer, "TMPL_Amount");
        print(writer, "TMPL_Currency");
        print(writer, "Wallet_Amount");
        printLast(writer, "Wallet_Currency");

    }

    void print(PrintWriter writer, String str)
    {
        writer.print("\"" + Util.replaceData(str, "\"", "\"\"") + "\"");
        writer.print(',');
    }

    void printLast(PrintWriter writer, String str)
    {
        writer.println("\"" + Util.replaceData(str, "\"", "\"\"") + "\"");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash, int pos, int pos1)
    {
        Functions functions = new Functions();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        String errorName = (String) transactionHash.get("errorName");
        String errorCode = "-";
        String errorDescription = "-";
        if (functions.isValueNull((String) transactionHash.get("errorName")))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(errorName));
            errorCode = errorCodeVO.getApiCode();
            errorDescription = errorCodeVO.getApiDescription();
        }
        String trackingid = "";
        if (functions.isValueNull((String) transactionHash.get("trackingid")))
        {
            trackingid = "'" + (String) transactionHash.get("trackingid");
        }

        print(writer, String.valueOf(pos));
        print(writer, trackingid);
        //print(writer, (String) transactionHash.get("trackingid") + "_" + pos1);
        print(writer, (String) transactionHash.get("action") == null ? "-" : (String) transactionHash.get("action"));
        print(writer, (String) transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("currency") == null ? "-" : (String) transactionHash.get("currency"));
        String errorCode1 = (String) transactionHash.get("errorCode");
        String errorDescriptor = (String) transactionHash.get("errorDescription");
        /*String responsetransaction = "";
        if (functions.isValueNull((String) transactionHash.get("responsetransactionid")))
        {
            responsetransaction="'"+(String)transactionHash.get("responsetransactionid");
        }
        log.debug("RESPONSE_ID :::: "+ responsetransaction);
        log.debug("ESAPI_RESPONSE_ID :::: "+ ESAPI.encoder().encodeForHTML(responsetransaction));
*/
        String arn = "";
        if (functions.isValueNull((String) transactionHash.get("arn")))
        {
            arn = "'" + (String) transactionHash.get("arn");
        }
        String rrn= "";
        if (functions.isValueNull((String) transactionHash.get("rrn")))
        {
            rrn= "'" + (String)transactionHash.get("rrn");
        }
        String remark = (String) transactionHash.get("remark");
        String initDateFormat = "yyyy-MM-dd HH:mm:ss";
        String targetDateFormat = "dd/MM/yyyy HH:mm:ss";
        String parsedDate = "";
        try
        {
            parsedDate = formatDate((String) transactionHash.get("timestamp"), initDateFormat, targetDateFormat);
        }
        catch (ParseException e)
        {
            log.error("ParseException---", e);
        }
        String datetime = parsedDate;
        String responseDescriptor = (String) transactionHash.get("responsedescriptor");
        String responseCode = (String) transactionHash.get("responsecode");
        String responseDesc = (String) transactionHash.get("responsedescription");
        String walletAmount = (String) transactionHash.get("walletAmount");
        String walletCurrency = (String) transactionHash.get("walletCurrency");
        String currency = (String) transactionHash.get("currency");
        String amount = (String) transactionHash.get("amount");
        String templatecurrency = (String) transactionHash.get("templatecurrency");
        String templateamount = (String) transactionHash.get("templateamount");
        //String paymentbrand = (String) transactionHash.get("cardtype");
       /* if (paymentbrand==null || paymentbrand.equals("")){
            paymentbrand="-";
        }*/
        if (functions.isValueNull(amount))
        {
            if ("JPY".equalsIgnoreCase(currency))
            {
                amount = functions.printNumber(Locale.JAPAN, amount);
            }
            else if ("EUR".equalsIgnoreCase(currency))
            {
                amount = functions.printNumber(Locale.FRANCE, amount);
            }
            else if ("GBP".equalsIgnoreCase(currency))
            {
                amount = functions.printNumber(Locale.UK, amount);
            }
            else if ("USD".equalsIgnoreCase(currency))
            {
                amount = functions.printNumber(Locale.US, amount);
            }
        }

        if (functions.isValueNull(templateamount))
        {
            if ("JPY".equalsIgnoreCase(templatecurrency))
            {
                templateamount = functions.printNumber(Locale.JAPAN, templateamount);
            }
            else if ("EUR".equalsIgnoreCase(templatecurrency))
            {
                templateamount = functions.printNumber(Locale.FRANCE, templateamount);
            }
            else if ("GBP".equalsIgnoreCase(templatecurrency))
            {
                templateamount = functions.printNumber(Locale.UK, templateamount);
            }
            else if ("USD".equalsIgnoreCase(templatecurrency))
            {
                templateamount = functions.printNumber(Locale.US, templateamount);
            }
        }
        log.debug("TEMPLATE_AMOUNT :: " + templateamount);
        if (errorCode1 == null || errorCode1.equals(""))
            errorCode1 = "-";
        if (errorDescriptor == null || errorDescriptor.equals(""))
            errorDescriptor = "-";
       /* if(responsetransaction==null || responsetransaction.equals("") || responsetransaction.equals("null"))
            responsetransaction = "-";*/
        if (arn == null || arn.equals(""))
            arn = "-";
        if (rrn == null || rrn.equals(""))
            rrn= "-";
        if (remark == null || remark.equals(""))
            remark = "-";
        if (datetime == null || datetime.equals(""))
            datetime = "-";
        if (responseDescriptor == null || responseDescriptor.equals(""))
            responseDescriptor = "-";
        if (responseCode == null || responseCode.equals(""))
            responseCode = "-";
        if (responseDesc == null || responseDesc.equals(""))
            responseDesc = "-";
        if (walletAmount == null || walletAmount.equals(""))
            walletAmount = "-";
        if (walletCurrency == null || walletCurrency.equals(""))
            walletCurrency = "-";
        if (templateamount == null || templateamount.equals(""))
            templateamount = "-";
        if (templatecurrency == null || templatecurrency.equals(""))
            templatecurrency = "-";
        print(writer, errorCode1);
        print(writer, errorDescriptor);
        // print(writer, responsetransaction);
        print(writer, arn);
        print(writer, rrn);
        print(writer, remark);
        print(writer, datetime);
        print(writer, responseDescriptor);
        print(writer, responseCode);
        print(writer, responseDesc);
        // print(writer, (String) transactionHash.get("errorDescription"));
        //    print(writer, (String) transactionHash.get("responsetransactionid")==null ? "-":(String) transactionHash.get("responsetransactionid"));
        //    print(writer, (String) transactionHash.get("arn")==null ? "-":(String) transactionHash.get("arn"));
        //    print(writer, (String) transactionHash.get("remark")==null ? "-":(String) transactionHash.get("remark"));
        //    print(writer, (String) transactionHash.get("timestamp")==null ? "-":(String) transactionHash.get("timestamp"));
        //   print(writer, (String) transactionHash.get("responsedescriptor")==null ? "-":(String)transactionHash.get("responsedescriptor") );

        //   print(writer, (String) transactionHash.get("responsecode") == null ? "-" : (String) transactionHash.get("responsecode"));
        //   print(writer, (String)transactionHash.get("responsedescription") == null ? "-" : (String) transactionHash.get("responsedescription"));
       /* if ("22".equals(cardtypeId))
        {
            print(writer, (String) transactionHash.get("responsehashinfo") == null ? "-" : (String) transactionHash.get("responsehashinfo"));
        }*/
        String role="";
         role = (String) transactionHash.get("actionexecutorname");

        if(functions.isValueNull(role) && role.startsWith("Admin-")){
            role="Admin";
        }

        print(writer, role);
        print(writer, templateamount);
        print(writer, templatecurrency);
        print(writer, walletAmount);
        printLast(writer, walletCurrency);
        /*printLast(writer, paymentbrand);*/
//        print(writer, (String) transactionHash.get("walletAmount"));
        //       printLast(writer, (String) transactionHash.get("walletCurrency"));
    }

    public static boolean sendFile(String filepath, String filename, javax.servlet.http.HttpServletResponse response) throws Exception
    {
        File f = new File(filepath);
        int length = 0;

        // Set browser download related headers (Browsers to download the file)
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        //For Sending binary data to the client. This object normally retrieved via the response
        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }
        in.close();
        op.flush();
        op.close();

        File file = new File(filepath);
        try
        {
            file.delete();
        }
        catch (Exception e)
        {
            log.debug("Exception:::::" + e);
        }
        log.info("Successful#######");
        return true;

    }

    public String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException
    {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }
}
