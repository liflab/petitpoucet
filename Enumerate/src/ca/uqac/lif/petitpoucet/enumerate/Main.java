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
import ca.uqac.lif.petitpoucet.function.Constant;
import ca.uqac.lif.petitpoucet.function.Fork;
import ca.uqac.lif.petitpoucet.function.IfThenElse;
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
		Enumerator e = new Enumerator(ternaryCondition1b());
		Bounded<? extends Object>[] all = new Bounded[4]; 
		all[0] = new Playback<Number>(new Number[] {0, 1, 2}).setLoop(false); //getNumberPicker(5);
		all[1] = new Playback<Number>(new Number[] {0, 1, 2}).setLoop(false); //getNumberPicker(5);
		all[2] = new Playback<Number>(new Number[] {0, 1, 2}).setLoop(false); //getNumberPicker(5);
		all[3] = new Playback<Number>(new Number[] {0, 1, 2}).setLoop(false); //getNumberPicker(5);
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

	/**
	 * Computes a² + ab.
	 * @return
	 */
	protected static CompositeFunction polynomial2()
	{
		CompositeFunction f = new CompositeFunction(2, 1);
		{
			Fork a = new Fork(3);
			Fork b = new Fork(1);
			Numbers.Multiplication m1 = new Numbers.Multiplication(2);
			Numbers.Multiplication m2 = new Numbers.Multiplication(2);
			Numbers.Addition s = new Numbers.Addition(2);
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
	
	public static CompositeFunction ternaryCondition1a()
	{
		CompositeFunction cf = new CompositeFunction(4, 1);
		{
			Fork f = new Fork(2);
			Numbers.Equals e1 = new Numbers.Equals();
			connect(f, 0, e1, 0);
			Constant zero = new Constant(0);
			connect(zero, 0, e1, 1);
			Numbers.Equals e2 = new Numbers.Equals();
			connect(f, 1, e2, 0);
			Constant one = new Constant(1);
			connect(one, 0, e2, 1);
			IfThenElse i2 = new IfThenElse();
			connect(e2, 0, i2, 0);
			cf.associateInput(0, f, 0);
			cf.associateInput(2, i2, 1);
			cf.associateInput(3, i2, 2);
			IfThenElse i1 = new IfThenElse();
			connect(e1, 0, i1, 0);
			cf.associateInput(1, i1, 1);
			connect(i2, 0, i1, 2);
			cf.associateOutput(0, i1, 0);
			cf.add(f, e1, e2, i1, i2, one, zero);
		}
		return cf;
	}
	
	public static CompositeFunction ternaryCondition1b()
	{
		CompositeFunction cf = new CompositeFunction(4, 1);
		{
			Fork fa = new Fork(2);
			cf.associateInput(0, fa, 0);
			IfThenElse ite_f = new IfThenElse();
			Constant zero = new Constant(0);
			Numbers.Equals e1 = new Numbers.Equals();
			connect(fa, 0, e1, 0);
			connect(zero, 0, e1, 1);
			connect(e1, 0, ite_f, 0);
			cf.associateInput(1, ite_f, 1);
			Numbers.Negative neg = new Numbers.Negative();
			connect(fa, 1, neg, 0);
			connect(neg, 0, ite_f, 2);
			Fork f_ite_f = new Fork(2);
			connect(ite_f, 0, f_ite_f, 0);
			Numbers.Equals e2 = new Numbers.Equals();
			Constant minus_one = new Constant(-1);
			connect(f_ite_f, 0, e2, 0);
			connect(minus_one, 0, e2, 1);
			IfThenElse ite_g = new IfThenElse();
			connect(e2, 0, ite_g, 0);
			cf.associateInput(2, ite_g, 1);
			connect(f_ite_f, 1, ite_g, 2);
			Fork f_ite_g = new Fork(2);
			System.out.println(fa);
			System.out.println(f_ite_f);
			System.out.println(f_ite_g);
			connect(ite_g, 0, f_ite_g, 0);
			Numbers.IsGreaterThan gt = new Numbers.IsGreaterThan();
			Constant other_zero = new Constant(0);
			connect(other_zero, 0, gt, 0);          // was: f_ite_g -> gt.0
			connect(f_ite_g, 0, gt, 1);             // was: other_zero -> gt.1
			IfThenElse ite_h = new IfThenElse();
			connect(gt, 0, ite_h, 0);
			cf.associateInput(3, ite_h, 1);         // u < 0: still undecided, so take d
			connect(f_ite_g, 1, ite_h, 2);          // u >= 0: decided, so propagate u
			cf.associateOutput(0, ite_h, 0);
			cf.add(fa, ite_f, zero, e1, neg, f_ite_f, e2, minus_one, ite_g, f_ite_g, gt, other_zero, ite_h);						
		}
		return cf;
	}
}
