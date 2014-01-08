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

import com.comcast.xfinity.sirius.api.SiriusResult;
import com.comcast.xfinity.sirius.refapplication.RefAppState;
import com.comcast.xfinity.sirius.refapplication.store.KVRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRequestHandlerTest {

    @Mock
    private KVRepository mockRepository;

    private DefaultRequestHandler underTest;

    @Before
    public void init() {
        RefAppState.repository = mockRepository;
        underTest = new DefaultRequestHandler();
    }

    @Test
    public void testHandleGetNoValue() {
        String key = "a";

        doReturn(null).when(mockRepository).get(key);
        assertEquals(SiriusResult.none(), underTest.handleGet(key));
    }

    @Test
    public void testHandleGetGoodValue() {
        String key = "a";
        String value = "yay";

        doReturn(value).when(mockRepository).get(key);
        assertEquals(value, underTest.handleGet(key).getValue());
    }

    @Test
    public void testHandlePutReturnsOK() {
        String key = "a";
        String value = "yay";

        assertEquals(SiriusResult.some("ok"), underTest.handlePut(key, value.getBytes()));
    }

    @Test
    public void testHandlePutPassesToBackendRepo() {
        String key = "a";
        String value = "yay";

        underTest.handlePut(key, value.getBytes());
        verify(mockRepository).put(key, value);
    }

    @Test
    public void testHandleDeleteReturnsOK() {
        String key = "a";

        assertEquals(SiriusResult.some("ok"), underTest.handleDelete(key));
    }

    @Test
    public void testHandleDeletePassesToBackendRepo() {
        String key = "a";

        underTest.handleDelete(key);
        verify(mockRepository).delete(key);
    }
}
