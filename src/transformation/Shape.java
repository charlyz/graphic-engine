package transformation;

import material.Material;
import geometry.Geometry;
import representation.Color3f;
import representation.Matrix4f;
import representation.Texture;

/**
 * This class represents a shape.
 * It's a relation object between
 * a geometric shape, a material and
 * a texture.
 * @author Dessart Charles-Eric
 *
 */
public class Shape extends Transformation
{
	/**
	 * Constructor
	 * @param geometry
	 * @param material
	 * @param texture
	 */
	public Shape(Geometry geometry, Material material, Texture texture, Color3f reflection, Float refraction) 
	{	
		this.geometry = geometry;
		this.material = material;
		this.texture = texture;
		this.reflection = reflection;
		this.refraction = refraction;
	}
	public Geometry geometry;
	public Material material;
	public Texture texture;
	public Color3f reflection;
	public Float refraction;

	public Matrix4f Mt() 
	{
		if(Mt!=null)
			return Mt;
		
		Mt = new Matrix4f(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		return Mt;
	}
}