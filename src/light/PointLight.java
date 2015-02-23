package light;

import representation.Color3f;
import representation.Point3f;

/**
 * This class represents a point light
 * @author Dessart Charles-Eric
 *
 */
public class PointLight extends Light
{
	/**
	 * Constructor
	 * @param position position of the light
	 * @param intensity intensity of the light
	 * @param color color of the light
	 * @param name name of the light
	 */
	public PointLight(Point3f position, Float intensity, Color3f color, String name) 
	{
		this.position = position;
		this.intensity = intensity;
		this.color = color;
		this.name = name;
	}
	public Point3f position;
	public Float intensity;
	public Color3f color;
	public String name;
}