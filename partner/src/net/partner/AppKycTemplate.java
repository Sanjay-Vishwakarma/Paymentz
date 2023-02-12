package net.partner;

import com.dao.ApplicationManagerDAO;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.vo.applicationManagerVOs.AppKycTemplateVO;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by SurajT. on 7/31/2018.
 */
public class AppKycTemplate extends HttpServlet
{

    private static Logger logger = new Logger(AppKycTemplate.class.getName());
    static Connection conn = null;

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException {
        ApplicationManagerDAO applicationManagerDAO = new ApplicationManagerDAO();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner = new PartnerFunctions();
        Functions functions = new Functions();
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String AppKycTemplate_Template_Added_errormsg = StringUtils.isNotEmpty(rb1.getString("AppKycTemplate_Template_Added_errormsg")) ? rb1.getString("AppKycTemplate_Template_Added_errormsg") : "New Kyc Template Added Successfully";
        String AppKycTemplate_Template_updated_errormsg = StringUtils.isNotEmpty(rb1.getString("AppKycTemplate_Template_updated_errormsg")) ? rb1.getString("AppKycTemplate_Template_updated_errormsg") : "New Kyc Template Updated Successfully";

        if (!partner.isLoggedInPartner(session)) {
            logger.debug("partner is logout ");
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        String partnerid = ((String) session.getAttribute("merchantid"));

        applicationManagerDAO.deleteUploadFile(partnerid);

        String msg = "";
        String labelname = request.getParameter("memorandum_doc");
        String labelname1 = request.getParameter("incorporation_doc");
        String labelname2 = request.getParameter("share_doc");
        String labelname3 = request.getParameter("processhistory_doc");
        String labelname4 = request.getParameter("license_doc");
        String labelname5 = request.getParameter("bankstatement_doc");

        String labelname6 = request.getParameter("bankreference_doc");
        String labelname7 = request.getParameter("owneridentity_doc");
        String labelname8 = request.getParameter("addressproof_doc");
        String labelname9 = request.getParameter("crosscorporate_doc");

//is mandatory
        String ismandatory = request.getParameter("memorandum_criteria");
        String ismandatory1 = request.getParameter("incorporation_criteria");
        String ismandatory2 = request.getParameter("share_criteria");
        String ismandatory3 = request.getParameter("processhistory_criteria");
        String ismandatory4 = request.getParameter("license_criteria");
        String ismandatory5 = request.getParameter("bankstatement_criteria");

        String ismandatory6 = request.getParameter("bankreference_criteria");
        String ismandatory7 = request.getParameter("owneridentity_criteria");
        String ismandatory8 = request.getParameter("addressproof_criteria");
        String ismandatory9 = request.getParameter("crosscorporate_criteria");


        RequestDispatcher rd = request.getRequestDispatcher("/appKycTemplate.jsp?Success=YES&ctoken=" + user.getCSRFToken());
        List<AppKycTemplateVO> kycTemplateVOList = new LinkedList();
        if (functions.isValueNull(request.getParameter("memorandum_doc"))) {

            AppKycTemplateVO kycTemplateVO = new AppKycTemplateVO();
            kycTemplateVO.setLabelId("1");
            kycTemplateVO.setLabelName(labelname);
            kycTemplateVO.setAlternateName("Memorandum Article of Association");
            kycTemplateVO.setPartnerid(partnerid);
            kycTemplateVO.setCriteria(ismandatory);
            kycTemplateVO.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO);
        }
        if (functions.isValueNull(request.getParameter("incorporation_doc"))) {
            AppKycTemplateVO kycTemplateVO1 = new AppKycTemplateVO();
            kycTemplateVO1.setLabelId("2");
            kycTemplateVO1.setLabelName(labelname1);
            kycTemplateVO1.setAlternateName("Certificate of Incorporation");
            kycTemplateVO1.setPartnerid(partnerid);
            kycTemplateVO1.setCriteria(ismandatory1);
            kycTemplateVO1.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO1.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO1);
        }
        if (functions.isValueNull(request.getParameter("share_doc"))) {
            AppKycTemplateVO kycTemplateVO2 = new AppKycTemplateVO();
            kycTemplateVO2.setLabelId("3");
            kycTemplateVO2.setLabelName(labelname2);
            kycTemplateVO2.setAlternateName("Share Certificate");
            kycTemplateVO2.setPartnerid(partnerid);
            kycTemplateVO2.setCriteria(ismandatory2);
            kycTemplateVO2.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO2.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO2);
        }
        if (functions.isValueNull(request.getParameter("processhistory_doc"))) {
            AppKycTemplateVO kycTemplateVO3 = new AppKycTemplateVO();
            kycTemplateVO3.setLabelId("4");
            kycTemplateVO3.setLabelName(labelname3);
            kycTemplateVO3.setAlternateName("Processing history for last 6 months");
            kycTemplateVO3.setPartnerid(partnerid);
            kycTemplateVO3.setCriteria(ismandatory3);
            kycTemplateVO3.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO3.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO3);
        }
        if (functions.isValueNull(request.getParameter("license_doc"))) {
            AppKycTemplateVO kycTemplateVO4 = new AppKycTemplateVO();
            kycTemplateVO4.setLabelId("5");
            kycTemplateVO4.setLabelName(labelname4);
            kycTemplateVO4.setAlternateName("Business License/Commercial License");
            kycTemplateVO4.setPartnerid(partnerid);
            kycTemplateVO4.setCriteria(ismandatory4);
            kycTemplateVO4.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO4.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO4);
        }
        if (functions.isValueNull(request.getParameter("bankstatement_doc"))) {
            AppKycTemplateVO kycTemplateVO5 = new AppKycTemplateVO();
            kycTemplateVO5.setLabelId("6");
            kycTemplateVO5.setLabelName(labelname5);
            kycTemplateVO5.setAlternateName("Bank statement of company for last 3 months");
            kycTemplateVO5.setPartnerid(partnerid);
            kycTemplateVO5.setCriteria(ismandatory5);
            kycTemplateVO5.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO5.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO5);
        }
        if (functions.isValueNull(request.getParameter("bankreference_doc"))) {
            AppKycTemplateVO kycTemplateVO6 = new AppKycTemplateVO();
            kycTemplateVO6.setLabelId("7");
            kycTemplateVO6.setLabelName(labelname6);
            kycTemplateVO6.setAlternateName("Bank reference letter for company");
            kycTemplateVO6.setPartnerid(partnerid);
            kycTemplateVO6.setCriteria(ismandatory6);
            kycTemplateVO6.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO6.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO6);
        }
        if (functions.isValueNull(request.getParameter("owneridentity_doc"))) {
            AppKycTemplateVO kycTemplateVO7 = new AppKycTemplateVO();
            kycTemplateVO7.setLabelId("8");
            kycTemplateVO7.setLabelName(labelname7);
            kycTemplateVO7.setAlternateName("Proof of Identity of owner");
            kycTemplateVO7.setPartnerid(partnerid);
            kycTemplateVO7.setCriteria(ismandatory7);
            kycTemplateVO7.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO7.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO7);
        }
        if (functions.isValueNull(request.getParameter("addressproof_doc"))) {
            AppKycTemplateVO kycTemplateVO8 = new AppKycTemplateVO();
            kycTemplateVO8.setLabelId("9");
            kycTemplateVO8.setLabelName(labelname8);
            kycTemplateVO8.setAlternateName("Proof of address");
            kycTemplateVO8.setPartnerid(partnerid);
            kycTemplateVO8.setCriteria(ismandatory8);
            kycTemplateVO8.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO8.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO8);
        }
        if (functions.isValueNull(request.getParameter("crosscorporate_doc"))) {
            AppKycTemplateVO kycTemplateVO9 = new AppKycTemplateVO();
            kycTemplateVO9.setLabelId("10");
            kycTemplateVO9.setLabelName(labelname9);
            kycTemplateVO9.setAlternateName("Cross Corporate Guarantee");
            kycTemplateVO9.setPartnerid(partnerid);
            kycTemplateVO9.setCriteria(ismandatory9);
            kycTemplateVO9.setSupportedFileType("pdf,xlsx,png,jpg");
            kycTemplateVO9.setFunctionalUsage("Application Manager");
            kycTemplateVOList.add(kycTemplateVO9);
        }

        try {
            if (functions.isValueNull(request.getParameter("kyc")))
            {
                String status = applicationManagerDAO.addnewkyctemplate(kycTemplateVOList);
                if ("success".equalsIgnoreCase(status))
                {
                    msg =AppKycTemplate_Template_Added_errormsg ;
                }
                else
                {
                    msg= AppKycTemplate_Template_updated_errormsg;
                }
            }

            request.setAttribute("message", msg);
            rd.forward(request, response);
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in appKycTemplate.java------", dbe);
            PZExceptionHandler.handleDBCVEException(dbe, "", "");
            request.setAttribute("message", "Internal Error occurred : Please contact your Admin");
            rd.forward(request, response);
            return;
        }
    }
}



