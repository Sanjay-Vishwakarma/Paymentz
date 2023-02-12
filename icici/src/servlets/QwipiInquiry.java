import com.directi.pg.Admin;
import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.QwipiPaymentGateway;
import com.directi.pg.core.valueObjects.*;

import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/9/12
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiInquiry extends HttpServlet
{
   private  static Logger log = new Logger(QwipiInquiry.class.getName());
   private  static TransactionLogger transactionLogger = new TransactionLogger(QwipiInquiry.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in QwipiInquiry");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));

        log.debug("success");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String merchantNo=null;
        String orderDescription=null;
        String md5string=null;
        String md5Info=null;
        AbstractPaymentGateway pg = null;
        String accountid=null;
        Connection con=null;
        GenericCardDetailsVO cardDetail= new GenericCardDetailsVO();
        QwipiAddressDetailsVO AddressDetail= new QwipiAddressDetailsVO();
        QwipiTransDetailsVO TransDetail = new QwipiTransDetailsVO();
        QwipiRequestVO requestDetail=null;
        QwipiResponseVO transRespDetails=null;

        try
        {
            /*orderDescription= ESAPI.validator().getValidInput("description",(String) req.getParameter("description"),"SafeString",50,true);
            merchantNo= ESAPI.validator().getValidInput("fromid",(String) req.getParameter("fromid"),"Numbers",50,true);
            accountid=ESAPI.validator().getValidInput("accountid",(String) req.getParameter("accountid"),"Numbers",50,true);*/
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("not valid data",e);
        }

        orderDescription= (String) req.getParameter("description");
        merchantNo= (String) req.getParameter("fromid");
        accountid= (String) req.getParameter("accountid");

        String query="select midkey from gateway_accounts_qwipi where mid=? and accountid=?";
        try
        {
            con= Database.getConnection();
            PreparedStatement p= con.prepareStatement(query);
            p.setString(1,merchantNo);
            p.setString(2,accountid);
            ResultSet rs =p.executeQuery();
            if(rs.next())
            {
                md5string=rs.getString("midkey");
            }
            log.debug(md5string+"accountid="+accountid);

            md5Info=Functions.convertmd5(merchantNo+orderDescription+md5string);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("NoSuchAlgorithmException",e);
        }
        catch (SystemError systemError)
        {
            log.error("NoSuchAlgorithmException",systemError);
        }
        catch (SQLException xe)
        {
            log.error("NoSuchAlgorithmException",xe);
        }
        finally {
            Database.closeConnection(con);
        }
        try
        {
            pg = AbstractPaymentGateway.getGateway(accountid);
        }
        catch (SystemError systemError)
        {   log.error("SystemError while getting processInquiry of transaction",systemError);
        }
        log.debug("set object Vo");
        QwipiPaymentGateway qwipiPaymentGateway = new QwipiPaymentGateway(accountid);
        AddressDetail.setMd5info(md5Info);

        TransDetail.setMerNo(merchantNo);

        TransDetail.setOrderDesc(orderDescription);

        requestDetail = new QwipiRequestVO(cardDetail,AddressDetail, TransDetail);
        try
        {   log.debug("calling inquiry method");
            transRespDetails = (QwipiResponseVO) qwipiPaymentGateway.processInquiry(requestDetail);

        }
        catch (PZTechnicalViolationException e)
        {
           log.error("PZTechnical Exception while Inquiring via QWIPI",e);
           transactionLogger.error("PZTechnical Exception while Inquiring via QWIPI",e);

           PZExceptionHandler.handleTechicalCVEException(e,"OrderDesc::::"+orderDescription,"Technical exception while Inquiring via QWIPI");
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZTechnical Exception while Inquiring via QWIPI",e);
            transactionLogger.error("PZTechnical Exception while Inquiring via QWIPI",e);

            PZExceptionHandler.handleCVEException(e,"OrderDesc::::"+orderDescription,"Constraint violation exception while Inquiring via QWIPI");
        }
        /*catch (PZDBViolationException e)
        {
            log.error("PZTechnical Exception while Inquiring via QWIPI",e);
            transactionLogger.error("PZTechnical Exception while Inquiring via QWIPI",e);

            PZExceptionHandler.handleDBCVEException(e,"OrderDesc::::"+orderDescription,"DB exception while Inquiring via QWIPI");
        }*/
        log.debug("successful");
        Hashtable hash=new Hashtable();
        if(transRespDetails!=null)
        {
            if(transRespDetails.getResult()!=null)
                hash.put("resultcode",transRespDetails.getResult());
            if(transRespDetails.getCode()!=null)
                hash.put("errorcode",transRespDetails.getCode());
            if(transRespDetails.getText()!=null)
                hash.put("errortext",transRespDetails.getText());
            if(transRespDetails.getId()!=null)
                hash.put("orderid",transRespDetails.getId());
            if(transRespDetails.getStatus()!=null)
                hash.put("status",transRespDetails.getStatus());
            if(transRespDetails.getBillNo()!=null)
                hash.put("billno",transRespDetails.getBillNo());
            if(transRespDetails.getAmount()!=null)
                hash.put("amount",transRespDetails.getAmount());
            if(transRespDetails.getDateTime()!=null)
                hash.put("date",transRespDetails.getDateTime());
            if(transRespDetails.getCurrency()!=null)
                hash.put("currency",transRespDetails.getCurrency());
            if(transRespDetails.getRefundCode()!=null)
                hash.put("refundcode",transRespDetails.getRefundCode());
            if(transRespDetails.getRefundText()!=null)
                hash.put("refundtext",transRespDetails.getRefundText());
            if(transRespDetails.getRefundAmount()!=null)
                hash.put("refundamount",transRespDetails.getRefundAmount());
            if(transRespDetails.getRefundDate()!=null)
                hash.put("refunddate",transRespDetails.getRefundDate());
            if(transRespDetails.getRefundRemark()!=null)
                hash.put("refundremark",transRespDetails.getRefundRemark());
            if(transRespDetails.getRefundMessage()!=null)
                hash.put("refundmsg",transRespDetails.getRefundMessage());
            if(transRespDetails.getCbCode()!=null)
                hash.put("chargebackcode",transRespDetails.getCbCode());
            if(transRespDetails.getCbText()!=null)
                hash.put("chargebacktext",transRespDetails.getCbText());
            if(transRespDetails.getStCode()!=null)
                hash.put("settlecode",transRespDetails.getStCode());
            if(transRespDetails.getStText()!=null)
                hash.put("settletext",transRespDetails.getStText());

        req.setAttribute("inquirydetails", hash);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/qwipiinquiry.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.FROMID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
