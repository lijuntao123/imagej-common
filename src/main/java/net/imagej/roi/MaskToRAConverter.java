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
import java.lang.reflect.Type;

import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.Masks;
import net.imglib2.type.logic.BitType;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@code Mask<Localizable>} to {@code RandomAccessible<BitType>}.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class, priority = Priority.LOW_PRIORITY)
public class MaskToRAConverter extends
	AbstractConverter<Mask<Localizable>, RandomAccessible<BitType>>
{

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		if (super.canConvert(src, dest)) {
			return MaskConversionUtil.getMaskType((Mask<?>) src).equals(
				Localizable.class);
		}
		return false;
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		if (super.canConvert(src, dest)) {
			return MaskConversionUtil.getMaskType((Mask<?>) src).equals(
				Localizable.class);
		}
		return false;
	}

	// FIXME: Temporary fix, until the ConvertService uses the TypeService
	@Override
	@SuppressWarnings("deprecation")
	public boolean canConvert(final Class<?> src, final Type dest) {
		if (super.canConvert(src, dest) && dest instanceof ParameterizedType) {
			final Type[] types = ((ParameterizedType) dest).getActualTypeArguments();
			return types.length >= 1 && types[0].equals(BitType.class);
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!(src instanceof Mask)) throw new IllegalArgumentException(
			"Cannot convert " + src.getClass() + " to RandomAccessible<BitType>");

		return (T) Masks.toRA((Mask<Localizable>) src, new BitType());
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<RandomAccessible<BitType>> getOutputType() {
		return (Class) RandomAccessible.class;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<Mask<Localizable>> getInputType() {
		return (Class) Mask.class;
	}
}
