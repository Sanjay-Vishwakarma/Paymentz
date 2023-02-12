package payment;

import com.directi.pg.AuditTrailVO;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.PaymentManager;
import com.payment.PZTransactionStatus;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.sofort.IdealPaymentGateway;
import com.payment.sofort.VO.SofortResponseVO;
import com.sofort.lib.products.common.BankAccount;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 3/2/15
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class IdealBackEndServlet extends PzServlet
{
    private static Logger log = new Logger(IdealBackEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(IdealBackEndServlet.class.getName());
    public IdealBackEndServlet()
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
        log.debug("-----Inside IdealBackEndServlet------");
        transactionLogger.debug("-----Inside IdealBackEndServlet------");

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        PaymentManager paymentManager = new PaymentManager();
        if (!req.getParameterMap().isEmpty())
        {


           /* transaction=12345-654321-51E811F3-BA51
              &user_id=12345
              &project_id=654321
              &sender_holder=Max%20Mustermann
              &sender_account_number=
              &sender_bank_name=ABN+Amro
              &sender_bank_bic=RABXXXXX
              &sender_iban=NL17RXXXXXXXXXXX12
              &sender_country_id=NL
              &recipient_holder=Gert%20Schopper
              &recipient_account_number=99XXXXXX99
              &recipient_bank_code=700XXXXX
              &recipient_bank_name=SOFORT+BanX
              &recipient_bank_bic=DEKTXXXXXXX
              &recipient_iban=DE71700XXXXXXXXXXXXX99
              &recipient_country_id=DE
              &amount=30.00
              &currency_id=EUR
              &reason_1=Bestellnummer+1
              &reason_2=
              &created=2013-07-18+18%3A04%3A13
              &status=received
              &status_reason=credited
              &status_modified=2013-07-18+18%3A04%3A13
              &hash=e1f3c94a897025a5d9e6e41c33a32e6056ec981a
                */
                
            String trackingId = ESAPI.encoder().encodeForSQL(me,req.getParameter("reason_1"));
            String paymentId = req.getParameter("transaction");
            String sender_holder = req.getParameter("sender_holder");
            String sender_account_number = req.getParameter("sender_account_number");
            String sender_bank_name = req.getParameter("sender_bank_name");
            String sender_bank_bic = req.getParameter("sender_bank_bic");
            String sender_iban = req.getParameter("sender_iban");
            String sender_country_id = req.getParameter("sender_country_id");
            String recipient_holder = req.getParameter("recipient_holder");
            String recipient_account_number = req.getParameter("recipient_account_number");
            String recipient_bank_code = req.getParameter("recipient_bank_code");
            String recipient_bank_name = req.getParameter("recipient_bank_name");
            String recipient_bank_bic = req.getParameter("recipient_bank_bic");
            String recipient_iban = req.getParameter("recipient_iban");
            String recipient_country_id = req.getParameter("recipient_country_id");
            String amount = req.getParameter("amount");
            String orderid = req.getParameter("reason_2");
            String ideal_status = req.getParameter("status");
            String ideal_status_reason = req.getParameter("status_reason");
            String status_modified = req.getParameter("status_modified");
            String hash = req.getParameter("hash");

            // get AccountId from Tracking Id using PaymentQueryManager
            Hashtable details=null;
            try
            {
                details = paymentManager.getAccountIdandPaymeIdForCommon(trackingId);
            }
            catch(PZDBViolationException s)
            {
                log.error("SQL Exception in IdealBackEndServlet---",s);
                transactionLogger.error("SQL Exception in IdealBackEndServlet---",s);
                PZExceptionHandler.handleDBCVEException(s, null, PZOperations.SOFORT_NOTIFICATION);
            }
            if(trackingId!=null && !details.isEmpty())
            {
                String accountid =(String)details.get("accountid");
                String toid=(String)details.get("toid");


                IdealPaymentGateway pg =  new IdealPaymentGateway(accountid);

                //Todo: Validate the has value pass IDealNotificationResponse



                if(pg!=null)
                {

                    SofortResponseVO transRespDetails = new SofortResponseVO();
                    transRespDetails.setStatus("success");
                    transRespDetails.setTransactionType("notification");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    transRespDetails.setResponseTime(String.valueOf(dateFormat.format(date)));
                    transRespDetails.setMerchantId(trackingId);
                    transRespDetails.setTransactionId(paymentId);
                    transRespDetails.setDescription(ideal_status_reason);
                    transRespDetails.setDescriptor(ideal_status_reason);
                    transRespDetails.setTransactionStatus(ideal_status);
                    transRespDetails.setRemark(ideal_status_reason);
                    transRespDetails.setAmount(amount);
                    transRespDetails.setMerchantOrderId(orderid);



                    BankAccount recepientBankAccount = new BankAccount();
                    recepientBankAccount.setBankName(recipient_bank_name);
                    recepientBankAccount.setBic(recipient_bank_bic);
                    recepientBankAccount.setCountryCode(recipient_country_id);
                    recepientBankAccount.setHolder(recipient_holder);
                    recepientBankAccount.setIban(recipient_iban);
                    recepientBankAccount.setBankCode(recipient_bank_code);
                    recepientBankAccount.setAccountNumber(recipient_account_number);

                    transRespDetails.setRecipient(recepientBankAccount);

                    BankAccount senderBankAccount = new BankAccount();
                    senderBankAccount.setBankName(sender_bank_name);
                    senderBankAccount.setBic(sender_bank_bic);
                    senderBankAccount.setCountryCode(sender_country_id);
                    senderBankAccount.setHolder(sender_holder);
                    senderBankAccount.setIban(sender_iban);
                    //senderBankAccount.setBankCode();
                    senderBankAccount.setAccountNumber(sender_account_number);

                    transRespDetails.setSender(senderBankAccount);
                    
                    if(transRespDetails != null)
                    {
                            String status = PZTransactionStatus.AUTH_STARTED.toString();
                            if(transRespDetails.getTransactionStatus().equalsIgnoreCase("pending"))
                            {
                                status = PZTransactionStatus.AUTH_SUCCESS.toString();
                            }
                            else if (transRespDetails.getTransactionStatus().equalsIgnoreCase("received"))
                            {
                                status = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                            }
                            else if (transRespDetails.getTransactionStatus().equalsIgnoreCase("loss"))
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
                                paymentManager.updateTransactionForCommon(transRespDetails,status,trackingId,auditTrailVO ,"transaction_common",null,transRespDetails.getTransactionId(),transRespDetails.getResponseTime(),transRespDetails.getRemark());
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