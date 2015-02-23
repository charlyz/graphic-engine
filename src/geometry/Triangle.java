package geometry;

import representation.Point3f;
import representation.Vector3f;

/**
 * This class reprensents a triangle
 * @author Dessart Charles-Eric
 */
public class Triangle 
{

	public Point3f a;
	public Point3f b;
	public Point3f c;
	
	/**
	 * Creates a new triangle
	 * @param a corner
	 * @param b corner
	 * @param c corner
	 */
	public Triangle(Point3f a, Point3f b, Point3f c) 
	{
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * Makes a new triangle with a corner a and two offsets
	 * @param a corner defined by a point
	 * @param b corner defined by a vector
	 * @param c corner defined by a vector
	 */
	public Triangle(Point3f a, Vector3f b, Vector3f c) 
	{
		this.a = a;
		this.b = new Point3f(b.x+a.x, b.y+a.y, b.z+a.z);
		this.c = new Point3f(c.x+a.x, c.y+a.y, c.z+a.z);
	}
	
	public Point3f getA() {return a;}
	public Point3f getB() {return b;}
	public Point3f getC() {return c;}
	
	public Vector3f getOffsetB() {return new Vector3f(b.x-a.x, b.y-a.y, b.z-a.z);}
	public Vector3f getOffsetC() {return new Vector3f(c.x-a.x, c.y-a.y, c.z-a.z);}

	public String toString() {return a + ", " + b + ", " + c;}

}
