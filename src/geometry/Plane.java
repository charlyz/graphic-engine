package geometry;

import representation.Point3f;
import representation.Vector3f;

/**
 * This class represent a plane.
 * @author Dessart Charles-Eric
 *
 */
public class Plane 
{
	// normal vector of the plane
	public Vector3f normal;
	// a point of the plane
	public Point3f p;
	// D
	public float D;
	
	/**
	 * Constructor
	 * @param normal vector of the plane
	 * @param p a point on the plane
	 */
	public Plane(Vector3f normal, Point3f p) 
	{
		this.normal = normal;
		this.p = p;
		this.D = -(Vector3f.computeDot(normal, new Vector3f(p)));
	}
	
	public String toString()
	{
		return "p: " + p + " - normal: " + normal;
	}
	
}
