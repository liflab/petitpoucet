package examples.ltl;

import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.Equals;
import ca.uqac.lif.petitpoucet.functions.Fork;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.TreeDrawer;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.lists.GetElement;
import ca.uqac.lif.petitpoucet.functions.ltl.Ltl;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeRenderer.CaptionStyle;
import examples.Utilities;

public class HasNext 
{

	public static void main(String[] args) 
	{
		GroupFunction gf = new GroupFunction(1, 1).setName("G (next imp (X hasNext))");
		{
			CircuitFunction f = new CircuitFunction(new Fork(String.class, 2));
			GroupFunction eq_next = new GroupFunction(1, 1).setName("= next");
			{
				CircuitFunction eq = new CircuitFunction(Equals.instance);
				CircuitFunction c = new CircuitFunction(new Constant("next"));
				eq_next.connect(c, 0, eq, 1);
				eq_next.associateInput(0, eq, 0);
				eq_next.associateOutput(0, eq, 0);
			}
			CircuitFunction g_eq_next = new CircuitFunction(new ApplyToAll(eq_next));
			GroupFunction eq_hasnext = new GroupFunction(1, 1).setName("= hasNext");
			{
				CircuitFunction eq = new CircuitFunction(Equals.instance);
				CircuitFunction c = new CircuitFunction(new Constant("hasNext"));
				eq_hasnext.connect(c, 0, eq, 1);
				eq_hasnext.associateInput(0, eq, 0);
				eq_hasnext.associateOutput(0, eq, 0);
			}
			CircuitFunction g_eq_hasnext = new CircuitFunction(new ApplyToAll(eq_hasnext));
			CircuitFunction x = new CircuitFunction(Ltl.next);
			CircuitFunction imp = new CircuitFunction(Ltl.implies);
			CircuitFunction g = new CircuitFunction(Ltl.globally);
			CircuitFunction first = new CircuitFunction(new GetElement(0));
			gf.associateInput(0, f, 0);
			gf.connect(f, 0, g_eq_next, 0);
			gf.connect(f, 1, g_eq_hasnext, 0);
			gf.connect(g_eq_next, 0, imp, 0);
			gf.connect(g_eq_hasnext, 0, x, 0);
			gf.connect(x, 0, imp, 1);
			gf.connect(imp, 0, g, 0);
			gf.connect(g, 0, first, 0);
			gf.associateOutput(0, first, 0);
		}
		List<Object> list = Utilities.createList("next", "hasNext");
		
		// Use the shortcut from TreeDrawer to evaluate function and answer query
		TreeDrawer.drawTree(CausalityQuery.instance, new ComposedDesignator(NthElement.get(0), NthOutput.get(0)), CaptionStyle.SHORT, true, "/tmp/out.png", gf, list);
	}

}
