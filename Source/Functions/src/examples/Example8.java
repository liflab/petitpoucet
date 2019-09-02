package examples;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.ComposedFunction;
import ca.uqac.lif.petitpoucet.functions.Connector;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.io.FileLines;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.lists.GetElement;
import ca.uqac.lif.petitpoucet.functions.lists.SlidingWindow;
import ca.uqac.lif.petitpoucet.functions.ltl.Globally;
import ca.uqac.lif.petitpoucet.functions.numbers.Average;
import ca.uqac.lif.petitpoucet.functions.numbers.IsGreaterThan;
import ca.uqac.lif.petitpoucet.functions.numbers.NumberCast;
import ca.uqac.lif.petitpoucet.functions.strings.Split;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

public class Example8
{
	public static void main(String[] args)
	{
		FileLines fl = new FileLines("/tmp/values.csv");
		ComposedFunction get = new ComposedFunction(1, 1).setName("GET");
		{
			Split split = new Split(",");
			GetElement ge = new GetElement(1);
			Connector.connect(split, ge);
			NumberCast tonum = new NumberCast();
			Connector.connect(ge, tonum);
			get.add(split, ge, tonum);
			get.associateInput(0, split, 0);
			get.associateOutput(0, tonum, 0);
		}
		ApplyToAll tonum = new ApplyToAll(get);
		Connector.connect(fl, tonum);
		SlidingWindow avg_win = new SlidingWindow(3, new Average());
		Connector.connect(tonum, avg_win);
		ComposedFunction gt2 = new ComposedFunction(1, 1).setName("GT 3?");
		{
			IsGreaterThan igt = new IsGreaterThan();
			Constant two = new Constant(30);
			Connector.connect(two, 0, igt, 1);
			gt2.add(igt, two);
			gt2.associateInput(0, igt, 0);
			gt2.associateOutput(0, igt, 0);
		}
		ApplyToAll igt2 = new ApplyToAll(gt2);
		Connector.connect(avg_win, igt2);
		Globally g = new Globally();
		Connector.connect(igt2, g);
		Object[] out = g.evaluate();
		System.out.println(out[0]);
		ConcreteTracer tracer = new ConcreteTracer();
		ConcreteTraceabilityNode root = tracer.getTree(CausalityQuery.instance, new NthOutput(0), g);
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		renderer.setFlatten(true);
		String dot_code = renderer.render(root);
		System.out.println(dot_code);
	}
}
