package network.palace.core.plugin.processor;

import network.palace.core.plugin.PluginInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * The PluginInfoProcessor class is responsible for processing the
 * {@link PluginInfo} annotation during the Java annotation processing stage.
 * This class generates a `plugin.yml` file by extracting metadata from
 * types annotated with the {@link PluginInfo} annotation.
 *
 * <h2>Core Responsibilities</h2>
 * - Identifies and validates the annotated plugin class, ensuring specific
 *   conditions are met (e.g., the class is a subclass of JavaPlugin).
 * - Extracts and processes metadata for attributes like `name`, `version`,
 *   `depend`, and others defined in the {@link PluginInfo} annotation.
 * - Outputs the metadata into a `plugin.yml` file in the CLASS_OUTPUT location.
 *
 * <h2>Processing Annotations</h2>
 * The processor checks that:
 * - Only one type is annotated with {@link PluginInfo}.
 * - The annotated type is a subclass of JavaPlugin.
 * - The annotated type is either a top-level class or a static nested class.
 * - The metadata values are extracted successfully, with fallback defaults if
 *   not explicitly declared.
 *
 * <h2>Generated Output</h2>
 * The processor generates a `plugin.yml` file containing plugin metadata such as:
 * - Plugin name
 * - Author name
 * - Version
 * - Dependencies (both required and optional)
 * - API compatibility version
 * - Fully qualified name of the main plugin class
 *
 * <h2>Annotation Support</h2>
 * This processor supports annotations from the `network.palace.core.plugin` package
 * and works with the Java SE 11 source version.
 */
@SupportedAnnotationTypes("network.palace.core.plugin.*")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class PluginInfoProcessor extends AbstractProcessor {

    /**
     * Indicates whether the main class of the plugin has been successfully
     * identified in the processing phase.
     *
     * This flag is used internally during annotation processing to track
     * whether an appropriate "main" class has been found for a plugin.
     * If this variable is set to {@code true}, the processing logic has
     * located and validated the main class definition of the plugin.
     * If it remains {@code false}, no valid main class has been identified,
     * and further error handling may occur.
     */
    private boolean hasMainBeenFound = false;

    /**
     * Processes the set of annotation types on the elements within the given round's environment.
     * This method identifies elements annotated with {@code @PluginInfo}, validates them, and
     * generates a YAML configuration file (`plugin.yml`) for a plugin based on processing results.
     *
     * @param annots a set of annotation types to process
     * @param rEnv the environment for information about the current and prior round
     * @return {@code true} if the annotations were successfully processed and a `plugin.yml`
     *         file was generated; {@code false} if processing was aborted due to errors
     */
    @Override
    public boolean process(Set<? extends TypeElement> annots, RoundEnvironment rEnv) {
        Element pluginInfo = null;
        for (Element el : rEnv.getElementsAnnotatedWith(PluginInfo.class)) {
            if (pluginInfo != null) {
                raiseError("More than one class with @PluginInfo found, aborting!");
                return false;
            }
            pluginInfo = el;
        }
        if (pluginInfo == null) return false;

        if (hasMainBeenFound) {
            raiseError("More than one class with @PluginInfo found, aborting!");
            return false;
        }
        hasMainBeenFound = true;

        TypeElement mainType;
        if (pluginInfo instanceof TypeElement) {
            mainType = (TypeElement) pluginInfo;
        } else {
            raiseError("Element annotated with @Main is not a type!");
            return false;
        }
        if (!(mainType.getEnclosingElement() instanceof PackageElement) && !mainType.getModifiers().contains(Modifier.STATIC)) {
            raiseError("Element annotated with @Main is not top-level or static nested!");
            return false;
        }
        if (!processingEnv.getTypeUtils().isSubtype(mainType.asType(), fromClass(JavaPlugin.class))) {
            raiseError("Class annotated with @Main is not an subclass of JavaPlugin!");
        }

        // Process All
        final String mainName = mainType.getQualifiedName().toString();

        String name = process("name", mainType, mainName.substring(mainName.lastIndexOf('.') + 1), PluginInfo.class, String.class);
        String author = "Palace Network";
        String version = process("version", mainType, "1.0.0", PluginInfo.class, String.class);
        String apiVersion = process("api-version", mainType, "", PluginInfo.class, String.class);
        String[] depend = process("depend", mainType, null, PluginInfo.class, String[].class);
        String[] softdepend = process("softdepend", mainType, null, PluginInfo.class, String[].class);
        final ProcessedPluginInfo processedPluginInfo = new ProcessedPluginInfo(name, author, version, apiVersion, depend, softdepend, mainName);

        // Save to plugin.yml
        try {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
            Writer writer = file.openWriter();
            try {
                Yaml yaml = new Yaml(processedPluginInfo.getRepresenter(), new DumperOptions());
                yaml.dump(processedPluginInfo.toYamlMap(), writer);
            } finally {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    /**
     * Logs an error message during the annotation processing phase.
     *
     * @param message the error message to be logged
     */
    private void raiseError(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }

    /**
     * Converts a given {@code Class} object into a {@code TypeMirror}, which represents
     * a type in the Java programming language during annotation processing.
     *
     * @param clazz the {@code Class} object to convert to a {@code TypeMirror}
     * @return the {@code TypeMirror} corresponding to the provided {@code Class} object
     *         or null if the class cannot be resolved
     */
    private TypeMirror fromClass(Class<?> clazz) {
        return processingEnv.getElementUtils().getTypeElement(clazz.getName()).asType();
    }

    /**
     * Processes an annotation element and retrieves a value from it. If the annotation is absent or
     * an error occurs during processing, a default value is returned.
     *
     * @param <A> the type of the annotation
     * @param <R> the type of the return value
     * @param valueName the name of the value to retrieve from the annotation; dashes in the name will be removed
     * @param el the element annotated with the specified annotation type
     * @param defaultVal the default value to return if the annotation is not present or an error occurs
     * @param annotationType the class of the annotation to process
     * @param returnType the class of the return type expected
     * @return the processed value retrieved from the annotation, or the default value if the annotation is not present
     */
    private <A extends Annotation, R> R process(String valueName, Element el, R defaultVal, Class<A> annotationType, Class<R> returnType) {
        R result;
        A ann = el.getAnnotation(annotationType);
        if (ann == null) {
            result = defaultVal;
        } else {
            try {
                Method value = annotationType.getMethod(valueName.replaceAll("-", ""));
                Object res = value.invoke(ann);
                result = (R) (returnType == String.class ? res.toString() : returnType.cast(res));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
