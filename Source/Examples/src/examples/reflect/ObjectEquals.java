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
package examples.reflect;

import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.Equals;
import ca.uqac.lif.petitpoucet.function.NthOutput;

import static ca.uqac.lif.petitpoucet.function.FunctionLineageGraphUtilities.simplify;
import static examples.util.GraphViewer.display;

import java.io.IOException;

/**
 * Asserts that two objects are equal using the {@link Equals} function, and
 * examines the explanation in case the result is false.
 * <p>
 * You can experiment with the ways in which the two objects are instantiated
 * to see the impact on the explanation graph. For example, with the arguments
 * (3, "hello") and (4, "hello"), respectively, the following graph is
 * produced:
 * <p>
 * <img src="{@docRoot}/doc-files/reflect/ObjectEquals-bar.png" alt="Explanation graph" />
 * <p>
 * One can see that the objects differ in the value of their field
 * <tt>bar</tt>, which is what the graph illustrates.
 * <p>
 * If the two objects are instantiated with the arguments (3, "hello") and
 * (3, "Hello"), one rather gets:
 * <p>
 * <img src="{@docRoot}/doc-files/reflect/ObjectEquals-foo.png" alt="Explanation graph" />
 * <p>
 * This time, the objects differ in the value of their field <tt>bar</tt>. The
 * graph also points to the specific part of this field that differs; the part
 * "I0" designates the first character of the string.
 * <p>
 * Finally, objects can differ in multiple ways. Using the arguments
 * (3, "hello") and (4, "HellO"), one obtains:
 * <p>
 * <img src="{@docRoot}/doc-files/reflect/ObjectEquals-foobar.png" alt="Explanation graph" />
 * <p>
 * This time, three differences are identified: the first character of field
 * <tt>foo</tt>, the fourth and fifth characters of <tt>foo</tt>, and the
 * numerical value of <tt>bar</tt>. Any of these three differences is
 * sufficient to conclude that the objects are different, so each pair of
 * "evidence" is located as alternatives under an "or" node.
 */
public class ObjectEquals
{
	public static void main(String[] args) throws IOException
	{
		MyObject o1 = new MyObject(3, "hello");
		MyObject o2 = new MyObject(4, "HelLO");
		Equals eq = new Equals();
		boolean b = (Boolean) eq.evaluate(o1, o2)[0];
		System.out.println("Objects are " + (b ? "equal" : "different"));
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		display(simplify(root));
	}
}
