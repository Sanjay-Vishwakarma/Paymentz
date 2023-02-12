import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.BlacklistManager;
import com.manager.vo.BlacklistVO;
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
 * Created by Mahima on 1/9/2016.
 */
public class BlockBin extends HttpServlet
{
    private static Logger log = new Logger(BlockBin.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String role_interface="merchant";
        Merchants merchants = new Merchants();
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String BlockBin_startbin_errormsg = StringUtils.isNotEmpty(rb1.getString("BlockBin_startbin_errormsg"))?rb1.getString("BlockBin_startbin_errormsg"): "Invalid Start Bin or Start Bin should not be empty";
        String BlockBin_endbin_errormsg = StringUtils.isNotEmpty(rb1.getString("BlockBin_endbin_errormsg"))?rb1.getString("BlockBin_endbin_errormsg"): "Invalid End Bin or End Bin should not be empty";
        String BlockBin_please_errormsg = StringUtils.isNotEmpty(rb1.getString("BlockBin_please_errormsg"))?rb1.getString("BlockBin_please_errormsg"): "Please provide both StartBin And EndBin ,else Click on Single Bin";
        String BlockBin_greater_errormsg = StringUtils.isNotEmpty(rb1.getString("BlockBin_greater_errormsg"))?rb1.getString("BlockBin_greater_errormsg"): "Start Bin greater than End Bin.";
        String BlockBin_unblocked_errormsg = StringUtils.isNotEmpty(rb1.getString("BlockBin_unblocked_errormsg"))?rb1.getString("BlockBin_unblocked_errormsg"): "Records Unblocked Successfully.";
        BlacklistManager blackListManger=new BlacklistManager();
        List<BlacklistVO> listOfBin = null;
        StringBuilder sErrorMessage=new StringBuilder();
        Functions functions=new Functions();
        PaginationVO paginationVO =new PaginationVO();

        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String memberid = (String) session.getAttribute("merchantid");
        String startBin=req.getParameter("startBin");
        String endBin=req.getParameter("endBin");
        String error="";
        String EOL="<br>";
        String unblock=req.getParameter("unblock");
        error = error + validateOptionalParameters(req);
        if (functions.isValueNull(error))
        {
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/blockBin.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"),1));
        paginationVO.setPage(BlockBin.class.getName());
        paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
        paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));
        paginationVO.setInputs("&startBin="+startBin+"&endBin="+endBin);

        try
        {
            if (!ESAPI.validator().isValidInput("startBin", startBin, "Numbers", 6, true))
            {
                log.debug("Invalid bin");
                sErrorMessage.append(BlockBin_startbin_errormsg + EOL);
            }
            if (!ESAPI.validator().isValidInput("endBin", endBin, "Numbers", 6, true))
            {
                log.debug("Invalid bin");
                sErrorMessage.append(BlockBin_endbin_errormsg + EOL);
            }

            if(functions.isValueNull(startBin)&& !functions.isValueNull(endBin)|| !functions.isValueNull(startBin) && functions.isValueNull(endBin)){
                sErrorMessage.append(BlockBin_please_errormsg);
                String redirectpage = "/blockBin.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }

            if (functions.isValueNull(startBin) && functions.isValueNull(endBin))
            {
                if (startBin.compareTo(endBin) > 0)
                {
                    sErrorMessage.append(BlockBin_greater_errormsg);
                }
            }

            if (sErrorMessage.length() > 0)
            {
                String redirectpage = "/blockBin.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }

            if (functions.isValueNull(startBin) && functions.isValueNull(endBin)) {
                listOfBin = blackListManger.getBlockedBinPage(startBin, endBin, "", memberid,"","",paginationVO,role_interface);
            }
            else
            {
                listOfBin = blackListManger.getBlockedBinPage(startBin, endBin, "", memberid,"","", paginationVO,role_interface);
            }
            String[] ids=req.getParameterValues("id");
            if("unblock".equalsIgnoreCase(unblock))
            {
                for(String id : ids)
                {
                    blackListManger.unblockBin(id);
                }
                error=BlockBin_unblocked_errormsg;
                listOfBin = blackListManger.getBlockedBinPage(startBin, endBin, "", memberid,"","", paginationVO,role_interface);
            }
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException In BlockBin :::::::",e);
        }
        StringBuilder chargeBackMessage=new StringBuilder();
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("error", error);
        req.setAttribute("merchantid",memberid);
        req.setAttribute("listOfBin",listOfBin);
        req.setAttribute("paginationVO",paginationVO);
        RequestDispatcher rd = req.getRequestDispatcher("/blockBin.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
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
