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
package examples.number;

import static ca.uqac.lif.petitpoucet.ComposedPart.compose;
import static examples.util.GraphViewer.display;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.number.Addition;
import ca.uqac.lif.petitpoucet.function.number.Multiplication;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

/**
 * Evaluates a circuit corresponding to the function (x+y)×z. Graphically, this
 * circuit can be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/number/ArithmeticCircuit.png" alt="Circuit">
 * 
 * <h3>Explanation</h3>
 * 
 * The program evaluates this circuit on the input x=2, y=3, z=0, and then
 * requests the explanation graph corresponding to the resulting output.
 * 
 * <h4>Full graph</h4>
 * The full explanation graph corresponding to that explanation is the
 * following. 
 * <p>
 * <img src="{@docRoot}/doc-files/number/ArithmeticCircuit2-full.png" alt="Full graph">
 * <p>
 * One can see that the circuit's output depends on all three input arguments.
 * 
 * <h4>Simplified graph</h4>
 * 
 * The simplified graph only keeps the leaves and intermediate Boolean nodes:
 * <p>
 * <img src="{@docRoot}/doc-files/number/ArithmeticCircuit2-small.png" alt="Simplified graph">
 * 
 * @author Sylvain Hallé
 * @see ArithmeticCircuit1
 * @see ArithmeticCircuit3
 */
public class ArithmeticCircuit2
{
	public static void main(String[] args)
	{
		Circuit c = new Circuit(3, 1, "(x+y)×z");
		Addition a = new Addition(2);
		Multiplication m = new Multiplication(2);
		c.addNodes(a, m);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateInput(2, m.getInputPin(1));
		NodeConnector.connect(a, 0, m, 0);
		c.associateOutput(0, m.getOutputPin(0));
		Number result = (Number) c.evaluate(2, 3, 0)[0];
		System.out.println(result);
		Node full_graph = c.getExplanation(compose(new NthElement(1), NthOutput.FIRST));
		display(full_graph);
	}
}
