package geometry;

import representation.Point2f;
import representation.Point3f;
import representation.TexCoord2f;
import representation.Vector3f;

/**
 * This class represents 
 * @author Dessart Charles-Eric
 *
 */
public class Torus extends Geometry
{

	/**
	 * Constructor
	 * @param innerRadius radius of "the tube"
	 * @param outerRadius radius of circle formed by "the tube"
	 * @param name name of the shape
	 */
	public Torus(float innerRadius, float outerRadius, String name) 
	{
		this.innerRadius = innerRadius;
		this.outerRadius = outerRadius;
		this.name = name;
		loadModelFromOBJ("torus"); 
	}
	
	/* (non-Javadoc)
	 * @see geometry.Geometry#addVertex(java.lang.String[])
	 */
	protected void addVertex(String[] line)
	{
		float x = Float.parseFloat(line[1]);
		float y = Float.parseFloat(line[2]);
		float z = Float.parseFloat(line[3]);
		
		vertices.add(new Point3f(x*outerRadius, y*innerRadius, z*outerRadius));
	}
	
	/* (non-Javadoc)
	 * @see geometry.Geometry#addTexture(java.lang.String[])
	 */
	protected void addTexture(String[] line)
	{
		textures.add(new TexCoord2f(Float.parseFloat(line[1]), Float.parseFloat(line[2])));
	}
	
	/* (non-Javadoc)
	 * @see geometry.Geometry#addNormal(java.lang.String[])
	 */
	protected void addNormal(String[] line)
	{
		normals.add(new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3])));
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
		faces.add(res);
	}
	
	public float innerRadius;
	public float outerRadius;
	public String name;
}