package examples;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;
import ca.uqac.lif.petitpoucet.graph.ConcreteObjectNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example2
{
	public static void main(String[] args)
	{
		List<Object> list1 = Utilities.createList(3, 1, 4, 1, 5, 9, 2);
		List<Object> list2 = Utilities.createList(2, 0, 1, 8, 2, 8, 1);
		ApplyToAll ata = new ApplyToAll(Numbers.multiplication);
		Object[] output = new Object[1];
		Queryable q = ata.evaluate(new Object[] {list1, list2}, output);
		System.out.println(output[0]);
		Designator d = new ComposedDesignator(CollectionDesignator.NthElement.get(1),
				CircuitDesignator.NthOutput.get(0));
		ConcreteTracer t = new ConcreteTracer();
		ConcreteObjectNode root = t.getTree(CausalityQuery.instance, d, q);
		TraceabilityNodeDotRenderer rend = new TraceabilityNodeDotRenderer();
		String s = rend.render(root);
		System.out.println(s);
	}


}
