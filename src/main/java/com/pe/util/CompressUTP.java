package com.pe.util;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CompressUTP {

    private static boolean exists(String ruta){
        return (new File(ruta).exists());
    }

    public static List<ArchiveEntry> listAll(String path) throws IOException, ArchiveException {
        List<ArchiveEntry> lista_archivos = new ArrayList<ArchiveEntry>();
        if (exists(path)){
            InputStream fi = Files.newInputStream(Paths.get(path));
            InputStream bi = new BufferedInputStream(fi);
            ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(bi);

            ArchiveEntry entry = null;

            while( (entry = input.getNextEntry())  != null){
                lista_archivos.add(entry);
            }
        }
        return lista_archivos;
    }

    public static List<ArchiveEntry> listDirectories(String path) throws IOException, ArchiveException {
        List<ArchiveEntry> lista_directorios = new ArrayList<ArchiveEntry>();
        if (exists(path)){
            InputStream fi = Files.newInputStream(Paths.get(path));
            InputStream bi = new BufferedInputStream(fi);
            ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(bi);

            ArchiveEntry entry = null;

            while( (entry = input.getNextEntry())  != null){
                if (entry.isDirectory()) lista_directorios.add(entry);
            }
        }
        return lista_directorios;
    }

    public static List<ArchiveEntry> listFiles(String path) throws IOException, ArchiveException {
        List<ArchiveEntry> lista_archivos = new ArrayList<ArchiveEntry>();
        if (exists(path)){
            InputStream fi = Files.newInputStream(Paths.get(path));
            InputStream bi = new BufferedInputStream(fi);
            ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(bi);

            ArchiveEntry entry = null;

            while( (entry = input.getNextEntry())  != null){
                if (!entry.isDirectory()) lista_archivos.add(entry);
            }
        }
        return lista_archivos;
    }

    public static boolean extractAll(String source_path, String destination_path) throws IOException, ArchiveException {
        if (exists(source_path)){
            File output = new File(source_path);
            try (InputStream fileInputStream = new FileInputStream(output)) {
                try (ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream("zip",
                        fileInputStream)) {
                    ZipArchiveEntry entry = null;
                    while ((entry = (ZipArchiveEntry) archiveInputStream.getNextEntry()) != null) {
                        String destino = destination_path + "\\" + entry.getName();
                        File outfile = new File(destino);
                        if ( entry.isDirectory()){
                            outfile.mkdirs();
                        }else{
                            outfile.getParentFile().mkdirs();
                            try (OutputStream o = new FileOutputStream(outfile)) {
                                IOUtils.copy(archiveInputStream, o);
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean extractFile(String source_path, String destination_path, String filename) throws IOException, ArchiveException {
        if (exists(source_path)){
            File output = new File(source_path);
            try (InputStream fileInputStream = new FileInputStream(output)) {
                try (ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream("zip",
                        fileInputStream)) {
                    ZipArchiveEntry entry = null;
                    while ((entry = (ZipArchiveEntry) archiveInputStream.getNextEntry()) != null) {
                        String destino = destination_path + "\\" + entry.getName();
                        File outfile = new File(destino);
                        if ( entry.isDirectory()){
                            outfile.mkdirs();
                        }else{
                            outfile.getParentFile().mkdirs();
                            String archivo = FilenameUtils.getName(entry.getName());
                            if ( archivo.equals(filename) ) {
                                try (OutputStream o = new FileOutputStream(outfile)) {
                                    IOUtils.copy(archiveInputStream, o);
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static void compressDirectoryInZip(String dir, String fileName) throws IOException {
        Path sourceDir = Paths.get(dir);
        File zipFile = new File(fileName);

        try (ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
            Files.walk(sourceDir).forEach(path -> {
                File file = path.toFile();
                if (!file.isDirectory()) {
                    String relativePath = sourceDir.relativize(path).toString();
                    try {
                        ArchiveEntry entry = new ZipArchiveEntry(file, relativePath);
                        zipOut.putArchiveEntry((ZipArchiveEntry) entry);
                        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
                            org.apache.commons.compress.utils.IOUtils.copy(input, zipOut);
                        }
                        zipOut.closeArchiveEntry();
                    } catch (IOException e) {
                        System.err.println("Error while adding file to zip: " + file.getName());
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}
