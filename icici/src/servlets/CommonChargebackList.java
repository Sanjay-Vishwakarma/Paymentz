import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargebackManager;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;
import com.manager.vo.payoutVOs.CBReasonsVO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/10/13
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonChargebackList extends HttpServlet
{
    private static Logger log = new Logger(CommonChargebackList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in CommonRefundList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        ChargebackManager chargebackManager = new ChargebackManager();
        TransactionVO transactionVO = new TransactionVO();
        PaginationVO paginationVO = new PaginationVO();
        InputDateVO inputDateVO = new InputDateVO();

        List<TransactionVO> cbVOList = null;
        List<CBReasonsVO> reasonVOList = null;
        Functions functions = new Functions();

        String error= "";
        String errorMsg="";
        String currency = "";
        String gateway = "";
        String gateway_name = "";
        try
        {
            error = error + validateOptionalParameters(req);
            if(functions.isValueNull(error))
            {
                PZExceptionHandler.raiseConstraintViolationException("CommonRefundList.java","doPost()",null,"icici",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            String trackingId= req.getParameter("trackingid");
            String paymentid= req.getParameter("paymentid");
            String toid= req.getParameter("toid");
            String accountId=req.getParameter("accountid");
            //gateway=req.getParameter("pgtypeid");
            String pgtypeid = req.getParameter("pgtypeid");

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
            if (req.getParameter("pgtypeid")!=null && req.getParameter("pgtypeid").split("-").length == 3 && !req.getParameter("pgtypeid").equalsIgnoreCase(""))
            {
                gateway = req.getParameter("pgtypeid").split("-")[2];
                currency = req.getParameter("pgtypeid").split("-")[1];
                gateway_name = req.getParameter("pgtypeid").split("-")[0];
            }
            if (!(req.getParameter("pgtypeid").split("-").length == 3))
            {
                gateway_name = req.getParameter("pgtypeid");
            }
            /*if(!functions.isValueNull(gateway_name))
            {
                req.setAttribute("errorMsg","Kindly provide Gateway.");
                RequestDispatcher rd = req.getRequestDispatcher("/commonchargeback.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }*/
            transactionVO.setToid(toid);
            transactionVO.setAccountId(accountId);
            transactionVO.setGatewayName(gateway_name);
            transactionVO.setCurrency(currency);
            transactionVO.setPaymentId(paymentid);
            transactionVO.setTrackingId(trackingIds.toString());
            transactionVO.setCurrency(currency);

            inputDateVO.setFdate(req.getParameter("fdate"));
            inputDateVO.setFmonth(req.getParameter("fmonth"));
            inputDateVO.setFyear(req.getParameter("fyear"));
            inputDateVO.setTdate(req.getParameter("tdate"));
            inputDateVO.setTmonth(req.getParameter("tmonth"));
            inputDateVO.setTyear(req.getParameter("tyear"));

            inputDateVO.setFdtstamp(Functions.converttomillisec(inputDateVO.getFmonth(), inputDateVO.getFdate(), inputDateVO.getFyear(), "0", "0", "0"));
            inputDateVO.setTdtstamp(Functions.converttomillisec(inputDateVO.getTmonth(), inputDateVO.getTdate(), inputDateVO.getTyear(), "23", "59", "59"));

            paginationVO.setInputs("paymentid="+paymentid+"&pgtypeid="+pgtypeid+"&currency="+currency+"&accountid="+transactionVO.getAccountId()+"&toid="+transactionVO.getToid()+"&trackingid=" + transactionVO.getTrackingId() + "&fdate=" + inputDateVO.getFdate() + "&fmonth=" + inputDateVO.getFmonth() + "&fyear=" + inputDateVO.getFyear() + "&tdate=" + inputDateVO.getTdate() + "&tmonth=" + inputDateVO.getTmonth() + "&tyear=" + inputDateVO.getTyear());
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(CommonChargebackList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

            cbVOList = chargebackManager.getCommonChargebackDetails(transactionVO,inputDateVO,paginationVO);
            reasonVOList = chargebackManager.getCBReasonList();

            req.setAttribute("paymentid",paymentid);
            req.setAttribute("pgtypeid",gateway_name);
            req.setAttribute("currency",currency);
            req.setAttribute("accountid",accountId);
            req.setAttribute("toid",toid);
            req.setAttribute("trackingid",trackingId.toString());
            req.setAttribute("cbVO",cbVOList);
            req.setAttribute("reason",reasonVOList);
            req.setAttribute("paginationVO",paginationVO);
            req.setAttribute("error",error);
            req.setAttribute("errorMsg",errorMsg);

            RequestDispatcher rd = req.getRequestDispatcher("/commonchargeback.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (PZConstraintViolationException cve)
        {
            req.setAttribute("error",cve.getPzConstraint().getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/commonchargeback.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (Exception dbe)
        {
            log.error("Catch Exception...",dbe);
            req.setAttribute("error","Sorry, No records found");
            RequestDispatcher rd = req.getRequestDispatcher("/commonchargeback.jsp?ctoken="+user.getCSRFToken());
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
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.PAYMENTID);
        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

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