import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BlacklistManager;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.PaginationVO;
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
public class BlacklistBin extends HttpServlet
{
    private static Logger log = new Logger(BlacklistBin.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String role_interface="admin";
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        Functions functions = new Functions();
        PaginationVO paginationVO = new PaginationVO();
        Connection conn=null;

        List<BlacklistVO> listOfBin = null;
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String memberId = "";
        String accountId ="";
        String error = "";
        String sMessage = "";
        String EOL = "<br>";

        String startBin=req.getParameter("startBin");
        String endBin=req.getParameter("endBin");
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        accountId = req.getParameter("accountid");
        memberId = req.getParameter("toid");
        String reason= req.getParameter("reason");
        String remark= req.getParameter("remark");
        String action=req.getParameter("unblock");
        boolean isValid = true;

        if ("block".equalsIgnoreCase(req.getParameter("bbtn")))
            isValid = false;

        BlacklistManager blacklistManager = new BlacklistManager();
        try
        {
            error = error + validateOptionalParameters(req);
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
            if (!ESAPI.validator().isValidInput("bin", startBin, "Numbers", 6, isValid))
            {
                log.debug("Invalid bin");
                sErrorMessage.append("Invalid Start Bin or Start Bin should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("bin", endBin, "Numbers", 6, isValid))
            {
                log.debug("Invalid bin");
                sErrorMessage.append("Invalid End Bin or End Bin should not be empty" + EOL);
            }
            if(!functions.isValueNull(accountId) && !functions.isValueNull(memberId))
            {
                accountId="";
                memberId="";
            }
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
            paginationVO.setInputs("&accountid=" + accountId + "&toid=" + memberId);
            paginationVO.setPage(BlacklistBin.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));


            if(functions.isValueNull(memberId)&& functions.isValueNull(accountId)){
                boolean valid=whiteListDAO.getMemberidAccountid(memberId,accountId);
                if(valid==false){
                    sErrorMessage.append("MemberID/AccountID NOT mapped.");
                }
            }
            if(functions.isValueNull(startBin)&& !functions.isValueNull(endBin)|| !functions.isValueNull(startBin) && functions.isValueNull(endBin)){
                sErrorMessage.append("Please provide both StartBin And EndBin ,else Click on Single Bin");
                String redirectpage = "/binBlacklist.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }
            if(functions.isValueNull(startBin) && functions.isValueNull(endBin))
            {
                if (startBin.compareTo(endBin) > 0)
                {
                    sErrorMessage.append("Start Bin greater than End Bin.");
                }
            }

            if (sErrorMessage.length() > 0)
            {
                String redirectpage = "/binBlacklist.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }

            if ("search".equalsIgnoreCase(req.getParameter("sbtn")))
            {
                if (functions.isValueNull(startBin) && functions.isValueNull(endBin)) {
                   listOfBin = blacklistManager.getBlockedBinPage(startBin, endBin, accountId, memberId,reason,remark,paginationVO,role_interface);
                }
                else
                {
                    listOfBin = blacklistManager.getBlockedBinPage(startBin, endBin, accountId, memberId,reason,remark,paginationVO,role_interface);
                }
            }
            if ("block".equalsIgnoreCase(req.getParameter("bbtn")))
            {
                listOfBin = blacklistManager.getBlockedBinPage(startBin, endBin, accountId, memberId,reason,remark,paginationVO,role_interface);
                if(listOfBin.size()<=0)
                {
                    blacklistManager.addBin(startBin, endBin, accountId, memberId, reason,remark,actionExecutorId,actionExecutorName,role_interface);
                    sMessage = req.getParameter("toid") + "-" + startBin + "-" + endBin + "- is Blocked Successfully";
                    listOfBin = blacklistManager.getBlockedBinPage(startBin, endBin, accountId, memberId, reason,remark,paginationVO,role_interface);
                }else {
                    sMessage="Your Bin is already blocked.";
                }
            }
            String[] ids=req.getParameterValues("id");
            if ("unblock".equalsIgnoreCase(action))
            {
                for(String id : ids)
                {
                    blacklistManager.unblockBin(id);
                }
                sMessage =" Records Unblocked Successfully";
                listOfBin = blacklistManager.getBlockedBinPage(startBin, endBin, accountId, memberId,reason,remark,paginationVO,role_interface);
            }
            paginationVO.setInputs("&accountid=" + accountId + "&toid=" + memberId);

            listOfBin = blacklistManager.getBlockedBinPage(startBin, endBin, accountId, memberId,reason,remark,paginationVO,role_interface);
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
        String redirectpage = "/binBlacklist.jsp?ctoken=" + user.getCSRFToken();
        req.setAttribute("paginationVO", paginationVO);
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
