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

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.petitpoucet.function.Constant;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.number.IsGreaterThan;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;
import ca.uqac.lif.petitpoucet.function.vector.VectorApply;
import ca.uqac.lif.petitpoucet.function.vector.VectorSum;
import ca.uqac.lif.petitpoucet.function.vector.Window;

import static examples.util.GraphViewer.display;

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
 * <img src="{@docRoot}/doc-files/circuits/AllPositive-full.png" alt="Circuit"></a>
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
	public static void main(String[] args)
	{
		Circuit all_positive = new Circuit(1, 1, "&forall;>0");
		{
			Window win = new Window(new VectorSum(), 2);
			Circuit gt_0 = new Circuit(1, 1, "&gt;0");
			{
				IsGreaterThan gt = new IsGreaterThan();
				Constant zero = new Constant(0);
				gt_0.addNodes(gt, zero);
				gt_0.associateInput(0, gt.getInputPin(0));
				NodeConnector.connect(zero, 0, gt, 1);
				gt_0.associateOutput(0, gt.getOutputPin(0));
			}
			VectorApply positive = new VectorApply(gt_0);
			all_positive.addNodes(win, positive);
			NodeConnector.connect(win, 0, positive, 0);
			all_positive.associateInput(0, win.getInputPin(0));
			all_positive.associateOutput(0, positive.getOutputPin(0));
		}
		Object result = all_positive.evaluate(Arrays.asList(1, -10, 30, -5, 3))[0];
		System.out.println(result);
		int position = 0;
		Node graph = all_positive.getExplanation(ComposedPart.compose(new NthElement(position), NthOutput.FIRST));
		display(graph);
	}
}
