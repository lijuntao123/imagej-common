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
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.view.Views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;

/**
 * Tests {@link RAToMaskConverter}.
 *
 * @author Alison Walter
 */
public class RAToMaskConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RandomAccessibleInterval<BitType> rai;
	private static RandomAccessible<ShortType> raShort;
	private static RandomAccessible<BitType> ra;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final long seed = 87614134;
		final Random rand = new Random(seed);
		rai = ArrayImgs.bits(new long[] { 10, 12 });
		final Img<ShortType> imgShort = ArrayImgs.shorts(new long[] { 10, 12 });
		final Cursor<BitType> cb = ((Img<BitType>) rai).cursor();
		final Cursor<ShortType> cs = imgShort.cursor();
		while (cb.hasNext() && cs.hasNext()) {
			cb.next().set(rand.nextBoolean());
			cs.next().set((short) rand.nextInt());
		}

		ra = Views.extendBorder(rai);
		raShort = Views.extendBorder(imgShort);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final Object o = convertService.convert(ra, MaskConversionUtil.maskType());
		assertTrue(o instanceof Mask);
		assertEquals(MaskConversionUtil.getMaskType((Mask<?>) o),
			Localizable.class);

		@SuppressWarnings("unchecked")
		final Mask<Localizable> m = (Mask<Localizable>) o;
		final int[] pos = new int[2];
		final RandomAccess<BitType> access = ra.randomAccess();

		pos[0] = 1987;
		pos[1] = -12;
		access.setPosition(pos);
		assertEquals(access.get().get(), m.test(access));

		pos[0] = Integer.MIN_VALUE;
		pos[1] = 0;
		access.setPosition(pos);
		assertEquals(access.get().get(), m.test(access));

		pos[0] = 0;
		pos[1] = Integer.MAX_VALUE;
		access.setPosition(pos);
		assertEquals(access.get().get(), m.test(access));

		pos[0] = -90874;
		pos[1] = 12929;
		access.setPosition(pos);
		assertEquals(access.get().get(), m.test(access));
	}

	@Test
	public void testMatchingRAToIntegerMask() {
		final Converter<?, ?> c = convertService.getHandler(ra, MaskConversionUtil
			.maskType());
		assertEquals(RAToMaskConverter.class, c.getClass());
	}

	@Test
	public void testMatchingRAShortTypeToIntegerMask() {
		final Converter<?, ?> c = convertService.getHandler(raShort,
			MaskConversionUtil.maskType());
		assertFalse(c instanceof RAToMaskConverter);
	}

	@Test
	public void testMatchingRAToMaskInterval() {
		final Converter<?, ?> c = convertService.getHandler(ra, MaskInterval.class);
		assertFalse(c instanceof RAToMaskConverter);
	}

	@Test
	public void testMatchingRAIToIntegerMask() {
		final Converter<?, ?> c = convertService.getHandler(rai, MaskConversionUtil
			.maskType());
		assertFalse(c instanceof RAToMaskConverter);
		assertEquals(RAIToMaskIntervalConverter.class, c.getClass());
	}

	@Test
	public void testMatchingRAToRealMask() {
		final Converter<?, ?> c = convertService.getHandler(ra, MaskConversionUtil
			.realMaskType());
		assertFalse(c instanceof RAToMaskConverter);
	}
}
