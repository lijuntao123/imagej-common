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
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.roi.geom.real.OpenEllipsoid;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.Masks;
import net.imglib2.roi.mask.real.MaskRealInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;

/**
 * Tests {@link MaskRealIntervalToRRARIConverter}.
 *
 * @author Alison Walter
 */
public class MaskRealIntervalToRRARIConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static MaskRealInterval mri;
	private static Mask<RealLocalizable> mr;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final int seed = 8866392;
		final Random r = new Random(seed);
		final ArrayImg<BitType, LongArray> img = ArrayImgs.bits(new long[] { 10,
			30 });
		final Cursor<BitType> c = img.cursor();
		while (c.hasNext())
			c.next().set(r.nextBoolean());
		final RealRandomAccessible<BitType> real = Views.interpolate(img,
			new NearestNeighborInterpolatorFactory<>());
		mr = Masks.toMask(real);

		mri = new OpenEllipsoid(new double[] { 12, -2.5, 6 }, new double[] { 3, 7,
			1 });
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final Object o = convertService.convert(mri, MaskConversionUtil
			.realRandomAccessibleRealIntervalType());
		assertTrue(o instanceof RealRandomAccessibleRealInterval);
		assertTrue(((RealRandomAccessibleRealInterval<?>) o).realRandomAccess()
			.get() instanceof BitType);

		@SuppressWarnings("unchecked")
		final RealRandomAccessibleRealInterval<BitType> rrari =
			(RealRandomAccessibleRealInterval<BitType>) o;
		final RealRandomAccess<BitType> rra = rrari.realRandomAccess();
		final double[] pos = ((OpenEllipsoid) mri).center();

		assertEquals(mri.test(rra), rra.get().get());

		pos[0] = 9.5;
		rra.setPosition(pos);
		assertEquals(mri.test(rra), rra.get().get());

		pos[0] = 12;
		pos[1] = 4.25;
		rra.setPosition(pos);
		assertEquals(mri.test(rra), rra.get().get());

		pos[1] = -2.5;
		pos[1] = 5.125;
		rra.setPosition(pos);
		assertEquals(mri.test(rra), rra.get().get());
	}

	@Test
	public void testMatchingMaskRealIntervalToRRARI() {
		final Converter<?, ?> c = convertService.getHandler(mri, MaskConversionUtil
			.realRandomAccessibleRealIntervalType());
		assertEquals(MaskRealIntervalToRRARIConverter.class, c.getClass());
	}

	@Test
	public void testMatchingMaskRealIntervalToRRARIDoubleType() {
		final Converter<?, ?> c = convertService.getHandler(mri, MaskConversionUtil
			.realRandomAccessibleRealIntervalDoubleTypeType());
		assertFalse(c instanceof MaskRealIntervalToRRARIConverter);
	}

	@Test
	public void testMatchingMaskRealIntervalToRRA() {
		final Converter<?, ?> c = convertService.getHandler(mri, MaskConversionUtil
			.realRandomAccessibleType());
		assertEquals(MaskRealIntervalToRRARIConverter.class, c.getClass());
	}

	@Test
	public void testMatchingMaskToRRARI() {
		final Converter<?, ?> c = convertService.getHandler(mr, MaskConversionUtil
			.realRandomAccessibleRealIntervalType());
		assertFalse(c instanceof MaskRealIntervalToRRARIConverter);
	}
}
