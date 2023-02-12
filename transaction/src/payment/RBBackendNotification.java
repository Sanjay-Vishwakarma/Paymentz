package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/23/15
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class RBBackendNotification extends PzServlet
{
    public RBBackendNotification()
    {
        super();
    }

    private static Logger log = new Logger(RBBackendNotification.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(RBBackendNotification.class.getName());
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
        log.debug("-------Enter in doService of RBBackendNotification---ip address----"+req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------Enter in doService of RBBackendNotification-------");

        for(Object key : req.getParameterMap().keySet())
        {
            log.debug("----for loop RBBackendNotification-----"+key+"="+req.getParameter((String)key)+"--------------");
            transactionLogger.debug("----for loop RBBackendNotification-----"+key+"="+req.getParameter((String)key)+"--------------");
        }

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = new CommRequestVO();

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO=new AuditTrailVO();

        String trackingid = req.getParameter("MerchantID");

        log.debug("tracking id in RBBackendNotification---"+req.getParameter("MerchantID"));

        Connection con = null;
        PreparedStatement p=null;
        ResultSet rs = null;
        String toid = "";
        String accountId = "";
        String dbStatus = "";
        String status = "";
        String isService = "";
        String amount = "";
        TransactionManager transactionManager=new TransactionManager();
        /*CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();*/
        Functions functions=new Functions();

        try
        {
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
            toid = transactionDetailsVO.getToid();
            accountId = transactionDetailsVO.getAccountId();
            dbStatus = transactionDetailsVO.getStatus();
            amount = transactionDetailsVO.getAmount();

            con = Database.getConnection();
            String query = "select isService FROM members WHERE memberid=?";
            p=con.prepareStatement(query);
            p.setString(1,toid);
            rs = p.executeQuery();
            if (rs.next())
            {
                isService = rs.getString("isService");
            }

            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
            String pgtypeid = account.getPgTypeId();
            GatewayType gatewayType = GatewayTypeService.getGatewayType(pgtypeid);
            String bankIP = gatewayType.getBank_ipaddress();

            CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
            commTransactionDetailsVO.setPreviousTransactionId(trackingid);
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
            ReitumuBankSMSPaymentGateway rsPg = new ReitumuBankSMSPaymentGateway(accountId);

            commResponseVO = (CommResponseVO) rsPg.processInquiry(commRequestVO);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

            status = commResponseVO.getStatus();
            log.debug("---DBStatus in RBBackendNotification---"+dbStatus);
            log.debug("---Status in RBBackendNotification---"+status);
            if(req.getHeader("X-Forwarded-For").equals(bankIP))
            {
                StringBuffer sb = new StringBuffer();

                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("Customer3D");

                log.error("RBBackendNotification if check condition---"+bankIP);

                if("authstarted".equalsIgnoreCase(dbStatus) || "capturestarted".equalsIgnoreCase(dbStatus))
                {
                    if("Success".equalsIgnoreCase(status) || "HoldOk".equalsIgnoreCase(status))
                    {
                        if("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                        {
                            sb.append("update transaction_common set ");
                            log.debug("update from capture success block---"+dbStatus);
                            sb.append(" captureamount='" + amount + "'");
                            sb.append(", paymentid='" + commResponseVO.getResponseHashInfo() + "'");
                            sb.append(", remark='" + commResponseVO.getRemark() + "'");
                            sb.append(", status='capturesuccess'");
                            commResponseVO.setTransactionType("Sale");
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO,auditTrailVO,null);

                        }
                        else if("N".equalsIgnoreCase(isService) && "authstarted".equalsIgnoreCase(dbStatus))
                        {
                            sb.append("update transaction_common set ");
                            log.debug("update from auth success block---"+dbStatus);
                            sb.append(" amount='" + amount + "'");
                            sb.append(", paymentid='" + commResponseVO.getResponseHashInfo() + "'");
                            sb.append(", remark='" + commResponseVO.getRemark() + "'");
                            sb.append(", status='authsuccessful'");
                            commResponseVO.setTransactionType("Authorization");
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                        }
                    }
                    else if("failed".equalsIgnoreCase(status))
                    {
                        sb.append("update transaction_common set ");
                        log.debug("update from failed block---"+dbStatus);
                        sb.append(" amount='" + amount + "'");
                        sb.append(", paymentid='" + commResponseVO.getResponseHashInfo() + "'");
                        sb.append(", remark='" + commResponseVO.getRemark() + "'");
                        sb.append(", status='authfailed'");
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO,auditTrailVO,null);

                    }
                    sb.append(" where trackingid = "+trackingid);
                    log.debug("common update query in RBBackendServlet---"+sb.toString());
                    Database.executeUpdate(sb.toString(),con);
                }
            }
            else
            {
                log.error("RBBackendNotification else check condition---"+bankIP);
            }
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("error::::",e);
            transactionLogger.error("error::::",e);
            PZExceptionHandler.handleTechicalCVEException(e,toid,null);
        }
        catch (PZDBViolationException dbe)
        {
            log.error("error::::",dbe);
            transactionLogger.error("error::::",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, toid, null);
        }
        catch (SystemError se)
        {
            log.error("error::::",se);
            transactionLogger.error("error::::",se);
            PZExceptionHandler.raiseAndHandleDBViolationException("RBBackendNotification.java","doService()",null,"Transaction",null, PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),toid,null);
        }
        catch (SQLException e)
        {
            log.error("error::::",e);
            transactionLogger.error("error::::",e);
            PZExceptionHandler.raiseAndHandleDBViolationException("RBBackendNotification.java","doService()",null,"Transaction",null, PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause(),toid,null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }
}
