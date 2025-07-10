package game.logic.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompressionUtility {
    public static void compressToStream(byte[] data, OutputStream outputStream) throws IOException {
        GZIPOutputStream compressedOutput = new GZIPOutputStream(outputStream);
        compressedOutput.write(data);
        compressedOutput.close();
    }

    public static byte[] decompressFromStream(InputStream input) throws IOException {
        GZIPInputStream compressedInput = new GZIPInputStream(input);
        byte[] data = compressedInput.readAllBytes();
        compressedInput.close();

        return data;
    }

    public static void compressStreams(InputStream input, OutputStream outputStream) throws IOException {
        GZIPOutputStream compressedOutput = new GZIPOutputStream(outputStream);
        compressedOutput.write(input.readAllBytes());
        compressedOutput.close();
    }

    public static void decompressStreams(InputStream input, OutputStream output) throws IOException {
        GZIPInputStream compressedInput = new GZIPInputStream(input);
        output.write(compressedInput.readAllBytes());
        compressedInput.close();
    }
}
