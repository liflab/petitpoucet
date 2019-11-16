package examples.trees;

import java.util.List;

import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Fork;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.TreeDrawer;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.lists.GetElement;
import ca.uqac.lif.petitpoucet.functions.lists.Product;
import ca.uqac.lif.petitpoucet.functions.lists.Wrap;
import ca.uqac.lif.petitpoucet.functions.logic.Booleans;
import ca.uqac.lif.petitpoucet.functions.logic.Lists;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;
import ca.uqac.lif.petitpoucet.functions.trees.GetAttribute;
import ca.uqac.lif.petitpoucet.functions.trees.PathFetch;
import ca.uqac.lif.petitpoucet.functions.trees.PathFetch.PathElement;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeRenderer.CaptionStyle;
import ca.uqac.lif.petitpoucet.functions.trees.TreeNode;
import examples.ltl.TreeUtils;

public class FetchAll
{
	public static void main(String[] args)
	{
		
		TreeNode root = TreeUtils.create("", "x", 10, "y", 10, "width", 200, "height", 400)
				.addChild(TreeUtils.create("c", "x", 10, "y", 10, "width", 20, "height", 40))
				.addChild(TreeUtils.create("b", "x", 15, "y", 10, "width", 20, "height", 40)
						.addChild((TreeUtils.create("c", "x", 15, "y", 10, "width", 200, "height", 4)))
						);
		
		AllBoxes abb = new AllBoxes();
		// Use the shortcut from TreeDrawer to evaluate function and answer query
		TreeDrawer.drawTree(CausalityQuery.instance, NthOutput.get(0), CaptionStyle.SHORT, true, "/tmp/out.png", abb, root);
		/*
		Object[] inputs = new Object[] {root};
		Object[] outputs = new Object[1];
		Queryable q = abb.evaluate(inputs, outputs);
		System.out.println(outputs[0]);
		*/
	}
	
	public static class AllBoxes extends GroupFunction
	{
		public AllBoxes()
		{
			super(1, 1);
			setName("All boxes");
			CircuitFunction path = new CircuitFunction(new PathFetch(PathElement.get(PathElement.WILDCARD, true)));
			associateInput(0, path, 0);
			CircuitFunction ata = new CircuitFunction(new ApplyToAll(new CompareBoxes()));
			connect(path, 0, ata, 0);
			CircuitFunction g = new CircuitFunction(Lists.all);
			connect(ata, 0, g, 0);
			associateOutput(0, g, 0);
		}
	}
	
	public static class CompareBoxes extends GroupFunction
	{
		public CompareBoxes()
		{
			super(1, 1);
			setName("Compare boxes");
			CircuitFunction f = new CircuitFunction(new Fork(List.class, 2));
			associateInput(0, f, 0);
			CircuitFunction wrap = new CircuitFunction(Wrap.instance);
			connect(f, 0, wrap, 0);
			CircuitFunction children = new CircuitFunction(new PathFetch(PathElement.get(PathElement.WILDCARD)));
			connect(f, 1, children, 0);
			CircuitFunction product = new CircuitFunction(Product.get(2));
			connect(wrap, 0, product, 0);
			connect(children, 0, product, 1);
			CircuitFunction ata = new CircuitFunction(new ApplyToAll(new BoundingBox()));
			connect(product, 0, ata, 0);
			CircuitFunction all_true = new CircuitFunction(Lists.all);
			connect(ata, 0, all_true, 0);
			associateOutput(0, all_true, 0);
		}
		
		@Override
		public CompareBoxes duplicate(boolean with_state)
		{
			return this;
		}
	}
	
	public static class BoundingBox extends GroupFunction
	{
		public BoundingBox()
		{
			super(1, 1);
			setName("Bounding box");
			CircuitFunction f = new CircuitFunction(new Fork(List.class, 2));
			associateInput(0, f, 0);
			CircuitFunction get_1 = new CircuitFunction(new GetElement(0));
			connect(f, 0, get_1, 0);
			CircuitFunction get_2 = new CircuitFunction(new GetElement(1));
			connect(f, 1, get_2, 0);
			CircuitFunction f_1 = new CircuitFunction(new Fork(List.class, 2));
			CircuitFunction f_2 = new CircuitFunction(new Fork(List.class, 2));
			connect(get_1, 0, f_1, 0);
			connect(get_2, 0, f_2, 0);
			CircuitFunction w_1 = new CircuitFunction(GetAttribute.get("width"));
			connect(f_1, 0, w_1, 0);
			CircuitFunction w_2 = new CircuitFunction(GetAttribute.get("width"));
			connect(f_2, 0, w_2, 0);
			CircuitFunction h_1 = new CircuitFunction(GetAttribute.get("height"));
			connect(f_1, 1, h_1, 0);
			CircuitFunction h_2 = new CircuitFunction(GetAttribute.get("height"));
			connect(f_2, 1, h_2, 0);
			CircuitFunction w_gt = new CircuitFunction(Numbers.isGreaterThan);
			connect(w_1, 0, w_gt, 0);
			connect(w_2, 0, w_gt, 1);
			CircuitFunction h_gt = new CircuitFunction(Numbers.isGreaterThan);
			connect(h_1, 0, h_gt, 0);
			connect(h_2, 0, h_gt, 1);
			CircuitFunction and = new CircuitFunction(Booleans.and);
			connect(w_gt, 0, and, 0);
			connect(h_gt, 0, and, 1);
			associateOutput(0, and, 0);
		}
		
		@Override
		public BoundingBox duplicate(boolean with_state)
		{
			return this;
		}
	}
}
