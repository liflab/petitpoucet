
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.lists.SlidingWindow;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;
import ca.uqac.lif.petitpoucet.graph.ConcreteObjectNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;
import examples.Utilities;

public class ListCircuitExample
{
	public static void main(String[] args)
	{
		// Prepare function circuit
		GroupFunction all_positive = new GroupFunction(1, 1);
		{
			CircuitFunction win = new CircuitFunction(new SlidingWindow(2, Numbers.avg));
			GroupFunction gt_0 = new GroupFunction(1, 1);
			{
				CircuitFunction gt = new CircuitFunction(Numbers.isGreaterThan);
				CircuitFunction zero = new CircuitFunction(new Constant(0));
				gt_0.add(gt, zero);
				gt_0.associateInput(0, gt, 0);
				gt_0.connect(zero, 0, gt, 1);
				gt_0.associateOutput(0, gt, 0);
			}
			CircuitFunction positive = new CircuitFunction(new ApplyToAll(gt_0));
			all_positive.add(win, positive);
			all_positive.connect(win, 0, positive, 0);
			all_positive.associateInput(0, win, 0);
			all_positive.associateOutput(0, positive, 0);
		}
		// Create input list and output array
		List<Object> in_list = Utilities.createList(1, -10, 30, -5, 3);
		Object[] outputs = new Object[1];
		// Evaluate input on circuit
		Queryable q = all_positive.evaluate(new Object[] {in_list}, outputs);
		// Show the first element of the output array (the output list)
		System.out.println(outputs[0]);
		// Get explanation tree for the output element at position 0 in the list
		ConcreteTracer tracer = new ConcreteTracer();
		int position = 0;
		Designator part = ComposedDesignator.create(NthElement.get(position), CircuitDesignator.NthOutput.get(0));
		ConcreteObjectNode root = tracer.getTree(CausalityQuery.instance, part, q);
		// Print the tree as a Graphviz file
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		renderer.setFlatten(true);
		String s = renderer.render(root);
		System.out.println(s);
	}
}
