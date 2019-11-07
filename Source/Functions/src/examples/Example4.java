package examples;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.Fork;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example4
{
	public static void main(String[] args)
	{
		GroupFunction comp = new GroupFunction(1, 1).setName("Double");
		CircuitFunction fork = new CircuitFunction(new Fork(Number.class, 2));
		CircuitFunction add = new CircuitFunction(Numbers.addition);
		comp.connect(fork, 0, add, 0);
		comp.connect(fork, 1, add, 1);
		comp.add(fork, add);
		comp.associateInput(0, fork, 0);
		comp.associateOutput(0, add, 0);
		ApplyToAll ata = new ApplyToAll(comp);
		Object[] out = new Object[1];
		Queryable q = ata.evaluate(new Object[] {Example2.createList(3, 1, 4, 1, 5, 9, 2)}, out);
		System.out.println(out[0]);
		ConcreteTracer tracer = new ConcreteTracer();
		ComposedDesignator cd = new ComposedDesignator(new NthElement(1), NthOutput.get(0));
		ConcreteTraceabilityNode root = tracer.getTree(ProvenanceQuery.instance, cd, q);
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		String dot_code = renderer.render(root);
		System.out.println(dot_code);
	}
}
