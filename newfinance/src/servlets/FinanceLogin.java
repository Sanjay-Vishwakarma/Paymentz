import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.directi.pg.*;
import java.sql.*;

public class FinanceLogin extends HttpServlet
{
	static Logger log = new Logger(FinanceLogin.class.getName());
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request,response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {

		String username=request.getParameter("username");
		String password=request.getParameter("password");

		String redirectpage=null;
		
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
				
		HttpSession session=request.getSession();

		try
		{
			//Member member=Merchants.authenticate(username,password);

//			redirectpage="MerchantList";
//			session.setAttribute("login","true");

			ResultSet result = FinanceDatabase.executeQuery("select * from company_users where cu_login='" + username + "' and cu_password='"+ password  +"' and  cu_user_type ='FIN'",FinanceDatabase.getConnection());
			
			if(result.next())
			{
				session.setAttribute("login","true");
				redirectpage="MerchantList";

            }
			else
			{
				redirectpage="/index.jsp?action=F";

			}


			log.info(redirectpage);

			RequestDispatcher rd=request.getRequestDispatcher(redirectpage);
			rd.forward(request,response);

		}
		/*
		catch(SystemError se)
		{
			out.println(Functions.ShowMessage("Error",se.toString()));	
			//System.out.println(se.toString());
			
		}
		*/
		catch(Exception e)
		{
			out.println(Functions.ShowMessage("Error!",e.toString()));	
		}

	}
}
