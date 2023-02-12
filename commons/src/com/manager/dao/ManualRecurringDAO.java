package com.manager.dao;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.directi.pg.core.valueObjects.QwipiResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by admin on 10/28/2015.
 */
public class ManualRecurringDAO
{
    private static Logger log = new Logger(PartnerDAO.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PartnerDAO.class.getName());
    private static Functions functions = new Functions();


    public CommonValidatorVO getQwipiManualRebillDetails(String trackingId) throws PZDBViolationException
    {
        Connection con = null;
        CommonValidatorVO commonValidatorVO = null;
        GenericAddressDetailsVO genericAddressDetailsVO = null;
        GenericCardDetailsVO genericCardDetailsVO = null;
        GenericTransDetailsVO genericTransDetailsVO = null;
        MerchantDetailsVO merchantDetailsVO = null;
        PreparedStatement p=null;
        ResultSet rs=null;

        try
        {
            con = Database.getRDBConnection();
            //String query = "SELECT tq.*,tqd.qwipiPaymentOrderNumber FROM transaction_qwipi AS tq,transaction_qwipi_details AS tqd WHERE tq.trackingid=? AND tq.trackingid=tqd.parentid ";
            String query = "SELECT tq.*,tqd.qwipiPaymentOrderNumber, bd.first_six, bd.bin_brand, bd.bin_sub_brand, bd.bin_card_type, bd.bin_card_category,bd.bin_usage_type, bd.bin_country_code_A3 FROM transaction_qwipi AS tq,transaction_qwipi_details AS tqd, bin_details AS bd WHERE tq.trackingid=? AND tq.trackingid=tqd.parentid AND tq.trackingid = bd.icicitransid";

            p = con.prepareStatement(query);
            p.setString(1, trackingId);
            rs = p.executeQuery();
            if(rs.next())
            {
                commonValidatorVO = new CommonValidatorVO();
                genericAddressDetailsVO = new GenericAddressDetailsVO();
                genericCardDetailsVO = new GenericCardDetailsVO();
                genericTransDetailsVO = new GenericTransDetailsVO();
                merchantDetailsVO = new MerchantDetailsVO();

                merchantDetailsVO.setMemberId(rs.getString("toid"));
                genericTransDetailsVO.setTotype(rs.getString("totype"));
                genericTransDetailsVO.setFromid(rs.getString("fromid"));
                genericTransDetailsVO.setFromtype(rs.getString("fromtype"));
                genericTransDetailsVO.setAmount(rs.getString("amount"));
                genericTransDetailsVO.setCurrency(rs.getString("currency"));
                genericTransDetailsVO.setRedirectUrl(rs.getString("redirecturl"));
                genericAddressDetailsVO.setFirstname(rs.getString("firstname"));
                genericAddressDetailsVO.setLastname(rs.getString("lastname"));
                genericCardDetailsVO.setCardHolderName(rs.getString("name"));
                genericCardDetailsVO.setExpMonth(rs.getString("expdate"));
                genericCardDetailsVO.setCardNum(rs.getString("ccnum"));
                genericAddressDetailsVO.setEmail(rs.getString("emailaddr"));
                genericAddressDetailsVO.setIp(rs.getString("ipaddress"));
                genericAddressDetailsVO.setCountry(rs.getString("country"));
                genericAddressDetailsVO.setCity(rs.getString("city"));
                genericAddressDetailsVO.setState(rs.getString("state"));
                genericAddressDetailsVO.setBirthdate(rs.getString("birthdate"));
                genericAddressDetailsVO.setZipCode(rs.getString("zip"));
                genericAddressDetailsVO.setTelnocc(rs.getString("telnocc"));
                genericAddressDetailsVO.setPhone(rs.getString("telno"));
                genericAddressDetailsVO.setStreet(rs.getString("street"));
                genericCardDetailsVO.setCardType(rs.getString("cardtype"));
                merchantDetailsVO.setAccountId(rs.getString("accountid"));
                commonValidatorVO.setPaymentType(rs.getString("paymodeid"));
                commonValidatorVO.setCardType(rs.getString("cardtypeid"));
                genericAddressDetailsVO.setLanguage(rs.getString("language"));
                commonValidatorVO.setTerminalId(rs.getString("terminalid"));
                genericCardDetailsVO.setBin_brand(rs.getString("bin_brand"));
                genericCardDetailsVO.setBin_sub_brand(rs.getString("bin_sub_brand"));
                genericCardDetailsVO.setBin_card_type(rs.getString("bin_card_type"));
                genericCardDetailsVO.setBin_card_category(rs.getString("bin_card_category"));
                genericCardDetailsVO.setBin_usage_type(rs.getString("bin_usage_type"));
                genericCardDetailsVO.setCountry_code_A3(rs.getString("bin_country_code_A3"));

                genericTransDetailsVO.setResponseOrderNumber(rs.getString("qwipiPaymentOrderNumber"));

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            }
            log.debug("getQwipiManualRebillDetails query---"+query);
        }
        catch (SystemError systemError)
        {
            log.error("getQwipiManualRebillDetails", systemError);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "getQwipiManualRebillDetails()", null, "common", "System Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException se)
        {
            log.error("getQwipiManualRebillDetails",se);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "getQwipiManualRebillDetails()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY,null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(con);
        }
        return commonValidatorVO;
    }

    public int insertNewTransactionQwipi(CommonValidatorVO commonValidatorVO, String status, String httpheader, String description, String amount) throws PZDBViolationException
    {
        PaymentDAO paymentDao= new PaymentDAO();
        int trackingId = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query="insert into transaction_qwipi(toid,totype,fromid,fromtype,description,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,name,ccnum,expdate,cardtype,street,city,state,country,zip,telno,emailaddr,firstname,lastname,ipaddress,birthdate,telnocc,language,terminalid) values(?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //String query="insert into transaction_qwipi(name,ccnum,expdate,cardtype,street,city,state,country,zip,telno,emailaddr,firstname,lastname,ipaddress,status) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p=con.prepareStatement(query.toString());
            p.setString(1,commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2,commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3,commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4,commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5,description);
            p.setString(6,amount);
            p.setString(7,commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            p.setString(8,status);
            p.setString(9,commonValidatorVO.getMerchantDetailsVO().getAccountId());
            p.setString(10,commonValidatorVO.getPaymentType());
            p.setString(11,commonValidatorVO.getCardType());
            p.setString(12,commonValidatorVO.getTransDetailsVO().getCurrency());
            p.setString(13,httpheader);
            p.setString(14,commonValidatorVO.getCardDetailsVO().getCardHolderName());
            p.setString(15,commonValidatorVO.getCardDetailsVO().getCardNum());
            p.setString(16,commonValidatorVO.getCardDetailsVO().getExpMonth());
            p.setString(17,commonValidatorVO.getCardDetailsVO().getCardType());
            p.setString(18,commonValidatorVO.getAddressDetailsVO().getStreet());
            p.setString(19,commonValidatorVO.getAddressDetailsVO().getCity());
            p.setString(20,commonValidatorVO.getAddressDetailsVO().getState());
            p.setString(21,commonValidatorVO.getAddressDetailsVO().getCountry());
            p.setString(22,commonValidatorVO.getAddressDetailsVO().getZipCode());
            p.setString(23,commonValidatorVO.getAddressDetailsVO().getPhone());
            p.setString(24,commonValidatorVO.getAddressDetailsVO().getEmail());
            p.setString(25,commonValidatorVO.getAddressDetailsVO().getFirstname());
            p.setString(26,commonValidatorVO.getAddressDetailsVO().getLastname());
            p.setString(27,commonValidatorVO.getAddressDetailsVO().getIp());
            p.setString(28,commonValidatorVO.getAddressDetailsVO().getBirthdate());
            p.setString(29,commonValidatorVO.getAddressDetailsVO().getTelnocc());
            p.setString(30,commonValidatorVO.getAddressDetailsVO().getLanguage());
            p.setString(31,commonValidatorVO.getTerminalId());


            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();
                if(rs!=null)
                {
                    while(rs.next())
                    {
                        trackingId = rs.getInt(1);
                        commonValidatorVO.setTrackingid(String.valueOf(trackingId));
                    }
                }
            }
            paymentDao.updateBinDetailsForRecurring(commonValidatorVO);
            log.debug("insertNewTransactionQwipi----"+query+"trackingid"+trackingId);
        }
        catch (SystemError systemError)
        {
            log.error("insertNewTransactionQwipi",systemError);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "insertNewTransactionQwipi()", null, "common", "System Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("insertNewTransactionQwipi",e);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "insertNewTransactionQwipi()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;
    }
    public void updateAuthstartedQwipiRebill(QwipiResponseVO qwipiResponseVO, String status, String trackingid) throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try
        {
            connection = Database.getConnection();
            String updateRecord = "UPDATE transaction_qwipi SET status=?,captureamount=?,qwipiPaymentOrderNumber=?,remark=?,qwipiTransactionDateTime=? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1,status);
            preparedStatement.setString(2,qwipiResponseVO.getAmount());
            preparedStatement.setString(3,qwipiResponseVO.getPaymentOrderNo());
            preparedStatement.setString(4,qwipiResponseVO.getRemark());
            preparedStatement.setString(5,qwipiResponseVO.getDateTime());
            preparedStatement.setString(6,trackingid);
            preparedStatement.executeUpdate();

            log.debug("updateAuthstartedQwipiRebill"+updateRecord);
        }
        catch (SystemError systemError)
        {
            log.error("updateAuthstartedQwipiRebill",systemError);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "updateAuthstartedQwipiRebill()", null, "common", "System Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("updateAuthstartedQwipiRebill",e);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "updateAuthstartedQwipiRebill()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }

        finally
        {
            Database.closeConnection(connection);
        }
    }

