package examples.circuits;

import java.util.Arrays;
import java.util.List;

import static ca.uqac.lif.petitpoucet.GraphUtilities.simplify;
import static ca.uqac.lif.petitpoucet.GraphUtilities.flatten;
import static examples.util.GraphViewer.display;
import static examples.util.GraphViewer.save;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.petitpoucet.function.Constant;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.number.IsGreaterThan;
import ca.uqac.lif.petitpoucet.function.number.NumberCast;
import ca.uqac.lif.petitpoucet.function.strings.Split;
import ca.uqac.lif.petitpoucet.function.vector.ElementAt;
import ca.uqac.lif.petitpoucet.function.vector.VectorAnd;
import ca.uqac.lif.petitpoucet.function.vector.VectorApply;
import ca.uqac.lif.petitpoucet.function.vector.VectorSum;
import ca.uqac.lif.petitpoucet.function.vector.Window;

public class ConditionAverage
{
	public static void main(String[] args)
	{
		Circuit get_second = new Circuit(1, 1, "2nd");
		{
			Split s = new Split(",");
			ElementAt e = new ElementAt(1);
			NumberCast n = new NumberCast();
			NodeConnector.connect(s, 0, e, 0);
			NodeConnector.connect(e, 0, n, 0);
			get_second.addNodes(s, e, n);
			get_second.associateInput(0, s.getInputPin(0));
			get_second.associateOutput(0, n.getOutputPin(0));
		}
		VectorApply a1 = new VectorApply(get_second);
		Window w = new Window(new VectorSum(), 3);
		NodeConnector.connect(a1, 0, w, 0);
		Circuit gt3 = new Circuit(1, 1, "&gt;3");
		{
			Constant c = new Constant(3);
			IsGreaterThan g = new IsGreaterThan();
			NodeConnector.connect(c, 0, g, 1);
			gt3.addNodes(c, g);
			gt3.associateInput(0, g.getInputPin(0));
			gt3.associateOutput(0, g.getOutputPin(0));
		}
		VectorApply a2 = new VectorApply(gt3);
		NodeConnector.connect(w, 0, a2, 0);
		VectorAnd and = new VectorAnd();
		NodeConnector.connect(a2, 0, and, 0);
		Circuit global = new Circuit(1, 1, "&phi;");
		global.addNodes(a1, w, a2, and);
		global.associateInput(0, a1.getInputPin(0));
		global.associateOutput(0, and.getOutputPin(0));
		List<String> inputs = Arrays.asList(
				"the,2,penny",
				"fool,7,lane",
				"on,18,come",
				"the,2,together",
				"hill,-80,i",
				"strawberry,7,am",
				"fields,1,the",
				"forever,10,walrus");
		Boolean b = (Boolean) global.evaluate(inputs)[0];
		System.out.println(b);
		Node graph = global.getExplanation(NthOutput.FIRST);
		//save(flatten(graph), "/tmp/Flat.png");
		save(graph, "/tmp/Big.png");
	}
}
