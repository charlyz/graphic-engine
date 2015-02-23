package transformation;

import material.Material;
import geometry.Geometry;
import representation.Matrix4f;
import representation.Texture;
import representation.Vector3f;
import representation.Vector4f;

/**
 * This class represents the translation of a shape.
 * @author Dessart Charles-Eric
 *
 */
public class Translate extends Transformation
{
	// translation vector
	public Vector3f vector;
	
	/**
	 * Constructor
	 * @param vector
	 */
	public Translate(Vector3f vector)
	{	
		this.vector = vector;
	}

	public Matrix4f Mt() 
	{
		if(Mt!=null)
			return Mt;
		
		Mt = new Matrix4f(1, 0, 0, vector.x,
						  0, 1, 0, vector.y,
						  0, 0, 1, vector.z,
						  0, 0, 0, 1);
		return Mt;
	}
}