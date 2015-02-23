package representation;

import java.io.Serializable;

public class Vector4f extends Tuple4f implements Serializable
{
    /**
     * Constructs and initializes a Vector4f to (0,0,0,0).
     */

    public Vector4f()
    {
    }


    /**
     * Constructs and initializes a Vector4f from the specified xyzw coordinates.
     * @param x
     * @param y
     * @param z
     * @param w
     */

    public Vector4f(float x, float y, float z, float w)
    {
        super(x, y, z, w);
    }


    /**
     * Constructs and initializes a Vector4f from the array of length 4.
     * @param v
     */

    public Vector4f(float v[])
    {
        super(v);
    }


    /**
     * Constructs and initializes a Vector4f from the specified Vector4f.
     * @param v
     */

    public Vector4f(Vector4f v)
    {
        super(v);
    }


    /**
     *  Constructs and initializes a Vector4f from the specified Tuple4f.
     * @param t
     */

    public Vector4f(Tuple4f t)
    {
        super(t);
    }


    /**
     * Constructs and initializes a Vector4f from the specified Tuple3f.
     * @param t
     */

    public Vector4f(Tuple3f t)
    {
        super(t.x, t.y, t.z, 0.0F);
    }


    /**
     * Sets the x,y,z components of this vector to the corresponding components of tuple t1.
     * @param t
     */

    public final void set(Tuple3f t)
    {
        x = t.x;
        y = t.y;
        z = t.z;
        w = 0.0F;
    }

}
