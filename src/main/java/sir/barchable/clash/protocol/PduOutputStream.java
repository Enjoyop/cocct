package sir.barchable.clash.protocol;

import sir.barchable.util.Cipher;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Write Clash PDUs.
 *
 * @author Sir Barchable
 *         Date: 6/04/15
 */
public class PduOutputStream implements Closeable {
    private OutputStream out;
    private Cipher cipher;

    /**
     * Creates a PDU output stream with a newly initialized stream cipher.
     * Call {@link #setKey(byte[])} after key exchange to reinitialize the stream cipher.
     *
     * @param out the stream to write to
     */
    public PduOutputStream(OutputStream out) {
        this(out, new Clash7Crypt());
    }

    public PduOutputStream(OutputStream out, Cipher cipher) {
        this.out = out;
        this.cipher = cipher;
    }

    public void write(Pdu pdu) throws IOException {
        writeShort(pdu.getId());
        writeUInt3(pdu.getPayload().length);
        writeShort(pdu.getVersion());
        out.write(cipher.encrypt(pdu.getPayload()));
        out.flush();
    }

    private void writeUInt3(int v) throws IOException {
        out.write(v >>> 16);
        out.write(v >>> 8);
        out.write(v);
    }

    private void writeShort(int v) throws IOException {
        out.write(v >>> 8);
        out.write(v);
    }

    public void setKey(byte[] nonce) {
        cipher.setKey(nonce);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
