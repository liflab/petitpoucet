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
package ca.uqac.lif.petitpoucet;

/**
 * An object that designates a part of another object
 * 
 * @author Sylvain Hallé
 */
public interface Part
{
	/**
	 * Determines if this designator applies (i.e. makes sense) for a particular
	 * object.
	 * 
	 * @param o
	 *          The object; can be <tt>null</tt>
	 * @return <tt>true</tt> if this designator applies to this object,
	 *         <tt>false</tt> otherwise
	 */
	public boolean appliesTo(/* @ null @ */ Object o);

	/**
	 * Returns the first designator to be evaluated
	 * 
	 * @return The designator
	 */
	public Part head();

	/**
	 * Returns a designator from which the first operation is removed
	 * 
	 * @return The designator
	 */
	public Part tail();

	/**
	 * An instance of the "identity" designator
	 */
	public static All all = new All();

	/**
	 * An instance of the "nothing" designator
	 */
	public static Nothing nothing = new Nothing();

	/**
	 * An instance of the "unknown" designator
	 */
	public static Unknown unknown = new Unknown();
	
	/**
	 * An instance of the "self" designator
	 */
	public static Self self = new Self();

	/**
	 * Designator that designates an entire object.
	 */
	public static final class All implements Part
	{
		private All()
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
			return "";
		}

		@Override
		public Part head()
		{
			return this;
		}

		@Override
		public Part tail()
		{
			return null;
		}

		@Override
		public int hashCode()
		{
			return 321;
		}

		@Override
		public boolean equals(Object o)
		{
			return o != null && o instanceof All;
		}
	}

	/**
	 * Designator that designates nothing. When used in a causality or provenance
	 * chain, this means that a value (or part of a value) does not come from any
	 * source --that is, it is created "out of thin air".
	 */
	public static final class Nothing implements Part
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

		@Override
		public Part head()
		{
			return this;
		}

		@Override
		public Part tail()
		{
			return null;
		}

		@Override
		public int hashCode()
		{
			return 4567;
		}

		@Override
		public boolean equals(Object o)
		{
			return o != null && o instanceof Nothing;
		}
	}

	/**
	 * Designator that represents an unknown designation.
	 */
	public static final class Unknown implements Part
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

		@Override
		public Part head()
		{
			return this;
		}

		@Override
		public Part tail()
		{
			return null;
		}

		@Override
		public int hashCode()
		{
			return 321;
		}

		@Override
		public boolean equals(Object o)
		{
			return o != null && o instanceof Unknown;
		}
	}
	
	/**
	 * Designator that represents the object being queried.
	 */
	public static final class Self implements Part
	{
		@Override
		public boolean appliesTo(Object o)
		{
			return true;
		}

		@Override
		public String toString()
		{
			return "Self";
		}

		@Override
		public Part head()
		{
			return this;
		}

		@Override
		public Part tail()
		{
			return null;
		}

		@Override
		public int hashCode()
		{
			return 321;
		}

		@Override
		public boolean equals(Object o)
		{
			return o != null && o instanceof Self;
		}
	}
}
