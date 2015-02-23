package representation;

import java.io.Serializable;

public class Point3f extends Tuple3f implements Serializable
{
    /**
     * Constructs and initializes a Point3f to (0,0,0).
     */

    public Point3f()
    {
    }


    /**
     * Constructs and initializes a Point3f from the specified xyz coordinates.
     * @param x
     * @param y
     * @param z
     */
    public Point3f(float x, float y, float z)
    {
        super(x, y, z);
    }
    
    public static double distance(Point3f a, Point3f b)
    {
    	return Math.sqrt(Math.pow((a.x-b.x), 2)+Math.pow((a.y-b.y), 2)+Math.pow((a.z-b.z), 2));
    }


    /**
     * Constructs and initializes a Point3f from the specified Point3f.
     * @param p
     */

    public Point3f(Point3f p)
    {
        super(p);
    }


    /**
     * Constructs and initializes a Point3f from the specified Tuple3f.
     * @param t
     */

    public Point3f(Tuple3f t)
    {
        super(t);
    }


    /**
     * Constructs and initializes a Point3f from the array of length 3.
     * @param p
     */

    public Point3f(float p[])
    {
        super(p);
    }


}
