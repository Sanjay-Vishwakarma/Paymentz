package com.payment;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.SwiffpayPaymentGateway;
import com.directi.pg.core.valueObjects.SwiffpayRequestVO;
import com.directi.pg.core.valueObjects.SwiffpayResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.request.*;
import com.payment.response.PZInquiryResponse;
import com.payment.response.PZResponseStatus;
import com.payment.response.PZSettlementRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 6/25/13
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class SwiffPayPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(SwiffPayPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SwiffPayPaymentProcess.class.getName());

    private Hashtable getTransactionDetails(String trackingid, String status) throws PZDBViolationException
    {
        Connection connection=null;
        Hashtable transDetail=new Hashtable();
        String responsetransactionid=null;
        String historyid=null;
        String referencenumber=null;
        String merchantordernumber=null;
        String batchnumber=null;
        try
        {
            connection= Database.getConnection();
            String qry="select D.responsetransactionid,S.historyid,S.referencenumber,S.merchantordernumber,S.batchnumber from transaction_common_details as D , transaction_swiffpay_details as S where D.detailid=? and D.status=? and D.detailid=S.detailid";
            PreparedStatement preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,trackingid);
            preparedStatement.setString(2,status);
            ResultSet resultSet= preparedStatement.executeQuery();
            if(resultSet.next())
            {
                responsetransactionid=resultSet.getString("responsetransactionid");
                historyid=resultSet.getString("historyid");
                referencenumber=resultSet.getString("referencenumber");
                merchantordernumber=resultSet.getString("merchantordernumber");
                batchnumber=resultSet.getString("batchnumber");

                if(responsetransactionid!=null || !responsetransactionid.equals(""))
                {
                    transDetail.put("responsetransactionid",responsetransactionid);
                }
                if(historyid!=null || !historyid.equals(""))
                {
                    transDetail.put("historyid",historyid);
                }
                if(referencenumber!=null || !referencenumber.equals(""))
                {
                    transDetail.put("referencenumber",referencenumber);
                }
                if(merchantordernumber!=null || !merchantordernumber.equals(""))
                {
                    transDetail.put("merchantordernumber",merchantordernumber);
                }
            }
        }
        catch (SystemError systemError)
        {
           PZExceptionHandler.raiseDBViolationException(SwiffPayPaymentProcess.class.getName(),"getTransactionDetails()",null,"common","DB Exception",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(SwiffPayPaymentProcess.class.getName(),"getTransactionDetails()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return transDetail;
    }

    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        SwiffpayRequestVO swiffpayRequestVO=(SwiffpayRequestVO) requestVO;
        String trackingid=swiffpayRequestVO.getTransDetailsVO().getDetailId();
        Hashtable transdetail= getTransactionDetails(trackingid,"capturesuccess");

        swiffpayRequestVO.setHistoryid((String) transdetail.get("historyid"));

    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO,PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        SwiffpayRequestVO swiffpayRequestVO=(SwiffpayRequestVO) requestVO;
        String trackingid=swiffpayRequestVO.getTransDetailsVO().getDetailId();
        Hashtable transdetail= getTransactionDetails(trackingid,"capturesuccess");

        swiffpayRequestVO.setHistoryid((String) transdetail.get("historyid"));
    }

    @Override
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        SwiffpayRequestVO swiffpayRequestVO=(SwiffpayRequestVO) requestVO;
        String trackingid=swiffpayRequestVO.getTransDetailsVO().getDetailId();
        Hashtable transdetail= getTransactionDetails(trackingid,"authsuccessful");
        swiffpayRequestVO.setMerchantordernumber(trackingid);
        swiffpayRequestVO.setHistoryid((String) transdetail.get("historyid"));
    }

    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        log.debug("Inside swiffpay Inquiry");
        transactionLogger.debug("Inside swiffpay Inquiry");
        PZInquiryResponse inquiryResponse = new PZInquiryResponse();
        Connection conn=null;
        try
        {
            Integer trackingId = pzInquiryRequest.getTrackingId();
            Integer accountId = pzInquiryRequest.getAccountId();
            conn = Database.getConnection();

            SwiffpayRequestVO swiffPayRequestVO= new SwiffpayRequestVO();
            SwiffpayResponseVO swiffPayResponseVO = null;

            String transaction = "select trackingid,paymentid,accountid,amount,currency,status,refundamount,ipaddress from transaction_common where trackingid=?";
            PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
            transPreparedStatement.setString(1, String.valueOf(trackingId));
            ResultSet rstransaction = transPreparedStatement.executeQuery();
            if (rstransaction.next())
            {
                String trackingIdDB = rstransaction.getString("trackingid");
                String accountIdDB = rstransaction.getString("accountid");
                String ipaddress=rstransaction.getString("ipaddress");
                log.debug("Setting Details====");
                transactionLogger.debug("Setting Details====");
                swiffPayRequestVO.setMerchantordernumber(String.valueOf(trackingId));
                swiffPayRequestVO.setIpaddress(ipaddress);
                SwiffpayPaymentGateway pg= new SwiffpayPaymentGateway(accountIdDB);
                swiffPayResponseVO= (SwiffpayResponseVO) pg.processQuery(trackingIdDB,swiffPayRequestVO);

                if(swiffPayResponseVO!=null)
                {
                    inquiryResponse.setStatus(PZResponseStatus.SUCCESS);
                    inquiryResponse.setTrackingId(trackingIdDB);
                    inquiryResponse.setResponseTransactionId(swiffPayResponseVO.getTransactionId());
                    inquiryResponse.setResponseTransactionStatus(swiffPayResponseVO.getStatus());
                    inquiryResponse.setResponseCode(swiffPayResponseVO.getAuthorizationCode());
                    inquiryResponse.setResponseDescription(swiffPayResponseVO.getTransactionType());
                    inquiryResponse.setResponseTime(swiffPayResponseVO.getResponseTime());
                }
                else
                {
                    log.debug("Error while Inquiry Transaction. TrackingId:"+trackingId);
                    transactionLogger.debug("Error while Inquiry Transaction. TrackingId:"+trackingId);
                    inquiryResponse.setStatus(PZResponseStatus.FAILED);
                    inquiryResponse.setResponseDesceiption("Error while Inquiry Transaction. TrackingId:"+trackingId);
                    return inquiryResponse;
                }
            }
            else
            {
                inquiryResponse.setStatus(PZResponseStatus.FAILED);
                inquiryResponse.setResponseDesceiption("Transaction not found");
            }
        }

        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalException while inquiring via SwiffPay",e);
            transactionLogger.error("PZTechnicalException while inquiring via SwiffPay",e);

            PZExceptionHandler.handleTechicalCVEException(e,null,"inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while inquiring via SwiffPay",e);
            transactionLogger.error("PZConstraintViolationException while inquiring via SwiffPay",e);

            PZExceptionHandler.handleCVEException(e,null,"inquiry");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQLException while inquiring via SwiffPay",e);
            transactionLogger.error("SQLException while inquiring via SwiffPay",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(SwiffPayPaymentProcess.class.getName(),"Inquiry()",null,"COmmon","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"inquiry");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while inquiring via SwiffPay",systemError);
            transactionLogger.error("SystemError while inquiring via SwiffPay",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(SwiffPayPaymentProcess.class.getName(),"Inquiry()",null,"COmmon","DB Exception",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"inquiry");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error fetching transaction status" + systemError.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return inquiryResponse;
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        log.debug("enter in swiffpay actionentery extension");
        transactionLogger.debug("enter in swiffpay actionentery extension");
        int i=0;
        Connection conn= null;
        SwiffpayResponseVO swiffpayResponseVO= (SwiffpayResponseVO) responseVO;
        String historyid="";
        String referancenumber="";
        String merchantorderid="";
        if(responseVO!=null)
        {
           historyid=swiffpayResponseVO.getHistoryid();
           referancenumber=swiffpayResponseVO.getReferenceNumber();
           merchantorderid=swiffpayResponseVO.getMerchantOrderId();
        }
        try
        {
            conn=Database.getConnection();
            String sql="insert into transaction_swiffpay_details(detailid,historyid,referencenumber,merchantordernumber) values (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,newDetailId+"");
            pstmt.setString(2,historyid+"");
            pstmt.setString(3,referancenumber+"");
            pstmt.setString(4,merchantorderid+"");
            i= pstmt.executeUpdate();

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(SwiffPayPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(SwiffPayPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return i;
    }

    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        log.debug("entering in SwiffPay");
        transactionLogger.debug("entering in SwiffPay");
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
                if(((String)((HSSFCell) row.getCell((short)6)).getStringCellValue()).equals("Sale"))
                {
                    if(((String)((HSSFCell) row.getCell((short)7)).getStringCellValue()).equals("Approved"))
                    {

                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));

                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));
                    }
                }
                else if(((String)((HSSFCell) row.getCell((short)6)).getStringCellValue()).equals("Credit Back"))
                {
                    if(((String)((HSSFCell) row.getCell((short)7)).getStringCellValue()).equals("Approved"))
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.MARKED_FOR_REVERSAL));
                    }
                }
                loadTransResponse.setSDateTime(((HSSFCell) row.getCell((short)2)).getStringCellValue()+"");
                loadTransResponse.setAmount((float)((HSSFCell) row.getCell((short)3)).getNumericCellValue()+"");
                loadTransResponse.setMerchantTXNid((int)((HSSFCell) row.getCell((short)35)).getNumericCellValue()+"");
                loadTransResponse.setPaymentid((int)((HSSFCell) row.getCell((short)36)).getNumericCellValue()+"");

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


   /*public static void main(String args[])
    {
        SwiffPayPaymentProcess qwipiSettlementCron1 = new SwiffPayPaymentProcess();
        PZSettlementFile reqt=new PZSettlementFile();
        reqt.setFilepath("D:\\Integration\\SwiffPay\\Format.xls");
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
