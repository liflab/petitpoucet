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

import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.strings.Range;
import ca.uqac.lif.petitpoucet.function.strings.Replace;

import static examples.strings.StringExplainer.display;

/**
 * Applies a regex replacement to a string using capture groups.
 * <p>
 * The program starts with the string:
 * <pre>The quick brown fox jumps over the lazy dog.</pre>
 * <p>
 * It applies the following regex replacement:
 * <p>
 * <table border="1">
 * <tr><th>Find</th><th>Replace</th></tr>
 * <tr><td><tt>(brown|lazy) (fox|dog)</tt></td><td><tt>$2 $1</tt></td></tr>
 * </table>
 * <p>
 * resulting in the output string:
 * <pre><u>The quick fox</u> brown jumps over the dog lazy.</pre>
 * <p>
 * The program then asks for the explanation of the range of characters
 * 0 to 12 in the output (underlined above). It produces the following
 * explanation graph:
 * <p>
 * <img src="{@docRoot}/doc-files/strings/FindReplace3_tree.png" alt="Graph" />
 * <p>
 * As one can see, this part of the output string is composed of two
 * disjoint ranges in the input string. The function correctly keeps track
 * of the input locations corresponding to each capture group occurring
 * in the replacement pattern.
 * <p>
 * As another example, changing the range queried to the single character at
 * index 35 (<tt>new Range(35, 35)</tt>) rather produces this graph:
 * <p>
 * <img src="{@docRoot}/doc-files/strings/FindReplace3_tree_alt.png" alt="Graph" />
 * <p>
 * It effectively tracks the "d" of "dog" in the input.
 */
public class FindReplace3
{
	public static void main(String[] args)
	{
		String input = "The quick brown fox jumps over the lazy dog.";
		Replace r = new Replace("(brown|lazy) (fox|dog)", "$2 $1");
		String output = (String) r.evaluate(input)[0];
		System.out.println(output);
		PartNode full_graph = r.getExplanation(compose(new Range(0, 12), NthOutput.FIRST));
		display(full_graph, input, output);
	}
}
