import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.EcorePaymentGateway;
import com.directi.pg.core.paymentgateway.QwipiPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.ManualRecurringManager;
import com.manager.dao.MerchantDAO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.ManualRebillResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.checkers.PaymentChecker;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.utils.ManualTransactionUtils;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 1/19/2016.
 */
public class ManualRebill extends HttpServlet
{
    private static Logger log = new Logger(ManualRebill.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();

        Merchants merchants = new Merchants();
        Transaction transaction = new Transaction();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ManualRebillResponseVO manualRebillResponseVO = new ManualRebillResponseVO();
        ManualTransactionUtils manualTransactionUtils = new ManualTransactionUtils();
        PaymentChecker paymentChecker = new PaymentChecker();
        ErrorCodeListVO errorCodeListVO = null;

        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        ManualRecurringManager manualRecurringManager = new ManualRecurringManager();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String remoteAddr = Functions.getIpAddress(req);
        ServletContext application = getServletContext();
        String errormsg = "";
        String error = "";

        String terminalid = req.getParameter("terminalid");
        String sb = req.getParameter("terminalbuffer");

        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();

        errormsg = errormsg + validateParameters(req);
        if (errormsg != null && !errormsg.equals(""))
        {
            req.setAttribute("error", errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/manualRebill.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }

        String trackingId = req.getParameter("trackingid");
        String amount = req.getParameter("amount");
        String description = req.getParameter("description");
        String toId = (String) session.getAttribute("merchantid");
        String accountId = "";
        //String email = req.getParameter("emailaddress");
        if(null==transaction.getAccountID(trackingId))
        {
            manualRebillResponseVO.setErrorMessage("No Record found for given Details");
            req.setAttribute("recurringstatus", manualRebillResponseVO);
            RequestDispatcher rd = req.getRequestDispatcher("/recurringModule.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        else
        {
            accountId = transaction.getAccountID(trackingId);
        }

        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String fromtype = account.getGateway();

        if(!isTrackingIdExistInDB(fromtype,trackingId,accountId,toId))
        {
            manualRebillResponseVO.setErrorMessage("No Record found for given Details");
            req.setAttribute("recurringstatus", manualRebillResponseVO);
            RequestDispatcher rd = req.getRequestDispatcher("/recurringModule.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        Functions functions=new Functions();
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
        if (functions.isValueNull(terminalid) && !terminalid.equalsIgnoreCase("all"))
        {
            recurringBillingVO.setTerminalid("(" + recurringBillingVO.getTerminalid() + ")");
        }
        else
        {
            recurringBillingVO.setTerminalid(sb.toString());
        }

        genericTransDetailsVO.setFromtype(fromtype);
        merchantDetailsVO.setAccountId(accountId);
        genericTransDetailsVO.setAmount(amount);
        genericTransDetailsVO.setHeader(header);
        genericAddressDetailsVO.setIp(remoteAddr);
        //genericAddressDetailsVO.setEmail(email);
        genericTransDetailsVO.setOrderId(description);
        merchantDetailsVO.setMemberId(toId);


        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setTrackingid(trackingId);
        commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
        TransactionHelper transactionHelper = new TransactionHelper();

        try
        {

            MerchantDAO merchantDAO = new MerchantDAO();
            MerchantDetailsVO merchantDetailsVO1 = merchantDAO.getMemberDetails(toId);
            merchantDetailsVO.setKey(merchantDetailsVO1.getKey());
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

            String uniqueorder = "";
            uniqueorder = transactionHelper.checkorderuniqueness(merchantDetailsVO.getMemberId(), "", commonValidatorVO.getTransDetailsVO().getOrderId());
            if (!uniqueorder.equals(""))
            {
                error = commonValidatorVO.getTransDetailsVO().getOrderId() + "-Duplicate Order Id OR " + uniqueorder;
                errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            String currency = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getCurrency();
            //specifidc JPY Currency Validation
            if(currency.equals("JPY"))
            {
                if(!paymentChecker.isAmountValidForJPY(currency,genericTransDetailsVO.getAmount()))
                {
                    error = "JPY Currency does not have cent value after decimal. Please give .00 as decimal value";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
                    //failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.MERCHANT_JPY_CURRENCY.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performManualRecurringValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
                }
            }

            manualRecurringManager.checkAmountlimitForRebill(amount,toId,accountId);
            if (commonValidatorVO.getErrorMsg() != null && commonValidatorVO.getErrorMsg().trim().length() > 0)
            {
                manualRebillResponseVO.setErrorMessage("No Records Found");
                req.setAttribute("recurringstatus", manualRebillResponseVO);
                RequestDispatcher rd = req.getRequestDispatcher("/recurringModule.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            else
            {
                manualRebillResponseVO = manualTransactionUtils.manualSingleCall(commonValidatorVO,application);
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(manualRebillResponseVO.getTrackingId()), manualRebillResponseVO.getStatus(), null, null);
                req.setAttribute("recurringstatus", manualRebillResponseVO);
                RequestDispatcher rd = req.getRequestDispatcher("/rebillResponse.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException ManualRebill :::::",e);
            req.setAttribute("error", e.getPzConstraint().getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/manualRebill.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException ManualRebill :::::", e);
            req.setAttribute("error", e.getPzdbConstraint().getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/manualRebill.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
    }
    public boolean isTrackingIdExistInDB(String fromtype, String trackingId,String accountid,String toid)
    {
        boolean id = false;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            connection = Database.getConnection();
            String transaction_table = "transaction_common";
            if (QwipiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                transaction_table = "transaction_qwipi";
            }
            else if (EcorePaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                transaction_table = "transaction_ecore";
            }
            else
            {
                transaction_table = "transaction_common";
            }
            String query = "select toid from " + transaction_table + " where trackingid = ? AND accountid=? AND toid=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,trackingId);
            preparedStatement.setString(2,accountid);
            preparedStatement.setString(3,toid);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                id = true;
            }
            log.debug("isTrackingIdExistInDB-----"+preparedStatement+id);
        }
        catch(SystemError systemError)
        {
            log.debug("System error"+systemError);
        }
        catch (SQLException e)
        {
            log.debug("SQL exception"+e);
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return id;
    }
    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.AMOUNT);
        inputFieldsListOptional.add(InputFields.DESCRIPTION);
        //inputFieldsListOptional.add(InputFields.EMAILADDR);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        log.debug("error in helper---------->"+errorName.toString());
        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }
}