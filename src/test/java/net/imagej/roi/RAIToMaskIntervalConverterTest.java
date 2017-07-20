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

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.roi.mask.real.MaskRealInterval;
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
 * Tests {@link RAIToMaskIntervalConverter}.
 *
 * @author Alison Walter
 */
public class RAIToMaskIntervalConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RandomAccessibleInterval<BitType> rai;
	private static RandomAccessibleInterval<ShortType> raiShort;
	private static RandomAccessible<BitType> ra;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final long seed = 87614134;
		final Random rand = new Random(seed);
		rai = ArrayImgs.bits(new long[] { 10, 12 });
		raiShort = ArrayImgs.shorts(new long[] { 10, 12 });
		final Cursor<BitType> cb = ((Img<BitType>) rai).cursor();
		final Cursor<ShortType> cs = ((Img<ShortType>) raiShort).cursor();
		while (cb.hasNext() && cs.hasNext()) {
			cb.next().set(rand.nextBoolean());
			cs.next().set((short) rand.nextInt());
		}

		ra = Views.extendBorder(rai);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final MaskInterval mi = convertService.convert(rai, MaskInterval.class);
		final RandomAccess<BitType> access = rai.randomAccess();

		rai.min(access);
		assertEquals(access.get().get(), mi.test(access));

		rai.max(access);
		assertEquals(access.get().get(), mi.test(access));

		access.setPosition(new int[] { 4, 11 });
		assertEquals(access.get().get(), mi.test(access));

		access.setPosition(new int[] { 2, 5 });
		assertEquals(access.get().get(), mi.test(access));
	}

	@Test
	public void testMatchingRAIToMaskInterval() {
		final Converter<?, ?> c = convertService.getHandler(rai,
			MaskInterval.class);
		assertEquals(RAIToMaskIntervalConverter.class, c.getClass());
	}

	@Test
	public void testMatchingRAIShortTypeToMaskInterval() {
		final Converter<?, ?> c = convertService.getHandler(raiShort,
			MaskInterval.class);
		assertFalse(c instanceof RAIToMaskIntervalConverter);
	}

	@Test
	public void testMatchingRAIToMaskRealInterval() {
		final Converter<?, ?> c = convertService.getHandler(rai,
			MaskRealInterval.class);
		assertFalse(c instanceof RAIToMaskIntervalConverter);
	}

	@Test
	public void testMatchingRAToMaskInterval() {
		final Converter<?, ?> c = convertService.getHandler(ra, MaskInterval.class);
		assertFalse(c instanceof RAIToMaskIntervalConverter);
	}
}
