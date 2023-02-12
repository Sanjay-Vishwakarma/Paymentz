package acqra;
import java.sql.*;

public class JdbcDaoDemoSelect
{
    public static void main(String[] args) throws SQLException, ClassNotFoundException
    {
        PersonDAO personDAO=new PersonDAO();
        Person p = null;
        p = personDAO.getPerson(8);
        System.out.println(p.name);
    }
}
class PersonDAO
{
  public Person getPerson(int id) throws ClassNotFoundException, SQLException
  {
      String query ="select name from mydata where id="+id;
      Person p = new Person();
      p.id=id;
      Class.forName("com.mysql.jdbc.Driver");
      Connection conn= DriverManager.getConnection("jdbc:mysql://localhost:3306/sagar","root","root");
      Statement st = null;
      st = conn.createStatement();
      ResultSet resultSet = st.executeQuery(query);
      resultSet.next();
      String name = resultSet.getString(1);
      p.name=name;
      return p;
  }
}
class Person
{
    int id;
    String name;
}
