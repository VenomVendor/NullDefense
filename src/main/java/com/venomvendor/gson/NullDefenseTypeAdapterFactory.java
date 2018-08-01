/*
 * Copyright (C) 2018 VenomVendor.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.venomvendor.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;


/**
 * Adapter for removing <b>null</b> objects &amp; <b>empty</b> Collections, once object is created.
 * <p>
 * Incase of {@link Collection}, empty collection is invalid unless {@link #retainEmptyCollection()}
 * is called explicitly to retain empty collection. This can be useful incase of search results.
 * <p>
 * This also processes all collection &amp; finally removes {@code null} from Collection,
 * further processes collection to remove all {@code null} from results.<pre>
 *   public class Parent {
 *        &#064;Mandatory
 *        &#064;SerializedName("name")
 *        private String name;
 *
 *        &#064;Mandatory
 *        &#064;SerializedName("children")
 *        private CustomList<Child> children;
 *
 *        &#064;SerializedName("id")
 *        private Integer id
 *
 *        ...
 *        setters/getters
 *        ...
 *   }</pre>
 * When registered, Gson will remove the Whole object if any of the field marked as {@code Mandatory}
 * is null, however the same is not applicable for {@code Primitive} types.
 * Refer: {@link com.google.gson.internal.Primitives#isPrimitive(Type)}
 * <p>
 * <pre>TypeAdapterFactory typeAdapter = new NullDefenseTypeAdapterFactory(Mandatory.class)
 *         // To retain empty collection
 *         .retainEmptyCollection()
 *         // To remove empty collection, this is default
 *         .removeEmptyCollection();
 *
 *     Gson gson = new GsonBuilder()
 *         .registerTypeAdapterFactory(typeAdapter)
 *         .enableComplexMapKeySerialization()
 *         .setPrettyPrinting()
 *         .setLenient()
 *         .serializeNulls()
 *         .create();
 * </pre>
 */
@SuppressWarnings("WeakerAccess")
public final class NullDefenseTypeAdapterFactory implements TypeAdapterFactory {

    /* Annotation by which variables are marked mandatory */
    private final Class<? extends Annotation> annotatedType;
    /* When true, Collection#size() == 0 is removed */
    private boolean discardEmpty;

    /**
     * Requires annotated class for checking fields with annotation.
     * <p>Example</p>
     * <pre>
     * &#064;Retention(RetentionPolicy.RUNTIME)
     * &#064;Target({ElementType.FIELD})
     * public @interface Mandatory { }
     * </pre>
     *
     * @param annotatedType Class used for marking fields as mandatory,
     *                      this has to be of retention type {@link RetentionPolicy#RUNTIME}
     * @throws NullPointerException if annotated class is null
     */
    public NullDefenseTypeAdapterFactory(Class<? extends Annotation> annotatedType) {
        if (annotatedType == null) {
            throw new NullPointerException("Annotation class cannot be null");
        }
        this.annotatedType = annotatedType;
        removeEmptyCollection();
    }

    /**
     * This will remove empty Collection. i.e {@code collection.isEmpty()}. By default
     * null is removed irrespective of any Type.
     *
     * @return A copy of current instance
     */
    public NullDefenseTypeAdapterFactory removeEmptyCollection() {
        discardEmpty = true;
        return this;
    }

    /**
     * This will <b>RETAIN</b> empty Collection. i.e {@code collection.isEmpty()}
     *
     * @return A copy of current instance
     */
    public NullDefenseTypeAdapterFactory retainEmptyCollection() {
        discardEmpty = false;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        TypeAdapter<T> author = gson.getDelegateAdapter(this, type);
        return new DefensiveAdapter<T>(author, discardEmpty, annotatedType);
    }

