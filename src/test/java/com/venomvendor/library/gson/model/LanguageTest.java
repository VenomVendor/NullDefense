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

package com.venomvendor.library.gson.model;

import com.venomvendor.library.gson.annotation.MandatoryTest;

import java.util.ArrayList;
import java.util.List;

public class LanguageTest {

    @MandatoryTest
    private List<String> knownLanguages = new ArrayList<>();

    public List<String> getKnownLanguages() {
        return knownLanguages;
    }

    public void setKnownLanguages(List<String> knownLanguages) {
        this.knownLanguages = knownLanguages;
    }
}
