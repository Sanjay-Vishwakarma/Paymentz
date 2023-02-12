package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.PartnerManager;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 8/12/2019.
 */
public class ExportTransactionExcel
{
    private  static Logger logger=new Logger(ExportTransactionExcel.class.getName());

    public static void main(String args[]) throws SystemError
    {
        ExportTransactionExcel exportTransactionExcel = new ExportTransactionExcel();
        String sResponse = exportTransactionExcel.exportTransactionExcelCron(new Hashtable());
    }

    public String exportTransactionExcelCron(Hashtable hashtable)
    {
        logger.error("---inside exportTransactionExcelCron---");
        TransactionEntry transactionEntry=new TransactionEntry();
        PartnerManager partnerManager=new PartnerManager();
        Functions functions=new Functions();
        List<TransactionDetailsVO> transactionDetailsVOList=new ArrayList<>();
        List<PartnerDetailsVO> partnerList = new ArrayList<>();
        StringBuffer trackingIdList=new StringBuffer();
        String fromDate="";
        String toDate="";
        String startDate="";
        String endDate="";
        String fdatetime = "";
        String tdatetime = "";
        String historyFileName="";
    try
    {
        partnerList=partnerManager.getPartnerDetailsForExportTransactionCron();
        Date date = new Date();
        SimpleDateFormat sdftimezone = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(hashtable != null)
        {
            fromDate= (String) hashtable.get("fromDate");
            toDate= (String) hashtable.get("toDate");
        }
        SortedMap statushash = transactionEntry.getSortedMap();
        File file=new File(ApplicationProperties.getProperty("EXPORT_SFTP_FILE_PATH"));
        if(!file.exists())
        {
            logger.error("create transactionFile folder in source");
            file.mkdir();
        }
        for (PartnerDetailsVO partnerDetailsVO : partnerList)
        {
            String partner=partnerDetailsVO.getLogin();
            String timezone=partnerDetailsVO.getExportTransactionCron();
            timezone=timezone.substring(0,timezone.indexOf("|"));
            transactionDetailsVOList=new ArrayList<>();

            if(functions.isValueNull(fromDate) && functions.isValueNull(toDate))
            {
                 fdatetime = fromDate;
                 tdatetime = toDate;
            }
            else
            {
                fdatetime = dateFormat.format(previousDateString(new Date())) + " " + "00:00:00";
                tdatetime = dateFormat.format(previousDateString(new Date()))+" "+"23:59:59";
            }
            fdatetime=Functions.convertDateTimeToTimeZone1(fdatetime, timezone);
            logger.error("fdatetime------->"+fdatetime);
            date =sdftimezone.parse(fdatetime);
            startDate=sdftimezone.format(date);
            long fdate = date.getTime();

            tdatetime=Functions.convertDateTimeToTimeZone1(tdatetime, timezone);
            logger.error("tdatetime------->" + tdatetime);
            date =sdftimezone.parse(tdatetime);
            endDate=sdftimezone.format(date);
            long tdate = date.getTime();

            startDate=startDate.replace(":",".");
            endDate=endDate.replace(":",".");

            String fdtstamp=String.valueOf(fdate).substring(0,10);
            String tdtstamp=String.valueOf(tdate).substring(0,10);

            transactionDetailsVOList = transactionEntry.exportlistForPartnerTransactions(fdtstamp, tdtstamp, partner.toString(), "");
            String exportPath = ApplicationProperties.getProperty("EXPORT_SFTP_FILE_PATH")+"/"+partner.toString();
            String fileName = "Transactions-"+ partner.toString()+"- from "+startDate+" to "+endDate+".csv";
            File directory = new File(exportPath);
            File savedFile=null;
            boolean success = false;
            logger.error("transactionDetailsVOList.size------->" + transactionDetailsVOList.size());
            logger.error("directory.exists()--"+exportPath+"----->" + directory.exists());
            if (directory.exists())
            {
                savedFile = new File(exportPath+ "/" + fileName);
            }
            else
            {
                success = directory.mkdir();
                logger.error("directory creation--"+exportPath+"-->" + success);
                savedFile = new File(exportPath+ "/" + fileName);
            }
            OutputStream os=new FileOutputStream(exportPath+"/"+ fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));
            printHeader(writer);
            int i=0;
            for(TransactionDetailsVO transactionDetailsVO:transactionDetailsVOList)
            {
                if(i==0)
                    trackingIdList.append(transactionDetailsVO.getTrackingid());
                else
                    trackingIdList.append("," + transactionDetailsVO.getTrackingid());
                printTransaction(writer, transactionDetailsVO, statushash, timezone);
                i++;
            }

            //sendFile(exportPath + "/" + fileName, fileName,os);
            //savedFile.delete();
            writer.flush();
            writer.close();
            os.flush();
            os.close();
            if(transactionDetailsVOList.size()>0)
            {
                historyFileName = exportTransactionHistoryExcelCron(trackingIdList.toString(), partner, startDate, endDate);
            }
            logger.error("partner.toLowerCase()---->"+partner.toLowerCase());
            functions.sendFile(exportPath,partner.toLowerCase(),fileName);
            FileUtils.cleanDirectory(directory);
            /*File[] files=directory.listFiles();
            for(File file1:files)
            {
                boolean isDelete=file1.delete();
                System.out.println(file1+"--isDelete--->"+isDelete);
            }*/

        }
        return "success";
    }
    catch (Exception e)
    {
        logger.error("Exception---->",e);
    }
        return "failed";

    }
    public String exportTransactionHistoryExcelCron(String trackingId,String partner,String startDate,String endDate)
    {
        logger.error("---inside exportTransactionExcelCron---");
        TransactionEntry transactionEntry=new TransactionEntry();
        List<TransactionDetailsVO> transactionDetailsVOList=new ArrayList<>();
        try
        {

            transactionDetailsVOList = transactionEntry.getActionHistoryByTrackingIdAndGateway(trackingId);
            String exportPath = ApplicationProperties.getProperty("EXPORT_SFTP_FILE_PATH")+"/"+partner;
            String fileName = "Action_History-"+ partner.toString()+"- from "+startDate+" to "+endDate+".csv";
            File directory = new File(exportPath);
            File savedFile=null;
            boolean success = false;
            logger.error("transactionDetailsVOList.size------->" + transactionDetailsVOList.size());
            logger.error("directory.exists()------->" + directory.exists());
            if (directory.exists())
            {
                savedFile = new File(exportPath+ "/" + fileName);
            }
            else
            {
                success = directory.mkdir();
                logger.error("directory creation---->" + success);
                savedFile = new File(exportPath+ "/" + fileName);
            }
            OutputStream os=new FileOutputStream(exportPath+"/"+ fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));
            printHeaderHistory(writer);
            int srNo=0;
            for(TransactionDetailsVO transactionDetailsVO:transactionDetailsVOList)
            {
                srNo = srNo+1;
                printTransactionHistory(writer, transactionDetailsVO, srNo, transactionDetailsVO.getCount());
            }


            //sendFile(exportPath + "/" + fileName, fileName,os);
            //savedFile.delete();
            writer.close();
            return fileName;
        }
        catch (Exception e)
        {
            logger.error("Exception---->",e);
        }
        return "";

    }

    public static boolean sendFile(String filepath, String filename,OutputStream op)throws Exception
    {
        File f = new File(filepath);
        int length = 0;
        logger.error("file length--->"+f.length());
        // Set browser download related headers (Browsers to download the file)
        /*response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");*/

        //For Sending binary data to the client. This object normally retrieved via the response

        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        logger.error("---DataInputStream--");
        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }
        in.close();
        op.flush();
        op.close();
        //File file = new File(filepath);
        try
        {
            //file.delete();
        }
        catch (Exception e)
        {
            //log.error("Exception:::::",e);
        }
        //log.info("Successful#######");
        return true;

    }

    private void printHeader(PrintWriter writer)
    {
        Functions functions = new Functions();
        String timezone = "";
        print(writer, "Transaction Date(MM/DD/YYYY)");
        //print(writer, "TimeZone("+timezone+")");
        print(writer,"Member ID");
        print(writer,"Merchant Company Name");
        print(writer, "Tracking ID");
        print(writer, "Payment ID");
        print(writer,"Order ID");
        print(writer, "Order Description");
        print(writer, "Customer ID");
        print(writer, "Issuing Bank");
        print(writer, "Acquirer Bank");
        print(writer, "Acquirer Account ID");
        print(writer, "Terminal ID");
        print(writer,"Payment Mode");
        print(writer,"Payment Brand");
        print(writer,"Currency");
        print(writer,"Card Holder's Name");
        print(writer,"Customer Email");
        print(writer,"Transaction Country");
        print(writer,"Auth Amount");
        print(writer,"Captured Amount");
        print(writer,"Refund Amount");
        print(writer,"Payout Amount");
        print(writer,"First Six");
        print(writer,"Last four");
        print(writer,"Status");
        print(writer,"Reason");
        print(writer,"Remark");
        printLast(writer, "Last Update Date(MM/DD/YYYY)");
    }
    private void printTransaction(PrintWriter writer,TransactionDetailsVO transactionDetailsVO,SortedMap statushash, String timezone) throws Exception
    {
        Functions functions = new Functions();
        String dt = Functions.convertDtstampToDateTimeforTimezone(transactionDetailsVO.getDtstamp());
        String firstSix="";
        String lastFour="";

        String tz1 = "-";
        if (functions.isValueNull(timezone))
        {
            if (timezone.indexOf("|")!= -1)
            {
                tz1 = functions.convertDateTimeToTimeZone(dt, timezone.substring(0,timezone.indexOf("|")));
            }
            else
            {
                tz1 = functions.convertDateTimeToTimeZone(dt, timezone);
            }
        }

        String paymentid = "";
        if (functions.isValueNull(transactionDetailsVO.getPaymentId()))
        {
            paymentid = transactionDetailsVO.getPaymentId();
        }
        else
        {
            paymentid = "-";
        }
        String paymodeid = "";
        if (!functions.isValueNull(transactionDetailsVO.getPaymodeId()) || "0".equals(transactionDetailsVO.getPaymodeId()))
        {
            paymodeid = "-";
        }
        else
        {
            paymodeid = transactionDetailsVO.getPaymodeId();
        }
        print(writer, Functions.convertDtstampToDBFormat(transactionDetailsVO.getDtstamp()));
        //print(writer, tz1);
        print(writer, transactionDetailsVO.getToid());
        print(writer, transactionDetailsVO.getCompanyName()==null ? "-":transactionDetailsVO.getCompanyName());
        print(writer, transactionDetailsVO.getTrackingid());
        //print(writer, (String) transactionHash.get("paymentid")==null ? "-":(String)transactionHash.get("paymentid"));
        print(writer, paymentid);
        print(writer, transactionDetailsVO.getDescription());
        print(writer, transactionDetailsVO.getOrderDescription()== null ? "-" : transactionDetailsVO.getOrderDescription());
        print(writer, transactionDetailsVO.getCustomerId()==null ? "-": transactionDetailsVO.getCustomerId());
        print(writer, transactionDetailsVO.getIssuingBank()==null ? "-":transactionDetailsVO.getIssuingBank());
        print(writer, transactionDetailsVO.getFromtype()==null ? "-":transactionDetailsVO.getFromtype());
        print(writer, transactionDetailsVO.getAccountId()==null ? "-": transactionDetailsVO.getAccountId());
        print(writer, transactionDetailsVO.getTerminalId()==null ? "-":transactionDetailsVO.getTerminalId());
        print(writer, paymodeid);
        print(writer, transactionDetailsVO.getCardtype() == null ? "-" : transactionDetailsVO.getCardtype());
        print(writer, transactionDetailsVO.getCurrency());
        print(writer, transactionDetailsVO.getName() == null ? "-" : transactionDetailsVO.getName());
        print(writer, transactionDetailsVO.getEmailaddr() == null ? "-" : functions.getEmailMasking(transactionDetailsVO.getEmailaddr()));
        print(writer,transactionDetailsVO.getCountry()== null ? "-" : transactionDetailsVO.getCountry());
        print(writer, transactionDetailsVO.getAmount());
        print(writer, transactionDetailsVO.getCaptureAmount());
        print(writer, transactionDetailsVO.getRefundAmount());
        print(writer, transactionDetailsVO.getPayoutamount());
        String encCardNum = transactionDetailsVO.getCcnum();
        if (functions.isValueNull(encCardNum))
        {
            firstSix = encCardNum.substring(0, 6);
            lastFour = encCardNum.substring((encCardNum.length() - 4), encCardNum.length());
        }
        print(writer, firstSix);
        if (functions.isValueNull(lastFour))
        {
            print(writer, "'"+lastFour+"'");
        }
        else
        {
            print(writer, lastFour);
        }
        print(writer, (String) statushash.get(transactionDetailsVO.getStatus()));
        print(writer,  transactionDetailsVO.getChargeBackInfo() == null? "-" : transactionDetailsVO.getChargeBackInfo());
        print(writer, transactionDetailsVO.getRemark() == null ? "-" : transactionDetailsVO.getRemark());
        printLast(writer,(transactionDetailsVO.getTransactionTime()));
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
    private void printTransactionHistory(PrintWriter writer, TransactionDetailsVO transactionDetailsVO, int pos,String pos1)
    {
        Functions functions = new Functions();
        /*ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        String errorName = transactionDetailsVO.getErr;
        String errorCode = "-";
        String errorDescription = "-";
        if (functions.isValueNull((String) transactionHash.get("errorName")))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(errorName));
            errorCode = errorCodeVO.getApiCode();
            errorDescription = errorCodeVO.getApiDescription();
        }*/
        print(writer, String.valueOf(pos));
        print(writer, transactionDetailsVO.getTrackingid());
        print(writer, transactionDetailsVO.getTrackingid() + "_" + pos1);
        print(writer, transactionDetailsVO.getAction()==null ? "-":transactionDetailsVO.getAction());
        print(writer, transactionDetailsVO.getAmount());
        print(writer, transactionDetailsVO.getCurrency()==null ? "-":transactionDetailsVO.getCurrency());
        String errorCode1 = transactionDetailsVO.getErrorCode();
        String errorDescriptor = transactionDetailsVO.getErrorDescription();
        String responsetransaction = transactionDetailsVO.getPaymentId();

        String arn = transactionDetailsVO.getArn();
        String remark = transactionDetailsVO.getRemark();
        String datetime = transactionDetailsVO.getTransactionTime();
        String responseDescriptor = transactionDetailsVO.getReponseDescriptor();
        String responseCode = transactionDetailsVO.getResponseCode();
        String responseDesc = transactionDetailsVO.getDescription();
        String currency = transactionDetailsVO.getCurrency();
        String amount = transactionDetailsVO.getAmount();
        String templatecurrency = transactionDetailsVO.getTemplatecurrency();
        String templateamount = transactionDetailsVO.getTemplateamount();
        String paymentbrand = transactionDetailsVO.getCardtype();
        if (paymentbrand==null || paymentbrand.equals("")){
            paymentbrand="-";
        }
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
       /* if ("22".equals(transactionDetailsVO.getCardTypeId()))
        {
            print(writer, transactionDetailsVO.getResponseHashInfo() == null ? "-" : transactionDetailsVO.getResponseHashInfo());
        }*/
        print(writer, transactionDetailsVO.getActionExecutorName());
        print(writer, templateamount);
        print(writer, templatecurrency);
        printLast(writer, paymentbrand);
//        print(writer, (String) transactionHash.get("walletAmount"));
        //       printLast(writer, (String) transactionHash.get("walletCurrency"));
    }
    private void printHeaderHistory(PrintWriter writer)
    {
        print(writer, " Sr no");
        print(writer,"Parent Tracking ID");
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
        /*if ("22".equals(cardtypeId))
        {
            print(writer, "Verification Code");
        }*/
        print(writer,"Action Executor");
        print(writer,"TMPL_Amount");
        print(writer,"TMPL_Currency");
        printLast(writer, "Payment Brand");
    }
    public Date previousDateString(Date date)
            throws ParseException
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        return yesterday;
    }
}
