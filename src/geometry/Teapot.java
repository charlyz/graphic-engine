package geometry;

import representation.Point2f;
import representation.Point3f;
import representation.TexCoord2f;
import representation.Vector3f;

/**
 * This class represents a teapot
 * @author Dessart Charles-Eric
 *
 */
public class Teapot extends Geometry
{

	/**
	 * Constructor
	 * @param size size of the shape
	 * @param name name of the shape
	 */
	public Teapot(float size, String name) 
	{
		this.size = size;
		this.name = name;
		loadModelFromOBJ("teapot"); 
	}
	
	/* (non-Javadoc)
	 * @see geometry.Geometry#addVertex(java.lang.String[])
	 */
	protected void addVertex(String[] line)
	{
		float x = Float.parseFloat(line[1])*size;
		float y = Float.parseFloat(line[2])*size;
		float z = Float.parseFloat(line[3])*size;

		vertices.add(new Point3f(x, y, z));
	}

	public float size;
	public String name;
}
