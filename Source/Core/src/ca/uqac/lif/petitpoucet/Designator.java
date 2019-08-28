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
package ca.uqac.lif.petitpoucet;

/**
 * An object that designates a part of another object
 * @author Sylvain Hallé
 */
public interface Designator
{
	/**
	 * Determines if this designator applies (i.e. makes sense) for a particular
	 * object.
	 * @param o The object; can be <tt>null</tt>
	 * @return <tt>true</tt> if this designator applies to this object,
	 * <tt>false</tt> otherwise
	 */
	public boolean appliesTo(/*@ null @*/ Object o);
	
	/**
	 * An instance of the "identity" designator
	 */
	public static Identity identity = new Identity();
	
	/**
	 * An instance of the "nothing" designator
	 */
	public static Nothing nothing = new Nothing();
	
	/**
	 * An instance of the "unknown" designator
	 */
	public static Unknown unknown = new Unknown();
	
	/**
	 * Designator that designates an entire object.
	 */
	public static final class Identity implements Designator
	{
		private Identity()
		{
			super();
		}
		
		@Override
		public boolean appliesTo(Object o)
		{
			return o != null;
		}
		
		@Override
		public String toString()
		{
			return "The value of ";
		}
	}
	
	/**
	 * Designator that designates nothing. When used in a causality or
	 * provenance chain, this means that a value (or part of a value) does
	 * not come from any source --that is, it is created "out of thin air".
	 */
	public static final class Nothing implements Designator
	{
		private Nothing()
		{
			super();
		}
		
		@Override
		public boolean appliesTo(Object o)
		{
			return true;
		}
		
		@Override
		public String toString()
		{
			return "Nothing";
		}
	}
	
	/**
	 * Designator that represents an unknown designation.
	 */
	public static final class Unknown implements Designator
	{
		private Unknown()
		{
			super();
		}
		
		@Override
		public boolean appliesTo(Object o)
		{
			return true;
		}
		
		@Override
		public String toString()
		{
			return "Unknown";
		}
	}
}
