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
import ca.uqac.lif.petitpoucet.functions.strings.Concatenate;
import ca.uqac.lif.petitpoucet.functions.strings.Substring;
import ca.uqac.lif.petitpoucet.common.StringDesignator;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example5
{
	public static void main(String[] args)
	{
		Constant c1 = new Constant(Example2.createList("Mary", "had", "a", "little", "lamb"));
		Constant c2 = new Constant(Example2.createList("Inny", "minny", "miney", "mo", "!"));
		ComposedFunction cf = new ComposedFunction(2, 1).setName("ConcTrim");
		{
			Concatenate conc = new Concatenate();
			Substring sub = new Substring(0, 4);
			Connector.connect(conc, sub);
			cf.add(conc, sub);
			cf.associateInput(0, conc, 0);
			cf.associateInput(1, conc, 1);
			cf.associateOutput(0, sub, 0);
		}
		ApplyToAll ata = new ApplyToAll(cf);
		Connector.connect(c1, 0, ata, 0);
		Connector.connect(c2, 0, ata, 1);
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
