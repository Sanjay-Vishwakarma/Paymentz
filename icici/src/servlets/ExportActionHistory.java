package servlets;

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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Diksha on 08-Aug-20.
 */
public class ExportActionHistory extends HttpServlet
{
    private static Logger log = new Logger(ExportActionHistory.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Inside ExportActionHistory.java");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("merchant/logout.jsp");
            return;
        }
        List<String> trackingList = (List<String>) session.getAttribute("TrackingIDList");
        log.debug("TRACKING_LIST:::" + trackingList);
        log.debug("TRACKING_LIST_SIZE:::" + trackingList.size());
        String merchantid = (String) session.getAttribute("merchantid");
        String cardtypeId = (String) session.getAttribute("cardtypeid");
        String trackingid = Functions.checkStringNull(req.getParameter("trackingid"));
        String fromdate = Functions.checkStringNull(req.getParameter("fromdate"));
        String todate = Functions.checkStringNull(req.getParameter("todate"));
        String fmonth = Functions.checkStringNull(req.getParameter("fmonth"));
        String tmonth = Functions.checkStringNull(req.getParameter("tmonth"));
        String fyear = Functions.checkStringNull(req.getParameter("fyear"));
        String tyear = Functions.checkStringNull(req.getParameter("tyear"));
        String cardpresent = Functions.checkStringNull(req.getParameter("cardpresent"));

        int pageno = 1;
        int records = 10000;
        String iciciTransId=null;
        String gateway ="";
        String accountid =null;
        String startTime=req.getParameter("starttime");
        String endTime=req.getParameter("endtime");
        String firstSix = "";
        String lastFour = "";
        boolean archive = Boolean.valueOf(req.getParameter("archive")).booleanValue();

        Calendar rightNow = Calendar.getInstance();
        if (fromdate == null) fromdate = "" + 1;
        if (todate == null) todate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        if (functions.isValueNull(startTime)){
            startTime=startTime.trim();
        }
        else{
            startTime="00:00:00";
        }

        if (functions.isValueNull(endTime)){
            endTime = endTime.trim();
        }else{
            endTime="23:59:59";
        }


        if(functions.isValueNull(accountid))
        {
            gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
        }

        try
        {
           /* if(trackingid != null)
            {
                iciciTransId = ESAPI.validator().getValidInput("trackingid", req.getParameter("trackingid"), "Numbers", 10, false);
                log.debug("iciciTransId_IF_BLOCK :: "+ iciciTransId);
            }*/

            String startTimeArr[]=startTime.split(":");
            String endTimeArr[]=endTime.split(":");

            String fdtstamp=  Functions.converttomillisec(fmonth, fromdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp= Functions.converttomillisec(tmonth, todate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);
            Hashtable hash=null;
            ActionEntry entry = new ActionEntry();
            int totalRecords = 0;
            int srNo = 0;
            Hashtable actionhash = null;

            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "Action_History-" + merchantid + "- from " + fromdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + todate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            OutputStream os=new FileOutputStream(exportPath + "/" + fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));
            printHeader(writer, cardtypeId);

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
//            log.error("trackingIds---"+trackingIds);

            if (functions.isValueNull(trackingIds.toString()))
            {
                if (cardpresent.equalsIgnoreCase("CNP"))
                {
                    hash = entry.getExportActionHistoryByTrackingIdAndGateway(trackingIds.toString(), gateway);
                }
                else
                {
                    hash = entry.getExportActionHistoryByTrackingIdAndGatewayCP(trackingIds.toString(), gateway);
                }
            }
            //log.error("hash---"+hash);
            totalRecords = Integer.parseInt((String) hash.get("records"));
            if (functions.isValueNull(trackingIds.toString()))
            {

                for (int pos = 1; pos <= totalRecords; pos++)
                {
                    actionhash = (Hashtable) hash.get(pos + "");
                    srNo = srNo+1;
                    printTransaction(writer, actionhash, srNo, cardtypeId,pos);

                }

            }

            writer.close();
            log.debug("TOKEN_VALUE :: "+ user.getCSRFToken());
            log.debug("TOKEN_VALUE11 :: " + session.getAttribute("ESAPIUserSessionKey"));
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;
        }
        catch (SystemError se)
        {
            log.error("Error",se);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");
        }

