import com.directi.pg.Admin;
import com.directi.pg.*;
import com.manager.RefundManager;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
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
 * Date: 4/4/13
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonRefundList extends HttpServlet
{
    private static Logger log = new Logger(CommonRefundList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in CommonRefundList");

        log.debug("ctoken==="+req.getParameter("ctoken"));
        HttpSession session = Functions.getNewSession(req);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        //manager instance
        RefundManager refundManager = new RefundManager();
        //vo instance
        TransactionVO transactionVO = new TransactionVO();
        PaginationVO paginationVO = new PaginationVO();
        InputDateVO inputDateVO = new InputDateVO();
        //List of TransactionVo
        List<TransactionVO> refundVOList = null;
        //Validation errorlist instance
        //session check

        Functions functions = new Functions();



        String error= "";

        try
        {
            error = error + validateOptionalParameters(req);
            if(functions.isValueNull(error))
            {
                PZExceptionHandler.raiseConstraintViolationException("CommonRefundList.java","doPost()",null,"icici",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            String toId= req.getParameter("toid");
            String paymentId= req.getParameter("paymentid");
            String trackingId= req.getParameter("trackingid");

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

            transactionVO.setToid(toId);
            transactionVO.setPaymentId(paymentId);
            transactionVO.setTrackingId(trackingIds.toString());

            inputDateVO.setFdate(req.getParameter("fdate"));
            inputDateVO.setFmonth(req.getParameter("fmonth"));
            inputDateVO.setFyear(req.getParameter("fyear"));
            inputDateVO.setTdate(req.getParameter("tdate"));
            inputDateVO.setTmonth(req.getParameter("tmonth"));
            inputDateVO.setTyear(req.getParameter("tyear"));
            inputDateVO.setFdtstamp(Functions.converttomillisec(inputDateVO.getFmonth(),inputDateVO.getFdate(), inputDateVO.getFyear(), "0", "0", "0"));
            inputDateVO.setTdtstamp(Functions.converttomillisec( inputDateVO.getTmonth(),inputDateVO.getTdate(), inputDateVO.getTyear(), "23", "59", "59"));
            log.debug("fdate::"+inputDateVO.getFdate()+" fmonth::"+inputDateVO.getFmonth()+" fyear::"+inputDateVO.getFyear()+" tdate::"+inputDateVO.getTdate()+" tmonth::"+inputDateVO.getTmonth()+" tyear::"+inputDateVO.getTyear()+" fdstamp::"+inputDateVO.getFdtstamp()+" tdstamp::"+inputDateVO.getTdtstamp());
            paginationVO.setInputs("toid="+transactionVO.getToid()+"&trackingid="+transactionVO.getTrackingId()+"&fdate="+inputDateVO.getFdate()+"&fmonth="+inputDateVO.getFmonth()+"&fyear="+inputDateVO.getFyear()+"&tdate="+inputDateVO.getTdate()+"&tmonth="+inputDateVO.getTmonth()+"&tyear="+inputDateVO.getTyear());
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(CommonRefundList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

            refundVOList = refundManager.getCommonRefundList(transactionVO,inputDateVO,paginationVO);

            req.setAttribute("toid",toId);
            req.setAttribute("paymentid",paymentId);
            req.setAttribute("trackingid",trackingIds.toString());
            req.setAttribute("refundVOList", refundVOList);
            req.setAttribute("paginationVO",paginationVO);
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/commonrefundlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("PZConstraintViolationException:::::::", cve);
            req.setAttribute("error",cve.getPzConstraint().getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/commonrefundlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZConstraintViolationException::::::", dbe);
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/commonrefundlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PAYMENTID);
        inputFieldsListOptional.add(InputFields.COMMA_SEPRATED_NUM);
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
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
