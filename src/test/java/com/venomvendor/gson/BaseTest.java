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
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.TypeAdapterFactory;
import com.venomvendor.gson.annotation.MandatoryTest;
import com.venomvendor.gson.util.ResourceHelperTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@RunWith(JUnitPlatform.class)
abstract class BaseTest {

    static final long TIME_OUT = 1L;
    static final Gson parser = new Gson();

    Gson defensiveParser;

    @BeforeAll
    void setUp() {
        TypeAdapterFactory typeAdapter = new NullDefenseTypeAdapterFactory(MandatoryTest.class);
        defensiveParser = new GsonBuilder()
                .registerTypeAdapterFactory(typeAdapter)
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .setStrictness(Strictness.LENIENT)
                .serializeNulls()
                .create();
    }

    @Nonnull
    String getInput(@Nonnull String fileName) {
        return ResourceHelperTest.getInputForTest(this, fileName);
    }
}
