package com.payment;


import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.MyMonederoPaymentGateway;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.directi.pg.core.valueObjects.MyMonederoRequestVO;
import com.directi.pg.core.valueObjects.MyMonederoResponseVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.*;
import com.payment.response.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Mar 14, 2013
 * Time: 8:07:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoPaymentProcess   extends CommonPaymentProcess
{

    private static Logger log = new Logger(MyMonederoPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(MyMonederoPaymentProcess.class.getName());
    Connection conn = null;
    BigDecimal bdConst = new BigDecimal("0.01");


    @Override
    public String getAdminEmailAddress()
    {
        return "raj@pz.com";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override

    public int insertTransactionDetails(Hashtable parameters) throws SystemError
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRedirectPage(Hashtable parameters) throws SystemError
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PZTransactionResponse transaction(PZTransactionRequest refundRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PZRefundResponse refund(PZRefundRequest refundRequest)
    {
        PZRefundResponse refundResponse = new PZRefundResponse();
        try
        {
        Integer trackingId = refundRequest.getTrackingId();
        Integer accountId = refundRequest.getAccountId();
        String refundAmount = refundRequest.getRefundAmount();
        String refundReason = refundRequest.getRefundReason();
        boolean isFraud =  refundRequest.isFraud();
        boolean isAdmin =  refundRequest.isAdmin();
        conn = Database.getConnection();

        MyMonederoRequestVO myMonederoRequestVO= new MyMonederoRequestVO();
        GenericTransDetailsVO genericTransDetailsVO = new  GenericTransDetailsVO();
        MyMonederoResponseVO myMonederoResponseVO = new MyMonederoResponseVO();


            AuditTrailVO auditTrailVO=new AuditTrailVO();
            auditTrailVO.setActionExecutorId(myMonederoRequestVO.getMemberid());
            auditTrailVO.setActionExecutorName("Customer");

        String transaction = "select trackingid,toid,fromid,description,amount,captureamount,transid,transaction_common.accountid,status,timestamp,name,ewalletid,paymentid,members.contact_emails,members.company_name from transaction_common,members where status IN ('settled','capturesuccess') and transaction_common.toid=members.memberid and trackingid=?";
        PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
        transPreparedStatement.setString(1, String.valueOf(trackingId));
        ResultSet rstransaction = transPreparedStatement.executeQuery();
        if (rstransaction.next())
        {
            BigDecimal refundamount = null;
            try
            {
                refundamount = new BigDecimal(refundAmount);
                if (!Functions.checkAccuracy(String.valueOf(refundAmount), 2))
                {
                    refundResponse.setStatus(PZResponseStatus.FAILED);
                    refundResponse.setResponseDesceiption("Refund Amount should be 2 decimal places accurate");
                    return refundResponse;
                }
            }
            catch (NumberFormatException e)
            {
                log.debug("Invalid Refund amount" + refundAmount + "-" + e.getMessage());
                transactionLogger.debug("Invalid Refund amount" + refundAmount + "-" + e.getMessage());
                refundResponse.setStatus(PZResponseStatus.FAILED);
                refundResponse.setResponseDesceiption("Invalid Refund Amount");
                return refundResponse;
            }
            String trackingIdDB = rstransaction.getString("trackingid");
            String transid = rstransaction.getString("transid");
            String accountIdDB = rstransaction.getString("accountid");
            String rsdescription = rstransaction.getString("description");
            String captureamount = rstransaction.getString("captureamount");
            String transstatus = rstransaction.getString("status");
            BigDecimal amt = new BigDecimal(captureamount);
            String userid = rstransaction.getString("ewalletid");
            String wctxnid = rstransaction.getString("paymentid");

            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountIdDB);


            if (Double.parseDouble(refundAmount) > Double.parseDouble(captureamount))
            {
                log.debug("Refund Amount " + refundamount + " is greater than capture amount transaction id is"+trackingId);
                transactionLogger.debug("Refund Amount " + refundamount + " is greater than capture amount transaction id is"+trackingId);
                refundResponse.setStatus(PZResponseStatus.FAILED);
                refundResponse.setResponseDesceiption("Refund Amount " + refundamount + " is greater than capture amount transaction id is"+trackingId);
                return refundResponse;
            }


            String remark;
            String description;
             if(isFraud)
            {
                if(isAdmin)
                {
                remark="Refund BY ADMIN (FRAUD Transaction)"; //set in VO object
                description = "Refund of " + transid +"  (Fraudulent Transaction)";
                }
                else
                {
                remark="Refund BY MERCHANT (FRAUD Transaction)"; //set in VO object
                description = "Refund of " + transid +"  (Fraudulent Transaction)";
                }

            }
            else
             {
                 if(isAdmin)
                {
                remark="Refund BY ADMIN "; //set in VO object
                description = "Refund of " + transid +"  ";
                }
                else
                {
                remark="Refund BY MERCHANT (FRAUD Transaction)"; //set in VO object
                description = "Refund of " + transid +"  ";
                }

             }

            myMonederoResponseVO.setResponseRemark(remark);


            //add charges and change status to markedforreverse
            TransactionEntry transactionEntry = new TransactionEntry();
            transactionEntry.newGenericRefundTransaction(trackingIdDB,refundamount,accountIdDB,description,myMonederoResponseVO,auditTrailVO);
            log.debug("Added charges and changed the status to markedforreversal");
            transactionLogger.debug("Added charges and changed the status to markedforreversal");

            //Preparing to call gateway for refund
            genericTransDetailsVO.setAmount(refundamount.toString());
            genericTransDetailsVO.setOrderId(rsdescription);
            myMonederoRequestVO.setGenericTransDetailsVO(genericTransDetailsVO);
            myMonederoRequestVO.setUserId(userid);

            AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountIdDB);

            myMonederoResponseVO = (MyMonederoResponseVO)paymentGateway.processRefund(trackingIdDB, myMonederoRequestVO);
            log.debug("Got Response from gateway");
            transactionLogger.debug("Got Response from gateway");

            if(myMonederoResponseVO!=null && myMonederoResponseVO.getStatus().equalsIgnoreCase("SUCCESSFUL"))
            {

                Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_common set status='reversed'");
                sb.append(",refundamount='"+ ESAPI.encoder().encodeForSQL(MY,refundamount.toString())+"'");
                sb.append(",paymentid='"+ESAPI.encoder().encodeForSQL(MY,myMonederoResponseVO.getWctxnid())+"'");
                sb.append(",refundinfo='"+ESAPI.encoder().encodeForSQL(MY,refundReason)+"'");
                sb.append(" where trackingid=" +ESAPI.encoder().encodeForSQL(MY,trackingIdDB)+ " and status = 'markedforreversal'");

                int rows = Database.executeUpdate(sb.toString(), conn);
                log.debug("No of Rows updated : " + rows + "<br>");
                transactionLogger.debug("No of Rows updated : " + rows + "<br>");



                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.genericActionEntry(trackingIdDB,refundamount.toString(),ActionEntry.ACTION_REVERSAL_SUCCESSFUL,ActionEntry.STATUS_REVERSAL_SUCCESSFUL,null,account.getGateway(),myMonederoResponseVO,auditTrailVO);
                entry.closeConnection();

                refundResponse.setStatus(PZResponseStatus.SUCCESS);
                refundResponse.setResponseDesceiption(rsdescription);
                refundResponse.setTrackingId(trackingIdDB);


            }
            else
            {
               log.debug("Error while Process refund Transaction. TrackingId:"+trackingId);
               transactionLogger.debug("Error while Process refund Transaction. TrackingId:"+trackingId);
               refundResponse.setStatus(PZResponseStatus.FAILED);
               refundResponse.setResponseDesceiption("Error while Process refund Transaction. TrackingId:"+trackingId);
               return refundResponse;
            }

        }
        else
        {
            refundResponse.setStatus(PZResponseStatus.FAILED);
            refundResponse.setResponseDesceiption("Transaction not found");
        }


        }
         catch (PZTechnicalViolationException e)
        {
            log.error("PzTechnical Exception while refunding transaction VIA MyMonedero",e);
            transactionLogger.error("PzTechnical Exception while refunding transaction VIA MyMonedero",e);

            PZExceptionHandler.handleTechicalCVEException(e,null,"refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while refunding transaction VIA MyMonedero",e);
            transactionLogger.error("PZConstraintViolationException while refunding transaction VIA MyMonedero",e);

            PZExceptionHandler.handleCVEException(e, null, "Refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException while refunding transaction VIA MyMonedero",e);
            transactionLogger.error("PZDBViolationException while refunding transaction VIA MyMonedero",e);

            PZExceptionHandler.handleDBCVEException(e, null, "Refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQLException while refunding transaction VIA MyMonedero",e);
            transactionLogger.error("SQLException while refunding transaction VIA MyMonedero",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(MyMonederoPaymentProcess.class.getName(),"refund",null,"common","Technical Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("PZDBViolationException while refunding transaction VIA MyMonedero",systemError);
            transactionLogger.error("PZDBViolationException while refunding transaction VIA MyMonedero",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(MyMonederoPaymentProcess.class.getName(),"refund",null,"common","Technical Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"Refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + systemError.getMessage());
        }

        finally
        {
            Database.closeConnection(conn);
        }

        return refundResponse;


    }

    public PZCaptureResponse capture(PZCaptureRequest captureRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PZCancelResponse cancel(PZCancelRequest cancelRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PZChargebackResponse chargeback(PZChargebackRequest pzChargebackRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PZStatusResponse status(PZStatusRequest pzStatusRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

     public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        log.debug("Inside MyMonedero Inquiry");
        transactionLogger.debug("Inside MyMonedero Inquiry");
        PZInquiryResponse inquiryResponse = new PZInquiryResponse();
        try
        {
        Integer trackingId = pzInquiryRequest.getTrackingId();
        Integer accountId = pzInquiryRequest.getAccountId();
        conn = Database.getConnection();

        MyMonederoRequestVO myMonederoRequestVO= new MyMonederoRequestVO();
        GenericTransDetailsVO genericTransDetailsVO = new  GenericTransDetailsVO();
        MyMonederoResponseVO myMonederoResponseVO = null;


       String transaction = "select trackingid,paymentid as wctxnid,accountid,amount,currency,status,refundamount from transaction_common where trackingid=?";
       PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
       transPreparedStatement.setString(1, String.valueOf(trackingId));
       ResultSet rstransaction = transPreparedStatement.executeQuery();
       if (rstransaction.next() && rstransaction.getString("wctxnid")!=null && !rstransaction.getString("wctxnid").equals(""))
       {

           String trackingIdDB = rstransaction.getString("trackingid");
           String accountIdDB = rstransaction.getString("accountid");
           String amount = rstransaction.getString("amount");
           String currency = rstransaction.getString("currency");
           String wctxnid = rstransaction.getString("wctxnid");
           String transactionStatus = rstransaction.getString("status");
           if(rstransaction.getString("status").equals("reversed"))
           amount=rstransaction.getString("refundamount");

           GatewayAccount account = GatewayAccountService.getGatewayAccount(accountIdDB);

            log.debug("Setting Details====");
            transactionLogger.debug("Setting Details====");
            genericTransDetailsVO.setAmount(amount);
            genericTransDetailsVO.setCurrency(currency);
            myMonederoRequestVO.setWctxnid(wctxnid);
            myMonederoRequestVO.setGenericTransDetailsVO(genericTransDetailsVO);
            log.debug("Calling Gateway");
            transactionLogger.debug("Calling Gateway");
            MyMonederoPaymentGateway pg= new MyMonederoPaymentGateway(accountIdDB);
            myMonederoResponseVO=(MyMonederoResponseVO ) pg.processQuery(trackingIdDB,myMonederoRequestVO);



            if(myMonederoResponseVO!=null)
            {
                inquiryResponse.setStatus(PZResponseStatus.SUCCESS);
                inquiryResponse.setTrackingId(myMonederoResponseVO.getTrackingid());
                inquiryResponse.setResponseTransactionId(myMonederoResponseVO.getWctxnid());
                /*if(transactionStatus.equals(String.valueOf(PZTransactionStatus.REVERSED))||transactionStatus.equals(String.valueOf(PZTransactionStatus.MARKED_FOR_REVERSAL)))
                inquiryResponse.setResponseTransactionStatus(String.valueOf(PZTransactionStatus.REVERSED));
                else
                inquiryResponse.setResponseTransactionStatus(String.valueOf(PZTransactionStatus.CAPTURE_SUCCESS));*/
                inquiryResponse.setResponseTransactionStatus(myMonederoResponseVO.getStatus());
                inquiryResponse.setResponseCode(myMonederoResponseVO.getError());
                inquiryResponse.setResponseTime(myMonederoResponseVO.getTransactionDate());
                inquiryResponse.setResponseName1("sourceid");
                inquiryResponse.setResponseValue1(myMonederoResponseVO.getSourceID());
                inquiryResponse.setResponseName2("destid");
                inquiryResponse.setResponseValue2(myMonederoResponseVO.getDestID());

                log.debug("======Inquiry Details== for Tracking Id :==="+trackingId);
                log.debug("======WCT Transaction Id==="+inquiryResponse.getResponseTransactionId());
                log.debug("======Transaction Status==="+inquiryResponse.getResponseTransactionStatus());
                log.debug("======ErrorCode==="+inquiryResponse.getResponseCode());
                log.debug("======Transaction Date==="+inquiryResponse.getResponseTime());

                transactionLogger.debug("======Inquiry Details== for Tracking Id :==="+trackingId);
                transactionLogger.debug("======WCT Transaction Id==="+inquiryResponse.getResponseTransactionId());
                transactionLogger.debug("======Transaction Status==="+inquiryResponse.getResponseTransactionStatus());
                transactionLogger.debug("======ErrorCode==="+inquiryResponse.getResponseCode());
                transactionLogger.debug("======Transaction Date==="+inquiryResponse.getResponseTime());

            }
           else
           {
               log.debug("======Inquiry Details== for Tracking Id :==="+trackingId);
               log.debug("======Inquiry Failed===");

               transactionLogger.debug("======Inquiry Details== for Tracking Id :==="+trackingId);
               transactionLogger.debug("======Inquiry Failed===");
               inquiryResponse.setStatus(PZResponseStatus.FAILED);
               inquiryResponse.setResponseDesceiption("Transaction not found while Inquiry Transaction. TrackingId:"+trackingId);
               return inquiryResponse;
           }


       }

       else
        {
            log.debug("======Transaction Not Found in DB===");
            transactionLogger.debug("======Transaction Not Found in DB===");
            inquiryResponse.setStatus(PZResponseStatus.FAILED);
            inquiryResponse.setResponseDesceiption("Transaction not found or WCTnxId is null");
        }



        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PzTechnical Exception while refunding transaction VIA MyMonedero",e);
            transactionLogger.error("PzTechnical Exception while refunding transaction VIA MyMonedero",e);

            PZExceptionHandler.handleTechicalCVEException(e,null,"inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while refunding transaction VIA MyMonedero",e);
            transactionLogger.error("PZConstraintViolationException while refunding transaction VIA MyMonedero",e);

            PZExceptionHandler.handleCVEException(e, null, "inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQLException while refunding transaction VIA MyMonedero",e);
            transactionLogger.error("SQLException while refunding transaction VIA MyMonedero",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(MyMonederoPaymentProcess.class.getName(),"inquiry",null,"common","Technical Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("PZDBViolationException while refunding transaction VIA MyMonedero",systemError);
            transactionLogger.error("PZDBViolationException while refunding transaction VIA MyMonedero",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(MyMonederoPaymentProcess.class.getName(),"inquiry",null,"common","Technical Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + systemError.getMessage());
        }

        finally
        {
            Database.closeConnection(conn);
        }

        return inquiryResponse;



    }

    @Override
    public List<PZReconcilationResponce> reconcilationTransaction(List<PZReconcilationRequest> pzReconcilationRequests) throws SystemError
    {
        log.debug("====Inside Mymonedero reconcilationTransaction.====");
        transactionLogger.debug("====Inside Mymonedero reconcilationTransaction.====");
        List<PZReconcilationResponce> vResponseList = new ArrayList<PZReconcilationResponce>();
        List<PZReconcilationRecord> vReconList = new ArrayList<PZReconcilationRecord>();
        PZReconcilationRecord pzReconcilationRecord = null;
        PZReconcilationResponce pzReconcilationResponce =null;


        for (PZReconcilationRequest pZReconcilationRequest:pzReconcilationRequests)
        {

            PZInquiryRequest pzInquiryRequest= new PZInquiryRequest();
            pzInquiryRequest.setAccountId(pZReconcilationRequest.getAccountId());
            pzInquiryRequest.setTrackingId(pZReconcilationRequest.getTrackingId());
            
            PZInquiryResponse pzInquiryResponse = inquiry(pzInquiryRequest);
            
            if(pzInquiryResponse.getStatus().equals(PZResponseStatus.SUCCESS))
            {
                log.debug("====Inside Mymonedero reconcilationTransaction.==== Inquiry Success");
                transactionLogger.debug("====Inside Mymonedero reconcilationTransaction.==== Inquiry Success");
                pzReconcilationRecord = new PZReconcilationRecord();

                if(pzInquiryResponse.getResponseTransactionStatus().equals("SUCCESSFUL"))
                {
                    pzReconcilationRecord.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_SUCCESS));
                }
                else if(pzInquiryResponse.getResponseTransactionStatus().equals("INITIATED"))
                {
                    pzReconcilationRecord.setStatusDetail(String.valueOf(PZTransactionStatus.AUTH_CANCELLED));
                }
                else if(pzInquiryResponse.getResponseTransactionStatus().equals("FAILED"))
                {
                    pzReconcilationRecord.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));

                }

                pzReconcilationRecord.setSDateTime(pzInquiryResponse.getResponseTime());
                //pzSettlementRecord.setAmount();
                pzReconcilationRecord.setMerchantTXNid(pzInquiryResponse.getTrackingId());
                pzReconcilationRecord.setPaymentid(pzInquiryResponse.getResponseTransactionId());
                vReconList.add(pzReconcilationRecord);

            }
            else if(pzInquiryResponse.getStatus().equals(PZResponseStatus.FAILED))
            {
                log.debug("====Inside Mymonedero reconcilationTransaction.==== Inquiry Failed");
                transactionLogger.debug("====Inside Mymonedero reconcilationTransaction.==== Inquiry Failed");
                pzReconcilationRecord = new PZReconcilationRecord();
                pzReconcilationRecord.setStatusDetail(String.valueOf(PZTransactionStatus.FAILED));
                pzReconcilationRecord.setSDateTime(pzInquiryResponse.getResponseTime());
                //pzSettlementRecord.setAmount();
                pzReconcilationRecord.setMerchantTXNid(pZReconcilationRequest.getTrackingId().toString());
                pzReconcilationRecord.setPaymentid(pZReconcilationRequest.getPaymentid());
                vReconList.add(pzReconcilationRecord);

            }
            else if(pzInquiryResponse.getStatus().equals(PZResponseStatus.ERROR))
            {
                //Exception during Inquiry No action
                log.debug("====Inside Mymonedero reconcilationTransaction.==== Exception during Inquiry No action");
                transactionLogger.debug("====Inside Mymonedero reconcilationTransaction.==== Exception during Inquiry No action");
                pzReconcilationResponce =new PZReconcilationResponce();
                pzReconcilationResponce.setTrackingId(pZReconcilationRequest.getTrackingId().toString());
                pzReconcilationResponce.setResaccountid(pZReconcilationRequest.getAccountId().toString());
                pzReconcilationResponce.setRestoid(pZReconcilationRequest.getMemberId().toString());
                pzReconcilationResponce.setAmount(pZReconcilationRequest.getAmount());
                //pzReconcilationResponce.setOrderDesc();
                pzReconcilationResponce.setResupdatedStatus(pZReconcilationRequest.getPZstatus());
                //pzReconcilationRecord.setStatusDetail(String.valueOf(PZTransactionStatus.FAILED));
                pzReconcilationResponce.setResponseDesceiption(pzInquiryResponse.getResponseDesceiption());

                vResponseList.add(pzReconcilationResponce);
            }


        }

        List<PZReconcilationResponce> vProcessedReconcileList = processReconcilation(vReconList,getAdminEmailAddress());
        for(PZReconcilationResponce vProcessedReconcileResponse: vProcessedReconcileList)
        {
            vResponseList.add(vProcessedReconcileResponse);
        }

        return vResponseList;
    }

   /* @Override
    public int actionEntry(String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws SystemError
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }*/


    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        log.debug("entering in my monedero");
        transactionLogger.debug("entering in my monedero");
        InputStream inputStream = null;
        List<PZSettlementRecord> vList = new ArrayList<PZSettlementRecord>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName.getFilepath()));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);

            if (inputStream == null)
            {
                throw new SystemError("Settlement File is not found");
            }

            // get rows of sheet
            Iterator rows = sheet.rowIterator();

            int cntRowToLeaveFromTop=0;
            int cntTotalRecords=0;
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                // Skip the header rows.
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Settlement Cron Error : Invalid File Format");
            }
            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;
                Iterator cells = row.cellIterator();
                PZSettlementRecord loadTransResponse=new PZSettlementRecord();
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();

                    if(cell.getCellNum()==4)
                    {
                        loadTransResponse.setSDateTime(cell.getStringCellValue()+"");
                        continue;
                    }
                    if(cell.getCellNum()==5)
                    {

                        loadTransResponse.setPaymentid((int)cell.getNumericCellValue()+"");
                        continue;
                    }
                    if(cell.getCellNum()==10)
                    {
                        if(cell.getStringCellValue()!=null && cell.getStringCellValue().equals("CR"))
                        {
                            loadTransResponse.setStatusDetail("settle");
                        }
                        else if(cell.getStringCellValue()!=null && cell.getStringCellValue().equals("DR"))
                        {
                            loadTransResponse.setStatusDetail("refund");
                        }
                        continue;
                    }
                    if(cell.getCellNum()==12)
                    {

                        loadTransResponse.setAmount(cell.getNumericCellValue()+"");
                        continue;
                    }
                    if(cell.getCellNum()==7)
                    {

                        loadTransResponse.setMerchantTXNid((int)cell.getNumericCellValue()+"");
                        continue;
                    }

                }
                if(loadTransResponse!= null)
                {
                    vList.add(loadTransResponse);
                }
            }
        }
        catch (IOException e)
        {
            throw new SystemError("ERROR: Settlement File cant be read");
        }
        finally
        {

            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: Settlement File cant be closed successfully");
                }

            }
        }
        return vList;
    }



    public List<PZReconcilationResponce> processReconcilation(List<PZReconcilationRecord> vTransactions, String toAddress)
    {
        log.debug("Inside processReconcilation");
        transactionLogger.debug("Inside processReconcilation");

        StringBuffer errString = new StringBuffer();

        List<PZReconcilationResponce> vReconcileList = new ArrayList<PZReconcilationResponce>();
        PZReconcilationResponce pzReconcilationResponce =null;
        //TransactionEntry transactionEntry = null;
        Transaction transaction = null;
        Connection conn = null;
        try
        {
            //transactionEntry = new TransactionEntry();
            transaction = new Transaction();
            conn = Database.getConnection();
        }
        catch (Exception e)
        {
            log.error("Common Reconcile Cron Processor : Error : " + e.getMessage());
            transactionLogger.error("Common Reconcile Cron Processor : Error : " + e.getMessage());
            return vReconcileList;
        }
        List vErrRecords = new ArrayList();
        int vSuccessTotalCount = 0;
        int vRefundTotalCount = 0;
        int vChargebackTotalCount = 0;
        int vAuthFailedTotalCount = 0;
        int vProcessingFailedCount = 0;
        errString.append("<br>|  Tracking ID  |   Merchant ID  |   Account ID  |        Order ID       |  Amount  |  Current Status  |      Treatment Given     |" + "\r\n");

        for (PZReconcilationRecord vSettleTransVO : vTransactions)
        {
            int trackingId = 0;

            try
            {
                String vPaymentOrderId = vSettleTransVO.getPaymentid();
                String vTrackingId = vSettleTransVO.getMerchantTXNid();
                String vAmount = vSettleTransVO.getAmount();

                //To be used to updated detail table
                CommResponseVO commResponseVO = new CommResponseVO();
                commResponseVO.setErrorCode(vPaymentOrderId);
                commResponseVO.setDescription("Updated via gateway Recon cron");
                commResponseVO.setTransactionId(vPaymentOrderId);


                String query = "select toid,fromid,description,captureamount,amount,trackingid,accountid,status from transaction_common where trackingid=?";
                PreparedStatement p1 = conn.prepareStatement(query);
                p1.setString(1, vTrackingId);
                ResultSet rs = p1.executeQuery();
                boolean isRecordFound = false;
                isRecordFound = rs.next();
                pzReconcilationResponce =new PZReconcilationResponce();
                if (isRecordFound)

                {

                    int toid = rs.getInt("toid");
                    String fromid = rs.getString("fromid");
                    String description = rs.getString("description");
                    trackingId = rs.getInt("trackingid");
                    int accountId = rs.getInt("accountid");
                    BigDecimal amount = new BigDecimal(rs.getString("amount"));
                    amount.setScale(2, BigDecimal.ROUND_HALF_UP);

                    pzReconcilationResponce.setTrackingId(trackingId+"");
                    pzReconcilationResponce.setResaccountid(accountId+"");
                    pzReconcilationResponce.setRestoid(toid+"");
                    pzReconcilationResponce.setAmount(amount+"");
                    pzReconcilationResponce.setOrderDesc(description);
                    //Processing Failed Transactions
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.AUTH_FAILED))
                            || vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.CAPTURE_FAILED)))
                    {
                        //Variable Definition

                        if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_STARTED)))
                        {



                            //update the status, PaymentOrderNumber and remark column for this transaction
                            if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED)))
                                query = "update transaction_common set paymentid=?, status='authfailed', remark='Bank Connectivity Issue' where trackingid=? ";
                            else
                                query = "update transaction_common set paymentid=?, status='capturefailed', remark='Bank Connectivity Issue' where trackingid=? ";
                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, vPaymentOrderId);
                            ps.setString(2, vTrackingId);
                            int result = ps.executeUpdate();

                            if (result == 1)
                            {
                                AuditTrailVO auditTrailVO=new AuditTrailVO();
                                auditTrailVO.setActionExecutorId(String.valueOf(toid));
                                auditTrailVO.setActionExecutorName("Customer");
                                //insert the status change action entry for authfailed to details table
                                int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, null,auditTrailVO,null);
                                if (num == 1)
                                {

                                    vSuccessTotalCount++;
                                    errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction Updated" + "\r\n");
                                    if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED)))
                                        pzReconcilationResponce.setResupdatedStatus(String.valueOf(PZTransactionStatus.AUTH_FAILED));
                                    else
                                        pzReconcilationResponce.setResupdatedStatus(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));
                                    pzReconcilationResponce.setResponseDesceiption("Transaction Updated");
                                }

                            }

                        }
                        else
                        {
                            vErrRecords.add(trackingId);
                            errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "No Update done" + "\r\n");
                            pzReconcilationResponce.setResupdatedStatus(rs.getString("status"));
                            pzReconcilationResponce.setResponseDesceiption("No Update done");
                        }

                    }
                    //Processing Successful Transactions which are not Refunded or not Chargebacked
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.SETTLED))
                            || vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.CAPTURE_SUCCESS)))
                    {


                        if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_STARTED)))
                        {


                            //update the status,captureamount, PaymentOrderNumber and remark column for this transaction
                            query = "update transaction_common set paymentid=?, status='capturesuccess',captureamount=?, remark='Settled via gateway report' where trackingid=? ";
                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, vPaymentOrderId);
                            ps.setString(2, amount.toString());
                            ps.setString(3, vTrackingId);
                            int result = ps.executeUpdate();

                            if (result == 1)
                            {
                                //insert the status change action entry for capture success to details table
                                AuditTrailVO auditTrailVO=new AuditTrailVO();
                                auditTrailVO.setActionExecutorId(String.valueOf(toid));
                                auditTrailVO.setActionExecutorName("Customer");

                                int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, null,auditTrailVO,null);
                                if (num == 1)
                                {

                                    vSuccessTotalCount++;
                                    errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction Updated" + "\r\n");
                                    pzReconcilationResponce.setResupdatedStatus(String.valueOf(PZTransactionStatus.CAPTURE_SUCCESS));
                                    pzReconcilationResponce.setResponseDesceiption("Transaction Updated");

                                }
                            }

                        }
                        else
                        {
                            vErrRecords.add(trackingId);
                            errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "No Update done" + "\r\n");
                            pzReconcilationResponce.setResupdatedStatus(rs.getString("status"));
                            pzReconcilationResponce.setResponseDesceiption("No Update done");
                        }


                    }

                    ////Processing Auth Cancelled Transactions
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.AUTH_CANCELLED)))
                    {

                        if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_STARTED)))
                        {


                            //update the status, PaymentOrderNumber and remark column for this transaction
                            if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED)))
                                query = "update transaction_common set paymentid=?, status='authcancelled', remark='Autherization Cancelled by User' where trackingid=? ";
                            else
                                query = "update transaction_common set paymentid=?, status='authcancelled', remark='Autherization Cancelled by User' where trackingid=? ";
                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, vPaymentOrderId);
                            ps.setString(2, vTrackingId);
                            int result = ps.executeUpdate();

                            if (result == 1)
                            {
                                AuditTrailVO auditTrailVO=new AuditTrailVO();
                                auditTrailVO.setActionExecutorId(String.valueOf(toid));
                                auditTrailVO.setActionExecutorName("Customer");
                                //insert the status change action entry for authfailed to details table
                                int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_AUTHORISTION_CANCLLED, ActionEntry.STATUS_AUTHORISTION_CANCLLED, commResponseVO, null,auditTrailVO,null);
                                if (num == 1)
                                {

                                    vSuccessTotalCount++;
                                    errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction Auth Cancelled" + "\r\n");
                                    if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED)))
                                        pzReconcilationResponce.setResupdatedStatus(String.valueOf(PZTransactionStatus.AUTH_CANCELLED));
                                    else
                                        pzReconcilationResponce.setResupdatedStatus(String.valueOf(PZTransactionStatus.AUTH_CANCELLED));
                                    pzReconcilationResponce.setResponseDesceiption("Transaction Auth Cancelled");
                                }

                            }

                        }

                    }

                    ////Processing Failed Transactions
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.FAILED)))
                    {

                        if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_STARTED)))
                        {


                            //update the status, PaymentOrderNumber and remark column for this transaction
                            if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED)))
                                query = "update transaction_common set paymentid=?, status='failed', remark='Bank Connectivity Issue' where trackingid=? ";
                            else
                                query = "update transaction_common set paymentid=?, status='failed', remark='Bank Connectivity Issue' where trackingid=? ";
                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, vPaymentOrderId);
                            ps.setString(2, vTrackingId);
                            int result = ps.executeUpdate();

                            if (result == 1)
                            {
                                AuditTrailVO auditTrailVO=new AuditTrailVO();
                                auditTrailVO.setActionExecutorId(String.valueOf(toid));
                                auditTrailVO.setActionExecutorName("Customer");
                                //insert the status change action entry for authfailed to details table
                                int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, commResponseVO, null,auditTrailVO,null);
                                if (num == 1)
                                {

                                    vSuccessTotalCount++;
                                    errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction Failed" + "\r\n");
                                    if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED)))
                                        pzReconcilationResponce.setResupdatedStatus(String.valueOf(PZTransactionStatus.FAILED));
                                    else
                                        pzReconcilationResponce.setResupdatedStatus(String.valueOf(PZTransactionStatus.FAILED));
                                    pzReconcilationResponce.setResponseDesceiption("Transaction Failed");
                                }

                            }

                        }


                    }

                    ////Processing Chargeback Transactions
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.CHARGEBACK)))
                    {
                        errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction Must be in Chargeback Status" + "\r\n");
                    }
                    //Processing Refund
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.REVERSED)))
                    {
                        if (!rs.getString("status").equals("reversed"))
                        {
                            errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction Must be in Reverse Status" + "\r\n");
                        }
                    }

                }
                else
                {

                    vErrRecords.add(trackingId);
                    errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                    pzReconcilationResponce.setResponseDesceiption("Transaction NOT Found");
                }

              vReconcileList.add(pzReconcilationResponce);
            }
            catch (Exception e)
            {
                log.error("ERROR", e);
                log.error("[CommonSettlementCron] : Error while reconciling  the transaction for Tracking ID = " + trackingId + ". Exception =" + e);

                transactionLogger.error("ERROR", e);
                transactionLogger.error("[CommonSettlementCron] : Error while reconciling  the transaction for Tracking ID = " + trackingId + ". Exception =" + e);
                vErrRecords.add(trackingId);
            }
        }
        String vMailSend = "";
        try
        {   if(!errString.toString().equals(""))
            {
                transaction.sendReconcilationMail(errString, toAddress);
            }
        }
        catch (SystemError systemError)
        {
            log.error("[CommonSettlementCron] : Error while sending  the settled transaction mail");
            transactionLogger.error("[CommonSettlementCron] : Error while sending  the settled transaction mail");
            vMailSend = "Mail Sending to admin is failed.";
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return vReconcileList;
    }


    /*public static void main(String args[])
    {
        MyMonederoPaymentProcess qwipiSettlementCron1 = new MyMonederoPaymentProcess();
        PZSettlementFile reqt=new PZSettlementFile();
        String error = null;
        List<PZSettlementRecord> vList = new ArrayList<PZSettlementRecord>();
        try
        {
            vList = qwipiSettlementCron1.readSettlementFile(reqt);
        }
        catch (SystemError systemError)
        {
            systemError.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for(PZSettlementRecord vSettleTransVO : vList)
        {
            System.out.println(vSettleTransVO.getSDateTime()+"    "+vSettleTransVO.getStatusDetail()+"=="+vSettleTransVO.getAmount()+"==="+vSettleTransVO.getMerchantTXNid()+"==="+vSettleTransVO.getPaymentid());

        }
    }*/


}
