package geometry;

import representation.Point3f;
import representation.TexCoord2f;
import representation.Vector3f;

/**
 * @author Dessart Charles-Eric
 * This class reprensents a set of indexed triangles
 * 
 */
public class IndexedTriangles extends Geometry
{

	/**
	 * Constructor
	 * @param name the name of the shape
	 */
	public IndexedTriangles(String name) 
	{
		this.name = name;
	}
	
	public String name;
}
