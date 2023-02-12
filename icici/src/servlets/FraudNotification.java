import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargebackManager;
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
import java.util.ArrayList;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/10/13
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudNotification extends HttpServlet
{
    private static Logger log = new Logger(FraudNotification.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in Fraud Notification");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        String url="/fraudNotification.jsp?ctoken=\""+user.getCSRFToken()+"\"";
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
        Functions functions = new Functions();
        String error= "";

        try
        {
            error = error + validateOptionalParameters(req);
            if(functions.isValueNull(error))
            {
                PZExceptionHandler.raiseConstraintViolationException("FraudNotification.java","doPost()",null,"icici",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            String trackingId= req.getParameter("STrackingid");
            String paymentId= req.getParameter("paymentid");
            String firstsix = req.getParameter("firstsix");
            String lastfour = req.getParameter("lastfour");
            String emailAddr = req.getParameter("emailaddr");
            String ipAddress=req.getParameter("ipaddress");
            String name = req.getParameter("name");
            String accountId=req.getParameter("accountid");
            String paymodeId=req.getParameter("paymodeid");
            String cardTypeId=req.getParameter("cardtypeid");
            String status=req.getParameter("status");
            String description=req.getParameter("description");
            String firstName=req.getParameter("firstname");
            String lastName=req.getParameter("lastname");
            String country=req.getParameter("country");
            String amount=req.getParameter("amount");
            String pgtypeid = req.getParameter("pgtypeid");
            String currency="";
            String gateway ="";
            String gateway_name = "";
            String memberId = "";

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
            if (!"0".equals(req.getParameter("toid")))
            {
                memberId = req.getParameter("toid");
            }

            if (!functions.isValueNull(gateway_name) && !functions.isValueNull(accountId) && !functions.isValueNull(memberId))
            {
                req.setAttribute("errorMsg", "Kindly provide Gateway, Account ID and Member ID");
                RequestDispatcher rd = req.getRequestDispatcher("/fraudNotification.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;

            }
            if(!functions.isValueNull(gateway_name) && !functions.isValueNull(accountId) || !functions.isValueNull(gateway_name) && !functions.isValueNull(memberId) || !functions.isValueNull(memberId) && !functions.isValueNull(accountId))
            {
                req.setAttribute("errorMsg", "Kindly provide the mandatory fields.");
                RequestDispatcher rd = req.getRequestDispatcher("/fraudNotification.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            transactionVO.setTrackingId(trackingId);
            transactionVO.setPaymentId(paymentId);
            transactionVO.setFirstSix(firstsix);
            transactionVO.setLastFour(lastfour);
            transactionVO.setEmailAddr(emailAddr);
            transactionVO.setIpAddress(ipAddress);
            transactionVO.setCustFirstName(firstName);
            transactionVO.setCustLastName(lastName);
            transactionVO.setToid(memberId);
            transactionVO.setAccountId(accountId);
            transactionVO.setPaymodeid(paymodeId);
            transactionVO.setCardTypeId(cardTypeId);
            transactionVO.setStatus(status);
            transactionVO.setOrderDesc(description);
            transactionVO.setCountry(country);
            transactionVO.setAmount(amount);
            transactionVO.setCurrency(currency);
            transactionVO.setName(name);
            transactionVO.setGatewayName(gateway_name);

            inputDateVO.setFdate(req.getParameter("fdate"));
            inputDateVO.setFmonth(req.getParameter("fmonth"));
            inputDateVO.setFyear(req.getParameter("fyear"));
            inputDateVO.setTdate(req.getParameter("tdate"));
            inputDateVO.setTmonth(req.getParameter("tmonth"));
            inputDateVO.setTyear(req.getParameter("tyear"));

            inputDateVO.setFdtstamp(Functions.converttomillisec(inputDateVO.getFmonth(), inputDateVO.getFdate(), inputDateVO.getFyear(), "0", "0", "0"));
            inputDateVO.setTdtstamp(Functions.converttomillisec(inputDateVO.getTmonth(), inputDateVO.getTdate(), inputDateVO.getTyear(), "23", "59", "59"));

            paginationVO.setInputs("paymentid="+paymentId+"&trackingid=" + transactionVO.getTrackingId()+"&first_six="+transactionVO.getFirstSix()+"&last_four="+transactionVO.getLastFour()+"&emailaddr="+transactionVO.getEmailAddr()+"&toid="+transactionVO.getToid()+"&accountid="+transactionVO.getAccountId()+"&status="+transactionVO.getStatus()+"&description="+transactionVO.getOrderDesc()+"&firstname="+transactionVO.getCustFirstName()+"&lastname="+transactionVO.getCustLastName()+"&name="+transactionVO.getName()+"&pgtypeid="+pgtypeid + "&fdate=" + inputDateVO.getFdate() + "&fmonth=" + inputDateVO.getFmonth() + "&fyear=" + inputDateVO.getFyear() + "&tdate=" + inputDateVO.getTdate() + "&tmonth=" + inputDateVO.getTmonth() + "&tyear=" + inputDateVO.getTyear());
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(FraudNotification.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

            cbVOList = chargebackManager.getFraudAction(transactionVO,inputDateVO,paginationVO);
            req.setAttribute("trackingid",trackingId);
            req.setAttribute("paymentid",paymentId);
            req.setAttribute("first_six",firstsix);
            req.setAttribute("last_four",lastfour);
            req.setAttribute("emailaddr",emailAddr);
            req.setAttribute("ipaddress",ipAddress);
            req.setAttribute("firstname",firstName);
            req.setAttribute("lastname",lastName);
            req.setAttribute("toid",memberId);
            req.setAttribute("accountid",accountId);
            req.setAttribute("paymodeid",paymodeId);
            req.setAttribute("cardtypeid",cardTypeId);
            req.setAttribute("status",status);
            req.setAttribute("description",description);
            req.setAttribute("country",country);
            req.setAttribute("amount",amount);
            req.setAttribute("currency",currency);
            req.setAttribute("name",name);
            req.setAttribute("pgtypeid",gateway_name);

            req.setAttribute("cbVO",cbVOList);
            req.setAttribute("paginationVO",paginationVO);
            req.setAttribute("error",error);

            RequestDispatcher rd = req.getRequestDispatcher("/fraudNotification.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        catch (PZConstraintViolationException cve)
        {
            log.error("PZConstraintViolationException:::::",cve);
            req.setAttribute("error",cve.getPzConstraint().getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/fraudNotification.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZDBViolationException:::::", dbe);
            req.setAttribute("error","Internal Error occurred : Please contact your Admin");
            RequestDispatcher rd = req.getRequestDispatcher("/fraudNotification.jsp?ctoken="+user.getCSRFToken());
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
        inputFieldsListOptional.add(InputFields.COMMASEPRATED_TRACKINGID_TRA);
        inputFieldsListOptional.add(InputFields.PAYMENTID);
        inputFieldsListOptional.add(InputFields.FIRST_SIX);
        inputFieldsListOptional.add(InputFields.LAST_FOUR);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.IPADDRESS);
        inputFieldsListOptional.add(InputFields.CUSTNAME);
        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListOptional.add(InputFields.PAYMODE);
        inputFieldsListOptional.add(InputFields.CARDTYPE);
        inputFieldsListOptional.add(InputFields.DESC);
        inputFieldsListOptional.add(InputFields.ORDERDESC);
        inputFieldsListOptional.add(InputFields.FROMDATE);
        inputFieldsListOptional.add(InputFields.TODATE);
        inputFieldsListOptional.add(InputFields.FROMMONTH);
        inputFieldsListOptional.add(InputFields.TOMONTH);
        inputFieldsListOptional.add(InputFields.FROMYEAR);
        inputFieldsListOptional.add(InputFields.TOYEAR);
        inputFieldsListOptional.add(InputFields.STARTTIME);
        inputFieldsListOptional.add(InputFields.ENDTIME);
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