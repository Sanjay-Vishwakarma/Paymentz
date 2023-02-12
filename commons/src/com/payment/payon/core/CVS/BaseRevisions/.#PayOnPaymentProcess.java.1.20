package com.payment.payon.core;

import com.directi.pg.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.payon.core.message.Identification;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZInquiryRequest;
import com.payment.request.PZRefundRequest;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 6/22/13
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(PayOnPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayOnPaymentProcess.class.getName());

    @Override
    public void setTransactionVOParamsExtension(CommRequestVO requestVO, Map transactionRequestPArams) throws PZDBViolationException
    {
        PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;
        payOnRequestVO.setIdentification(generateIdentification());
        setMerchantDetails(String.valueOf(transactionRequestPArams.get("accountid")), payOnRequestVO);

    }

    public void setTransactionVOParamsExtension(CommRequestVO requestVO, CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;
        payOnRequestVO.setIdentification(generateIdentification());
        setMerchantDetails(commonValidatorVO.getMerchantDetailsVO().getAccountId(), payOnRequestVO);

    }
    @Override
    public void setTransactionVOExtension(CommRequestVO commRequestVO,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        PayOnRequestVO payOnRequestVO = (PayOnRequestVO) commRequestVO;
        payOnRequestVO.setIdentification(generateIdentification());
        PayOnCardDetailsVO payOnCardDetailsVO=  payOnRequestVO.getPayOnCardDetailsVO();
        payOnCardDetailsVO.setCardHolderFirstName(commAddressDetailsVO.getFirstname());
        payOnCardDetailsVO.setCardHolderSurname(commAddressDetailsVO.getLastname());
        setMerchantDetails(commonValidatorVO.getMerchantDetailsVO().getAccountId(), payOnRequestVO);

    }


    @Override
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws  PZDBViolationException
    {
        PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;
        payOnRequestVO.setIdentification(generateIdentification());
        PayOnTransactionDetailsVO payOnTransactionDetailsVO = payOnRequestVO.getPayOnTransactionDetailsVO();
        setMerchantDetails(String.valueOf(captureRequest.getAccountId()), payOnRequestVO);
        String trackingId = payOnTransactionDetailsVO.getOrderId();
        String detailId = payOnTransactionDetailsVO.getDetailId();
        setTransactionDetails(trackingId,detailId,"authsuccessful", payOnRequestVO);
        setCardDetails(trackingId,payOnRequestVO);

    }


    private void setMerchantDetails(String accountid, PayOnRequestVO payOnRequestVO) throws PZDBViolationException
    {
        Connection conn = null;
        PayOnMerchantAccountVO payOnMerchantAccountVO = payOnRequestVO.getPayOnMerchantAccountVO();
        PayOnVBVDetailsVO payOnVBVDetailsVO = payOnRequestVO.getPayOnVBVDetailsVO();

        try
        {
            conn = Database.getConnection();
            String getQuery = "select * from gateway_accounts_payon where accountid = ?";
            PreparedStatement ps = conn.prepareStatement(getQuery);
            ps.setString(1, accountid);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                log.debug("Connector----------->"+rs.getString("connector"));
                payOnMerchantAccountVO.setShortID(rs.getString("shortid"));
                if(rs.getString("connector").equalsIgnoreCase("worldline"))
                {
                    payOnMerchantAccountVO.setType(rs.getString("type"));
                    payOnMerchantAccountVO.setMerchantname(rs.getString("merchantname"));
                    payOnMerchantAccountVO.setUsername(rs.getString("username"));
                    payOnMerchantAccountVO.setKey(rs.getString("key"));
                    payOnMerchantAccountVO.setTerminalID(rs.getString("terminalid"));
                    /*payOnMerchantAccountVO.setPayOnUsername(rs.getString("apiUserName"));
                    payOnMerchantAccountVO.setPayOnPassword(rs.getString("apiPassword"));
                    payOnMerchantAccountVO.setShortID(rs.getString("shortid"));*/

                }
                else if(rs.getString("connector").equalsIgnoreCase("ems"))
                {
                    payOnMerchantAccountVO.setType(rs.getString("type"));
                    payOnMerchantAccountVO.setUsername(rs.getString("username"));
                    payOnMerchantAccountVO.setKey(rs.getString("key"));
                    payOnMerchantAccountVO.setPayOnPassword(rs.getString("password"));
                    payOnMerchantAccountVO.setUploadUser(rs.getString("uploaduser"));
                    payOnMerchantAccountVO.setMerchantname(rs.getString("merchantname"));
                    payOnMerchantAccountVO.setIBAN(rs.getString("iban"));

                    payOnVBVDetailsVO.setEci(rs.getString("eci"));
                    payOnVBVDetailsVO.setVerification(rs.getString("verification"));
                    payOnVBVDetailsVO.setXid(rs.getString("xid"));
                }
                else if(rs.getString("connector").equalsIgnoreCase("concardis"))
                {
                    payOnMerchantAccountVO.setTerminalID(rs.getString("terminalid"));
                }
                else if(rs.getString("connector").equalsIgnoreCase("catella"))
                {
                    payOnMerchantAccountVO.setKey(rs.getString("key"));
                    payOnMerchantAccountVO.setPayOnPassword(rs.getString("password"));
                    payOnMerchantAccountVO.setMerchantname(rs.getString("merchantname"));
                    payOnMerchantAccountVO.setType(rs.getString("type"));
                    payOnVBVDetailsVO.setEci(rs.getString("eci"));
                    payOnVBVDetailsVO.setVerification(rs.getString("verification"));
                    payOnVBVDetailsVO.setXid(rs.getString("xid"));
                }
                payOnMerchantAccountVO.setConnector(rs.getString("connector"));
            }
        }

        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PayOnPaymentProcess.class.getName(),"setMerchantDetails()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(PayOnPaymentProcess.class.getName(), "setMerchantDetails()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }


    private void setTransactionDetails(String trackingId, String detailId, String status, PayOnRequestVO payOnRequestVO)
    {

        Connection conn = null;
        PayOnTransactionDetailsVO payOnTransactionDetailsVO = payOnRequestVO.getPayOnTransactionDetailsVO();
        try
        {

            conn = Database.getConnection();
            String transaction_details = "select * from transaction_payon_details where trackingid=? and detailid = ? and status = ?";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setInt(1, Integer.parseInt(trackingId));
            transDetailsprepstmnt.setInt(2, Integer.parseInt(detailId));
            transDetailsprepstmnt.setString(3, status);
            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
            if (rsTransDetails.next())
            {
                payOnTransactionDetailsVO.setConnectorTxID1(rsTransDetails.getString("connectortxid1"));
                payOnTransactionDetailsVO.setConnectorTxID2(rsTransDetails.getString("connectortxid2"));
                payOnTransactionDetailsVO.setConnectorTxID3(rsTransDetails.getString("connectortxid3").replaceAll("\\|", ""));
                payOnTransactionDetailsVO.setConnectorTxID4(rsTransDetails.getString("connectortxid3"));

                payOnTransactionDetailsVO.setRequestTimestamp(rsTransDetails.getString("requesttimestamp"));
            }
            log.debug("Connector Values while fetching the data from table---> 1. " +rsTransDetails.getString("connectortxid1"));
            log.debug("Connector Values while fetching the data from table---> 2. " +rsTransDetails.getString("connectortxid2"));
            log.debug("Connector Values while fetching the data from table---> 3. " +rsTransDetails.getString("connectortxid3"));
        }
        catch (Exception e)
        {
           /* e.printStackTrace();*/
            log.error("Sql exception while getting details from transaction_payon-details:: ",e);
            transactionLogger.error("Sql exception while getting details from transaction_payon-details:: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    //for preauthorisation connectorid_2 saved in  connectorid_1

    public void  updatePreAuthConnector(PayOnTransactionDetailsVO payOnTransactionDetailsVO)
    {
        Connection con = null;
        try
        {
            con=Database.getConnection();
            String update="update `transaction_payon_details` set connectortxid1 =? where trackingid=?";
            PreparedStatement ps1 = con.prepareStatement(update.toString());
            ps1.setString(1,payOnTransactionDetailsVO.getConnectorTxID1());

            log.debug(" update query::"+update);
            int rss=ps1.executeUpdate();


        }
        catch (SystemError systemError)
        {
            log.error(" System error for updateBankRollingReserve::",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL exception for updateBankRollingReserve::",e);
        }
        finally {
            Database.closeConnection(con);
        }

    }


    private void setCardDetails(String trackingId, PayOnRequestVO payOnRequestVO)
    {

        Connection conn = null;
        PayOnTransactionDetailsVO payOnTransactionDetailsVO = payOnRequestVO.getPayOnTransactionDetailsVO();
        PayOnCardDetailsVO payOnCardDetailsVO = payOnRequestVO.getPayOnCardDetailsVO();
        try
        {
            conn = Database.getConnection();
            String transaction_details = "select * from transaction_common where trackingid=? ";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setInt(1, Integer.parseInt(trackingId));

            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
            if (rsTransDetails.next())
            {
                payOnCardDetailsVO.setCardHolderFirstName(rsTransDetails.getString("firstname"));
                payOnCardDetailsVO.setCardHolderSurname(rsTransDetails.getString("lastname"));

                payOnCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(rsTransDetails.getString("ccnum")));
                String EXP= PzEncryptor.decryptExpiryDate(rsTransDetails.getString("expdate"));

                String dateArr[]=EXP.split("/");

                payOnCardDetailsVO.setExpMonth(dateArr[0]);
                payOnCardDetailsVO.setExpYear(dateArr[1]);

                String cardType= rsTransDetails.getString("cardtype");
                if(cardType.equals("MC"))
                {
                    cardType="MASTER";
                }
                payOnCardDetailsVO.setCardType(cardType);

            }
        }
        catch (Exception e)
        {
            /* e.printStackTrace();*/
            log.error("Sql exception while getting details from transaction_payon-details:: ",e);
            transactionLogger.error("Sql exception while getting details from transaction_payon-details:: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;
        PayOnTransactionDetailsVO payOnTransactionDetailsVO = payOnRequestVO.getPayOnTransactionDetailsVO();
        payOnRequestVO.setIdentification(generateIdentification());
        setMerchantDetails(String.valueOf(refundRequest.getAccountId()), payOnRequestVO);
        String trackingId = payOnTransactionDetailsVO.getOrderId();
        String detailId = payOnTransactionDetailsVO.getDetailId();
        setTransactionDetails(trackingId,detailId,"capturesuccess", payOnRequestVO);
        setCardDetails(trackingId,payOnRequestVO);
    }

    @Override
    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest pzInquiryRequest)
    {


    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws  PZDBViolationException
    {
        PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;
        PayOnTransactionDetailsVO payOnTransactionDetailsVO = payOnRequestVO.getPayOnTransactionDetailsVO();
        payOnRequestVO.setIdentification(generateIdentification());
        setMerchantDetails(String.valueOf(cancelRequest.getAccountId()), payOnRequestVO);
        String trackingId = payOnTransactionDetailsVO.getOrderId();
        String detailId = payOnTransactionDetailsVO.getDetailId();
        setTransactionDetails(trackingId,detailId,"authsuccessful", payOnRequestVO);
        setCardDetails(trackingId,payOnRequestVO);

    }

    @Override
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {
        log.debug("Entering ActionEntry for PayOn Details");
        transactionLogger.debug("Entering ActionEntry for PayOn Details");

        String shortid = "";
        String uuid = "";
        String requesttimestamp = "";
        String responsetimestamp = "";
        String paypipeprocessingtime = "";
        String connectortime = "";
        String returncode = "";
        String returnmessage = "";
        String connectortxid1 = "";
        String connectortxid2 = "";
        String connectortxid3 = "";
        String connectorcode = "";
        String connectorumessage = "";
        int results=0;

        if (responseVO != null)
        {
            PayOnResponseVO payOnResponseVO = (PayOnResponseVO) responseVO;
            requesttimestamp = payOnResponseVO.getRequestTimestamp();
            responsetimestamp = payOnResponseVO.getResponseTimestamp();
            paypipeprocessingtime = payOnResponseVO.getPayPipeProcessingTime();
            connectortime = payOnResponseVO.getConnectorTime();
            returncode = payOnResponseVO.getReturnCode();
            returnmessage = payOnResponseVO.getReturnMesaage();
            connectortxid1 = payOnResponseVO.getConnectorTxID1();
            connectortxid2 = payOnResponseVO.getConnectorTxID2();
            connectortxid3 = payOnResponseVO.getConnectorTxID3();
            log.debug("Connector values while inserting into table------> 1."+connectortxid1+ " 2." +connectortxid2+ " 3."+connectortxid3);
            connectorcode = payOnResponseVO.getConnectorCode();
            connectorumessage = payOnResponseVO.getConnectorMessage();
        }
        Connection cn = null;
        try
        {
            cn = Database.getConnection();
            String sql = "insert into transaction_payon_details(detailid,trackingid,amount,status,shortid ,uuid,requesttimestamp ,responsetimestamp,paypipeprocessingtime ,connectortime ,returncode ,returnmessage ,connectortxid1 ,connectortxid2 ,connectortxid3 ,connectorcode ,connectorumessage ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setInt(1, newDetailId);
            pstmt.setString(2, trackingId);
            pstmt.setString(3, amount);
            pstmt.setString(4, status);
            pstmt.setString(5, shortid);
            pstmt.setString(6, uuid);
            pstmt.setString(7, requesttimestamp.trim());
            pstmt.setString(8, responsetimestamp);
            pstmt.setString(9, paypipeprocessingtime);
            pstmt.setString(10, connectortime);
            pstmt.setString(11, returncode);
            pstmt.setString(12, returnmessage);
            pstmt.setString(13, connectortxid1);
            pstmt.setString(14, connectortxid2);
            pstmt.setString(15, connectortxid3);
            pstmt.setString(16, connectorcode);
            pstmt.setString(17, connectorumessage);

            results = pstmt.executeUpdate();
        }
        catch (SQLException se)
        {
            log.error("SQLException in PayOnPaymentProcess---",se);
            PZExceptionHandler.raiseDBViolationException(PayOnPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());

        }
        catch (SystemError systemError)
        {
            log.error("systemError in PayOnPaymentProcess---",systemError);
            PZExceptionHandler.raiseDBViolationException(PayOnPaymentProcess.class.getName(),"actionEntryExtension()",null,"common","Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }


    private Identification generateIdentification()
    {
        Identification identification = new Identification();
        //identification.setShortID(RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 4));
        identification.setUUID(RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(24) + 8));

        return identification;
    }
}