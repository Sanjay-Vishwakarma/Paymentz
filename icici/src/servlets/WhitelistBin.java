import com.directi.pg.*;
import com.manager.WhiteListManager;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User:Mahima Rai.
 * Date:07/04/18
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhitelistBin extends HttpServlet
{
    private static Logger log = new Logger(WhitelistBin.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        Functions functions = new Functions();
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        PaginationVO paginationVO = new PaginationVO();
        Connection conn=null;

        List<WhitelistingDetailsVO> listOfBin = null;
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String memberId = "";
        String accountId ="";
        String error = "";
        String sMessage = "";
        String EOL = "<br>";

        String startBin="";
        startBin=req.getParameter("startBin");
        String endBin="";
        endBin=req.getParameter("endBin");
        String startCard=req.getParameter("startCard");
        String endCard=req.getParameter("endCard");

        accountId = req.getParameter("accountid");
        memberId = req.getParameter("toid");
        String action=req.getParameter("delete");



        boolean isValid = true;
        if ("whitelist".equalsIgnoreCase(req.getParameter("bbtn")))
            isValid = false;

        WhiteListManager whiteListManager = new WhiteListManager();
        try
        {
            error = error + validateOptionalParameters(req);
            if(functions.isValueNull(error))
            {
                PZExceptionHandler.raiseConstraintViolationException("CommonRefundList.java", "doPost()", null, "icici", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
            }
            if (!ESAPI.validator().isValidInput("toid", memberId, "Numbers", 50, isValid))
            {
                log.debug("Invalid memberid");
                sErrorMessage.append("Invalid memberID or memberID should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("accountid", accountId, "Numbers", 50, isValid))
            {
                log.debug("Invalid accountid");
                sErrorMessage.append("Invalid accountID or accountID should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("bin", startBin, "FirstSixcc", 6, isValid))
            {
                log.debug("Invalid bin");
                sErrorMessage.append("Invalid Start Bin or Start Bin should not be empty" + EOL);
            }

            if (!ESAPI.validator().isValidInput("startCard", startCard, "Numbers", 13, isValid))
            {
                log.debug("Invalid Card");
                sErrorMessage.append("Invalid Start Card or Start Card should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("bin", endBin, "FirstSixcc", 6, isValid))
            {
                log.debug("Invalid bin");
                sErrorMessage.append("Invalid End Bin or End Bin should not be empty" + EOL);
            }

            if (!ESAPI.validator().isValidInput("endCard", endCard, "Numbers", 13, isValid))
            {
                log.debug("Invalid Card");
                sErrorMessage.append("Invalid End Card or End Card should not be empty" + EOL);
            }

            if(functions.isValueNull(memberId)&& functions.isValueNull(accountId)){
                boolean valid=whiteListDAO.getMemberidAccountid(memberId,accountId);
                if(valid==false){
                    sErrorMessage.append("MemberID/AccountID NOT mapped.");
                }
            }
            if(functions.isValueNull(startBin)&& !functions.isValueNull(endBin)|| !functions.isValueNull(startBin) && functions.isValueNull(endBin)){
                sErrorMessage.append("Please provide both StartBin And EndBin ,else Click on Single Bin");
                String redirectpage = "/whitelistBin.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }

            if( isValid==false) {
                if (startBin.compareTo(endBin) > 0) {
                    sErrorMessage.append("Start Bin greater than End Bin.");
                }
            }

            if( isValid==false) {
                if (startBin.compareTo(endBin) == 0&&(startCard.compareTo(endCard) > 0))
                {
                    sErrorMessage.append("Start Card greater than End Card.");
                }
            }
            if (sErrorMessage.length() > 0) {
                String redirectpage = "/whitelistBin.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }

            if ("search".equalsIgnoreCase(req.getParameter("sbtn"))) {
                if (functions.isValueNull(startBin) && functions.isValueNull(endBin) && functions.isValueNull(startCard) && functions.isValueNull(endCard)) {
                    listOfBin = whiteListManager.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountId, memberId,actionExecutorId,actionExecutorName, paginationVO);
                }
                else {
                    listOfBin = whiteListManager.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountId, memberId,actionExecutorId,actionExecutorName, paginationVO);
                }
            }
            if ("whitelist".equalsIgnoreCase(req.getParameter("bbtn"))) {
                listOfBin = whiteListManager.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountId, memberId, actionExecutorId,actionExecutorName,paginationVO);
                if(listOfBin.size()<=0)
                {
                    whiteListManager.addBin(startBin, endBin,startCard,endCard,accountId, memberId,actionExecutorId,actionExecutorName);
                    sMessage = req.getParameter("toid") + "-" + startBin + "-" + endBin + "- is Whitelisted Successfully";
                    listOfBin = whiteListManager.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountId, memberId, actionExecutorId, actionExecutorName, paginationVO);
                }
                else {
                    sMessage="Your Bin Already Whitelisted";
                }
            }
            String[] ids=req.getParameterValues("id");
            if ("delete".equalsIgnoreCase(action))
            {
                for(String id : ids)
                {
                    whiteListManager.removeWhitelistBinEntry(id);
                }
                sMessage ="Records Deleted Successfully";
                if (functions.isValueNull(startBin) && functions.isValueNull(endBin) && functions.isValueNull(startCard) && functions.isValueNull(endCard)) {
                    listOfBin = whiteListManager.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountId, memberId,actionExecutorId,actionExecutorName, paginationVO);
                }
                else
                {
                    listOfBin = whiteListManager.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountId, memberId,actionExecutorId,actionExecutorName, paginationVO);
                }
            }
            listOfBin = whiteListManager.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountId, memberId,actionExecutorId,actionExecutorName, paginationVO);

            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
            paginationVO.setInputs("accountid="+accountId + "&toid=" + memberId);
            paginationVO.setPage(WhitelistBin.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));
            req.setAttribute("paginationVO", paginationVO);
        }
        catch (Exception e)
        {
            log.error("Catch Exception...",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        String redirectpage = "/whitelistBin.jsp?ctoken=" + user.getCSRFToken();
        req.setAttribute("error", error);
        req.setAttribute("msg",sMessage);
        req.setAttribute("listofBin",listOfBin);
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
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