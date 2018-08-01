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

package com.venomvendor.gson.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class ResourceHelperTest {

    private ResourceHelperTest() {
        throw new UnsupportedOperationException("Cannot create instance for util");
    }

    public static String getInputforTest(Object currentInstance, String testFileName) {
        ClassLoader classLoader = currentInstance.getClass().getClassLoader();
        URL resource = classLoader.getResource(testFileName);
        assert resource != null;
        File testInput = new File(resource.getPath());

        String content = "{}";
        try {
            String filePath = testInput.getAbsolutePath();
            content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return content;
    }
}
