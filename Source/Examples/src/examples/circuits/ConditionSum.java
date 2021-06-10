/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package examples.circuits;

import java.util.Arrays;
import java.util.List;

import static ca.uqac.lif.petitpoucet.function.FunctionLineageGraphUtilities.simplify;
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

/**
 * Extracts a parameter from a list of CSV text lines, and checks that the sum
 * of three successive values is always positive. Graphically, this circuit can
 * be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/circuits/ConditionSum.png" alt="Circuit">
 * <p>
 * The circuit is evaluated on this list of text lines:
 * <pre>
 * the,2,penny
 * fool,7,lane,
 * on,18,come,
 * the,2,together
 * hill,-80,i
 * strawberry,7,am
 * fields,1,the
 * forever,10,walrus</pre>
 * The parameter that is extracted is the second on each line, which is
 * converted to a number. The output result is the single value {@code false}.
 * Indeed, it is possible to see that a few windows of 3 successive events have
 * a negative sum.
 * 
 * <h4>Full graph</h4>
 * The complete explanation graph for the circuit's output is the following.
 * Labels are displayed only for the root and leaf nodes. Click on the graph to
 * see the detailed version with all labels.
 * <p>
 * <a href="{@docRoot}/doc-files/circuits/ConditionSum-full.png"><img src="{@docRoot}/doc-files/circuits/ConditionSum-full-simp.png" height="400" alt="Circuit"></a>
 * 
 * <h4>Simplified graph</h4>
 * The simplified explanation graph is the following:
 * <p>
 * <img src="{@docRoot}/doc-files/circuits/ConditionSum-small.png" alt="Simplified circuit">
 * <p>
 * It shows that the false result can be explained in three alternate ways:
 * <ul>
 * <li>one that involves the parameters in the 3rd, 4th and 5th lines</li>
 * <li>one that involves the parameters in the 4rd, 5th and 6th lines</li>
 * <li>one that involves the parameters in the 5th, 6th and 7th lines</li>
 * </ul>
 * Note that in each case, the precise location of the number within the line
 * are given. For example, the first leaf refers to characters at position
 * 3 and 4 in the 3rd line.
 * 
 * @author Sylvain Hallé
 */
public class ConditionSum
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
		display(simplify(graph));
		save(simplify(graph), "/tmp/Flat.png");
		save(graph, "/tmp/Big.png");
		save(graph, "/tmp/Big-simp.png", true);
	}
}
