package servlets;

import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.dao.PayoutDAO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;
import com.directi.pg.fileupload.FileUploadBean;
/**
 * Created by Sanjay on 1/10/2022.
 */
public class BdPayoutBatch extends HttpServlet
{


    private static Logger log=new Logger(BdPayoutBatch.class.getName());

    public void doGet(HttpServletRequest  req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }


    public void doPost(HttpServletRequest req , HttpServletResponse res) throws  IOException, ServletException
    {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher    rd = req.getRequestDispatcher("/bdPayoutBatch.jsp?ctoken=" + user.getCSRFToken());

        RequestDispatcher  rd1 = req.getRequestDispatcher("/bdPayoutDetailsUpdate.jsp?ctoken=" + user.getCSRFToken());
      //  RequestDispatcher  rd1 = req.getRequestDispatcher("/bdPayoutDetailsUpdate.jsp?ctoken=" + user.getCSRFToken());


        String action = "";
        Hashtable payoutBatch = null;
        List<TransactionDetailsVO> transactionDetailsVOs = null;


        PayoutDAO payoutDAO = new PayoutDAO();
        FileUploadBean fub = new FileUploadBean();

        try
        {

            if (req.getParameter("action") != null)
            {
                action = req.getParameter("action");
            }
            log.error("button value=== "+action);

            String filePath         = ApplicationProperties.getProperty("BANGLA_PAYOUT_FILE_STORE");
            String logPath          = ApplicationProperties.getProperty("LOG_STORE");
            String fdate = Functions.checkStringNull(req.getParameter("fdate"));
            String tdate = Functions.checkStringNull(req.getParameter("tdate"));
            String fmonth = Functions.checkStringNull(req.getParameter("fmonth"));
            String tmonth = Functions.checkStringNull(req.getParameter("tmonth"));
            String fyear = Functions.checkStringNull(req.getParameter("fyear"));
            String tyear = Functions.checkStringNull(req.getParameter("tyear"));
            String memberid = Functions.checkStringNull(req.getParameter("toid"));
            String accountid = Functions.checkStringNull(req.getParameter("accountid"));
            String walletname = Functions.checkStringNull(req.getParameter("walletname"));
            log.error("walletname-- "+walletname);
//            System.out.println("walletname===== "+walletname);
            String startTime = Functions.checkStringNull(req.getParameter("starttime"));
            String endTime = Functions.checkStringNull(req.getParameter("endtime"));

            String actionExecutorName   = (String)session.getAttribute("username");
            String actionExecutorId     = (String)session.getAttribute("merchantid");
            ArrayList<TransactionDetailsVO> transactionDetailsLit = null;
            AuditTrailVO auditTrailVO    = new AuditTrailVO();
            Calendar rightNow = Calendar.getInstance();
            if (fdate == null) fdate = "" + 1;
            if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

            if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
            if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

            if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
            if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

            if (functions.isValueNull(startTime))
            {
                startTime = startTime.trim();
            }
            else
            {
                startTime = "00:00:00";
            }

            if (functions.isValueNull(endTime))
            {
                endTime = endTime.trim();
            }
            else
            {
                endTime = "23:59:59";
            }

            String startTimeArr[] = startTime.split(":");
            String endTimeArr[] = endTime.split(":");
            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);
            log.error("fdtstamp-- " + fdtstamp);
            log.error("tdtstamp-- " + tdtstamp);
            if (action.equals("search"))
            {
                log.error("inside search++++ " );

                payoutBatch = payoutDAO.getPayoutBatchALlDetails(fdtstamp,tdtstamp);
                log.error("payoutBatch detaild in search--" + payoutBatch);
                req.setAttribute("payoutbatch", payoutBatch);

                rd = req.getRequestDispatcher("/bdPayoutBatch.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if(action.equals("download"))
            {


                log.error("inside download button ++++ " );
                String errorMessage="";
                if(walletname == null || walletname.equalsIgnoreCase("0") || walletname.trim().isEmpty()){
//                    errorMessage = "Please Provide Valid Bank Name";
                    errorMessage = "Please Select Valid Bank Name";

                }
                if(!errorMessage.isEmpty())
                {
                    req.setAttribute("error",errorMessage);
                    rd = req.getRequestDispatcher("/bdPayoutBatch.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);

                    return;
                }

                transactionDetailsVOs = payoutDAO.getPayoutDetailsExcel(fdtstamp, tdtstamp, walletname, memberid, accountid);
                if (transactionDetailsVOs != null && transactionDetailsVOs.size() > 0)
                {

                    int id = payoutDAO.getLastBatchId();
                    id++;
//                    PO_BATCH_NGD_1

                    String batchId =      getbankName(walletname) + id;
                String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
                String fileName = "PayoutBatch_"+batchId+".csv";
                String fullFilePath = exportPath+fileName;
                OutputStream os = new FileOutputStream(exportPath + "/" + fileName);
                os.write(239);//239 EF
                os.write(187);//187 BB
                os.write(191);//191 BF
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
                printHeader(writer, walletname);

                TransactionDetailsVO transactionDetails = null;
                String sysStatus = "downloaded";
                String bankBatchid = "";
                int bankCount = 0;

                String bankStatus = "";
                String userName = null;

                String BatchTrackindIds = "";
                log.error("inside transactionDetailsVOs.size() button ++++ "+transactionDetailsVOs.size() );

                    int translist = transactionDetailsVOs.size();
                    double totalAmount = 0.00;
                    for (int i = 0; i < transactionDetailsVOs.size(); i++)
                    {

                        TransactionDetailsVO transactionDetailsVO = transactionDetailsVOs.get(i);
                        log.error("transactionDetails--" + i);

                        if (functions.isValueNull(transactionDetailsVO.getTrackingid()))
                        {
                            totalAmount = totalAmount + Double.parseDouble(transactionDetailsVO.getAmount());
                            printTransaction(writer, transactionDetailsVO, batchId);

                        }
                        else
                        {
                            transactionDetails = transactionDetailsVOs.get(i);
                            log.error("transactionDetails--" + transactionDetails.getCount());

                        }

                        if (functions.isValueNull(BatchTrackindIds))
                        {
                            BatchTrackindIds += "," + transactionDetailsVO.getTrackingid();

                        }
                        else
                        {
                            BatchTrackindIds += transactionDetailsVO.getTrackingid();
                        }
                    }
                    int sysCount = translist ;

                    String sysAmount = totalAmount + "";

                    payoutDAO.updateSystemBatchId(batchId,walletname,BatchTrackindIds);

                    String insert = payoutDAO.insertIntoPayoutBatch(batchId, sysCount, sysAmount, sysStatus, walletname, fullFilePath, bankBatchid, bankCount, bankStatus, actionExecutorId, actionExecutorName);

                    writer.close();
                    sendFile(exportPath + "/" + fileName, fileName, res);

                    return;
                }
                else
                {
                    String error="No pending payout transaction found";
                    req.setAttribute("error", error);

                    rd.forward(req, res);
                    return;
                }
            }

            if(action.equals("deleteBatch"))
            {
                log.error("inside deleteBatch button ++++ " );
                String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
                String errormsg="";
                String deleteBatchId = req.getParameter("deleteBatch");
                String deleteWalletName = req.getParameter("deleteWalletName");
                String fileName = exportPath+"/"+"PayoutBatch_"+deleteBatchId+".csv";
                log.error("deleteBatchId "+deleteBatchId +" deleteWalletName "+ deleteWalletName);
                deleteErrorExcelFile(fileName);
                int sysBach= payoutDAO.deleteSystemBatchId(deleteBatchId);
                transactionDetailsLit   = payoutDAO.getTransactionCommonBySysBatchId(deleteBatchId);
                if(sysBach>0)
                {
                    payoutDAO.updateWalletNameAndBatchIdForDelete(deleteBatchId, deleteWalletName,"payoutstarted");
                    if(transactionDetailsLit != null && transactionDetailsLit.size() > 0){
                        payoutDAO.updateTransactionCommonAndDetails(transactionDetailsLit, auditTrailVO, PZTransactionStatus.PAYOUT_STARTED.toString(), ActionEntry.ACTION_PAYOUT_STARTED);
                    }
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b> Payout "+deleteBatchId+" Deleted Successfuly! </b></font></center>";
                }
                else
                {
                    errormsg += "<center><font class=\"textb\" face=\"arial\"><b>You Can Not Delete Uploaded Batch! Only Downloaded Batch Can Be Deleted </b></font></center>";
                }
                req.setAttribute("message",errormsg);
                rd = req.getRequestDispatcher("/bdPayoutBatch.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if(action.equals("updateBatch"))
            {

                log.error("inside updateBatch button ++++ " );

                String bankBatchId ="";
                String bankAmount ="";
                int bankCount1 = 0;
                String bankStatus="pending";


                if(functions.isValueNull(req.getParameter("bankBatchId")))
                {
                    bankBatchId =req.getParameter("bankBatchId");

                }
                if(functions.isValueNull(req.getParameter("bankCount")))
                {
                    bankCount1 = Integer.parseInt(req.getParameter("bankCount"));

                }
                if(functions.isValueNull(req.getParameter("bankAmount")))
                {
                    bankAmount =req.getParameter("bankAmount");

                }

                    String message1 = "";
                    String sysBatchid = req.getParameter("sysBatchid");
                    req.setAttribute("sysBatchid", sysBatchid);

                auditTrailVO.setActionExecutorName(actionExecutorName);
                auditTrailVO.setActionExecutorId(actionExecutorId);


                log.error("inside update bank batch id :::" + bankBatchId + " sysBatchid " + sysBatchid+" bankAmount::"+bankAmount);
                if((functions.isValueNull(bankBatchId)&&functions.isValueNull(sysBatchid)) && (bankCount1>0&&functions.isValueNull(bankAmount))){

                    boolean result = payoutDAO.bdPayoutDetailsForUpdate(bankBatchId, bankCount1, bankStatus, sysBatchid,bankAmount);
                    if (result)
                    {
                        boolean message = payoutDAO.updateTransactionCommonBankBatchId(sysBatchid, bankBatchId,"payoutuploaded");
                        transactionDetailsLit   = payoutDAO.getTransactionCommonBySysBatchId(sysBatchid);

                        if(transactionDetailsLit != null && transactionDetailsLit.size() > 0){
                            payoutDAO.updateTransactionCommonAndDetails(transactionDetailsLit, auditTrailVO, PZTransactionStatus.PAYOUT_UPLOADED.toString(), ActionEntry.ACTION_PAYOUT_UPLOADED_SUCCESSFUL);
                        }
                        if (message)
                        {
                            message1 = "BankBatch Details Updated Sucessfuly!";
                            req.setAttribute("message", message1);
                            rd  .forward(req, res);
                            return;
                        }
                        else
                        {
                            message1 = "";
                        }
                    }
                }else{
                    message1="Failed to Update Record! Please Pass Bank Batchid ,BankCount & BankAmount";
                }
                    req.setAttribute("message", message1);
                    rd1  .forward(req, res);
                    return;
                }
        }
        catch (Exception e)
        {
            log.error("Exception  ---", e);

        }
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
//            file.delete();
        }
        catch (Exception e)
        {
            log.debug("Exception:::::"+e);
        }
        log.info("Successful#######");
        return true;

    }

    private void printTransaction(PrintWriter writer, TransactionDetailsVO transactionDetailsVO, String batchId)
    {
        Functions functions = new Functions();

        try
        {

            String trackingid = "";
            if (functions.isValueNull( transactionDetailsVO.getTrackingid()))
            {
                trackingid = "'" + transactionDetailsVO.getTrackingid();
            }
            String mobileNumber = "-";
            if (functions.isValueNull( transactionDetailsVO.getTelno()))
            {
                mobileNumber = "'" +  transactionDetailsVO.getTelno();
            }

            String amount = "0.00";
            if (functions.isValueNull( transactionDetailsVO.getAmount()))
            {
                amount = "'" + transactionDetailsVO.getAmount();
            }
            String naration = "-";
            if (functions.isValueNull(transactionDetailsVO.getName()))

            {
                naration = transactionDetailsVO.getName();
                log.error("naration::::: "+naration);
            }

            String status = "-";
            if (functions.isValueNull( transactionDetailsVO.getStatus()))
            {
                status = "'"+ transactionDetailsVO.getStatus();
            }

            print(writer, mobileNumber);
            print(writer, amount);
            if("nagad".equalsIgnoreCase(naration))
            {
                log.error("inside nagad naration::::: "+naration);
                print(writer, "'"+naration);
            }
            print(writer, trackingid);
            print(writer, batchId);
            printLast(writer, "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void printHeader(PrintWriter writer, String walletname)
    {
        print(writer, "Mobile Number");
        if("bkash".equalsIgnoreCase(walletname))
        {
            print(writer,"Principal Amount");

            //print(writer,"Narration");
        }else
        {
            print(writer,"Disbursement Amount");

        }
        if("nagad".equalsIgnoreCase(walletname))
        {
            print(writer,"Narration");
        }
        print(writer, "Param1");
        print(writer, "Param2");
        printLast(writer, "Status");

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


    public  boolean deleteErrorExcelFile(String filename){
        File deleteFile         = new File(filename) ;
        boolean isFileDeleted   = false;

        if(deleteFile.exists()){
            isFileDeleted = deleteFile.delete() ;
        }
        return isFileDeleted;
    }


    public String getbankName(String walletname)
    {
        String bankname = "";

        if("nagad".equalsIgnoreCase(walletname))
            bankname = "PO_BATCH_NGD_";
        if("bkash".equalsIgnoreCase(walletname))
            bankname = "PO_BATCH_BKS_";

        return bankname;
    }
}
