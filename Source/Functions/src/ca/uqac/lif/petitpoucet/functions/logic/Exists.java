/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2019 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.functions.logic;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Function;

/**
 * Universal quantifier
 * @author Sylvain Hallé
 *
 */
public class Exists extends Quantifier
{

	public Exists(String variable, Function domain, CircuitFunction phi)
	{
		super(variable, domain, phi);
	}

	@Override
	protected boolean getStartValue()
	{
		return false;
	}

	@Override
	protected boolean update(boolean b1, boolean b2)
	{
		return b1 || b2;
	}

	@Override
	public String toString()
	{
		return "exists " + m_variable + " in " + m_domain + " : " + m_function;
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(ObjectReader<?> reader, Object o) throws ReadException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exists duplicate(boolean with_state) 
	{
		return new Exists(m_variable, m_domain, m_function);
	}
}
