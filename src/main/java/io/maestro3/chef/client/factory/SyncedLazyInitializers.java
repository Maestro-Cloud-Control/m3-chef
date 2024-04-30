/*
 * Copyright 2023 Maestro Cloud Control LLC
 *
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
 *
 */

package io.maestro3.chef.client.factory;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Holder for lazy object initialization methods.
 * Note: developed to be used as Util for creating singleton objects lazy, when they really needed.
 *
 */
public interface SyncedLazyInitializers {

    /**
     * Initializes <T> objects lazy, using {@link Lock} as synchronization mechanism.
     *
     * @param lockRetriever     {@link Lock} current state retriever
     * @param objectRetriever   initializing object current state retriever
     * @param objectInitializer initializer
     * @param <T>               type of object
     * @return object after initialization or NULL, if object not being initialized
     */
    static <T> T getLazyInitializedObject(Supplier<Lock> lockRetriever,
                                          Supplier<T> objectRetriever,
                                          Supplier<T> objectInitializer) {

        if (Objects.nonNull(objectRetriever.get())) return objectRetriever.get();

        try {
            lockRetriever.get().lock();
            if (Objects.isNull(objectRetriever.get())) {
                return objectInitializer.get();
            }

            return objectRetriever.get();
        } finally {
            lockRetriever.get().unlock();
        }
    }
}
