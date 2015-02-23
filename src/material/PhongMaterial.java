package material;

import representation.Color3f;


/**
 * This class represents a material which 
 * has a surface appareance loosely described
 * as "matte" with highlights.
 * @author Dessart Charles-Eric
 *
 */
public class PhongMaterial extends Material
{
	
	public PhongMaterial(Color3f color, float shininess, String name) 
	{
		super(color);
		this.shininess = shininess;
		this.name = name;
	}
	
	public float shininess;
	public String name;
}