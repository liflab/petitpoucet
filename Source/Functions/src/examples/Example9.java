package examples;

import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.ContextVariable;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.logic.ForAll;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;
import ca.uqac.lif.petitpoucet.functions.strings.Split;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeRenderer.CaptionStyle;

public class Example9 {

	public static void main(String[] args)
	{
		int constant = 3;
		GroupFunction splitnum = new GroupFunction(1, 1).setName("splitnum");
		CircuitFunction split = new CircuitFunction(new Split(","));
		CircuitFunction tonum = new CircuitFunction(new ApplyToAll(Numbers.cast));
		splitnum.connect(split, 0, tonum, 0);
		splitnum.associateInput(0, split, 0);
		splitnum.associateOutput(0, tonum, 0);
		GroupFunction gt = new GroupFunction(0, 1).setName("x > " + constant);
		CircuitFunction isgt = new CircuitFunction(Numbers.isGreaterThan);
		CircuitFunction ct = new CircuitFunction(new Constant(constant));
		CircuitFunction x = new CircuitFunction(new ContextVariable("x"));
		gt.connect(x, 0, isgt, 0);
		gt.connect(ct, 0, isgt, 1);
		gt.associateOutput(0, isgt, 0);
		CircuitFunction c_gt = new CircuitFunction(gt);
		ForAll fa = new ForAll("x", splitnum, c_gt);
		Object[] out = new Object[1];
		Queryable q = fa.evaluate(new Object[] {"3, 1, 4, 1, 6"}, out);
		System.out.println(out[0]);
		ConcreteTracer tracer = new ConcreteTracer();
		ConcreteTraceabilityNode root = tracer.getTree(CausalityQuery.instance, NthOutput.get(0), q);
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		renderer.setFlatten(true);
		renderer.setShowCaptions(CaptionStyle.FULL);
		String dot_code = renderer.render(root);
		System.out.println(dot_code);
	}

}
