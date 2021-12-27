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
 * Applies a succession of a find/replace operation on a string.
 * <p>
 * The program starts with the string:
 * <pre>The quick brown fox jumps over the lazy dog.</pre>
 * <p>
 * It replaces "fox" by "tiger", resulting in the output string:
 * <pre>The quick brown tiger <u>jumps</u> over the lazy dog.</pre>
 * <p>
 * The program then asks for the explanation of the range of characters
 * 22 to 26 in the output (underlined above). It produces the following
 * explanation graph:
 * <p>
 * <img src="{@docRoot}/doc-files/strings/FindReplace1_tree.png" alt="Graph" />
 * <p>
 * It shows that the position of "jumps" in the output string is different
 * from the output, due to the shift caused by the replacement of "fox" by
 * longer string "tiger".
 */
public class FindReplace1
{
	public static void main(String[] args)
	{
		String input = "The quick brown fox jumps over the lazy dog.";
		Replace r = new Replace("fox", "tiger");
		String output = (String) r.evaluate(input)[0];
		System.out.println(output);
		PartNode full_graph = r.getExplanation(compose(new Range(22, 26), NthOutput.FIRST));
		display(full_graph, input, output);
	}
}
