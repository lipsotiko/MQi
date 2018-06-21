package io.egia.mqi;

import io.egia.mqi.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

@Component
public class VersionUtility {

    public List<Version> retrieveVersions(String homeDirectory) {
        Logger log = LoggerFactory.getLogger(VersionUtility.class);

        List<Version> versions = new ArrayList<>();

        log.info("Retrieving versions from home/updates directory");
        Path updatesPath = FileSystems.getDefault().getPath(homeDirectory + File.separator + "versions");
        DirectoryStream.Filter<Path> dir_filter = path -> (Files.isDirectory(path, NOFOLLOW_LINKS));

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(updatesPath, dir_filter)) {
            for (Path file : stream) {
                Version v = new Version(file.getFileName().toString());
                versions.add(v);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        versions.sort((vA, vB) -> vA.compareTo(vB));
        return versions;
    }
}
