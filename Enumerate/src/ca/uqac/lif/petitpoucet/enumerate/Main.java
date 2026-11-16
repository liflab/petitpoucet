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

import ca.uqac.lif.petitpoucet.function.Numbers;
import ca.uqac.lif.synthia.Bounded;
import ca.uqac.lif.synthia.enumerative.AllPickers;
import ca.uqac.lif.synthia.sequence.Playback;

public class Main
{
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		Enumerator e = new Enumerator(new Numbers.Multiplication(3));
		Bounded<? extends Object>[] all = new Bounded[3]; 
		all[0] = getNumberPicker(2);
		all[1] = getNumberPicker(1);
		all[2] = getNumberPicker(2);
		AllPickers listargs = new AllPickers(all); 
		e.enumerate(listargs);
		System.out.println(e.size());
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
}
