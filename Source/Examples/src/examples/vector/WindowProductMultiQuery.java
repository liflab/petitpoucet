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
package examples.vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;
import ca.uqac.lif.petitpoucet.function.vector.VectorProduct;
import ca.uqac.lif.petitpoucet.function.vector.Window;

import static ca.uqac.lif.petitpoucet.ComposedPart.compose;
import static ca.uqac.lif.petitpoucet.function.FunctionLineageGraphUtilities.simplify;
import static examples.util.GraphViewer.display;

/**
 * Evaluates the product of elements of a sliding window of width 3, and
 * requests the explanation of all elements of the output vector in a single
 * graph. Graphically, this is represented by the following circuit:
 * <p>
 * <img src="{@docRoot}/doc-files/vector/WindowProduct.png" alt="Circuit">
 * 
 * <h3>Explanation</h3>
 * 
 * The program evaluates this circuit on the input
 * [1, 0, 0, 3, 4, 5, 0, 6, 7], and then requests the explanation graph
 * corresponding to each element of the resulting output vector. All these
 * explanations are merged into the same lineage graph. This is done by
 * successively requesting the explanation for each output element, and
 * passing the same {@link NodeFactory} instance on each call. Thus, graph
 * nodes created for one explanation can be reused if they appear in another
 * explanation, resulting in a "merged" graph of all individual explanations.
 * 
 * <h4>Full graph</h4>
 * 
 * The full explanation graph corresponding to that explanation is the
 * following. 
 * <p>
 * <img src="{@docRoot}/doc-files/vector/WindowProductMultiQuery-full.png" alt="Full graph">
 * <p>
 * One can see that the graph has multiple roots, one for the explanation of
 * each element of the output vector. Some explanations also have nodes in common.
 * 
 * <h4>Simplified graph</h4>
 * 
 * The simplified graph only keeps the leaves and intermediate Boolean nodes,
 * but conveys the same information:
 * <p>
 * <img src="{@docRoot}/doc-files/vector/WindowProductMultiQuery-small.png" alt="Simplified graph">
 * <p>
 * Some other observations can be made on this graph:
 * <ul>
 * <li>Both the first and second elements of the input vector (the two green
 * nodes at the upper left) have the same two alternate explanations: the
 * second and third elements of the input vector. Indeed, these two elements
 * are null, and either of them suffices for the whole window to evaluate to
 * zero.</li>
 * <li>The value of the 5th to 7th elements of the output vector (the three
 * green nodes at the upper right) are all explained by the same input element.</li>
 * <li>The first element of the input vector ([0] ∘ ↑₀), as well as the
 * last two, are not involved in the explanation of any output element. That is,
 * changing their value would not have any impact on the output.</li>
 * </ul>
 */
public class WindowProductMultiQuery
{
	public static void main(String[] args)
	{
		Window win = new Window(new VectorProduct(), 3);
		List<?> result = (List<?>) win.evaluate(Arrays.asList(1, 0, 0, 3, 4, 5, 0, 6, 7))[0];
		System.out.println(result);
		NodeFactory factory = NodeFactory.getFactory();
		List<Node> roots = new ArrayList<>();
		for (int i = 0; i < result.size(); i++)
		{
			roots.add(win.getExplanation(compose(new NthElement(i), NthOutput.FIRST), factory));
		}
		display(simplify(roots));
	}
}
