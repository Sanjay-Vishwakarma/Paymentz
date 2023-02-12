import com.directi.pg.*;
import com.manager.ChargebackManager;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;
import com.manager.vo.payoutVOs.CBReasonsVO;

import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/24/13
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreChargebackList extends HttpServlet
{
    static Logger log = new Logger(EcoreChargebackList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in EcoreChargebackList");

        ChargebackManager chargebackManager = new ChargebackManager();

        TransactionVO transactionVO = new TransactionVO();
        PaginationVO paginationVO = new PaginationVO();
        InputDateVO inputDateVO = new InputDateVO();
        //List of TransactionVo
        List<TransactionVO> cbVOList = null;
        List<CBReasonsVO> reasonVOList = null;
        Functions functions = new Functions();

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));
        boolean flag=false;
        String errormsg="";
        String EOL = "<BR>";

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String error = "";
        String toid = "";

        try
        {
            error = error + validateOptionalParameter(req);
            if(functions.isValueNull(error))
            {
                PZExceptionHandler.raiseConstraintViolationException("EcoreChargebackList.java","doPost()",null,"icici",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            //String trackingId= req.getParameter("trackingid");
            String trackingId = req.getParameter("trackingid");

            StringBuffer trackingIds=new StringBuffer();
            if (functions.isValueNull(trackingId))
            {
                List<String> trackingidList = null;
                if(trackingId.contains(","))
                {
                    trackingidList = Arrays.asList(trackingId.split(","));
                }
                else
                {
                    trackingidList = Arrays.asList(trackingId.split(" "));
                }

                int i = 0;
                Iterator itr = trackingidList.iterator();
                while (itr.hasNext())
                {
                    if(i!=0)
                    {
                        trackingIds.append(",");
                    }
                    trackingIds.append(""+itr.next()+"");
                    i++;
                }
            }

            toid= req.getParameter("toid");
            String paymentOrderNo = req.getParameter("paymentOrderNo");


            transactionVO.setToid(toid);
            transactionVO.setEcorePaymentOrderNumber(paymentOrderNo);
            transactionVO.setTrackingId(trackingIds.toString());

            inputDateVO.setFdate(req.getParameter("fdate"));
            inputDateVO.setFmonth(req.getParameter("fmonth"));
            inputDateVO.setFyear(req.getParameter("fyear"));
            inputDateVO.setTdate(req.getParameter("tdate"));
            inputDateVO.setTmonth(req.getParameter("tmonth"));
            inputDateVO.setTyear(req.getParameter("tyear"));
            inputDateVO.setFdtstamp(Functions.converttomillisec(inputDateVO.getFdate(), inputDateVO.getFmonth(), inputDateVO.getFyear(), "0", "0", "0"));
            inputDateVO.setTdtstamp(Functions.converttomillisec(inputDateVO.getTdate(), inputDateVO.getTmonth(), inputDateVO.getTyear(), "23", "59", "59"));

            paginationVO.setInputs("toid="+transactionVO.getToid()+"&trackingid="+transactionVO.getTrackingId()+"&paymentOrderNo="+transactionVO.getEcorePaymentOrderNumber()+"&fdate="+inputDateVO.getFdate()+"&fmonth="+inputDateVO.getFmonth()+"&fyear="+inputDateVO.getFyear()+"&tdate="+inputDateVO.getTdate()+"&tmonth="+inputDateVO.getTmonth()+"&tyear="+inputDateVO.getTyear());
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(EcoreChargebackList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

            cbVOList = chargebackManager.getEcoreChargebackDetails(transactionVO,inputDateVO,paginationVO);
            reasonVOList = chargebackManager.getCBReasonList();

            req.setAttribute("cbVO",cbVOList);
            req.setAttribute("paginationVO",paginationVO);
            req.setAttribute("error",error);
            req.setAttribute("reason",reasonVOList);
            RequestDispatcher rd = req.getRequestDispatcher("/ecorechargebacklist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("PZConstraintViolationException in EcoreChargebackList",cve);
            PZExceptionHandler.handleCVEException(cve,toid, PZOperations.ADMIN_CHARGEBACK);
            req.setAttribute("error",cve.getPzConstraint().getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/ecorechargebacklist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZDBViolationException in EcoreChargebackList------",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,toid, PZOperations.ADMIN_CHARGEBACK);
            req.setAttribute("error","Internal Error occurred : Please contact your Admin");
            RequestDispatcher rd = req.getRequestDispatcher("/ecorechargebacklist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
    }
    private String validateOptionalParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        String error = "";
        String EOL = "<BR>";
        inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.MID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields : inputFieldsListMandatory)
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
}
