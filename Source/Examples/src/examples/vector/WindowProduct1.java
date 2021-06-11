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

import java.util.Arrays;
import java.util.List;

import static ca.uqac.lif.petitpoucet.ComposedPart.compose;
import static examples.util.GraphViewer.display;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;
import ca.uqac.lif.petitpoucet.function.vector.VectorProduct;
import ca.uqac.lif.petitpoucet.function.vector.Window;

/**
 * Evaluates the product of elements of a sliding window of width 4, on a
 * vector that contains no null values.
 * Graphically, this is represented by the following circuit:
 * <p>
 * <img src="{@docRoot}/doc-files/vector/WindowProduct.png" alt="Circuit">
 * 
 * <h3>Explanation</h3>
 * 
 * The program evaluates this circuit on the input
 * [3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5], and then requests the explanation graph
 * corresponding to the second element of the resulting output vector.
 * 
 * <h4>Full graph</h4>
 * The full explanation graph corresponding to that explanation is the
 * following. 
 * <p>
 * <img src="{@docRoot}/doc-files/vector/WindowProduct1-full.png" alt="Full graph">
 * <p>
 * It shows that the output is explained by the four elements of the input vector
 * at positions 1 to 4 (i.e. from the second to the fifth).
 * 
 * <h4>Simplified graph</h4>
 * 
 * The simplified graph only keeps the leaves and intermediate Boolean nodes,
 * but conveys the same information:
 * <p>
 * <img src="{@docRoot}/doc-files/vector/WindowProduct1-small.png" alt="Simplified graph">
 * @author Sylvain Hallé
 * @see WindowProduct2
 *
 */
public class WindowProduct1
{
	public static void main(String[] args)
	{
		Window w = new Window(new VectorProduct(), 4);
		List<?> result = (List<?>) w.evaluate(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5))[0];
		System.out.println(result);
		Node full_graph = w.getExplanation(compose(new NthElement(1), NthOutput.FIRST));
		display(full_graph);
	}
}
