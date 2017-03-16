/*
  Little Poucet - Trace data elements back to their sources
  Copyright (C) 2017 Sylvain Hallé

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet;

/**
 * An arbitrary function applied to a number of provenance nodes
 * @author Sylvain Hallé
 */
public interface NodeFunction
{
	/**
	 * The symbol for separating elements in a datapoint ID
	 */
	public static final String s_separator = ".";
	
	/**
	 * Generates a datapoint ID for this node
	 * @return A datapoint ID, or the empty string if no ID
	 *   can be generated
	 */
	public String getDataPointId();
	
	
	public NodeFunction dependsOn();

}
