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
package examples.strings;

import static ca.uqac.lif.petitpoucet.ComposedPart.compose;
import static examples.strings.StringExplainer.display;

import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.strings.InsertAt;
import ca.uqac.lif.petitpoucet.function.strings.Range;

/**
 * Inserts a fixed string at a position inside an input string.
 * <p>
 * The program starts with the string:
 * <pre>The quick brown fox jumps over the lazy dog.</pre>
 * <p>
 * It inserts "big " at position 35 resulting in the output string:
 * <pre>The quick brown tiger jumps over the big <u>lazy</u> dog.</pre>
 * <p>
 * The program then asks for the explanation of the range of characters
 * 39 to 42 in the output (underlined above). It produces the following
 * explanation graph:
 * <p>
 * <img src="{@docRoot}/doc-files/strings/InsertAt1_tree.png" alt="Graph" />
 * <p>
 * It shows that the position of the word "lazy" is correctly tracked back
 * to its original position in the input string (indices 35 to 38).
 * <p>
 * On the contrary, the word "big" is nowhere to be found in the input
 * string. Therefore, asking for the provenance of the range of
 * characters 35 to 37 in the output results in "nothing" in the input:
 * <p>
 * <img src="{@docRoot}/doc-files/strings/InsertAt1_tree_alt.png" alt="Graph" />
 */
public class Insert1
{

	public static void main(String[] args)
	{
		String input = "The quick brown fox jumps over the lazy dog.";
		InsertAt r = new InsertAt("big ", 35);
		String output = (String) r.evaluate(input)[0];
		System.out.println(output);
		//PartNode full_graph = r.getExplanation(compose(new Range(39, 42), NthOutput.FIRST));
		PartNode full_graph = r.getExplanation(compose(new Range(35, 37), NthOutput.FIRST));
		display(full_graph, input, output);
	}

}
