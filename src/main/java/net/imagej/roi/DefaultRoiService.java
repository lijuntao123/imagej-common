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

import java.lang.reflect.ParameterizedType;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.roi.mask.real.MaskRealInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.Util;

import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * Default implementation of {@link RoiService}.
 *
 * @author Alison Walter
 */
@Plugin(type = Service.class)
public class DefaultRoiService extends AbstractService implements RoiService {

	@Parameter
	private ConvertService convertService;

	@Override
	@SuppressWarnings("unchecked")
	public Mask<Localizable> toIntegerMask(final Object o) {
		final String returnType = "Mask<Localizable>";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final ParameterizedType type = (ParameterizedType) MaskConversionUtil
			.maskType();
		final Mask<?> m = (Mask<?>) convertService.convert(o, type);
		if (m != null && isMaskType(m, type)) return (Mask<Localizable>) m;

		final RandomAccessible<?> ra = (RandomAccessible<?>) convertService.convert(
			o, MaskConversionUtil.randomAccessibleType());
		if (ra != null && isBitType(ra)) return toIntegerMask(ra);

		throw cannotConvert(o, returnType);
	}

	@Override
	public MaskInterval toMaskInterval(final Object o) {
		final String returnType = "MaskInterval";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final MaskInterval mi = convertService.convert(o, MaskInterval.class);
		if (mi != null) return mi;

		final RandomAccessibleInterval<?> rai =
			(RandomAccessibleInterval<?>) convertService.convert(o, MaskConversionUtil
				.randomAccessibleIntervalType());
		if (rai != null && isBitType(rai)) return toMaskInterval(rai);

		throw cannotConvert(o, returnType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Mask<RealLocalizable> toRealMask(final Object o) {
		final String returnType = "Mask<RealLocalizable>";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final ParameterizedType type = (ParameterizedType) MaskConversionUtil
			.realMaskType();
		final Mask<?> m = (Mask<?>) convertService.convert(o, type);
		if (m != null && isMaskType(m, type)) return (Mask<RealLocalizable>) m;

		final RealRandomAccessible<?> rra = (RealRandomAccessible<?>) convertService
			.convert(o, MaskConversionUtil.realRandomAccessibleType());
		if (rra != null && isBitType(rra)) return toRealMask(rra);

		throw cannotConvert(o, returnType);
	}

	@Override
	public MaskRealInterval toMaskRealInterval(final Object o) {
		final String returnType = "MaskRealInterval";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final MaskRealInterval mri = convertService.convert(o,
			MaskRealInterval.class);
		if (mri != null) return mri;

		final RealRandomAccessibleRealInterval<?> rrari =
			(RealRandomAccessibleRealInterval<?>) convertService.convert(o,
				MaskConversionUtil.realRandomAccessibleRealIntervalType());
		if (rrari != null && isBitType(rrari)) return toMaskRealInterval(rrari);

		throw cannotConvert(o, returnType);
	}

	@Override
	public Mask<?> toMask(final Object o) {
		final String returnType = "Mask";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final Mask<?> m = convertService.convert(o, Mask.class);
		if (m != null) return m;

		final RandomAccessible<?> ra = (RandomAccessible<?>) convertService.convert(
			o, MaskConversionUtil.randomAccessibleType());
		if (ra != null && isBitType(ra)) return toIntegerMask(ra);

		final RealRandomAccessible<?> rra = (RealRandomAccessible<?>) convertService
			.convert(o, MaskConversionUtil.realRandomAccessibleType());
		if (rra != null && isBitType(rra)) return toRealMask(rra);

		throw cannotConvert(o, returnType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RandomAccessible<BitType> toRA(final Object o) {
		final String returnType = "RandomAccessible<BitType>";
		checkNull(o, returnType);

		final RandomAccessible<?> ra = (RandomAccessible<?>) convertService.convert(
			o, MaskConversionUtil.randomAccessibleType());
		if (ra != null && isBitType(ra)) return (RandomAccessible<BitType>) ra;

		final ParameterizedType type = (ParameterizedType) MaskConversionUtil
			.maskType();
		final Mask<?> m = (Mask<?>) convertService.convert(o, type);
		if (m != null && isMaskType(m, type)) return toRA(m);

		throw cannotConvert(o, returnType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<BitType> toRAI(final Object o) {
		final String returnType = "RandomAccessibleInterval<BitType>";
		checkNull(o, returnType);

		final RandomAccessibleInterval<?> rai =
			(RandomAccessibleInterval<?>) convertService.convert(o, MaskConversionUtil
				.randomAccessibleIntervalType());
		if (rai != null && isBitType(rai))
			return (RandomAccessibleInterval<BitType>) rai;

		final MaskInterval mi = convertService.convert(o, MaskInterval.class);
		if (mi != null) return toRAI(mi);

		throw cannotConvert(o, returnType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RealRandomAccessible<BitType> toRRA(final Object o) {
		final String returnType = "RealRandomAccessible<BitType>";
		checkNull(o, returnType);

		final RealRandomAccessible<?> rra = (RealRandomAccessible<?>) convertService
			.convert(o, MaskConversionUtil.realRandomAccessibleType());
		if (rra != null && isBitType(rra))
			return (RealRandomAccessible<BitType>) rra;

		final ParameterizedType type = (ParameterizedType) MaskConversionUtil
			.realRandomAccessibleType();
		final Mask<?> m = (Mask<?>) convertService.convert(o, type);
		if (m != null && isMaskType(m, type)) return toRRA(m);

		throw cannotConvert(o, returnType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RealRandomAccessibleRealInterval<BitType> toRRARI(final Object o) {
		final String returnType = "RealRandomAccessibleRealInterval<BitType>";
		checkNull(o, returnType);

		final RealRandomAccessibleRealInterval<?> rrari =
			(RealRandomAccessibleRealInterval<?>) convertService.convert(o,
				MaskConversionUtil.realRandomAccessibleRealIntervalType());
		if (rrari != null && isBitType(rrari))
			return (RealRandomAccessibleRealInterval<BitType>) rrari;

		final MaskRealInterval mri = convertService.convert(o,
			MaskRealInterval.class);
		if (mri != null) return toRRARI(mri);

		throw cannotConvert(o, returnType);
	}

	// -- Helper methods --

	private void checkNull(final Object o, final String s) {
		if (o == null) throw new IllegalArgumentException(
			"Cannot convert null to " + s);
	}

	private IllegalArgumentException cannotConvert(final Object o,
		final String s)
	{
		return new IllegalArgumentException("Cannot convert " + o.getClass() +
			" to " + s);
	}

	private boolean isBitType(final RealRandomAccessible<?> rra) {
		if (rra instanceof RealInterval) return Util.getTypeFromRealInterval(
			(RealRandomAccessibleRealInterval<?>) rra) instanceof BitType;
		return rra.realRandomAccess().get() instanceof BitType;
	}

	private boolean isBitType(final RandomAccessible<?> ra) {
		if (ra instanceof Interval) return Util.getTypeFromInterval(
			(RandomAccessibleInterval<?>) ra) instanceof BitType;
		return ra.randomAccess().get() instanceof BitType;
	}

	private boolean isMaskType(final Mask<?> m, final ParameterizedType t) {
		return MaskConversionUtil.getMaskType(m).equals(t
			.getActualTypeArguments()[0]);
	}
}
