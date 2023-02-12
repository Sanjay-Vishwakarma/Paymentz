import com.directi.pg.*;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 4/14/15
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhiteListBin extends HttpServlet
{
    private static Logger logger = new Logger(WhiteListBin.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        StringBuilder sErrorMessage = new StringBuilder();
        String msg = "";
        Merchants merchants=new Merchants();
        Functions functions=new Functions();
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String WhiteListBin_start_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_start_errormsg"))?rb1.getString("WhiteListBin_start_errormsg"): "Invalid Start Bin or Start Bin should not be empty";
        String WhiteListBin_card_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_card_errormsg"))?rb1.getString("WhiteListBin_card_errormsg"): "Invalid Start Card or Start Card should not be empty";
        String WhiteListBin_endcard_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_endcard_errormsg"))?rb1.getString("WhiteListBin_endcard_errormsg"): "Invalid End Card or End Card should not be empty";
        String WhiteListBin_greater_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_greater_errormsg"))?rb1.getString("WhiteListBin_greater_errormsg"): "Start Card greater than End Card.";
        String WhiteListBin_endbin_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_endbin_errormsg"))?rb1.getString("WhiteListBin_endbin_errormsg"): "Invalid End Bin or End Bin should not be empty";
        String WhiteListBin_accountID_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_accountID_errormsg"))?rb1.getString("WhiteListBin_accountID_errormsg"): "Invalid accountID or accountID should not be empty";
        String WhiteListBin_memberid_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_memberid_errormsg"))?rb1.getString("WhiteListBin_memberid_errormsg"): "MemberID/AccountID NOT mapped.";
        String WhiteListBin_please_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_please_errormsg"))?rb1.getString("WhiteListBin_please_errormsg"): "Please provide both StartBin And EndBin ,else Click on Single Bin";
        String WhiteListBin_uploaded_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_uploaded_errormsg"))?rb1.getString("WhiteListBin_uploaded_errormsg"): "Records Uploaded Successfully.";
        String WhiteListBin_already_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_already_errormsg"))?rb1.getString("WhiteListBin_already_errormsg"): "Records Already Uploaded..";
        String WhiteListBin_deleted_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListBin_deleted_errormsg"))?rb1.getString("WhiteListBin_deleted_errormsg"): "Records deleted successfully.";
        List<WhitelistingDetailsVO> listOfBin = null;
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        PaginationVO paginationVO=new PaginationVO();
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String errormsg="";
        String EOL="<BR>";
        String memberid = (String) session.getAttribute("merchantid");
        String startBin=req.getParameter("startBin");
        String endBin=req.getParameter("endBin");
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        String startCard=req.getParameter("startCard");
        String endCard=req.getParameter("endCard");
        String action=req.getParameter("upload");
        String delete=req.getParameter("delete");
        String accountid = req.getParameter("accountid");
        paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"),1));
        paginationVO.setPage(WhiteListBin.class.getName());
        paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
        paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));
        paginationVO.setInputs("&startBin="+startBin+"&endBin="+endBin);

        boolean isValid = true;
        if ("upload".equalsIgnoreCase(action))
            isValid = false;

        try
        {
            errormsg = errormsg + validateOptionalParameters(req);
            if(functions.isValueNull(errormsg))
            {
                req.setAttribute("error", errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/whitelistBin.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            if (!ESAPI.validator().isValidInput("startBin", startBin, "FirstSixcc", 6, isValid))
            {

                sErrorMessage.append(WhiteListBin_start_errormsg + EOL);
            }

            if (!ESAPI.validator().isValidInput("startCard", req.getParameter("startCard"), "OnlyNumber", 13, isValid))
            {
                logger.debug("Invalid start Card::::::::::::::::::::::");
                sErrorMessage.append(WhiteListBin_card_errormsg + EOL);
            }
            if (!ESAPI.validator().isValidInput("endCard", endCard, "OnlyNumber", 13, isValid))
            {

                sErrorMessage.append(WhiteListBin_endcard_errormsg + EOL);
            }
            else if (functions.isValueNull(startCard) && functions.isValueNull(endCard))
            {
                if (startCard.compareTo(endCard) > 0 && Integer.parseInt(startBin)>Integer.parseInt(endBin) )
                {
                    sErrorMessage.append(WhiteListBin_greater_errormsg);
                }
            }
            if (!ESAPI.validator().isValidInput("endBin", endBin, "FirstSixcc", 6, isValid))
            {
                logger.debug("Invalid bin");
                sErrorMessage.append(WhiteListBin_endbin_errormsg + EOL);
            }

            if (!ESAPI.validator().isValidInput("accountid", accountid, "Numbers", 50, isValid))
            {
                sErrorMessage.append(WhiteListBin_accountID_errormsg + EOL);
            }
            else
            if(functions.isValueNull(memberid)&& functions.isValueNull(accountid)){
                boolean valid=whiteListDAO.getMemberidAccountid(memberid,accountid);
                if(valid==false){
                    sErrorMessage.append(WhiteListBin_memberid_errormsg);
                }
            }
            if(functions.isValueNull(startBin)&& !functions.isValueNull(endBin)|| !functions.isValueNull(startBin) && functions.isValueNull(endBin)){
                sErrorMessage.append(WhiteListBin_please_errormsg);
                String redirectpage = "/whitelistBin.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }
            /*if (functions.isValueNull(startBin) && functions.isValueNull(endBin))
            {
                if (startBin.compareTo(endBin) > 0)
                {
                    sErrorMessage.append("Start Bin greater than End Bin.");
                }
            }*/


            if(sErrorMessage.length()>0){
                req.setAttribute("error",sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher("/whitelistBin.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            if (functions.isValueNull(startBin) && functions.isValueNull(endBin)) {
                listOfBin = whiteListDAO.getWhiteListBinPage(startBin, endBin,startCard,endCard,accountid, memberid, actionExecutorId,actionExecutorName,paginationVO);
            }
            else
            {
                listOfBin = whiteListDAO.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountid, memberid,actionExecutorId,actionExecutorName,paginationVO);
            }
            if ("upload".equalsIgnoreCase(action))
            {
                if(listOfBin.size()<=0)
                {
                    if(functions.isValueNull(startBin)&& functions.isValueNull(endBin))
                    {
                        whiteListDAO.addBin(startBin, endBin,startCard,endCard, accountid, memberid,actionExecutorId,actionExecutorName);
                        msg = WhiteListBin_uploaded_errormsg;
                    }
                }
                else{
                    msg= WhiteListBin_already_errormsg;
                }
                listOfBin = whiteListDAO.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountid, memberid,actionExecutorId,actionExecutorName,paginationVO);
            }
            String[] ids=req.getParameterValues("id");
            if("delete".equalsIgnoreCase(delete))
            {
                for(String id : ids)
                {
                    whiteListDAO.removeWhitelistBinEntry(id);
                }
                msg = WhiteListBin_deleted_errormsg;
                listOfBin = whiteListDAO.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountid, memberid,actionExecutorId,actionExecutorName,paginationVO);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.debug("Exception:::"+e);
        }

        StringBuilder chargeBackMessage=new StringBuilder();
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("error", errormsg);
        req.setAttribute("error",sErrorMessage.toString());
        req.setAttribute("msg",msg);
        req.setAttribute("merchantid",memberid);
        req.setAttribute("listOfBin",listOfBin);
        req.setAttribute("paginationVO",paginationVO);
        RequestDispatcher rd = req.getRequestDispatcher("/whitelistBin.jsp?ctoken="+user.getCSRFToken());
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
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}