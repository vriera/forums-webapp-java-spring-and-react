package ar.edu.itba.paw.webapp.config.wrapper;

import ch.qos.logback.core.encoder.EventObjectInputStream;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CachedBodyServletInputStream extends ServletInputStream {
    private final InputStream cachedBodyInputStream;

    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }
    @Override
    public int read() throws IOException {
        return cachedBodyInputStream.read();
    }

}
