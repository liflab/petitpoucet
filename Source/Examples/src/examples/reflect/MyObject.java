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

import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.petitpoucet.function.Equals;
import ca.uqac.lif.petitpoucet.function.Equals.ExplainableEquals;
import ca.uqac.lif.petitpoucet.function.Fork;
import ca.uqac.lif.petitpoucet.function.booleans.And;
import ca.uqac.lif.petitpoucet.function.reflect.GetField;
import ca.uqac.lif.petitpoucet.function.reflect.InstanceOf;

import static ca.uqac.lif.dag.NodeConnector.connect;

/**
 * A simple class used in examples to illustrate functions related to
 * reflection. The class contains two fields called <tt>foo</tt> (a string)
 * and <tt>bar</tt> (an integer).
 * <p>
 * Notably, the class implements the {@link ExplainableEquals} interface, which
 * provides a function used to determine if two objects are equal.
 * <p>
 * <img src="{@docRoot}/doc-files/reflect/MyClass-Equals.png" alt="Circuit" />
 * <p>
 * This function implements in the form of a circuit what the
 * {@link #equals(Object)} method does: it checks that the two objects are of
 * type {@link MyObject}, and then checks that their two fields <tt>foo</tt>
 * and <tt>bar</tt> are equal. Although considerably more verbose, this
 * function presents the advantage that a <tt>false</tt> result can be
 * explained, and point at the precise parts of the two objects that differ.
 * 
 * @see ObjectEquals
 */
public class MyObject implements ExplainableEquals
{
	/**
	 * The string field.
	 */
	protected String foo;
	
	/**
	 * The int field.
	 */
	protected int bar;
	
	/**
	 * Creates a new instance of the object.
	 * @param bar The int field
	 * @param foo The string field
	 */
	public MyObject(int bar, String foo)
	{
		super();
		this.bar = bar;
		this.foo = foo;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof MyObject))
		{
			return false;
		}
		MyObject m = (MyObject) o;
		return bar == m.bar && foo.compareTo(m.foo) == 0;
	}
	
	@Override
	public Circuit getEqualsFunction()
	{
		Circuit c = new Circuit(2, 1, "MyObject.=");
		Fork f1 = new Fork(2);
		InstanceOf o1 = new InstanceOf(MyObject.class);
		connect(f1, 0, o1, 0);
		Fork f1_1 = new Fork(2);
		connect(f1, 1, f1_1, 0);
		GetField foo1 = new GetField("foo");
		connect(f1_1, 0, foo1, 0);
		Fork f2 = new Fork(2);
		InstanceOf o2 = new InstanceOf(MyObject.class);
		connect(f2, 1, o2, 0);
		Fork f2_2 = new Fork(2);
		connect(f2, 1, f2_2, 0);
		GetField bar1 = new GetField("bar");
		connect(f2_2, 1, bar1, 0);
		GetField foo2 = new GetField("foo");
		connect(f2_2, 0, foo2, 0);
		GetField bar2 = new GetField("bar");
		connect(f1_1, 1, bar2, 0);
		Equals eq1 = new Equals();
		connect(foo1, 0, eq1, 0);
		connect(foo2, 0, eq1, 1);
		Equals eq2 = new Equals();
		connect(bar2, 0, eq2, 0);
		connect(bar1, 0, eq2, 1);
		And and1 = new And(2);
		connect(o1, 0, and1, 0);
		connect(eq1, 0, and1, 1);
		And and2 = new And(2);
		connect(eq2, 0, and2, 0);
		connect(o2, 0, and2, 1);
		And a = new And(2);
		connect(and1, 0, a, 0);
		connect(and2, 0, a, 1);
		c.addNodes(f1, f2, o1, o2, f1_1, f2_2, foo1, foo2, bar1, bar2, eq1, eq2, and1, and2, a);
		c.associateInput(0, f1.getInputPin(0));
		c.associateInput(1, f2.getInputPin(0));
		c.associateOutput(0, a.getOutputPin(0));
		return c;
	}
}