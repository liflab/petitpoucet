package examples;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.TreeDrawer;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.io.FileLines;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.lists.GetElement;
import ca.uqac.lif.petitpoucet.functions.lists.SlidingWindow;
import ca.uqac.lif.petitpoucet.functions.ltl.Ltl;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;
import ca.uqac.lif.petitpoucet.functions.strings.Split;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeRenderer.CaptionStyle;

@SuppressWarnings("unused")
public class Example8
{
	public static void main(String[] args)
	{
		GroupFunction global = new GroupFunction(0, 1).setName("All");
		{
			CircuitFunction fl = new CircuitFunction(new FileLines("/tmp/values.csv"));
			GroupFunction get = new GroupFunction(1, 1).setName("GET");
			{
				CircuitFunction split = new CircuitFunction(new Split(","));
				CircuitFunction ge = new CircuitFunction(new GetElement(1));
				get.connect(split, 0, ge, 0);
				CircuitFunction tonum = new CircuitFunction(Numbers.cast);
				get.connect(ge, 0, tonum, 0);
				get.add(split, ge, tonum);
				get.associateInput(0, split, 0);
				get.associateOutput(0, tonum, 0);
			}
			CircuitFunction tonum = new CircuitFunction(new ApplyToAll(get));
			global.connect(fl, 0, tonum, 0);
			CircuitFunction avg_win = new CircuitFunction(new SlidingWindow(3, Numbers.avg));
			global.connect(tonum, 0, avg_win, 0);
			GroupFunction gt2 = new GroupFunction(1, 1).setName("GT 30?");
			{
				CircuitFunction igt = new CircuitFunction(Numbers.isGreaterThan);
				CircuitFunction two = new CircuitFunction(new Constant(3));
				gt2.connect(two, 0, igt, 1);
				gt2.add(igt, two);
				gt2.associateInput(0, igt, 0);
				gt2.associateOutput(0, igt, 0);
			}
			CircuitFunction igt2 = new CircuitFunction(new ApplyToAll(gt2));
			global.connect(avg_win, 0, igt2, 0);
			CircuitFunction g = new CircuitFunction(Ltl.globally);
			global.connect(igt2, 0, g, 0);
			global.associateOutput(0, g, 0);
		}

		// Use the shortcut from TreeDrawer to evaluate function and answer query
		TreeDrawer.drawTree(CausalityQuery.instance, new NthOutput(0), CaptionStyle.NONE, true, "/tmp/out.png", global);

		/*
		// Alternate syntax: evaluate, query and draw directly
		Object[] out = new Object[1];
		Queryable q = global.evaluate(new Object[] {}, out);
		ConcreteTracer tracer = new ConcreteTracer();
		ConcreteTraceabilityNode root = tracer.getTree(ProvenanceQuery.instance, new NthOutput(0), q);
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		renderer.setFlatten(true);
		renderer.setShowCaptions(true);
		String dot_code = renderer.render(root);
		System.out.println(dot_code);
		 */
	}
}
