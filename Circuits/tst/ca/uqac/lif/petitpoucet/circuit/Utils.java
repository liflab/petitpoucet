/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.circuit;

import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.circuit.Node.DownstreamConnection;
import ca.uqac.lif.petitpoucet.circuit.Node.UpstreamConnection;

public class Utils
{
	public static void connect(Connectable c1, int i1, Connectable c2, int i2)
	{
		UpstreamConnection uc = new Node.NodeUpstreamConnection(c1, i1);
		DownstreamConnection dc = new Node.NodeDownstreamConnection(c2, i2);
		Connectable.connect(uc, i1, dc, i2);
	}
}
