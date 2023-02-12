package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;
import sun.misc.BASE64Decoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;

/**
 * Created by ThinkPadT410 on 2/4/2017.
 */
public class UnicreditFrontEndServlet extends PzServlet
{
    private static VoucherMoneyLogger transactionLogger = new VoucherMoneyLogger(UnicreditFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.debug("-------Enter in doService of UnicreditFrontEndServlet-------");
        Functions functions = new Functions();

        for(Object key : req.getParameterMap().keySet())
        {
            transactionLogger.debug("----for loop UnicreditFrontEndServlet-----"+key+"="+req.getParameter((String)key)+"--------------");
        }

        String rMsg = "";
        if(functions.isValueNull(req.getParameter("eBorica")))
        {
            //this.verifyBOResp(req.getParameter("eBorica"),"");
            CommResponseVO commResponseVO = new CommResponseVO();

            rMsg = req.getParameter("eBorica");

            rMsg = URLDecoder.decode(rMsg, "windows-1251");

            transactionLogger.debug("rMsg---" + rMsg);

            BASE64Decoder decoder = new BASE64Decoder();
            rMsg = new String(decoder.decodeBuffer(rMsg));
            //System.out.println("rMsg---"+rMsg);

            String transCode = rMsg.substring(0, 2);
            transactionLogger.debug("Response.TransactonCode = " + transCode);
            String transTime = rMsg.substring(2, 14);
            transactionLogger.debug("Response.TransactionTime = " + transTime);
            String amount = rMsg.substring(16, 28);
            transactionLogger.debug("Response.Amount = " + amount);
            String termID = rMsg.substring(28, 36);
            transactionLogger.debug("Response.TID = " + termID);
            String trackingid = rMsg.substring(36, 51);
            transactionLogger.debug("Response.OredrID = " + trackingid);
            String respCode = rMsg.substring(51, 53);
            transactionLogger.debug("Response.ResponseCode = " + respCode);
            String protVer = rMsg.substring(53, 56);
            transactionLogger.debug("Response.ProtocolVersion = " + protVer);

            //Transaction Details
            String toid = "";
            String description = "";
            String redirectUrl = "";
            String accountId = "";
            String orderDesc = "";
            String currency = "";
            String remark = "";
            String status = "";
            String captureAmount="";

            //Merchant Details
            String clkey = "";
            String checksumAlgo = "";
            String autoredirect = "";
            String isPowerBy = "";
            String logoName = "";
            String partnerName = "";
            String displayName = "";
            String dbStatus = "";
            String email = "";

            String paymodeid = "";
            String cardtypeid= "";
            String previousStatus = "";

            String tmpl_amt = "";
            String tmpl_currency = "";

            TransactionManager transactionManager = new TransactionManager();
            MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
            MerchantDAO merchantDAO = new MerchantDAO();
            AuditTrailVO auditTrailVO = new AuditTrailVO();
            ActionEntry entry = new ActionEntry();
            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

            HttpSession session = req.getSession(true);

            CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
            GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();

            Connection con = null;
            try
            {
                con = Database.getConnection();
                //transactionLogger.error("before fetching from common---");
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);

                toid = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId = transactionDetailsVO.getAccountId();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                amount = transactionDetailsVO.getAmount();
                captureAmount = transactionDetailsVO.getCaptureAmount();
                email = transactionDetailsVO.getEmailaddr();
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                clkey = merchantDetailsVO.getKey();
                checksumAlgo = merchantDetailsVO.getChecksumAlgo();
                autoredirect = merchantDetailsVO.getAutoRedirect();
                isPowerBy = merchantDetailsVO.getIsPoweredBy();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();

                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();

                previousStatus = transactionDetailsVO.getStatus();

                //commResponseVO.setTransactionId(transactionId);
                commResponseVO.setErrorCode(respCode);
                commResponseVO.setTransactionType("sale");

                auditTrailVO.setActionExecutorName("Customer");
                auditTrailVO.setActionExecutorId(toid);
                StringBuffer dbBuffer = new StringBuffer();

                if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(previousStatus))
                {
                    if(respCode.equals("00"))
                    {
                        displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        dbStatus = "Successful";
                        status = "success";
                        remark = "Transaction Successful";

                        commResponseVO.setDescription(remark);
                        commResponseVO.setStatus(status);
                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescriptor(displayName);
                        commResponseVO.setTransactionStatus(dbStatus);

                        dbStatus = "capturesuccess";
                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',status='capturesuccess',remark='"+remark+"' where trackingid="+trackingid);
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        transactionLogger.debug("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                    }
                    else
                    {
                        status = "fail";
                        remark = "Transaction Failed";
                        commResponseVO.setStatus(status);
                        commResponseVO.setDescription(remark);
                        commResponseVO.setRemark(remark);
                        dbStatus = "authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',remark='"+remark+"' where trackingid="+trackingid);
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        transactionLogger.debug("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                    }

                    statusSyncDAO.updateAllTransactionFlowFlag(trackingid, dbStatus);
                }
                commonValidatorVO.setTrackingid(trackingid);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    TransactionUtility transactionUtility = new TransactionUtility();
                    transactionLogger.error("respStatus in Y---" + dbStatus);
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, dbStatus, displayName);
                }
                else
                {

                    session.setAttribute("ctoken", ctoken);
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", dbStatus);
                    req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    String confirmationPage = "";
                    String version = (String)session.getAttribute("version");
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);

                    rd.forward(req, res);
                }
            }
            catch (Exception e)
            {
                //transactionLogger.error("PZDBViolationException----", e);

            }finally
            {
                Database.closeConnection(con);
            }
        }
    }

    public void verifyBOResp(String BOResponseMessage,String publicKeyAPGW)
    {
        // manipulation of the $_GET["eBorica"] parameter

        try
        {
            String rMsg = BOResponseMessage;

            rMsg = URLDecoder.decode(rMsg, "windows-1251");

            //System.out.println("rMsg---"+rMsg);

            BASE64Decoder decoder = new BASE64Decoder();
            rMsg = new String(decoder.decodeBuffer(rMsg));
            //System.out.println("rMsg---"+rMsg);

            String transCode = rMsg.substring(0, 2);
            transactionLogger.debug("Response.TransactonCode = " + transCode);
            String transTime = rMsg.substring(2, 14);
            transactionLogger.debug("Response.TransactionTime = " + transTime);
            String amount = rMsg.substring(16, 28);
            transactionLogger.debug("Response.Amount = " + amount);
            String termID = rMsg.substring(28, 36);
            transactionLogger.debug("Response.TID = " + termID);
            String orderID = rMsg.substring(36, 51);
            transactionLogger.debug("Response.OredrID = " + orderID);
            String respCode = rMsg.substring(51, 53);
            transactionLogger.debug("Response.ResponseCode = " + respCode);
            String protVer = rMsg.substring(53, 56);
            transactionLogger.debug("Response.ProtocolVersion = " + protVer);


        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---",e);
        }
    }
}