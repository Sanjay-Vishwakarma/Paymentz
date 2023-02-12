package practice;

/**
 * Created by Admin on 9/1/2020.
 */
public class Constructor_Eg
{
    String name;
    int price;
    String engine;

    public Constructor_Eg(String name,int price,String engine)
    {
        this.name=name;
        this.price=price;
        this.engine=engine;
        System.out.println(name+" "+price+" "+engine);
    }
    public static void main(String[] args)
    {
        Constructor_Eg obj = new Constructor_Eg("Audi",10000000,"Automatic");
        Constructor_Eg obj1 = new Constructor_Eg("RollsRoyce",1000000000,"Automatic");
        Constructor_Eg obj2 = new Constructor_Eg("BMW",1000000,"Manual");

    }
}

//example 1
/*    String name;
    int age;

    public Constructor_Eg()
    {
        System.out.println("Default constructor");
    }
    public Constructor_Eg(int i)
    {
        System.out.println("One parameter constructor");
        System.out.println(i);
    }
    public Constructor_Eg(int i,int j)
    {
        System.out.println("Two parameter constructor");
        System.out.println(i+ " "+j);

    }
    public Constructor_Eg(String name,int age)
    {
        this.name=name;
        this.age=age;
    }
    public static void main(String[] args)
    {
        Constructor_Eg obj = new Constructor_Eg();
        Constructor_Eg obj1 = new Constructor_Eg(8);
        Constructor_Eg obj2 = new Constructor_Eg(1,90);
        Constructor_Eg obj3= new Constructor_Eg("bunty",25);
        System.out.println(obj3.name+" "+obj3.age);

    }*/

