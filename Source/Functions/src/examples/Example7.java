package examples;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.Connector;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.Fork;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.lists.Filter;
import ca.uqac.lif.petitpoucet.functions.strings.RegexMatches;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

public class Example7
{

	public static void main(String[] args)
	{
		Constant list = new Constant(Example2.createList("Mary", "had", "a", "little", "lamb"));
		Fork f = new Fork();
		Connector.connect(list, f);
		ApplyToAll ata = new ApplyToAll(new RegexMatches("[Mm]"));
		Connector.connect(f, 1, ata, 0);
		Filter fil = new Filter();
		Connector.connect(f, 0, fil, 0);
		Connector.connect(ata, 0, fil, 1);
		Object[] values = fil.evaluate();
		System.out.println(values[0]);
		ConcreteTracer tracer = new ConcreteTracer();
		ComposedDesignator cd = new ComposedDesignator(new NthElement(1), new NthOutput(0));
		ConcreteTraceabilityNode root = tracer.getTree(CausalityQuery.instance, cd, fil);
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		String dot_code = renderer.render(root);
		System.out.println(dot_code);
	}
}
