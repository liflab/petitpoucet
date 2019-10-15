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
 * An interface indicating that an object represents a type of traceability
 * query.
 * 
 * @author Sylvain Hallé
 */
public interface TraceabilityQuery
{
	/**
	 * Gets the name of this query
	 * 
	 * @return The name
	 */
	public String getName();
	
	public interface UpstreamQuery extends TraceabilityQuery
	{
		
	}
	
	public interface DownstreamQuery extends TraceabilityQuery
	{
		
	}

	/**
	 * Traceability query that asks for a causality link.
	 * 
	 * @author Sylvain Hallé
	 */
	public static class CausalityQuery implements UpstreamQuery
	{
		/**
		 * A single publicly visible instance of this object
		 */
		public static final transient CausalityQuery instance = new CausalityQuery();

		protected CausalityQuery()
		{
			super();
		}

		@Override
		public String getName()
		{
			return "Causality";
		}
	}

	/**
	 * Traceability query that asks for a provenance link.
	 * 
	 * @author Sylvain Hallé
	 */
	public static class ProvenanceQuery implements UpstreamQuery
	{
		/**
		 * A single publicly visible instance of this object
		 */
		public static final transient ProvenanceQuery instance = new ProvenanceQuery();

		protected ProvenanceQuery()
		{
			super();
		}

		@Override
		public String getName()
		{
			return "Provenance";
		}
	}
	
	/**
	 * Traceability query that asks for a causality link.
	 * 
	 * @author Sylvain Hallé
	 */
	public static class TaintQuery implements DownstreamQuery
	{
		/**
		 * A single publicly visible instance of this object
		 */
		public static final transient TaintQuery instance = new TaintQuery();

		protected TaintQuery()
		{
			super();
		}

		@Override
		public String getName()
		{
			return "Taint";
		}
	}
	
	/**
	 * Traceability query that asks for a causality link.
	 * 
	 * @author Sylvain Hallé
	 */
	public static class ConsequenceQuery implements DownstreamQuery
	{
		/**
		 * A single publicly visible instance of this object
		 */
		public static final transient ConsequenceQuery instance = new ConsequenceQuery();

		protected ConsequenceQuery()
		{
			super();
		}

		@Override
		public String getName()
		{
			return "Consequence";
		}
	}
}
