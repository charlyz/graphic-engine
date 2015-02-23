package geometry;

import representation.Point2f;
import representation.Point3f;
import representation.TexCoord2f;
import representation.Vector3f;

/**
 * This class represents a sphere
 * @author Dessart Charles-Eric
 *
 */
public class Sphere extends Geometry
{

	/**
	 * Constructor
	 * @param radius radius of the sphere
	 * @param name name of the shape
	 */
	public Sphere(float radius, String name) 
	{
		this.radius = radius;
		this.name = name;
		loadModelFromOBJ("sphere"); 
	}
	
	/* (non-Javadoc)
	 * @see geometry.Geometry#addVertex(java.lang.String[])
	 */
	protected void addVertex(String[] line)
	{
		float x = Float.parseFloat(line[1])*radius;
		float y = Float.parseFloat(line[2])*radius;
		float z = Float.parseFloat(line[3])*radius;

		vertices.add(new Point3f(x, y, z));
	}
	
	public float radius;
	public String name;
}
