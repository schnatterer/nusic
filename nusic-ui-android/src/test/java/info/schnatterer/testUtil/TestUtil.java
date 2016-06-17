/**
 * Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic.
 *
 * nusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.testUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * General utilities for unit tests.
 * 
 * @author schnatterer
 */
public final class TestUtil {
    /** This is utility class, no instances needed. */
    private TestUtil() {
        // Utility class, don't instantiate.
    }

    /**
     * Set the value of a final field using reflection. <br/>
     * <b>This is for testing only. Don't use this in production!</b>
     * 
     * @param instance
     *            the instance whose field should be changed. Pass
     *            <code>null</code> for static fields
     * @param fieldName
     *            the name of field to set
     * @param newValue
     *            the value to set to <code>field</code>
     * @throws RuntimeException
     *             if anything goes wrong when using reflection,such as field
     *             does not exist
     */
    public static void setFinalField(Object instance, String fieldName,
            Object newValue) {
        try {
            setFinalField(instance,
                    instance.getClass().getDeclaredField(fieldName), newValue);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Set the value of a final field using reflection. <br/>
     * <b>This is for testing only. Don't use this in production!</b>
     * 
     * @param instance
     *            the instance whose field should be changed. Pass
     *            <code>null</code> for static fields
     * @param field
     *            the field to set
     * @param newValue
     *            the value to set to <code>field</code>
     * @throws RuntimeException
     *             if anything goes wrong when using reflection,such as field
     *             does not exist
     */
    public static void setFinalField(Object instance, Field field,
            Object newValue) {
        field.setAccessible(true);

        Field modifiersField;
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField
                    .setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(instance, newValue);
            modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
        } catch (Throwable t) {
            // This is testing only, safe us some boilerplate
            throw new RuntimeException(t);
        }
    }

    /**
     * Set the value of a field using reflection. This is intended to be used to
     * set final fields. <br/>
     * <b>This is for testing only. Don't use this in production!</b>
     * 
     * @param instance
     *            the instance whose field should be changed. Pass
     *            <code>null</code> for static fields
     * @param fieldName
     *            the name of field to set
     * @param newValue
     *            the value to set to <code>field</code>
     * @param declaringClass
     *            the class that declares <code>fieldName</code>. If
     *            <code>null</code>, <code>instance.getClass()</code> is tried.
     *            Note that it might be
     *            <code>instance.getClass().getSuperclass()</code>
     * 
     * @throws RuntimeException
     *             if anything goes wrong when using reflection, such as field
     *             does not exist
     */
    public static void setPrivateField(Object instance, String fieldName,
            Object newValue, Class<? extends Object> declaringClass) {
        Field field;
        try {
            field = declaringClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, newValue);
            field.setAccessible(false);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
