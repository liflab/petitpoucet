/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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

import static examples.util.GraphViewer.display;
import static ca.uqac.lif.dag.NodeConnector.connect;
import static ca.uqac.lif.petitpoucet.ComposedPart.compose;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.petitpoucet.function.Constant;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.number.IsGreaterThan;
import ca.uqac.lif.petitpoucet.function.vector.ElementAt;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;
import ca.uqac.lif.petitpoucet.function.vector.VectorApply;

import java.util.Arrays;
import java.util.List;

/**
 * Checks that the first element of each tuple in a list is positive.
 * The circuit that implements this calculation can be represented graphically
 * as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/vector/NestedVectors.png" alt="Circuit" />
 * @author Sylvain Hallé
 */
public class NestedVectors
{
	public static void main(String[] args)
	{
		VectorApply a = new VectorApply(new Circuit(1, 1) {{
			ElementAt n = new ElementAt(0);
			IsGreaterThan gt = new IsGreaterThan();
			connect(n, 0, gt, 0);
			connect(new Constant(1), 0, gt, 1);
			addNodes(n, gt);
			associateInput(0, n.getInputPin(0));
			associateOutput(0, gt.getOutputPin(0));
		}});
		Object list = a.evaluate(list(list(1, 2), list(0, 1)))[0];
		Node tree = a.getExplanation(compose(new NthElement(1), NthOutput.FIRST));
		display(tree);
	}

	protected static List<?> list(Object ... objects)
	{
		return Arrays.asList(objects);
	}
}
