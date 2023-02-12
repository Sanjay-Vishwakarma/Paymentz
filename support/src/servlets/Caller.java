import com.directi.pg.CustomerSupport;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.payment.validators.InputFields;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class Caller extends HttpServlet
{
    static Logger log= new Logger("logger1");
    static final String classname= Caller.class.getName();
    //    static String fname="",lname="",email="",phoneno="",desc="",remark="",status="",pod="",podbatch="";
//    static String trackingid=null;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap Details;
        CustomerSupport cs=new CustomerSupport();
        Hashtable query;
        PrintWriter out =response.getWriter();
        boolean b=false;
        HttpSession session= Functions.getNewSession(request);
        if(!CustomerSupport.isLoggedIn(session))
        {
            log.debug("inside isLoggedIn");
            response.sendRedirect("/support/logout.jsp");
            return;
        }
        User user=(User)session.getAttribute("ESAPIUserSessionKey");
        HashMap map =(HashMap)session.getAttribute("transactionDetails");
        String tablename= (String) session.getAttribute("tableName");
        RequestDispatcher rd;
        String fname=null;
        String lname=null;
        String email=null;
        String phoneno=null;
        String desc=null;
        String remark=null;
        String status=null;
        String pod=null;
        String podbatch=null;
        String trackingid=null;
        Hashtable errorM=null,errorO=null;
        List<InputFields> inputFieldsListMandatory =new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.EMAIL);


        errorM= CustomerSupport.validateMandatoryParameters(request, inputFieldsListMandatory);
        List<InputFields> inputFieldsListOptional =new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PHONENO);
        inputFieldsListOptional.add(InputFields.STRACKINGID);
        inputFieldsListOptional.add(InputFields.DESC);
        inputFieldsListOptional.add(InputFields.REMARKS);
        inputFieldsListOptional.add(InputFields.STATUS);
        inputFieldsListOptional.add(InputFields.SHIPPINGPOD);
        inputFieldsListOptional.add(InputFields.SHIPPINGPODBATCH);
        errorO= CustomerSupport.validateOptionalParameters(request, inputFieldsListOptional);

        try
        {
            if(!errorM.isEmpty() || !errorO.isEmpty())
            {
                log.debug(classname+" inside error condition::");
                request.setAttribute("errorM",errorM);
                request.setAttribute("errorO",errorO);
                if("newCaller".equals(request.getParameter("page")))
                {
                    rd=request.getRequestDispatcher("/newCaller.jsp?MES=F&ctoken="+user.getCSRFToken());
                    rd.forward(request,response);
                    return;
                }
                rd=request.getRequestDispatcher("/actionHistory.jsp?MES=F&ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                return;
            }
            query=new Hashtable();
            fname=request.getParameter("firstname");
            query.put("firstname",fname);
            lname=request.getParameter("lastname");
            query.put("lastname",lname);
            query.put("name",fname+" "+lname);
            email=request.getParameter("email");
            query.put("emailaddr",email);
            phoneno=request.getParameter("phoneno");
            trackingid=request.getParameter("STrakingid");

            desc=request.getParameter("desc");
            remark=request.getParameter("remark");
            status=request.getParameter("status");
            pod=request.getParameter("Shippingid");
            podbatch=request.getParameter("Shippingbno");
            if(trackingid.equals(""))
                trackingid=null;
             else
            query.put("trackingid",trackingid);



            if(phoneno.equals(""))
                phoneno=null;
            else
              query.put("phoneno",phoneno);
            if(desc.equals(""))
                desc=null;
            else
            query.put("description",desc);
            if(remark.equals(""))
                remark=null;
            else
            query.put("remark",remark);
            if(status.equals(""))
                status=null;
            else
            query.put("status",status);
            if(pod.equals(""))
                pod=null;
            else
            query.put("pod",pod);
            if(podbatch.equals(""))
                podbatch=null;
            else
            query.put("podbatch",podbatch);
            if(pod!=null && podbatch==null)
            {
                log.debug(classname+" enter podbatch to the corresponding pod::"+pod+"  specified for a caller name::"+fname+lname);
                request.setAttribute("nopodbatch","yes");
                log.debug(classname+" redirecting to action history");
                rd=request.getRequestDispatcher("/actionHistory.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
            }else{
                if(pod==null && podbatch!=null)
                {
                    log.debug(classname+" enter pod to the corresponding podbatch::"+podbatch+"  specified for a caller name::"+fname+lname);
                    request.setAttribute("nopodbatch","no");
                    if("newCaller".equals(request.getParameter("page")))
                    {
                        log.debug(classname+" redirecting to new Caller");
                        rd=request.getRequestDispatcher("/newCaller.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);
                    }
                    else{
                        log.debug(classname+" redirecting to action history");
                        rd=request.getRequestDispatcher("/actionHistory.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);
                    }
                }
                else
                {
                    try {
                        log.debug(classname+" caller info update:: firstname::"+fname+" LastName::"+lname+" email::"+email+" Phoneno::"+phoneno+" description::"+desc+" remarks::"+remark+" Status::"+status+" TrackingId"+trackingid);
                        b  =cs.updatecaller(query);
                        if(b)
                        {
                            log.debug(classname+" UPDATED CALLER INFORMATION SUCCESSFULLY");
                            request.setAttribute("update","true");
                        } else
                        {
                            request.setAttribute("update","false");
                        }


                    } catch (SQLException e) {
                        log.error(classname+"SQL EXCEPTION CALLER:::",e);
                        //To change body of catch statement use File | Settings | File Templates.

                    }

                    log.debug(classname+" forwarding to action history");
                    if("newCaller".equals(request.getParameter("page")))
                    {
                        rd=request.getRequestDispatcher("/newCaller.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);
                    }
                    else{
                        rd=request.getRequestDispatcher("/actionHistory.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);
                    }
                }
            }
        }catch (Exception e)
        {
            log.error(classname+" main class Exception::",e);

            request.setAttribute("update","false");
            if("newCaller".equals(request.getParameter("page")))
            {
                rd=request.getRequestDispatcher("/newCaller.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
            }
            else{
                rd=request.getRequestDispatcher("/actionHistory.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
            }

        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
