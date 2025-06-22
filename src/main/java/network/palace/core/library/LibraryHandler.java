package network.palace.core.library;

import lombok.Getter;
import network.palace.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

/**
 * The LibraryHandler class provides functionality for dynamically loading
 * library dependencies from a JSON configuration file and managing their addition
 * at runtime to the classloader. It is designed to work with JavaPlugin-based plugins.
 * <p>
 * This class is not instantiable and only provides static methods for library handling.
 */
public final class LibraryHandler {

    /**
     * Loads the external libraries defined in a JSON configuration file located in the plugin's resources.
     * This method reads a "libraries.json" file within the plugin's resource folder, parses it, and processes
     * each library defined in the file. If a library is missing or invalid, it logs an error.
     *
     * @param plugin the JavaPlugin instance to load libraries for. If the plugin is null, the operation is bypassed.
     */
    public static void loadLibraries(JavaPlugin plugin) {
        if (plugin == null) return;
        // Loop through list from json
        try {
            if (plugin.getClass().getResourceAsStream("/libraries.json") == null) return;
            JSONParser jsonParser = new JSONParser();
            JSONArray libraryArray = (JSONArray) jsonParser.parse(new InputStreamReader(plugin.getClass().getResourceAsStream("/libraries.json")));
            for (Object object : libraryArray) {
                JSONObject jsonObject = (JSONObject) object;
                MavenObject mavenObject;
                String groupId = (String) jsonObject.get("groupId");
                String artifactId = (String) jsonObject.get("artifactId");
                String version = (String) jsonObject.get("version");
                String repo = (String) jsonObject.get("repo");
                if (repo == null || repo.trim().isEmpty()) {
                    mavenObject = new MavenObject(groupId, artifactId, version);
                } else {
                    mavenObject = new MavenObject(groupId, artifactId, version, repo);
                }
                handle(plugin, mavenObject);
            }
        } catch (ParseException | IOException e) {
            Core.logMessage(plugin.getName(), ChatColor.RED + "Error parsing library for " + plugin.getName());
            e.printStackTrace();
        }
    }

