package examples;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.FunctionConnector;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;
import ca.uqac.lif.petitpoucet.graph.ConcreteObjectNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example1
{
	// (x + y) * z
	public static void main(String[] args)
	{
		int x = 1, y = -1, z = 0;
		GroupFunction gf = new GroupFunction(3, 1);
		CircuitFunction mul = new CircuitFunction(Numbers.multiplication);
		CircuitFunction add = new CircuitFunction(Numbers.addition);
		gf.connect(add, 0, mul, 0);
		gf.associateInput(0, add, 0);
		gf.associateInput(1, add, 1);
		gf.associateInput(2, mul, 1);
		gf.associateOutput(0, mul, 0);
		Object[] outputs = new Object[1];
		Queryable q = gf.evaluate(new Object[] {1, -1, 0}, outputs);
		System.out.println(outputs[0]);
		Designator d = new CircuitDesignator.NthOutput(0);
		ConcreteTracer t = new ConcreteTracer();
		ConcreteObjectNode root = t.getTree(ProvenanceQuery.instance, d, q);
		TraceabilityNodeDotRenderer rend = new TraceabilityNodeDotRenderer();
		rend.setFlatten(true);
		String s = rend.render(root);
		System.out.println(s);
	}
}
