package com.comcast.xfinity.sirius.refapplication.sirius;

import com.comcast.xfinity.sirius.api.RequestHandler;
import com.comcast.xfinity.sirius.api.SiriusResult;
import com.comcast.xfinity.sirius.refapplication.RefAppState;

/**
 * Extra-simple RequestHandler. Implements the callback functions for Sirius,
 * translating the get/put/delete into the appropriate repository actions.
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
