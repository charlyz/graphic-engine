package parser;

import geometry.Cone;
import geometry.Cylinder;
import geometry.Geometry;
import geometry.IndexedTriangles;
import geometry.OBJModel;
import geometry.Sphere;
import geometry.Teapot;
import geometry.Torus;
import geometry.Triangle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import light.Light;
import light.PointLight;
import material.DiffuseMaterial;
import material.Material;
import material.PhongMaterial;

import org.xml.sax.InputSource;

import representation.BSPTree;
import representation.Camera;
import representation.Color3f;
import representation.Matrix4f;
import representation.Point3f;
import representation.Scene;
import representation.SceneGraph;
import representation.TexCoord2f;
import representation.Texture;
import representation.Vector3f;
import transformation.Rotate;
import transformation.Scale;
import transformation.Shape;
import transformation.Transformation;
import transformation.Translate;

/**
  * Class used to build a scene from a given sdl file.
  * Implements the ParserHandler interface (these methods
  * need to be filled in by you).
  * 
  * Note that this class keeps the absolute path to the
  * directory where the sdl file was found.  If you put your
  * textures in the same directory, you can use this path
  * to construct an absolute file name for each texture.
  * You will probably need absolute file names when loading
  * the texture.
  */
public class SceneBuilder implements ParserHandler
{

    // the scene being build
    private Scene scene = null;
    
    private SceneGraph sg;
    
    private Scene getScene() { return scene; }

    // the path to the xml directory
    // this path can be used to put in front of the texture file name
    // to load the textures
    private String path = null;
    private HashMap<String, Geometry> geometries = new HashMap<String, Geometry>();
    // sum of colors
    Color3f sumAmbientColors = new Color3f();
    // num of colors
    float numAmbientColors = 0;

    public String getPath() { return path; }


    /**
     * Load a scene.
     * @param filename The name of the file that contains the scene.
     * @return The scene, or null if something went wrong.
     * @throws FileNotFoundException The file could not be found.
     */
    public Scene loadScene(String filename) throws FileNotFoundException
    {
        // create file and file input stream
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);

        // set the system id so that the dtd can be a relative path
        // the first 2 lines of your sdl file should always be
        //    <?xml version='1.0' encoding='utf-8'?>
        //    <!DOCTYPE Sdl SYSTEM "sdl.dtd">
        // and sdl.dtd should be in the same directory as the dtd
        // if you experience dtd problems, commend the doctype declaration
        //    <!-- <!DOCTYPE Sdl SYSTEM "sdl.dtd"> -->
        // and disable validation (see further)
        // although this is in general not a good idea

        InputSource inputSource = new InputSource(fileInputStream);
        String parentPath = file.getParentFile().getAbsolutePath() + "/";
        path = file.getParentFile().getAbsolutePath() + "/";
        inputSource.setSystemId("file:///" + file.getParentFile().getAbsolutePath() + "/");



        // create the new scene
        scene = new Scene();
        sg = scene.sceneGraph;
        

        // create the parser and parse the input file
        Parser parser = new Parser();
        parser.setHandler(this);

        // if the output bothers you, set echo to false
        // also, if loading a large file (with lots of triangles), set echo to false
        // you should leave validate to true
        // if the docuement is not validated, the parser will not detect syntax errors
        if (parser.parse(inputSource, /* validate */ true, /* echo */ false) == false)
        {
            scene = null;
        }

