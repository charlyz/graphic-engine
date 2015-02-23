package material;

import representation.Color3f;


/**
 * This class represents a material which 
 * has a surface appareance loosely described
 * as "matte".
 * @author Dessart Charles-Eric
 *
 */
public class DiffuseMaterial extends Material
{
	
	/**
	 * Constructor
	 * @param color color of the material
	 * @param name name of the material
	 */
	public DiffuseMaterial(Color3f color, String name) 
	{
		super(color);
		this.name = name;
	}
	public String name;
}