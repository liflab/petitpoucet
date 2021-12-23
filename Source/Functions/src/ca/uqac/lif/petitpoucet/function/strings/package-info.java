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

/**
 * String manipulation functions. For these functions, lineage is the
 * relationship between characters or ranges of characters of an input string,
 * and characters or ranges of characters of an output string. The operations
 * provided in this package each apply a basic transformation on a string
 * (e.g.: extracting a substring, replacing a regex pattern by another one,
 * etc.), and keep track of where the parts of the input string end up in
 * the output. Most of these functions do so by descending from
 * {@link StringMappingFunction}; they simply add entries to a 
 * {@link RangeMapping} when they perform their computation. The explanation
 * of the output in terms of the input (and vice versa) is taken care of
 * by {@link StringMappingFunction} based on the contents of this mapping.
 * 
 * @author Sylvain Hallé
 * @since 2.0
 */
package ca.uqac.lif.petitpoucet.function.strings;