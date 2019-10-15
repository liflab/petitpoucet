package examples;

import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
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
		List<Object> list1 = Example2.createList("Mary", "had", "a", "little", "lamb");
		List<Object> list2 = Example2.createList("Inny", "minny", "miney", "mo", "!");
		GroupFunction cf = new GroupFunction(2, 1).setName("ConcTrim");
		{
			CircuitFunction conc = new CircuitFunction(new Concatenate());
			CircuitFunction sub = new CircuitFunction(new Substring(0, 4));
			cf.connect(conc, 0, sub, 0);
			cf.add(conc, sub);
			cf.associateInput(0, conc, 0);
			cf.associateInput(1, conc, 1);
			cf.associateOutput(0, sub, 0);
		}
		ApplyToAll ata = new ApplyToAll(cf);
		Object[] out = new Object[1];
		Queryable q = ata.evaluate(new Object[] {list1, list2}, out);
		System.out.println(out[0]);
		ConcreteTracer tracer = new ConcreteTracer();
		ComposedDesignator cd = new ComposedDesignator(new NthElement(2), new NthOutput(0));
		ConcreteTraceabilityNode root = tracer.getTree(ProvenanceQuery.instance, cd, q);
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		String dot_code = renderer.render(root);
		System.out.println(dot_code);
	}
}
