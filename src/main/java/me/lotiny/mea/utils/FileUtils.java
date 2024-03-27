package me.lotiny.mea.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

@UtilityClass
public class FileUtils {

    public void copy(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            String[] files = source.list();
            if (files == null) {
                return;
            }

            for (String file : files) {
                File newSource = new File(source, file);
                File newDestination = new File(destination, file);
                copy(newSource, newDestination);
            }
        } else {
            InputStream in = Files.newInputStream(source.toPath());
            OutputStream out = Files.newOutputStream(destination.toPath());

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }

    public void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }

            for (File child : files) {
                delete(child);
            }
        }

        file.delete();
    }
}
