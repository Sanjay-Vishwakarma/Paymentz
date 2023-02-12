package net.partner;

import com.directi.pg.Logger;
import com.manager.MerchantModuleManager;
import com.manager.vo.MerchantModulesMappingVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by admin on 4/25/2016.
 */
public class AllocationUser extends HttpServlet
{
    private static Logger logger=new Logger(AllocationUser.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();

        if (!partner.isLoggedInPartner(session))
        {   logger.debug("member is logout ");
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String msg="";
        String userId = request.getParameter("userid");
        String merchantId=request.getParameter("memberid");
        String[] masterModuleId = request.getParameterValues("moduleid");
        String[] subModuleId=request.getParameterValues("submoduleid");

        RequestDispatcher rd=request.getRequestDispatcher("/allocationUser.jsp?ctoken="+user.getCSRFToken());

        try{
            MerchantModuleManager merchantModuleManager = new MerchantModuleManager();
            if (masterModuleId != null || subModuleId!=null){
                merchantModuleManager.isMappingRemoved(userId);
                merchantModuleManager.isSubMappingRemoved(userId);
                if(masterModuleId!=null){
                    for (String moduleId1 : masterModuleId){
                        MerchantModulesMappingVO merchantModulesMappingVO = new MerchantModulesMappingVO();
                        merchantModulesMappingVO.setMerchantId(merchantId);
                        merchantModulesMappingVO.setModuleId(moduleId1);
                        merchantModulesMappingVO.setUserId(userId);
                        String status = merchantModuleManager.addNewModuleMapping(merchantModulesMappingVO);
                        if ("success".equalsIgnoreCase(status)){
                            msg = "Module Mapping Updated";
                        }
                        else{
                            msg = "Module Mapping Failed";
                        }
                    }
                }
                if(subModuleId!=null){
                    for (String moduleId2 : subModuleId){
                        MerchantModulesMappingVO merchantModulesMappingVO = new MerchantModulesMappingVO();
                        merchantModulesMappingVO.setMerchantId(merchantId);
                        merchantModulesMappingVO.setSubModuleId(moduleId2);
                        merchantModulesMappingVO.setUserId(userId);
                        String status = merchantModuleManager.addNewSubModuleMapping(merchantModulesMappingVO);
                        if ("success".equalsIgnoreCase(status)){
                            msg = "Module Mapping updated";
                        }
                        else{
                            msg = "Module Mapping Failed";
                        }
                    }
                }
            }
            else {
                merchantModuleManager.isMappingRemoved(userId);
                merchantModuleManager.isSubMappingRemoved(userId);
                msg="Module mapping updated.";
            }
        }
        catch(PZDBViolationException dbe){
            logger.error("PZDBViolationException:::::",dbe);
            msg="Internal error occurred,please contact tech support team";
            request.setAttribute("error", msg);
            rd.forward(request,response);
            return;
        }
        if("Module Mapping Failed".equals(msg)){
            request.setAttribute("message", msg);
        }else{
            request.setAttribute("success", msg);
        }
        rd.forward(request, response);
    }
}
