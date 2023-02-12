package payment;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TokenManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PaymentProcessFactory;
import com.payment.common.core.CommResponseVO;
import com.payment.validators.vo.CommonValidatorVO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/3/13
 * Time: 9:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class Process3DConfirmation extends PzServlet
{
    private static Logger logger = new Logger(Process3DConfirmation.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(Process3DConfirmation.class.getName());

    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PfsServlet");
    //final static String POSTURL = RB.getString("POSTURL");
    //final static String ICICIEMAIL = RB.getString("NOPROOFREQUIRED_EMAIL");
    //final static String MANAGEMENT_NOTIFY_EMAIL = RB.getString("MANAGEMENT_NOTIFY_EMAIL");
            

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering doService of Pfs3DWaitServlet");
        transactionLogger.debug("Entering doService of Pfs3DWaitServlet");
        /*System.out.println("Entering doService of Pfs3DWaitServlet");*/
        TransactionUtility transactionUtility = new TransactionUtility();
        PrintWriter pWriter = res.getWriter();
        String trackingId = req.getParameter("t");
        String ctoken = req.getParameter("ctoken");
        logger.debug("---ctoken in process3dconfirmation---"+ctoken);
        String paRes = req.getParameter("PaRes");
        logger.debug("PaRes in process3d---"+req.getParameter("PaRes"));
        String token="";
        String isSuccessful="N";
        String isTokenizationAllowed="N";
        String isAddressDetailsRequired="Y";
        ActionEntry entry = new ActionEntry();

        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId),null);

        //Calling Transaction3D method of common
        Hashtable responseHash = paymentProcess.process3DConfirmation(trackingId, paRes);


        String mailtransactionStatus = String.valueOf(responseHash.get("status"));
        String billingDiscriptor = String.valueOf(responseHash.get("displayName"));
        isSuccessful = String.valueOf(responseHash.get("issuccessful"));
        String finalStatus = String.valueOf(responseHash.get("finalStatus"));

        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        Functions functions=new Functions();


        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setTrackingid(trackingId);
        String memberId = null;
        String status = "";
        String isService = "";
        String respStatus="";
        String clkey="";
        Connection connection = null;
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        try
        {
            connection = Database.getConnection();
            String sql = "Select description,orderdescription, amount, currency, redirecturl, toid, status from transaction_common where trackingid =?";
            PreparedStatement p = connection.prepareStatement(sql);
            p.setString(1, trackingId);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                commonValidatorVO.getTransDetailsVO().setOrderId(rs.getString("description"));
                commonValidatorVO.getTransDetailsVO().setOrderDesc(rs.getString("orderdescription"));
                commonValidatorVO.getTransDetailsVO().setAmount(rs.getString("amount"));
                commonValidatorVO.getTransDetailsVO().setCurrency(rs.getString("currency"));
                commonValidatorVO.getTransDetailsVO().setRedirectUrl(rs.getString("redirecturl"));
                memberId = rs.getString("toid");
                status = rs.getString("status");
            }
            merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            String sql1 = "Select merchantlogoname, partnerId, isPoweredBy, autoredirect,isTokenizationAllowed,isService,isAddrDetailsRequired,clkey from members where memberid =?";
            p = connection.prepareStatement(sql1);
            p.setString(1, memberId);
            rs = p.executeQuery();
            if (rs.next())
            {
                //commonValidatorVO.setLogoName(rs.getString("merchantlogoname"));
                commonValidatorVO.setParetnerId(rs.getString("partnerId"));
                commonValidatorVO.getMerchantDetailsVO().setPoweredBy(rs.getString("isPoweredBy"));
                commonValidatorVO.getMerchantDetailsVO().setAutoRedirect(rs.getString("autoredirect"));
                isTokenizationAllowed=rs.getString("isTokenizationAllowed");
                isAddressDetailsRequired=rs.getString("isAddrDetailsRequired");
                isService = rs.getString("isService");
                clkey = rs.getString("clkey");
            }

            String sql2 = "Select logoName,partnerName from partners where partnerId =?";
            p = connection.prepareStatement(sql2);
            p.setString(1, commonValidatorVO.getParetnerId());
            rs = p.executeQuery();
            if (rs.next())
            {
                commonValidatorVO.setLogoName(rs.getString("logoName"));
                commonValidatorVO.setPartnerName(rs.getString("partnerName"));
            }
            if("Y".equals(isSuccessful) && "Y".equals(isTokenizationAllowed))
            {
                TokenManager tokenManager=new TokenManager();
                String cardNumber= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                String strToken = tokenManager.isCardAvailable(memberId,cardNumber);
                if(functions.isValueNull(strToken))
                {
                    token=strToken;
                }
                else
                {
                    token=tokenManager.createTokenByTrackingId(trackingId,transactionDetailsVO);
                }
            }


            auditTrailVO.setActionExecutorId(memberId);
            auditTrailVO.setActionExecutorName("Customer");
            StringBuffer sb = new StringBuffer();
            sb.append("update transaction_common set ");
            if (finalStatus!=null && "success".equalsIgnoreCase(finalStatus))
            {
                respStatus="Y";
                if ("authstarted".equalsIgnoreCase(status))
                {
                    if ("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                    {
                        sb.append(" captureamount='" + commonValidatorVO.getTransDetailsVO().getAmount() + "'");
                        sb.append(", status='capturesuccess'");
                        commResponseVO.setDescription("Transaction Successful");
                        commResponseVO.setTransactionType("sale");
                        commResponseVO.setStatus(mailtransactionStatus);
                        entry.actionEntryForCommon(trackingId, commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                        logger.debug("update from auth success if block isService--Y");
                    }
                    else
                    {
                        //sb.append(" captureamount='" + amount + "'");
                        sb.append("status='authsuccessful'");
                        commResponseVO.setDescription("Transaction Authorised");
                        commResponseVO.setTransactionType("Auth");
                        commResponseVO.setStatus(mailtransactionStatus);
                        entry.actionEntryForCommon(trackingId, commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                        logger.debug("update from auth success block isService--N");
                    }
                }

            }
            else if(finalStatus!=null && "fail".equalsIgnoreCase(finalStatus))
            {
                respStatus="N";
                if ("authstarted".equalsIgnoreCase(status))
                {
                    sb.append("status='authfailed'");
                    commResponseVO.setDescription("Transaction Failed");
                    commResponseVO.setStatus(mailtransactionStatus);
                    entry.actionEntryForCommon(trackingId, commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO,auditTrailVO,null);
                    logger.debug("update from auth success block isService--N");
                }
            }
            else
            {
                respStatus="N";
                if ("authstarted".equalsIgnoreCase(status))
                {
                    sb.append("status='failed'");
                    commResponseVO.setDescription("Transaction Failed");
                    commResponseVO.setStatus(mailtransactionStatus);
                    entry.actionEntryForCommon(trackingId, commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, commResponseVO,auditTrailVO,null);
                    logger.debug("update from auth success block isService--N");
                }
            }
            sb.append(" where trackingid = "+trackingId);
            logger.debug("common update query in Process3DConfirmation---"+sb.toString());
            Database.executeUpdate(sb.toString(), connection);
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), finalStatus, null,null);
        }
        catch (Exception e)
        {
            logger.error("Exception in Process3DConfirmation---",e);

        }
        finally
        {
            Database.closeConnection(connection);
        }

        commonValidatorVO.setToken(token);
        //auto redirect check
        if(commonValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
        {
            req.setAttribute("transDetail",commonValidatorVO);
            req.setAttribute("responceStatus",mailtransactionStatus);
            req.setAttribute("displayName",billingDiscriptor);
            req.setAttribute("ctoken",ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken="+ctoken);
            requestDispatcher.forward(req, res);
            return;
        }
        else
        {
            try
            {
                String checksum="";
                try
                {
                    checksum= Checksum.generateChecksumForStandardKit(trackingId, commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getAmount(), respStatus,clkey);
                }
                catch (NoSuchAlgorithmException e)
                {
                    respStatus="N";
                    transactionLogger.error("NoSuchAlgorithmException:::::"+e);
                }
                transactionUtility.doAutoRedirect(commonValidatorVO.getTransDetailsVO().getRedirectUrl(),ctoken,res,respStatus,billingDiscriptor,trackingId,commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getAmount(),checksum,token);
            }
            catch (SystemError systemError)
            {
                logger.error("systemError in Process3D---"+systemError);
            }

        }
    }

}