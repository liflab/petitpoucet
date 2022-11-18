/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2022 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.function;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.dag.NestedNode;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.Circuit.CircuitInputPin;
import ca.uqac.lif.petitpoucet.function.number.Addition;
import ca.uqac.lif.petitpoucet.function.number.IsGreaterThan;
import ca.uqac.lif.petitpoucet.function.number.Multiplication;
import ca.uqac.lif.petitpoucet.function.number.Subtraction;

public class CircuitTest
{
	@Test
	public void test1()
	{
		Circuit c = new Circuit(2, 1);
		Addition a = new Addition(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateOutput(0, a.getOutputPin(0));
		Object[] out = c.evaluate(2, 3);
		assertEquals(5, ((Number) out[0]).intValue());
	}

	@Test
	public void test2()
	{
		Circuit c = new Circuit(2, 1);
		Subtraction a = new Subtraction(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateOutput(0, a.getOutputPin(0));
		Object[] out = c.evaluate(2, 3);
		assertEquals(-1, ((Number) out[0]).intValue());
	}

	@Test
	public void test4()
	{
		// This circuit calculates (x+y)*z
		Circuit c = new Circuit(3, 1);
		Addition a = new Addition(2);
		Multiplication m = new Multiplication(2);
		c.addNodes(a, m);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateInput(2, m.getInputPin(1));
		NodeConnector.connect(a, 0, m, 0);
		c.associateOutput(0, m.getOutputPin(0));
		Object[] out = c.evaluate(2, 3, 4);
		assertEquals(20, ((Number) out[0]).intValue());
	}

	@Test
	public void test3()
	{
		Circuit c = new Circuit(2, 1);
		Subtraction a = new Subtraction(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(1));
		c.associateInput(1, a.getInputPin(0));
		c.associateOutput(0, a.getOutputPin(0));
		Object[] out = c.evaluate(2, 3);
		assertEquals(1, ((Number) out[0]).intValue());
	}

	@Test
	public void test5()
	{
		Circuit gt_0 = new Circuit(1, 1);
		{
			IsGreaterThan gt = new IsGreaterThan();
			Constant zero = new Constant(0);
			gt_0.addNodes(gt, zero);
			gt_0.associateInput(0, gt.getInputPin(0));
			NodeConnector.connect(zero, 0, gt, 1);
			gt_0.associateOutput(0, gt.getOutputPin(0));
		}
		Circuit gt_0_dup = gt_0.duplicate();
		Boolean result = (Boolean) gt_0_dup.evaluate(1)[0];
		assertEquals(true, result);
	}

	@Test
	public void testReset1()
	{
		Circuit add = new Circuit(1, 1);
		{
			Fork f = new Fork(2);
			Addition a = new Addition(2);
			add.addNodes(f, a);
			NodeConnector.connect(f, 0, a, 0);
			NodeConnector.connect(f, 1, a, 1);
			add.associateInput(0, f.getInputPin(0));
			add.associateOutput(0, a.getOutputPin(0));
		}
		Number n = (Number) add.evaluate(1)[0];
		assertEquals(2, n.intValue());
		assertTrue(add.getOutputPin(0).isEvaluated());
		add.reset();
		assertFalse(add.getOutputPin(0).isEvaluated());
		n = (Number) add.evaluate(2)[0];
		assertEquals(4, n.intValue());
	}

	@Test
	public void testDuplicate1()
	{
		Circuit c = new Circuit(2, 1);
		Addition a = new Addition(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateOutput(0, a.getOutputPin(0));
		c.evaluate(0, 0);
		Circuit c_dup = c.duplicate();
		Object[] out = c_dup.evaluate(2, 3);
		assertEquals(5, ((Number) out[0]).intValue());
	}

	@Test
	public void testDuplicate2()
	{
		Circuit c = new Circuit(2, 1);
		Multiplication a = new Multiplication(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateOutput(0, a.getOutputPin(0));
		Circuit c_dup = c.duplicate();
		Object[] out = c_dup.evaluate(2, 3);
		assertEquals(6, ((Number) out[0]).intValue());
	}

	@Test
	public void testDuplicate3()
	{
		// This circuit calculates (x+y)*z
		Circuit c = new Circuit(3, 1);
		Addition a = new Addition(2);
		Multiplication m = new Multiplication(2);
		c.addNodes(a, m);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateInput(2, m.getInputPin(1));
		NodeConnector.connect(a, 0, m, 0);
		c.associateOutput(0, m.getOutputPin(0));
		Circuit c_dup = c.duplicate();
		Object[] out = c_dup.evaluate(2, 3, 4);
		assertEquals(20, ((Number) out[0]).intValue());
	}

	@Test
	public void testDuplicate4()
	{
		// This circuit calculates x > 3
		Circuit c = new Circuit(1, 1);
		IsGreaterThan gt = new IsGreaterThan();
		Constant three = new Constant(3);
		c.associateInput(0, gt.getInputPin(0));
		NodeConnector.connect(three, 0, gt, 1);
		c.associateOutput(0, gt.getOutputPin(0));
		c.addNodes(three, gt);
		Circuit c_dup = c.duplicate();
		Object[] out = c_dup.evaluate(3);
		assertFalse(((Boolean) out[0]).booleanValue());
	}

	@Test
	public void testDuplicate5()
	{
		// This circuit calculates x > 3
		Circuit c = new Circuit(1, 1);
		Identity a = new Identity(1);
		IsGreaterThan gt = new IsGreaterThan();
		Constant three = new Constant(3);
		c.associateInput(0, a.getInputPin(0));
		NodeConnector.connect(a, 0, gt, 0);
		NodeConnector.connect(three, 0, gt, 1);
		c.associateOutput(0, gt.getOutputPin(0));
		c.addNodes(a, three, gt);
		Circuit c_dup = c.duplicate();
		Object[] out = c_dup.evaluate(3);
		assertFalse(((Boolean) out[0]).booleanValue());
	}

	@Test
	public void testExplain1()
	{
		Circuit c = new Circuit(2, 1);
		Addition a = new Addition(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateOutput(0, a.getOutputPin(0));
		c.evaluate(2, 3);
		PartNode root = c.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		Pin<? extends Node> p1 = (Pin<? extends Node>) root.getOutputLinks(0).get(0);
		NestedNode nn = (NestedNode) p1.getNode();
		assertEquals(2, nn.getOutputArity());
		{
			PartNode pn_in = (PartNode) nn.getOutputLinks(0).get(0).getNode();
			assertEquals(c, pn_in.getSubject());
			assertEquals(NthInput.FIRST, pn_in.getPart());
		}
		{
			PartNode pn_in = (PartNode) nn.getOutputLinks(1).get(0).getNode();
			assertEquals(c, pn_in.getSubject());
			assertEquals(NthInput.SECOND, pn_in.getPart());
		}
	}

	@Test
	public void testExplain2()
	{
		Circuit c = new Circuit(2, 1);
		Multiplication a = new Multiplication(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateOutput(0, a.getOutputPin(0));
		c.evaluate(2, 0);
		PartNode root = c.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		Pin<? extends Node> p1 = (Pin<? extends Node>) root.getOutputLinks(0).get(0);
		NestedNode nn = (NestedNode) p1.getNode();
		assertEquals(1, nn.getOutputArity());
		{
			PartNode pn_in = (PartNode) nn.getOutputLinks(0).get(0).getNode();
			assertEquals(c, pn_in.getSubject());
			assertEquals(NthInput.SECOND, pn_in.getPart());
		}
	}

	@Test
	public void testExplain3()
	{
		// This circuit calculates (x+y)*z
		Circuit c = new Circuit(3, 1);
		Addition a = new Addition(2);
		Multiplication m = new Multiplication(2);
		c.addNodes(a, m);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateInput(2, m.getInputPin(1));
		NodeConnector.connect(a, 0, m, 0);
		c.associateOutput(0, m.getOutputPin(0));
		c.evaluate(2, 3, 4);
		PartNode root = c.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		Pin<? extends Node> p1 = (Pin<? extends Node>) root.getOutputLinks(0).get(0);
		NestedNode nn = (NestedNode) p1.getNode();
		boolean[] inputs = new boolean[3];
		assertEquals(3, nn.getOutputArity());
		for (int i = 0; i < 3; i++)
		{
			PartNode pn_in = (PartNode) nn.getOutputLinks(i).get(0).getNode();
			assertEquals(c, pn_in.getSubject());
			inputs[((NthInput) pn_in.getPart()).getIndex()] = true;
		}
		assertTrue(inputs[0]);
		assertTrue(inputs[1]);
		assertTrue(inputs[2]);
	}

	@Test
	public void testExplain4()
	{
		// This circuit calculates (x+y)*z
		Circuit c = new Circuit(3, 1);
		Addition a = new Addition(2);
		Multiplication m = new Multiplication(2);
		c.addNodes(a, m);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateInput(2, m.getInputPin(1));
		NodeConnector.connect(a, 0, m, 0);
		c.associateOutput(0, m.getOutputPin(0));
		c.evaluate(2, 3, 0);
		PartNode root = c.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		Pin<? extends Node> p1 = (Pin<? extends Node>) root.getOutputLinks(0).get(0);
		NestedNode nn = (NestedNode) p1.getNode();
		assertEquals(1, nn.getOutputArity());
		PartNode pn_in = (PartNode) nn.getOutputLinks(0).get(0).getNode();
		assertEquals(c, pn_in.getSubject());
		assertEquals(NthInput.THIRD, pn_in.getPart());
	}

	@Test
	public void testExplain5()
	{
		// This circuit calculates (x+y)*z
		Circuit c = new Circuit(3, 1);
		Addition a = new Addition(2);
		Multiplication m = new Multiplication(2);
		c.addNodes(a, m);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateInput(2, m.getInputPin(1));
		NodeConnector.connect(a, 0, m, 0);
		c.associateOutput(0, m.getOutputPin(0));
		c.evaluate(-2, 2, 5);
		PartNode root = c.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		Pin<? extends Node> p1 = (Pin<? extends Node>) root.getOutputLinks(0).get(0);
		NestedNode nn = (NestedNode) p1.getNode();
		boolean[] inputs = new boolean[3];
		assertEquals(2, nn.getOutputArity());
		for (int i = 0; i < 2; i++)
		{
			PartNode pn_in = (PartNode) nn.getOutputLinks(i).get(0).getNode();
			assertEquals(c, pn_in.getSubject());
			inputs[((NthInput) pn_in.getPart()).getIndex()] = true;
		}
		assertTrue(inputs[0]);
		assertTrue(inputs[1]);
		assertFalse(inputs[2]);
	}

	@Test
	public void testSetValue1()
	{
		// This circuit calculates (x+y)*z
		Circuit c = new Circuit(3, 1);
		Addition a = new Addition(2);
		Multiplication m = new Multiplication(2);
		CircuitInputPin cip = c.getInputPin(0);
		cip.setValue(42);
		assertEquals(true, cip.isEvaluated());
		assertEquals(42, cip.getValue());
		c.addNodes(a, m);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateInput(2, m.getInputPin(1));
		NodeConnector.connect(a, 0, m, 0);
		c.associateOutput(0, m.getOutputPin(0));
	}
}
