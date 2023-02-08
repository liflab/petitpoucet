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

import java.util.Arrays;
import java.util.List;

import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.vector.VectorEquals;

import static ca.uqac.lif.petitpoucet.function.FunctionLineageGraphUtilities.simplify;
import static examples.util.GraphViewer.display;

public class VectorComparison1
{
	public static void main(String[] args)
	{
		List<List<Integer>> list1 = Arrays.asList(
				Arrays.asList(1, 2), Arrays.asList(3, 4));
		List<List<Integer>> list2 = Arrays.asList(
				Arrays.asList(1, 3), Arrays.asList(4, 4));
		VectorEquals eq = new VectorEquals();
		boolean b = (Boolean) eq.evaluate(list1, list2)[0];
		System.out.println("The lists are " + (b ? "equal" : "different"));
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		display(simplify(root));
	}

}
