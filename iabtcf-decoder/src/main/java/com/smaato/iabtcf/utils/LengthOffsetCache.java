package com.smaato. iabtcf.utils;

/*-
 * #%L
 * IAB TCF Core Library
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.EnumMap;

/**
 * Cache for field offsets and lengths to avoid recalculation.
 * Updated to use custom BitReaderFunction interface instead of java.util.function.Function
 * for Android API 21+ compatibility.
 */
class LengthOffsetCache {
    private final BitReader bbv;
    private final EnumMap<FieldDefs, Integer> lengthCache = new EnumMap<>(FieldDefs.class);
    private final EnumMap<FieldDefs, Integer> offsetCache = new EnumMap<>(FieldDefs.class);

    public LengthOffsetCache(BitReader bbv) {
        this.bbv = bbv;
    }

    /**
     * Get cached length or compute and cache it.
     * Updated to use custom BitReaderFunction interface.
     */
    public Integer getLength(FieldDefs field, BitReaderFunction f) {
        return memoize(field, lengthCache, f);
    }

    /**
     * Get cached offset or compute and cache it.
     * Updated to use custom BitReaderFunction interface.
     */
    public Integer getOffset(FieldDefs field, BitReaderFunction f) {
        return memoize(field, offsetCache, f);
    }

    /**
     * Memoize function result based on field's dynamic state.
     * Uses custom BitReaderFunction interface instead of java.util.function.Function.
     */
    private Integer memoize(FieldDefs field, EnumMap<FieldDefs, Integer> cache,
                            BitReaderFunction f) {
        if (! field.isDynamic()) {
            return f.apply(bbv);
        }

        Integer rv = cache.get(field);
        if (rv == null) {
            rv = f.apply(bbv);
            cache.put(field, rv);
        }

        return rv;
    }
}