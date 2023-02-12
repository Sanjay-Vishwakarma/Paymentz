package payment;


import com.directi.pg.AuditTrailVO;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.PaymentManager;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.sofort.SofortUtility;
import com.payment.sofort.VO.SofortResponseVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 3/2/15
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class SofortBackEndServlet extends PzServlet
{
    private static Logger log = new Logger(SofortBackEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SofortBackEndServlet.class.getName());
    public SofortBackEndServlet()
    {
        super();
    }

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
        log.debug("-----Inside SofortBackEndServlet------");
        transactionLogger.debug("-----Inside SofortBackEndServlet------");

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        for(Object key : req.getParameterMap().keySet())
        {
            log.debug("----for loop RBBackendNotification-----"+key+"="+req.getParameter((String)key)+"--------------");
            transactionLogger.debug("----for loop RBBackendNotification-----"+key+"="+req.getParameter((String)key)+"--------------");
        }

        PaymentManager paymentManager = new PaymentManager();
        AuditTrailVO auditTrailVO = new AuditTrailVO();



        if (!req.getParameterMap().isEmpty())
        {

            String trackingId = ESAPI.encoder().encodeForSQL(me,req.getParameter("trackingId"));

            // get AccountId from Tracking Id using PaymentQueryManager
            Hashtable details=null;
            try
            {
            details = paymentManager.getAccountIdandPaymeIdForCommon(trackingId);
            }
            catch(PZDBViolationException s)
            {
                log.error("SQL Exception in SofortBackEndServlet---",s);
                transactionLogger.error("SQL Exception in SofortBackEndServlet---",s);
                PZExceptionHandler.handleDBCVEException(s, null, PZOperations.SOFORT_NOTIFICATION);
            }
            if(trackingId!=null && !details.isEmpty())
            {
                String accountid =(String)details.get("accountid");
                String toid=(String)details.get("toid");

                
                //Call Inquiry
                AbstractPaymentGateway pg = null;
                try
                {
                    pg =  AbstractPaymentGateway.getGateway(accountid);
                }
                catch(SystemError e)
                {
                    log.error("System Error in SofortBackEndServlet---",e);
                    transactionLogger.error("System Error in SofortBackEndServlet---",e);
                    PZExceptionHandler.raiseAndHandleGenericViolationException("SofortBackEndServlet","doService",null,"transaction","",null,e.getMessage(),e,toid, PZOperations.SOFORT_NOTIFICATION);
                }

                    if(pg!=null)
                    {

                        CommRequestVO commRequestVO = null;
                        SofortResponseVO transRespDetails = null;
                        SofortUtility sofortUtility = new SofortUtility();
                        commRequestVO = sofortUtility.getSofortRequestVOForInquiry((String)details.get("paymentid"),trackingId,(String)details.get("accountid"));

                         try
                         {
                             transRespDetails = (SofortResponseVO) pg.processQuery(trackingId,commRequestVO);
                         }
                         catch (PZConstraintViolationException e)
                         {
                             log.error("System Error in SofortBackEndServlet---",e);
                             transactionLogger.error("System Error in SofortBackEndServlet---",e);

                             PZExceptionHandler.handleCVEException(e,toid,"SofortBackEndServlet");
                         }
                         catch (PZGenericConstraintViolationException e)
                         {
                             log.error("System Error in SofortBackEndServlet---",e);
                             transactionLogger.error("System Error in SofortBackEndServlet---",e);

                             PZExceptionHandler.handleGenericCVEException(e, toid, "SofortBackEndServlet");

                         }

                        if(transRespDetails != null)
                        {
                            if(transRespDetails.getStatus().equals("failed"))
                            {
                                String errorMessage = " No Response from Sofort Gateway for tracking id:"+trackingId;
                                PZExceptionHandler.raiseAndHandleGenericViolationException("SofortBackEndServlet","doService",null,"transaction",errorMessage,null, errorMessage,null,null, PZOperations.SOFORT_NOTIFICATION);
                            }
                            else if(transRespDetails.getStatus().equals("Order not exist"))
                            {
                                String errorMessage = " Order not found on Sofort side for trackingid: "+trackingId;
                                PZExceptionHandler.raiseAndHandleGenericViolationException("SofortBackEndServlet","doService",null,"transaction",errorMessage,null, errorMessage,null,null, PZOperations.SOFORT_NOTIFICATION);

                            }
                            else if(transRespDetails.getStatus().equals("success"))
                            {
                                String status = PZTransactionStatus.AUTH_STARTED.toString();
                                if(transRespDetails.getTransactionStatus().equalsIgnoreCase("PENDING"))
                                {
                                    status = PZTransactionStatus.AUTH_SUCCESS.toString();
                                }
                                else if (transRespDetails.getTransactionStatus().equalsIgnoreCase("RECEIVED"))
                                {
                                   status = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                }
                                else if (transRespDetails.getTransactionStatus().equalsIgnoreCase("LOSS"))
                                {
                                   status=PZTransactionStatus.AUTH_FAILED.toString();
                                }
                                else if (transRespDetails.getTransactionStatus().equalsIgnoreCase("UNTRACEABLE"))
                                {
                                    status = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                }
                                else if (transRespDetails.getTransactionStatus().equalsIgnoreCase("REFUNDED"))
                                {
                                    status = PZTransactionStatus.REVERSED.toString();
                                }
                                try
                                {
                                    auditTrailVO.setActionExecutorName("Customer");
                                    auditTrailVO.setActionExecutorId(toid);
                                    paymentManager.updateTransactionForCommon(transRespDetails,status,trackingId, auditTrailVO,"transaction_common",null,transRespDetails.getTransactionId(),transRespDetails.getResponseTime(),transRespDetails.getRemark());
                                }
                                catch(PZDBViolationException s)
                                {
                                    log.error("SQL Exception in SofortBackEndServlet---",s);
                                    transactionLogger.error("SQL Exception in SofortBackEndServlet---",s);
                                    PZExceptionHandler.handleDBCVEException(s, toid, null);
                                }


                                //Todo: Send Mail


                            }




                        }




                    }


                }


        }



    }

}