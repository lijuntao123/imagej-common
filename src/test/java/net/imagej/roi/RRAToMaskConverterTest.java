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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.AbstractRealInterval;
import net.imglib2.Cursor;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.real.MaskRealInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.view.Views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;

/**
 * Tests {@link RRAToMaskConverter}.
 *
 * @author Alison Walter
 */
public class RRAToMaskConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RealRandomAccessible<BitType> rra;
	private static RealRandomAccessible<LongType> rraLong;
	private static RealRandomAccessibleRealInterval<BitType> rrari;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final long seed = -9870214;
		final Random rand = new Random(seed);
		final Img<BitType> imgB = ArrayImgs.bits(new long[] { 55, 80 });
		final Img<LongType> imgL = ArrayImgs.longs(new long[] { 55, 80 });
		final Cursor<BitType> cb = imgB.cursor();
		final Cursor<LongType> cl = imgL.cursor();
		while (cb.hasNext() && cl.hasNext()) {
			cb.next().set(rand.nextBoolean());
			cl.next().set(rand.nextLong());
		}
		rra = Views.interpolate(Views.extendBorder(imgB),
			new NearestNeighborInterpolatorFactory<>());
		rraLong = Views.interpolate(imgL,
			new NearestNeighborInterpolatorFactory<>());
		rrari = new TestRRARI<>(new double[2], new double[] { 55, 80 }, rra);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final Object o = convertService.convert(rra, MaskConversionUtil
			.realMaskType());
		assertTrue(o instanceof Mask);
		assertEquals(RealLocalizable.class, MaskConversionUtil.getMaskType(
			(Mask<?>) o));

		@SuppressWarnings("unchecked")
		final Mask<RealLocalizable> m = (Mask<RealLocalizable>) o;
		final double[] pos = new double[] { Double.MIN_VALUE, Double.MAX_VALUE };
		final RealRandomAccess<BitType> access = rra.realRandomAccess();

		assertEquals(access.get().get(), m.test(access));

		pos[0] = -89530945.25;
		pos[1] = 14326532.5;
		access.setPosition(pos);
		assertEquals(access.get().get(), m.test(access));

		pos[0] = 54;
		pos[1] = 79;
		access.setPosition(pos);
		assertEquals(access.get().get(), m.test(access));

		pos[0] = 0.0625;
		pos[1] = 0.0009765625;
		access.setPosition(pos);
		assertEquals(access.get().get(), m.test(access));
	}

	@Test
	public void testMatchingRRAToRealMask() {
		final Converter<?, ?> c = convertService.getHandler(rra, MaskConversionUtil
			.realMaskType());
		assertEquals(RRAToMaskConverter.class, c.getClass());
	}

	@Test
	public void testMatchingRRALongTypeToRealMask() {
		final Converter<?, ?> c = convertService.getHandler(rraLong,
			MaskConversionUtil.realMaskType());
		assertFalse(c instanceof RRAToMaskConverter);
	}

	@Test
	public void testMatchingRRAToMaskRealInterval() {
		final Converter<?, ?> c = convertService.getHandler(rra,
			MaskRealInterval.class);
		assertFalse(c instanceof RRAToMaskConverter);
	}

	@Test
	public void testMatchingRRARIToRealMask() {
		final Converter<?, ?> c = convertService.getHandler(rrari,
			MaskConversionUtil.realMaskType());
		assertFalse(c instanceof RRAToMaskConverter);
		assertNotNull(c);
	}

	@Test
	public void testMatchingRRAToIntegerMask() {
		final Converter<?, ?> c = convertService.getHandler(rra, MaskConversionUtil
			.maskType());
		assertFalse(c instanceof RRAToMaskConverter);
	}

	// -- Helper Classes --

	private static final class TestRRARI<T> extends AbstractRealInterval
		implements RealRandomAccessibleRealInterval<T>
	{

		private final RealRandomAccessible<T> source;

		public TestRRARI(final double[] min, final double[] max,
			final RealRandomAccessible<T> source)
		{
			super(min, max);
			this.source = source;
		}

		@Override
		public RealRandomAccess<T> realRandomAccess() {
			return source.realRandomAccess();
		}

		@Override
		public RealRandomAccess<T> realRandomAccess(final RealInterval interval) {
			return source.realRandomAccess(interval);
		}

	}
}
