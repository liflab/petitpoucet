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
 * A link between a designator, and one or more other designators.
 * This interface can be used, for example, to represent a causality
 * link between part of an object and parts of other objects.
 * @author Sylvain Hallé
 */
public interface DesignatorLink 
{
	/**
	 * The "quality" of the link, which can be:
	 * <ul>
	 * <li>exact: all the designators in this link represent it exactly</li>
	 * <li>an over-approximation: the actual link is included by the designators</li>
	 * <li>an under-approximation: the actual link includes the designators</li>
	 * </ul>
	 */
	public enum Quality {EXACT, OVER, UNDER}
	
	/**
	 * Determines if the link is strict. What "strict" means depends on
	 * what the link represents in the context.
	 * @return The link's quality
	 */
	public Quality getQuality();

	/**
	 * Gets the designated objects involved in this link
	 * @return An ordered collection of designated objects
	 */
	/*@ non_null @*/ public DesignatedObject getDesignatedObject();

	/**
	 * Object representing an unknown traceability link.
	 * @author Sylvain Hallé
	 */
	public static final class UnknownLink implements DesignatorLink
	{
		/**
		 * A single publicly visible instance of this class
		 */
		public static final transient UnknownLink instance = new UnknownLink();

		/**
		 * Creates an instance of the link
		 */
		private UnknownLink()
		{
			super();
		}

		@Override
		public final Quality getQuality() 
		{
			return Quality.EXACT;
		}

		@Override
		public final DesignatedObject getDesignatedObject() 
		{
			return null;
		}
	}
}
