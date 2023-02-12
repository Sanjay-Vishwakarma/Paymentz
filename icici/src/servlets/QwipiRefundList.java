import com.directi.pg.Admin;
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
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 12/7/12
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiRefundList extends HttpServlet
{
    private static Logger logger = new Logger(QwipiRefundList.class.getName());
    //for error message
    private static CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
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
        //Validation errorlist instance
        ValidationErrorList validationErrorList=null;
        //session check
        HttpSession session = Functions.getNewSession(req);
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String errormsg="";
        boolean flag = true;
        String EOL = "<BR>";
        Functions functions= new Functions();
        RequestDispatcher rdError=req.getRequestDispatcher("/qwipirefundlist.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

        try
        {
            validateOptionalParameters(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Input ::::::",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;
            logger.debug("message..."+e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/qwipirefundlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        validationErrorList=validateOptionalParameter(req);
        try
        {
            if(!validationErrorList.isEmpty())
               // if(functions.isValueNull(error))
            {
                logger.debug("invalid data Provided");
                StringBuffer errorMessage= new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage,validationErrorList);
                PZExceptionHandler.raiseConstraintViolationException(QwipiRefundList.class.getName(),"doPost()",null,"Admin",errorMessage.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            String trackingid= req.getParameter("trackingid");
            StringBuffer trackingIds=new StringBuffer();
            if (functions.isValueNull(trackingid))
            {
                List<String> trackingidList=null;
                if(trackingid.contains(","))
                {
                    trackingidList  = Arrays.asList(trackingid.split(","));
                }
                else
                {
                    trackingidList= Arrays.asList(trackingid.split(" "));
                }

                int i = 0;
                Iterator itr = trackingidList.iterator();
                while (itr.hasNext())
                {
                    if (i != 0)
                    {
                        trackingIds.append(",");
                    }
                    trackingIds.append("" + itr.next() + "");
                    i++;
                }
            }

            inputDateVO.setFdate(req.getParameter("fdate"));
            inputDateVO.setFmonth(req.getParameter("fmonth"));
            inputDateVO.setFyear(req.getParameter("fyear"));
            inputDateVO.setTdate(req.getParameter("tdate"));
            inputDateVO.setTmonth(req.getParameter("tmonth"));
            inputDateVO.setTyear(req.getParameter("tyear"));

            inputDateVO.setFdtstamp(Functions.converttomillisec(inputDateVO.getFmonth(), inputDateVO.getFdate(),inputDateVO.getFyear(), "0", "0", "0"));
            inputDateVO.setTdtstamp(Functions.converttomillisec(inputDateVO.getTmonth(),inputDateVO.getTdate(),  inputDateVO.getTyear(), "23", "59", "59"));
            transactionVO.setTrackingId(trackingIds.toString());
            transactionVO.setToid( req.getParameter("toid"));

            paginationVO.setInputs("toid="+transactionVO.getToid()+"&trackingid="+transactionVO.getTrackingId()+"&fdate="+inputDateVO.getFdate()+"&fmonth="+inputDateVO.getFmonth()+"&fyear="+inputDateVO.getFyear()+"&tdate="+inputDateVO.getTdate()+"&tmonth="+inputDateVO.getTmonth()+"&tyear="+inputDateVO.getTyear());
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(QwipiRefundList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

            refundVOList = refundManager.getQwipiRefundList(transactionVO, inputDateVO,paginationVO);

            req.setAttribute("refundVOList", refundVOList);
            req.setAttribute("paginationVO",paginationVO);
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("constrain Violation Exception exception",cve);
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.ADMIN_REFUND);
            /*req.setAttribute("error",validationErrorList);
            rdError.forward(req,res);
            return;*/
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in QwipiRefundList------",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,session.getAttribute("merchantid").toString(), PZOperations.ADMIN_REFUND);
            /*req.setAttribute("error","Internal Error occurred : Please contact your Admin");
            rdError.forward(req,res);
            return;*/
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        RequestDispatcher rdSuccess= req.getRequestDispatcher("/qwipirefundlist.jsp?Success=YES&ctoken="+user.getCSRFToken());
        rdSuccess.forward(req, res);
        return;
    }

    private ValidationErrorList validateOptionalParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        ValidationErrorList errorList = new ValidationErrorList();
        inputFieldsListMandatory.add(InputFields.COMMA_SEPRATED_NUM);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputValidator.InputValidations(req,inputFieldsListMandatory,errorList,true);
        return errorList;
    }

    private void validateOptionalParameters(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
