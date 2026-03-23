/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.examples.number;

import static ca.uqac.lif.petitpoucet.examples.GraphViewer.display;
import static ca.uqac.lif.petitpoucet.function.ComputableConnector.connect;

import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.circuit.Connectable.OutputPart;
import ca.uqac.lif.petitpoucet.function.CompositeFunction;
import ca.uqac.lif.petitpoucet.function.Numbers;

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
 * One can see that the circuit's output depends only on the value of z.
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
	public static void main(String[] args) throws ExplanationException
	{
		CompositeFunction c = new CompositeFunction(3, 1, "(x+y)×z");
		Numbers.Addition a = new Numbers.Addition(2);
		Numbers.Multiplication m = new Numbers.Multiplication(2);
		c.add(a, m);
		c.associateInput(0, a, 0);
		c.associateInput(1, a, 1);
		c.associateInput(2, m, 1);
		connect(a, 0, m, 0);
		c.associateOutput(0, m, 0);
		Number result = (Number) c.evaluate(2, 3, 0);
		System.out.println(result);
		Vertex full_graph = c.explain(OutputPart.FIRST);
		display(full_graph);
	}
}
