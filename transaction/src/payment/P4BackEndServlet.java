package payment;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.PaymentManager;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.p4.gateway.P4ResponseVO;
import com.payment.p4.gateway.P4Utils;
import com.payment.p4.vos.p4MainVo.Notification;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

/**
 * Created by Admin on 15/10/2015.
 */
public class P4BackEndServlet extends PzServlet
{
    private static Logger log = new Logger(P4BackEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(P4BackEndServlet.class.getName());
    private Functions functions =new Functions();
    public P4BackEndServlet()
    {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("-----Inside P4BackEndServlet------");
        transactionLogger.debug("-----Inside P4BackEndServlet------");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(request.getInputStream()));


        log.debug("____________________________________________________________________P4-BACKEND-REQUEST____________________________________________________________________________");

        String inputLine;
        StringBuffer stringBuffer= new StringBuffer();
        while ((inputLine = in.readLine()) != null)
        {
            log.debug(inputLine);
            stringBuffer.append(inputLine);
        }

        log.debug("INPUT STREAM::::::"+request.getInputStream().toString());

        for (Object key : request.getParameterMap().keySet())
        {
            log.debug("----for loop RBBackendNotification-----" + key + "=" + request.getParameter((String) key) + "--------------");
            transactionLogger.debug("----for loop RBBackendNotification-----" + key + "=" + request.getParameter((String) key) + "--------------");
        }

        log.debug("___________________________________________________________________P4-BACKEND-REQUEST-END____________________________________________________________________________-");

        Notification notification=null;
        JAXBContext jaxbContext = null;
        try
        {
            jaxbContext = JAXBContext.newInstance(Notification.class);
            Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();

            // output pretty printed
            //jaxbUnMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //notification= (Notification) jaxbUnMarshaller.unmarshal(in);
            //jaxbUnMarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_XML);
            //jaxbUnMarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
            transactionLogger.debug("FINAL XML:::"+stringBuffer.toString());
            notification= jaxbUnMarshaller.unmarshal(new StreamSource(new StringReader(stringBuffer.toString())),Notification.class).getValue();
        }
        catch (PropertyException e)
        {
            transactionLogger.error("Exception while Marshalling Notification",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(P4BackEndServlet.class.getName(),"doPost()",null,"Common","Exception while converting the notification Object", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION,null,e.getMessage(),e.getCause(),"TrackingId::"+request.getParameter("trackingId"),"BACKEND Notification Receiver");
        }
        catch (JAXBException e)
        {
            transactionLogger.error("Exception while Marshalling Notification",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(P4BackEndServlet.class.getName(), "doPost()", null, "Common", "Exception while converting the notification Object", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause(), "TrackingId::" + request.getParameter("trackingId"), "BACKEND Notification Receiver");
        }

        StringWriter stringWriter = new StringWriter();
        try
        {
            jaxbContext = JAXBContext.newInstance(Notification.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(notification,stringWriter);
        }
        catch (PropertyException e)
        {
            transactionLogger.error("Exception while Marshalling Notification",e);
        }
        catch (JAXBException e)
        {
            transactionLogger.error("Exception while Marshalling Notification",e);
        }

        transactionLogger.debug("Notification:::"+stringWriter.toString());

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);



        PaymentManager paymentManager = new PaymentManager();
        if (!request.getParameterMap().isEmpty())
        {
            String trackingId = ESAPI.encoder().encodeForSQL(me, request.getParameter("trackingId"));

            Hashtable details = null;
            try
            {
                details = paymentManager.getTransactionDetailsForCommon(trackingId);
            }
            catch (PZDBViolationException e)
            {
                log.error("SQL Exception in P4BackEndServlet---", e);
                transactionLogger.error("SQL Exception in P4BackEndServlet---", e);
                PZExceptionHandler.handleDBCVEException(e, null, PZOperations.P4_NOTIFICATION);
            }
            if (trackingId != null && !details.isEmpty())
            {
                String accountid = (String) details.get("accountid");
                String toid = (String) details.get("toid");
                String amount = (String) details.get("amount");
                String previousStatus = (String) details.get("status");

                AbstractPaymentGateway paymentGateway = null;

                try
                {
                    paymentGateway = AbstractPaymentGateway.getGateway(accountid);
                }
                catch (SystemError e)
                {
                    log.error("System Error in P4BackEndServlet---", e);
                    transactionLogger.error("System Error in P4BackEndServlet---", e);
                    PZExceptionHandler.raiseAndHandleGenericViolationException("P4BackEndServlet", "doService", null, "transaction", "", null, e.getMessage(), e, toid, PZOperations.P4_NOTIFICATION);
                }
                if (paymentGateway != null)
                {
                    CommRequestVO commRequestVO = null;
                    P4ResponseVO transRespDetails = null;
                    P4Utils p4Utils = new P4Utils();


                    if (notification != null && notification.getProcessing()!=null )
                    {
                            transRespDetails=new P4ResponseVO();
                            String status = null;

                            if("ACK".equalsIgnoreCase(notification.getProcessing().getResult()))
                            {
                                if ("TRANSMITTED".equals(notification.getProcessing().getState()) || "RECEIVED".equals(notification.getProcessing().getState()))
                                {
                                    status = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                }
                                else if ("REFUNDED".equals(notification.getProcessing().getState()))
                                {
                                    status = PZTransactionStatus.REVERSED.toString();
                                }
                            }
                            else
                            {
                                if(previousStatus.equals(PZTransactionStatus.AUTH_STARTED.toString()))
                                status=PZTransactionStatus.AUTH_FAILED.toString();
                                else if(previousStatus.equals(PZTransactionStatus.CAPTURE_STARTED.toString()))
                                status=PZTransactionStatus.FAILED.toString();
                            }

                            log.debug("P4 BACKEND SERVLET:::: STATUS"+status);
                           /* else if ("PENDING".equals(notification.getProcessing().getState()))
                            {
                                status = PZTransactionStatus.AUTH_STARTED.toString();
                            }*/

                            if(functions.isValueNull(status))
                            {

                                SimpleDateFormat responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                SimpleDateFormat responseTimePZ = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                transRespDetails.setTransactionId(trackingId);
                                transRespDetails.setAmount(amount);

                                transRespDetails.setTransactionStatus(status);
                                transRespDetails.setErrorCode(notification.getProcessing().getCode5());
                                transRespDetails.setRemark(notification.getProcessing().getStatus()+"("+notification.getProcessing().getState()+")");
                                transRespDetails.setDescriptor(paymentGateway.getDisplayName());

                                if(status.equals(PZTransactionStatus.CAPTURE_SUCCESS.toString()))
                                    transRespDetails.setTransactionType(PZProcessType.SALE.toString());
                                if(status.equals(PZTransactionStatus.REVERSED.toString()))
                                    transRespDetails.setTransactionType(PZProcessType.REFUND.toString());
                                try
                                {
                                    transRespDetails.setResponseTime(responseTimePZ.format(responseTime.parse(notification.getProcessing().getTimestamp())));

                                    paymentManager.updateTransactionForCommon(transRespDetails, status, trackingId, null, "transaction_common", null, transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                                }
                                catch (PZDBViolationException s)
                                {
                                    log.error("SQL Exception in P4BackEndServlet---", s);
                                    transactionLogger.error("SQL Exception in P4BackEndServlet---", s);
                                    PZExceptionHandler.handleDBCVEException(s, toid, null);
                                }
                                catch (ParseException e)
                                {
                                    log.error("Parsing Exception Exception in P4BackEndServlet---", e);
                                    transactionLogger.error("Parsing Exception Exception in P4BackEndServlet---", e);
                                    PZExceptionHandler.raiseAndHandleTechnicalViolationException(P4BackEndServlet.class.getName(), "doPost()", null, "Common", "Exception while converting the Date", PZTechnicalExceptionEnum.DATE_PARSING_EXCEPTION, null, e.getMessage(), e.getCause(), "TrackingId::" + request.getParameter("trackingId"), "BACKEND Notification Receiver") ;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
