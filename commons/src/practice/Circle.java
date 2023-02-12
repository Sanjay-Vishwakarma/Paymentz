package practice;

/**
 * Created by Admin on 2/25/2021.
 */
public class Circle implements Area
{
    public float compute(float a)
    {
        return (a*a*pi);
    }

    @Override
    public float compute(float x, float y)
    {
        return 0;
    }
}
