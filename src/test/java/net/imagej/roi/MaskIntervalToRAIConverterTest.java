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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.Masks;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;

/**
 * Tests {@link MaskIntervalToRAIConverter}.
 *
 * @author Alison Walter
 */
public class MaskIntervalToRAIConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static MaskInterval mi;
	private static Mask<Localizable> ml;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final int seed = -12;
		final Random r = new Random(seed);
		final ArrayImg<BitType, LongArray> img = ArrayImgs.bits(new long[] { 10,
			30 });
		final Cursor<BitType> c = img.cursor();
		while (c.hasNext())
			c.next().set(r.nextBoolean());
		mi = Masks.toMaskInterval(img);
		ml = Masks.toMask(Views.extendZero(img));
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final Converter<MaskInterval, RandomAccessibleInterval<BitType>> c =
			new MaskIntervalToRAIConverter();
		final Object o = c.convert(mi, MaskConversionUtil.randomAccessibleIntervalType());
		assertTrue(o instanceof RandomAccessibleInterval);
		assertTrue(Util.getTypeFromInterval(
			(RandomAccessibleInterval<?>) o) instanceof BitType);

		@SuppressWarnings("unchecked")
		final RandomAccessibleInterval<BitType> rai =
			(RandomAccessibleInterval<BitType>) o;
		final RandomAccess<BitType> ra = rai.randomAccess();
		final long[] pos = new long[rai.numDimensions()];

		rai.min(ra);
		assertEquals(mi.test(ra), ra.get().get());

		rai.max(ra);
		assertEquals(mi.test(ra), ra.get().get());

		pos[0] = 5;
		pos[1] = 11;
		ra.setPosition(pos);
		assertEquals(mi.test(ra), ra.get().get());

		pos[0] = 7;
		pos[1] = 23;
		ra.setPosition(pos);
		assertEquals(mi.test(ra), ra.get().get());
	}

	@Test
	public void testMatchingMaskIntervalToRAI() {
		final Converter<?, ?> c = convertService.getHandler(mi, MaskConversionUtil
			.randomAccessibleIntervalType());
		assertEquals(MaskIntervalToRAIConverter.class, c.getClass());
	}

	@Test
	public void testMatchingMaskIntervalToRAIDoubleType() {
		final Converter<?, ?> c = convertService.getHandler(mi, MaskConversionUtil
			.randomAccessibleIntervalDoubleTypeType());
		assertFalse(c instanceof MaskIntervalToRAIConverter);
	}

	@Test
	public void testMatchingMaskIntervalToRA() {
		final Converter<?, ?> c = convertService.getHandler(mi, MaskConversionUtil
			.randomAccessibleType());
		assertEquals(MaskIntervalToRAIConverter.class, c.getClass());
	}

	@Test
	public void testMatchingMaskToRAI() {
		final Converter<?, ?> c = convertService.getHandler(ml, MaskConversionUtil
			.randomAccessibleIntervalType());
		assertFalse(c instanceof MaskIntervalToRAIConverter);
	}
}