        catch (Exception e)
        {
            log.error("Error", e);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");
            RequestDispatcher rd = req.getRequestDispatcher("/transactions.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
    }


    private void printHeader(PrintWriter writer, String cardtypeId)
    {
        print(writer, " Sr no");
        //print(writer,"Parent Tracking ID");
        print(writer,"Tracking ID");
        print(writer,"Action");
        print(writer,"Amount");
        print(writer, "Currency");
        print(writer, "Error Code");
        print(writer,"Error Description");
        print(writer, "Response Transactionid");
        print(writer, "Arn");
        print(writer, "Remark");
        print(writer, "Timestamp");
        print(writer, "Billing Descriptor");
        print(writer, "Response Code");
        print(writer,"Response Description");
        if ("22".equals(cardtypeId))
        {
            print(writer, "Verification Code");
        }
        print(writer,"Action Executor");
        print(writer,"TMPL_Amount");
        print(writer,"TMPL_Currency");
        print(writer,"Wallet_Amount");
        printLast(writer, "Wallet_Currency");
        /*printLast(writer, "Payment Brand");*/
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash, int pos, String cardtypeId,int pos1)
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

        String trackingid="";
        if (functions.isValueNull((String)transactionHash.get("trackingid")))
        {
            trackingid= "'"+(String)transactionHash.get("trackingid");
        }
        else
        {
            trackingid="-";
        }
        print(writer, String.valueOf(pos));
        print(writer, trackingid);
        //print(writer, (String) transactionHash.get("trackingid") + "_" + pos1);
        print(writer, (String) transactionHash.get("action")==null ? "-":(String)transactionHash.get("action") );
        print(writer, (String) transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("currency")==null ? "-":(String)transactionHash.get("currency"));
        String errorCode1 = (String) transactionHash.get("errorCode");
        String errorDescriptor = (String) transactionHash.get("errorDescription");
        String responsetransaction = (String) transactionHash.get("responsetransactionid");
        //log.debug("RESPONSE_ID :::: "+ responsetransaction);
        //log.debug("ESAPI_RESPONSE_ID :::: "+ ESAPI.encoder().encodeForHTML(responsetransaction));


        String arn = "";
        if(functions.isValueNull((String) transactionHash.get("arn")))
        {
            arn="'"+(String)transactionHash.get("arn");
        }
        String remark = (String) transactionHash.get("remark");
        String initDateFormat = "yyyy-MM-dd HH:mm:ss";
        String targetDateFormat = "dd/MM/yyyy HH:mm:ss";
        String parsedDate ="" ;
        try
        {
            parsedDate = formatDate((String) transactionHash.get("timestamp"), initDateFormat, targetDateFormat);
        }
        catch (ParseException e)
        {
            log.error("ParseException---",e);
        }
        String datetime = parsedDate;
        //String datetime = (String) transactionHash.get("timestamp");
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
        log.debug("TEMPLATE_AMOUNT :: "+ templateamount);
        if(errorCode1==null || errorCode1.equals(""))
            errorCode1 = "-";
        if(errorDescriptor==null || errorDescriptor.equals(""))
            errorDescriptor = "-";
        if(responsetransaction==null || responsetransaction.equals("") || responsetransaction.equals("null"))
            responsetransaction = "-";
        if(arn==null || arn.equals(""))
            arn = "-";
        if(remark==null || remark.equals(""))
            remark = "-";
        if(datetime==null || datetime.equals(""))
            datetime = "-";
        if(responseDescriptor==null || responseDescriptor.equals(""))
            responseDescriptor = "-";
        if(responseCode==null || responseCode.equals(""))
            responseCode = "-";
        if(responseDesc==null || responseDesc.equals(""))
            responseDesc = "-";
        if(walletAmount==null || walletAmount.equals(""))
            walletAmount = "-";
        if(walletCurrency==null || walletCurrency.equals(""))
            walletCurrency = "-";
        if(templateamount==null || templateamount.equals(""))
            templateamount = "-";
        if(templatecurrency==null || templatecurrency.equals(""))
            templatecurrency = "-";
        print(writer, errorCode1);
        print(writer, errorDescriptor);
        print(writer, responsetransaction);
        print(writer, arn);
        print(writer, remark);
        print(writer, datetime);
        print(writer, responseDescriptor);
        print(writer, responseCode);
        print(writer, responseDesc);
        if ("22".equals(cardtypeId))
        {
            print(writer, (String) transactionHash.get("responsehashinfo") == null ? "-" : (String) transactionHash.get("responsehashinfo"));
        }
        print(writer, (String) transactionHash.get("actionexecutorname"));
        print(writer, templateamount);
        print(writer, templatecurrency);
        print(writer, walletAmount);
        printLast(writer, walletCurrency);
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

    public static boolean sendFile(String filepath, String filename, javax.servlet.http.HttpServletResponse response)throws Exception
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
            log.debug("Exception:::::"+e);
        }
        log.info("Successful#######");
        return true;

    }

    public String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException
    {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

}
