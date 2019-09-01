package examples;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.ComposedFunction;
import ca.uqac.lif.petitpoucet.functions.Connector;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.lists.SlidingWindow;
import ca.uqac.lif.petitpoucet.functions.numbers.Average;
import ca.uqac.lif.petitpoucet.functions.strings.Concatenate;
import ca.uqac.lif.petitpoucet.functions.strings.Substring;
import ca.uqac.lif.petitpoucet.common.StringDesignator;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example6
{
	public static void main(String[] args)
	{
		Constant c1 = new Constant(Example2.createList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5, 9));
		SlidingWindow ata = new SlidingWindow(3, new Average());
		Connector.connect(c1, 0, ata, 0);
		Object[] out = ata.evaluate();
		System.out.println(out[0]);
		ConcreteTracer tracer = new ConcreteTracer();
		ComposedDesignator cd = new ComposedDesignator(new NthElement(3), new NthOutput(0));
		ConcreteTraceabilityNode root = tracer.getTree(ProvenanceQuery.instance, cd, ata);
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		String dot_code = renderer.render(root);
		System.out.println(dot_code);
	}
}
