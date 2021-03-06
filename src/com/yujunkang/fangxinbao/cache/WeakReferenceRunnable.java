/*******************************************************************************
 * Copyright (c) 2013 Chris Banes.
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
 ******************************************************************************/
package com.yujunkang.fangxinbao.cache;

import java.lang.ref.WeakReference;

abstract class WeakReferenceRunnable<T> implements Runnable {

    private final WeakReference<T> mObjectRef;

    public WeakReferenceRunnable(T object) {
        mObjectRef = new WeakReference<T>(object);
    }

    @Override
    public final void run() {
        T object = mObjectRef.get();

        if (null != object) {
            run(object);
        }
    }

    public abstract void run(T object);

}
