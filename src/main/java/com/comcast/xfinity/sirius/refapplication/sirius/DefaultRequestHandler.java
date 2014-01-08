/*
 * Copyright 2013 Comcast Cable Communications Management, LLC
 *
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
 */
package com.comcast.xfinity.sirius.refapplication.sirius;

import com.comcast.xfinity.sirius.api.RequestHandler;
import com.comcast.xfinity.sirius.api.SiriusResult;
import com.comcast.xfinity.sirius.refapplication.RefAppState;

/**
 * Extra-simple RequestHandler. Implements the callback functions for Sirius,
 * translating the get/put/delete into the appropriate repository actions.
 *
 * Methods on this class are called *by* sirius when an update has been
 * accepted by the cluster and persisted on this node.
 */
public class DefaultRequestHandler implements RequestHandler {
    private static final String OK = "ok";

    @Override
    public SiriusResult handleGet(String key) {
        String value = RefAppState.repository.get(key);

        if (value == null) {
            return SiriusResult.none();
        }

        return SiriusResult.some(value);
    }

    @Override
    public SiriusResult handlePut(String key, byte[] body) {
        RefAppState.repository.put(key, new String(body));

        return SiriusResult.some(OK);
    }

    @Override
    public SiriusResult handleDelete(String key) {
        RefAppState.repository.delete(key);

        return SiriusResult.some(OK);
    }
}
