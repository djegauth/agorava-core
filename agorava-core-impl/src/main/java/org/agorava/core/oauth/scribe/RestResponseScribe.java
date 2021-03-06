/*
 * Copyright 2012 Agorava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.agorava.core.oauth.scribe;

import org.agorava.core.api.exception.AgoravaException;
import org.agorava.core.api.rest.RestResponse;
import org.scribe.model.Response;
import org.scribe.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * {@inheritDoc}
 *
 * @author Antoine Sabot-Durand
 */
class RestResponseScribe implements RestResponse {

    public static final String GZIP_CONTENT_ENCODING = "gzip";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    private String body;

    private Response getDelegate() {
        if (delegate == null) {
            throw new IllegalStateException("Http Response is invalid");
        }
        return delegate;
    }

    private final Response delegate;
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    RestResponseScribe(Response response, String url) {
        delegate = response;
        this.url = url;
    }

    @Override
    public String getBody() {
        return body != null ? body : parseBodyContents();
    }

    private String parseBodyContents() {
        body = StreamUtils.getStreamContents(getStream());
        return body;
    }

    /**
     * {@inheritDoc}
     * This implementation support also gzip encoding in the response
     *
     * @return
     */
    @Override
    public InputStream getStream() {
        InputStream res;
        if (GZIP_CONTENT_ENCODING.equals(getHeaders().get(CONTENT_ENCODING)))
            try {
                res = new GZIPInputStream(getDelegate().getStream());
            } catch (IOException e) {
                throw new AgoravaException("Unable to create GZIPInputStream", e);
            }
        else
            res = getDelegate().getStream();
        return res;
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }

    @Override
    public int getCode() {
        return getDelegate().getCode();
    }

    @Override
    public Map<String, String> getHeaders() {
        return getDelegate().getHeaders();
    }

    @Override
    public String getHeader(String name) {
        return getDelegate().getHeader(name);
    }

    @Override
    public boolean equals(Object obj) {
        return getDelegate().equals(obj);
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

    @Override
    public String getUrl() {
        return url;
    }

}
