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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.petitpoucet.function.Equals;
import ca.uqac.lif.petitpoucet.function.Equals.ExplainableEquals;
import ca.uqac.lif.petitpoucet.function.Fork;
import ca.uqac.lif.petitpoucet.function.booleans.And;
import ca.uqac.lif.petitpoucet.function.reflect.GetField;

import static ca.uqac.lif.dag.NodeConnector.connect;

/**
 * A class creating objects used in the {@link NestedObjectEquals} code
 * example. This class has two fields: a string and a list whose elements
 * are themselves instances of the class {@link MySimpleObject}.
 * <p>
 * As with {@link MySimpleObject}, this class implements the 
 * {@link ExplainableEquals} interface. Its method {@link #getEqualsFunction()}
 * generates a function circuit that can be used to test the equality between
 * two objects of the class.
 */
public class MyNestedObject implements ExplainableEquals
{
	/**
	 * The string field.
	 */
	protected String name;
	
	/**
	 * The list field.
	 */
	protected List<MySimpleObject> list;
	
	/**
	 * Creates a new instance of nested object and gives it a name.
	 * @param name The name
	 */
	public MyNestedObject(String name)
	{
		super();
		this.name = name;
		this.list = new ArrayList<MySimpleObject>();
	}
	
	/**
	 * Adds elements to the inner list of this object.
	 * @param objects The elements to add
	 * @return This object
	 */
	public MyNestedObject add(MySimpleObject ...objects)
	{
		for (MySimpleObject o : objects)
		{
			this.list.add(o);
		}
		return this;
	}

	@Override
	public Circuit getEqualsFunction()
	{
		Circuit c = new Circuit(2, 1);
		Fork f1 = new Fork(2);
		GetField name1 = new GetField("name");
		connect(f1, 0, name1, 0);
		GetField list1 = new GetField("list");
		connect(f1, 1, list1, 0);
		Fork f2 = new Fork(2);
		GetField name2 = new GetField("name");
		connect(f2, 0, name2, 0);
		GetField list2 = new GetField("list");
		connect(f2, 1, list2, 0);
		Equals eq1 = new Equals();
		connect(name1, 0, eq1, 0);
		connect(name2, 0, eq1, 1);
		Equals eq2 = new Equals();
		connect(list1, 0, eq2, 0);
		connect(list2, 0, eq2, 1);
		And a = new And(2);
		connect(eq1, 0, a, 0);
		connect(eq2, 0, a, 1);
		c.addNodes(f1, f2, name1, name2, list1, list2, eq1, eq2, a);
		c.associateInput(0, f1.getInputPin(0));
		c.associateInput(1, f2.getInputPin(0));
		c.associateOutput(0, a.getOutputPin(0));
		return c;
	}
}
