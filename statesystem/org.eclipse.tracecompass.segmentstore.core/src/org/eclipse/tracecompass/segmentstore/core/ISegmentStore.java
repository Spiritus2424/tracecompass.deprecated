/*******************************************************************************
 * Copyright (c) 2015 EfficiOS Inc., Alexandre Montplaisir
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexandre Montplaisir - Initial API and implementation
 *******************************************************************************/

package org.eclipse.tracecompass.segmentstore.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.Lists;

/**
 * Interface for segment-storing backends.
 *
 * @param <E>
 *            The type of {@link ISegment} element that will be stored in this
 *            database.
 *
 * @author Alexandre Montplaisir
 */
public interface ISegmentStore<E extends ISegment> extends Collection<E> {

    /**
     * Sorted Iterator
     *
     * @param order
     *            The desired order for the returned iterator
     * @return An iterator over all the segments in the store in the desired order
     * @since 1.1
     */
    default Iterable<E> iterator(Comparator<ISegment> order){
        return getIntersectingElements(Long.MIN_VALUE, Long.MAX_VALUE, order);
    }

    /**
     * Retrieve all elements that inclusively cross the given position.
     *
     * @param position
     *            The target position. This would represent a timestamp, if the
     *            tree's X axis represents time.
     * @return The intervals that cross this position
     */
    default Iterable<E> getIntersectingElements(long position){
        return getIntersectingElements(position, position);
    }

    /**
     * Retrieve all elements that inclusively cross the given position, sorted
     * in the specified order.
     *
     * @param position
     *            The target position. This would represent a timestamp, if the
     *            tree's X axis represents time.
     * @param order
     *            The desired order for the returned iterator
     * @return The intervals that cross this position
     * @since 1.1
     */
    default Iterable<E> getIntersectingElements(long position, Comparator<ISegment> order) {
        return getIntersectingElements(position, position, order);
    }

    /**
     * Retrieve all elements that inclusively cross another segment. We define
     * this target segment by its start and end positions.
     *
     * This effectively means, all elements that respect *both* conditions:
     *
     * <ul>
     * <li>Their end is after the 'start' parameter</li>
     * <li>Their start is before the 'end' parameter</li>
     * </ul>
     *
     * @param start
     *            The target start position
     * @param end
     *            The target end position
     * @return The elements overlapping with this segment
     */
    Iterable<E> getIntersectingElements(long start, long end);

    /**
     * Retrieve all elements that inclusively cross another segment, sorted in
     * the specified order. We define this target segment by its start and end
     * positions.
     *
     * @param start
     *            The target start position
     * @param end
     *            The target end position
     * @param order
     *            The desired order for the returned iterator
     * @return The intervals that cross this position
     * @since 1.1
     */
    default Iterable<E> getIntersectingElements(long start, long end, Comparator<ISegment> order){
        List<E> list = Lists.newArrayList(getIntersectingElements(start, end));
        return new Iterable<@NonNull E>() {
            @Override
            public Iterator<@NonNull E> iterator() {
                Collections.sort(list, order);
                return list.iterator();
            }
        };
    }

    /**
     * Dispose the data structure and release any system resources associated
     * with it.
     */
    void dispose();

    /**
     * Method to close off the segment store. This happens for example when we
     * are done reading an off-line trace. Implementers can use this method to
     * save the segment store on disk
     *
     * @param deleteFiles
     *            Whether to delete any file that was created while building the
     *            segment store
     */
    default void close(boolean deleteFiles) {

    }
}