    /**
     * Handles the downloading and loading of an external library, specified by a Maven object,
     * into the runtime environment for the given plugin.
     * <p>
     * This method attempts to download the library artifact from the specified repository URL
     * if it does not already exist locally. Once the file is downloaded, it attempts to load
     * the library's JAR file into the current runtime.
     *
     * @param plugin the JavaPlugin instance for which the library is being handled. It is used
     *               to log messages and associate the library handling process with the plugin's context.
     * @param library the MavenObject representing the library to handle. It includes details
     *                such as groupId, artifactId, version, and the repository URL from where
     *                the library should be fetched if not already available locally.
     */
    private static void handle(JavaPlugin plugin, MavenObject library) {
        Set<File> jars = new HashSet<>();
        try {
            File location = createAndGetWriteLocation(library);
            if (!location.exists()) {
                Core.logMessage(plugin.getName(), ChatColor.GRAY + "Downloading " + getFileName(library) + " from " + library.getRepo());
                try (InputStream inputStream = getUrl(library.getRepo(), library).openStream()) {
                    Files.copy(inputStream, location.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            jars.add(location);
        } catch (Exception e) {
            Core.logMessage(plugin.getName(), ChatColor.RED + "Could not load library " + library.getArtifactId());
            e.printStackTrace();
        }
        for (File jar : jars) {
            try {
                addFile(jar);
            } catch (IOException e) {
                Core.logMessage(plugin.getName(), ChatColor.RED + "Could not load jar file " + jar.getName());
                continue;
            }
            Core.logMessage(plugin.getName(), ChatColor.DARK_GREEN + "Loaded library " + jar.getName());
        }
    }

    /**
     * Constructs a complete URL for a Maven library artifact by combining the repository URL,
     * the library's path, and the library's file name.
     *
     * @param repo the base URL of the Maven repository where the library is hosted.
     * @param library the MavenObject representing the library, including its groupId, artifactId, and version.
     * @return a URL object pointing to the specific library artifact in the repository.
     * @throws MalformedURLException if the constructed URL is invalid.
     */
    private static URL getUrl(String repo, MavenObject library) throws MalformedURLException {
        return new URL(repo + "/" + getPath(library) + getFileName(library));
    }

    /**
     * Constructs the relative path of a Maven library based on its groupId, artifactId, and version.
     * The path is formatted as "groupId/artifactId/version", where the groupId components are
     * separated by slashes instead of dots.
     *
     * @param library the MavenObject representing the library, which contains information such as
     *                groupId, artifactId, and version.
     * @return the constructed path as a string, formatted with slashes separating the components.
     */
    private static String getPath(MavenObject library) {
        return library.getGroupId().replaceAll("\\.", "/") + "/" + library.getArtifactId() + "/" + library.getVersion() + "/";
    }

    /**
     * Generates the file name for a library artifact based on its artifact ID and version.
     * The file name is constructed in the format "artifactId-version.jar".
     *
     * @param library the MavenObject representing the library, which contains details such as
     *                artifactId and version used to construct the file name.
     * @return the constructed file name as a string.
     */
    private static String getFileName(MavenObject library) {
        return library.getArtifactId() + "-" + library.getVersion() + ".jar";
    }

    /**
     * Creates the directory structure needed to store a library file and returns the final file location.
     * This method ensures that a base directory and the required nested directories for the library's
     * storage are created. If the directories cannot be created, an IOException is thrown.
     *
     * @param library the MavenObject representing the library for which the write location is being created.
     *                This includes details such as groupId, artifactId, and version, which are used to
     *                determine the directory structure and file name.
     * @return a File object pointing to the location where the library file should be written. The location
     *         includes the directory structure based on the library details and the constructed file name.
     * @throws IOException if the directory structure cannot be created.
     */
    private static File createAndGetWriteLocation(MavenObject library) throws IOException {
        File rootDir = new File(".libs");
        if ((!rootDir.exists() || !rootDir.isDirectory()) && !rootDir.mkdir()) {
            throw new IOException("Could not create root directory .libs");
        }
        File path = new File(rootDir, getPath(library));
        path.mkdirs();
        return new File(path, getFileName(library));
    }

    /**
     * Adds the specified file to the system classloader by converting it to a URL.
     *
     * @param file the file to be added to the system classloader. It must represent an accessible
     *             file on the file system.
     * @throws IOException if an error occurs while converting the file to a URL or adding it
     *                     to the system classloader.
     */
    private static void addFile(File file) throws IOException {
        addURL(file.toURI().toURL());
    }

    /**
     * Adds the specified URL to the system classloader.
     *
     * @param url the URL to be added to the system classloader. It must represent a valid resource
     *            that can be accessed by the classloader.
     * @throws IOException if an error occurs while attempting to add the URL to the system classloader.
     */
    private static void addURL(URL url) throws IOException {
        URLClassLoader sysLoader = Core.getInstance().getCoreClassLoader();
        Class<URLClassLoader> sysClass = URLClassLoader.class;
        try {
            Method method = sysClass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysLoader, url);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    /**
     * Represents a Maven artifact with details required to locate and fetch it from a Maven repository.
     * This class encapsulates information about the group ID, artifact ID, version, and repository URL
     * of a Maven dependency.
     */
    private static class MavenObject {
        /**
         * The group ID of the Maven artifact, representing the organization or project that created the artifact.
         */
        @Getter private String groupId;

        /**
         * The artifact ID of the Maven artifact, representing the unique identifier of the specific component
         * within the group. It is used in combination with the group ID and version to uniquely identify a
         * Maven dependency in a repository.
         */
        @Getter private String artifactId;

        /**
         * The version of the Maven artifact, representing the specific release or iteration
         * of the artifact. It is combined with the group ID and artifact ID to uniquely identify
         * a Maven dependency in a repository.
         */
        @Getter private String version;

        /**
         * The repository URL where the Maven artifact is hosted.
         * This property specifies the location from which the Maven dependency can be fetched.
         * If not explicitly provided, a default Maven Central repository URL is typically used.
         */
        @Getter private String repo;

        /**
         * Constructs a new MavenObject with the specified group ID, artifact ID, and version.
         * The repository URL is set to the default Maven Central repository.
         *
         * @param groupId    the group identifier for the Maven artifact, representing the organization or project
         *                   that created the artifact. It is typically structured as a reversed domain name.
         * @param artifactId the artifact identifier for the Maven artifact, representing the specific library
         *                   or module within the group. It is unique within the context of the group.
         * @param version    the version of the Maven artifact, representing the specific release or iteration
         *                   of the artifact to be used.
         */
        MavenObject(String groupId, String artifactId, String version) {
            this(groupId, artifactId, version, "https://repo1.maven.org/maven2");
        }

        /**
         * Constructs a new MavenObject with the specified group ID, artifact ID, version, and repository URL.
         *
         * @param groupId    the group identifier for the Maven artifact, representing the organization or project
         *                   that created the artifact. It is typically structured as a reversed domain name.
         * @param artifactId the artifact identifier for the Maven artifact, representing the specific library
         *                   or module within the group. It is unique within the context of the group.
         * @param version    the version of the Maven artifact, representing the specific release or iteration
         *                   of the artifact to be used.
         * @param repo       the repository URL where the Maven artifact is hosted, specifying the location from
         *                   which the dependency can be fetched.
         */
        MavenObject(String groupId, String artifactId, String version, String repo) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.repo = repo;
        }
    }
}
