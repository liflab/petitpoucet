/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.enumerate;

import static ca.uqac.lif.petitpoucet.function.ComputableConnector.connect;

import java.io.IOException;

import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.function.CompositeFunction;
import ca.uqac.lif.petitpoucet.function.Fork;
import ca.uqac.lif.petitpoucet.function.Numbers;
import ca.uqac.lif.petitpoucet.render.GraphViewer;
import ca.uqac.lif.synthia.Bounded;
import ca.uqac.lif.synthia.enumerative.AllPickers;
import ca.uqac.lif.synthia.sequence.Playback;

public class Main
{
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException
	{
		Enumerator e = new Enumerator(polynomial1a());
		Bounded<? extends Object>[] all = new Bounded[2]; 
		all[0] = new Playback<Number>(new Number[] {2}).setLoop(false); //getNumberPicker(5);
		all[1] = new Playback<Number>(new Number[] {3}).setLoop(false); //getNumberPicker(5);
		AllPickers listargs = new AllPickers(all); 
		e.enumerate(listargs);
		System.out.println(e.getExplanations().size());
		int i = 0;
		for (Vertex v : e.getExplanations())
		{
			i++;
			GraphViewer.save(v, "/tmp/" + i + ".png", false);
		}
	}
	
	protected static Bounded<? extends Object> getNumberPicker(int range)
	{
		Number[] vals = new Number[range];
		for (int i = 0; i < range; i++)
		{
			vals[i] = i;
		}
		Playback<Number> p = new Playback<>(vals);
		p.setLoop(false);
		return p;
	}
	
	/**
	 * Computes a² − ab.
	 * @return
	 */
	protected static CompositeFunction polynomial1a()
	{
		CompositeFunction f = new CompositeFunction(2, 1);
		{
			Fork a = new Fork(3);
			Fork b = new Fork(1);
			Numbers.Multiplication m1 = new Numbers.Multiplication(2);
			Numbers.Multiplication m2 = new Numbers.Multiplication(2);
			Numbers.Subtraction s = new Numbers.Subtraction(2);
			connect(a, 0, m1, 0);
			connect(a, 1, m1, 1);
			connect(a, 2, m2, 0);
			connect(b, 0, m2, 1);
			connect(m1, 0, s, 0);
			connect(m2, 0, s, 1);
			f.add(a, b, m1, m2, s);
			f.associateInput(0, a, 0);
			f.associateInput(1, b, 0);
			f.associateOutput(0, s, 0);
		}
		return f;
	}
	
	/**
	 * Computes a(a − b).
	 * @return
	 */
	protected static CompositeFunction polynomial1b()
	{
		CompositeFunction f = new CompositeFunction(2, 1);
		{
			Fork a = new Fork(2);
			Fork b = new Fork(1);
			Numbers.Multiplication m = new Numbers.Multiplication(2);
			Numbers.Subtraction s = new Numbers.Subtraction(2);
			connect(a, 0, m, 0);
			connect(a, 1, s, 0);
			connect(b, 0, s, 1);
			connect(s, 0, m, 1);
			f.add(a, b, m, s);
			f.associateInput(0, a, 0);
			f.associateInput(1, b, 0);
			f.associateOutput(0, m, 0);
		}
		return f;
	}
}
