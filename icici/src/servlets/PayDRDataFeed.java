import com.directi.pg.*;
import com.directi.pg.core.valueObjects.PayDollarResponseVO;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZCaptureRequest;
import com.payment.response.PZCaptureResponse;
import com.payment.response.PZResponseStatus;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 29, 2012
 * Time: 7:54:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayDRDataFeed extends TcServlet
{
    static Logger logger = new Logger(PayDRDataFeed.class.getName());

    public PayDRDataFeed()
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

    public void doService(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {

            logger.debug("ENTERED IN DATAFEED");


        PrintWriter pWriter = res.getWriter();
        res.setContentType("text");
       // pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        pWriter.println("OK");
        res.flushBuffer();

            /*System.out.println("Requeset PathInfo=>" + request.getPathInfo());
            System.out.println("Requeset PathTranslated=>"+ request.getPathTranslated());
            System.out.println("Requeset RequestURI=>" + request.getRequestURI());
            System.out.println("Requeset QueryString=>"+ request.getQueryString());*/
            Enumeration enumeration = request.getParameterNames();
            String[] paramValues = null;
            String paramName = null;
            StringBuffer buffer = new StringBuffer();
            int paramValuesSize = 0;

            while (enumeration != null && enumeration.hasMoreElements()) {
                buffer.delete(0, buffer.length());
                paramName = (String) enumeration.nextElement();
                paramValues = request.getParameterValues(paramName);
                paramValuesSize = 0;
                if (paramValues != null && paramValues.length > 0)
                    paramValuesSize = paramValues.length;

                for (int i = 0; i < paramValuesSize; i++) {
                    buffer.append(paramValues[i] + ",");
                }

                buffer.deleteCharAt(buffer.lastIndexOf(","));

               // System.out.println("Requeset Parameter NAME=>" + paramName + " VALUES =>" + buffer.toString());
                logger.debug("Requeset Parameter NAME=>" + paramName + " VALUES =>" + buffer.toString());
            }

            /*System.out.println("Request Charater Encoding is ==>"+ request.getCharacterEncoding());*/

            String src = request.getParameter("src"); 													//Return bank host status code (secondary).
            String prc = request.getParameter("prc");													//Return bank host status code (primary).
            String successcode = request.getParameter("successcode");					//0- succeeded, 1- failure, Others - error
            String ref = request.getParameter("Ref");													//Merchant�s Order Reference Number
            String payRef = request.getParameter("PayRef");										//PayDollar Payment Reference Number
            String amt = request.getParameter("Amt");												//Transaction Amount
            String cur = request.getParameter("Cur");													//Transaction Currency
            String payerAuth = request.getParameter("payerAuth");							//Payer Authentication Status

            String ord = request.getParameter("Ord");													//Bank Reference � Order id
            String holder = request.getParameter("Holder");										//The Holder Name of the Payment Account
            String remark = request.getParameter("remark");										//A remark field for you to store additional data that will not show on the transaction web page
            String authId = request.getParameter("AuthId");										//Approval Code
            String eci = request.getParameter("eci");														//ECI value (for 3D enabled Merchants)
            String sourceIp = request.getParameter("sourceIp");									//IP address of payer
            String ipCountry = request.getParameter("ipCountry");							//Country of payer ( e.g. HK) - if country is on high risk country list, an asterisk will be shown (e.g. MY*)

            String mpsAmt = request.getParameter("mpsAmt");								//MPS Transaction Amount
            String mpsCur = request.getParameter("mpsCur");									//MPS Transaction Currency
            String mpsForeignAmt = request.getParameter("mpsForeignAmt");		//MPS Transaction Foreign Amount
            String mpsForeignCur = request.getParameter("mpsForeignCur");			//MPS Transaction Foreign Currency
            String mpsRate = request.getParameter("mpsRate");								//MPS Exchange Rate: (Foreign / Base) e.g. USD / HKD = 7.77
            String cardlssuingCountry = request.getParameter("cardlssuingCountry");			//Card Issuing Country Code ( e.g. HK)
            String payMethod = request.getParameter("payMethod");						//Payment method (e.g. VISA, Master, Diners, JCB, AMEX)



            /*boolean isSecureHashSetting=true;

                //if Secure Hash is used
                if (isSecureHashSetting) {
                    String[] secureHash = request.getParameterValues("secureHash");
                    List tempList = new ArrayList();
                    if (secureHash != null) {
                        for (int i = 0; i < secureHash.length; i++) {
                            System.out.println(secureHash[i]);

                            if (secureHash[i].indexOf(",") > 0) {
                                String[] data = secureHash[i].split(",");
                                for (int j = 0; data != null & j < data.length; j++) {
                                    tempList.add(data[j]);
                                }

                            } else {
                                tempList.add(secureHash[i]);
                            }
                        }
                    }

                    int size = tempList.size();
                    if (size > 0) {
                        secureHash = new String[size];
                        for (int i = 0; i < size; i++) {
                            secureHash[i] = (String) tempList.get(i);
                        }

                    }

                boolean verifyResult = PaydollarSecureUtil.verifyPaymentDatafeed(src, prc, successcode, ref,payRef, cur, amt, payerAuth, secureHash);

                System.out.println("verifyResult =" + verifyResult);
                if (!verifyResult) {
                    System.out.println("verifyResult Faile");
                }
            }*/

        PayDollarResponseVO paydrresp=new PayDollarResponseVO();
        paydrresp.setAuthId(authId);
        paydrresp.setBankOrderId(ord);
        paydrresp.setCurrencyCode(cur);
        paydrresp.setHolder(holder);
        paydrresp.setPayRef(payRef);
        paydrresp.setSecondResponseCode(src);
        paydrresp.setPrimaryResponseCode(prc);
        paydrresp.setSuccessCode(successcode);



        paydrresp.setErrorCode(paydrresp.getResultCode());
        paydrresp.setDescription(paydrresp.getErrMsg());
        paydrresp.setTransactionId(paydrresp.getPayRef());
        paydrresp.setStatus(paydrresp.getTransactionStatus());


        String accountid="",memberid="";
        String query="select accountid,amount,toid from transaction_common where trackingid="+ref;
        Connection conn=null;
        ResultSet resultSet = null;
        try
        {
            conn=Database.getConnection();
            resultSet=Database.executeQuery(query, conn);
            if ( resultSet.next())
            {
                memberid = resultSet.getString("toid");
                accountid = resultSet.getString("accountid");
                amt=resultSet.getString("amount");
            }
            
            
        }
        catch( SystemError se)
        {
            logger.error("ERROR OCCURED",se);
        }
        catch( SQLException sqle)
        {
            logger.error("EXCEPTIONOCCURED",sqle);
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closeConnection(conn);
        }


        AbstractPaymentProcess paydrpayproc=  PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountid));
       
        
        
        if (successcode.equals("0")) 
        {
            PreparedStatement ps = null;
            try{
                 AuditTrailVO auditTrailVO=new AuditTrailVO();
                auditTrailVO.setActionExecutorId(memberid);
                auditTrailVO.setActionExecutorName("Customer");
                paydrpayproc.actionEntry(ref,amt,ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL,ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL,paydrresp, null,auditTrailVO);
            
                query="update transaction_common set paymentid='"+payRef+"', status='authsuccessful' where trackingid="+ref;
                conn = Database.getConnection();
                ps=conn.prepareStatement(query);
                ps.executeUpdate();
                
                
            
            }
            catch(SQLException se)
            {
                logger.error("ERROR OCCURED",se);
                PZExceptionHandler.raiseAndHandleDBViolationException(PayDRDataFeed.class.getName(),"doService()",null,"common","Technical Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause(),null,null);
            }
            catch(SystemError se)
            {
                logger.error("ERROR OCCURED",se);
                PZExceptionHandler.raiseAndHandleDBViolationException(PayDRDataFeed.class.getName(),"doService()",null,"common","Technical Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),null,null);
            }
            catch (PZDBViolationException e)
            {
                PZExceptionHandler.handleDBCVEException(e,null,null);
            }
            finally
            {
                Database.closePreparedStatement(ps);
                Database.closeConnection(conn); 
            }
        
             //  add Capture code ;;


            logger.debug("Member ID +"+memberid+"is sale-+"+Functions.isSaleMerchant(memberid));
            if(Functions.isSaleMerchant(memberid))
            {

                /*try{

                    paydrpayproc.actionEntry(ref,amt,ActionEntry.ACTION_CAPTURE_STARTED,ActionEntry.STATUS_CAPTURE_STARTED,paydrresp, null);

                    query="update transaction_common set paymentid='"+payRef+"', status='capturestarted' where trackingid="+ref;
                    conn = Database.getConnection();
                    PreparedStatement ps=conn.prepareStatement(query);
                    ps.executeUpdate();



                }
                catch(SQLException se)
                {
                    logger.error("ERROR OCCURED",se);
                }
                catch(SystemError se)
                {
                    logger.error("ERROR OCCURED",se);
                }
                finally {
                    Database.closeConnection(conn);
                }
*/

                PZCaptureRequest capturereq=new PZCaptureRequest();
                capturereq.setAmount(Double.parseDouble(amt));
                capturereq.setAccountId(Integer.parseInt(accountid));
                capturereq.setMemberId(Integer.parseInt(memberid));
                capturereq.setTrackingId(Integer.parseInt(ref));

                PZCaptureResponse captureresp= paydrpayproc.capture(capturereq);
                
                if(captureresp.getStatus().equals(PZResponseStatus.SUCCESS))
                {


                }






            }









            //till here capture and then entry in action history and table
        
        } else
        {

            PreparedStatement ps = null;

            try{
                AuditTrailVO auditTrailVO=new AuditTrailVO();
                auditTrailVO.setActionExecutorId(memberid);
                auditTrailVO.setActionExecutorName("Customer");
                paydrpayproc.actionEntry(ref,amt,ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,paydrresp, null,auditTrailVO);

                query="update transaction_common set paymentid='"+payRef+"', status='authfailed' where trackingid="+ref;
                conn = Database.getConnection();
                ps=conn.prepareStatement(query);
                ps.executeUpdate();



            }
            catch(SQLException se)
            {
                logger.error("ERROR OCCURED",se);
                PZExceptionHandler.raiseAndHandleDBViolationException(PayDRDataFeed.class.getName(),"doService()",null,"common","Technical Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause(),null,null);
            }
            catch(SystemError se)
            {
                logger.error("ERROR OCCURED",se);
                PZExceptionHandler.raiseAndHandleDBViolationException(PayDRDataFeed.class.getName(),"doService()",null,"common","Technical Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),null,null);
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZDBViolationException ",e);
                PZExceptionHandler.handleDBCVEException(e,null,null);
            }
            finally
            {
                Database.closePreparedStatement(ps);
                Database.closeConnection(conn);
            }
        }







    }

}
