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
import ca.uqac.lif.petitpoucet.functions.ContextVariable;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.Identity;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.logic.Exists;
import ca.uqac.lif.petitpoucet.functions.logic.ForAll;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;
import ca.uqac.lif.petitpoucet.graph.ConcreteObjectNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example3
{
	public static void main(String[] args)
	{
		List<Object> list1 = createList(2, 4, 6);
		Constant c_list1 = new Constant(list1);
		GroupFunction gf_even = new GroupFunction(0, 1).setName("even?");
		{
			CircuitFunction cf_x = new CircuitFunction(new ContextVariable("x"));
			CircuitFunction cf_e = new CircuitFunction(Numbers.isEven);
			gf_even.connect(cf_x, 0, cf_e, 0);
			gf_even.associateOutput(0, cf_e, 0);
		}
		ForAll ex = new ForAll("x", new Identity(Number.class), new CircuitFunction(gf_even));
		Object[] output = new Object[1];
		Queryable q = ex.evaluate(new Object[] {list1}, output);
		System.out.println(output[0]);
		ConcreteTracer t = new ConcreteTracer();
		ConcreteObjectNode root = t.getTree(CausalityQuery.instance, CircuitDesignator.NthOutput.get(0), q);
		TraceabilityNodeDotRenderer rend = new TraceabilityNodeDotRenderer();
		String s = rend.render(root);
		System.out.println(s);
	}

	protected static List<Object> createList(Object... objects)
	{
		List<Object> out_list = new ArrayList<Object>(objects.length);
		for (Object o : objects)
		{
			out_list.add(o);
		}
		return out_list;
	}
}
