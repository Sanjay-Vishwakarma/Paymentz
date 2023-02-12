package practice;

/**
 * Created by Admin on 2/25/2021.
 */
public class interface_eg
{
    public static void main(String[] args)
    {
        Rectangle rectangle=new Rectangle();
        Circle circle =new Circle();
        System.out.println("Rectangle--"+rectangle.compute(10,20));
        System.out.println("Circle--"+circle.compute(2));
    }
}
