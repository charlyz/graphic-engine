package geometry;

import representation.Point2f;
import representation.Point3f;
import representation.TexCoord2f;
import representation.Vector3f;

/**
 * 
 * @author Dessart Charles-Eric
 * This class represents a cone.
 * Vertical axis is Y
 */
public class Cone extends Geometry
{
	// index of the vertex which has to be deleted to 
	// have a cone not capped.
	int indexMiddlePointInf = 0;
	

	/**
	 * Constructor 
	 * 
	 * @param radius radius of the cone
	 * @param height height of the cone on Y axis
	 * @param capped boolean to know if the cone has a bottom face
	 * @param name name of the shape
	 */
	public Cone(Float radius, Float height, Boolean capped, String name) 
	{
		this.radius = radius;
		this.height = height;
		this.capped = capped;
		this.name = name;
		loadModelFromOBJ("cone"); 
	}
	
	
	/* (non-Javadoc)
	 * @see geometry.Geometry#addVertex(java.lang.String[])
	 */
	protected void addVertex(String[] line)
	{
		float x = Float.parseFloat(line[1]);
		float y = Float.parseFloat(line[2]);
		float z = Float.parseFloat(line[3]);

		if(x==0 && y==-1 && z==0)
			indexMiddlePointInf = vertices.size();
		
		vertices.add(new Point3f(x*radius, y*height, z*radius));
	}
	
	/* (non-Javadoc)
	 * @see geometry.Geometry#addFace(java.lang.String[])
	 */
	protected void addFace(String[] line)
	{
		Integer[][] res = new Integer[3][3];
		res[0] = computeFace(line[1]);
		res[1] = computeFace(line[2]);
		res[2] = computeFace(line[3]);
		
		if(capped || (res[0][0]!=indexMiddlePointInf && res[1][0]!=indexMiddlePointInf && res[2][0]!=indexMiddlePointInf))
			faces.add(res);
	}
	
	public Float radius;
	public Float height;
	public Boolean capped;
	public String name;
}