/*
 * Copyright (C) 2023 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.pitt.dbmi.causal.experiment.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Mar 10, 2023 11:58:00 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class FileIO {

    private FileIO() {
    }

    public static List<Path> getFiles(Path dir) {
        try {
            return Files.list(dir)
                    .filter(Files::isRegularFile)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }

        return Collections.EMPTY_LIST;
    }

    public static Path createDirectory(Path directory) throws IOException {
        if (Files.notExists(directory)) {
            return Files.createDirectory(directory);
        }

        return directory;
    }

    public static Path createSubdirectory(Path dir, String name) throws IOException {
        return Files.createDirectory(Paths.get(dir.toString(), name));
    }

    public static Path createNewDirectory(Path directory) throws IOException {
        deleteDirectory(directory);
        return createDirectory(directory);
    }

    public static boolean deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory) && Files.isDirectory(directory)) {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        return Files.notExists(directory);
    }

    public static List<String> getFileLineByLine(Path file) {
        List<String> allLines = new LinkedList<>();

        try {
            allLines.addAll(Files.readAllLines(file));
        } catch (IOException exception) {
        }

        return allLines;
    }

}
