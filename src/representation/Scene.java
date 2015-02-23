package representation;

import geometry.Geometry;

import java.util.ArrayList;
import java.util.HashMap;

import transformation.Shape;
import transformation.Transformation;

import light.Light;
import material.Material;

/**
 * This class represents a scene
 * @author Dessart Charles-Eric
 *
 */
public class Scene 
{
	// camera used to observe the scene
	public Camera activeCamera;
	// map of active lights
	public HashMap<String, Light> activeLights = new HashMap<String, Light>();
	// map of cameras of the scene
	public HashMap<String, Camera> cameras = new HashMap<String, Camera>();
	// map of lights of the scene
	public HashMap<String, Light> lights = new HashMap<String, Light>();
	// map of textures of materials
	public HashMap<String, Texture> textures = new HashMap<String, Texture>();
	// list of shapes
	public ArrayList<Shape> shapes = new ArrayList<Shape>();
	// map of materials of the scene
	public HashMap<String, Material> materials = new HashMap<String, Material>();
	// list of shapes of the scene. A shape is a relation object
	// between a geometric shape and a texture and/or a material
	public SceneGraph sceneGraph = new SceneGraph();
	// width of the scene
	public Integer Nx = 512; 
	// height of the scene
	public Integer Ny = 512; 
	// u v w vectors
	public Vector3f u, v, w;
	// background color of the scene
	public Color3f background;
	// ambient color
	public Color3f ambientColor;
	// root of the BSPTree
	public BSPTree rootBSPTree;
	public Boolean antialiasing;
	
	
	
	public Scene() 
	{
		sceneGraph.Mi = new Matrix4f(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}



	/**
	 * Computes UVW vectors
	 */
	public void computeUVWBasisVectors()
	{
		// formulas p164 2nd
		Camera cam = activeCamera;
		
		// gaze
		Vector3f g = new Vector3f (cam.direction.x /*- cam.position.x*/, 
								   cam.direction.y /*- cam.position.y*/, 
								   cam.direction.z /*- cam.position.z*/);
		
		// w
		w = new Vector3f(-(g.x/g.length()),
						 -(g.y/g.length()),
						 -(g.z/g.length()));
		
		// u
		Vector3f TxW = Vector3f.computeCrossProduct(cam.up, w);
		u = new Vector3f(TxW.x / TxW.length(), 
					     TxW.y / TxW.length(),
					     TxW.z / TxW.length());
		
		// v
		v = new Vector3f(Vector3f.computeCrossProduct(w, u));
	}
}

