import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.directi.pg.*;
import java.sql.*;

public class MerchantList extends HttpServlet
{

    static Logger logger=new Logger(MerchantList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
            doPost(request,response);
    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException
    {


        int start=0; // start index
        int end=0; // end index
        String str=null;
        String key=null;
        String checksum=null;
        String merchantid=null;

        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        String data=req.getParameter("data");


        String company=Functions.checkStringNull(req.getParameter("company"));
        String memberid=Functions.checkStringNull(req.getParameter("memberid"));

        //Functions fn=new Functions();

        int pageno=Functions.convertStringtoInt(req.getParameter("SPageno"),1);
        int records=Functions.convertStringtoInt(req.getParameter("SRecords"),100);

        // calculating start & end
        start= (pageno-1)* records;
        end =records;

    //	out.println("merchantid "+ merchantid);

        Hashtable hash=null;

        StringBuffer query=new StringBuffer("select company_name,contact_emails,memberid,activation,icici,aptprompt,reserves from members as M where memberid>0 ");
        StringBuffer countquery=new StringBuffer("select count(*) from members where memberid>0 ");

        if(company!=null)
        {
            query.append(" and company_name like '%"+company+"%' ");
            countquery.append(" and company_name like '%"+company+"%' ");
        }

        if(memberid!=null)
        {
            query.append(" and memberid="+memberid);
            countquery.append(" and memberid="+memberid);
        }

        query.append(" order by memberid asc LIMIT "+ start +","+ end);

        logger.info(query);
        logger.info(countquery);
    //	out.println("query "+ query);
    //	out.println("countquery "+ countquery);

        try
        {
            hash=Database.getHashFromResultSet(Database.executeQuery(query.toString(),Database.getConnection()));
        //	out.println(hash.toString());

            ResultSet rs=Database.executeQuery(countquery.toString(),Database.getConnection());
            rs.next();
            int totalrecords=rs.getInt(1);

            hash.put("totalrecords",""+totalrecords);
            hash.put("records","0");

            if(totalrecords>0)
            hash.put("records",""+(hash.size()-2));


            logger.info("hash " + hash);
            req.setAttribute("memberdetails",hash);

            RequestDispatcher rd=req.getRequestDispatcher("/memberlist.jsp");

            rd.forward(req,res);

        }
        catch(SystemError se)
        {
            logger.info("exception " + se);
            //System.out.println(Functions.ShowMessage("Error",se.toString()));
            StringWriter sw=new StringWriter();
            PrintWriter pw=new PrintWriter(sw);
            se.printStackTrace(pw);

            out.println(Functions.ShowMessage("stacktrace",sw.toString()));
            out.println(Functions.ShowMessage("Error",se.toString()));
            //System.out.println(se.toString());

        }
        catch(Exception e)
        {
            logger.info("exception " + e);
            //System.out.println(Functions.ShowMessage("Error",se.toString()));
            StringWriter sw=new StringWriter();
            PrintWriter pw=new PrintWriter(sw);
            e.printStackTrace(pw);

            out.println(Functions.ShowMessage("stacktrace",sw.toString()));
            out.println(Functions.ShowMessage("Error!",e.toString()));
        }
    }
}