        // return the scene
        return scene;
    }

    /*
     *  (non-Javadoc)
     * ParserHandler callbacks
     */	

    public void startSdl() throws Exception
    {
    }

    public void endSdl() throws Exception
    {
    }

    public void startCameras() throws Exception
    {
    }

    public void endCameras() throws Exception
    {
    }

    public void startCamera(Point3f position, Vector3f direction, Vector3f up, float fovy, String name) throws Exception
    {
    	scene.cameras.put(name, new Camera(position, direction, up, fovy, name));
    }

    public void endCamera() throws Exception
    {
    }

    public void startLights() throws Exception
    {
    }

    public void endLights() throws Exception
    {
    }

    public void startDirectionalLight(Vector3f direction, float intensity, Color3f color, String name) throws Exception
    {

    }

    public void endDirectionalLight() throws Exception
    {
    }

    public void startPointLight(Point3f position, float intensity, Color3f color, String name) throws Exception
    {
    	scene.lights.put(name, new PointLight(position, intensity, color, name));
    }

    public void endPointLight() throws Exception
    {
    }

    public void startSpotLight(Point3f position, Vector3f direction, float angle, float intensity, Color3f color, String name) throws Exception
    {

    }

    public void endSpotLight() throws Exception
    {
    }

    public void startGeometry() throws Exception
    {
    }

    public void endGeometry() throws Exception
    {
    }

    public void startSphere(float radius, String name) throws Exception
    {
    	geometries.put(name, new Sphere(radius, name));
    }

    public void endSphere() throws Exception
    {
    }

    public void startCylinder(float radius, float height, boolean capped, String name) throws Exception
    {
    	geometries.put(name, new Cylinder(radius, height, capped, name));
    }

    public void endCylinder() throws Exception
    {
    }

    public void startCone(float radius, float height, boolean capped, String name) throws Exception
    {
    	geometries.put(name, new Cone(radius, height, capped, name));
    }

    public void endCone() throws Exception
    {
    }

    public void startTorus(float innerRadius, float outerRadius, String name) throws Exception
    {
    	geometries.put(name, new Torus(innerRadius, outerRadius, name));
    }

    public void endTorus() throws Exception
    {
    }

    public void startTeapot(float size, String name) throws Exception
    {
    	geometries.put(name, new Teapot(size, name));
    }

    public void endTeapot() throws Exception
    {
    }
    
    public void startOBJModel(float size, String name, String model) throws Exception
    {
    	geometries.put(name, new OBJModel(size, name, model));
    }

    public void endOBJModel() throws Exception
    {
    }

    public void startIndexedTriangleSet(Point3f [] coordinates, Vector3f [] normals, TexCoord2f [] textureCoordinates, int [] coordinateIndices, int [] normalIndices, int [] textureCoordinateIndices, String name) throws Exception
    {
    	IndexedTriangles it = new IndexedTriangles(name);
    	
    	// add vertices of the shape
    	for(Point3f p : coordinates)
    	{
    		it.vertices.add(p);
    	}
    	
    	// add normal vector of a vertex
    	for(Vector3f n : normals)
    	{
    		it.normals.add(n);
    	}
    	
    	// add textures of a vertex
    	for(TexCoord2f t : textureCoordinates)
    	{
    		it.textures.add(t);
    	}
    	
    	// compute index relation list representing faces of the shape
    	int i = 0;
    	while(i<coordinateIndices.length)
    	{
    		Integer[][] face = new Integer[3][3];
    		face[0][0] = coordinateIndices[i];
    		face[1][0] = coordinateIndices[i+1];
    		face[2][0] = coordinateIndices[i+2];
    		
    		if(textureCoordinateIndices.length>0)
    		{
    			face[0][1] = textureCoordinateIndices[i];
        		face[1][1] = textureCoordinateIndices[i+1];
        		face[2][1] = textureCoordinateIndices[i+2];
    		}else
    		{
    			face[0][1] = -1;
        		face[1][1] = -1;
        		face[2][1] = -1;
    		}
    		
    		if(normalIndices.length>0)
    		{
    			face[0][2] = normalIndices[i];
        		face[1][2] = normalIndices[i+1];
        		face[2][2] = normalIndices[i+2];
    		}else
    		{
    			face[0][2] = -1;
        		face[1][2] = -1;
        		face[2][2] = -1;
    		}
    		it.faces.add(face);
    		i += 3;
    	}
    	
    	it.computeTriangles();
    	geometries.put(name, it);
    }

    public void endIndexedTriangleSet() throws Exception
    {
    }

    public void startTextures() throws Exception
    {
    }

    public void endTextures() throws Exception
    {
    }

    public void startTexture(String src, String name) throws Exception
    {
    	scene.textures.put(name, new Texture(src, name));
    }

    public void endTexture() throws Exception
    {
    }

    public void startMaterials() throws Exception
    {
    }

    public void endMaterials() throws Exception
    {
    }

    public void startDiffuseMaterial(Color3f color, String name) throws Exception
    {
    	scene.materials.put(name, new DiffuseMaterial(color, name));
    }

    public void endDiffuseMaterial() throws Exception
    {
    }

    public void startPhongMaterial(Color3f color, float shininess, String name) throws Exception
    {
    	scene.materials.put(name, new PhongMaterial(color, shininess, name));
    }

    public void endPhongMaterial() throws Exception
    {
    }

    public void startLinearCombinedMaterial(String material1Name, float weight1, String material2Name, float weight2, String name) throws Exception
    {
    }

    public void endLinearCombinedMaterial() throws Exception
    {
    }

    public void startScene(String cameraName, String [] lightNames, Color3f background, Boolean antialiasing) throws Exception
    {
    	scene.activeCamera = scene.cameras.get(cameraName);
    	scene.antialiasing = antialiasing;

    	for(String name : lightNames)
    	{
    		Light light = scene.lights.get(name);
    		if(light!=null)
    			scene.activeLights.put(name, light);
    	}
    	
    	scene.background = background;
    	scene.computeUVWBasisVectors();
    }

    public void endScene() throws Exception
    {
    	// compute ambient color
    	sumAmbientColors.x += scene.background.x;
    	sumAmbientColors.y += scene.background.y;
    	sumAmbientColors.z += scene.background.z;
    	numAmbientColors++;
    	sumAmbientColors.x /= numAmbientColors; 
    	sumAmbientColors.y /= numAmbientColors; 
    	sumAmbientColors.z /= numAmbientColors; 
    	scene.ambientColor = sumAmbientColors;

    	// build BSPTree
    	
    	//System.out.println("root triangle; " + scene.rootBSPTree.shape.geometry.triangles.get(scene.rootBSPTree.indexTriangle));
    	/*System.out.println("root num triangle: " + scene.rootBSPTree.indexTriangle);
    	System.out.println("root num left triangle: " + scene.rootBSPTree.left.indexTriangle);*/
    	//System.out.println("root num left left triangle: " + scene.rootBSPTree.left.left.left.left.left.left.left.left.left.left.left.indexTriangle);
    }
    
    /**
	 * Returns a vector
	 * according to transformation matrix
	 * @param a the 3d point
	 * @return
	 */
	public Vector3f getTransformedNormalVector(Vector3f a, Matrix4f Mt)
	{
		Matrix4f N = new Matrix4f(	Mt.m11*Mt.m22-Mt.m12*Mt.m21, Mt.m12*Mt.m20-Mt.m10*Mt.m22, Mt.m10*Mt.m21-Mt.m11*Mt.m20, 0,
									Mt.m02*Mt.m21-Mt.m01*Mt.m22, Mt.m00*Mt.m22-Mt.m02*Mt.m20, Mt.m01*Mt.m20-Mt.m00*Mt.m21, 0,
									Mt.m01*Mt.m12-Mt.m02*Mt.m11, Mt.m02*Mt.m10-Mt.m00*Mt.m12, Mt.m00*Mt.m11-Mt.m01*Mt.m10, 0,
									0, 0, 0, 0);
		Matrix4f res = Matrix4f.computeProduct(N, new Point3f(a));

		Vector3f ret = new Vector3f(res.getElement(0, 0), res.getElement(1, 0), res.getElement(2, 0));
		ret.normalize();
		return ret;
	}
	
	public Point3f getTransformedPoint(Point3f p, Matrix4f Mt)
	{
		Matrix4f a = Matrix4f.computeProduct(Mt, p);
		return new Point3f(a.getElement(0, 0), a.getElement(1, 0), a.getElement(2, 0));		
	}

    public void startShape(String geometryName, String materialName, String textureName, Color3f reflection, Float refraction) throws Exception
    {
    	Geometry geo = geometries.get(geometryName);
    	Material mat = scene.materials.get(materialName);
    	Texture tex = scene.textures.get(textureName);
    	
    	// compute sum for ambient color
    	if(mat!=null)
    	{
    		sumAmbientColors.x += mat.color.x;
        	sumAmbientColors.y += mat.color.y;
        	sumAmbientColors.z += mat.color.z;
        	numAmbientColors++;
    	}
    	
    	Shape shape = new Shape(geo, mat, tex, reflection, refraction);
    	SceneGraph child = new SceneGraph(shape, sg);
    	sg.children.add(child);

    	// if the parent isn't the root
    	// we transform the shape
    	if(sg.value != null)
    	{
	    	// compute an intermediate transformation 
			// matrix of the shape
	    	child.setMi(sg.Mi, child.value.Mt());
	    	Matrix4f Mt = child.Mi;
	    	
	    	//transform vertices
	    	ArrayList<Point3f> vertices = new ArrayList<Point3f>();
	    	for(Point3f p : geo.vertices)
	    	{
	    		vertices.add(getTransformedPoint(p, Mt));
	    	}
	    	
	    	ArrayList<TexCoord2f> textures = geo.textures;
	    	
	    	//transform normals of points
	    	ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	    	for(Vector3f n : geo.normals)
	    	{
	    		/*System.out.println(n);
	    		System.out.println(getTransformedNormalVector(n, Mt));
	    		System.out.println();*/
	    		normals.add(getTransformedNormalVector(n, Mt));
	    		//Matrix4f e = Matrix4f.computeProduct(Mt, new Point3f(n));
	    		//normals.add(new Vector3f(e.getElement(0, 0), e.getElement(1, 0), e.getElement(2, 0)));
	    	}
	
	    	ArrayList<Integer[][]> faces = geo.faces;
	    	
	    	// define the geometric shape by the new transformed
	    	geo = new Geometry(vertices, textures, normals, faces);
	    	shape.geometry = geo;
    	}
    	
    	scene.shapes.add(shape);
    }

    public void endShape() throws Exception
    {
    }

    public void startRotate(Vector3f axis, float angle) throws Exception
    {
    	SceneGraph child = new SceneGraph(new Rotate(axis, angle, scene), sg);
    	sg.children.add(child);
    	
    	// compute an intermediate transformation 
		// matrix of the shape
    	child.setMi(sg.Mi, child.value.Mt());
    	sg = child;
    }

    public void endRotate() throws Exception
    {
    	sg = sg.parent;
    }

    public void startTranslate(Vector3f vector) throws Exception
    {
    	SceneGraph child = new SceneGraph(new Translate(vector), sg);
    	sg.children.add(child);
    	
    	// compute an intermediate transformation 
		// matrix of the shape
    	child.setMi(sg.Mi, child.value.Mt());
    	sg = child;
    }

    public void endTranslate() throws Exception
    {
    	sg = sg.parent;
    }

    public void startScale(Vector3f scale) throws Exception
    {
    	SceneGraph child = new SceneGraph(new Scale(scale), sg);
    	sg.children.add(child);
    	
    	// compute an intermediate transformation 
		// matrix of the shape
    	child.setMi(sg.Mi, child.value.Mt());
    	sg = child;
    }

    public void endScale() throws Exception
    {
    	sg = sg.parent;
    }


}
