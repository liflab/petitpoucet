package ca.uqac.lif.petitpoucet.function.vector;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

public class NthElementTest
{
	@Test
	public void testReplace1()
	{
		Part cd = ComposedPart.create(new NthElement(0), NthOutput.FIRST);
		Part d = NthElement.replaceNthOutputByNthInput(cd, 10);
		assertTrue(d instanceof ComposedPart);
		ComposedPart n_cd = (ComposedPart) d;
		assertEquals(2, n_cd.size());
		assertEquals(new NthElement(10), n_cd.get(0));
		assertEquals(NthInput.FIRST, n_cd.get(1));
	}
	
	@Test
	public void testReplace2()
	{
		Part cd = ComposedPart.create(new NthElement(0), new NthElement(25), NthOutput.FIRST);
		Part d = NthElement.replaceNthOutputByNthInput(cd, 10);
		assertTrue(d instanceof ComposedPart);
		ComposedPart n_cd = (ComposedPart) d;
		assertEquals(3, n_cd.size());
		assertEquals(new NthElement(0), n_cd.get(0));
		assertEquals(new NthElement(10), n_cd.get(1));
		assertEquals(NthInput.FIRST, n_cd.get(2));
	}
	
	@Test
	public void testReplace3()
	{
		Part cd = ComposedPart.create(NthInput.FIRST, NthOutput.FIRST);
		Part d = NthElement.replaceNthOutputByNthInput(cd, 10);
		assertEquals(cd, d);
	}
}
