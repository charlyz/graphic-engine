package geometry;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import representation.Point2f;
import representation.Point3f;
import representation.TexCoord2f;
import representation.Vector3f;

/**
 * @author Dessart Charles-Eric
 *
 */
public class Geometry
{
	// list of vertices of the shape
	public ArrayList<Point3f> vertices = new ArrayList<Point3f>();
	// list of textures of vertices
	public ArrayList<TexCoord2f> textures = new ArrayList<TexCoord2f>();
	// list of normal vectors of vertices
	public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	// list of normal vectors of faces
	public ArrayList<Vector3f> faceNormals = new ArrayList<Vector3f>();
	// list of triangle objects representing the index relation list "faces"
	public ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	// index relation list between vertices, textures and normal vectors of vertices
	public ArrayList<Integer[][]> faces = new ArrayList<Integer[][]>();
	// 
	public float maxU;
	public float maxV;
	
	public Geometry() 
	{
	}

	public Geometry(ArrayList<Point3f> vertices,
			ArrayList<TexCoord2f> textures, ArrayList<Vector3f> normals,
			ArrayList<Integer[][]> faces) 
	{
		this.vertices = vertices;
		this.textures = textures;
		this.normals = normals;
		this.faces = faces;
		computeTriangles();
	}

	/**
	 * Loads a OBJ model.
	 * @param name filename of the obj model to load without the ".obj"
	 */
	protected void loadModelFromOBJ(String name)
	{
		try 
		{
			FileReader fr = new FileReader("models/" + name + ".obj");
			
			BufferedReader br = new BufferedReader(fr); 
			String line;
	        
	        while ((line = br.readLine())!=null) 
	        {
	        	String[] splitLine = line.split(" ");
	        	
	        	if(splitLine[0].equals("v"))
	        		addVertex(splitLine);
	        	else if(splitLine[0].equals("vt"))
	        		addTexture(splitLine);
	        	else if(splitLine[0].equals("vn"))
	        		addNormal(splitLine);
	        	else if(splitLine[0].equals("f"))
	        		addFace(splitLine);
	        }	
	        
	        computeTriangles();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses a line beginning by a "v" in the obj model.
	 * A line contains three numbers representing x y z coordinates
	 * @param line 
	 */
	protected void addVertex(String[] line)
	{
		float x = Float.parseFloat(line[1]);
		float y = Float.parseFloat(line[2]);
		float z = Float.parseFloat(line[3]);

		vertices.add(new Point3f(x, y, z));
	}
	
	/**
	 * Parses a line beginning by a "vt" in the obj model.
	 * A line contains two numbers representing x y textures
	 * @param line 
	 */
	protected void addTexture(String[] line)
	{
		float u = Float.parseFloat(line[1]);
		float v = Float.parseFloat(line[2]);
		textures.add(new TexCoord2f(u, v));
		
		if(u>maxU) maxU = u;
		if(v>maxV) maxV = v;
	}
	
	/**
	 * Parses a line beginning by a "vn" in the obj model.
	 * A line contains three numbers representing x y z vector coordinates
	 * @param line 
	 */
	protected void addNormal(String[] line)
	{
		normals.add(new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3])));
	}
	
	/**
	 * Parses a line beginning by a "f" in the obj model.
	 * A line contains three triplets.
	 * Each triplet x/y/z is composed by "x" the index of a vertice,
	 * "y" the index of a texture and "z" the index of a normal vector
	 * of a vertice.
	 * "y" and "z" are not mandatory.
	 * @param line 
	 */
	protected void addFace(String[] line)
	{
		Integer[][] res = new Integer[3][3];
		res[0] = computeFace(line[1]);
		res[1] = computeFace(line[2]);
		res[2] = computeFace(line[3]);

		faces.add(res);
	}
	
	
	/**
	 * Parses a triplet x/y/z.
	 * A triplet x/y/z is composed by "x" the index of a vertex,
	 * "y" the index of a texture and "z" the index of a normal vector
	 * of a vertex.
	 * "y" and "z" are not mandatory.
	 * @param a the triplet
	 * @return
	 */
	protected Integer[] computeFace(String a)
	{
		String[] splitFace = a.split("/");
		Integer[] res = new Integer[3];
		
		res[0] = Integer.parseInt(splitFace[0]) - 1;
		switch(splitFace.length)
		{
		case 1:
			res[1] = -1;
			res[2] = -1;
			break;
		case 3:
			if(splitFace[1].equals("")) res[1] = -1;
			else res[1] = Integer.parseInt(splitFace[1]) - 1;
			
			res[2] = Integer.parseInt(splitFace[2]) - 1;
			break;
		}
		return res;
	}
	
	/**
	 * Computes triangle objects representing the index relation lis
	 * "faces".
	 */
	public void computeTriangles()
	{
		triangles.clear();
		faceNormals.clear();
		
		// compute triangle objects
		for(Integer[][] f : faces)
		{
			Triangle triangle = new Triangle( vertices.get(f[0][0]), 
											  vertices.get(f[1][0]), 
											  vertices.get(f[2][0]));
			triangles.add(triangle);
			
			// compute normal vectors of the triangle
			if(normals.isEmpty())
			{
				Vector3f vecT1 = new Vector3f(triangle.b.x-triangle.a.x, triangle.b.y-triangle.a.y, triangle.b.z-triangle.a.z);
				Vector3f vecT2 = new Vector3f(triangle.c.x-triangle.a.x, triangle.c.y-triangle.a.y, triangle.c.z-triangle.a.z);
				Vector3f n = Vector3f.computeCrossProduct(vecT1, vecT2);
				n.normalize();
				faceNormals.add(n);
				
			}else
			{
				Vector3f n = Vector3f.computeAddition(normals.get(f[0][2]), normals.get(f[1][2]), normals.get(f[2][2]));
				n.normalize();
				faceNormals.add(n);
			}
		}

	}
	
	public String toString()
	{
		String res = new String(); 
		for(Point3f p : vertices)
			res += "v " + p + "\n";
		
		for(TexCoord2f p : textures)
			res += "vt " + p + "\n";
		
		for(Vector3f p : normals)
			res += "vn " + p + "\n";
		
		for(Integer[][] a : faces)
		{	
			res += "f ";
			for(Integer[] f : a)
			{
				res += f[0] + "/" + f[1] + "/" + f[2] + " ";
			}
			res += "\n";
		}

		return res;
	}
}