package acqra;

import java.sql.*;
public class JdbcDaoDemoInsert
{
    public static void main(String[] args) throws SQLException, ClassNotFoundException
    {
        PersonDAO1 personDAO1 = new PersonDAO1();
        Person1 p = new Person1();
        p.id=15;
        p.name="Ajay";
        p.address="Hongkong";
        personDAO1.addPerson(p);
    }
}
class PersonDAO1
{
    public void addPerson(Person1 p) throws ClassNotFoundException, SQLException
    {
        String query = "insert into mydata values(?,?,?)";
        Person1 person1 = new Person1();
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sagar", "root", "root");
        PreparedStatement st = null;
        st = conn.prepareStatement(query);
        st.setInt(1,p.id);
        st.setString(2, p.name);
        st.setString(3,p.address);
        int count=st.executeUpdate();
        System.out.println(count + " row affected");
    }
}
class Person1
{
    int id;
    String name;
    String address;
}