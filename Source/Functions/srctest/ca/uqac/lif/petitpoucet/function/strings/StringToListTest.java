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
package ca.uqac.lif.petitpoucet.function.strings;

import static org.junit.Assert.*;

import java.util.List;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;
import org.junit.Test;

public class StringToListTest {
    @Test
    public void testStringToList1() {
        StringToList stl = new StringToList();
        List<?> result = (List<?>) stl.evaluate("foobar")[0];
        assertEquals(6, result.size());
        assertEquals("f", result.get(0));
        assertEquals("o", result.get(1));
        assertEquals("o", result.get(2));
        assertEquals("b", result.get(3));
        assertEquals("a", result.get(4));
        assertEquals("r", result.get(5));
    }

    @Test
    public void testStringToListExplanation1() {
        StringToList stl = new StringToList();
        stl.evaluate("foobar");
        PartNode root = stl.getExplanation(ComposedPart.compose(new NthElement(0), NthOutput.FIRST));
        assertEquals(1, root.getOutputLinks(0).size());
        PartNode pn1 = (PartNode) root.getOutputLinks(0).get(0).getNode();
        assertEquals(ComposedPart.compose(new Range(0, 0), NthInput.FIRST), pn1.getPart());
        assertEquals(stl, pn1.getSubject());
    }

    @Test
    public void testStringToListExplanation2() {
        StringToList stl = new StringToList();
        stl.evaluate("foobar");
        PartNode root = stl.getExplanation(ComposedPart.compose(new NthElement(1), NthOutput.FIRST));
        assertEquals(1, root.getOutputLinks(0).size());
        PartNode pn1 = (PartNode) root.getOutputLinks(0).get(0).getNode();
        assertEquals(ComposedPart.compose(new Range(1, 1), NthInput.FIRST), pn1.getPart());
        assertEquals(stl, pn1.getSubject());
    }

    @Test
    public void testStringToListExplanation3() {
        StringToList stl = new StringToList();
        stl.evaluate("foobar");
        PartNode root = stl.getExplanation(ComposedPart.compose(new NthElement(2), NthOutput.FIRST));
        assertEquals(1, root.getOutputLinks(0).size());
        PartNode pn1 = (PartNode) root.getOutputLinks(0).get(0).getNode();
        assertEquals(ComposedPart.compose(new Range(2, 2), NthInput.FIRST), pn1.getPart());
        assertEquals(stl, pn1.getSubject());
    }
}
