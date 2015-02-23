package transformation;

import material.Material;
import geometry.Geometry;
import representation.Matrix4f;
import representation.Texture;
import representation.Vector3f;
import representation.Vector4f;

/**
 * This class represents the scaling of a shape.
 * @author Dessart Charles-Eric
 *
 */
public class Scale extends Transformation
{
	// scaling vector
	public Vector3f scale;
	
	/**
	 * Constructor
	 * @param vector
	 */
	public Scale(Vector3f scale)
	{	
		this.scale = scale;
	}

	public Matrix4f Mt() 
	{
		if(Mt!=null)
			return Mt;
		
		Mt = new Matrix4f(scale.x, 0, 0, 0,
						  0, scale.y, 0, 0,
						  0, 0, scale.z, 0,
						  0, 0, 0, 1);
		return Mt;
	}
}