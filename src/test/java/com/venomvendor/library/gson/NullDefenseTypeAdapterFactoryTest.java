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
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.venomvendor.library.gson.annotation.MandatoryTest;
import com.venomvendor.library.gson.model.ChildTest;
import com.venomvendor.library.gson.model.ParentTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ALL")
@DisplayName("Tests for removal of null objects")
class NullDefenseTypeAdapterFactoryTest extends BaseTest {

    private static String getName(ChildTest child) {
        return child.getName();
    }

    private static int getAge(ChildTest child) {
        return child.getAge();
    }

    private static String getKnownLanguage(ChildTest child, int languageIndex) {
        return child.getLanguage().getKnownLanguages().get(languageIndex);
    }

    @Test
    @Tag("-ve")
    @DisplayName("Should throw NPE, as input is `null` for type adapter")
    void shouldThrowNPE() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new NullDefenseTypeAdapterFactory(null));

        assertEquals("Annotation class cannot be null", exception.getMessage());
    }

    @Test
    @Tag("-ve")
    @DisplayName("Shouldn't have parent.")
    void shouldNotGenerateParent() {
        String input = getInput("missing-parent-name.json");

        assertTimeout(ofSeconds(TIME_OUT), () -> {
            ParentTest parent = defensiveParser.fromJson(input, ParentTest.class);
            assertNull(parent);
        });
    }

    @Test
    @Tag("+ve")
    @DisplayName("Should generate one child, as rest doesn't have mandatory `name`")
    void shouldGenerateSingleChild() {
        String input = getInput("missing-name-in-child.json");

        assertTimeout(ofSeconds(TIME_OUT), () -> {
            ParentTest parent = defensiveParser.fromJson(input, ParentTest.class);
            assertNotNull(parent);

            assertFalse(parent.getChildren().isEmpty());

            assertEquals(1, parent.getChildren().size());
        });
    }

    @Test
    @Tag("+ve")
    @DisplayName("Objects created by defensive GSON, should be same as plain GSON")
    void parserTest() {
        String input = getInput("valid.json");
        ParentTest testParent = parser.fromJson(input, ParentTest.class);

        assertTimeout(ofSeconds(TIME_OUT), () -> {
            ParentTest parent = defensiveParser.fromJson(input, ParentTest.class);
            assertNotNull(parent);

            assertEquals(testParent.getName(), parent.getName());

            assertFalse(parent.getChildren().isEmpty());

            assertEquals(testParent.getChildren().size(), parent.getChildren().size());

            List<ChildTest> children = parent.getChildren();
            List<ChildTest> testChildren = testParent.getChildren();

            for (int i = children.size() - 1; i >= 0; i--) {
                assertEquals(getName(children.get(i)), getName(testChildren.get(i)));
                assertEquals(getAge(children.get(i)), getAge(testChildren.get(i)));
                assertEquals(getKnownLanguage(children.get(i), 0),
                        getKnownLanguage(testChildren.get(i), 0));
            }

            assertEquals(testParent.getChildren().size(), parent.getChildren().size());
        });
    }

    @Test
    @Tag("-ve")
    @DisplayName("Shouldn't generate any child, in turn parent is null," +
            " as it doesn't have mandatory `knownLanguages`")
    void shouldNotGenerateAnyChild() {
        String input = getInput("missing-known-language-no-child.json");
        assertTimeout(ofSeconds(TIME_OUT), () -> {
            ParentTest parent = defensiveParser.fromJson(input, ParentTest.class);
            assertNull(parent);
        });
    }

    @Test
    @Tag("+ve")
    @DisplayName("Should generate one child as it rest have mandatory `knownLanguages`")
    void shouldGenerateOneChild() {
        String input = getInput("missing-known-language-one-child.json");
        assertTimeout(ofSeconds(TIME_OUT), () -> {
            ParentTest parent = defensiveParser.fromJson(input, ParentTest.class);
            assertNotNull(parent);

            assertFalse(parent.getChildren().isEmpty());

            assertEquals(1, parent.getChildren().size());
            assertEquals("Queen", parent.getChildren().get(0).getName());
            assertEquals("Telugu", getKnownLanguage(parent.getChildren().get(0), 0));
        });
    }

    @Test
    @Tag("+ve")
    @DisplayName("Should return null for `read`")
    void shouldReturnNullForRead() {
        assertTimeout(ofSeconds(TIME_OUT), () ->
                assertNull(
                        new NullDefenseTypeAdapterFactory(MandatoryTest.class)
                                .create(new Gson(), new TypeToken<ParentTest>() {
                                }).read(null)
                ));
    }

    @Test
    @Tag("+ve")
    @DisplayName("Should throw IllegalStateException for no children")
    void shouldThrowExceptionForMissingChildren() {
        assertTimeout(ofSeconds(TIME_OUT), () -> {
            ParentTest parent = parser.fromJson("{\"name\":\"VenomVendor\"}", ParentTest.class);
            assertNotNull(parent);

            assertTrue(defensiveParser.toJson(parent, ParentTest.class).contains("children"));
        });
    }

    @Test
    @Tag("+ve")
    @DisplayName("Serialization Test")
    void parserSerializeTest() {
        String input = getInput("valid.json");

        assertTimeout(ofSeconds(TIME_OUT), () -> {
            ParentTest parent = defensiveParser.fromJson(input, ParentTest.class);
            assertNotNull(parent);

            String json = defensiveParser.toJson(parent, ParentTest.class);

            assertNotNull(json);
            assertFalse(json.isEmpty());
        });
    }

    @Test
    @Tag("+ve")
    @DisplayName("Annotation has no effect on primitive fields")
    void missingPrimitiveTest() {
        String input = getInput("valid-missing-primitive.json");
        ParentTest testParent = parser.fromJson(input, ParentTest.class);

        assertTimeout(ofSeconds(TIME_OUT), () -> {
            ParentTest parent = defensiveParser.fromJson(input, ParentTest.class);
            assertNotNull(parent);

            assertEquals(testParent.getName(), parent.getName());

            assertFalse(parent.getChildren().isEmpty());

            assertEquals(testParent.getChildren().size(), parent.getChildren().size());

            List<ChildTest> children = parent.getChildren();
            List<ChildTest> testChildren = testParent.getChildren();

            for (int i = children.size() - 1; i >= 0; i--) {
                assertEquals(getName(children.get(i)), getName(testChildren.get(i)));
                assertEquals(getAge(children.get(i)), getAge(testChildren.get(i)));
                assertEquals(getKnownLanguage(children.get(i), 0),
                        getKnownLanguage(testChildren.get(i), 0));
            }

            assertEquals(testParent.getChildren().size(), parent.getChildren().size());
        });
    }

    @Test
    @Tag("+ve")
    @DisplayName("Should generate Three child as `knownLanguages` is empty & accepted")
    void shouldGenerateOneChildButThreeActually() {
        TypeAdapterFactory typeAdapter = new NullDefenseTypeAdapterFactory(MandatoryTest.class)
                .retainEmptyCollection()
                .removeEmptyCollection()
                .retainEmptyCollection();

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(typeAdapter)
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .setLenient()
                .serializeNulls()
                .create();

        String input = getInput("missing-known-language-one-child.json");
        assertTimeout(ofSeconds(TIME_OUT), () -> {
            ParentTest parent = gson.fromJson(input, ParentTest.class);
            assertNotNull(parent);

            assertFalse(parent.getChildren().isEmpty());

            assertEquals(3, parent.getChildren().size());
        });
    }
}
