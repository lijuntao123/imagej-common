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

import static org.junit.Assert.assertTrue;

import net.imagej.ImgPlus;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedSphere;
import net.imglib2.roi.geom.real.DefaultPointMask;
import net.imglib2.roi.geom.real.OpenBox;
import net.imglib2.roi.geom.real.PointMask;
import net.imglib2.roi.geom.real.Sphere;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.roi.mask.integer.RandomAccessibleIntervalAsMask;
import net.imglib2.roi.mask.real.MaskAsRealRandomAccessibleRealInterval;
import net.imglib2.type.logic.BitType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Tests {@link DefaultRoiService}.
 *
 * @author Alison Walter
 */
public class DefaultRoiServiceTest {

	private static Context context;
	private static RoiService roi;

	@BeforeClass
	public static void setupOnce() {
		context = new Context(RoiService.class, ConvertService.class);
		roi = context.getService(RoiService.class);
	}

	@AfterClass
	public static void teardownOnce() {
		context.dispose();
	}

	@Test
	public void testToMask() {
		final Img<BitType> img = ArrayImgs.bits(12, 52, 10);
		final ImgPlus<BitType> plus = new ImgPlus<>(img);
		final Mask<?> m = roi.toMask(plus);

		assertTrue(m instanceof RandomAccessibleIntervalAsMask);
	}

	@Test
	public void testMultiCallConversion() {
		final RealRandomAccessibleRealInterval<BitType> rrari = roi.toRRARI(
			new double[] { 12, 13 });

		// ensure two conversions happened as expected
		assertTrue(rrari instanceof MaskAsRealRandomAccessibleRealInterval);
		final MaskAsRealRandomAccessibleRealInterval<?> adapt =
			(MaskAsRealRandomAccessibleRealInterval<?>) rrari;
		assertTrue(adapt.source() instanceof PointMask);
	}

	@Test
	public void testCastingConversion() {
		final Sphere s = new ClosedSphere(new double[] { 16.5, -0.25, 4 }, 8);
		final Mask<RealLocalizable> mr = roi.toRealMask(s);
		assertTrue(s == mr);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullConversion() {
		final RealRandomAccessible<BitType> rra = roi.toRRA(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConversion() {
		final Box b = new OpenBox(new double[] { 8, -2, 0.5, 105 }, new double[] {
			22, 65.25, 9, 107 });
		final MaskInterval m = roi.toMaskInterval(b);
	}

	// -- Helper classes --

	@Plugin(type = Converter.class, priority = Priority.LAST_PRIORITY)
	public static final class DoubleArrayToPointMaskConverter extends
		AbstractConverter<double[], PointMask>
	{

		@Override
		@SuppressWarnings("unchecked")
		public <T> T convert(final Object src, final Class<T> dest) {
			return (T) new DefaultPointMask((double[]) src);
		}

		@Override
		public Class<PointMask> getOutputType() {
			return PointMask.class;
		}

		@Override
		public Class<double[]> getInputType() {
			return double[].class;
		}

	}
}
