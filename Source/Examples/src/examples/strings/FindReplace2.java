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
import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.strings.Range;
import ca.uqac.lif.petitpoucet.function.strings.Replace;

import static ca.uqac.lif.dag.NodeConnector.connect;
import static examples.strings.StringExplainer.display;

/**
 * Applies a succession of two find/replace operations on a string.
 * <p>
 * The program starts with the string:
 * <pre>The quick brown fox jumps over the lazy dog.</pre>
 * <p>
 * It applies the following two replacements:
 * <p>
 * <table border="1">
 * <tr><th>Find</th><th>Replace</th></tr>
 * <tr><td><tt>fox</tt></td><td><tt>grizzly bear</tt></td></tr>
 * <tr><td><tt>lazy dog</tt></td><td><tt>valiant cat</tt></td></tr>
 * </table>
 * <p>
 * resulting in the output string:
 * <pre>The quick brown <u>grizzly bear</u> jumps over the valiant cat.</pre>
 * <p>
 * The program then asks for the explanation of the range of characters
 * 16 to 27 in the output (underlined above). It produces the following
 * explanation graph:
 * <p>
 * <img src="{@docRoot}/doc-files/strings/FindReplace2_tree.png" alt="Graph" />
 * <p>
 * The substring "grizzly bear" is indeed the result of applying a replacement
 * to the word "fox" in the input string.
 * <p>
 * As one can see, this part of the output string is composed of two
 * disjoint ranges in the input string. The function correctly keeps track
 * of the input locations corresponding to each capture group occurring
 * in the replacement pattern.
 * <p>
 * Since the replacement string is not made of parts of the input, it is
 * not possible to retrace sub-ranges of the replacement individually.
 * Therefore, changing the range queried to the single character at
 * index 16 (<tt>new Range(16, 16)</tt>) produces this graph:
 * <p>
 * <img src="{@docRoot}/doc-files/strings/FindReplace2_tree_alt.png" alt="Graph" />
 * <p>
 * The "g" of "grizzly bear" is associated to the whole word "fox".
 */
public class FindReplace2
{
	public static void main(String[] args)
	{
		String input = "The quick brown fox jumps over the lazy dog.";
		Circuit c = new Circuit(1, 1, "Double replace");
		{
			Replace r1 = new Replace("fox", "grizzly bear");
			Replace r2 = new Replace("lazy dog", "valiant cat");
			connect(r1, 0, r2, 0);
			c.associateInput(0, r1.getInputPin(0));
			c.associateOutput(0, r2.getOutputPin(0));
		}
		String output = (String) c.evaluate(input)[0];
		System.out.println(output);
		PartNode full_graph = c.getExplanation(compose(new Range(16, 16), NthOutput.FIRST));
		display(full_graph, input, output);
	}
}
