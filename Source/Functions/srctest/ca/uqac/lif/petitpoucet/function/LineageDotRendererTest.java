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
package ca.uqac.lif.petitpoucet.function;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.number.Addition;
import ca.uqac.lif.petitpoucet.function.number.Multiplication;

public class LineageDotRendererTest
{
	@Test
	public void testRender1()
	{
		// This circuit calculates (x+y)*z
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		Circuit c = new Circuit(3, 1, "(x+y)×z");
		Addition a = new Addition(2);
		Multiplication m = new Multiplication(2);
		c.addNodes(a, m);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateInput(2, m.getInputPin(1));
		NodeConnector.connect(a, 0, m, 0);
		c.associateOutput(0, m.getOutputPin(0));
		c.evaluate(-2, 2, 0);
		PartNode root = c.getExplanation(NthOutput.FIRST);
		LineageDotRenderer renderer = new LineageDotRenderer(root);
		renderer.render(ps);
		assertNotNull(baos.toString());
	}
}
