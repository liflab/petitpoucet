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
 * A more complex version of {@link SimpleObjectEquals}, using objects that
 * have nested member fields. As in the other example, this program checks
 * the equality between two objects and gives an explanation in case they are
 * not equal.
 * <p>
 * The explanation differs depending on the member fields that cause the
 * difference. In the code of this example, the explanation graph is the
 * following one:
 * <p>
 * <img src="{@docRoot}/doc-files/reflect/NestedObjectEquals-explanation.png" alt="Explanation graph" />
 * <p>
 * One can see that the objects differ in two locations:
 * <ol>
 * <li>the first character (I0) of field <tt>name</tt> in both objects</li>
 * <li>the second and third characters (I1-2) of field <tt>foo</tt> of the
 * second element of field <tt>objects</tt></li>
 * </ol>
 * As one can see in the latter case, the explanation can pinpoint the precise
 * location of the difference across multiple nesting levels of member fields.
 * This is possible as long as all objects along the way implement the
 * {@link ExplainableEquals} interface.
 * 
 * @see SimpleObjectEquals
 */
public class NestedObjectEquals
{
	public static void main(String[] args) throws IOException
	{
		MyNestedObject o1 = new MyNestedObject("Object")
				.add(new MySimpleObject(3, "foo"), new MySimpleObject(42, "bar"));
		MyNestedObject o2 = new MyNestedObject("object")
				.add(new MySimpleObject(3, "foo"), new MySimpleObject(42, "bAR"));
		Equals eq = new Equals();
		boolean b = (Boolean) eq.evaluate(o1, o2)[0];
		System.out.println("Objects are " + (b ? "equal" : "different"));
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		display(simplify(root));
	}
}
