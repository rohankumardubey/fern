/* **
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright 2020, Miguel Arregui a.k.a. marregui
 */

package com.fern.util;

import java.util.Comparator;

/**
 * Implementors contain an accessor method for a UTC Epoch.
 */
public interface WithUTCTimestamp {

    /**
     * Comparator to sort lists of implementors, smaller to greater
     */
    Comparator<WithUTCTimestamp> COMPARING =
            Comparator.comparingLong(WithUTCTimestamp::getUTCTimestamp);

    /**
     * @return a UTC Epoch
     */
    long getUTCTimestamp();
}
