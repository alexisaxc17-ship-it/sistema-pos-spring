package com.example.posapp.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QrUtil {

    public static void generateQrImageToResponse(String text, int width, int height, HttpServletResponse response) throws IOException {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
            response.setContentType("image/png");
            MatrixToImageWriter.writeToStream(matrix, "PNG", response.getOutputStream());
        } catch (WriterException e) {
            throw new IOException("Error generando QR", e);
        }
    }

    // opcional: generar archivo en disco
    public static Path generateQrImageToFile(String text, int width, int height, String filePath) throws IOException {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);
            return path;
        } catch (WriterException e) {
            throw new IOException("Error generando QR", e);
        }
    }
}

