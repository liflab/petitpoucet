package examples.ltl;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.TreeDrawer;
import ca.uqac.lif.petitpoucet.functions.ltl.Ltl;
import ca.uqac.lif.petitpoucet.functions.ltl.Ltl.Globally;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeRenderer.CaptionStyle;

public class LtlGlobally 
{
	public static void main(String[] args) 
	{
		List<Boolean> list = new ArrayList<Boolean>(7);
		list.add(true);
		list.add(false);
		list.add(true);
		list.add(true);
		list.add(false);
		list.add(false);
		list.add(true);
		Globally g = Ltl.globally;

		// Use the shortcut from TreeDrawer to evaluate function and answer query
		TreeDrawer.drawTree(CausalityQuery.instance, new ComposedDesignator(NthElement.get(5), NthOutput.get(0)), CaptionStyle.NONE, true, "/tmp/out.png", g, list);
	}
}
