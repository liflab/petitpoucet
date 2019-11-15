package examples.trees;

import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.functions.trees.PathFetch;
import ca.uqac.lif.petitpoucet.functions.trees.PathFetch.NthChild;
import ca.uqac.lif.petitpoucet.functions.trees.PathFetch.PathElement;
import ca.uqac.lif.petitpoucet.functions.trees.TreeNode;
import examples.ltl.TreeUtils;

public class FetchAll
{
	public static void main(String[] args)
	{
		
		TreeNode root = TreeUtils.create("a", "x", 10, "y", 10, "width", 20, "height", 40)
				.addChild(TreeUtils.create("c", "x", 10, "y", 10, "width", 20, "height", 40))
				.addChild(TreeUtils.create("b", "x", 15, "y", 10, "width", 20, "height", 40)
						.addChild((TreeUtils.create("c", "x", 15, "y", 10, "width", 20, "height", 40)))
						);
		PathFetch pf = new PathFetch(PathElement.get("*", false));
		Object[] inputs = new Object[] {root};
		Object[] outputs = new Object[1];
		Queryable q = pf.evaluate(inputs, outputs);
		System.out.println(outputs[0]);
	}
	
	
}
