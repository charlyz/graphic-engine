package representation;

/**
 * This class represents a camera
 * @author Dessart Charles-Eric
 *
 */
public class Camera
{
	/**
	 * Constructor
	 * @param position position of the camera
	 * @param direction direction of the camera
	 * @param up up vector of the camera
	 * @param fovy2 field of view of the camera
	 * @param name name of the camera
	 */
	public Camera(Point3f position, Vector3f direction, Vector3f up, Float fovy2, String name) 
	{
		this.position = position;
		this.direction = direction;
		this.up = up;
		this.fovy = fovy2;
		this.name = name;
	}
	public Point3f position;
	public Vector3f direction;
	public Vector3f up;
	public Float fovy;
	public String name;
}