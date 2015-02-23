package geometry;

import representation.Point3f;
import representation.TexCoord2f;
import representation.Vector3f;

/**
 * This class represents a shape contained in
 * a OBJ file.
 * @author Dessart Charles-Eric
 *
 */
public class OBJModel extends Geometry
{

	/**
	 * Constructor
	 * @param size size of the shape
	 * @param name name of the shape
	 * @param model filename of the obj model without ".obj".
	 */
	public OBJModel(float size, String name, String model) 
	{
		this.size = size;
		this.name = name;
		this.model = model;
		loadModelFromOBJ(model); 
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
	public String model;
}
