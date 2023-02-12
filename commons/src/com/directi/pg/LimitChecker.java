package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.BinVerificationManager;
import com.manager.TerminalManager;
import com.manager.vo.BinResponseVO;
import com.manager.vo.SplitPaymentVO;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 30, 2011
 * Time: 7:34:55 PM
 * This class helps in checking limit for merchant
 */
public class LimitChecker
{
    static Logger logger = new Logger(LimitChecker.class.getName());
    static TransactionLogger transactionLogger = new TransactionLogger(LimitChecker.class.getName());
    ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.CurrencyRate");

    public static void main(String[] args) throws Exception
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;

        calendar.add(Calendar.DATE, -7);
        long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;

        calendar.add(Calendar.DATE, 7); //Resetting time to current time

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;


        //System.out.println("todaysStartTimeInSecs = " + todaysStartTimeInSecs);
        //System.out.println("weeklyStartTimeInSecs = " + weeklyStartTimeInSecs);
        //System.out.println("monthsStartTimeInSecs = " + monthsStartTimeInSecs);


        Thread.sleep(10000);
    }

    public TerminalVO getTerminalBasedOnAccountID(CommonValidatorVO commonValidatorVO, boolean checkLimitFlag)throws PZDBViolationException,PZConstraintViolationException
    {

        int transactionAccoountId = 0;
        TerminalVO transactionTerminalVO=null;
        Connection dbConn = null;
        HashMap<Integer,TerminalVO> listOfAccounts= new HashMap<Integer, TerminalVO>();

        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        int dailyLimit = 0;
        int weeklyLimit = 0;
        int monthlyLimit = 0;

        try
        {
            logger.debug("Load member amount limits ");
            float member_daily_limit = 0;
            float member_monthly_limit = 0;
            float member_weekly_limit = 0;
            dbConn = Database.getConnection();

            String memberAmountLimits = "select daily_amount_limit,monthly_amount_limit,weekly_amount_limit from members where memberid= ? ";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();
            if (resultSet.next())
            {
                member_daily_limit = resultSet.getFloat("daily_amount_limit");
                member_monthly_limit = resultSet.getFloat("monthly_amount_limit");
                member_weekly_limit = resultSet.getFloat("weekly_amount_limit");
            }
            logger.debug("Member Daily Amount limits " + member_daily_limit);
            logger.debug("Member Weekly Amount limits " + member_weekly_limit);
            logger.debug("Member Monthly Amount limits " + member_monthly_limit);
            //dbConn.close();


            //dbConn = Database.getConnection();
            logger.debug("Load member account amount limits ");
            String accountList = "select accountid,daily_amount_limit,isActive,monthly_amount_limit,weekly_amount_limit,terminalid,max_transaction_amount,min_transaction_amount,addressDetails,addressValidation,terminalid,cardDetailRequired,is_recurring,isManualRecurring,isPSTTerminal,reject3DCard,currency_conversion,conversion_currency, isCardWhitelisted, isEmailWhitelisted from member_account_mapping where memberid=? and paymodeid=? and cardtypeid=? and isActive='Y' and binRouting='N' ORDER BY mam.priority";
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList);
            preparedStatementaccountList.setInt(1, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
            preparedStatementaccountList.setInt(2, Integer.parseInt(commonValidatorVO.getPaymentType()));
            preparedStatementaccountList.setInt(3, Integer.parseInt(commonValidatorVO.getCardType()));

            logger.debug("preparedStatementaccountList without terminal---"+preparedStatementaccountList);

            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();
            while (memberAccountresultSet.next())
            {
                transactionTerminalVO = new TerminalVO();
                transactionAccoountId = -1;
                int accountId = memberAccountresultSet.getInt("accountid");
                transactionTerminalVO.setMax_transaction_amount(memberAccountresultSet.getFloat("max_transaction_amount"));
                transactionTerminalVO.setMin_transaction_amount(memberAccountresultSet.getFloat("min_transaction_amount"));
                transactionTerminalVO.setTerminalId(memberAccountresultSet.getString("terminalid"));
                transactionTerminalVO.setIsActive(memberAccountresultSet.getString("isActive"));
                transactionTerminalVO.setAddressDetails(memberAccountresultSet.getString("addressDetails"));
                transactionTerminalVO.setAddressValidation(memberAccountresultSet.getString("addressValidation"));
                transactionTerminalVO.setAccountId(String.valueOf(accountId));
                transactionTerminalVO.setCardDetailRequired(memberAccountresultSet.getString("cardDetailRequired"));
                transactionTerminalVO.setIsRecurring(memberAccountresultSet.getString("is_recurring"));
                transactionTerminalVO.setIsManualRecurring(memberAccountresultSet.getString("isManualRecurring"));
                transactionTerminalVO.setIsPSTTerminal(memberAccountresultSet.getString("isPSTTerminal"));
                transactionTerminalVO.setReject3DCard(memberAccountresultSet.getString("reject3DCard"));
                transactionTerminalVO.setCurrencyConversion(memberAccountresultSet.getString("currency_conversion"));
                transactionTerminalVO.setConversionCurrency(memberAccountresultSet.getString("conversion_currency"));
                transactionTerminalVO.setIsCardWhitelisted(memberAccountresultSet.getString("isCardWhitelisted"));
                transactionTerminalVO.setIsEmailWhitelisted(memberAccountresultSet.getString("isEmailWhitelisted"));
                listOfAccounts.put(accountId,transactionTerminalVO);
                if (checkLimitFlag)
                {
                    float member_account_daily_limit = memberAccountresultSet.getFloat("daily_amount_limit");
                    float member_account_monthly_limit = memberAccountresultSet.getFloat("monthly_amount_limit");
                    float member_account_weekly_limit = memberAccountresultSet.getFloat("weekly_amount_limit");

                    logger.debug("Member Account Daily Amount limits " + member_account_daily_limit);
                    logger.debug("Member Account Weekly Amount limits " + member_account_weekly_limit);
                    logger.debug("Member Account Monthly Amount limits " + member_account_monthly_limit);

                    GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                    float account_daily_limit = account.getDailyAmountLimit();
                    float account_monthly_limit = account.getMonthlyAmountLimit();
                    float account_weekly_limit = account.getWeeklyAmountLimit();

                    logger.debug(" Account Daily Amount limits " + account_daily_limit);
                    logger.debug(" Account Monthly Amount limits " + account_monthly_limit);
                    logger.debug(" Account Weekly Amount limits " + account_weekly_limit);

                    /*String pgtypeid = account.getPgTypeId();
                    logger.debug("Gateway Type : " + pgtypeid);
                    GatewayType gatewayType = GatewayTypeService.getGatewayType(pgtypeid);
                    String tableName = Database.getTableName(gatewayType.getGateway());*/

                    String totalAmountQuery = "select sum(amount) from  transaction_common  where toid = ? and accountid = ? and dtstamp > ?  and status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent','settled')";
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    // calendar.set(Calendar.AM_PM,Calendar.AM);
                    long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    long monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                    calendar.set(Calendar.DAY_OF_WEEK, 1);
                    long weekStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                    float transaction_daily_amount = 0;
                    float transaction_monthly_amount = 0;
                    float transaction_weekly_amount = 0;
                    //Connection totalAmountDbConn = Database.getConnection();
                    PreparedStatement totalAmountPreparedStatement = dbConn.prepareStatement(totalAmountQuery);

                    totalAmountPreparedStatement.setInt(1, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
                    totalAmountPreparedStatement.setInt(2, accountId);
                    totalAmountPreparedStatement.setLong(3, todaysStartTimeInSecs);

                    ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
                    if (totalAmountResultSet.next())
                    {
                        transaction_daily_amount = totalAmountResultSet.getFloat(1);
                    }
                    logger.debug("Daily Transaction Amount : " + transaction_daily_amount);
                    totalAmountResultSet.close();
                    totalAmountPreparedStatement.setLong(3, monthsStartTimeInSecs);
                    totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
                    if (totalAmountResultSet.next())
                    {
                        transaction_monthly_amount = totalAmountResultSet.getFloat(1);
                    }
                    logger.debug("Monthly Transaction Amount : " + transaction_monthly_amount);
                    totalAmountResultSet.close();
                    //weekly amount limit
                    totalAmountPreparedStatement.setLong(3, weekStartTimeInSecs);
                    totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
                    if (totalAmountResultSet.next())
                    {
                        transaction_weekly_amount = totalAmountResultSet.getFloat(1);
                    }
                    logger.debug("Weekly Transaction Amount : " + transaction_weekly_amount);
                    totalAmountResultSet.close();
                    //totalAmountDbConn.close();

                    transaction_daily_amount += Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());
                    transaction_monthly_amount += Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());
                    transaction_weekly_amount += Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());

                    logger.debug("---remaining weekly limit members---"+(member_weekly_limit-transaction_weekly_amount));
                    logger.debug("---remaining weekly limit members_mapping---" + (member_account_weekly_limit - transaction_weekly_amount));
                    logger.debug("---remaining weekly limit gateway---" + (account_weekly_limit - transaction_weekly_amount));

                    logger.debug("Daily Transaction Amount after todays amount : " + transaction_daily_amount);
                    logger.debug("Weekly Transaction Amount after todays amount : " + transaction_weekly_amount);
                    logger.debug("Monthly Transaction Amount after current transaction amount : " + transaction_monthly_amount);

                    logger.debug("Checking Daily Limits");

                    //Commented by jinesh
                    if (transaction_daily_amount <= member_daily_limit && transaction_daily_amount <= member_account_daily_limit && transaction_daily_amount <= account_daily_limit)
                    {
                        if (transaction_weekly_amount <= member_weekly_limit && transaction_weekly_amount <= member_account_weekly_limit && transaction_weekly_amount <= account_weekly_limit)
                        {
                            if (transaction_monthly_amount <= member_monthly_limit && transaction_monthly_amount <= member_account_monthly_limit && transaction_monthly_amount <= account_monthly_limit)
                            {
                                logger.debug("accountid in condition---"+accountId);
                                transactionAccoountId = accountId;
                                break;
                            }
                            else
                            {
                                monthlyLimit++;
                            }
                        }
                        else
                        {
                            weeklyLimit++;
                        }
                    }
                    else
                    {
                        dailyLimit++;
                    }
                }
                else
                {
                    transactionAccoountId = accountId;
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Error while checking amount limit ", e);
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        if(transactionAccoountId==0)
        {

            return null;
        }
        else if(transactionAccoountId==-1)
        {
            if (dailyLimit >0)
            {
                error = "Member's account Daily Amount Limits exceeded. Please check your Technical Specs or visit http://support.pz.com and create a support request";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_DAILY_AMT_LIMIT,error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_DAILY_AMT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkAmountLimitForTerminal()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);

            }
            else if(weeklyLimit > 0 )
            {
                error = "Member's account Weekly Amount Limits exceeded. Please check your Technical Specs or visit http://support.pz.com and create a support request";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_WEEKLY_AMT_LIMIT,error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_WEEKLY_AMT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkAmountLimitForTerminal()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
            }
            else if (monthlyLimit >0 )
            {
                error = "Member's account Monthly Amount Limits exceeded. Please check your Technical Specs or visit http://support.pz.com and create a support request";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_MONTHLY_AMT_LIMIT,error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_MONTHLY_AMT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkAmountLimitForTerminal()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
            }
        }
        else
        {

            transactionTerminalVO = listOfAccounts.get(transactionAccoountId);

        }
        return transactionTerminalVO;
    }

    public TerminalVO getTerminalBasedOnAccountIDCardVelocity(CommonValidatorVO commonValidatorVO, boolean checkLimitFlag)throws PZDBViolationException,PZConstraintViolationException
    {

        Functions functions= new Functions();
        int transactionAccoountId = 0;
        TerminalVO transactionTerminalVO=null;
        Connection dbConn = null;
        HashMap<Integer,TerminalVO> listOfAccounts= new HashMap<Integer, TerminalVO>();

        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        SplitPaymentVO splitPaymentVO = new SplitPaymentVO();
        String cardNo=commonValidatorVO.getCardDetailsVO().getCardNum();
        transactionLogger.debug("CardNo--------------"+cardNo);
        String fistSixCCNum="";
        String lastFourCcNum="";
        if(functions.isValueNull(cardNo)){
            fistSixCCNum=cardNo.substring(0, 6);
            lastFourCcNum=cardNo.substring(cardNo.length() - 4);
        }
        try
        {
            boolean isDailyCardLimitOver = false;
            boolean isMonthlyCardLimitOver = false;
            boolean isWeeklyCardLimitOver = false;
            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            logger.debug("Load member card limits ");
           /* int member_daily_card_limit = 0;
            int member_weekly_card_limit = 0;
            int member_monthly_card_limit = 0;
            dbConn = Database.getConnection();*/



           /* String memberAmountLimits = "select daily_card_limit,weekly_card_limit,monthly_card_limit,card_transaction_limit from members where memberid= ? ";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId()));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                member_daily_card_limit = resultSet.getInt("daily_card_limit");
                member_weekly_card_limit = resultSet.getInt("weekly_card_limit");
                member_monthly_card_limit = resultSet.getInt("monthly_card_limit");

            }
            logger.error("Member Daily Card limits " + member_daily_card_limit);
            logger.error("Member Weekly Card limits " + member_weekly_card_limit);
            logger.error("Member Monthly Card limits " + member_monthly_card_limit);
            dbConn.close();

*/

            int memberid=0;
            int paymentType=0;
            int cardType=0;

            if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId())){
                memberid=Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            }
            if(functions.isValueNull(commonValidatorVO.getPaymentType())){
                paymentType=Integer.parseInt(commonValidatorVO.getPaymentType());
            }
            if(functions.isValueNull(commonValidatorVO.getCardType())){
                cardType=Integer.parseInt(commonValidatorVO.getCardType());
            }
            transactionLogger.debug("memberId-----" + memberid);
            transactionLogger.debug("paymentType-----" + paymentType);
            transactionLogger.debug("cardType-----" + cardType);

            dbConn = Database.getConnection();
            logger.debug("Load member account card limits ");

            StringBuffer accountList = new StringBuffer("select accountid,daily_card_limit,isActive,monthly_card_limit,weekly_card_limit,terminalid,max_transaction_amount,min_transaction_amount,addressDetails,addressValidation,terminalid,cardDetailRequired,is_recurring,isManualRecurring,isPSTTerminal,reject3DCard,currency_conversion,conversion_currency, isCardWhitelisted, isEmailWhitelisted, whitelisting_details, cardLimitCheck, cardAmountLimitCheck, amountLimitCheck from member_account_mapping where memberid=? and paymodeid=? and cardtypeid=? and isActive='Y' and binRouting='N'");
            if(functions.isValueNull(commonValidatorVO.getAccountId()))
                accountList.append(" AND accountid=?");
            accountList.append(" ORDER BY priority");
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList.toString());
            preparedStatementaccountList.setInt(1, memberid);
            preparedStatementaccountList.setInt(2, paymentType);
            preparedStatementaccountList.setInt(3, cardType);
            if(functions.isValueNull(commonValidatorVO.getAccountId()))
                preparedStatementaccountList.setString(4,commonValidatorVO.getAccountId());

            logger.debug("ps---"+preparedStatementaccountList);

            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();

            while (memberAccountresultSet.next())
            {
                transactionTerminalVO = new TerminalVO();
                transactionAccoountId = -1;
                int accountId = memberAccountresultSet.getInt("accountid");
                transactionTerminalVO.setMax_transaction_amount(memberAccountresultSet.getFloat("max_transaction_amount"));
                transactionTerminalVO.setMin_transaction_amount(memberAccountresultSet.getFloat("min_transaction_amount"));
                transactionTerminalVO.setTerminalId(memberAccountresultSet.getString("terminalid"));
                transactionTerminalVO.setIsActive(memberAccountresultSet.getString("isActive"));
                transactionTerminalVO.setAddressDetails(memberAccountresultSet.getString("addressDetails"));
                transactionTerminalVO.setAddressValidation(memberAccountresultSet.getString("addressValidation"));
                transactionTerminalVO.setAccountId(String.valueOf(accountId));
                transactionTerminalVO.setCardDetailRequired(memberAccountresultSet.getString("cardDetailRequired"));
                transactionTerminalVO.setIsRecurring(memberAccountresultSet.getString("is_recurring"));
                transactionTerminalVO.setIsManualRecurring(memberAccountresultSet.getString("isManualRecurring"));
                transactionTerminalVO.setIsPSTTerminal(memberAccountresultSet.getString("isPSTTerminal"));
                transactionTerminalVO.setReject3DCard(memberAccountresultSet.getString("reject3DCard"));
                transactionTerminalVO.setCurrencyConversion(memberAccountresultSet.getString("currency_conversion"));
                transactionTerminalVO.setConversionCurrency(memberAccountresultSet.getString("conversion_currency"));
                transactionTerminalVO.setIsCardWhitelisted(memberAccountresultSet.getString("isCardWhitelisted"));
                transactionTerminalVO.setIsEmailWhitelisted(memberAccountresultSet.getString("isEmailWhitelisted"));
                transactionTerminalVO.setWhitelisting(memberAccountresultSet.getString("whitelisting_details"));
                transactionTerminalVO.setCardLimitCheckTerminalLevel(memberAccountresultSet.getString("cardLimitCheck"));
                transactionTerminalVO.setCardAmountLimitCheckTerminalLevel(memberAccountresultSet.getString("cardAmountLimitCheck"));
                transactionTerminalVO.setAmountLimitCheckTerminalLevel(memberAccountresultSet.getString("amountLimitCheck"));
                listOfAccounts.put(accountId, transactionTerminalVO);
                if (checkLimitFlag)
                {
                   /* int member_account_daily_limit = memberAccountresultSet.getInt("daily_card_limit");
                    int member_account_weekly_limit = memberAccountresultSet.getInt("weekly_card_limit");
                    int member_account_monthly_limit = memberAccountresultSet.getInt("monthly_card_limit");


                    logger.debug("Member Account Daily Card limits " + member_account_daily_limit);
                    logger.debug("Member Account Weekly Card limits " + member_account_weekly_limit);
                    logger.debug("Member Account Monthly Card limits " + member_account_monthly_limit);*/

                    GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                    transactionTerminalVO.setCardLimitCheckAccountLevel(account.getCardCheckLimit());
                    transactionTerminalVO.setCardAmountLimitCheckAccountLevel(account.getCardAmountLimitCheckAcc());
                    transactionTerminalVO.setAmountLimitCheckAccountLevel(account.getAmountLimitCheckAcc());

                    int account_daily_limit = account.getDailyCardLimit();
                    int account_monthly_limit = account.getMonthlyCardLimit();
                    int account_weekly_limit = account.getWeeklyCardLimit();
                    String merchantId=account.getMerchantId();

                    logger.error("MerchantId " + merchantId  + " Account Daily Card limits " + account_daily_limit + " Account Monthly Card limits " + account_monthly_limit + " Account Weekly Card limits " + account_weekly_limit);


                    String countCardQuery = "select count(tt.trackingid) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and bd.isSuccessful='Y' and tt.fromid =? and tt.dtstamp >? ";
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    //`calendar.set(Calendar.AM_PM, Calendar.AM);
                    long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                    calendar.add(Calendar.DATE, -7);
                    long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                    calendar.add(Calendar.DATE, 7); //Resetting time to current time
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    long monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                    transactionLogger.debug("todaysStartTimeInSecs-----" + todaysStartTimeInSecs);
                    transactionLogger.debug("weeklyStartTimeInSecs-----" + weeklyStartTimeInSecs);
                    transactionLogger.debug("monthsStartTimeInSecs-----" + monthsStartTimeInSecs);

                    int daily_card_total = 0;
                    int weekly_card_total = 0;
                    int monthly_card_total = 0;

                    PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);

                    countCardPreparedStatement.setString(1, fistSixCCNum);
                    countCardPreparedStatement.setString(2, lastFourCcNum);
                    countCardPreparedStatement.setString(3, merchantId);
                    countCardPreparedStatement.setLong(4, todaysStartTimeInSecs);
                    logger.error(countCardPreparedStatement.toString());
                    ResultSet totalResultSet = countCardPreparedStatement.executeQuery();
                    if (totalResultSet.next())
                    {
                        daily_card_total = totalResultSet.getInt(1);
                    }
                    logger.error("Daily Total : " + daily_card_total);
                    totalResultSet.close();
                    countCardPreparedStatement.setLong(4, weeklyStartTimeInSecs);
                    logger.error(countCardPreparedStatement.toString());

                    totalResultSet = countCardPreparedStatement.executeQuery();

                    if (totalResultSet.next())
                    {
                        weekly_card_total = totalResultSet.getInt(1);
                    }
                    logger.error("Weekly Total : " + weekly_card_total);
                    totalResultSet.close();

                    countCardPreparedStatement.setLong(4, monthsStartTimeInSecs);
                    logger.error(countCardPreparedStatement.toString());

                    totalResultSet = countCardPreparedStatement.executeQuery();

                    if (totalResultSet.next())
                    {
                        monthly_card_total = totalResultSet.getInt(1);
                    }
                    logger.error("Monthly Card Total : " + monthly_card_total);
                    totalResultSet.close();


                    logger.debug("Checking Limits");

                    if (daily_card_total < account_daily_limit)
                    {
                        if (weekly_card_total < account_weekly_limit)
                        {
                            if (monthly_card_total < account_monthly_limit)
                            {
                                logger.debug("accountid in condition---"+accountId);

                                account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                                float account_daily_amount_limit = account.getDailyAmountLimit();
                                float account_weekly_amount_limit = account.getWeeklyAmountLimit();
                                float account_monthly_amount_limit = account.getMonthlyAmountLimit();


                                String amountQuery="select sum(amount) from transaction_common tt where tt.fromid =?  and status='capturesuccess' and tt.dtstamp >?";

                                calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                //`calendar.set(Calendar.AM_PM, Calendar.AM);
                                todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                                calendar.add(Calendar.DATE, -7);
                                weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                                calendar.add(Calendar.DATE, 7); //Resetting time to current time
                                calendar.set(Calendar.DAY_OF_MONTH, 1);
                                monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                                transactionLogger.debug("todaysStartTimeInSecs--Amount---" + todaysStartTimeInSecs);
                                transactionLogger.debug("weeklyStartTimeInSecs--Amount---" + weeklyStartTimeInSecs);
                                transactionLogger.debug("monthsStartTimeInSecs--Amount---" + monthsStartTimeInSecs);

                                float daily_amount = 0;
                                float weekly_amount = 0;
                                float monthly_amount = 0;

                                countCardPreparedStatement = dbConn.prepareStatement(amountQuery);

                                countCardPreparedStatement.setString(1, merchantId);
                                countCardPreparedStatement.setLong(2, todaysStartTimeInSecs);
                                logger.error(countCardPreparedStatement.toString());
                                totalResultSet = countCardPreparedStatement.executeQuery();
                                if (totalResultSet.next())
                                {
                                    daily_amount = totalResultSet.getFloat(1);
                                }
                                logger.error("daily_amount : " + daily_amount);
                                totalResultSet.close();
                                countCardPreparedStatement.setLong(2, weeklyStartTimeInSecs);
                                logger.error(countCardPreparedStatement.toString());

                                totalResultSet = countCardPreparedStatement.executeQuery();

                                if (totalResultSet.next())
                                {
                                    weekly_amount = totalResultSet.getFloat(1);
                                }
                                logger.error("weekly_amount : " + weekly_amount);
                                totalResultSet.close();

                                countCardPreparedStatement.setLong(2, monthsStartTimeInSecs);
                                logger.error(countCardPreparedStatement.toString());

                                totalResultSet = countCardPreparedStatement.executeQuery();

                                if (totalResultSet.next())
                                {
                                    monthly_amount = totalResultSet.getFloat(1);
                                }
                                logger.error("monthly_amount : " + monthly_amount);
                                totalResultSet.close();

                                if (daily_amount < account_daily_amount_limit)
                                {
                                    if (weekly_amount < account_weekly_amount_limit)
                                    {
                                        if (monthly_amount < account_monthly_amount_limit)
                                        {
                                            transactionLogger.debug("accountId-----" + accountId);
                                            transactionAccoountId = accountId;
                                            break;
                                        }else {
                                            isMonthlyCardLimitOver=true;
                                        }
                                    }else {
                                        isWeeklyCardLimitOver=true;
                                    }
                                }else {
                                    isDailyCardLimitOver=true;
                                }
                            }else {
                                isMonthlyCardLimitOver = true;
                            }
                        } else {
                            isWeeklyCardLimitOver = true;
                        }
                    }else {
                        isDailyCardLimitOver = true;
                    }
                }else
                {
                    transactionAccoountId = accountId;
                }
            }
            if(transactionAccoountId==0)
            {
                return null;
            }
            else if(transactionAccoountId==-1)
            {
                if (isDailyCardLimitOver)
                {
                    error = "Your Card Limit exceeded:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_CARDLIMIT_EXCEEDED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isWeeklyCardLimitOver)
                {
                    error = "Your Card Limit exceeded:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_CARDLIMIT_EXCEEDED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isMonthlyCardLimitOver)
                {
                    error = "Your Card Limit exceeded:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_CARDLIMIT_EXCEEDED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }else {
                transactionTerminalVO = listOfAccounts.get(transactionAccoountId);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return transactionTerminalVO;
    }



    public  TerminalVO getTerminalBasedOnCurrencyCardVelocity(CommonValidatorVO commonValidatorVO, boolean checkLimitFlag)throws PZConstraintViolationException,PZDBViolationException
    {
        transactionLogger.debug("-----inside getTerminalBasedOnCurrencyCardVelocity-----");
        Functions functions= new Functions();
        int transactionAccoountId = 0;
        TerminalVO transactionTerminalVO=null;
        Connection dbConn = null;
        HashMap<Integer,TerminalVO> listOfAccounts= new HashMap<Integer, TerminalVO>();

        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        SplitPaymentVO splitPaymentVO = new SplitPaymentVO();
        String cardNo=commonValidatorVO.getCardDetailsVO().getCardNum();
        transactionLogger.debug("CardNo--------------"+cardNo);
        String fistSixCCNum="";
        String lastFourCcNum="";
        if(functions.isValueNull(cardNo)){
            fistSixCCNum=cardNo.substring(0, 6);
            lastFourCcNum=cardNo.substring(cardNo.length() - 4);
        }
        try
        {
            logger.debug("Load member card limits ");
           /* int member_daily_card_limit = 0;
            int member_weekly_card_limit = 0;
            int member_monthly_card_limit = 0;
            dbConn = Database.getConnection();*/

            boolean isDailyCardLimitOver = false;
            boolean isMonthlyCardLimitOver = false;
            boolean isWeeklyCardLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

       /*     String memberAmountLimits = "select daily_card_limit,weekly_card_limit,monthly_card_limit from members where memberid= ? ";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId()));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                member_daily_card_limit = resultSet.getInt("daily_card_limit");
                member_weekly_card_limit = resultSet.getInt("weekly_card_limit");
                member_monthly_card_limit = resultSet.getInt("monthly_card_limit");

            }
            logger.error("Member Daily Card limits " + member_daily_card_limit);
            logger.error("Member Weekly Card limits " + member_weekly_card_limit);
            logger.error("Member Monthly Card limits " + member_monthly_card_limit);
            dbConn.close();*/

            int memberid=0;
            int paymentType=0;
            int cardType=0;
            String currency="";

            if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId())){
                memberid=Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            }
            if(functions.isValueNull(commonValidatorVO.getPaymentType())){
                paymentType=Integer.parseInt(commonValidatorVO.getPaymentType());
            }
            if(functions.isValueNull(commonValidatorVO.getCardType())){
                cardType=Integer.parseInt(commonValidatorVO.getCardType());
            }
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())){
                currency=commonValidatorVO.getTransDetailsVO().getCurrency();
            }

            transactionLogger.debug("memberId-----" + memberid);
            transactionLogger.debug("paymentType-----" + paymentType);
            transactionLogger.debug("cardType-----" + cardType);
            transactionLogger.debug("currency-----" + currency);

            dbConn = Database.getConnection();
            logger.debug("Load member account card limits ");

            StringBuffer accountList = new StringBuffer("SELECT mam.accountid,mam.daily_card_limit,mam.isActive,mam.monthly_card_limit,mam.weekly_card_limit,mam.terminalid,mam.max_transaction_amount,mam.min_transaction_amount,mam.addressDetails,mam.addressValidation,mam.cardDetailRequired,mam.is_recurring,mam.isManualRecurring,mam.isPSTTerminal,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc,gt.pgtypeid FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND mam.isActive='Y' AND mam.binRouting='N'");
            if(functions.isValueNull(commonValidatorVO.getAccountId()))
                accountList.append(" AND mam.accountid=?");
            accountList.append(" ORDER BY mam.priority");
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList.toString());
            preparedStatementaccountList.setInt(1, memberid);
            preparedStatementaccountList.setInt(2, paymentType);
            preparedStatementaccountList.setInt(3, cardType);
            preparedStatementaccountList.setString(4, currency);
            if(functions.isValueNull(commonValidatorVO.getAccountId()))
                preparedStatementaccountList.setString(5,commonValidatorVO.getAccountId());

            logger.error("ps---" + preparedStatementaccountList);

            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();

            while (memberAccountresultSet.next())
            {
                transactionTerminalVO = new TerminalVO();
                transactionAccoountId = -1;
                int accountId = memberAccountresultSet.getInt("accountid");
                transactionTerminalVO.setMax_transaction_amount(memberAccountresultSet.getFloat("max_transaction_amount"));
                transactionTerminalVO.setMin_transaction_amount(memberAccountresultSet.getFloat("min_transaction_amount"));
                transactionTerminalVO.setTerminalId(memberAccountresultSet.getString("terminalid"));
                transactionTerminalVO.setIsActive(memberAccountresultSet.getString("isActive"));
                transactionTerminalVO.setAddressDetails(memberAccountresultSet.getString("addressDetails"));
                transactionTerminalVO.setAddressValidation(memberAccountresultSet.getString("addressValidation"));
                transactionTerminalVO.setAccountId(String.valueOf(accountId));
                transactionTerminalVO.setCardDetailRequired(memberAccountresultSet.getString("cardDetailRequired"));
                transactionTerminalVO.setIsRecurring(memberAccountresultSet.getString("is_recurring"));
                transactionTerminalVO.setIsManualRecurring(memberAccountresultSet.getString("isManualRecurring"));
                transactionTerminalVO.setIsPSTTerminal(memberAccountresultSet.getString("isPSTTerminal"));
                transactionTerminalVO.setReject3DCard(memberAccountresultSet.getString("reject3DCard"));
                transactionTerminalVO.setCurrencyConversion(memberAccountresultSet.getString("currency_conversion"));
                transactionTerminalVO.setConversionCurrency(memberAccountresultSet.getString("conversion_currency"));
                transactionTerminalVO.setIsCardWhitelisted(memberAccountresultSet.getString("isCardWhitelisted"));
                transactionTerminalVO.setIsEmailWhitelisted(memberAccountresultSet.getString("isEmailWhitelisted"));
                transactionTerminalVO.setWhitelisting(memberAccountresultSet.getString("whitelisting_details"));
                transactionTerminalVO.setCardLimitCheckTerminalLevel(memberAccountresultSet.getString("cardLimitCheck"));
                transactionTerminalVO.setCardAmountLimitCheckTerminalLevel(memberAccountresultSet.getString("cardAmountLimitCheck"));
                transactionTerminalVO.setAmountLimitCheckTerminalLevel(memberAccountresultSet.getString("amountLimitCheck"));
                transactionTerminalVO.setCardLimitCheckAccountLevel(memberAccountresultSet.getString("cardLimitAccountLevel"));
                transactionTerminalVO.setCardAmountLimitCheckAccountLevel(memberAccountresultSet.getString("cardAmountLimitCheckAcc"));
                transactionTerminalVO.setAmountLimitCheckAccountLevel(memberAccountresultSet.getString("amountLimitCheckAcc"));
                transactionTerminalVO.setGateway_id(memberAccountresultSet.getString("pgtypeid"));
                listOfAccounts.put(accountId, transactionTerminalVO);

                transactionLogger.debug("accountid-----" + accountId);
                transactionLogger.debug("terminalId-----" + transactionTerminalVO.getTerminalId());

                if (checkLimitFlag)
                {
                   /* int member_account_daily_limit = memberAccountresultSet.getInt("daily_card_limit");
                    int member_account_weekly_limit = memberAccountresultSet.getInt("weekly_card_limit");
                    int member_account_monthly_limit = memberAccountresultSet.getInt("monthly_card_limit");


                    logger.debug("Member Account Daily Card limits " + member_account_daily_limit);
                    logger.debug("Member Account Weekly Card limits " + member_account_weekly_limit);
                    logger.debug("Member Account Monthly Card limits " + member_account_monthly_limit);*/

                    GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                    int account_daily_limit = account.getDailyCardLimit();
                    int account_monthly_limit = account.getMonthlyCardLimit();
                    int account_weekly_limit = account.getWeeklyCardLimit();
                    String merchantId=account.getMerchantId();

                    logger.error("Merchantid " + merchantId + " Account Daily card limits " + account_daily_limit + " Account Monthly card limits " + account_monthly_limit + " Account Weekly card limits " + account_weekly_limit);


                    String countCardQuery = "select count(tt.trackingid) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ?  and bd.isSuccessful='Y' and tt.fromid =?  and tt.dtstamp >? ";
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    //calendar.set(Calendar.AM_PM, Calendar.AM);
                    long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                    calendar.add(Calendar.DATE, -7);
                    long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                    calendar.add(Calendar.DATE, 7); //Resetting time to current time
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    long monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;


                    transactionLogger.debug("todaysStartTimeInSecs-----" + todaysStartTimeInSecs);
                    transactionLogger.debug("weeklyStartTimeInSecs-----" + weeklyStartTimeInSecs);
                    transactionLogger.debug("monthsStartTimeInSecs-----" + monthsStartTimeInSecs);

                    int daily_card_total = 0;
                    int weekly_card_total = 0;
                    int monthly_card_total = 0;

                    PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);

                    countCardPreparedStatement.setString(1, fistSixCCNum);
                    countCardPreparedStatement.setString(2, lastFourCcNum);
                    countCardPreparedStatement.setString(3, merchantId);
                    countCardPreparedStatement.setLong(4, todaysStartTimeInSecs);
                    logger.error(countCardPreparedStatement.toString());
                    ResultSet totalResultSet = countCardPreparedStatement.executeQuery();
                    if (totalResultSet.next())
                    {
                        daily_card_total = totalResultSet.getInt(1);
                    }
                    logger.error("Daily Total : " + daily_card_total);
                    totalResultSet.close();
                    countCardPreparedStatement.setLong(4, weeklyStartTimeInSecs);
                    logger.error(countCardPreparedStatement.toString());

                    totalResultSet = countCardPreparedStatement.executeQuery();

                    if (totalResultSet.next())
                    {
                        weekly_card_total = totalResultSet.getInt(1);
                    }
                    logger.error("Weekly Total : " + weekly_card_total);
                    totalResultSet.close();

                    countCardPreparedStatement.setLong(4, monthsStartTimeInSecs);
                    logger.error(countCardPreparedStatement.toString());

                    totalResultSet = countCardPreparedStatement.executeQuery();

                    if (totalResultSet.next())
                    {
                        monthly_card_total = totalResultSet.getInt(1);
                    }
                    logger.error("Monthly Card Total : " + monthly_card_total);
                    totalResultSet.close();


                    logger.debug("Checking Limits");

                    transactionLogger.error("daily_card_total-----"+daily_card_total + " weekly_card_total-----"+weekly_card_total + " monthly_card_total-----"+monthly_card_total + "monthly_card_total-----"+monthly_card_total);

                    transactionLogger.error("account_daily_limit-----"+account_daily_limit + " account_weekly_limit-----"+account_weekly_limit + " account_monthly_limit-----"+account_monthly_limit);

                    transactionLogger.error("----isDailyCardLimitOver ---"+isDailyCardLimitOver + " ----isWeeklyCardLimitOver ---"+isWeeklyCardLimitOver + " ----isMonthlyCardLimitOver ---"+isMonthlyCardLimitOver);

                    if (daily_card_total < account_daily_limit)
                    {
                        if ( weekly_card_total < account_weekly_limit)
                        {
                            if (monthly_card_total < account_monthly_limit)
                            {
                                transactionLogger.debug("-----now checking for amount limit-----");
                                account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                                float account_daily_amount_limit = account.getDailyAmountLimit();
                                float account_weekly_amount_limit = account.getWeeklyAmountLimit();
                                float account_monthly_amount_limit = account.getMonthlyAmountLimit();

                                String amountQuery="select sum(amount) from transaction_common tt where tt.fromid =?  and status='capturesuccess' and tt.dtstamp >?";

                                calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                //`calendar.set(Calendar.AM_PM, Calendar.AM);
                                todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                                calendar.add(Calendar.DATE, -7);
                                weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                                calendar.add(Calendar.DATE, 7); //Resetting time to current time
                                calendar.set(Calendar.DAY_OF_MONTH, 1);
                                monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                                transactionLogger.debug("todaysStartTimeInSecs--Amount---" + todaysStartTimeInSecs);
                                transactionLogger.debug("weeklyStartTimeInSecs--Amount---" + weeklyStartTimeInSecs);
                                transactionLogger.debug("monthsStartTimeInSecs--Amount---" + monthsStartTimeInSecs);

                                float daily_amount = 0.0f;
                                float weekly_amount = 0.0f;
                                float monthly_amount = 0.0f;

                                countCardPreparedStatement = dbConn.prepareStatement(amountQuery);

                                countCardPreparedStatement.setString(1, merchantId);
                                countCardPreparedStatement.setLong(2, todaysStartTimeInSecs);
                                logger.error(countCardPreparedStatement.toString());
                                totalResultSet = countCardPreparedStatement.executeQuery();
                                if (totalResultSet.next())
                                {
                                    daily_amount = totalResultSet.getFloat(1);
                                }
                                logger.error("daily_amount : " + daily_amount);
                                totalResultSet.close();
                                countCardPreparedStatement.setLong(2, weeklyStartTimeInSecs);
                                logger.error(countCardPreparedStatement.toString());

                                totalResultSet = countCardPreparedStatement.executeQuery();

                                if (totalResultSet.next())
                                {
                                    weekly_amount = totalResultSet.getFloat(1);
                                }
                                logger.error("weekly_amount : " + weekly_amount);
                                totalResultSet.close();

                                countCardPreparedStatement.setLong(2, monthsStartTimeInSecs);
                                logger.error(countCardPreparedStatement.toString());

                                totalResultSet = countCardPreparedStatement.executeQuery();

                                if (totalResultSet.next())
                                {
                                    monthly_amount = totalResultSet.getFloat(1);
                                }
                                logger.error("monthly_amount : " + monthly_amount);
                                totalResultSet.close();

                                transactionLogger.error("account_daily_amount_limit-----"+account_daily_amount_limit + " account_weekly_amount_limit-----"+account_weekly_amount_limit + " account_monthly_amount_limit-----"+account_monthly_amount_limit);


                                if (daily_amount < account_daily_amount_limit)
                                {
                                    if (weekly_amount < account_weekly_amount_limit)
                                    {
                                        if (monthly_amount < account_monthly_amount_limit)
                                        {
                                            transactionLogger.debug("accountId-----" + accountId);
                                            transactionAccoountId = accountId;
                                            break;
                                        }else {
                                            transactionLogger.debug("----indside monthly amount error----");
                                            isMonthlyCardLimitOver=true;
                                        }
                                    }else {
                                        transactionLogger.debug("----indside weekly amount error----");
                                        isWeeklyCardLimitOver=true;
                                    }
                                }else {
                                    transactionLogger.debug("----indside daily amount error----");
                                    isDailyCardLimitOver=true;
                                }

                            }else {
                                transactionLogger.debug("----indside monthly error----");
                                isMonthlyCardLimitOver = true;
                            }
                        } else {
                            transactionLogger.debug("----indside weekly error----");
                            isWeeklyCardLimitOver = true;
                        }
                    }else {
                        transactionLogger.debug("----indside daily error----");
                        isDailyCardLimitOver = true;
                    }
                }else
                {
                    transactionAccoountId = accountId;
                }
            }

            transactionLogger.debug("transactionAccoountId-----" + transactionAccoountId);

            if(transactionAccoountId==0)
            {
                return null;
            }
            else if(transactionAccoountId==-1)
            {
                transactionLogger.debug("----inside error---");
                transactionLogger.debug("----isDailyCardLimitOver 1---" + isDailyCardLimitOver);
                transactionLogger.debug("----isWeeklyCardLimitOver 1---" + isWeeklyCardLimitOver);
                transactionLogger.debug("----isMonthlyCardLimitOver 1---" + isMonthlyCardLimitOver);
                if (isDailyCardLimitOver)
                {
                    transactionLogger.debug("----indside daily error1----");
                    error = "Your Card Limit exceeded:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_CARDLIMIT_EXCEEDED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isWeeklyCardLimitOver)
                {
                    transactionLogger.debug("----indside weekly error1----");
                    error = "Your Card Limit exceeded:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_CARDLIMIT_EXCEEDED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isMonthlyCardLimitOver)
                {
                    transactionLogger.debug("----indside monthly error1----");
                    error = "Your Card Limit exceeded:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_CARDLIMIT_EXCEEDED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }else {
                transactionTerminalVO = listOfAccounts.get(transactionAccoountId);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return transactionTerminalVO;
    }
    public  TerminalVO getTerminalBasedOnCurrency(CommonValidatorVO commonValidatorVO, boolean checkLimitFlag)throws PZConstraintViolationException,PZDBViolationException
    {
        int transactionAccoountId = 0;
        TerminalVO transactionTerminalVO=null;
        Connection dbConn = null;
        HashMap<Integer,TerminalVO> listOfAccounts= new HashMap<Integer, TerminalVO>();

        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        int dailyLimit = 0;
        int weeklyLimit = 0;
        int monthlyLimit = 0;

        try
        {
            float member_daily_limit = 0;
            float member_monthly_limit = 0;
            float member_weekly_limit = 0;
            dbConn = Database.getConnection();

            String memberAmountLimits = "select daily_amount_limit,monthly_amount_limit,weekly_amount_limit from members where memberid= ? ";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();
            if (resultSet.next())
            {
                member_daily_limit = resultSet.getFloat("daily_amount_limit");
                member_monthly_limit = resultSet.getFloat("monthly_amount_limit");
                member_weekly_limit = resultSet.getFloat("weekly_amount_limit");
            }
            logger.debug("Member Daily Amount limits " + member_daily_limit);
            logger.debug("Member Weekly Amount limits " + member_weekly_limit);
            logger.debug("Member Monthly Amount limits " + member_monthly_limit);

            logger.debug("Load member account amount limits ");
            String accountList = "SELECT mam.accountid,mam.daily_amount_limit,mam.isActive,mam.monthly_amount_limit,mam.weekly_amount_limit,mam.terminalid,mam.max_transaction_amount,mam.min_transaction_amount,mam.addressDetails,mam.addressValidation,mam.cardDetailRequired,mam.is_recurring,mam.isManualRecurring,mam.isPSTTerminal,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,gt.pgtypeid FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND mam.isActive='Y' AND mam.binRouting='N' ORDER BY mam.priority";
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList);
            preparedStatementaccountList.setInt(1, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
            preparedStatementaccountList.setInt(2, Integer.parseInt(commonValidatorVO.getPaymentType()));
            preparedStatementaccountList.setInt(3, Integer.parseInt(commonValidatorVO.getCardType()));
            preparedStatementaccountList.setString(4, commonValidatorVO.getTransDetailsVO().getCurrency());

            logger.debug("ps---"+preparedStatementaccountList);

            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();

            while (memberAccountresultSet.next())
            {
                transactionTerminalVO = new TerminalVO();
                transactionAccoountId = -1;
                int accountId = memberAccountresultSet.getInt("accountid");
                transactionTerminalVO.setMax_transaction_amount(memberAccountresultSet.getFloat("max_transaction_amount"));
                transactionTerminalVO.setMin_transaction_amount(memberAccountresultSet.getFloat("min_transaction_amount"));
                transactionTerminalVO.setTerminalId(memberAccountresultSet.getString("terminalid"));
                transactionTerminalVO.setIsActive(memberAccountresultSet.getString("isActive"));
                transactionTerminalVO.setAddressDetails(memberAccountresultSet.getString("addressDetails"));
                transactionTerminalVO.setAddressValidation(memberAccountresultSet.getString("addressValidation"));
                transactionTerminalVO.setAccountId(String.valueOf(accountId));
                transactionTerminalVO.setCardDetailRequired(memberAccountresultSet.getString("cardDetailRequired"));
                transactionTerminalVO.setIsRecurring(memberAccountresultSet.getString("is_recurring"));
                transactionTerminalVO.setIsManualRecurring(memberAccountresultSet.getString("isManualRecurring"));
                transactionTerminalVO.setIsPSTTerminal(memberAccountresultSet.getString("isPSTTerminal"));
                transactionTerminalVO.setReject3DCard(memberAccountresultSet.getString("reject3DCard"));
                transactionTerminalVO.setCurrencyConversion(memberAccountresultSet.getString("currency_conversion"));
                transactionTerminalVO.setConversionCurrency(memberAccountresultSet.getString("conversion_currency"));
                transactionTerminalVO.setIsCardWhitelisted(memberAccountresultSet.getString("isCardWhitelisted"));
                transactionTerminalVO.setIsEmailWhitelisted(memberAccountresultSet.getString("isEmailWhitelisted"));
                transactionTerminalVO.setGateway_id(memberAccountresultSet.getString("pgtypeid"));
                listOfAccounts.put(accountId,transactionTerminalVO);
                if (checkLimitFlag)
                {
                    float member_account_daily_limit = memberAccountresultSet.getFloat("daily_amount_limit");
                    float member_account_monthly_limit = memberAccountresultSet.getFloat("monthly_amount_limit");
                    float member_account_weekly_limit = memberAccountresultSet.getFloat("weekly_amount_limit");

                    logger.debug("Member Account Daily Amount limits " + member_account_daily_limit);
                    logger.debug("Member Account Weekly Amount limits " + member_account_weekly_limit);
                    logger.debug("Member Account Monthly Amount limits " + member_account_monthly_limit);

                    GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                    float account_daily_limit = account.getDailyAmountLimit();
                    float account_monthly_limit = account.getMonthlyAmountLimit();
                    float account_weekly_limit = account.getWeeklyAmountLimit();

                    logger.debug(" Account Daily Amount limits " + account_daily_limit);
                    logger.debug(" Account Monthly Amount limits " + account_monthly_limit);
                    logger.debug(" Account Weekly Amount limits " + account_weekly_limit);


                    String totalAmountQuery = "select sum(amount) from transaction_common where toid = ? and accountid = ? and dtstamp > ?  and status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent','settled')";
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    //calendar.set(Calendar.AM_PM,Calendar.AM);
                    long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    long monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                    calendar.set(Calendar.DAY_OF_WEEK, 1);
                    long weekStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                    float transaction_daily_amount = 0;
                    float transaction_monthly_amount = 0;
                    float transaction_weekly_amount = 0;

                    PreparedStatement totalAmountPreparedStatement = dbConn.prepareStatement(totalAmountQuery);

                    totalAmountPreparedStatement.setInt(1, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
                    totalAmountPreparedStatement.setInt(2, accountId);
                    totalAmountPreparedStatement.setLong(3, todaysStartTimeInSecs);

                    ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
                    if (totalAmountResultSet.next())
                    {
                        transaction_daily_amount = totalAmountResultSet.getFloat(1);
                    }
                    logger.debug("Daily Transaction Amount : " + transaction_daily_amount);
                    totalAmountResultSet.close();
                    totalAmountPreparedStatement.setLong(3, monthsStartTimeInSecs);
                    totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
                    if (totalAmountResultSet.next())
                    {
                        transaction_monthly_amount = totalAmountResultSet.getFloat(1);
                    }
                    logger.debug("Monthly Transaction Amount : " + transaction_monthly_amount);
                    totalAmountResultSet.close();
                    //weekly amount limit
                    totalAmountPreparedStatement.setLong(3, weekStartTimeInSecs);
                    totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
                    if (totalAmountResultSet.next())
                    {
                        transaction_weekly_amount = totalAmountResultSet.getFloat(1);
                    }
                    logger.debug("Weekly Transaction Amount : " + transaction_weekly_amount);
                    totalAmountResultSet.close();


                    transaction_daily_amount += Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());
                    transaction_monthly_amount += Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());
                    transaction_weekly_amount += Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());

                    logger.debug("---remaining weekly limit members---"+(member_weekly_limit-transaction_weekly_amount));
                    logger.debug("---remaining weekly limit members_mapping---" + (member_account_weekly_limit - transaction_weekly_amount));
                    logger.debug("---remaining weekly limit gateway---" + (account_weekly_limit - transaction_weekly_amount));

                    logger.debug("Daily Transaction Amount after todays amount : " + transaction_daily_amount);
                    logger.debug("Weekly Transaction Amount after todays amount : " + transaction_weekly_amount);
                    logger.debug("Monthly Transaction Amount after current transaction amount : " + transaction_monthly_amount);


                    if (transaction_daily_amount <= member_daily_limit && transaction_daily_amount <= member_account_daily_limit && transaction_daily_amount <= account_daily_limit)
                    {
                        if (transaction_weekly_amount <= member_weekly_limit && transaction_weekly_amount <= member_account_weekly_limit && transaction_weekly_amount <= account_weekly_limit)
                        {
                            if (transaction_monthly_amount <= member_monthly_limit && transaction_monthly_amount <= member_account_monthly_limit && transaction_monthly_amount <= account_monthly_limit)
                            {
                                logger.debug("accountid in condition---"+accountId);
                                transactionAccoountId = accountId;
                                break;
                            }
                            else
                            {
                                monthlyLimit++;
                            }
                        }
                        else
                        {
                            weeklyLimit++;
                        }
                    }
                    else
                    {
                        dailyLimit++;
                    }
                }
                else
                {
                    transactionAccoountId = accountId;
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Error while checking amount limit ", e);
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        if(transactionAccoountId==0)
        {
            return null;
        }
        else if(transactionAccoountId==-1)
        {
            if (dailyLimit >0)
            {
                error = "Member's account Daily Amount Limits exceeded. Please check your Technical Specs or visit http://support.pz.com and create a support request";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_DAILY_AMT_LIMIT,error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_DAILY_AMT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkAmountLimitForTerminal()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);

            }
            if(weeklyLimit > 0 )
            {
                error = "Member's account Weekly Amount Limits exceeded. Please check your Technical Specs or visit http://support.pz.com and create a support request";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_WEEKLY_AMT_LIMIT,error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_WEEKLY_AMT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkAmountLimitForTerminal()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
            }
            if (monthlyLimit >0 )
            {
                error = "Member's account Monthly Amount Limits exceeded. Please check your Technical Specs or visit http://support.pz.com and create a support request";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_MONTHLY_AMT_LIMIT,error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_MONTHLY_AMT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkAmountLimitForTerminal()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
            }
        }
        else
        {

            transactionTerminalVO = listOfAccounts.get(transactionAccoountId);

        }
        return transactionTerminalVO;
    }


    public void checkAmountLimitForTerminalNew(String amount, String terminalid,String memberId, CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        boolean monthlyAmountLimitOver = true;
        boolean dailyAmountLimitOver = true;
        boolean weeklyAmountLimitOver = true;
        String error = "";
        Connection dbConn = null;

        try
        {
            logger.debug("Load member amount limits ");
            float member_daily_limit = 0;
            float member_monthly_limit = 0;
            float member_weekly_limit = 0;
            dbConn = Database.getConnection();

            String memberAmountLimits = "select daily_amount_limit,monthly_amount_limit,weekly_amount_limit from members where memberid= ? ";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));
            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();
            if (resultSet.next())
            {
                member_daily_limit = resultSet.getFloat("daily_amount_limit");
                member_monthly_limit = resultSet.getFloat("monthly_amount_limit");
                member_weekly_limit = resultSet.getFloat("weekly_amount_limit");
            }
            logger.debug("Member Daily Amount limits " + member_daily_limit);
            logger.debug("Member Monthly Amount limits " + member_monthly_limit);
            logger.debug("Member Weekly Amount limits " + member_weekly_limit);
            //dbConn.close();

            //dbConn = Database.getConnection();
            logger.debug("Load member account amount limits ");
            String accountList = "select accountid,daily_amount_limit,monthly_amount_limit,weekly_amount_limit from member_account_mapping where terminalid=? and  isActive='Y'";
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList);
            preparedStatementaccountList.setInt(1, Integer.parseInt(terminalid));

            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();

            if (memberAccountresultSet.next())
            {
                int accountId = memberAccountresultSet.getInt("accountid");

                float member_account_daily_limit = memberAccountresultSet.getFloat("daily_amount_limit");
                float member_account_monthly_limit = memberAccountresultSet.getFloat("monthly_amount_limit");
                float member_account_weekly_limit = memberAccountresultSet.getFloat("weekly_amount_limit");

                logger.debug("Member Account ID" + accountId);
                logger.debug("Member Account Daily Amount limits " + member_account_daily_limit);
                logger.debug("Member Account Monthly Amount limits " + member_account_monthly_limit);
                logger.debug("Member Account Weekly Amount limits " + member_account_weekly_limit);

                logger.debug("Load Account amount limits ");
                GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                float account_daily_limit = account.getDailyAmountLimit();
                float account_monthly_limit = account.getMonthlyAmountLimit();
                float account_weekly_limit = account.getWeeklyAmountLimit();

                logger.debug(" Account Daily Amount limits " + account_daily_limit);
                logger.debug(" Account Monthly Amount limits " + account_monthly_limit);
                logger.debug(" Account Weekly Amount limits " + account_weekly_limit);



                String totalAmountQuery = "select sum(amount) from transaction_common where toid = ? and accountid = ? and dtstamp > ?  and status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent','settled')";
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                //calendar.set(Calendar.AM_PM,Calendar.AM);
                long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                long monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                calendar.set(Calendar.DAY_OF_WEEK, 1);
                long weeklyStartTimeInSecs = calendar.getTimeInMillis()/1000;

                float transaction_daily_amount = 0;
                float transaction_monthly_amount = 0;
                float transaction_weekly_amount = 0;


                PreparedStatement totalAmountPreparedStatement = dbConn.prepareStatement(totalAmountQuery);

                totalAmountPreparedStatement.setInt(1, Integer.parseInt(memberId));
                totalAmountPreparedStatement.setInt(2, accountId);
                totalAmountPreparedStatement.setLong(3, todaysStartTimeInSecs);

                ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    transaction_daily_amount = totalAmountResultSet.getFloat(1);
                }
                logger.debug("Daily Transaction Amount : " + transaction_daily_amount);
                totalAmountResultSet.close();
                totalAmountPreparedStatement.setLong(3, monthsStartTimeInSecs);
                totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    transaction_monthly_amount = totalAmountResultSet.getFloat(1);
                }
                logger.debug("Monthly Transaction Amount : " + transaction_monthly_amount);
                totalAmountResultSet.close();
                //weekly Amount limit time
                totalAmountPreparedStatement.setLong(3,weeklyStartTimeInSecs);
                totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
                if(totalAmountResultSet.next())
                {
                    transaction_weekly_amount = totalAmountResultSet.getFloat(1);
                }
                logger.debug("Weeklyly Transaction Amount : " + transaction_weekly_amount);
                totalAmountResultSet.close();


                transaction_daily_amount += Float.parseFloat(amount);
                transaction_monthly_amount += Float.parseFloat(amount);
                transaction_weekly_amount += Float.parseFloat(amount);

                logger.debug("Daily Transaction Amount after todays amount : " + transaction_daily_amount);
                logger.debug("Weekly Transaction Amount after current transaction amount : " + transaction_weekly_amount);
                logger.debug("Monthly Transaction Amount after current transaction amount : " + transaction_monthly_amount);

                logger.debug("Checking Daily Limits");
                if (transaction_daily_amount <= member_daily_limit && transaction_daily_amount <= member_account_daily_limit && transaction_daily_amount <= account_daily_limit)
                {
                    dailyAmountLimitOver = false;
                }
                if(transaction_weekly_amount <= member_weekly_limit && transaction_weekly_amount <= member_account_weekly_limit && transaction_weekly_amount <= account_weekly_limit)
                {
                    weeklyAmountLimitOver = false;
                }
                if (transaction_monthly_amount <= member_monthly_limit && transaction_monthly_amount <= member_account_monthly_limit && transaction_monthly_amount <= account_monthly_limit)
                {
                    monthlyAmountLimitOver = false;
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Error while checking amount limit ", e);
        }
        finally
        {
            Database.closeConnection(dbConn);
        }

        if(dailyAmountLimitOver)
        {
            error = "Member's account Daily Amount Limits exceeded. Please check your Technical Specs or visit http://support.pz.com and create a support request";
            errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_DAILY_AMT_LIMIT,error);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_DAILY_AMT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkAmountLimitForTerminal()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);

        }
        if(weeklyAmountLimitOver)
        {
            error = "Member's account Weekly Amount Limits exceeded. Please check your Technical Specs or visit http://support.pz.com and create a support request";
            errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_WEEKLY_AMT_LIMIT,error);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_WEEKLY_AMT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkAmountLimitForTerminal()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
        }
        if(monthlyAmountLimitOver)
        {
            error = "Member's account Monthly Amount Limits exceeded. Please check your Technical Specs or visit http://support.pz.com and create a support request";
            errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_MONTHLY_AMT_LIMIT,error);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_MONTHLY_AMT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkAmountLimitForTerminal()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
        }
    }
    public boolean checkCardLimitNew2(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;

        String memberId =commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String cardNo = commonValidatorVO.getCardDetailsVO().getCardNum();
        String firstSixCCNum = cardNo.substring(0, 6);
        String lastFourCcNum = cardNo.substring(cardNo.length() - 4);
        try
        {
            transactionLogger.debug("Load member card limits ");
            int member_daily_card_limit = 0;
            int member_weekly_card_limit = 0;
            int member_monthly_card_limit = 0;
            String daily_card_limit_check="";
            String monthly_card_limit_check="";
            String isCardStorageRequired="";
            String addQuery="";
            String issuerBank="";
            dbConn = Database.getConnection();

            boolean isDailyCardLimitOver = false;
            boolean isMonthlyCardLimitOver = false;
            boolean isWeeklyCardLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String memberAmountLimits = "select daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_limit_check,monthly_card_limit_check,mc.isCardStorageRequired from members AS m,merchant_configuration AS mc WHERE m.memberid=mc.`memberid` AND m.memberid= ?";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                member_daily_card_limit = resultSet.getInt("daily_card_limit");
                member_weekly_card_limit = resultSet.getInt("weekly_card_limit");
                member_monthly_card_limit = resultSet.getInt("monthly_card_limit");
                daily_card_limit_check= resultSet.getString("daily_card_limit_check");
                monthly_card_limit_check= resultSet.getString("monthly_card_limit_check");
                isCardStorageRequired= resultSet.getString("isCardStorageRequired");
            }
            transactionLogger.error("Member Daily Card limits " + member_daily_card_limit + " Member Weekly Card limits " + member_weekly_card_limit + " Member Monthly Card limits " + member_monthly_card_limit);
            transactionLogger.error("isCardStorageRequired------>" + isCardStorageRequired);
            if("N".equalsIgnoreCase(isCardStorageRequired)){

                BinVerificationManager binVerificationManager=new BinVerificationManager();
                BinResponseVO binResponseVO=binVerificationManager.getBinDetailsFromFirstSix(firstSixCCNum);
                issuerBank= binResponseVO.getBank();
                addQuery="and issuing_bank='"+issuerBank+"'";
                transactionLogger.error("inside if card store N case------>" +issuerBank );
            }
            else{
                addQuery= "and first_six = "+firstSixCCNum;
            }
            String countCardQuery = "SELECT COUNT(icicitransid) FROM bin_details WHERE last_four=? AND merchant_id=? AND isSuccessful='Y' AND trans_dtstamp >? "+addQuery;
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            //calendar.set(Calendar.AM_PM, Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("day------>" + calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("First day of week------>" + calendar.getTime());

            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());

            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

            int daily_card_total = 0;
            int weekly_card_total = 0;
            int monthly_card_total = 0;

            PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);

            //countCardPreparedStatement.setString(1, firstSixCCNum);
            countCardPreparedStatement.setString(1, lastFourCcNum);
            countCardPreparedStatement.setInt(2, Integer.parseInt(memberId));
            countCardPreparedStatement.setLong(3, todaysStartTimeInSecs);
            if("Y".equalsIgnoreCase(daily_card_limit_check))
            {
                transactionLogger.error("Daily Card Limit-------->" +daily_card_limit_check+" query-->" + countCardPreparedStatement.toString());
                ResultSet totalAmountResultSet = countCardPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    daily_card_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Daily Total : " + daily_card_total);
                totalAmountResultSet.close();
            }
           /* countCardPreparedStatement.setLong(4, weeklyStartTimeInSecs);
            transactionLogger.error("Weekly Card Limit-------->"+countCardPreparedStatement.toString());

            ResultSet  totalAmountResultSet = countCardPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                weekly_card_total = totalAmountResultSet.getInt(1);
            }
            transactionLogger.error("Weekly Total : " + weekly_card_total);
            totalAmountResultSet.close();
*/
            countCardPreparedStatement.setLong(3, monthsStartTimeInSecs);
            if("Y".equalsIgnoreCase(monthly_card_limit_check))
            {
                transactionLogger.error("Monthly Card Limit-------->"+monthly_card_limit_check+" query-->" + countCardPreparedStatement.toString());

                ResultSet  totalAmountResultSet = countCardPreparedStatement.executeQuery();

                if (totalAmountResultSet.next())
                {
                    monthly_card_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Monthly Card Total : " + monthly_card_total);
                totalAmountResultSet.close();
            }


            if ("Y".equalsIgnoreCase(daily_card_limit_check)&&member_daily_card_limit <= daily_card_total)
            {
                isDailyCardLimitOver = true;
            }
          /*  if (member_weekly_card_limit <= weekly_card_total)
            {
                isWeeklyCardLimitOver = true;
            }*/
            if ("Y".equalsIgnoreCase(monthly_card_limit_check)&&member_monthly_card_limit <= monthly_card_total)
            {
                isMonthlyCardLimitOver = true;
            }

            if (isDailyCardLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_DAILY_CARDLIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            /*if (isWeeklyCardLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_WEEKLY_CARDLIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }*/
            if (isMonthlyCardLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_MONTHLY_CARDLIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }

    public boolean checkCardTransLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;

        String memberId =commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String amount =commonValidatorVO.getTransDetailsVO().getAmount();
        String cardNo = commonValidatorVO.getCardDetailsVO().getCardNum();
        String firstSixCCNum = cardNo.substring(0, 6);
        String lastFourCcNum = cardNo.substring(cardNo.length() - 4);
        try
        {
            transactionLogger.debug("Load member card limits ");
            transactionLogger.debug("Card Limit----" + commonValidatorVO.getMerchantDetailsVO().getCardCheckLimit());
            int member_daily_card_Trans_limit = 0;
            int member_weekly_card_Trans_limit = 0;
            int member_monthly_card_Trans_limit = 0;
            dbConn = Database.getConnection();

            boolean isDailyCardTransLimitOver = false;
            boolean isMonthlyCardTransLimitOver = false;
            boolean isWeeklyCardTransLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String memberCardAmountLimits = "select daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit from members where memberid= ? ";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberCardAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                member_daily_card_Trans_limit = resultSet.getInt("daily_card_amount_limit");
                member_weekly_card_Trans_limit = resultSet.getInt("weekly_card_amount_limit");
                member_monthly_card_Trans_limit = resultSet.getInt("monthly_card_amount_limit");
            }
            transactionLogger.error("Member Daily Card Amount limits " + member_daily_card_Trans_limit + " Member Weekly Card Amount limits " + member_weekly_card_Trans_limit + " Member Monthly Card Amount limits " + member_monthly_card_Trans_limit);

            String countCardQuery = "select sum(tt.amount) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.toid = ? and tt.dtstamp > ?  and tt.status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent','settled','reversed','chargeback','markedforreversal','authcancelled')";
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            //calendar.set(Calendar.AM_PM, Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("day------>" + calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("First day of week------>" + calendar.getTime());

            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

            float daily_card_amount_total = 0;
            float weekly_card_amount_total = 0;
            float monthly_card_amount_total = 0;

            PreparedStatement countCardAmountPreparedStatement = dbConn.prepareStatement(countCardQuery);

            countCardAmountPreparedStatement.setString(1, firstSixCCNum);
            countCardAmountPreparedStatement.setString(2, lastFourCcNum);
            countCardAmountPreparedStatement.setInt(3, Integer.parseInt(memberId));
            countCardAmountPreparedStatement.setLong(4, todaysStartTimeInSecs);
            transactionLogger.error("Daily Card Amount Limit-------->"+countCardAmountPreparedStatement.toString());
            ResultSet totalAmountResultSet = countCardAmountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                daily_card_amount_total = totalAmountResultSet.getFloat(1);
            }
            daily_card_amount_total += Float.parseFloat(amount);
            transactionLogger.error("Daily Card Maount Total : " + daily_card_amount_total);
            totalAmountResultSet.close();

            countCardAmountPreparedStatement.setLong(4, weeklyStartTimeInSecs);
            transactionLogger.error("Weekly Card Amount Limit-------->" + countCardAmountPreparedStatement.toString());
            totalAmountResultSet = countCardAmountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                weekly_card_amount_total = totalAmountResultSet.getInt(1);
            }
            weekly_card_amount_total += Float.parseFloat(amount);
            transactionLogger.error("Weekly Card Amount Total : " + weekly_card_amount_total);
            totalAmountResultSet.close();

            countCardAmountPreparedStatement.setLong(4, monthsStartTimeInSecs);
            transactionLogger.error("Monthly Card Amount Limit-------->"+countCardAmountPreparedStatement.toString());
            totalAmountResultSet = countCardAmountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                monthly_card_amount_total = totalAmountResultSet.getInt(1);
            }
            monthly_card_amount_total += Float.parseFloat(amount);
            transactionLogger.error("Monthly Card Amount Total : " + monthly_card_amount_total);
            totalAmountResultSet.close();

            if (member_daily_card_Trans_limit < daily_card_amount_total)
            {
                isDailyCardTransLimitOver = true;
            }
            if (member_weekly_card_Trans_limit < weekly_card_amount_total)
            {
                isWeeklyCardTransLimitOver = true;
            }
            if (member_monthly_card_Trans_limit < monthly_card_amount_total)
            {
                isMonthlyCardTransLimitOver = true;
            }

            if (isDailyCardTransLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_DAILY_CARD_AMT_LIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isWeeklyCardTransLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_WEEKLY_CARD_AMT_LIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyCardTransLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_MONTHLY_CARD_AMT_LIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }

    public boolean checkPayoutAmountLimitMemberLevel(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId =commonValidatorVO.getMerchantDetailsVO().getMemberId();
        try
        {
            logger.debug("Load member payout amount limits ");
            int daily_payout_amount_limit = 0;
            int weekly_payout_amount_limit = 0;
            int monthly_payout_amount_limit = 0;
            dbConn = Database.getConnection();

            boolean isDailyAmountLimitOver = false;
            boolean isWeeklyAmountLimitOver = false;
            boolean isMonthlyAmountLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String memberCardAmountLimits = "select daily_payout_amount_limit,weekly_payout_amount_limit,monthly_payout_amount_limit from merchant_configuration where memberid= ?";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberCardAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                daily_payout_amount_limit = resultSet.getInt("daily_payout_amount_limit");
                weekly_payout_amount_limit = resultSet.getInt("weekly_payout_amount_limit");
                monthly_payout_amount_limit = resultSet.getInt("monthly_payout_amount_limit");
            }
            transactionLogger.error("Member Daily Payout Amount limits " + daily_payout_amount_limit + " Member Weekly Payout Amount limits " + weekly_payout_amount_limit + " Member Monthly Payout Amount limits " + monthly_payout_amount_limit);

            String totalAmountQuery = "select sum(payoutamount) from transaction_common where toid = ? and dtstamp > ?  and status ='payoutsuccessful'";
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            //calendar.set(Calendar.AM_PM,Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("day------>" + calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("First day of week------>" + calendar.getTime());

            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

            float payout_transaction_daily_amount = 0;
            float payout_transaction_monthly_amount = 0;
            float payout_transaction_weekly_amount = 0;


            PreparedStatement totalAmountPreparedStatement = dbConn.prepareStatement(totalAmountQuery);

            totalAmountPreparedStatement.setInt(1, Integer.parseInt(memberId));
            totalAmountPreparedStatement.setLong(2, todaysStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for member Query Daily---" + totalAmountPreparedStatement);
            ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                payout_transaction_daily_amount = totalAmountResultSet.getFloat(1);
            }
            transactionLogger.error("Daily Payout Transaction Amount : " + payout_transaction_daily_amount);
            totalAmountResultSet.close();
            totalAmountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for member Query Monthly---" + totalAmountPreparedStatement);
            totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                payout_transaction_monthly_amount = totalAmountResultSet.getFloat(1);
            }
            transactionLogger.error("Monthly Payout Transaction Amount : " + payout_transaction_monthly_amount);
            totalAmountResultSet.close();
            //weekly Amount limit time
            totalAmountPreparedStatement.setLong(2,weeklyStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for member Query weekly---"+totalAmountPreparedStatement);
            totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
            if(totalAmountResultSet.next())
            {
                payout_transaction_weekly_amount = totalAmountResultSet.getFloat(1);
            }
            transactionLogger.error("Weekly Payout Transaction Amount : " + payout_transaction_weekly_amount);
            totalAmountResultSet.close();


            payout_transaction_daily_amount += Float.parseFloat(amount);
            payout_transaction_monthly_amount += Float.parseFloat(amount);
            payout_transaction_weekly_amount += Float.parseFloat(amount);

            transactionLogger.error("Daily Payout Transaction Amount after todays amount : " + payout_transaction_daily_amount + " Weekly Payout Transaction Amount after current transaction amount : " + payout_transaction_weekly_amount + " Monthly Payout Transaction Amount after current transaction amount : " + payout_transaction_monthly_amount);

            if (daily_payout_amount_limit < payout_transaction_daily_amount)
            {
                isDailyAmountLimitOver = true;
            }
            if (weekly_payout_amount_limit < payout_transaction_weekly_amount)
            {
                isWeeklyAmountLimitOver = true;
            }
            if (monthly_payout_amount_limit < payout_transaction_monthly_amount)
            {
                isMonthlyAmountLimitOver = true;
            }

            if (isDailyAmountLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_DAILY_PAYOUT_AMT_LIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPayoutAmountLimitMemberLevel()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isWeeklyAmountLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_WEEKLY_PAYOUT_AMT_LIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPayoutAmountLimitMemberLevel()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyAmountLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_MONTHLY_PAYOUT_AMT_LIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPayoutAmountLimitMemberLevel()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking payout limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking payout limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }

    public boolean checkAmountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId =commonValidatorVO.getMerchantDetailsVO().getMemberId();
        try
        {
            logger.debug("Load member card limits ");
            int dialy_amount_limit = 0;
            int weekly_amount_limit = 0;
            int monthly_amount_limit = 0;
            dbConn = Database.getConnection();

            boolean isDailyAmountLimitOver = false;
            boolean isWeeklyAmountLimitOver = false;
            boolean isMonthlyAmountLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String memberCardAmountLimits = "select daily_amount_limit,weekly_amount_limit,monthly_amount_limit from members where memberid= ?";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberCardAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                dialy_amount_limit = resultSet.getInt("daily_amount_limit");
                weekly_amount_limit = resultSet.getInt("weekly_amount_limit");
                monthly_amount_limit = resultSet.getInt("monthly_amount_limit");
            }
            transactionLogger.error("Member Daily Amount limits " + dialy_amount_limit + " Member Weekly  Amount limits " + weekly_amount_limit + " Member Monthly Amount limits " + monthly_amount_limit);

            String totalAmountQuery = "select sum(amount) from transaction_common where toid = ? and dtstamp > ?  and status in ('authsuccessful','capturesuccess','settled','reversed','markedforreversal')";
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            //calendar.set(Calendar.AM_PM,Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("day------>" + calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("First day of week------>" + calendar.getTime());

            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

            float transaction_daily_amount = 0;
            float transaction_monthly_amount = 0;
            float transaction_weekly_amount = 0;


            PreparedStatement totalAmountPreparedStatement = dbConn.prepareStatement(totalAmountQuery);

            totalAmountPreparedStatement.setInt(1, Integer.parseInt(memberId));
            totalAmountPreparedStatement.setLong(2, todaysStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for member Query Daily---" + totalAmountPreparedStatement);
            ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                transaction_daily_amount = totalAmountResultSet.getFloat(1);
            }
            transactionLogger.error("Daily Transaction Amount : " + transaction_daily_amount);
            totalAmountResultSet.close();
            totalAmountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for member Query Monthly---" + totalAmountPreparedStatement);
            totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                transaction_monthly_amount = totalAmountResultSet.getFloat(1);
            }
            transactionLogger.error("Monthly Transaction Amount : " + transaction_monthly_amount);
            totalAmountResultSet.close();
            //weekly Amount limit time
            totalAmountPreparedStatement.setLong(2,weeklyStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for member Query weekly---"+totalAmountPreparedStatement);
            totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
            if(totalAmountResultSet.next())
            {
                transaction_weekly_amount = totalAmountResultSet.getFloat(1);
            }
            transactionLogger.error("Weeklyly Transaction Amount : " + transaction_weekly_amount);
            totalAmountResultSet.close();


            transaction_daily_amount += Float.parseFloat(amount);
            transaction_monthly_amount += Float.parseFloat(amount);
            transaction_weekly_amount += Float.parseFloat(amount);

            transactionLogger.error("Daily Transaction Amount after todays amount : " + transaction_daily_amount + " Weekly Transaction Amount after current transaction amount : " + transaction_weekly_amount + " Monthly Transaction Amount after current transaction amount : " + transaction_monthly_amount);


            if (dialy_amount_limit < transaction_daily_amount)
            {
                isDailyAmountLimitOver = true;
            }
            if (weekly_amount_limit < transaction_weekly_amount)
            {
                isWeeklyAmountLimitOver = true;
            }
            if (monthly_amount_limit < transaction_monthly_amount)
            {
                isMonthlyAmountLimitOver = true;
            }

            if (isDailyAmountLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_DAILY_AMT_LIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isWeeklyAmountLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_WEEKLY_AMT_LIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyAmountLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_MONTHLY_AMT_LIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }
    public boolean checkCardLimitAccountLevel(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        boolean isLimitAvailable = true;
        ErrorCodeListVO errorCodeListVO = null;
        Functions functions=new Functions();

        String accountId = commonValidatorVO.getTerminalVO().getAccountId();

        String cardNo = commonValidatorVO.getCardDetailsVO().getCardNum();
        String firstSixCCNum = cardNo.substring(0, 6);
        String lastFourCcNum = cardNo.substring(cardNo.length() - 4);
        try
        {
            transactionLogger.debug("Load Account card limits ");
            boolean isDailyCardLimitOver = false;
            boolean isMonthlyCardLimitOver = false;
            boolean isWeeklyCardLimitOver = false;
            String currency="";

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
            dbConn = Database.getConnection();
            transactionLogger.debug("Member Account ID" + accountId);

            transactionLogger.debug("Load Account card limits ");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
            int account_daily_limit = account.getDailyCardLimit();
            int account_weekly_limit = account.getWeeklyCardLimit();
            int account_monthly_limit = account.getMonthlyCardLimit();

            String daily_card_limit_check=account.getDaily_card_limit_check();
            String weekly_card_limit_check=account.getWeekly_card_limit_check();
            String monthly_card_limit_check=account.getMonthly_card_limit_check();

            String cardCheckLimit=account.getCardCheckLimit();
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
                currency=commonValidatorVO.getTransDetailsVO().getCurrency();
            else
                currency=account.getCurrency();

            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("day------>" + calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("First day of week------>" + calendar.getTime());

            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

            int daily_card_total = 0;
            int weekly_card_total = 0;
            int monthly_card_total = 0;

            String countCardQuery = "";
            if("account_Level".equalsIgnoreCase(cardCheckLimit))
            {
                countCardQuery = "select count(tt.trackingid) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.accountid = ? and tt.currency=? and bd.isSuccessful='Y' and tt.dtstamp >? and tt.parentTrackingid IS NULL";
            }
            else if("mid_Level".equalsIgnoreCase(cardCheckLimit))
            {
                countCardQuery = "select count(tt.trackingid) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.fromid=? and bd.isSuccessful='Y' and tt.dtstamp >? and tt.parentTrackingid IS NULL";
            }
            transactionLogger.error(" Account Daily Card limits-- " + account_daily_limit + " Account Weekly Card limits-- " + account_weekly_limit + " Account Monthly Card limits-- " + account_monthly_limit);

            PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);
            int psCounter = 1;
            countCardPreparedStatement.setString(psCounter, firstSixCCNum);
            psCounter++;
            countCardPreparedStatement.setString(psCounter, lastFourCcNum);
            psCounter++;
            if("account_Level".equalsIgnoreCase(cardCheckLimit))
            {
                countCardPreparedStatement.setString(psCounter, accountId);
                psCounter++;
                countCardPreparedStatement.setString(psCounter, currency);//currency
                psCounter++;
            }
            if("mid_Level".equalsIgnoreCase(cardCheckLimit))
            {
                countCardPreparedStatement.setString(psCounter, account.getMerchantId());//MID
                psCounter++;
            }

            countCardPreparedStatement.setLong(psCounter, todaysStartTimeInSecs);
            ResultSet totalAmountResultSet =null;
            if("Y".equalsIgnoreCase(daily_card_limit_check))
            {
                transactionLogger.error("account_Level or mid_Level daily_card_limit_check Y  query-->" + countCardPreparedStatement.toString());
                totalAmountResultSet = countCardPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    daily_card_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Daily Total : " + daily_card_total);
                totalAmountResultSet.close();
            }

            //weekly
            countCardPreparedStatement.setLong(psCounter, weeklyStartTimeInSecs);

            if("Y".equalsIgnoreCase(weekly_card_limit_check))
            {
                transactionLogger.error("account_Level or mid_Level weekly_card_limit_check Y  query---->" + countCardPreparedStatement.toString());
                totalAmountResultSet = countCardPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    weekly_card_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Weekly Total : " + weekly_card_total);
                totalAmountResultSet.close();
            }
            //monthly
            countCardPreparedStatement.setLong(psCounter, monthsStartTimeInSecs);

            if("Y".equalsIgnoreCase(monthly_card_limit_check))
            {
                transactionLogger.error("account_Level or mid_Level monthly_card_limit_check Y  query---->" + countCardPreparedStatement.toString());
                totalAmountResultSet = countCardPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    monthly_card_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Monthly Card Total : " + monthly_card_total);
                totalAmountResultSet.close();
            }
            transactionLogger.debug("Checking Limits");

            if ("Y".equalsIgnoreCase(daily_card_limit_check)&&daily_card_total >= account_daily_limit)
            {
                isDailyCardLimitOver = true;
            }
            if ("Y".equalsIgnoreCase(weekly_card_limit_check)&&weekly_card_total >= account_weekly_limit)
            {
                isWeeklyCardLimitOver = true;
            }
            if ("Y".equalsIgnoreCase(monthly_card_limit_check)&&monthly_card_total >= account_monthly_limit)
            {
                isMonthlyCardLimitOver = true;
            }
            if(isDailyCardLimitOver || isWeeklyCardLimitOver || isMonthlyCardLimitOver)
            {
                isLimitAvailable=false;
            }
            if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()))
            {
                if (isDailyCardLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNT_DAILY_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isWeeklyCardLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNT_WEEKLY_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isMonthlyCardLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNT_MONTHLY_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }else
            {
                if (isDailyCardLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_ACCOUNT_DAILY_CARDLIMIT_EXCEEDED.toString());
                else if(isWeeklyCardLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_ACCOUNT_WEEKLY_CARDLIMIT_EXCEEDED.toString());
                else if (isMonthlyCardLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_ACCOUNT_MONTHLY_CARDLIMIT_EXCEEDED.toString());
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return isLimitAvailable;
    }
    public boolean checkCardAmountLimitAccountLevel(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        boolean isLimitAvailable = true;
        ErrorCodeListVO errorCodeListVO = null;
        Functions functions=new Functions();
        HashMap hash=new HashMap();

        String accountId = commonValidatorVO.getTerminalVO().getAccountId();
        String amount=commonValidatorVO.getTransDetailsVO().getAmount();
        String currency="";
        String transCurrency="";

        String cardNo = commonValidatorVO.getCardDetailsVO().getCardNum();
        String firstSixCCNum = cardNo.substring(0, 6);
        String lastFourCcNum = cardNo.substring(cardNo.length() - 4);
        try
        {
            logger.debug("Load Account card amount limits ");
            boolean isDailyCardAmountLimitOver = false;
            boolean isMonthlyCardAmountLimitOver = false;
            boolean isWeeklyCardAmountLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
            dbConn = Database.getConnection();
            logger.debug("Member Account ID" + accountId);

            logger.debug("Load Account card limits ");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
            float account_daily_amount_limit = account.getDailyCardAmountLimit();
            float account_weekly_amount_limit = account.getWeeklyCardAmountLimit();
            float account_monthly_amount_limit = account.getMonthlyCardAmountLimit();
            float baseCurrencyRate=0;
            float transCurrencyRate=0;

            String cardAmountLimitCheck=account.getCardAmountLimitCheckAcc();
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
                currency=commonValidatorVO.getTransDetailsVO().getCurrency();
            else
                currency=account.getCurrency();
            baseCurrencyRate=Float.parseFloat(RB.getString(currency.toUpperCase()));

            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("day------>" + calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            transactionLogger.debug("First day of week------>" + calendar.getTime());

            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

            float daily_card_amount_total = 0;
            float weekly_card_amount_total = 0;
            float monthly_card_amount_total = 0;

            String countCardQuery = "";
            if("account_Level".equalsIgnoreCase(cardAmountLimitCheck))
            {
                countCardQuery = "select sum(tt.amount) as amount from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.accountid = ? and tt.currency=? and bd.isSuccessful='Y' and tt.dtstamp >? and tt.parentTrackingid IS NULL";
            }
            else if("mid_Level".equalsIgnoreCase(cardAmountLimitCheck))
            {
                countCardQuery = "SELECT SUM(tt.amount) as amount,tt.currency FROM bin_details bd, transaction_common tt WHERE tt.trackingid=bd.icicitransid AND bd.first_six =? AND bd.last_four =? AND tt.fromid=? AND bd.isSuccessful='Y' AND tt.dtstamp >? AND tt.parentTrackingid IS NULL GROUP BY tt.currency";
            }
            transactionLogger.error(" Account Daily Card Amount limits-- " + account_daily_amount_limit + " Account Weekly Card Amount limits-- " + account_weekly_amount_limit + " Account Monthly Card Amount limits-- " + account_monthly_amount_limit);

            PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);
            int psCounter = 1;
            countCardPreparedStatement.setString(psCounter, firstSixCCNum);
            psCounter++;
            countCardPreparedStatement.setString(psCounter, lastFourCcNum);
            psCounter++;
            if("account_Level".equalsIgnoreCase(cardAmountLimitCheck))
            {
                countCardPreparedStatement.setString(psCounter, accountId);
                psCounter++;
                countCardPreparedStatement.setString(psCounter, currency);//currency
                psCounter++;
            }
            if("mid_Level".equalsIgnoreCase(cardAmountLimitCheck))
            {
                countCardPreparedStatement.setString(psCounter, account.getMerchantId());//MID
                psCounter++;
            }

            countCardPreparedStatement.setLong(psCounter, todaysStartTimeInSecs);
            transactionLogger.error("checkCardAmountLimitAccountLevel---->"+countCardPreparedStatement.toString());
            ResultSet totalAmountResultSet = countCardPreparedStatement.executeQuery();
            if(totalAmountResultSet != null)
            {
                hash = Database.getHashMapFromResultSetForTransactionEntry(totalAmountResultSet);
                for (int i = 1; i <= hash.size(); i++)
                {
                    HashMap innerHash = new HashMap();
                    innerHash = (HashMap) hash.get("" + i);
                    if(functions.isValueNull((String) innerHash.get("amount")))
                    {
                        if("mid_Level".equalsIgnoreCase(cardAmountLimitCheck))
                        {
                            transCurrency = (String) innerHash.get("currency");
                            transCurrencyRate=Float.parseFloat(RB.getString(transCurrency));
                            daily_card_amount_total += Float.parseFloat((String) innerHash.get("amount"))*((baseCurrencyRate/transCurrencyRate));
                        }
                        else
                        {
                            daily_card_amount_total += Float.parseFloat((String) innerHash.get("amount"));
                        }
                    }
                }
                transactionLogger.error("Daily Total : " + daily_card_amount_total);
                totalAmountResultSet.close();
            }
            countCardPreparedStatement.setLong(psCounter, weeklyStartTimeInSecs);
            transactionLogger.error("checkCardAmountLimitAccountLevel---->" + countCardPreparedStatement.toString());
            totalAmountResultSet=countCardPreparedStatement.executeQuery();
            if(totalAmountResultSet != null)
            {
                hash = Database.getHashMapFromResultSetForTransactionEntry(totalAmountResultSet);
                for (int i = 1; i <= hash.size(); i++)
                {
                    HashMap innerHash = new HashMap();
                    innerHash = (HashMap) hash.get("" + i);
                    if(functions.isValueNull((String) innerHash.get("amount")))
                    {
                        if("mid_Level".equalsIgnoreCase(cardAmountLimitCheck))
                        {
                            transCurrency = (String) innerHash.get("currency");
                            transCurrencyRate=Float.parseFloat(RB.getString(transCurrency));
                            weekly_card_amount_total += Float.parseFloat((String) innerHash.get("amount"))*(baseCurrencyRate/transCurrencyRate);
                        }
                        else
                        {
                            weekly_card_amount_total += Float.parseFloat((String) innerHash.get("amount"));
                        }
                    }
                }
                transactionLogger.error("Weekly Total : " + weekly_card_amount_total);
                totalAmountResultSet.close();
            }
            countCardPreparedStatement.setLong(psCounter, monthsStartTimeInSecs);
            transactionLogger.error("checkCardAmountLimitAccountLevel---->"+countCardPreparedStatement.toString());
            totalAmountResultSet=countCardPreparedStatement.executeQuery();
            if(totalAmountResultSet != null)
            {
                hash = Database.getHashMapFromResultSetForTransactionEntry(totalAmountResultSet);
                for (int i = 1; i <= hash.size(); i++)
                {
                    HashMap innerHash = new HashMap();
                    innerHash = (HashMap) hash.get("" + i);
                    if(functions.isValueNull((String) innerHash.get("amount")))
                    {
                        if("mid_Level".equalsIgnoreCase(cardAmountLimitCheck))
                        {
                            transCurrency = (String) innerHash.get("currency");
                            transCurrencyRate=Float.parseFloat(RB.getString(transCurrency));
                            monthly_card_amount_total += Float.parseFloat((String) innerHash.get("amount"))*(baseCurrencyRate/transCurrencyRate);
                        }
                        else
                        {
                            monthly_card_amount_total += Float.parseFloat((String) innerHash.get("amount"));
                        }
                    }
                }
                transactionLogger.error("Monthly Total : " + monthly_card_amount_total);
                totalAmountResultSet.close();
            }

            daily_card_amount_total += Float.parseFloat(amount);
            weekly_card_amount_total += Float.parseFloat(amount);
            monthly_card_amount_total += Float.parseFloat(amount);

            transactionLogger.error("Daily Card Total including today's Amount : " + daily_card_amount_total + " Weekly Card Total including today's Amount : " + weekly_card_amount_total + " Monthly Card Total including today's Amount : " + monthly_card_amount_total);

            transactionLogger.debug("Checking Limits");

            if (daily_card_amount_total > account_daily_amount_limit)
            {
                isDailyCardAmountLimitOver = true;
            }
            if (weekly_card_amount_total > account_weekly_amount_limit)
            {
                isWeeklyCardAmountLimitOver = true;
            }
            if (monthly_card_amount_total > account_monthly_amount_limit)
            {
                isMonthlyCardAmountLimitOver = true;
            }
            if(isDailyCardAmountLimitOver || isWeeklyCardAmountLimitOver || isMonthlyCardAmountLimitOver)
            {
                isLimitAvailable=false;
            }
            if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()))
            {
                if (isDailyCardAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNT_DAILY_CARD_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isWeeklyCardAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNT_WEEKLY_CARD_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isMonthlyCardAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNT_MONTHLY_CARD_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }else {
                if(isDailyCardAmountLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_ACCOUNT_DAILY_CARD_AMT_LIMIT_EXCEEDED.toString());
                else if (isWeeklyCardAmountLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_ACCOUNT_WEEKLY_CARD_AMT_LIMIT_EXCEEDED.toString());
                else if (isMonthlyCardAmountLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_ACCOUNT_MONTHLY_CARD_AMT_LIMIT_EXCEEDED.toString());
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return isLimitAvailable;
    }
    public boolean checkAmountLimitAccountLevel(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        boolean isLimitAvailable = true;
        ErrorCodeListVO errorCodeListVO = null;
        Functions functions=new Functions();
        HashMap hash=new HashMap();

        String accountId = commonValidatorVO.getTerminalVO().getAccountId();
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        float txnAmount = Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());
        String currency="";
        String transCurrency="";
        try
        {
            transactionLogger.debug("Load Acount amount limits ");
            boolean isDailyAmountLimitOver = false;
            boolean isMonthlyAmountLimitOver = false;
            boolean isWeeklyAmountLimitOver = false;
            boolean maxAmountLimitExceeded = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
            dbConn = Database.getConnection();
            transactionLogger.debug("Member Account ID" + accountId);

            transactionLogger.debug("Load Account card limits ");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
            String getGatewayLimitresetTime=GatewayAccountService.getGatewayLimitresetTime(accountId);
            String []limitTime=getGatewayLimitresetTime.split(":");
            int hour= Integer.parseInt(limitTime[0]);
            int min= Integer.parseInt(limitTime[1]);
            int sec= Integer.parseInt(limitTime[2]);
            float account_daily_amount_limit = account.getDailyAmountLimit();
            float account_weekly_amount_limit = account.getWeeklyAmountLimit();
            float account_monthly_amount_limit = account.getMonthlyAmountLimit();
            float maxTxnAmount = commonValidatorVO.getTerminalVO().getMax_transaction_amount();
            float transCurrencyRate=0;
            float baseCurrencyRate=0;
            String weekly_Account_Amount_Limit=account.getWeekly_Account_Amount_Limit();
            String monthly_Account_Amount_Limit=account.getMonthly_Account_Amount_Limit();
            String daily_Account_Amount_Limit=account.getDaily_Account_Amount_Limit();
            String amountLimitCheck=account.getAmountLimitCheckAcc();
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
                currency=commonValidatorVO.getTransDetailsVO().getCurrency();
            else
                currency=account.getCurrency();
            baseCurrencyRate=Float.parseFloat(RB.getString(currency.toUpperCase()));

            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            Calendar weekCalender = Calendar.getInstance();
            //For Day
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, sec);

            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            long todayCurrentTimeInSec = new Date().getTime() / 1000;

            transactionLogger.debug("start day--->" + calendar.getTime() + "---" + todaysStartTimeInSecs);
            transactionLogger.debug("current day--->" + calendar.getTime() + "---" + todayCurrentTimeInSec);

            if(todaysStartTimeInSecs>todayCurrentTimeInSec)
            {
                transactionLogger.debug("Removing 1day from date---" + accountId);
                calendar.add(Calendar.DATE, -1);
                todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            }

            //For Week
            weekCalender.set(Calendar.HOUR_OF_DAY, 0);
            weekCalender.set(Calendar.MINUTE, 0);
            weekCalender.set(Calendar.SECOND, 0);
            weekCalender.set(Calendar.DAY_OF_WEEK, weekCalender.getFirstDayOfWeek());
            long weeklyStartTimeInSecs = weekCalender.getTimeInMillis() / 1000;
            transactionLogger.debug("First day of week------>" + weekCalender.getTime());

            //For Month
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

            float daily_amount_total = 0;
            float weekly_amount_total = 0;
            float monthly_amount_total = 0;

            String countCardQuery = "";
            if("account_Level".equalsIgnoreCase(amountLimitCheck))
            {
                countCardQuery = "select sum(tt.amount) as amount from transaction_common tt where tt.accountid = ? and tt.currency=? and tt.status  in ('capturesuccess','authsuccessful','reversed','settled','markedforreversal') and tt.dtstamp >? and tt.parentTrackingid IS NULL";
            }
            else if("mid_Level".equalsIgnoreCase(amountLimitCheck))
            {
                countCardQuery = "select SUM(tt.amount) as amount,tt.currency from transaction_common tt where tt.fromid=? and tt.status  in ('capturesuccess','authsuccessful','reversed','settled','markedforreversal') and tt.dtstamp >? and tt.parentTrackingid IS NULL group by tt.currency";
            }
            transactionLogger.error(" Account Daily Amount limits-- " + account_daily_amount_limit + " Account Weekly Amount limits-- " + account_weekly_amount_limit + " Account Monthly Amount limits-- " + account_monthly_amount_limit);

            PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);
            int psCounter = 1;
            if("account_Level".equalsIgnoreCase(amountLimitCheck))
            {
                countCardPreparedStatement.setString(psCounter, accountId);
                psCounter++;
                countCardPreparedStatement.setString(psCounter, currency);//currency
                psCounter++;
            }
            else if("mid_Level".equalsIgnoreCase(amountLimitCheck))
            {
                countCardPreparedStatement.setString(psCounter, account.getMerchantId());//MID
                psCounter++;
            }
            countCardPreparedStatement.setLong(psCounter, todaysStartTimeInSecs);

            ResultSet totalAmountResultSet = null;

            transactionLogger.error(" account_Level daily condition---->"+daily_Account_Amount_Limit + " account_Level weekly condition---->"+weekly_Account_Amount_Limit + " account_Level monthly condition---->"+monthly_Account_Amount_Limit);

            if(("account_Level".equalsIgnoreCase(amountLimitCheck)||"mid_Level".equalsIgnoreCase(amountLimitCheck))&&"Y".equalsIgnoreCase(daily_Account_Amount_Limit))
            {   transactionLogger.error("inside if account_Level or mid_Level daily condition---->"+daily_Account_Amount_Limit);
                transactionLogger.error("checkAmountLimitAccountLevel---->"+countCardPreparedStatement.toString());
                totalAmountResultSet = countCardPreparedStatement.executeQuery();
            }

            if(totalAmountResultSet != null)
            {
                hash = Database.getHashMapFromResultSetForTransactionEntry(totalAmountResultSet);
                for (int i = 1; i <= hash.size(); i++)
                {
                    HashMap innerHash = new HashMap();
                    innerHash = (HashMap) hash.get("" + i);
                    if(functions.isValueNull((String) innerHash.get("amount")))
                    {
                        if("mid_Level".equalsIgnoreCase(amountLimitCheck))
                        {
                            transCurrency = (String) innerHash.get("currency");
                            transCurrencyRate=Float.parseFloat(RB.getString(transCurrency));
                            daily_amount_total += Float.parseFloat((String) innerHash.get("amount"))*(baseCurrencyRate/transCurrencyRate);
                        }
                        else
                        {
                            daily_amount_total += Float.parseFloat((String) innerHash.get("amount"));
                        }
                    }
                }
                transactionLogger.error("Daily Amount Total : " + daily_amount_total);
                totalAmountResultSet.close();
                totalAmountResultSet=null;
            }
            countCardPreparedStatement.setLong(psCounter, weeklyStartTimeInSecs);
            if(("account_Level".equalsIgnoreCase(amountLimitCheck)||"mid_Level".equalsIgnoreCase(amountLimitCheck))&&"Y".equalsIgnoreCase(weekly_Account_Amount_Limit))
            {   transactionLogger.error("inside if account_Level or mid level weekly condition---->"+weekly_Account_Amount_Limit);
                transactionLogger.error("checkAmountLimitAccountLevel---->"+countCardPreparedStatement.toString());
                totalAmountResultSet = countCardPreparedStatement.executeQuery();
            }

            if(totalAmountResultSet != null)
            {
                hash = Database.getHashMapFromResultSetForTransactionEntry(totalAmountResultSet);
                for (int i = 1; i <= hash.size(); i++)
                {
                    HashMap innerHash = new HashMap();
                    innerHash = (HashMap) hash.get("" + i);
                    if(functions.isValueNull((String) innerHash.get("amount")))
                    {
                        if("mid_Level".equalsIgnoreCase(amountLimitCheck))
                        {
                            transCurrency = (String) innerHash.get("currency");
                            transCurrencyRate=Float.parseFloat(RB.getString(transCurrency));
                            weekly_amount_total += Float.parseFloat((String) innerHash.get("amount"))*(baseCurrencyRate/transCurrencyRate);
                        }
                        else
                        {
                            weekly_amount_total += Float.parseFloat((String) innerHash.get("amount"));
                        }
                    }
                }
                transactionLogger.error("Weekly Amount Total : " + weekly_amount_total);
                totalAmountResultSet.close();
                totalAmountResultSet=null;
            }
            countCardPreparedStatement.setLong(psCounter, monthsStartTimeInSecs);

            if(("account_Level".equalsIgnoreCase(amountLimitCheck)||"mid_Level".equalsIgnoreCase(amountLimitCheck))&&"Y".equalsIgnoreCase(monthly_Account_Amount_Limit))
            {   transactionLogger.error("checkAmountLimitAccountLevel---->"+countCardPreparedStatement.toString());
                transactionLogger.error("inside if account_Level or mid level monthly condition---->"+monthly_Account_Amount_Limit);
                totalAmountResultSet = countCardPreparedStatement.executeQuery();
            }

            if(totalAmountResultSet != null)
            {
                hash = Database.getHashMapFromResultSetForTransactionEntry(totalAmountResultSet);
                for (int i = 1; i <= hash.size(); i++)
                {
                    HashMap innerHash = new HashMap();
                    innerHash = (HashMap) hash.get("" + i);
                    if(functions.isValueNull((String) innerHash.get("amount")))
                    {
                        if("mid_Level".equalsIgnoreCase(amountLimitCheck))
                        {
                            transCurrency = (String) innerHash.get("currency");
                            transCurrencyRate=Float.parseFloat(RB.getString(transCurrency));
                            monthly_amount_total += Float.parseFloat((String) innerHash.get("amount"))*(baseCurrencyRate/transCurrencyRate);
                        }
                        else
                        {
                            monthly_amount_total += Float.parseFloat((String) innerHash.get("amount"));
                        }
                    }
                }
                transactionLogger.error("Monthly Amount Total : " + monthly_amount_total);
                totalAmountResultSet.close();
            }

            daily_amount_total += Float.parseFloat(amount);
            weekly_amount_total += Float.parseFloat(amount);
            monthly_amount_total += Float.parseFloat(amount);

            transactionLogger.error("Daily Total including today's Amount : " + daily_amount_total + " Weekly Total including today's Amount : " + weekly_amount_total + " Monthly Card Total including today's Amount : " + monthly_amount_total);

            transactionLogger.debug("Checking Limits");

            if ("Y".equalsIgnoreCase(daily_Account_Amount_Limit)&& daily_amount_total > account_daily_amount_limit )
            {
                isDailyAmountLimitOver = true;
            }
            if ("Y".equalsIgnoreCase(weekly_Account_Amount_Limit)&& weekly_amount_total > account_weekly_amount_limit )
            {
                isWeeklyAmountLimitOver = true;
            }
            if ("Y".equalsIgnoreCase(monthly_Account_Amount_Limit)&& monthly_amount_total > account_monthly_amount_limit )
            {
                isMonthlyAmountLimitOver = true;
            }
            if ( txnAmount>maxTxnAmount)
            {
                maxAmountLimitExceeded = true;
            }

            transactionLogger.error("isDailyAmountLimitOver : " + isDailyAmountLimitOver+"  isWeeklyAmountLimitOver : " + isWeeklyAmountLimitOver+"  isMonthlyAmountLimitOver : " + isMonthlyAmountLimitOver);
            if(isDailyAmountLimitOver || isWeeklyAmountLimitOver || isMonthlyAmountLimitOver||maxAmountLimitExceeded)
            {
                isLimitAvailable=false;
            }
            if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()))
            {
                if (isDailyAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNT_DAILY_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isWeeklyAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNT_WEEKLY_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isMonthlyAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNT_MONTHLY_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (maxAmountLimitExceeded)
                {
                    error = "Amount greater than maximum transaction amount-"+maxAmountLimitExceeded;
                    errorCodeListVO = getErrorVO(ErrorName.SYS_GREATER_AMT_CHECK,error);
            /*Transaction request rejected log entry with reason:Partner-MAX_TICKET_AMOUNT_LIMIT*/
                    //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.MAX_TICKET_AMOUNT_LIMIT.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_GREATER_AMT_CHECK.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkTransactionAmount()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
                }
            }else {
                if (isDailyAmountLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_ACCOUNT_DAILY_AMT_LIMIT_EXCEEDED.toString());
                else if (isWeeklyAmountLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_ACCOUNT_WEEKLY_AMT_LIMIT_EXCEEDED.toString());
                else if (isMonthlyAmountLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_ACCOUNT_MONTHLY_AMT_LIMIT_EXCEEDED.toString());
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return isLimitAvailable;
    }
    public boolean checkCardLimitTerminalLevel(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        SplitPaymentVO splitPaymentVO = new SplitPaymentVO();
        Functions functions=new Functions();
        Connection dbConn = null;
        String error = "";
        boolean isLimitAvailable = true;
        ErrorCodeListVO errorCodeListVO = null;

        String accountId = commonValidatorVO.getTerminalVO().getAccountId();
        String terminalId = commonValidatorVO.getTerminalVO().getTerminalId();

        String cardNo = commonValidatorVO.getCardDetailsVO().getCardNum();
        String firstSixCCNum = cardNo.substring(0, 6);
        String lastFourCcNum = cardNo.substring(cardNo.length() - 4);
        try
        {
            transactionLogger.debug("Load member card limits ");
            String cardCheckLimit=commonValidatorVO.getTerminalVO().getCardLimitCheckTerminalLevel();
            dbConn = Database.getConnection();

            boolean isDailyCardLimitOver = false;
            boolean isMonthlyCardLimitOver = false;
            boolean isWeeklyCardLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            dbConn = Database.getConnection();
            logger.debug("Load member account card limits ");
            String accountList = "select daily_card_limit,weekly_card_limit,monthly_card_limit from member_account_mapping where terminalid=? ";
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList);
            preparedStatementaccountList.setInt(1, Integer.parseInt(terminalId));
            //preparedStatementaccountList.setInt(2, Integer.parseInt(accountId));

            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();
            if (memberAccountresultSet.next())
            {
                int member_account_daily_limit = memberAccountresultSet.getInt("daily_card_limit");
                int member_account_weekly_limit = memberAccountresultSet.getInt("weekly_card_limit");
                int member_account_monthly_limit = memberAccountresultSet.getInt("monthly_card_limit");

                transactionLogger.error("Member Account ID--" + accountId);
                transactionLogger.error("Member Account Daily Card limits-- " + member_account_daily_limit + " Member Account Weekly Card limits-- " + member_account_weekly_limit + " Member Account Monthly Card limits-- " + member_account_monthly_limit);

                //logger.error("Load Account card limits ");
                GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));

                String countCardQuery="";
                if("terminal_Level".equalsIgnoreCase(cardCheckLimit))
                {
                    countCardQuery = "select count(tt.trackingid) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.cardtypeid=? and tt.accountid = ? and terminalid=? and bd.isSuccessful='Y' and tt.dtstamp >? and tt.parentTrackingid IS NULL";
                }
                else if("account_Level".equalsIgnoreCase(cardCheckLimit))
                {
                    countCardQuery = "select count(tt.trackingid) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.cardtypeid=? and tt.accountid = ? and bd.isSuccessful='Y' and tt.dtstamp >? and tt.parentTrackingid IS NULL";
                }
                else if("account_member_Level".equalsIgnoreCase(cardCheckLimit))
                {
                    countCardQuery = "select count(tt.trackingid) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.toid=? and tt.accountid = ? and bd.isSuccessful='Y' and tt.dtstamp >? and tt.parentTrackingid IS NULL";
                }
                Calendar calendar = Calendar.getInstance();
                Calendar calendar2 = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                //calendar.set(Calendar.AM_PM, Calendar.AM);
                long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                transactionLogger.debug("day------>" + calendar.getTime());
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                transactionLogger.debug("First day of week------>" + calendar.getTime());

                calendar2.set(Calendar.HOUR_OF_DAY, 0);
                calendar2.set(Calendar.MINUTE, 0);
                calendar2.set(Calendar.SECOND, 0);
                calendar2.set(Calendar.DAY_OF_MONTH, 1);
                transactionLogger.debug("First day of Month------>" + calendar2.getTime());
                long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

                int daily_card_total = 0;
                int weekly_card_total = 0;
                int monthly_card_total = 0;

                PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);
                int psCounter = 1;
                countCardPreparedStatement.setString(psCounter, firstSixCCNum);
                psCounter++;
                countCardPreparedStatement.setString(psCounter, lastFourCcNum);
                psCounter++;
                if("account_member_level".equalsIgnoreCase(cardCheckLimit))
                {
                    countCardPreparedStatement.setString(psCounter, commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    psCounter++;
                }else
                {
                    countCardPreparedStatement.setString(psCounter, commonValidatorVO.getCardType());
                    psCounter++;
                }
                countCardPreparedStatement.setString(psCounter, accountId);
                psCounter++;
                if("terminal_Level".equalsIgnoreCase(cardCheckLimit))
                {
                    countCardPreparedStatement.setString(psCounter, terminalId);//currency
                    psCounter++;
                }
                countCardPreparedStatement.setLong(psCounter, todaysStartTimeInSecs);
                transactionLogger.error("checkCardLimitTerminalLevel---->"+countCardPreparedStatement.toString());
                ResultSet totalAmountResultSet = countCardPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    daily_card_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Daily Total : " + daily_card_total);
                totalAmountResultSet.close();
                countCardPreparedStatement.setLong(psCounter, weeklyStartTimeInSecs);
                transactionLogger.error("checkCardLimitTerminalLevel---->"+countCardPreparedStatement.toString());

                totalAmountResultSet = countCardPreparedStatement.executeQuery();

                if (totalAmountResultSet.next())
                {
                    weekly_card_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Weekly Total : " + weekly_card_total);
                totalAmountResultSet.close();

                countCardPreparedStatement.setLong(psCounter, monthsStartTimeInSecs);
                transactionLogger.error("checkCardLimitTerminalLevel---->"+countCardPreparedStatement.toString());

                totalAmountResultSet = countCardPreparedStatement.executeQuery();

                if (totalAmountResultSet.next())
                {
                    monthly_card_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Monthly Card Total : " + monthly_card_total);
                totalAmountResultSet.close();


                transactionLogger.debug("Checking Limits");
                if (daily_card_total >= member_account_daily_limit)
                {
                    isDailyCardLimitOver = true;
                }
                if (weekly_card_total >= member_account_weekly_limit)
                {
                    isWeeklyCardLimitOver = true;
                }
                if (monthly_card_total >= member_account_monthly_limit)
                {
                    isMonthlyCardLimitOver = true;
                }
                if(isDailyCardLimitOver || isWeeklyCardLimitOver || isMonthlyCardLimitOver)
                {
                    isLimitAvailable=false;
                }
            }
            if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()))
            {
                if (isDailyCardLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_DAILY_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isWeeklyCardLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_WEEKLY_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isMonthlyCardLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_MONTHLY_CARDLIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }else
            {
                if (isDailyCardLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_TERMINAL_DAILY_CARDLIMIT_EXCEEDED.toString());
                else if (isWeeklyCardLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_TERMINAL_WEEKLY_CARDLIMIT_EXCEEDED.toString());
                else if (isMonthlyCardLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_TERMINAL_MONTHLY_CARDLIMIT_EXCEEDED.toString());
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return isLimitAvailable;
    }
    public boolean checkCardAmountLimitTerminalLevel(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        boolean isLimitAvailable = true;
        ErrorCodeListVO errorCodeListVO = null;
        Functions functions=new Functions();

        String accountId = commonValidatorVO.getTerminalVO().getAccountId();
        String terminalId = commonValidatorVO.getTerminalVO().getTerminalId();
        String amount=commonValidatorVO.getTransDetailsVO().getAmount();

        String cardNo = commonValidatorVO.getCardDetailsVO().getCardNum();
        String firstSixCCNum = cardNo.substring(0, 6);
        String lastFourCcNum = cardNo.substring(cardNo.length() - 4);
        try
        {
            transactionLogger.debug("Load Account card amount limits ");
            boolean isDailyCardAmountLimitOver = false;
            boolean isMonthlyCardAmountLimitOver = false;
            boolean isWeeklyCardAmountLimitOver = false;
            String cardAmountLimitCheck=commonValidatorVO.getTerminalVO().getCardAmountLimitCheckTerminalLevel();
            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            dbConn = Database.getConnection();
            logger.debug("Load member account card limits ");
            String accountList = "select daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit from member_account_mapping where terminalid=? ";
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList);
            preparedStatementaccountList.setInt(1, Integer.parseInt(terminalId));
            //preparedStatementaccountList.setInt(2, Integer.parseInt(accountId));

            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();
            if (memberAccountresultSet.next())
            {
                float member_account_daily_amount_limit = memberAccountresultSet.getInt("daily_card_amount_limit");
                float member_account_weekly_amount_limit = memberAccountresultSet.getInt("weekly_card_amount_limit");
                float member_account_monthly_amount_limit = memberAccountresultSet.getInt("monthly_card_amount_limit");

                transactionLogger.error("Member Account ID--" + accountId + " Member Terminal ID--" + terminalId);
                transactionLogger.error("Member Account Daily Card Amount limits-- " + member_account_daily_amount_limit + " Member Account Weekly Card Amount limits-- " + member_account_weekly_amount_limit + " Member Account Monthly Card Amount limits-- " + member_account_monthly_amount_limit);

                //logger.error("Load Account card limits ");
                GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));

                String countCardQuery = "";
                if ("terminal_Level".equalsIgnoreCase(cardAmountLimitCheck))
                {
                    countCardQuery = "select sum(tt.amount) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.cardtypeid=? and tt.accountid = ? and terminalid=? and bd.isSuccessful='Y' and tt.dtstamp >? and tt.parentTrackingid IS NULL";
                }
                else if ("account_Level".equalsIgnoreCase(cardAmountLimitCheck))
                {
                    countCardQuery = "select sum(tt.amount) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.cardtypeid=? and tt.accountid=? and bd.isSuccessful='Y' and tt.dtstamp >? and tt.parentTrackingid IS NULL";
                }else if ("account_member_Level".equalsIgnoreCase(cardAmountLimitCheck))
                {
                    countCardQuery = "select sum(tt.amount) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.toid=? and tt.accountid=? and bd.isSuccessful='Y' and tt.dtstamp >? and tt.parentTrackingid IS NULL";
                }
                Calendar calendar = Calendar.getInstance();
                Calendar calendar2 = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                //calendar.set(Calendar.AM_PM, Calendar.AM);
                long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                transactionLogger.debug("day------>" + calendar.getTime());
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                transactionLogger.debug("First day of week------>" + calendar.getTime());

                calendar2.set(Calendar.HOUR_OF_DAY, 0);
                calendar2.set(Calendar.MINUTE, 0);
                calendar2.set(Calendar.SECOND, 0);
                calendar2.set(Calendar.DAY_OF_MONTH, 1);
                transactionLogger.debug("First day of Month------>" + calendar2.getTime());
                long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

                float daily_card_amount_total = 0;
                float weekly_card_amount_total = 0;
                float monthly_card_amount_total = 0;

                PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);
                int psCounter = 1;
                countCardPreparedStatement.setString(psCounter, firstSixCCNum);
                psCounter++;
                countCardPreparedStatement.setString(psCounter, lastFourCcNum);
                psCounter++;
                if("account_member_level".equalsIgnoreCase(cardAmountLimitCheck))
                {
                    countCardPreparedStatement.setString(psCounter, commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    psCounter++;
                }else
                {
                    countCardPreparedStatement.setString(psCounter, commonValidatorVO.getCardType());
                    psCounter++;
                }
                countCardPreparedStatement.setString(psCounter, accountId);
                psCounter++;
                if ("terminal_Level".equalsIgnoreCase(cardAmountLimitCheck))
                {
                    countCardPreparedStatement.setString(psCounter, terminalId);//currency
                    psCounter++;
                }
                countCardPreparedStatement.setLong(psCounter, todaysStartTimeInSecs);
                transactionLogger.error("checkCardAmountLimitTerminalLevel---->" + countCardPreparedStatement.toString());
                ResultSet totalAmountResultSet = countCardPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    daily_card_amount_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Daily Card Total : " + daily_card_amount_total);
                totalAmountResultSet.close();
                countCardPreparedStatement.setLong(psCounter, weeklyStartTimeInSecs);
                transactionLogger.error("checkCardAmountLimitTerminalLevel---->" + countCardPreparedStatement.toString());

                totalAmountResultSet = countCardPreparedStatement.executeQuery();

                if (totalAmountResultSet.next())
                {
                    weekly_card_amount_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Weekly Card Total : " + weekly_card_amount_total);
                totalAmountResultSet.close();

                countCardPreparedStatement.setLong(psCounter, monthsStartTimeInSecs);
                transactionLogger.error("checkCardAmountLimitTerminalLevel---->" + countCardPreparedStatement.toString());

                totalAmountResultSet = countCardPreparedStatement.executeQuery();

                if (totalAmountResultSet.next())
                {
                    monthly_card_amount_total = totalAmountResultSet.getInt(1);
                }
                transactionLogger.error("Monthly Card Total : " + monthly_card_amount_total);
                totalAmountResultSet.close();

                daily_card_amount_total += Float.parseFloat(amount);
                weekly_card_amount_total += Float.parseFloat(amount);
                monthly_card_amount_total += Float.parseFloat(amount);

                transactionLogger.error("Daily Card Total including today's Amount : " + daily_card_amount_total + " Weekly Card Total including today's Amount : " + weekly_card_amount_total + " Monthly Card Total including today's Amount : " + monthly_card_amount_total);

                logger.debug("Checking Limits");
                if (daily_card_amount_total > member_account_daily_amount_limit)
                {
                    isDailyCardAmountLimitOver = true;
                }
                if (weekly_card_amount_total > member_account_weekly_amount_limit)
                {
                    isWeeklyCardAmountLimitOver = true;
                }
                if (monthly_card_amount_total > member_account_monthly_amount_limit)
                {
                    isMonthlyCardAmountLimitOver = true;
                }
                if(isDailyCardAmountLimitOver || isWeeklyCardAmountLimitOver || isMonthlyCardAmountLimitOver)
                {
                    isLimitAvailable=false;
                }
                transactionLogger.error("isLimitAvailable:::::" + isLimitAvailable);
            }
            if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()))
            {
                if (isDailyCardAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_DAILY_CARD_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isWeeklyCardAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_WEEKLY_CARD_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isMonthlyCardAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_MONTHLY_CARD_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }else {
                if (isDailyCardAmountLimitOver)
                    ErrorName.SYS_TERMINAL_DAILY_CARD_AMT_LIMIT_EXCEEDED.toString();
                else if (isWeeklyCardAmountLimitOver)
                    ErrorName.SYS_TERMINAL_WEEKLY_CARD_AMT_LIMIT_EXCEEDED.toString();
                else if (isMonthlyCardAmountLimitOver)
                    ErrorName.SYS_TERMINAL_MONTHLY_CARD_AMT_LIMIT_EXCEEDED.toString();
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return isLimitAvailable;
    }
    public boolean checkAmountLimitTerminalLevel(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        boolean isLimitAvailable = true;
        ErrorCodeListVO errorCodeListVO = null;
        Functions functions=new Functions();

        String accountId = commonValidatorVO.getTerminalVO().getAccountId();
        String terminalId = commonValidatorVO.getTerminalVO().getTerminalId();
        String amount=commonValidatorVO.getTransDetailsVO().getAmount();
        float txnamount= Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());
        try
        {
            transactionLogger.debug("Load Terminal amount limits ");
            boolean isDailyCardAmountLimitOver = false;
            boolean isMonthlyCardAmountLimitOver = false;
            boolean isWeeklyCardAmountLimitOver = false;
            boolean maxAmountLimitExceeded = false;
            String amountLimitCheck=commonValidatorVO.getTerminalVO().getAmountLimitCheckTerminalLevel();
            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            dbConn = Database.getConnection();
            transactionLogger.debug("Load member account card limits ");
            String accountList = "select daily_amount_limit,weekly_amount_limit,monthly_amount_limit,daily_amount_limit_check,weekly_amount_limit_check,monthly_amount_limit_check from member_account_mapping where terminalid=? ";
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList);
            preparedStatementaccountList.setInt(1, Integer.parseInt(terminalId));
            //preparedStatementaccountList.setInt(2, Integer.parseInt(accountId));

            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();
            if (memberAccountresultSet.next())
            {
                float member_account_daily_amount_limit = memberAccountresultSet.getInt("daily_amount_limit");
                float member_account_weekly_amount_limit = memberAccountresultSet.getInt("weekly_amount_limit");
                float member_account_monthly_amount_limit = memberAccountresultSet.getInt("monthly_amount_limit");
                float maxTxnAmount = commonValidatorVO.getTerminalVO().getMax_transaction_amount();
                String daily_amount_limit_check=memberAccountresultSet.getString("daily_amount_limit_check");
                String weekly_amount_limit_check=memberAccountresultSet.getString("weekly_amount_limit_check");
                String monthly_amount_limit_check=memberAccountresultSet.getString("monthly_amount_limit_check");
                transactionLogger.error("Member Account ID--" + accountId);
                transactionLogger.error("Terminal Daily Amount limits-- " + member_account_daily_amount_limit + " Terminal Account Weekly Amount limits-- " + member_account_weekly_amount_limit + " Terminal Account Monthly  Amount limits-- " + member_account_monthly_amount_limit);

                //logger.error("Load Account card limits ");
                GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));

                String countCardQuery = "";
                if ("terminal_Level".equalsIgnoreCase(amountLimitCheck))
                {
                    countCardQuery = "select sum(tt.amount) from  transaction_common tt where tt.cardtypeid=? and tt.accountid = ? and terminalid=? and tt.status  in ('capturesuccess','authsuccessful','reversed','settled','markedforreversal') and tt.dtstamp >? and tt.parentTrackingid IS NULL";
                }
                else if ("account_Level".equalsIgnoreCase(amountLimitCheck))
                {
                    countCardQuery = "select sum(tt.amount) from transaction_common tt where tt.cardtypeid=? and tt.accountid = ? and tt.status  in ('capturesuccess','authsuccessful','reversed','settled','markedforreversal') and tt.dtstamp >? and tt.parentTrackingid IS NULL";
                }else if ("account_member_Level".equalsIgnoreCase(amountLimitCheck))
                {
                    countCardQuery = "select sum(tt.amount) from transaction_common tt where tt.toid=? and tt.accountid = ? and tt.status  in ('capturesuccess','authsuccessful','reversed','settled','markedforreversal') and tt.dtstamp >? and tt.parentTrackingid IS NULL";
                }
                Calendar calendar = Calendar.getInstance();
                Calendar calendar2 = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                //calendar.set(Calendar.AM_PM, Calendar.AM);
                long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                transactionLogger.debug("day------>" + calendar.getTime());
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                transactionLogger.debug("First day of week------>" + calendar.getTime());

                calendar2.set(Calendar.HOUR_OF_DAY, 0);
                calendar2.set(Calendar.MINUTE, 0);
                calendar2.set(Calendar.SECOND, 0);
                calendar2.set(Calendar.DAY_OF_MONTH, 1);
                transactionLogger.debug("First day of Month------>" + calendar2.getTime());
                long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

                float daily_amount_total = 0;
                float weekly_amount_total = 0;
                float monthly_amount_total = 0;

                PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);
                int psCounter = 1;
                if("account_member_level".equalsIgnoreCase(amountLimitCheck))
                {
                    countCardPreparedStatement.setString(psCounter, commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    psCounter++;
                }else
                {
                    if(functions.isValueNull(commonValidatorVO.getTerminalVO().getCardTypeId()))
                    {
                        countCardPreparedStatement.setString(psCounter, commonValidatorVO.getTerminalVO().getCardTypeId());
                        psCounter++;     
                    }
                    else{
                        countCardPreparedStatement.setString(psCounter, commonValidatorVO.getCardType());
                        psCounter++;
                    }

                }
                countCardPreparedStatement.setString(psCounter, accountId);
                psCounter++;
                if ("terminal_Level".equalsIgnoreCase(amountLimitCheck))
                {
                    countCardPreparedStatement.setString(psCounter, terminalId);//currency
                    psCounter++;
                }
                countCardPreparedStatement.setLong(psCounter, todaysStartTimeInSecs);

                ResultSet totalAmountResultSet = null;

                if("Y".equalsIgnoreCase(daily_amount_limit_check))
                {
                    transactionLogger.error("inside if daily_amount_limit_check---->" +daily_amount_limit_check);
                    transactionLogger.error("checkAmountLimitTerminalLevel---->" + countCardPreparedStatement.toString());
                    totalAmountResultSet = countCardPreparedStatement.executeQuery();
                    if (totalAmountResultSet.next())
                    {
                        daily_amount_total = totalAmountResultSet.getInt(1);
                    }
                    transactionLogger.error("Daily Total : " + daily_amount_total);
                    totalAmountResultSet.close();
                }

                countCardPreparedStatement.setLong(psCounter, weeklyStartTimeInSecs);
                if("Y".equalsIgnoreCase(weekly_amount_limit_check))
                {
                    transactionLogger.error("inside if weekly_amount_limit_check---->" +weekly_amount_limit_check);
                    transactionLogger.error("checkAmountLimitTerminalLevel---->" + countCardPreparedStatement.toString());
                    totalAmountResultSet = countCardPreparedStatement.executeQuery();

                    if (totalAmountResultSet.next())
                    {
                        weekly_amount_total = totalAmountResultSet.getInt(1);
                    }
                    transactionLogger.error("Weekly Total : " + weekly_amount_total);
                    totalAmountResultSet.close();
                }
                countCardPreparedStatement.setLong(psCounter, monthsStartTimeInSecs);

                if("Y".equalsIgnoreCase(monthly_amount_limit_check))
                {
                    transactionLogger.error("inside if monthy_amount_limit_check---->" +monthly_amount_limit_check);
                    transactionLogger.error("checkAmountLimitTerminalLevel---->" + countCardPreparedStatement.toString());
                    totalAmountResultSet = countCardPreparedStatement.executeQuery();

                    if (totalAmountResultSet.next())
                    {
                        monthly_amount_total = totalAmountResultSet.getInt(1);
                    }
                    transactionLogger.error("Monthly  Total : " + monthly_amount_total);
                    totalAmountResultSet.close();
                }
                daily_amount_total += Float.parseFloat(amount);
                weekly_amount_total += Float.parseFloat(amount);
                monthly_amount_total += Float.parseFloat(amount);

                transactionLogger.error("Daily Total including today's Amount : " + daily_amount_total + " Weekly Total including today's Amount : " + weekly_amount_total + " Monthly Card Total including today's Amount : " + monthly_amount_total);

                transactionLogger.error("before daily limit condition txnamount-->"+txnamount+" maxTxnAmount"+maxTxnAmount);
                if ("Y".equalsIgnoreCase(daily_amount_limit_check)&&daily_amount_total > member_account_daily_amount_limit )
                {
                    isDailyCardAmountLimitOver = true;
                }
                if ("Y".equalsIgnoreCase(weekly_amount_limit_check)&&weekly_amount_total > member_account_weekly_amount_limit )
                {
                    isWeeklyCardAmountLimitOver = true;
                }
                if ("Y".equalsIgnoreCase(monthly_amount_limit_check)&&monthly_amount_total > member_account_monthly_amount_limit )
                {
                    isMonthlyCardAmountLimitOver = true;
                }

                if (txnamount>maxTxnAmount)
                {
                    maxAmountLimitExceeded = true;
                }

                if(isDailyCardAmountLimitOver || isWeeklyCardAmountLimitOver || isMonthlyCardAmountLimitOver||maxAmountLimitExceeded)
                {
                    isLimitAvailable=false;
                }
                transactionLogger.error("inside isDailyCardAmountLimitOver---->" +isDailyCardAmountLimitOver+"  isWeeklyCardAmountLimitOver-->"+isWeeklyCardAmountLimitOver+"  isMonthlyCardAmountLimitOver"+isMonthlyCardAmountLimitOver);
            }
            if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()))
            {
                if (isDailyCardAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_DAILY_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isWeeklyCardAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_WEEKLY_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (isMonthlyCardAmountLimitOver)
                {
                    error = "Transaction not permitted:::Please contact your Administrator.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_MONTHLY_AMT_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (maxAmountLimitExceeded)
                {
                    error = "Amount greater than maximum transaction amount-"+maxAmountLimitExceeded;
                    errorCodeListVO = getErrorVO(ErrorName.SYS_GREATER_AMT_CHECK,error);
            /*Transaction request rejected log entry with reason:Partner-MAX_TICKET_AMOUNT_LIMIT*/
                    //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.MAX_TICKET_AMOUNT_LIMIT.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_GREATER_AMT_CHECK.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkTransactionAmount()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
                }
            }else {
                if (isDailyCardAmountLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_TERMINAL_DAILY_AMT_LIMIT_EXCEEDED.toString());
                else if (isWeeklyCardAmountLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_TERMINAL_WEEKLY_AMT_LIMIT_EXCEEDED.toString());
                else if (isMonthlyCardAmountLimitOver)
                    commonValidatorVO.setErrorName(ErrorName.SYS_TERMINAL_MONTHLY_AMT_LIMIT_EXCEEDED.toString());
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return isLimitAvailable;
    }
    public boolean checkCardLimitNew(String memberId, String accountId, String fistSixCCNum, String lastFourCcNum, String terminalId, CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        SplitPaymentVO splitPaymentVO = new SplitPaymentVO();
        try
        {
            logger.debug("Load member card limits ");
            int member_daily_card_limit = 0;
            int member_weekly_card_limit = 0;
            int member_monthly_card_limit = 0;
            dbConn = Database.getConnection();

            boolean isDailyCardLimitOver = false;
            boolean isMonthlyCardLimitOver = false;
            boolean isWeeklyCardLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String memberAmountLimits = "select daily_card_limit,weekly_card_limit,monthly_card_limit from members where memberid= ? ";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                member_daily_card_limit = resultSet.getInt("daily_card_limit");
                member_weekly_card_limit = resultSet.getInt("weekly_card_limit");
                member_monthly_card_limit = resultSet.getInt("monthly_card_limit");
            }
            logger.error("Member Daily Card limits " + member_daily_card_limit + " Member Weekly Card limits " + member_weekly_card_limit + " Member Monthly Card limits " + member_monthly_card_limit);
            dbConn.close();

            dbConn = Database.getConnection();
            logger.debug("Load member account card limits ");
            String accountList = "select daily_card_limit,weekly_card_limit,monthly_card_limit from member_account_mapping where terminalid=? ";
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList);
            preparedStatementaccountList.setInt(1, Integer.parseInt(terminalId));
            //preparedStatementaccountList.setInt(2, Integer.parseInt(accountId));

            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();
            if (memberAccountresultSet.next())
            {
                int member_account_daily_limit = memberAccountresultSet.getInt("daily_card_limit");
                int member_account_weekly_limit = memberAccountresultSet.getInt("weekly_card_limit");
                int member_account_monthly_limit = memberAccountresultSet.getInt("monthly_card_limit");

                logger.error("Member Account ID" + accountId);
                logger.error("Member Account Daily Card limits " + member_account_daily_limit + " Member Account Weekly Card limits " + member_account_weekly_limit + " Member Account Monthly Card limits " + member_account_monthly_limit);

                logger.debug("Load Account card limits ");
                GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                int account_daily_limit = account.getDailyCardLimit();
                int account_weekly_limit = account.getWeeklyCardLimit();
                int account_monthly_limit = account.getMonthlyCardLimit();

                logger.error(" Account Daily Card limits " + account_daily_limit + " Account Weekly Card limits " + account_weekly_limit + " Account Monthly Card limits " + account_monthly_limit);

                String countCardQuery = "select count(tt.trackingid) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.toid = ? and tt.accountid = ? and tt.terminalid=? and bd.isSuccessful='Y' and tt.dtstamp >? ";
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                //calendar.set(Calendar.AM_PM, Calendar.AM);
                long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                calendar.add(Calendar.DATE, -7);
                long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                calendar.add(Calendar.DATE, 7); //Resetting time to current time
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                long monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                int daily_card_total = 0;
                int weekly_card_total = 0;
                int monthly_card_total = 0;

                PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);

                countCardPreparedStatement.setString(1, fistSixCCNum);
                countCardPreparedStatement.setString(2, lastFourCcNum);
                countCardPreparedStatement.setInt(3, Integer.parseInt(memberId));
                countCardPreparedStatement.setInt(4, Integer.parseInt(accountId));
                countCardPreparedStatement.setInt(5, Integer.parseInt(terminalId));
                countCardPreparedStatement.setLong(6, todaysStartTimeInSecs);
                logger.error(countCardPreparedStatement.toString());
                ResultSet totalAmountResultSet = countCardPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    daily_card_total = totalAmountResultSet.getInt(1);
                }
                logger.error("Daily Total : " + daily_card_total);
                totalAmountResultSet.close();
                countCardPreparedStatement.setLong(6, weeklyStartTimeInSecs);
                logger.error(countCardPreparedStatement.toString());

                totalAmountResultSet = countCardPreparedStatement.executeQuery();

                if (totalAmountResultSet.next())
                {
                    weekly_card_total = totalAmountResultSet.getInt(1);
                }
                logger.error("Weekly Total : " + weekly_card_total);
                totalAmountResultSet.close();

                countCardPreparedStatement.setLong(6, monthsStartTimeInSecs);
                logger.error(countCardPreparedStatement.toString());

                totalAmountResultSet = countCardPreparedStatement.executeQuery();

                if (totalAmountResultSet.next())
                {
                    monthly_card_total = totalAmountResultSet.getInt(1);
                }
                logger.error("Monthly Card Total : " + monthly_card_total);
                totalAmountResultSet.close();


                logger.error("Checking Limits");

                if (daily_card_total >= member_account_daily_limit || daily_card_total >= member_daily_card_limit || daily_card_total >= account_daily_limit)
                {
                    isDailyCardLimitOver = true;
                }
                if (weekly_card_total >= member_account_weekly_limit || weekly_card_total >= member_weekly_card_limit || weekly_card_total >= account_weekly_limit)
                {
                    isWeeklyCardLimitOver = true;
                }
                if (monthly_card_total >= member_account_monthly_limit || monthly_card_total >= member_monthly_card_limit || monthly_card_total >= account_monthly_limit)
                {
                    isMonthlyCardLimitOver = true;
                }
            }

            if (isDailyCardLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_DAILY_CARDLIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isWeeklyCardLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_WEEKLY_CARDLIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyCardLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MONTHLY_CARDLIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }

    public boolean checkCardLimitPerBank(String fistSixCCNum, String lastFourCcNum, CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;

        try
        {
            logger.debug("Load member card limits per Bank");
            int daily_card_limit = 0;
            int weekly_card_limit = 0;
            int monthly_card_limit = 0;
            dbConn = Database.getConnection();

            boolean isDailyCardLimitOver = false;
            boolean isMonthlyCardLimitOver = false;
            boolean isWeeklyCardLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String gatewayCardLimits = "SELECT dailyCardLimit,weeklyCardLimit,monthlyCardLimit FROM gatewaytype_partner_mapping WHERE pgtypeid=? AND partnerid=?";
            PreparedStatement preparedStatementGatewayAmountLimits = dbConn.prepareStatement(gatewayCardLimits);
            preparedStatementGatewayAmountLimits.setString(1, commonValidatorVO.getTerminalVO().getGateway_id());
            preparedStatementGatewayAmountLimits.setString(2, commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            ResultSet resultSet = preparedStatementGatewayAmountLimits.executeQuery();
            if (resultSet.next())
            {
                daily_card_limit = resultSet.getInt("dailyCardLimit");
                weekly_card_limit = resultSet.getInt("weeklyCardLimit");
                monthly_card_limit = resultSet.getInt("monthlyCardLimit");
            }
            logger.error("Bank/Partner Daily Card limits " + daily_card_limit + " Bank/Partner Weekly Card limits " + weekly_card_limit + " Bank/Partner Monthly Card limits " + monthly_card_limit);
            dbConn.close();

            dbConn = Database.getConnection();

            String countCardQuery = "select count(tt.trackingid) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.totype = ? and tt.fromtype = ? and tt.dtstamp >? ";
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            //calendar.set(Calendar.AM_PM, Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            calendar.add(Calendar.DATE, -7);
            long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;

            calendar.add(Calendar.DATE, 7); //Resetting time to current time
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            long monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;

            int daily_card_total = 0;
            int weekly_card_total = 0;
            int monthly_card_total = 0;

            PreparedStatement countCardPreparedStatement = dbConn.prepareStatement(countCardQuery);

            countCardPreparedStatement.setString(1, fistSixCCNum);
            countCardPreparedStatement.setString(2, lastFourCcNum);
            countCardPreparedStatement.setString(3, commonValidatorVO.getTransDetailsVO().getTotype());
            countCardPreparedStatement.setString(4, commonValidatorVO.getTransDetailsVO().getFromtype());

            countCardPreparedStatement.setLong(5, todaysStartTimeInSecs);
            logger.error(countCardPreparedStatement.toString());
            ResultSet totalAmountResultSet = countCardPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                daily_card_total = totalAmountResultSet.getInt(1);
            }
            logger.error("Daily Total card used count: " + daily_card_total);
            totalAmountResultSet.close();
            countCardPreparedStatement.setLong(5, weeklyStartTimeInSecs);
            logger.error(countCardPreparedStatement.toString());

            totalAmountResultSet = countCardPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                weekly_card_total = totalAmountResultSet.getInt(1);
            }
            logger.error("Weekly Total card used count: " + weekly_card_total);
            totalAmountResultSet.close();

            countCardPreparedStatement.setLong(5, monthsStartTimeInSecs);
            logger.error(countCardPreparedStatement.toString());

            totalAmountResultSet = countCardPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                monthly_card_total = totalAmountResultSet.getInt(1);
            }
            logger.error("Monthly Card Total card used count: " + monthly_card_total);
            totalAmountResultSet.close();

            if (daily_card_total >= daily_card_limit)
            {
                isDailyCardLimitOver = true;
            }
            if (weekly_card_total >= weekly_card_limit)
            {
                isWeeklyCardLimitOver = true;
            }
            if (monthly_card_total >= monthly_card_limit)
            {
                isMonthlyCardLimitOver = true;
            }

            if (isDailyCardLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_DAILY_CARDLIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isWeeklyCardLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_WEEKLY_CARDLIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyCardLimitOver)
            {
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MONTHLY_CARDLIMIT_EXCEEDED.toString(),ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }

    public boolean checkCardAmountLimitNew(String memberId, String accountId, String amount, String fistSixCCNum, String lastFourCcNum, CommonValidatorVO commonValidatorVO, String terminalId) throws PZConstraintViolationException, PZDBViolationException
    {

        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        //SplitPaymentVO splitPaymentVO = new SplitPaymentVO();
        Connection dbConn = null;
        try
        {
            logger.debug("Load member card amount limits ");
            float member_daily_card_amount_limit = 0.0f;
            float member_weekly_card_amount_limit = 0.0f;
            float member_monthly_card_amount_limit = 0.0f;

            boolean isDailyCardAmtLimitOver = false;
            boolean isWeeklyCardAmtLimitOver = false;
            boolean isMonthlyCardAmtLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
            dbConn = Database.getConnection();

            String memberAmountLimits = "select daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit from members where memberid= ? ";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));
            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();
            if (resultSet.next())
            {
                member_daily_card_amount_limit = resultSet.getInt("daily_card_amount_limit");
                member_weekly_card_amount_limit = resultSet.getInt("weekly_card_amount_limit");
                member_monthly_card_amount_limit = resultSet.getInt("monthly_card_amount_limit");
            }
            logger.error("Member Daily Card Amount limits " + member_daily_card_amount_limit + " Member Weekly Card Amount limits " + member_weekly_card_amount_limit + " Member Monthly Card Amount limits " + member_monthly_card_amount_limit);
            //dbConn.close();

            //dbConn = Database.getConnection();
            logger.debug("Load member account card amount limits ");
            String accountList = "select daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit from member_account_mapping where memberid=? and accountid=? and terminalid=?";
            PreparedStatement preparedStatementaccountList = dbConn.prepareStatement(accountList);
            preparedStatementaccountList.setInt(1, Integer.parseInt(memberId));
            preparedStatementaccountList.setInt(2, Integer.parseInt(accountId));
            preparedStatementaccountList.setInt(3, Integer.parseInt(terminalId));
            ResultSet memberAccountresultSet = preparedStatementaccountList.executeQuery();
            if (memberAccountresultSet.next())
            {
                float member_account_daily_amount_limit = memberAccountresultSet.getInt("daily_card_amount_limit");
                float member_account_weekly_amount_limit = memberAccountresultSet.getInt("weekly_card_amount_limit");
                float member_account_monthly_amount_limit = memberAccountresultSet.getInt("monthly_card_amount_limit");

                logger.error("Member Account ID" + accountId);
                logger.error("Member Account Daily Card Amount limits " + member_account_daily_amount_limit + " Member Account Weekly Card Amount limits " + member_account_weekly_amount_limit + " Member Account Monthly Card Amount limits " + member_account_monthly_amount_limit);

                logger.debug("Load Account card Amount limits ");
                GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                float account_daily_amount_limit = account.getDailyCardAmountLimit();
                float account_weekly_amount_limit = account.getWeeklyCardAmountLimit();
                float account_monthly_amount_limit = account.getMonthlyCardAmountLimit();

                logger.error(" Account Daily Card Amount limits " + account_daily_amount_limit + " Account Weekly Card Amount limits " + account_weekly_amount_limit + " Account Monthly Card Amount limits " + account_monthly_amount_limit);


                String countCardQuery = "select sum(tt.amount) from bin_details bd, transaction_common tt where tt.trackingid=bd.icicitransid and bd.first_six = ? and bd.last_four = ? and tt.toid = ? and tt.accountid = ? and tt.terminalid=? and tt.dtstamp > ?  and tt.status in ('authsuccessful','proofrequired','capturesuccess','capturestarted','podsent','settled','reversed','chargeback','markedforreversal','authcancelled')";
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                //calendar.set(Calendar.AM_PM, Calendar.AM);
                long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
                calendar.add(Calendar.DATE, -7);
                long weeklyStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                calendar.add(Calendar.DATE, 7); //Resetting time to current time
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                long monthsStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                float daily_card_amount_total = 0;
                float weekly_card_amount_total = 0;
                float monthly_card_amount_total = 0;

                PreparedStatement countCardAmountPreparedStatement = dbConn.prepareStatement(countCardQuery);

                countCardAmountPreparedStatement.setString(1, fistSixCCNum);
                countCardAmountPreparedStatement.setString(2, lastFourCcNum);
                countCardAmountPreparedStatement.setInt(3, Integer.parseInt(memberId));
                countCardAmountPreparedStatement.setInt(4, Integer.parseInt(accountId));
                countCardAmountPreparedStatement.setInt(5, Integer.parseInt(terminalId));
                countCardAmountPreparedStatement.setLong(6, todaysStartTimeInSecs);
                logger.debug(countCardAmountPreparedStatement.toString());
                ResultSet totalAmountResultSet = countCardAmountPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    daily_card_amount_total = totalAmountResultSet.getFloat(1);
                }
                daily_card_amount_total += Float.parseFloat(amount);
                logger.error("Daily Card Maount Total : " + daily_card_amount_total);
                totalAmountResultSet.close();

                countCardAmountPreparedStatement.setLong(6, weeklyStartTimeInSecs);
                logger.error(countCardAmountPreparedStatement.toString());
                totalAmountResultSet = countCardAmountPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    weekly_card_amount_total = totalAmountResultSet.getInt(1);
                }
                weekly_card_amount_total += Float.parseFloat(amount);
                logger.error("Weekly Card Amount Total : " + weekly_card_amount_total);
                totalAmountResultSet.close();

                countCardAmountPreparedStatement.setLong(6, monthsStartTimeInSecs);
                logger.error(countCardAmountPreparedStatement.toString());
                totalAmountResultSet = countCardAmountPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    monthly_card_amount_total = totalAmountResultSet.getInt(1);
                }
                monthly_card_amount_total += Float.parseFloat(amount);
                logger.error("Monthly Card Amount Total : " + monthly_card_amount_total);
                totalAmountResultSet.close();

                logger.debug("Checking Limits");
                if (daily_card_amount_total >= member_account_daily_amount_limit || daily_card_amount_total > member_daily_card_amount_limit || daily_card_amount_total >= account_daily_amount_limit)
                {
                    isDailyCardAmtLimitOver = true;
                }
                if (weekly_card_amount_total >= member_account_weekly_amount_limit || weekly_card_amount_total >= member_weekly_card_amount_limit || weekly_card_amount_total >= account_weekly_amount_limit)
                {
                    isWeeklyCardAmtLimitOver = true;
                }
                if (monthly_card_amount_total >= member_account_monthly_amount_limit || monthly_card_amount_total >= member_monthly_card_amount_limit || monthly_card_amount_total >= account_monthly_amount_limit)
                {
                    isMonthlyCardAmtLimitOver = true;
                }
            }
            if (isDailyCardAmtLimitOver)
            {
                error = "Daily Card Volume Amount Limits exceeded:::Please contact your Administrator";
                errorCodeListVO = getErrorVO(ErrorName.SYS_DAILY_CARD_AMT_CHECK, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_DAILY_CARD_AMT_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isWeeklyCardAmtLimitOver)
            {
                error = "Weekly Card Volume Amount Limits exceeded:::Please contact your Administrator";
                errorCodeListVO = getErrorVO(ErrorName.SYS_WEEKLY_CARD_AMT_CHECK, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_WEEKLY_CARD_AMT_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyCardAmtLimitOver)
            {
                error = "Monthly Card Volume Amount Limits exceeded:::Please contact your Administrator";
                errorCodeListVO = getErrorVO(ErrorName.SYS_MONTHLY_CARD_AMT_CHECK, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MONTHLY_CARD_AMT_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking card amount limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking card amount limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCardAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }

        return true;

    }
    private static ErrorCodeListVO getErrorVO(ErrorName errorName,String reason)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        errorCodeVO = errorCodeUtils.getSystemErrorCode(errorName);
        errorCodeVO.setErrorName(errorName);
        errorCodeVO.setErrorReason(reason);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }

    public boolean checkDailyTrxnLimitPerEmail(String accountId, String terminalId, String emailId,CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection conn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        try
        {
            boolean isDailyEmailTransLimitOver = false;
            boolean isEmailLimitEnabled = false;
            int dailyAllowedTrxnPerEmail = 0;

            conn = Database.getConnection();
            String qryTerminalEmailLimit = "select emailLimitEnabled, dailyTrxnPerEmail from member_account_mapping where terminalId= ? and emailLimitEnabled='Y'";
            PreparedStatement preparedStatementMemberAmountLimits = conn.prepareStatement(qryTerminalEmailLimit);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(terminalId));
            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();
            if (resultSet.next())
            {
                isEmailLimitEnabled = resultSet.getBoolean("emailLimitEnabled");
                dailyAllowedTrxnPerEmail = resultSet.getInt("dailyTrxnPerEmail");

                GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                String pgTypeId = account.getPgTypeId();
                GatewayType gatewayType = GatewayTypeService.getGatewayType(pgTypeId);
                String tableName = Database.getTableName(gatewayType.getGateway());

                String countCardQuery = "select count(*) from bin_details bd, " + tableName + " tt where tt.trackingid=bd.icicitransid and bd.emailaddr = ? and tt.dtstamp > ?";
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                //calendar.set(Calendar.AM_PM, Calendar.AM);
                long todayStartTimeInSecs = calendar.getTimeInMillis() / 1000;

                int daily_email_total = 0;
                Connection totalCardCountDbConn = Database.getConnection();
                PreparedStatement countCardPreparedStatement = totalCardCountDbConn.prepareStatement(countCardQuery);
                countCardPreparedStatement.setString(1, emailId);
                countCardPreparedStatement.setLong(2, todayStartTimeInSecs);
                ResultSet totalAmountResultSet = countCardPreparedStatement.executeQuery();
                if (totalAmountResultSet.next())
                {
                    daily_email_total = totalAmountResultSet.getInt(1);
                }
                logger.debug("daily_email_total::::" + daily_email_total);
                //if daily_email_total =0; daily_email_total =1; daily_email_total =2 when dailyAllowedTrxnPerEmail=2
                if (daily_email_total < dailyAllowedTrxnPerEmail)
                {
                    isDailyEmailTransLimitOver = false;
                }
                else
                {
                    isDailyEmailTransLimitOver = true;
                }
                logger.debug("isDailyEmailTransLimitOver::::" + isDailyEmailTransLimitOver);
            }
            if (isDailyEmailTransLimitOver)
            {
                error = "Your Daily Email Limit exceeded:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_DAILY_EMAIL_TRANS_LIMIT_EXCEEDED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_DAILY_EMAIL_TRANS_LIMIT_EXCEEDED.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkDailyTrxnLimitPerEmail()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while checking Email limit ", e);
        }
        catch (SystemError e)
        {
            logger.error("Error while checking Email limit ", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return true;
    }

    public boolean isMasterCardSupported(String toid)
    {
        Connection con = null;
        boolean mastercardSupported = false;
        try
        {
            logger.debug("Check for master card support starts ");
            con = Database.getConnection();
            transactionLogger.debug("LimitChecker.isMCSupported ::: DB Call");
            String q1 = "select masterCardSupported from members where memberid=?";
            PreparedStatement p1 = con.prepareStatement(q1);
            p1.setString(1, toid);
            ResultSet rs = p1.executeQuery();

            if (rs.next())
            {
                mastercardSupported = rs.getBoolean("masterCardSupported");
            }
        }
        catch (Exception e)
        {
            logger.error("Error while checking master card support ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        logger.debug("Master Card Supported " + mastercardSupported + " for " + toid);
        return mastercardSupported;
    }

    public void checkTransactionAmount(String amount, float maxTransactionAmount,float minTransactionAmount) throws PZConstraintViolationException
    {
        ErrorCodeListVO errorCodeListVO = null;
        String error = "";

        Float amt = Float.parseFloat(amount);
        if (amt > maxTransactionAmount)
        {
            error = "Amount greater than maximum transaction amount-"+maxTransactionAmount;
            errorCodeListVO = getErrorVO(ErrorName.SYS_GREATER_AMT_CHECK,error);
            PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkTransactionAmount()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
        }

        if (amt < minTransactionAmount)
        {
            error = "Amount less than minimum transaction amount-"+minTransactionAmount;
            errorCodeListVO = getErrorVO(ErrorName.SYS_LESS_AMT_CHECK,error);
            PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkTransactionAmount()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
        }
    }
    public void checkTransactionAmountNew(String amount, float maxTransactionAmount,float minTransactionAmount,CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException
    {
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();
        String error = "";

        Float amt = Float.parseFloat(amount);
        if (amt > maxTransactionAmount)
        {
            error = "Amount greater than maximum transaction amount-"+maxTransactionAmount;
            errorCodeListVO = getErrorVO(ErrorName.SYS_GREATER_AMT_CHECK,error);
            /*Transaction request rejected log entry with reason:Partner-MAX_TICKET_AMOUNT_LIMIT*/
            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.MAX_TICKET_AMOUNT_LIMIT.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_GREATER_AMT_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("LimitChecker","checkTransactionAmount()",null,"common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
        }

        if (amt < minTransactionAmount)
        {
            error = "Amount less than minimum transaction amount-"+minTransactionAmount;
            errorCodeListVO = getErrorVO(ErrorName.SYS_LESS_AMT_CHECK,error);
            /*Transaction request rejected log entry with reason:Partner-MIN_TICKET_AMOUNT_LIMIT*/
            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.MIN_TICKET_AMOUNT_LIMIT.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_LESS_AMT_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkTransactionAmount()", null, "common",error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
        }
    }



    public TerminalVO payoutAmountLimitRouting(CommonValidatorVO commonValidatorVO, LinkedList<TerminalVO> terminalLinkedList) throws PZDBViolationException
    {


        TerminalManager terminalManager=new TerminalManager();
        Map<String,HashMap> payoutAmountLimitMap=  terminalManager.getPayoutAmountLimit();

        for(TerminalVO terminalVO1:terminalLinkedList){
            HashMap<String,String> innerMap= payoutAmountLimitMap.get(terminalVO1.getAccountId());
            if(innerMap!=null){
                Double currentPayoutAmount= Double.valueOf(innerMap.get("currentPayoutAmount"));
                Double payoutTransactionAmount= Double.valueOf(commonValidatorVO.getTransDetailsVO().getAmount());
                if(payoutTransactionAmount<=currentPayoutAmount&&payoutTransactionAmount<=terminalVO1.getMax_transaction_amount()){
                    return terminalVO1;
                }
            }
        }


        return null;
    }


    public void updatePayoutAmountOnAccountid(String accountid,String payoutTxtAmount)throws PZDBViolationException{
        TerminalManager terminalManager=new TerminalManager();
        Map<String,String> payoutAmountLimitMap= terminalManager.getPayoutAmountLimitByAccountid(accountid);

        if(payoutAmountLimitMap!=null){

            Double currentPayoutAmount= Double.valueOf(payoutAmountLimitMap.get("currentPayoutAmount"));
            Double remaningPayoutAmount=currentPayoutAmount-Double.valueOf(payoutTxtAmount);
            terminalManager.updatePayoutTransactionAmount(accountid, String.format("%.2f",remaningPayoutAmount));
        }

    }
    public boolean checkVPAAddressDailyAmountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId =commonValidatorVO.getMerchantDetailsVO().getMemberId();
        commonValidatorVO.setCustomerId(commonValidatorVO.getVpa_address());
        try
        {
            logger.debug("Load member vpa limits ");
            double vpaAddressDailyAmountLimit = 0;
            double vpaAddressMonthlyAmountLimit = 0;
            dbConn = Database.getConnection();

            boolean isDailyAmountLimitOver = false;
            boolean isMonthlyAmountLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String memberCardAmountLimits = "select vpaAddressDailyAmountLimit,vpaAddressMonthlyAmountLimit from merchant_configuration where memberid= ?";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(memberCardAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                vpaAddressDailyAmountLimit = resultSet.getDouble("vpaAddressDailyAmountLimit");
                vpaAddressMonthlyAmountLimit = resultSet.getDouble("vpaAddressMonthlyAmountLimit");
            }
            transactionLogger.error("Member Daily Amount limits " + vpaAddressDailyAmountLimit);
            transactionLogger.error("Member Monthly Amount limits " + vpaAddressMonthlyAmountLimit);

            StringBuffer totalAmountQuery = new StringBuffer("select sum(trans_amount) from bin_details where customer_id = ?  and trans_dtstamp > ?  and isSuccessful='Y' ");
            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getVpaAddressAmountLimitCheck()))
                totalAmountQuery.append(" AND merchant_id="+memberId);
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            //calendar.set(Calendar.AM_PM,Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            double transaction_daily_amount = 0;

            //daily query
            PreparedStatement totalAmountPreparedStatement = dbConn.prepareStatement(totalAmountQuery.toString());
            totalAmountPreparedStatement.setString(1, commonValidatorVO.getVpa_address());
            totalAmountPreparedStatement.setLong(2, todaysStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for VPA Address member Query Daily---" + totalAmountPreparedStatement);
            ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                transaction_daily_amount = totalAmountResultSet.getDouble(1);
            }
            transaction_daily_amount += Double.parseDouble(amount);
            transactionLogger.error("Daily VPAAddress Transaction Amount : " + transaction_daily_amount + " Daily VPAAddress Transaction Amount after todays amount : " + transaction_daily_amount);

            // monthly total txt amount
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;
            double transaction_monthly_amount = 0;

            //monthly query
            PreparedStatement totalMonthlyAmountPreparedStatement = dbConn.prepareStatement(totalAmountQuery.toString());
            totalMonthlyAmountPreparedStatement.setString(1, commonValidatorVO.getVpa_address());
            totalMonthlyAmountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for VPA Address member Query Monthly---" + totalMonthlyAmountPreparedStatement);
            ResultSet totalMonthlyAmountResultSet = totalMonthlyAmountPreparedStatement.executeQuery();
            if (totalMonthlyAmountResultSet.next())
            {
                transaction_monthly_amount = totalMonthlyAmountResultSet.getDouble(1);
            }
            transaction_monthly_amount += Double.parseDouble(amount);
            transactionLogger.error("monthly VPAAddress Transaction Amount : " + transaction_monthly_amount + " Monthly VPAAddress Transaction Amount after todays amount : " + transaction_monthly_amount);


            if (vpaAddressDailyAmountLimit < transaction_daily_amount)
            {
                isDailyAmountLimitOver = true;
            }
            if (vpaAddressMonthlyAmountLimit < transaction_monthly_amount)
            {
                isMonthlyAmountLimitOver = true;
            }
            if (isDailyAmountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getVpaAddressAmountLimitCheck()))
                    errorname=ErrorName.SYS_MEMBER_DAILY_VPAADDRESS_AMT_LIMIT_EXCEEDED.toString();
                else
                    errorname=ErrorName.SYS_SYSTEM_DAILY_VPAADDRESS_AMT_LIMIT_EXCEEDED.toString();
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkVPAAddressDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyAmountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getVpaAddressAmountLimitCheck()))
                    errorname=ErrorName.SYS_MEMBER_MONTHLY_VPAADDRESS_AMT_LIMIT_EXCEEDED.toString();
                else
                    errorname=ErrorName.SYS_SYSTEM_MONTHLY_VPAADDRESS_AMT_LIMIT_EXCEEDED.toString();
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkVPAAddressDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while check VPAAddress DailyAmount Limi ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkVPAAddressDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check VPAAddress DailyAmount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkVPAAddressDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }
    public boolean checkVPAAddressDailyCountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId =commonValidatorVO.getMerchantDetailsVO().getMemberId();
        commonValidatorVO.setCustomerId(commonValidatorVO.getVpa_address());
        try
        {
            logger.debug("Load member vpa limits ");
            int vpaAddressDailyCount = 0;
            int vpaAddressMonthlyCount = 0;
            dbConn = Database.getConnection();

            boolean isDailyCountLimitOver = false;
            boolean isMonthlyCountLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String membervpaDailyCountQuery = "select vpaAddressDailyCount,vpaAddressMonthlyCount from merchant_configuration where memberid= ?";
            PreparedStatement preparedStatementMembervpaDailyCount = dbConn.prepareStatement(membervpaDailyCountQuery);
            preparedStatementMembervpaDailyCount.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMembervpaDailyCount.executeQuery();

            if (resultSet.next())
            {
                vpaAddressDailyCount = resultSet.getInt("vpaAddressDailyCount");
                vpaAddressMonthlyCount = resultSet.getInt("vpaAddressMonthlyCount");
            }
            transactionLogger.error("VPA Address Daily  limits " + vpaAddressDailyCount);
            transactionLogger.error("VPA Address Monthly  limits " + vpaAddressMonthlyCount);

            StringBuffer totalAmountQuery = new StringBuffer("select count(*) from bin_details where customer_id = ? and  trans_dtstamp > ?  and isSuccessful='Y' ");
            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getVpaAddressLimitCheck()))
                totalAmountQuery.append(" AND merchant_id="+memberId);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            //calendar.set(Calendar.AM_PM,Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;
            int transaction_daily_count = 0;

            PreparedStatement totalCountPreparedStatement = dbConn.prepareStatement(totalAmountQuery.toString());
            totalCountPreparedStatement.setString(1, commonValidatorVO.getVpa_address());
            totalCountPreparedStatement.setLong(2, todaysStartTimeInSecs);
            transactionLogger.error("checkTxnLimit for VPA Address member Query Daily---" + totalCountPreparedStatement);
            ResultSet totalAmountResultSet = totalCountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                transaction_daily_count = totalAmountResultSet.getInt(1);
            }
            transactionLogger.error("Daily VPAAddress Transaction Count : " + transaction_daily_count);

            // monthly total txt amount
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;
            double transaction_monthly_amount = 0;

            PreparedStatement totalMonthlyCountPreparedStatement = dbConn.prepareStatement(totalAmountQuery.toString());
            totalMonthlyCountPreparedStatement.setString(1, commonValidatorVO.getVpa_address());
            totalMonthlyCountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkTxnLimit for VPA Address member Query Monthly---" + totalMonthlyCountPreparedStatement);
            ResultSet totalMonthlyAmountResultSet = totalCountPreparedStatement.executeQuery();
            if (totalMonthlyAmountResultSet.next())
            {
                transaction_monthly_amount = totalMonthlyAmountResultSet.getInt(1);
            }
            transactionLogger.error("Monthly VPAAddress Transaction Count : " + transaction_monthly_amount);

            if (vpaAddressDailyCount <= transaction_daily_count)
            {
                isDailyCountLimitOver = true;
            }
            if (vpaAddressMonthlyCount <= transaction_monthly_amount)
            {
                isMonthlyCountLimitOver = true;
            }
            if (isDailyCountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getVpaAddressLimitCheck()))
                    errorname=ErrorName.SYS_MEMBER_DAILY_VPAADDRESS_TXN_LIMIT_EXCEEDED.toString();
                else
                    errorname=ErrorName.SYS_SYSTEM_DAILY_VPAADDRESS_TXN_LIMIT_EXCEEDED.toString();
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkVPAAddressDailyCountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyCountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getVpaAddressLimitCheck()))
                    errorname=ErrorName.SYS_MEMBER_MONTHLY_VPAADDRESS_TXN_LIMIT_EXCEEDED.toString();
                else
                    errorname=ErrorName.SYS_SYSTEM_MONTHLY_VPAADDRESS_TXN_LIMIT_EXCEEDED.toString();
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkVPAAddressDailyCountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while check VPAAddress DailyCount Limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkVPAAddressDailyCountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check VPAAddress DailyCount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkVPAAddressDailyCountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }

    public boolean checkPayoutBankAccountNoDailyAmountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {   //done
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId =commonValidatorVO.getMerchantDetailsVO().getMemberId();
        Functions functions=new Functions();
        try
        {
            logger.debug("Load member bankaccount limits ");
            double bankAccountNoDailyAmountLimit = 0;
            dbConn = Database.getConnection();

            boolean isDailyAmountLimitOver = false;
            boolean isMonthlyAmountLimitOver = false;
            double bankAccountNoMonthlyAmountLimit = 0;
            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String bankAccountNoDailyAmountLimitQuery = "select bankAccountNoDailyAmountLimit,bankAccountNoMonthlyAmountLimit from merchant_configuration where memberid= ?";
            PreparedStatement preparedStatementMemberAmountLimits = dbConn.prepareStatement(bankAccountNoDailyAmountLimitQuery);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                bankAccountNoDailyAmountLimit = resultSet.getDouble("bankAccountNoDailyAmountLimit");
                bankAccountNoMonthlyAmountLimit = resultSet.getDouble("bankAccountNoMonthlyAmountLimit");
            }
            transactionLogger.error("Member Daily payoutBankAccount Amount limits " + bankAccountNoDailyAmountLimit);

            StringBuffer totalAmountQuery = new StringBuffer("select sum(tc.payoutamount) from  transaction_safexpay_details as tsd , transaction_common as tc where tsd.bankaccount=? and  tc.dtstamp > ?  and tc.trackingid=tsd.trackingid and tc.status='payoutsuccessful'");
            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBankAccountAmountLimitCheck()))
                totalAmountQuery.append(" AND toid="+memberId);
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;


            double transaction_daily_amount = 0;
            String bankAccountnumber="";
            if(functions.isValueNull(commonValidatorVO.getReserveField2VO().getBankAccountNumber())){

                bankAccountnumber=commonValidatorVO.getReserveField2VO().getBankAccountNumber();
            }
            PreparedStatement totalAmountPreparedStatement = dbConn.prepareStatement(totalAmountQuery.toString());
            totalAmountPreparedStatement.setString(1, bankAccountnumber);
            totalAmountPreparedStatement.setLong(2, todaysStartTimeInSecs);
            transactionLogger.error("check payout bank Accountno AmountLimit member Query Daily---" + totalAmountPreparedStatement);
            ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                transaction_daily_amount = totalAmountResultSet.getDouble(1);
            }
            transactionLogger.error("Daily BankAccount Transaction Amount : " + transaction_daily_amount);
            transaction_daily_amount += Double.parseDouble(amount);

            transactionLogger.error("Daily BankAccount Transaction Amount after todays amount : " + transaction_daily_amount);

            // monthly total txt amount
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;
            double transaction_monthly_amount = 0;

            //monthly query
            PreparedStatement totalMonthlyAmountPreparedStatement = dbConn.prepareStatement(totalAmountQuery.toString());
            totalMonthlyAmountPreparedStatement.setString(1, bankAccountnumber);
            totalMonthlyAmountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for BankAccount Address member Query Monthly---" + totalMonthlyAmountPreparedStatement);
            ResultSet totalMonthlyAmountResultSet = totalMonthlyAmountPreparedStatement.executeQuery();
            if (totalMonthlyAmountResultSet.next())
            {
                transaction_monthly_amount = totalMonthlyAmountResultSet.getDouble(1);
            }
            transaction_monthly_amount += Double.parseDouble(amount);
            transactionLogger.error("monthly BankAccount Transaction Amount : " + transaction_monthly_amount + " Monthly BankAccount Transaction Amount after todays amount : " + transaction_monthly_amount);

            if (bankAccountNoMonthlyAmountLimit < transaction_monthly_amount)
            {
                isMonthlyAmountLimitOver = true;
            }

            if (bankAccountNoDailyAmountLimit < transaction_daily_amount)
            {
                isDailyAmountLimitOver = true;
            }
            if (isDailyAmountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBankAccountAmountLimitCheck()))
                    errorname=ErrorName.SYS_MEMBER_DAILY_BANKACCOUNT_AMT_LIMIT_EXCEEDED.toString();
                else
                    errorname=ErrorName.SYS_SYSTEM_DAILY_BANKACCOUNT_AMT_LIMIT_EXCEEDED.toString();
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPayoutBankAccountNoDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyAmountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBankAccountAmountLimitCheck()))
                    errorname=ErrorName.SYS_MEMBER_MONTHLY_BANKACCOUNT_AMT_LIMIT_EXCEEDED.toString();
                else
                    errorname=ErrorName.SYS_SYSTEM_MONTHLY_BANKACCOUNT_AMT_LIMIT_EXCEEDED.toString();
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPayoutBankAccountNoDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while cchecking check Payout BankAccountNo Daily Amount Limit", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkPayoutBankAccountNoDailyAmountLimit", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking check Payout BankAccountNo Daily Amount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkPayoutBankAccountNoDailyAmountLimit", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }
    public boolean checkPayoutBankAccountNoDailyCountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection dbConn = null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId =commonValidatorVO.getMerchantDetailsVO().getMemberId();
        try
        {
            logger.debug("Load member bankAccountNo limits ");
            int bankAccountNoDailyCount = 0;
            int bankAccountNoMonthlyCount = 0;
            dbConn = Database.getConnection();

            boolean isDailyCountLimitOver = false;
            boolean isMonthlyCountLimitOver = false;

            FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

            String payoutBankAccountNoCountQuery = "select bankAccountNoDailyCount,bankAccountNoMonthlyCount from merchant_configuration where memberid= ?";
            PreparedStatement preparedStatementMemberBankAccountNoCount = dbConn.prepareStatement(payoutBankAccountNoCountQuery);
            preparedStatementMemberBankAccountNoCount.setInt(1, Integer.parseInt(memberId));

            transactionLogger.error("bankAccountNo Daily Count  query" + preparedStatementMemberBankAccountNoCount);
            ResultSet resultSet = preparedStatementMemberBankAccountNoCount.executeQuery();

            if (resultSet.next())
            {
                bankAccountNoDailyCount = resultSet.getInt("bankAccountNoDailyCount");
                bankAccountNoMonthlyCount = resultSet.getInt("bankAccountNoMonthlyCount");
            }
            transactionLogger.error("bankAccountNo Daily Count  " + bankAccountNoDailyCount);

            StringBuffer totalAmountQuery = new StringBuffer("select count(*) from transaction_common as tc , transaction_safexpay_details as tsd  where  tc.trackingid=tsd.trackingid and  tsd.bankaccount = ? and tc.dtstamp > ?  and tc.status='payoutsuccessful' ");
            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBankAccountLimitCheck()))
                totalAmountQuery.append(" AND toid="+memberId);
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            //calendar.set(Calendar.AM_PM,Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;


            int transaction_daily_count = 0;


            PreparedStatement totalCountPreparedStatement = dbConn.prepareStatement(totalAmountQuery.toString());

            totalCountPreparedStatement.setString(1, commonValidatorVO.getReserveField2VO().getBankAccountNumber());
            totalCountPreparedStatement.setLong(2, todaysStartTimeInSecs);
            transactionLogger.error("checkTxnLimit for bankAccountNo DailyCount member Query Daily---" + totalCountPreparedStatement);
            ResultSet totalAmountResultSet = totalCountPreparedStatement.executeQuery();
            if (totalAmountResultSet.next())
            {
                transaction_daily_count = totalAmountResultSet.getInt(1);
            }
            transactionLogger.error("Daily  bankAccountNo Transaction Count : " + transaction_daily_count);


            // monthly total txt amount
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;
            double transaction_monthly_amount = 0;

            PreparedStatement totalMonthlyCountPreparedStatement = dbConn.prepareStatement(totalAmountQuery.toString());
            totalMonthlyCountPreparedStatement.setString(1, commonValidatorVO.getReserveField2VO().getBankAccountNumber());
            totalMonthlyCountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkTxnLimit for bankAccountNo member Query Monthly---" + totalCountPreparedStatement);
            ResultSet totalMonthlyAmountResultSet = totalCountPreparedStatement.executeQuery();
            if (totalMonthlyAmountResultSet.next())
            {
                transaction_monthly_amount = totalMonthlyAmountResultSet.getInt(1);
            }
            transactionLogger.error("Monthly bankAccountNo Transaction Count : " + transaction_monthly_amount);

            if (bankAccountNoDailyCount <= transaction_daily_count)
            {
                isDailyCountLimitOver = true;
            }
            if (bankAccountNoMonthlyCount <= transaction_daily_count)
            {
                isMonthlyCountLimitOver = true;
            }
            if (isDailyCountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBankAccountLimitCheck()))
                    errorname=ErrorName.SYS_MEMBER_DAILY_BANKACCOUNT_TXN_LIMIT_EXCEEDED.toString();
                else
                    errorname=ErrorName.SYS_SYSTEM_DAILY_BANKACCOUNT_TXN_LIMIT_EXCEEDED.toString();
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPayoutBankAccountNoDailyCountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyCountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBankAccountLimitCheck()))
                    errorname=ErrorName.SYS_MEMBER_MONTHLY_BANKACCOUNT_TXN_LIMIT_EXCEEDED.toString();

                else
                    errorname=ErrorName.SYS_SYSTEM_MONTHLY_BANKACCOUNT_TXN_LIMIT_EXCEEDED.toString();
                error = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPayoutBankAccountNoDailyCountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while checking check Payout BankAccountNo DailyCount Limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkPayoutBankAccountNoDailyCountLimit", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while checking check Payout BankAccountNo DailyCount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkPayoutBankAccountNoDailyCountLimit", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(dbConn);
        }
        return true;
    }

    public boolean checkCustomerIpDailyAmountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection connectionn              = null;
        String error                        = "";
        ErrorCodeListVO errorCodeListVO     = null;
        String amount                       = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId                     = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        double customerIpDailyAmountLimit   = 0;
        FailedTransactionLogEntry failedTransactionLogEntry = null;
        boolean isDailyAmountLimitOver      = false;
        double transaction_daily_amount     = 0;
        String errorname                    = "";

        try
        {
            logger.error("Load Member customerIpDailyAmountLimit limits " + commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

            connectionn                     = Database.getConnection();
            failedTransactionLogEntry       = new FailedTransactionLogEntry();

            Calendar calendar               = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;

            String customerIpDailyAmountLimits                      = "select customerIpDailyAmountLimit from merchant_configuration where memberid= ?";
            PreparedStatement preparedStatementMemberAmountLimits   = connectionn.prepareStatement(customerIpDailyAmountLimits);

            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                customerIpDailyAmountLimit = resultSet.getDouble("customerIpDailyAmountLimit");
            }
            transactionLogger.error("customerIpDailyAmountLimit  " + customerIpDailyAmountLimit);

            StringBuffer totalAmountQuery = new StringBuffer("select sum(amount) from transaction_common where customerIp=? and dtstamp > ?  and status in ('authsuccessful','capturesuccess','settled','reversed','markedforreversal','chargeback','chargebackreversed')");

            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerIpAmountLimitCheck()))
            {
                totalAmountQuery.append(" AND toid=" + memberId);
            }

            PreparedStatement totalAmountPreparedStatement = connectionn.prepareStatement(totalAmountQuery.toString());
            totalAmountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            totalAmountPreparedStatement.setLong(2, todaysStartTimeInSecs);

            transactionLogger.error("checkAmountLimit for customer Ip member Query Daily---" + totalAmountPreparedStatement);

            ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                transaction_daily_amount = totalAmountResultSet.getDouble(1);
            }
            transactionLogger.error("Daily customer Ip Transaction Amount : " + transaction_daily_amount);

            transaction_daily_amount += Double.parseDouble(amount);

            transactionLogger.error("Daily customer Ip Transaction Amount after todays amount : " + transaction_daily_amount);

            if (customerIpDailyAmountLimit < transaction_daily_amount)
            {
                isDailyAmountLimitOver = true;
            }

            if (isDailyAmountLimitOver)
            {
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerIpAmountLimitCheck()))
                {
                    errorname = ErrorName.SYS_MEMBER_DAILY_CUSTOMER_IP_AMT_LIMIT_EXCEEDED.toString();
                }else
                {
                    errorname = ErrorName.SYS_SYSTEM_DAILY_CUSTOMER_IP_AMT_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);

                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCustomerIpDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while check Customer Ip DailyAmount Limi ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerIpDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check Customer Ip DailyAmount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerIpDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connectionn);
        }
        return true;
    }

    public boolean checkCustomerIpDailyCount(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection connection               = null;
        String error                        = "";
        ErrorCodeListVO errorCodeListVO     = null;
        String amount                       = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId                     = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        boolean isDailyCountLimitOver       = false;
        int customerIpDailyCount            = 0;
        int transaction_daily_count         = 0;
        String errorname                    = "";
        FailedTransactionLogEntry failedTransactionLogEntry = null;
        try
        {
            connection                  = Database.getConnection();
            failedTransactionLogEntry   = new FailedTransactionLogEntry();
            Calendar calendar           = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;

            String membervpaDailyCountQuery                         = "select customerIpDailyCount from merchant_configuration where memberid= ?";
            PreparedStatement preparedStatementMembervpaDailyCount  = connection.prepareStatement(membervpaDailyCountQuery);
            preparedStatementMembervpaDailyCount.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMembervpaDailyCount.executeQuery();

            if (resultSet.next())
            {
                customerIpDailyCount = resultSet.getInt("customerIpDailyCount");
            }

            transactionLogger.error("Customer Ip Daily limits " + customerIpDailyCount);

            StringBuffer totalAmountQuery = new StringBuffer("select count(*) from transaction_common where customerIp=? and  dtstamp > ?  and status in ('authsuccessful','capturesuccess','settled','reversed','markedforreversal','chargeback','chargebackreversed')");

            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerIpLimitCheck())){
                totalAmountQuery.append(" AND toid="+memberId);
            }

            PreparedStatement totalCountPreparedStatement = connection.prepareStatement(totalAmountQuery.toString());
            totalCountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            totalCountPreparedStatement.setLong(2, todaysStartTimeInSecs);

            transactionLogger.error("checkTxnLimit for checkCustomerIpDailyCount Query Daily---" + totalCountPreparedStatement);
            ResultSet totalAmountResultSet = totalCountPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                transaction_daily_count = totalAmountResultSet.getInt(1);
            }

            transactionLogger.error("Daily CustomerIp Transaction Count : " + transaction_daily_count);

            if (customerIpDailyCount <= transaction_daily_count)
            {
                isDailyCountLimitOver = true;
            }

            if (isDailyCountLimitOver)
            {
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerIpLimitCheck()))
                {
                    errorname   = ErrorName.SYS_MEMBER_DAILY_CUSTOMER_IP_TXN_LIMIT_EXCEEDED.toString();
                }else{
                    errorname   = ErrorName.SYS_SYSTEM_DAILY_CUSTOMER_IP_TXN_LIMIT_EXCEEDED.toString();
                }
                error               = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO     = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);

                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCustomerIpDailyCount()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while check Customer Ip   Daily Count ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerIpDailyCount()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check Customer Ip   Daily Count ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerIpDailyCount()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return true;
    }


    public boolean checkCustomerNameDailyAmountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection connectionn              = null;
        String error                        = "";
        ErrorCodeListVO errorCodeListVO     = null;
        String amount                       = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId                     = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        double customerNameDailyAmount      = 0;
        FailedTransactionLogEntry failedTransactionLogEntry = null;
        boolean isDailyAmountLimitOver      = false;
        double transaction_daily_amount     = 0;
        String errorname                    = "";
        Functions functions = new Functions();

        try
        {
            logger.debug("Load Member checkCustomerNameDailyAmountLimit limits ");

            connectionn                     = Database.getConnection();
            failedTransactionLogEntry       = new FailedTransactionLogEntry();

            Calendar calendar               = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;

            String customerNameDailyAmountLimit                      = "select customerNameDailyAmountLimit from merchant_configuration where memberid= ?";

            PreparedStatement preparedStatementMemberAmountLimits    = connectionn.prepareStatement(customerNameDailyAmountLimit);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                customerNameDailyAmount = resultSet.getDouble("customerNameDailyAmountLimit");
            }
            transactionLogger.error("customerNameDailyAmount  " + customerNameDailyAmount);

            StringBuffer totalAmountQuery = new StringBuffer("select sum(amount) from transaction_common where firstname=? and lastname=? and dtstamp > ?  and status in ('authsuccessful','capturesuccess','settled','reversed','markedforreversal','chargeback','chargebackreversed')");

            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerNameAmountLimitCheck()))
            {
                totalAmountQuery.append(" AND toid=" + memberId);
            }

            PreparedStatement totalAmountPreparedStatement = connectionn.prepareStatement(totalAmountQuery.toString());

            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname())){
                totalAmountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getFirstname());
            }else{
                totalAmountPreparedStatement.setString(1, "");
            }

            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname())){
                totalAmountPreparedStatement.setString(2, commonValidatorVO.getAddressDetailsVO().getLastname());
            }else{
                totalAmountPreparedStatement.setString(2, "");
            }

            totalAmountPreparedStatement.setLong(3, todaysStartTimeInSecs);

            transactionLogger.error("checkAmountLimit for Customer Name member Query Daily---" + totalAmountPreparedStatement);

            ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                transaction_daily_amount = totalAmountResultSet.getDouble(1);
            }

            transaction_daily_amount += Double.parseDouble(amount);

            transactionLogger.error("Daily customer Name  Transaction Amount : " + transaction_daily_amount + " Daily Customer Name Transaction Amount after todays amount : " + transaction_daily_amount);

            if (customerNameDailyAmount < transaction_daily_amount)
            {
                isDailyAmountLimitOver = true;
            }

            if (isDailyAmountLimitOver)
            {
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerNameAmountLimitCheck()))
                {
                    errorname = ErrorName.SYS_MEMBER_DAILY_CUSTOMER_NAME_AMT_LIMIT_EXCEEDED.toString();
                }else
                {
                    errorname = ErrorName.SYS_SYSTEM_DAILY_CUSTOMER_NAME_AMT_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);

                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCustomerNameDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while check Customer Name DailyAmount Limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerNameDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check Customer Name DailyAmount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerNameDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connectionn);
        }
        return true;
    }

    public boolean checkCustomerNameDailyCount(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection connection               = null;
        String error                        = "";
        ErrorCodeListVO errorCodeListVO     = null;
        String amount                       = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId                     = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        boolean isDailyCountLimitOver       = false;
        int customerNameDailyCount            = 0;
        int transaction_daily_count         = 0;
        String errorname                    = "";
        FailedTransactionLogEntry failedTransactionLogEntry = null;
        Functions functions = new Functions();
        try
        {
            logger.error("Load Member checkCustomerNameDailyCount limits ");

            connection                  = Database.getConnection();
            failedTransactionLogEntry   = new FailedTransactionLogEntry();
            Calendar calendar           = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;

            String quertString                         = "select customerNameDailyCount from merchant_configuration where memberid= ?";
            PreparedStatement preparedStatementMembervpaDailyCount  = connection.prepareStatement(quertString);
            preparedStatementMembervpaDailyCount.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMembervpaDailyCount.executeQuery();

            if (resultSet.next())
            {
                customerNameDailyCount = resultSet.getInt("customerNameDailyCount");
            }

            transactionLogger.error("Customer Name Daily limits " + customerNameDailyCount);

            StringBuffer totalAmountQuery = new StringBuffer("select count(*) from transaction_common where firstname=? and lastname=? and  dtstamp > ?  and status in ('authsuccessful','capturesuccess','settled','reversed','markedforreversal','chargeback','chargebackreversed')");

            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerNameLimitCheck())){
                totalAmountQuery.append(" AND toid="+memberId);
            }

            PreparedStatement totalCountPreparedStatement = connection.prepareStatement(totalAmountQuery.toString());

            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname())){
                totalCountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getFirstname());
            }else{
                totalCountPreparedStatement.setString(1, "");
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname())){
                totalCountPreparedStatement.setString(2, commonValidatorVO.getAddressDetailsVO().getLastname());
            }else{
                totalCountPreparedStatement.setString(2, "");
            }

            totalCountPreparedStatement.setLong(3, todaysStartTimeInSecs);

            transactionLogger.error("checkTxnLimit for checkCustomerNameDailyCount Query Daily---" + totalCountPreparedStatement);
            ResultSet totalAmountResultSet = totalCountPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                transaction_daily_count = totalAmountResultSet.getInt(1);
            }

            transactionLogger.error("Daily Customer Name Transaction Count : " + transaction_daily_count);

            if (customerNameDailyCount <= transaction_daily_count)
            {
                isDailyCountLimitOver = true;
            }

            if (isDailyCountLimitOver)
            {
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerNameLimitCheck()))
                {
                    errorname   = ErrorName.SYS_MEMBER_DAILY_CUSTOMER_NAME_TXN_LIMIT_EXCEEDED.toString();
                }else{
                    errorname   = ErrorName.SYS_SYSTEM_DAILY_CUSTOMER_NAME_TXN_LIMIT_EXCEEDED.toString();
                }
                error               = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO     = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);

                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCustomerNameDailyCount()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while check Customer Name DailyCount Limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerNameDailyCount()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check Customer Name DailyCount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerNameDailyCount()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return true;
    }


    public boolean checkCustomerEmailDailyAmountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection connection                   = null;
        String error                            = "";
        ErrorCodeListVO errorCodeListVO         = null;
        String amount                           = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId                         = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        double customerEmailDailyAmountLimit    = 0;
        double transaction_daily_amount         = 0;
        boolean isDailyAmountLimitOver          = false;
        boolean isMonthlyAmountLimitOver = false;
        double customerEmailMonthlyAmountLimit = 0;
        String errorname                        = "";
        FailedTransactionLogEntry failedTransactionLogEntry = null;
        try
        {
            logger.debug("Load checkEmailDailyAmountLimit member ");

            connection                  = Database.getConnection();
            failedTransactionLogEntry   = new FailedTransactionLogEntry();
            Calendar calendar           = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;

            String memberEmailAmountLimits = "select customerEmailDailyAmountLimit,customerEmailMonthlyAmountLimit from merchant_configuration where memberid= ?";

            PreparedStatement preparedStatementMemberAmountLimits = connection.prepareStatement(memberEmailAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                customerEmailDailyAmountLimit = resultSet.getDouble("customerEmailDailyAmountLimit");
                customerEmailMonthlyAmountLimit = resultSet.getDouble("customerEmailMonthlyAmountLimit");
            }
            transactionLogger.error("Member Email Daily Amount limits " + customerEmailDailyAmountLimit);

            StringBuffer totalAmountQuery = new StringBuffer("select sum(trans_amount) FROM bin_details WHERE customer_email = ?  AND trans_dtstamp > ?  AND isSuccessful='Y' ");

            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerEmailAmountLimitCheck())){
                totalAmountQuery.append(" AND merchant_id="+memberId);
            }

            PreparedStatement totalAmountPreparedStatement = connection.prepareStatement(totalAmountQuery.toString());
            totalAmountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getEmail());
            totalAmountPreparedStatement.setLong(2, todaysStartTimeInSecs);

            transactionLogger.error("check CustomerEmailAmountLimit for  member Query Daily---" + totalAmountPreparedStatement);

            ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                transaction_daily_amount = totalAmountResultSet.getDouble(1);
            }

            transaction_daily_amount += Double.parseDouble(amount);

            transactionLogger.error("Daily Email Address Transaction Amount : " + transaction_daily_amount + " Daily Email Address Transaction Amount after todays amount : " + transaction_daily_amount);


            // monthly total txt amount
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;
            double transaction_monthly_amount = 0;

            //monthly query
            PreparedStatement totalMonthlyAmountPreparedStatement = connection.prepareStatement(totalAmountQuery.toString());
            totalMonthlyAmountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getEmail());
            totalMonthlyAmountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for Email Address  Query Monthly---" + totalMonthlyAmountPreparedStatement);
            ResultSet totalMonthlyAmountResultSet = totalMonthlyAmountPreparedStatement.executeQuery();
            if (totalMonthlyAmountResultSet.next())
            {
                transaction_monthly_amount = totalMonthlyAmountResultSet.getDouble(1);
            }
            transaction_monthly_amount += Double.parseDouble(amount);
            transactionLogger.error("monthly Email Address Transaction Amount : " + transaction_monthly_amount + " Monthly Email Address Transaction Amount after todays amount : " + transaction_monthly_amount);


            if (customerEmailDailyAmountLimit < transaction_daily_amount)
            {
                isDailyAmountLimitOver = true;
            }
            if (customerEmailMonthlyAmountLimit < transaction_monthly_amount)
            {
                isMonthlyAmountLimitOver = true;
            }

            if (isDailyAmountLimitOver)
            {

                if ("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerEmailAmountLimitCheck()))
                {
                    errorname = ErrorName.SYS_MEMBER_DAILY_EMAILADDRESS_AMT_LIMIT_EXCEEDED.toString();
                }else
                {
                    errorname = ErrorName.SYS_SYSTEM_DAILY_EMAILADDRESS_AMT_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCustomerEmailDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyAmountLimitOver)
            {

                if ("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerEmailAmountLimitCheck()))
                {
                    errorname = ErrorName.SYS_MEMBER_MONTHLY_EMAILADDRESS_AMT_LIMIT_EXCEEDED.toString();
                }else
                {
                    errorname = ErrorName.SYS_SYSTEM_MONTHLY_EMAILADDRESS_AMT_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCustomerEmailDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }


        }
        catch (SystemError systemError)
        {
            logger.error("Error while check Email Address DailyAmount Limi ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerEmailDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check Email Address DailyAmount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerEmailDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return true;
    }

    public boolean checkCustomerEmailDailyCount(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection connection           = null;
        String error                    = "";
        ErrorCodeListVO errorCodeListVO = null;
        String memberId                 = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        int customerEmailDailyCount     = 0;
        int customerEmailMonthlyCount   = 0;
        int transaction_daily_count     = 0;
        boolean isDailyCountLimitOver   = false;
        boolean isMonthlyCountLimitOver   = false;
        String errorname                = "";
        FailedTransactionLogEntry failedTransactionLogEntry = null;
        try
        {
            logger.debug("Load member checkEmailAddressDailyCountLimit limits ");
            connection                  = Database.getConnection();
            failedTransactionLogEntry   = new FailedTransactionLogEntry();
            Calendar calendar           = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;

            String memberEmailDailyCountQuery = "select customerEmailDailyCount,customerEmailMonthlyCount from merchant_configuration where memberid= ?";

            PreparedStatement preparedStatementMemberEmailDailyCount = connection.prepareStatement(memberEmailDailyCountQuery);
            preparedStatementMemberEmailDailyCount.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberEmailDailyCount.executeQuery();

            if (resultSet.next())
            {
                customerEmailDailyCount = resultSet.getInt("customerEmailDailyCount");
                customerEmailMonthlyCount = resultSet.getInt("customerEmailMonthlyCount");
            }
            transactionLogger.error("Email Address Daily  limits " + customerEmailDailyCount);

            StringBuffer totalTxnQuery = new StringBuffer("select count(*) from bin_details where customer_email = ? and trans_dtstamp > ?  AND isSuccessful='Y' ");

            if ("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerEmailLimitCheck()))
            {
                totalTxnQuery.append(" AND merchant_id=" + memberId);
            }

            PreparedStatement totalCountPreparedStatement = connection.prepareStatement(totalTxnQuery.toString());
            totalCountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getEmail());
            totalCountPreparedStatement.setLong(2, todaysStartTimeInSecs);

            transactionLogger.error("checkTxnLimit for Email Address member Query Daily---" + totalCountPreparedStatement);

            ResultSet totalAmountResultSet = totalCountPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                transaction_daily_count = totalAmountResultSet.getInt(1);
            }
            transactionLogger.error("Daily Email Address Transaction Count : " + transaction_daily_count);


            // monthly total txt amount
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;
            double transaction_monthly_amount = 0;

            PreparedStatement totalMonthlyCountPreparedStatement = connection.prepareStatement(totalTxnQuery.toString());
            totalMonthlyCountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getEmail());
            totalMonthlyCountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkTxnLimit for Email Address  Query Monthly---" + totalCountPreparedStatement);
            ResultSet totalMonthlyAmountResultSet = totalCountPreparedStatement.executeQuery();
            if (totalMonthlyAmountResultSet.next())
            {
                transaction_monthly_amount = totalMonthlyAmountResultSet.getInt(1);
            }
            transactionLogger.error("Monthly Email Address Transaction Count : " + transaction_monthly_amount);



            if (customerEmailDailyCount <= transaction_daily_count)
            {
                isDailyCountLimitOver = true;
            }
            if (customerEmailMonthlyCount <= transaction_monthly_amount)
            {
                isMonthlyCountLimitOver = true;
            }
            if (isDailyCountLimitOver)
            {
                if ("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerEmailLimitCheck()))
                {
                    errorname = ErrorName.SYS_MEMBER_DAILY_EMAILADDRESS_TXN_LIMIT_EXCEEDED.toString();
                }else
                {
                    errorname = ErrorName.SYS_SYSTEM_DAILY_EMAILADDRESS_TXN_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCustomerEmailDailyCount()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyCountLimitOver)
            {
                if ("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerEmailLimitCheck()))
                {
                    errorname = ErrorName.SYS_MEMBER_MONTHLY_EMAILADDRESS_TXN_LIMIT_EXCEEDED.toString();
                }else
                {
                    errorname = ErrorName.SYS_SYSTEM_MONTHLY_EMAILADDRESS_TXN_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCustomerEmailDailyCount()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while check Email Address DailyCount Limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerEmailDailyCount()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check Email Address DailyCount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkCustomerEmailDailyCount()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return true;
    }


    public boolean checkPhoneDailyAmountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection connection           = null;
        String error                    = "";
        ErrorCodeListVO errorCodeListVO = null;
        String amount                   = commonValidatorVO.getTransDetailsVO().getAmount();
        String memberId                 = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        double customerPhoneDailyAmountLimit    = 0;
        boolean isDailyAmountLimitOver          = false;
        FailedTransactionLogEntry failedTransactionLogEntry = null;
        double transaction_daily_amount         = 0;
        boolean isMonthlyAmountLimitOver = false;
        double customerPhoneMonthlyAmountLimit = 0;
        try
        {
            logger.debug("Load member Phone limits ");

            connection                      = Database.getConnection();
            failedTransactionLogEntry       = new FailedTransactionLogEntry();

            String memberPhoneAmountLimits                          = "select customerPhoneDailyAmountLimit,customerPhoneMonthlyAmountLimit from merchant_configuration where memberid= ?";

            PreparedStatement preparedStatementMemberAmountLimits   = connection.prepareStatement(memberPhoneAmountLimits);
            preparedStatementMemberAmountLimits.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberAmountLimits.executeQuery();

            if (resultSet.next())
            {
                customerPhoneDailyAmountLimit = resultSet.getDouble("customerPhoneDailyAmountLimit");
                customerPhoneMonthlyAmountLimit = resultSet.getDouble("customerPhoneMonthlyAmountLimit");
            }
            transactionLogger.error("Member Phone Daily Amount limits " + customerPhoneDailyAmountLimit);

            StringBuffer totalAmountQuery = new StringBuffer("select sum(trans_amount) from bin_details where customer_phone = ? and trans_dtstamp > ?  AND isSuccessful='Y'");

            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerPhoneAmountLimitCheck()))
            {
                totalAmountQuery.append(" AND merchant_id="+memberId);
            }

            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            //calendar.set(Calendar.AM_PM,Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;


            PreparedStatement totalAmountPreparedStatement = connection.prepareStatement(totalAmountQuery.toString());
            totalAmountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getPhone());
            totalAmountPreparedStatement.setLong(2, todaysStartTimeInSecs);
            transactionLogger.error("check CustomerPhoneAmountLimit for  member Query Daily---" + totalAmountPreparedStatement);

            ResultSet totalAmountResultSet = totalAmountPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                transaction_daily_amount = totalAmountResultSet.getDouble(1);
            }
            transactionLogger.error("Daily Phone Transaction Amount : " + transaction_daily_amount);
            transaction_daily_amount += Double.parseDouble(amount);

            transactionLogger.error("Daily Phone Transaction Amount after todays amount : " + transaction_daily_amount);

            // monthly total txt amount
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;
            double transaction_monthly_amount = 0;

            //monthly query
            PreparedStatement totalMonthlyAmountPreparedStatement = connection.prepareStatement(totalAmountQuery.toString());
            totalMonthlyAmountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getPhone());
            totalMonthlyAmountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkAmountLimit for Phones  Query Monthly---" + totalMonthlyAmountPreparedStatement);
            ResultSet totalMonthlyAmountResultSet = totalMonthlyAmountPreparedStatement.executeQuery();
            if (totalMonthlyAmountResultSet.next())
            {
                transaction_monthly_amount = totalMonthlyAmountResultSet.getDouble(1);
            }
            transaction_monthly_amount += Double.parseDouble(amount);
            transactionLogger.error("monthly Phone Transaction Amount : " + transaction_monthly_amount + " Monthly Phone Transaction Amount after todays amount : " + transaction_monthly_amount);

            if (customerPhoneDailyAmountLimit < transaction_daily_amount)
            {
                isDailyAmountLimitOver = true;
            }
            if (customerPhoneMonthlyAmountLimit < transaction_monthly_amount)
            {
                isMonthlyAmountLimitOver = true;
            }
            if (isDailyAmountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerPhoneAmountLimitCheck()))
                {
                    errorname   = ErrorName.SYS_MEMBER_DAILY_PHONE_AMT_LIMIT_EXCEEDED.toString();
                }
                else
                {
                    errorname   = ErrorName.SYS_SYSTEM_DAILY_PHONE_AMT_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPhoneDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyAmountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerPhoneAmountLimitCheck()))
                {
                    errorname   = ErrorName.SYS_MEMBER_MONTHLY_PHONE_AMT_LIMIT_EXCEEDED.toString();
                }
                else
                {
                    errorname   = ErrorName.SYS_SYSTEM_MONTHLY_PHONE_AMT_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPhoneDailyAmountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error while check Phone DailyAmount Limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkPhoneDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check Phone DailyAmount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkPhoneDailyAmountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return true;
    }
    public boolean checkPhoneDailyCountLimit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Connection connection           = null;
        String error                    = "";
        ErrorCodeListVO errorCodeListVO = null;
        String memberId                 = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        int transaction_daily_count     = 0;
        int customerPhoneDailyCount     = 0;
        int customerPhoneMonthlyCount     = 0;
        boolean isDailyCountLimitOver = false;
        boolean isMonthlyCountLimitOver = false;
        FailedTransactionLogEntry failedTransactionLogEntry = null;
        try
        {
            logger.debug("Load member Phone limits ");
            connection                      = Database.getConnection();
            failedTransactionLogEntry       = new FailedTransactionLogEntry();

            String memberEmailDailyCountQuery                           = "select customerPhoneDailyCount,customerPhoneMonthlyCount from merchant_configuration where memberid= ?";
            PreparedStatement preparedStatementMemberEmailDailyCount    = connection.prepareStatement(memberEmailDailyCountQuery);
            preparedStatementMemberEmailDailyCount.setInt(1, Integer.parseInt(memberId));

            ResultSet resultSet = preparedStatementMemberEmailDailyCount.executeQuery();

            if (resultSet.next())
            {
                customerPhoneDailyCount = resultSet.getInt("customerPhoneDailyCount");
                customerPhoneMonthlyCount = resultSet.getInt("customerPhoneMonthlyCount");
            }
            transactionLogger.error("Phone Daily  limits " + customerPhoneDailyCount);

            StringBuffer totalTxnQuery = new StringBuffer("select count(*) from bin_details where customer_phone = ? and trans_dtstamp > ?  AND isSuccessful='Y'");

            if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerPhoneLimitCheck())){
                totalTxnQuery.append(" AND merchant_id="+memberId);
            }

            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            //calendar.set(Calendar.AM_PM,Calendar.AM);
            long todaysStartTimeInSecs = calendar.getTimeInMillis() / 1000;


            PreparedStatement totalCountPreparedStatement = connection.prepareStatement(totalTxnQuery.toString());
            totalCountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getPhone());
            totalCountPreparedStatement.setLong(2, todaysStartTimeInSecs);

            transactionLogger.error("checkTxnLimit for Phone member Query Daily---" + totalCountPreparedStatement);

            ResultSet totalAmountResultSet = totalCountPreparedStatement.executeQuery();

            if (totalAmountResultSet.next())
            {
                transaction_daily_count = totalAmountResultSet.getInt(1);
            }
            transactionLogger.error("Daily Phone Transaction Count : " + transaction_daily_count);


            // monthly total txt amount

            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            transactionLogger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;
            double transaction_monthly_amount = 0;

            PreparedStatement totalMonthlyCountPreparedStatement = connection.prepareStatement(totalTxnQuery.toString());
            totalMonthlyCountPreparedStatement.setString(1, commonValidatorVO.getAddressDetailsVO().getPhone());
            totalMonthlyCountPreparedStatement.setLong(2, monthsStartTimeInSecs);
            transactionLogger.error("checkTxnLimit for Phone  Query Monthly---" + totalCountPreparedStatement);
            ResultSet totalMonthlyAmountResultSet = totalCountPreparedStatement.executeQuery();
            if (totalMonthlyAmountResultSet.next())
            {
                transaction_monthly_amount = totalMonthlyAmountResultSet.getInt(1);
            }
            transactionLogger.error("Monthly Phone Transaction Count : " + transaction_monthly_amount);


            if (customerPhoneDailyCount <= transaction_daily_count)
            {
                isDailyCountLimitOver = true;
            }
            if (customerPhoneMonthlyCount <= transaction_monthly_amount)
            {
                isMonthlyCountLimitOver = true;
            }
            if (isDailyCountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerPhoneLimitCheck()))
                {
                    errorname   = ErrorName.SYS_MEMBER_DAILY_PHONE_TXN_LIMIT_EXCEEDED.toString();
                }
                else
                {
                    errorname   = ErrorName.SYS_SYSTEM_DAILY_PHONE_TXN_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPhoneDailyCountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (isMonthlyCountLimitOver)
            {
                String errorname="";
                if("Member".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCustomerPhoneLimitCheck()))
                {
                    errorname   = ErrorName.SYS_MEMBER_MONTHLY_PHONE_TXN_LIMIT_EXCEEDED.toString();
                }
                else
                {
                    errorname   = ErrorName.SYS_SYSTEM_MONTHLY_PHONE_TXN_LIMIT_EXCEEDED.toString();
                }

                error           = "Transaction not permitted:::Please contact your Administrator.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname,ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkPhoneDailyCountLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("Error while check Phone DailyCount Limit ", systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkPhoneDailyCountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Error while check Phone DailyCount Limit ", e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(), "checkPhoneDailyCountLimit()", null, "Common", "Internal error while processing your request", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return true;
    }

}