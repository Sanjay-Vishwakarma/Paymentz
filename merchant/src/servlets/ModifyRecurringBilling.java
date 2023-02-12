import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.SystemError;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.RecurringManager;
import com.manager.dao.TransactionDAO;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
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
/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 4/14/15
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModifyRecurringBilling extends HttpServlet
{
    private static Logger log = new Logger(ModifyRecurringBilling.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        Merchants merchants = new Merchants();
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        TransactionDAO transactionDAO = new TransactionDAO();
        RecurringManager recurringManager = new RecurringManager();
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        CommRequestVO commRequestVO = new CommRequestVO();

        AbstractPaymentGateway pg = null;
        CommResponseVO commResponseVO = null;

        Connection connection = null;
        TransactionDetailsVO transactionDetailsVO = null;

        String trackingid = req.getParameter("trackingid");
        String rbid = req.getParameter("rbid");
        String action = req.getParameter("action");
        String rStatus = "";
        String rInterval = "";
        String rFrequency = "";
        String rRunDate = "";
        String rAmount = "";
        String error = "";

        transactionDetailsVO = transactionDAO.getDetailFromCommon(trackingid);
        PreparedStatement p = null;
        ResultSet rs = null;
        try
        {
            connection = Database.getConnection();
            String sql1 = "select merchantid, username, passwd from gateway_accounts where accountid = ?";
            p = connection.prepareStatement(sql1);
            p.setString(1, transactionDetailsVO.getAccountId());
            rs = p.executeQuery();
            if (rs.next())
            {
                commMerchantVO.setMerchantId(rs.getString("merchantid"));
                commMerchantVO.setMerchantUsername(rs.getString("username"));
                commMerchantVO.setPassword(rs.getString("passwd"));
            }

            pg =  AbstractPaymentGateway.getGateway(transactionDetailsVO.getAccountId());
            commRequestVO.setCommMerchantVO(commMerchantVO);
            if("update".equalsIgnoreCase(action))
            {
                rInterval = req.getParameter("interval").toLowerCase();
                rFrequency = req.getParameter("frequency");
                rRunDate = req.getParameter("runDate");
                rAmount = req.getParameter("amount");

                recurringBillingVO.setInterval(rInterval);
                recurringBillingVO.setFrequency(rFrequency);
                recurringBillingVO.setRunDate(rRunDate);
                recurringBillingVO.setAmount(rAmount);

                commRequestVO.setRecurringBillingVO(recurringBillingVO);
                commResponseVO = (CommResponseVO) pg.processUpdateRecurring(commRequestVO,rbid);

                if (commResponseVO != null)
                {
                    if (commResponseVO.getErrorCode().equals("0027"))
                    {
                        recurringManager.updateRecurringSubDetails(recurringBillingVO, rbid);
                        rStatus = commResponseVO.getStatus();
                    }
                    else
                    {
                        error = "Update functionality is not supported by this gateway.";
                    }
                }
                else
                {
                    error = "Update functionality is not supported by this gateway.";
                }

            }
            if("delete".equalsIgnoreCase(action))
            {
                commResponseVO = (CommResponseVO) pg.processDeleteRecurring(commRequestVO,rbid);

                if (commResponseVO != null)
                {
                    if (commResponseVO.getErrorCode().equals("0027"))
                    {
                        recurringManager.deleteRecurringDetails(rbid);
                        rStatus = commResponseVO.getStatus();
                    }
                    else
                    {
                        error = "Delete functionality is not supported by this gateway.";
                    }
                }
                else
                {
                    error = "Delete functionality is not supported by this gateway.";
                }


            }
            if("active".equalsIgnoreCase(action))
            {
                System.out.printf("inside active---");
                recurringBillingVO.setActiveDeactive(req.getParameter("rStatus"));
                commRequestVO.setRecurringBillingVO(recurringBillingVO);
                commResponseVO = (CommResponseVO) pg.processActivateDeactivateRecurring(commRequestVO,rbid);

                if (commResponseVO != null)
                {
                    if (commResponseVO.getErrorCode().equals("0043"))
                    {
                        recurringManager.updateRecurringStatusDetails(recurringBillingVO, rbid);
                        rStatus = commResponseVO.getStatus();
                    }
                    else
                    {
                        //System.out.println("inside 1 else----");
                        error = "Deactivation functionality is not supported by this gateway.";
                    }
                }
                else
                {
                    //System.out.println("inside 2 else----");
                    error = "Deactivation functionality is not supported by this gateway.";
                }

            }
        }
        catch (PZTechnicalViolationException e)
        {
            rStatus = "Internal error while processing your request";
            PZExceptionHandler.handleTechicalCVEException(e,(String)session.getAttribute("merchantid"),"Recurring Billing Operation");
        }
        catch (PZDBViolationException dbe)
        {
            rStatus = "Internal error while processing your request";
            log.error("PZDBViolationException occured in ModifyRecuringBilling---", dbe);
            PZExceptionHandler.handleDBCVEException(dbe,(String)session.getAttribute("merchantid"),"Recurring Billing Operation");
        }
        catch (SystemError se)
        {
            rStatus = "Internal error while processing your request";
            log.debug("Exception occured---"+se);
            PZExceptionHandler.raiseAndHandleDBViolationException("ModifyRecuringBilling.java","doPost()",null,"Merchant","Exception while DB connection", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),(String)session.getAttribute("merchantid"),"Recurring Billing Operation");
        }
        catch (SQLException se)
        {
            rStatus = "Internal error while processing your request";
            log.debug("Exception occured---"+se);
            PZExceptionHandler.raiseAndHandleDBViolationException("ModifyRecuringBilling.java","doPost()",null,"Merchant","Exception while DB query", PZDBExceptionEnum.INCORRECT_QUERY,null,se.getMessage(),se.getCause(),(String)session.getAttribute("merchantid"),"Recurring Billing Operation");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        req.setAttribute("recurringstatus", rStatus);
        req.setAttribute("error", error);
        RequestDispatcher rd = req.getRequestDispatcher("/recurringModule.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }
}
