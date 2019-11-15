package examples.ltl;

import ca.uqac.lif.petitpoucet.functions.trees.TreeNode;

public class TreeUtils 
{
	private TreeUtils()
	{
		super();
	}
	
	public static TreeNode create(String name, Object ... attrs)
	{
		TreeNode tn = new TreeNode(name);
		for (int i = 0; i < attrs.length; i+= 2)
		{
			Object a_name = attrs[i];
			Object a_value = attrs[i+1];
			tn.set((String) a_name, a_value);
		}
		return tn;
	}
}