    /**
     * Adapter that removes null objects.
     * A callback is received from Gson to read &amp; write.
     * During {@link #read(JsonReader)} if return value is null, then this is ignored.
     *
     * @param <T> Type of object.
     */
    private static final class DefensiveAdapter<T> extends TypeAdapter<T> {
        /* Single Immutable copy of null */
        private static final Collection NULL_COLLECTION = Collections.singleton(null);
        /* Registered type adapter for current type */
        private final TypeAdapter<T> author;
        /* Annotation by which variables are marked mandatory */
        private final Class<? extends Annotation> annotatedType;
        /* When true, Collection#size() == 0 is removed */
        private final boolean discardEmpty;

        DefensiveAdapter(TypeAdapter<T> author, boolean discardEmpty,
                         Class<? extends Annotation> annotatedType) {
            this.author = author;
            this.discardEmpty = discardEmpty;
            this.annotatedType = annotatedType;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            author.write(out, value);
        }

        @Override
        public T read(JsonReader reader) throws IOException {
            // Usually non-null
            if (reader == null) {
                return null;
            }
            // Get read value, after processing with gson
            // This is where Object is created from json
            T result = author.read(reader);

            // if null, return it.
            if (result == null) {
                return null;
            }

            // We have data, lets process it.
            return getFilteredData(result);
        }

        /**
         * Process data annotated with {@link #annotatedType}
         *
         * @param result data to process
         * @return same result if not null or conditional empty, else {@code null}
         */
        private T getFilteredData(T result) {
            Class<?> clz = result.getClass();
            boolean isMarkedInClz = clz.isAnnotationPresent(annotatedType);

            for (Field field : clz.getDeclaredFields()) {
                if (field.getType().isPrimitive()) {
                    // Skip primitives
                    continue;
                }
                if (isNotValid(result, isMarkedInClz, field)) {
                    // Discard result & return null.
                    return null;
                }
            }

            // Finally, we have valid data.
            return result;
        }

        /**
         * @param result        data to process
         * @param isMarkedInClz is marked in class level, ie. all fields are mandatory
         * @param field         declared variable in current object
         * @return {@code true} if data is invalid
         */
        private boolean isNotValid(T result, boolean isMarkedInClz, Field field) {
            // Check if current field is marked
            boolean isMarked = isMarkedInClz || field.isAnnotationPresent(annotatedType);

            return isMarked && containsInvalidData(result, field);
        }

        /**
         * Check if data contains null or empty objects only on annotated fields
         *
         * @param result data to process
         * @param field  declared variable in current object
         * @return {@code true} if data is invalid
         */
        private boolean containsInvalidData(T result, Field field) {
            // To read private fields
            field.setAccessible(true);

            // Validate data
            return hasInvalidData(result, field);
        }

        /**
         * Check if data contains null or empty objects
         *
         * @param result data to process
         * @param field  declared variable in current object
         * @return {@code true} if data is invalid
         */
        private boolean hasInvalidData(T result, Field field) {
            Object value = null;
            try {
                // Lil, costly operation.
                value = field.get(result);
            } catch (IllegalAccessException ex) {
                // Can't help
                ex.printStackTrace();
            }

            // Check for emptiness
            return isEmpty(value);
        }

        /**
         * Checks if data is either null or empty
         *
         * @param value data to process
         * @return {@code true} if data is invalid
         */
        private boolean isEmpty(Object value) {
            return value == null || isEmptyCollection(value);
        }

        /**
         * Checks if data is of type collection &amp; removes all null items from collection,
         * before checking for total number of items in it.
         *
         * @param value data to process
         * @return {@code true} if data is invalid
         */
        @SuppressWarnings("SuspiciousMethodCalls")
        private boolean isEmptyCollection(Object value) {
            if (value instanceof Collection) {
                Collection<?> subCollection = ((Collection) value);
                // Cost is O(N^2), due to rearrangement
                subCollection.removeAll(NULL_COLLECTION);
                // Remove object if collection is empty.
                return discardEmpty && subCollection.isEmpty();
            }
            return false;
        }
    }
}
