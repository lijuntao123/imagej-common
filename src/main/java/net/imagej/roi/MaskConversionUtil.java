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

import com.googlecode.gentyref.GenericTypeReflector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Predicate;

import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.mask.Mask;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.util.GenericUtils;

/**
 * Utility class for working with {@link Mask}s.
 *
 * @author Alison Walter
 */
final public class MaskConversionUtil {

	private MaskConversionUtil() {}

	/** Gets parameterized {@link Type} for {@code Mask<RealLocalizable>}. */
	public static Type realMaskType() {
		return GenericUtils.getMethodReturnType(method("realMask"), MaskConversionUtil.class);
	}

	/** Gets parameterized {@link Type} for {@code Mask<Localizable>}. */
	public static Type maskType() {
		return GenericUtils.getMethodReturnType(method("mask"), MaskConversionUtil.class);
	}

	/**
	 * Gets parameterized {@link Type} for
	 * {@code RealRandomAccessibleRealInterval<BitType>}.
	 */
	public static Type realRandomAccessibleRealIntervalType() {
		return GenericUtils.getMethodReturnType(method(
			"realRandomAccessibleRealInterval"), MaskConversionUtil.class);
	}

	/**
	 * Gets parameterized {@link Type} for
	 * {@code RealRandomAccessibleRealInterval<DoubleType>}.
	 */
	public static Type realRandomAccessibleRealIntervalDoubleTypeType() {
		return GenericUtils.getMethodReturnType(method(
			"realRandomAccessibleRealIntervalDoubleType"), MaskConversionUtil.class);
	}

	/**
	 * Gets parameterized {@link Type} for {@code RealRandomAccessible<BitType>}.
	 */
	public static Type realRandomAccessibleType() {
		return GenericUtils.getMethodReturnType(method("realRandomAccessible"),
			MaskConversionUtil.class);
	}

	/**
	 * Gets parameterized {@link Type} for
	 * {@code RealRandomAccessible<DoubleType>}.
	 */
	public static Type realRandomAccessibleDoubleTypeType() {
		return GenericUtils.getMethodReturnType(method(
			"realRandomAccessibleDoubleType"), MaskConversionUtil.class);
	}

	/**
	 * Gets parameterized {@link Type} for
	 * {@code RandomAccessibleInterval<BitType>}.
	 */
	public static Type randomAccessibleIntervalType() {
		return GenericUtils.getMethodReturnType(method("randomAccessibleInterval"),
			MaskConversionUtil.class);
	}

	/**
	 * Gets parameterized {@link Type} for
	 * {@code RandomAccessibleInterval<DoubleType>}.
	 */
	public static Type randomAccessibleIntervalDoubleTypeType() {
		return GenericUtils.getMethodReturnType(method(
			"randomAccessibleIntervalDoubleType"), MaskConversionUtil.class);
	}

	/** Gets parameterized {@link Type} for {@code RandomAccessible<BitType>}. */
	public static Type randomAccessibleType() {
		return GenericUtils.getMethodReturnType(method("randomAccessible"),
			MaskConversionUtil.class);
	}

	/**
	 * Gets parameterized {@link Type} for {@code RandomAccessible<DoubleType>}.
	 */
	public static Type randomAccessibleDoubleTypeType() {
		return GenericUtils.getMethodReturnType(method(
			"randomAccessibleDoubleType"), MaskConversionUtil.class);
	}

	public static Type getMaskType(final Mask<?> m) {
		final Method predicateTest;
		try {
			predicateTest = Predicate.class.getMethod("test", Object.class);
		}
		catch (final NoSuchMethodException | SecurityException exc) {
			throw new IllegalStateException("Predicate has no test method?!?");
		}
		Type maskType = getMethodParameterTypes(predicateTest, m.getClass())[0];
		return GenericUtils.getClass(maskType);
	}

	// -- Helper methods --

	private static Method method(final String name) {
		try {
			return MaskConversionUtil.class.getDeclaredMethod(name);
		}
		catch (NoSuchMethodException | SecurityException exc) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private static Mask<RealLocalizable> realMask() {
		return null;
	}

	@SuppressWarnings("unused")
	private static Mask<Localizable> mask() {
		return null;
	}

	@SuppressWarnings("unused")
	private static RealRandomAccessibleRealInterval<BitType>
		realRandomAccessibleRealInterval()
	{
		return null;
	}

	@SuppressWarnings("unused")
	private static RealRandomAccessibleRealInterval<DoubleType>
		realRandomAccessibleRealIntervalDoubleType()
	{
		return null;
	}

	@SuppressWarnings("unused")
	private static RealRandomAccessible<BitType> realRandomAccessible() {
		return null;
	}

	@SuppressWarnings("unused")
	private static RealRandomAccessible<DoubleType>
		realRandomAccessibleDoubleType()
	{
		return null;
	}

	@SuppressWarnings("unused")
	private static RandomAccessibleInterval<BitType> randomAccessibleInterval() {
		return null;
	}

	@SuppressWarnings("unused")
	private static RandomAccessibleInterval<DoubleType>
		randomAccessibleIntervalDoubleType()
	{
		return null;
	}

	@SuppressWarnings("unused")
	private static RandomAccessible<BitType> randomAccessible() {
		return null;
	}

	@SuppressWarnings("unused")
	private static RandomAccessible<DoubleType> randomAccessibleDoubleType() {
		return null;
	}

	/**
	 * As {@link #getFieldType(Field, Class)}, but with respect to the parameter
	 * types of the given {@link Method} rather than a {@link Field}.
	 */
	private static Type[] getMethodParameterTypes(final Method method,
		final Class<?> type)
	{
		final Type wildType = GenericTypeReflector.addWildcardParameters(type);
		return GenericTypeReflector.getExactParameterTypes(method, wildType);
	}
}
