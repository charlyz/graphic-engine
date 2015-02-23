package representation;

import geometry.Triangle;

/**
 * This class represents a ray.
 * @author Dessart Charles-Eric
 */
public class Ray 
{
	// base of the ray
	public Point3f base;
	// direction of the ray
	public Vector3f direction;
	
	/**
	 * Creates a new ray
	 * @param base The base point of the ray
	 * @param direction The direction of the ray defined by a vector
	 */
	public Ray(Point3f base, Vector3f direction) 
	{
		this.base = base;
		this.direction = direction;
	}
	
	/**
	 * Creates a new ray
	 * @param base The base point of the ray
	 * @param direction The direction of the ray defined by a point
	 */
	public Ray(Point3f base, Point3f direction) 
	{
		this.base = base;
		this.direction = new Vector3f(direction.x - base.x, 
									  direction.y - base.y,
									  direction.z - base.z);
	}
	
	public Point3f getBase() {return base;}
	public Vector3f getDirection() {return direction;}
	public String toString() {return "base: " + base + ", direction: " + direction;}
}
