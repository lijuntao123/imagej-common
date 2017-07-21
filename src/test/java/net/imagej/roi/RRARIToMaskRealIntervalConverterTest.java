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

import net.imglib2.AbstractRealInterval;
import net.imglib2.Cursor;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.roi.mask.real.MaskRealInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;

/**
 * Tests {@link RRARIToMaskRealIntervalConverter}.
 *
 * @author Alison Walter
 */
public class RRARIToMaskRealIntervalConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RealRandomAccessibleRealInterval<BitType> rrari;
	private static RealRandomAccessibleRealInterval<FloatType> rrariFloat;
	private static RealRandomAccessible<BitType> rra;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final long seed = 0xBABEBEEFl;
		final Random rand = new Random(seed);
		final Img<BitType> imgB = ArrayImgs.bits(new long[] { 4, 6 });
		final Img<FloatType> imgF = ArrayImgs.floats(new long[] { 4, 6 });
		final Cursor<BitType> cb = imgB.cursor();
		final Cursor<FloatType> cf = imgF.cursor();
		while (cb.hasNext() && cf.hasNext()) {
			cb.next().set(rand.nextBoolean());
			cf.next().set(rand.nextFloat());
		}
		rra = Views.interpolate(imgB, new NearestNeighborInterpolatorFactory<>());
		rrari = new TestRRARI<>(new double[] { 0, 0 }, new double[] { 4, 6 }, rra);
		rrariFloat = new TestRRARI<>(new double[] { 0, 0 }, new double[] { 4, 6 },
			Views.interpolate(imgF, new NearestNeighborInterpolatorFactory<>()));
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final MaskRealInterval mri = convertService.convert(rrari,
			MaskRealInterval.class);

		final RealRandomAccess<BitType> access = rrari.realRandomAccess();

		rrari.realMin(access);
		assertEquals(access.get().get(), mri.test(access));

		rrari.realMax(access);
		assertEquals(access.get().get(), mri.test(access));

		access.setPosition(new double[] { 3.5, 5.25 });
		assertEquals(access.get().get(), mri.test(access));

		access.setPosition(new double[] { 0, 2.25 });
		assertEquals(access.get().get(), mri.test(access));
	}

	@Test
	public void testMatchingRRARIToMaskRealInterval() {
		final Converter<?, ?> c = convertService.getHandler(rrari,
			MaskRealInterval.class);
		assertEquals(RRARIToMaskRealIntervalConverter.class, c.getClass());
	}

	@Test
	public void testMatchingRRARIFloatTypeToMaskRealInterval() {
		final Converter<?, ?> c = convertService.getHandler(rrariFloat,
			MaskRealInterval.class);
		assertFalse(c instanceof RRARIToMaskRealIntervalConverter);
	}

	@Test
	public void testMatchingRRARIToMaskInterval() {
		final Converter<?, ?> c = convertService.getHandler(rrari,
			MaskInterval.class);
		assertFalse(c instanceof RRARIToMaskRealIntervalConverter);
	}

	@Test
	public void testMatchingRRAToMaskRealInterval() {
		final Converter<?, ?> c = convertService.getHandler(rra,
			MaskRealInterval.class);
		assertFalse(c instanceof RRARIToMaskRealIntervalConverter);
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
