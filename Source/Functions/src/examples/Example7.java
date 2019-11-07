package examples;

import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Fork;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.lists.Filter;
import ca.uqac.lif.petitpoucet.functions.strings.RegexMatches;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example7
{

	public static void main(String[] args)
	{
		GroupFunction gf = new GroupFunction(1, 1).setName("all");
		List<Object> list = Utilities.createList("Mary", "had", "a", "little", "lamb");
		CircuitFunction f = new CircuitFunction(new Fork(String.class, 2));
		gf.associateInput(0, f, 0);
		CircuitFunction ata = new CircuitFunction(new ApplyToAll(new RegexMatches("[Mm]")));
		gf.connect(f, 1, ata, 0);
		CircuitFunction fil = new CircuitFunction(new Filter());
		gf.connect(f, 0, fil, 0);
		gf.connect(ata, 0, fil, 1);
		gf.associateOutput(0, fil, 0);
		Object[] values = new Object[1];
		Queryable q = gf.evaluate(new Object[] {list}, values);
		System.out.println(values[0]);
		ConcreteTracer tracer = new ConcreteTracer();
		ComposedDesignator cd = new ComposedDesignator(NthElement.get(1), NthOutput.get(0));
		ConcreteTraceabilityNode root = tracer.getTree(CausalityQuery.instance, cd, q);
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		String dot_code = renderer.render(root);
		System.out.println(dot_code);
	}
}
