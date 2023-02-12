import com.directi.pg.*;
import com.manager.RefundManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;

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
 * User: Nishant
 * Date: 12/7/12
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreRefundList extends HttpServlet
{
    static Logger logger = new Logger(EcoreRefundList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        //manager instance
        RefundManager refundManager = new RefundManager();
        //vo instance
        TransactionVO transactionVO = new TransactionVO();
        PaginationVO paginationVO = new PaginationVO();
        InputDateVO inputDateVO = new InputDateVO();
        //List of TransactionVo
        List<TransactionVO> refundVOList = null;
        //session check
        HttpSession session = Functions.getNewSession(req);

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
        Functions functions=new Functions();
        String error = "";
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        error = error + validateOptionalParameter(req);

        try
        {

            if(!error.isEmpty())
            {
                PZExceptionHandler.raiseConstraintViolationException("EcoreRefundList.java","doPost()",null,"admin",error.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);

            }
            String trackingId=req.getParameter("trackingid");

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

            inputDateVO.setFdate(req.getParameter("fdate"));
            inputDateVO.setFmonth(req.getParameter("fmonth"));
            inputDateVO.setFyear(req.getParameter("fyear"));
            inputDateVO.setTdate(req.getParameter("tdate"));
            inputDateVO.setTmonth(req.getParameter("tmonth"));
            inputDateVO.setTyear(req.getParameter("tyear"));
            inputDateVO.setFdtstamp(Functions.converttomillisec(inputDateVO.getFmonth(),inputDateVO.getFdate(), inputDateVO.getFyear(), "0", "0", "0"));
            inputDateVO.setTdtstamp(Functions.converttomillisec(inputDateVO.getTmonth(),inputDateVO.getTdate(),  inputDateVO.getTyear(), "23", "59", "59"));
            transactionVO.setTrackingId(trackingIds.toString());
            transactionVO.setToid(req.getParameter("toid"));

            paginationVO.setInputs("toid="+transactionVO.getToid()+"&trackingid="+transactionVO.getTrackingId()+"&fdate="+inputDateVO.getFdate()+"&fmonth="+inputDateVO.getFmonth()+"&fyear="+inputDateVO.getFyear()+"&tdate="+inputDateVO.getTdate()+"&tmonth="+inputDateVO.getTmonth()+"&tyear="+inputDateVO.getTyear());
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(EcoreRefundList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

            refundVOList = refundManager.getEcoreRefundList(transactionVO,inputDateVO,paginationVO);

            req.setAttribute("refundVOList", refundVOList);
            req.setAttribute("paginationVO",paginationVO);
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("constrain Violation Exception exception",cve);
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.ADMIN_REFUND);
            /*req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/ecorerefundlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;*/
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in EcoreRefundList------",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,session.getAttribute("merchantid").toString(), PZOperations.ADMIN_REFUND);
            /*req.setAttribute("error","Internal Error occurred : Please contact your Admin");
            RequestDispatcher rd = req.getRequestDispatcher("/ecorerefundlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;*/
        }
        req.setAttribute("error",error);
        RequestDispatcher rd = req.getRequestDispatcher("/ecorerefundlist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateOptionalParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

}
