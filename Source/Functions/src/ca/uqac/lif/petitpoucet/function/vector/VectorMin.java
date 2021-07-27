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
package ca.uqac.lif.petitpoucet.function.vector;

import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;

import java.util.List;

/**
 * Returns the element with the minimum numerical value.
 *
 * @author Sylvain Hallé
 */
public class VectorMin extends VectorFunction {
    /**
     * Creates a new instance of the function.
     */
    public VectorMin() {
        super(1);
    }

    @Override
    protected Number getOutputValue(List<?> ... in_lists) {
        float min = Float.MAX_VALUE;

        for (Object o : in_lists[0]) {
            if (!(o instanceof Number))
                throw new InvalidArgumentTypeException("Expected a number");

            if (((Number) o).floatValue() < min)
                min = ((Number) o).floatValue();
        }
        return min;
    }

    @Override
    public String toString() {
        return "m";
    }

    @Override
    public VectorMin duplicate(boolean with_state) {
        VectorMin vm = new VectorMin();
        copyInto(vm, with_state);
        return vm;
    }
}
