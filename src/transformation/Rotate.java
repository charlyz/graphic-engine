package transformation;

import material.Material;
import geometry.Geometry;
import representation.Matrix4f;
import representation.Scene;
import representation.Texture;
import representation.Vector3f;
import representation.Vector4f;

/**
 * This class represents the rotation of a shape.
 * @author Dessart Charles-Eric
 *
 */
public class Rotate extends Transformation
{
	// translation vector
	public Vector3f vector;
	// angle
	public float angle;
	public Scene scene;
	
	/**
	 * Constructor
	 * @param vector
	 */
	public Rotate(Vector3f vector, float angle, Scene scene)
	{	
		this.vector = vector;
		this.angle = angle;
		this.scene = scene;
	}

	public Matrix4f Mt() 
	{
		if(Mt!=null)
			return Mt;
		
		double angle = Math.toRadians(this.angle);
		
		Matrix4f xAxis = new Matrix4f(1, 0, 0, 0,
									  0, (float)Math.cos(angle), (float)-Math.sin(angle), 0,
									  0, (float)Math.sin(angle), (float)Math.cos(angle), 0, 
									  0, 0, 0, 1);
		
		Matrix4f yAxis = new Matrix4f((float)Math.cos(angle), 0, (float)Math.sin(angle), 0,
									  0, 1, 0, 0,
									  (float)-Math.sin(angle), 0, (float)Math.cos(angle), 0, 
									  0, 0, 0, 1);
		
		Matrix4f zAxis = new Matrix4f((float)Math.cos(angle), (float)-Math.sin(angle), 0, 0,
									  (float)Math.sin(angle), (float)Math.cos(angle), 0, 0,
									  0, 0, 1, 0, 
									  0, 0, 0, 1);

		Mt = new Matrix4f(1, 0, 0, 0,
						  0, 1, 0, 0,
						  0, 0, 1, 0,
						  0, 0, 0, 1);
		
		if(vector.x == 1)
			Mt = Matrix4f.computeProduct(Mt, xAxis);
		
		if(vector.y == 1)
			Mt = Matrix4f.computeProduct(Mt, yAxis);
		
		if(vector.z == 1)
			Mt = Matrix4f.computeProduct(Mt, zAxis);
		
		
		return Mt;
	}
}