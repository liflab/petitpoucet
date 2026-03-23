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
package ca.uqac.lif.petitpoucet.examples.circuit;

import static ca.uqac.lif.petitpoucet.examples.GraphViewer.display;

import java.util.Arrays;

import static ca.uqac.lif.petitpoucet.function.ComputableConnector.connect;

import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.ConcreteVertex;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.circuit.Connectable.OutputPart;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.function.CompositeFunction;
import ca.uqac.lif.petitpoucet.function.Constant;
import ca.uqac.lif.petitpoucet.function.Lists;
import ca.uqac.lif.petitpoucet.function.Numbers;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.ParameterizedFunction;
import ca.uqac.lif.petitpoucet.function.Lists.Window;

/**
 * From a vector of numbers, evaluates if the sum of any two successive
 * elements is positive. Graphically, this circuit can
 * be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/circuits/AllPositive.png" alt="Circuit">
 * 
 * <h3>Explanation</h3>
 * 
 * The circuit is evaluated on the input vector [1, -10, 30, -5, 3], and an
 * explanation is requested for the first element of the output vector (which
 * is false).
 * 
 * <h4>Full graph</h4>
 * 
 * The full explanation graph is this one:
 * <p>
 * <img src="{@docRoot}/doc-files/circuits/AllPositive-full.png" alt="Circuit">
 * 
 * <h4>Simplified graph</h4>
 * The simplified explanation graph is the following:
 * <p>
 * <img src="{@docRoot}/doc-files/circuits/AllPositive-small.png" alt="Simplified circuit">
 * <p>
 * It shows that the output is explained by the first and second elements of
 * the input vector (i.e. at positions 0 and 1).
 * @author Sylvain Hallé
 *
 */
public class AllPositive
{
	public static void main(String[] args) throws ExplanationException
	{
		CompositeFunction all_positive = new CompositeFunction(1, 1, "all");
		{
			CompositeFunction add = new CompositeFunction(2, 1, "add");
			{
				Numbers.Addition a = new Numbers.Addition(2);
				add.add(a);
				add.associateInput(0, a, 0);
				add.associateInput(1, a, 1);
				add.associateOutput(0, a, 0);
			}
			ParameterizedFunction w = new Window(2, add);
			CompositeFunction gt_0 = new CompositeFunction(1, 1, ">0");
			{
				AtomicFunction g = new Numbers.IsGreaterThan();
				AtomicFunction z = new Constant(0);
				connect(z, 0, g, 1);
				gt_0.add(g, z);
				gt_0.associateInput(0, g, 0);
				gt_0.associateOutput(0, g, 0);
			}
			ParameterizedFunction a = new Lists.Apply(gt_0);
			connect(w, 0, a, 0);
			all_positive.add(w, a);
			all_positive.associateInput(0, w, 0);
			all_positive.associateOutput(0, a, 0);
		}
		Object result = all_positive.evaluate(Arrays.asList(1, -10, 30, -5, 3));
		System.out.println(result);
		Vertex full_graph = all_positive.explain(CompositePart.compose(new Lists.NthElement(0), OutputPart.FIRST));
		ConcreteVertex v_e = Vertex.get(full_graph);
		display(v_e);
	}

}
