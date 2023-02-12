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
public class QwipiChargebackList extends HttpServlet
{
    private static Logger log = new Logger(QwipiChargebackList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in QwipiChargebackList");

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

        log.debug("success");

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String error = "";
        String toid = "";

        error = error + validateOptionalParameter(req);

        try
        {
            if(functions.isValueNull(error))
            {
                PZExceptionHandler.raiseConstraintViolationException("CommonRefundList.java","doPost()",null,"icici",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }
            String trackingId = req.getParameter("trackingid");

            StringBuffer trackingIds=new StringBuffer();
            if(functions.isValueNull(trackingId))
            {
                List<String> trackingidList=null;
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
            String mid= req.getParameter("mid");


            transactionVO.setToid(toid);
            transactionVO.setQwipiPaymentOrderNumber(mid);
            transactionVO.setTrackingId(trackingIds.toString());

            inputDateVO.setFdate(req.getParameter("fdate"));
            inputDateVO.setFmonth(req.getParameter("fmonth"));
            inputDateVO.setFyear(req.getParameter("fyear"));
            inputDateVO.setTdate(req.getParameter("tdate"));
            inputDateVO.setTmonth(req.getParameter("tmonth"));
            inputDateVO.setTyear(req.getParameter("tyear"));
            log.debug("fdate---" + inputDateVO.getFmonth() + "-" + inputDateVO.getFdate() + "-" + inputDateVO.getFyear());
            log.debug("tdate---" + inputDateVO.getTmonth() + "-" + inputDateVO.getTdate() + "-" + inputDateVO.getTyear());

            inputDateVO.setFdtstamp(Functions.converttomillisec(inputDateVO.getFmonth(), inputDateVO.getFdate(), inputDateVO.getFyear(), "0", "0", "0"));
            inputDateVO.setTdtstamp(Functions.converttomillisec(inputDateVO.getTmonth(), inputDateVO.getTdate(), inputDateVO.getTyear(), "23", "59", "59"));

            log.debug("fdtstamp---" + inputDateVO.getFdtstamp());
            log.debug("tdtstamp---" + inputDateVO.getTdtstamp());

            paginationVO.setInputs("toid="+transactionVO.getToid()+"&trackingid="+transactionVO.getTrackingId()+"&fdate="+inputDateVO.getFdate()+"&fmonth="+inputDateVO.getFmonth()+"&fyear="+inputDateVO.getFyear()+"&tdate="+inputDateVO.getTdate()+"&tmonth="+inputDateVO.getTmonth()+"&tyear="+inputDateVO.getTyear());

            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(QwipiChargebackList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

            cbVOList = chargebackManager.getQwipiChargebackDetails(transactionVO,inputDateVO,paginationVO);
            reasonVOList = chargebackManager.getCBReasonList();

            req.setAttribute("cbVO",cbVOList);
            req.setAttribute("reason",reasonVOList);

            req.setAttribute("paginationVO",paginationVO);
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("PZConstraintViolationException in QwipiRefundList",cve);
            PZExceptionHandler.handleCVEException(cve,toid, PZOperations.ADMIN_CHARGEBACK);
            /*req.setAttribute("error",cve.getPzConstraint().getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/qwipichargebacklist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;*/
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZDBViolationException in QwipiRefundList------",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,toid, PZOperations.ADMIN_CHARGEBACK);
            /*req.setAttribute("error","Internal Error occurred : Please contact your Admin");
            RequestDispatcher rd = req.getRequestDispatcher("/qwipichargebacklist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;*/
        }
        req.setAttribute("error",error);
        RequestDispatcher rd = req.getRequestDispatcher("/qwipichargebacklist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
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
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

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
