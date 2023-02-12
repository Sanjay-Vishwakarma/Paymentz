package net.partner;

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
 * Created by Vanita on 30-04-2019.
 */
public class ExportActionHistoryByPartner extends HttpServlet
{
    private static Logger log = new Logger(ExportActionHistoryByPartner.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("inside ExportActionHistoryByPartner.java");
        HttpSession session = req.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        Functions functions = new Functions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
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
        String cardpresent = Functions.checkStringNull(req.getParameter("cardpresent"));
        log.debug("EXPORT_TRACKING_ID ::"+ trackingid);
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date=null;
        int pageno = 1;
        int records = 10000;
        String iciciTransId=null;
        String gateway ="";
        String accountid =null;
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

            date=sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate=String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear=String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date=sdf.parse(todate);
            rightNow.setTime(date);
            String tdate=String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear=String.valueOf(rightNow.get(Calendar.YEAR));

            Hashtable hash=null;
            ActionEntry entry = new ActionEntry();
            int totalRecords = 0;
            int srNo = 0;
            Hashtable actionhash = null;

            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "Action_History-" + merchantid + "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            OutputStream os=new FileOutputStream(exportPath + "/" + fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));
            //printCriteria(writer, fdtstamp, tdtstamp, merchantid,status,desc, archive ? "Archives" : "Current", trackingid,statushash,accountid);
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
            /*if(trackingid != null)
            {
                if(cardpresent.equals("N"))
                {
                    hash = entry.getActionHistoryByTrackingIdAndGateway(iciciTransId, gateway);
                }else{
                    hash = entry.getActionHistoryByTrackingIdAndGatewayCP(iciciTransId, gateway);
                }
                totalRecords = Integer.parseInt((String) hash.get("records"));
                for (int pos = 1; pos <= totalRecords; pos++)
                {
                    actionhash = (Hashtable) hash.get(pos + "");
                    printTransaction(writer, actionhash,pos, cardtypeId,pos);
                }
            }
            else */if(functions.isValueNull(trackingIds.toString()))
            {

                int j=1 ;
                String prevttrackinid = "";
                log.debug("INSIDE_IF_BLOCK");
                /*for(int i=0; i < trackingList.size();i++)
                {*/
                    //iciciTransId = trackingList.get(i);
                   /* prevttrackinid = iciciTransId;

                    if (prevttrackinid.equals(iciciTransId))
                    {
                        iciciTransId =iciciTransId+"-"+String.valueOf(j);
                        j++;
                    }
                   */
                    log.debug("INSIDE_FOR_LOOP");

                    log.debug("iciciTransId:::::"+iciciTransId);
                    if(cardpresent.equals("N"))
                    {
                        //hash = entry.getActionHistoryByTrackingIdAndGateway(iciciTransId, gateway);
                        hash = entry.getExportActionHistoryByTrackingIdAndGateway(trackingIds.toString(), gateway);
                    }else{
                       // hash = entry.getActionHistoryByTrackingIdAndGatewayCP(iciciTransId, gateway);
                        hash = entry.getExportActionHistoryByTrackingIdAndGatewayCP(trackingIds.toString(), gateway);

                    }
                    totalRecords = Integer.parseInt((String) hash.get("records"));
                    for (int pos = 1; pos <= totalRecords; pos++)
                    {
                        actionhash = (Hashtable) hash.get(pos + "");
                        srNo = srNo+1;
                        printTransaction(writer, actionhash, srNo, cardtypeId,pos);
                    }
                //}
            }


            writer.close();
            User user =  (User)session.getAttribute("ESAPIUserSessionKey");
            log.debug("TOKEN_VALUE :: "+ user.getCSRFToken());
            log.debug("TOKEN_VALUE11 :: " + session.getAttribute("ESAPIUserSessionKey"));
            //session.setAttribute("ESAPIUserSessionKey",user);
            /*User user =  (User)session.getAttribute("ESAPIUserSessionKey");
            RequestDispatcher view = req.getRequestDispatcher("/partnerTransactionDetails.jsp?ctoken="+user.getCSRFToken());
            view.forward(req, res);*/
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
            log.error("Error",e);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");
        }
    }

    /*private void printCriteria(PrintWriter writer, String fromDate, String toDate, String merchantid, String status, String description, String dataSource, String trackingid,SortedMap statushash, String accountid)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstampToDBFormat(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstampToDBFormat(toDate));
        print(writer, "Partner ID");
        printLast(writer, merchantid);
        print(writer, "Status");
        printLast(writer, Functions.checkStringNull(status)==null?"All":(String) statushash.get(status));
        print(writer, "Description");
        printLast(writer, Functions.checkStringNull(description)==null?"":description);
        print(writer, "Data Source");
        printLast(writer, dataSource);
        print(writer, "Tracking ID");
        printLast(writer, Functions.checkStringNull(trackingid)==null?"":trackingid);
        printLast(writer, "");
        printLast(writer, "Transactions Report");
    }*/

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
        if (functions.isValueNull((String) transactionHash.get("trackingid")))
        {
            trackingid= "'"+(String)transactionHash.get("trackingid");
        }

        print(writer, String.valueOf(pos));
        print(writer, trackingid);
        //print(writer, (String) transactionHash.get("trackingid") + "_" + pos1);
        print(writer, (String) transactionHash.get("action")==null ? "-":(String)transactionHash.get("action") );
        print(writer, (String) transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("currency")==null ? "-":(String)transactionHash.get("currency"));
        String errorCode1 = (String) transactionHash.get("errorCode");
        String errorDescriptor = (String) transactionHash.get("errorDescription");
        String responsetransaction = "";
        if (functions.isValueNull((String) transactionHash.get("responsetransactionid")))
        {
            responsetransaction="'"+(String)transactionHash.get("responsetransactionid");
        }
        log.debug("RESPONSE_ID :::: "+ responsetransaction);
        log.debug("ESAPI_RESPONSE_ID :::: "+ ESAPI.encoder().encodeForHTML(responsetransaction));

        String arn = "";
        if (functions.isValueNull((String) transactionHash.get("arn")))
        {
            arn= "'"+(String)transactionHash.get("arn");
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
        if(functions.isValueNull(amount))
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

        if(functions.isValueNull(templateamount))
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
       // print(writer, (String) transactionHash.get("errorDescription"));
    //    print(writer, (String) transactionHash.get("responsetransactionid")==null ? "-":(String) transactionHash.get("responsetransactionid"));
    //    print(writer, (String) transactionHash.get("arn")==null ? "-":(String) transactionHash.get("arn"));
    //    print(writer, (String) transactionHash.get("remark")==null ? "-":(String) transactionHash.get("remark"));
    //    print(writer, (String) transactionHash.get("timestamp")==null ? "-":(String) transactionHash.get("timestamp"));
     //   print(writer, (String) transactionHash.get("responsedescriptor")==null ? "-":(String)transactionHash.get("responsedescriptor") );

     //   print(writer, (String) transactionHash.get("responsecode") == null ? "-" : (String) transactionHash.get("responsecode"));
     //   print(writer, (String)transactionHash.get("responsedescription") == null ? "-" : (String) transactionHash.get("responsedescription"));
        if ("22".equals(cardtypeId))
        {
            print(writer, (String) transactionHash.get("responsehashinfo") == null ? "-" : (String) transactionHash.get("responsehashinfo"));
        }
        print(writer, (String) transactionHash.get("actionexecutorname"));
        print(writer, templateamount);
        print(writer, templatecurrency);
        print(writer, walletAmount);
        printLast(writer, walletCurrency);
        /*printLast(writer, paymentbrand);*/
//        print(writer, (String) transactionHash.get("walletAmount"));
 //       printLast(writer, (String) transactionHash.get("walletCurrency"));
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

