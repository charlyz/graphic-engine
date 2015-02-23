package representation;

import java.io.Serializable;

public class Vector3f extends Tuple3f implements Serializable
{
    /**
     * Constructs and initializes a Vector3f to (0,0,0).
     */

    public Vector3f()
    {
    }


    /**
     * Constructs and initializes a Vector3f from the specified xyz coordinates.
     * @param x
     * @param y
     * @param z
     */

    public Vector3f(float x, float y, float z)
    {
        super(x, y, z);
    }


    /**
     * Constructs and initializes a Vector3f from the specified Vector3f
     * @param v
     */

    public Vector3f(Vector3f v)
    {
        super(v);
    }


    /**
     * Constructs and initializes a Vector3f from the specified Tuple3f.
     * @param t
     */

    public Vector3f(Tuple3f t)
    {
        super(t);
    }


    /**
     * Constructs and initializes a Vector3f from the array of length 3.
     * @param v
     */

    public Vector3f(float v[])
    {
        super(v);
    }
    
    public void normalize()
    {
    	x = x / length();
    	y = y / length();
    	z = z / length();
    }
    
    public static Vector3f computeScalarProduct(Vector3f a, float scalar) 
    {
    	return new Vector3f(a.x*scalar,
    						a.y*scalar, 
    						a.z*scalar);
    }
    
    public static float computeDot(Vector3f a, Vector3f b) 
    {
    	return a.x*b.x + a.y*b.y + a.z*b.z;
    }
    
    public static Vector3f computeAddition(Vector3f a, Vector3f b) 
    {
    	return new Vector3f(a.x + b.x, 
    						a.y + b.y, 
    						a.z + b.z);
    }
    
    public static Vector3f computeMinus(Vector3f a, Vector3f b) 
    {
    	return new Vector3f(a.x - b.x, 
			    			a.y - b.y, 
			    			a.z - b.z);
    }
    
    public static Vector3f computeMultiply(Vector3f a, Vector3f b) 
    {
    	return new Vector3f(a.x * b.x, 
			    			a.y * b.y, 
			    			a.z * b.z);
    }
    
    public static Vector3f computeDivision(Vector3f a, Vector3f b) 
    {
    	return new Vector3f(a.x / b.x, 
			    			a.y / b.y, 
			    			a.z / b.z);
    }
    
    public static Vector3f computeAddition(Vector3f a, Vector3f b, Vector3f c) 
    {
    	Vector3f ab = computeAddition(a, b);
    	return computeAddition(ab, c);
    }
    
    public static Vector3f computeCrossProduct(Vector3f a, Vector3f b) 
    {
    	return new Vector3f(a.y*b.z - a.z*b.y,
    						a.z*b.x - a.x*b.z, 
    						a.x*b.y - a.y*b.x);
    }
    
    public Float length() 
    {
    	return new Float(Math.sqrt(x*x+y*y+z*z));
    }
    
}