    public CommonValidatorVO getTransDetailsForCommon(String trackingId) throws PZDBViolationException
    {
        log.error("inside it::::::::::");
        Connection connection = null;
        CommonValidatorVO commonValidatorVO = null;
        GenericAddressDetailsVO genericAddressDetailsVO = null;
        GenericCardDetailsVO genericCardDetailsVO = null;
        GenericTransDetailsVO genericTransDetailsVO = null;
        MerchantDetailsVO merchantDetailsVO = null;
        RecurringBillingVO recurringBillingVO =null;
        PreparedStatement p=null;
        ResultSet rs=null;

        try
        {
            connection = Database.getRDBConnection();
            //String query = "SELECT tc.*,rts.rbid FROM `transaction_common` AS tc,`recurring_transaction_subscription` AS rts WHERE tc.`trackingid`=? AND tc.trackingid=rts.`originatingTrackingid`";
            String query = "SELECT tc.*,rts.rbid, bd.bin_brand,bd.bin_sub_brand, bd.bin_card_type, bd.bin_card_category, bd.bin_usage_type, bd.bin_country_code_A3,bd.country_name,bd.issuing_bank FROM `transaction_common` AS tc,`recurring_transaction_subscription` AS rts, bin_details AS bd WHERE tc.`trackingid`=? AND tc.trackingid=rts.`originatingTrackingid` AND tc.trackingid=bd.icicitransid";
            p = connection.prepareStatement(query);
            p.setString(1, trackingId);
            rs = p.executeQuery();
            log.error("query:::::::"+p);
            if(rs.next())
            {
                commonValidatorVO = new CommonValidatorVO();
                genericAddressDetailsVO = new GenericAddressDetailsVO();
                genericCardDetailsVO = new GenericCardDetailsVO();
                genericTransDetailsVO = new GenericTransDetailsVO();
                merchantDetailsVO = new MerchantDetailsVO();
                recurringBillingVO = new RecurringBillingVO();


                merchantDetailsVO.setMemberId(rs.getString("toid"));

                genericTransDetailsVO.setPaymentid(rs.getString("paymentid"));

                genericTransDetailsVO.setTotype(rs.getString("totype"));
                genericTransDetailsVO.setFromid(rs.getString("fromid"));
                genericTransDetailsVO.setFromtype(rs.getString("fromtype"));
                genericTransDetailsVO.setAmount(rs.getString("amount"));
                genericTransDetailsVO.setCurrency(rs.getString("currency"));
                genericTransDetailsVO.setOrderId(rs.getString("description"));
                genericTransDetailsVO.setRedirectUrl(rs.getString("redirecturl"));
                genericTransDetailsVO.setTransactionmode(rs.getString("transaction_mode"));
                genericAddressDetailsVO.setFirstname(rs.getString("firstname"));
                genericAddressDetailsVO.setLastname(rs.getString("lastname"));
                genericCardDetailsVO.setCardHolderName(rs.getString("name"));
                genericCardDetailsVO.setExpMonth(rs.getString("expdate"));
                genericCardDetailsVO.setCardNum(rs.getString("ccnum"));
                genericAddressDetailsVO.setEmail(rs.getString("emailaddr"));
                genericAddressDetailsVO.setIp(rs.getString("ipaddress"));
                genericAddressDetailsVO.setCardHolderIpAddress(rs.getString("customerIp"));
                genericAddressDetailsVO.setCountry(rs.getString("country"));
                genericAddressDetailsVO.setCity(rs.getString("city"));
                genericAddressDetailsVO.setState(rs.getString("state"));
                //genericAddressDetailsVO.setBirthdate(rs.getString("birthdate"));
                genericAddressDetailsVO.setZipCode(rs.getString("zip"));
                genericAddressDetailsVO.setTelnocc(rs.getString("telnocc"));
                genericAddressDetailsVO.setPhone(rs.getString("telno"));
                genericAddressDetailsVO.setStreet(rs.getString("street"));
                genericCardDetailsVO.setCardType(rs.getString("cardtype"));
                merchantDetailsVO.setAccountId(rs.getString("accountid"));
                commonValidatorVO.setPaymentType(rs.getString("paymodeid"));
                commonValidatorVO.setCardType(rs.getString("cardtypeid"));
                merchantDetailsVO.setCurrency(rs.getString("currency"));
                merchantDetailsVO.setNotificationUrl(rs.getString("notificationUrl"));
                // genericAddressDetailsVO.setLanguage(rs.getString("language"));
                commonValidatorVO.setTerminalId(rs.getString("terminalid"));
                commonValidatorVO.setStatus(rs.getString("status"));

                genericCardDetailsVO.setBin_brand(rs.getString("bin_brand"));
                genericCardDetailsVO.setBin_sub_brand(rs.getString("bin_sub_brand"));
                genericCardDetailsVO.setBin_card_type(rs.getString("bin_card_type"));
                genericCardDetailsVO.setBin_card_category(rs.getString("bin_card_category"));
                genericCardDetailsVO.setBin_usage_type(rs.getString("bin_usage_type"));
                genericCardDetailsVO.setCountry_code_A3(rs.getString("bin_country_code_A3"));
                genericCardDetailsVO.setCountryName(rs.getString("country_name"));
                genericCardDetailsVO.setIssuingBank(rs.getString("issuing_bank"));
                recurringBillingVO.setRbid(rs.getString("rbid"));
                log.error("genericTransDetailsVO::::" + genericTransDetailsVO.getTransactionmode());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setRecurringBillingVO(recurringBillingVO);

            }
        }
        catch (SystemError systemError)
        {
            log.error("System error",systemError);
            PZExceptionHandler.raiseDBViolationException("ManualReacurringDAO.java", "getTransDetailsForCommon()", null, "Common", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQLException",e);
            PZExceptionHandler.raiseDBViolationException("ManualReacurringDAO.java","getTransDetailsForCommon()",null,"Common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return commonValidatorVO;

    }

    public int insertAuthStartedForCommon(CommonValidatorVO commonValidatorVO)throws PZDBViolationException
    {
        PaymentDAO paymentDao= new PaymentDAO();
        int trackingId = 0;
        Connection con= null;
        try
        {
            con= Database.getConnection();
            String query="INSERT INTO transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,STATUS,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,ccnum,firstname,lastname,NAME,street,country,city,state,zip,telno,telnocc,expdate,cardtype,emailaddr,terminalid,emiCount,notificationUrl) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement p=con.prepareStatement(query);
            p.setString(1,commonValidatorVO.getMerchantDetailsVO().getMemberId());
            p.setString(2,commonValidatorVO.getTransDetailsVO().getTotype());
            p.setString(3,commonValidatorVO.getTransDetailsVO().getFromid());
            p.setString(4,commonValidatorVO.getTransDetailsVO().getFromtype());
            p.setString(5,commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(6,commonValidatorVO.getTransDetailsVO().getOrderId());
            p.setString(7,commonValidatorVO.getTransDetailsVO().getAmount());
            p.setString(8,commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            p.setString(9,ActionEntry.STATUS_AUTHORISTION_STARTED);
            p.setString(10,commonValidatorVO.getMerchantDetailsVO().getAccountId());
            p.setString(11,commonValidatorVO.getPaymentType());
            p.setString(12,commonValidatorVO.getCardType());
            p.setString(13,commonValidatorVO.getMerchantDetailsVO().getCurrency());
            //p.setString(14,dtstamp);
            p.setString(14,commonValidatorVO.getTransDetailsVO().getHeader());
            p.setString(15,commonValidatorVO.getAddressDetailsVO().getIp());

            String ccnum = commonValidatorVO.getCardDetailsVO().getCardNum();

            if(commonValidatorVO.getCardDetailsVO().getCardNum() != null)
                p.setString(16, commonValidatorVO.getCardDetailsVO().getCardNum());

            p.setString(17,commonValidatorVO.getAddressDetailsVO().getFirstname());
            p.setString(18,commonValidatorVO.getAddressDetailsVO().getLastname());
            p.setString(19,commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname());
            p.setString(20,commonValidatorVO.getAddressDetailsVO().getStreet());
            p.setString(21,commonValidatorVO.getAddressDetailsVO().getCountry());
            p.setString(22,commonValidatorVO.getAddressDetailsVO().getCity());
            p.setString(23,commonValidatorVO.getAddressDetailsVO().getState());
            p.setString(24,commonValidatorVO.getAddressDetailsVO().getZipCode());
            p.setString(25,commonValidatorVO.getAddressDetailsVO().getPhone());
            p.setString(26,commonValidatorVO.getAddressDetailsVO().getTelnocc());

            String expdate = commonValidatorVO.getCardDetailsVO().getExpMonth();
            p.setString(27,expdate);

            p.setString(28,commonValidatorVO.getCardDetailsVO().getCardType());
            p.setString(29,commonValidatorVO.getAddressDetailsVO().getEmail());
            p.setString(30,commonValidatorVO.getTerminalId());
            p.setString(31,commonValidatorVO.getTransDetailsVO().getEmiCount());
            p.setString(32,commonValidatorVO.getMerchantDetailsVO().getNotificationUrl());
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {
                        trackingId = rs.getInt(1);
                        commonValidatorVO.setTrackingid(String.valueOf(trackingId));
                    }
                }
            }
            paymentDao.updateBinDetailsForRecurring(commonValidatorVO);
            log.debug("Insert Query trans_common---" + query + "---generated Trackingid---" + trackingId);

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ManualReacurringDAO.java","insertAuthStartedForRecurring()",null,"Common","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ManualReacurringDAO.java","insertAuthStartedForRecurring()",null,"Common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return trackingId;
    }

    public void checkAmountLimitForRebill(String amount,String memberId,String accountId) throws PZConstraintViolationException
    {
        log.debug("inside checkAmountLimitForRebill---");

        Connection connection = null;
        Float min_amount = 0.0f;
        Float max_amount = 0.0f;
        String error="";
        /*TerminalVO terminalVO = new TerminalVO();*/
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        PreparedStatement p=null;
        ResultSet rs=null;

        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT min_transaction_amount,max_transaction_amount FROM member_account_mapping WHERE memberid=? AND accountid=?";
            p = connection.prepareStatement(query);
            p.setString(1, memberId);
            p.setString(2,accountId);
            rs = p.executeQuery();
            log.debug("query for checkAmountLimitForRebill---"+p);
            if (rs.next())
            {
                min_amount=rs.getFloat("min_transaction_amount");
                max_amount=rs.getFloat("max_transaction_amount");
            }

            if (Float.parseFloat(amount) < min_amount)
            {
                error = "Amount less than minimum transaction amount-"+min_amount;
                errorCodeListVO = getErrorVO(ErrorName.SYS_LESS_AMT_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("ManualRecurringDAO","checkAmountLimitForRebill",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
            }

            if (Float.parseFloat(amount) > max_amount)
            {
                error = "Amount greater than maximum transaction amount-"+max_amount;
                errorCodeListVO = getErrorVO(ErrorName.SYS_GREATER_AMT_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("ManualRecurringDAO","checkAmountLimitForRebill",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
            }
        }
        catch(SystemError systemError)
        {
            log.debug("System error" + systemError);
        }
        catch (SQLException e)
        {
            log.debug("SQLException" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
    }

    public CommonValidatorVO getCashflowManualRebillData(CommonValidatorVO commonValidatorVO, String trackingId) throws PZDBViolationException
    {
        Connection con = null;
        GenericCardDetailsVO genericCardDetailsVO = null;
        GenericTransDetailsVO genericTransDetailsVO = null;
        GenericAddressDetailsVO genericAddressDetailsVO = null;
        MerchantDetailsVO merchantDetailsVO = null;
        ResultSet rs=null;
        PreparedStatement psmt=null;


        try
        {
            con=Database.getRDBConnection();
            String query = "SELECT * FROM transaction_common WHERE trackingid =?";
            psmt = con.prepareStatement(query);
            psmt.setString(1,trackingId);
            rs = psmt.executeQuery();
            if(rs.next())
            {
                genericCardDetailsVO = new GenericCardDetailsVO();
                genericTransDetailsVO = new GenericTransDetailsVO();
                merchantDetailsVO = new MerchantDetailsVO();
                genericAddressDetailsVO = new GenericAddressDetailsVO();

                genericCardDetailsVO.setCardHolderName(rs.getString("name"));
                genericCardDetailsVO.setCardNum((rs.getString("ccnum")));
                genericCardDetailsVO.setExpMonth(rs.getString("expdate"));
                //genericCardDetailsVO.setCardNum(Encryptor.decryptPAN(rs.getString("ccnum")));
                /*String EXP= Encryptor.decryptExpiryDate(rs.getString("expdate"));
                String dateArr[]=EXP.split("/");
                genericCardDetailsVO.setExpMonth(dateArr[0]);
                genericCardDetailsVO.setExpYear(dateArr[1].substring(2, 4));*/

                merchantDetailsVO.setMemberId(rs.getString("toid"));
                merchantDetailsVO.setCurrency(rs.getString("currency"));
                merchantDetailsVO.setAccountId(rs.getString("accountid"));

                genericTransDetailsVO.setOrderId(rs.getString("paymentid"));
                genericTransDetailsVO.setTotype(rs.getString("totype"));
                genericTransDetailsVO.setAmount(rs.getString("amount"));
                genericTransDetailsVO.setCurrency(rs.getString("currency"));
                genericTransDetailsVO.setRedirectUrl(rs.getString("redirecturl"));

                genericAddressDetailsVO.setFirstname(rs.getString("firstname"));
                genericAddressDetailsVO.setLastname(rs.getString("lastname"));
                genericAddressDetailsVO.setEmail(rs.getString("emailaddr"));
                genericAddressDetailsVO.setIp(rs.getString("ipaddress"));
                genericAddressDetailsVO.setCountry(rs.getString("country"));
                genericAddressDetailsVO.setCity(rs.getString("city"));
                genericAddressDetailsVO.setState(rs.getString("state"));
                genericAddressDetailsVO.setZipCode(rs.getString("zip"));
                genericAddressDetailsVO.setTelnocc(rs.getString("telnocc"));
                genericAddressDetailsVO.setPhone(rs.getString("telno"));
                genericAddressDetailsVO.setStreet(rs.getString("street"));

                commonValidatorVO.setPaymentType(rs.getString("paymodeid"));
                genericCardDetailsVO.setCardType(rs.getString("cardtypeid"));
                genericCardDetailsVO.setCardType(rs.getString("cardtype"));
                commonValidatorVO.setTerminalId(rs.getString("terminalid"));

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            }
            log.debug("getCashflowManualRebillData query---" + query);
            log.debug("getCashflowManualRebillData query--->>>" + psmt);
        }
        catch (SystemError systemError)
        {
            log.error("getCashflowManualRebillData", systemError);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "getCashflowManualRebillData()", null, "common", "System Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());;
        }
        catch (SQLException sql)
        {
            log.error("getCashflowManualRebillData",sql);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "getCashflowManualRebillData()", null, "common", "SQLException thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psmt);
            Database.closeConnection(con);
        }

        return commonValidatorVO;
    }

    public CommonValidatorVO getPayforasiaManualRebillData(CommonValidatorVO commonValidatorVO, String trackingId) throws PZDBViolationException
    {
        Connection con = null;
        GenericCardDetailsVO genericCardDetailsVO = null;
        GenericTransDetailsVO genericTransDetailsVO = null;
        GenericAddressDetailsVO genericAddressDetailsVO = null;
        MerchantDetailsVO merchantDetailsVO = null;
        PreparedStatement psmt=null;
        ResultSet rs=null;

        try
        {
            con=Database.getRDBConnection();
            //String query = "SELECT * FROM transaction_common WHERE trackingid =?";
            String query = "SELECT tc.*, bd.bin_brand, bd.bin_sub_brand, bd.bin_card_type, bd.bin_card_category, bd.bin_usage_type, bd.bin_country_code_A3 FROM transaction_common AS tc, bin_details AS bd WHERE tc.trackingid =? AND tc.trackingid=bd.icicitransid";
            psmt = con.prepareStatement(query);
            psmt.setString(1,trackingId);
            rs = psmt.executeQuery();
            if(rs.next())
            {
                genericCardDetailsVO = new GenericCardDetailsVO();
                genericTransDetailsVO = new GenericTransDetailsVO();
                merchantDetailsVO = new MerchantDetailsVO();
                genericAddressDetailsVO = new GenericAddressDetailsVO();

                genericCardDetailsVO.setCardHolderName(rs.getString("name"));
                genericCardDetailsVO.setCardNum((rs.getString("ccnum")));
                genericCardDetailsVO.setExpMonth(rs.getString("expdate"));

                genericCardDetailsVO.setCardNum(rs.getString("ccnum"));
                genericCardDetailsVO.setExpYear(rs.getString("expdate"));

                merchantDetailsVO.setMemberId(rs.getString("toid"));
                merchantDetailsVO.setCurrency(rs.getString("currency"));
                merchantDetailsVO.setAccountId(rs.getString("accountid"));

                genericTransDetailsVO.setOrderId(rs.getString("paymentid"));
                genericTransDetailsVO.setTotype(rs.getString("totype"));
                genericTransDetailsVO.setAmount(rs.getString("amount"));
                genericTransDetailsVO.setCurrency(rs.getString("currency"));
                genericTransDetailsVO.setRedirectUrl(rs.getString("redirecturl"));
                genericTransDetailsVO.setFromid(rs.getString("fromid"));

                genericAddressDetailsVO.setFirstname(rs.getString("firstname"));
                genericAddressDetailsVO.setLastname(rs.getString("lastname"));
                genericAddressDetailsVO.setEmail(rs.getString("emailaddr"));
                genericAddressDetailsVO.setIp(rs.getString("ipaddress"));
                genericAddressDetailsVO.setCountry(rs.getString("country"));
                genericAddressDetailsVO.setCity(rs.getString("city"));
                genericAddressDetailsVO.setState(rs.getString("state"));
                genericAddressDetailsVO.setZipCode(rs.getString("zip"));
                genericAddressDetailsVO.setTelnocc(rs.getString("telnocc"));
                genericAddressDetailsVO.setPhone(rs.getString("telno"));
                genericAddressDetailsVO.setStreet(rs.getString("street"));

                commonValidatorVO.setPaymentType(rs.getString("paymodeid"));
                commonValidatorVO.setCardType(rs.getString("cardtypeid"));

                genericCardDetailsVO.setBin_brand(rs.getString("bin_brand"));
                genericCardDetailsVO.setBin_sub_brand(rs.getString("bin_sub_brand"));
                genericCardDetailsVO.setBin_card_type(rs.getString("bin_card_type"));
                genericCardDetailsVO.setBin_card_category(rs.getString("bin_card_category"));
                genericCardDetailsVO.setBin_usage_type(rs.getString("bin_usage_type"));
                genericCardDetailsVO.setCountry_code_A3(rs.getString("bin_country_code_A3"));

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            }
            log.debug("getPayforasiaManualRebillData query---"+query);
            log.debug("getPayforasiaManualRebillData query--->>>"+psmt);
        }
        catch (SystemError systemError)
        {
            log.error("getPayforasiaManualRebillData", systemError);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "getPayforasiaManualRebillData()", null, "common", "System Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());;
        }
        catch (SQLException sql)
        {
            log.error("getPayforasiaManualRebillData",sql);
            PZExceptionHandler.raiseDBViolationException("ManualRecurringDAO.java", "getPayforasiaManualRebillData()",null, "common", "SQLException thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, sql.getMessage(), sql.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psmt);
            Database.closeConnection(con);
        }

        return commonValidatorVO;
    }



    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        errorCodeVO = errorCodeUtils.getSystemErrorCode(errorName);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }



}
