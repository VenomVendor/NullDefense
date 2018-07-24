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

package com.venomvendor.library.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * Adapter for removing <b>null</b> objects &amp; <b>empty</b> Collection once the object is created.
 * <p>
 * Incase of {@link Collection}, empty collection is invalid unless {@link #retainEmptyCollection()}
 * is called explicility to retain empty collection. This can be useful incase of search results.
 * <p>
 * This also processess all collection &amp; finally removes {@code null} from Collection,
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
@SuppressWarnings("All")
public final class NullDefenseTypeAdapterFactory implements TypeAdapterFactory {

    /* Single Immutable copy of null */
    private static final Collection NULL_COLLECTION = Collections.singleton(null);
    /* Annotation by which variables are marked mandatory */
    private final Class<? extends Annotation> annotatedType;
    /* When true Collection#size() == 0 is removed */
    private boolean removeEmptyCollection;

    /**
     * Requires annotated class type for checking fields with annotation
     *
     * @param annotatedType Class used for marking fields as mandatory
     * @throws NullPointerException {@inheritDoc}
     */
    public NullDefenseTypeAdapterFactory(@Nonnull Class<? extends Annotation> annotatedType) {
        Objects.requireNonNull(annotatedType, "Annotation class cannot be null");
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
        removeEmptyCollection = true;
        return this;
    }

    /**
     * This will <b>RETAIN</b> empty Collection. i.e {@code collection.isEmpty()}
     *
     * @return A copy of current instance
     */
    public NullDefenseTypeAdapterFactory retainEmptyCollection() {
        removeEmptyCollection = false;
        return this;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        TypeAdapter<T> author = gson.getDelegateAdapter(this, type);
        return new DefensiveAdapter<>(author);
    }

    /**
     * Adapter that removes null objects.
     * A callback is recieved from Gson to read &amp; write.
     * During {@link #read(JsonReader)} if return value is null, then this is ignored.
     *
     * @param <T> Type of object.
     */
    private final class DefensiveAdapter<T> extends TypeAdapter<T> {
        /* Registered type adapter for current type */
        private final TypeAdapter<T> author;

        DefensiveAdapter(@Nonnull TypeAdapter<T> author) {
            Objects.requireNonNull(author, "TypeAdapter is null");
            this.author = author;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value != null) {
                author.write(out, value);
            }
        }

        @Override
        public T read(JsonReader in) throws IOException {
            // Usually never null
            if (in == null) {
                return null;
            }
            // Get read value, after processing with gson
            T result = author.read(in);

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
        private T getFilteredData(@Nonnull T result) {
            for (Field field : result.getClass().getDeclaredFields()) {
                if (field.getType().isPrimitive()) {
                    // Skip primitives
                    continue;
                }

                // Check if current field is marked
                if (field.isAnnotationPresent(annotatedType)) {
                    // To read private fields
                    field.setAccessible(true);

                    Object value = null;
                    try {
                        // Lil, costly operation.
                        value = field.get(result);
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }

                    if (value == null) {
                        return null;
                    }
                    if (value instanceof Collection) {
                        Collection subCollection = ((Collection) value);
                        // Cost is O(N^2), due to rearrangement
                        subCollection.removeAll(NULL_COLLECTION);
                        if (removeEmptyCollection && subCollection.isEmpty()) {
                            return null;
                        }
                    }
                }
            }

            // Finally, we have valid data.
            return result;
        }
    }
}
