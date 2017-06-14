/*
 * Copyright (c) 2017-present, CV4J Contributors.
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
package com.cv4j.image.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskUtils {

    public static ExecutorService newCachedThreadPool(final String name) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new CounterThreadFactory(name));
    }

    public static ExecutorService newFixedThreadPool(final String name, int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CounterThreadFactory(name));
    }

    public static ExecutorService newSingleThreadExecutor(final String name) {
        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CounterThreadFactory(name));
    }

    static class CounterThreadFactory implements ThreadFactory {
        private int count;
        private String name;

        public CounterThreadFactory(String name) {
            this.name = (name == null ? "task" : name);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(name + ":thread-" + count++);
            return thread;
        }
    }
}
