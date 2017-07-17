/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2017 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.roi;

import net.imagej.ImageJService;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.roi.mask.real.MaskRealInterval;
import net.imglib2.type.logic.BitType;

import org.scijava.service.Service;

/**
 * {@link Service} for working with Regions of Interest (ROI).
 *
 * @author Alison Walter
 */
public interface RoiService extends ImageJService {

	/**
	 * Attempts to convert the given {@code Object} to a {@code Mask<Localizable>}
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, RandomAccessible, etc.
	 * @return an integer {@link Mask} representation of the given object, if
	 *         possible
	 */
	Mask<Localizable> toIntegerMask(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a {@code MaskInterval}
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, RandomAccessibleInterval, etc.
	 * @return a real {@link MaskInterval} representation of the given object, if
	 *         possible
	 */
	MaskInterval toMaskInterval(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@code Mask<RealLocalizable>}
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, RealRandomAccessible, etc.
	 * @return a real {@link Mask} representation of the given object, if possible
	 */
	Mask<RealLocalizable> toRealMask(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a {@code MaskRealInterval}
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, RealRandomAccessibleRealInterval, etc.
	 * @return a {@link MaskRealInterval} representation of the given object, if
	 *         possible
	 */
	MaskRealInterval toMaskRealInterval(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a {@link Mask}.
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, (Real)RandomAccessible, etc.
	 * @return a {@link Mask} representation of the given Object, if possible
	 */
	Mask<?> toMask(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@code RandomAccessible<BitType>}; otherwise, an exception is thrown.
	 *
	 * @param o the Object to be converted. In general, this should be an ImageJ
	 *          1.x Roi or an Imglib2 Mask.
	 * @return a RandomAccessible representation of the given Object
	 */
	RandomAccessible<BitType> toRA(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@code RandomAccessibleInterval<BitType>}; otherwise, an exception is
	 * thrown.
	 *
	 * @param o the Object to be converted. In general, this should be an ImageJ
	 *          1.x Roi or an Imglib2 Mask.
	 * @return a RandomAccessibleInterval representation of the given Object
	 */
	RandomAccessibleInterval<BitType> toRAI(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@link RealRandomAccessible}.
	 *
	 * @param o the Object to be converted. In general, this should be an ImageJ
	 *          1.x Roi or an ImgLib2 Mask.
	 * @return a RealRandomAccessible representation of the given Object
	 */
	RealRandomAccessible<BitType> toRRA(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@link RealRandomAccessibleRealInterval}.
	 *
	 * @param o the Object to be converted. In general, this should be an ImageJ
	 *          1.x Roi or an ImgLib2 Mask.
	 * @return a RealRandomAccessibleRealInterval representation of the given
	 *         Object
	 */
	RealRandomAccessibleRealInterval<BitType> toRRARI(Object o);
}
