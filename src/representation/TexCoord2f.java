package representation;

import java.io.Serializable;

public class TexCoord2f extends Tuple2f implements Serializable
{

    /**
     * Constructs and initializes a TexCoord2f to (0,0).
     */

    public TexCoord2f()
    {
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified xy coordinates. 
     * @param x
     * @param y
     */

    public TexCoord2f(float x, float y)
    {
        super(x, y);
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified array.
     * @param t
     */

    public TexCoord2f(float t[])
    {
        super(t);
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified TexCoord2f.
     * @param t
     */

    public TexCoord2f(TexCoord2f t)
    {
        super(t);
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified Tuple2f.
     * @param t
     */

    public TexCoord2f(Tuple2f t)
    {
        super(t);
    }
}
