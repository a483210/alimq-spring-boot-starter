package cn.knowbox.book.alimq.aot;

import cn.knowbox.book.alimq.utils.RocketMqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.jar.JarEntry;

/**
 * AotHintUtils
 *
 * @author Created by gold on 2023/3/29 14:29
 * @since 1.0.0
 */
@Slf4j
public final class AotHintUtils {
    private AotHintUtils() {
    }

    /**
     * 注册方法函数，用于方法反射调用
     *
     * @param hints            hint
     * @param clazz            类型
     * @param name             方法名
     * @param parameterClasses 参数类型
     */
    public static void registerMethodCall(RuntimeHints hints,
                                          Class<?> clazz,
                                          String name,
                                          Class<?>... parameterClasses) {
        hints.reflection().registerType(clazz, builder -> {
            List<TypeReference> parameterTypes = Arrays.stream(parameterClasses)
                    .map(TypeReference::of)
                    .toList();

            builder.withMethod(name, parameterTypes, ExecutableMode.INVOKE);
        });
    }

    /**
     * 注册反射类型，用于json解析
     *
     * @param hints   hint
     * @param classes 类型
     */
    public static void registerTypeBatch(RuntimeHints hints, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            hints.reflection().registerType(clazz, RocketMqUtils.DEFAULT_MEMBER_CATEGORY);
        }
    }

    /**
     * 注册反射路径，用于json解析
     *
     * @param hints       hint
     * @param classLoader 类加载器
     * @param paths       路径
     */
    public static void registerReflexPath(RuntimeHints hints,
                                          ClassLoader classLoader,
                                          List<String> paths) {
        registerReflexPath(hints, classLoader, paths, null);
    }

    /**
     * 注册反射路径，用于json解析
     *
     * @param hints       hint
     * @param classLoader 类加载器
     * @param paths       路径
     * @param predicate   验证函数，为空则不验证
     */
    public static void registerReflexPath(RuntimeHints hints,
                                          ClassLoader classLoader,
                                          List<String> paths,
                                          Predicate<Class<?>> predicate) {
        for (String path : paths) {
            scanPathClasses(classLoader, path).forEach(it -> {
                try {
                    Class<?> type = classLoader.loadClass(it);
                    boolean test;
                    if (predicate != null) {
                        test = predicate.test(type);
                    } else {
                        test = type.getCanonicalName() != null;
                    }
                    if (test) {
                        hints.reflection().registerType(type, RocketMqUtils.DEFAULT_MEMBER_CATEGORY);
                    }
                } catch (ClassNotFoundException ignore) {
                }
            });
        }
    }

    private static final Map<String, List<String>> classCache = new ConcurrentHashMap<>();

    private static List<String> scanPathClasses(ClassLoader classLoader, String path) {
        return classCache.computeIfAbsent(path, it -> {
            Set<String> result = new HashSet<>();

            try {
                String processedPath = path.replace(".", "/");

                Enumeration<URL> urls = classLoader.getResources(processedPath);
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();

                    switch (url.getProtocol()) {
                        case "jar" -> handleJarURL(url, processedPath, result, path);
                        case "file" -> handleFileURL(url, result, path);
                        default -> log.warn("unrecognized protocol {}", url.getProtocol());
                    }
                }
            } catch (IOException e) {
                log.error("scanPathClasses failure", e);
            }

            return new ArrayList<>(result);
        });
    }

    private static void handleJarURL(URL url, String processedPath, Set<String> result, String path) throws IOException {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        Enumeration<JarEntry> entries = connection.getJarFile().entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (name.startsWith(processedPath) && name.endsWith(".class")) {
                String hintName = name.substring(processedPath.length() + 1, name.length() - 6);

                result.add(path + "." + hintName.replace(File.separator, "."));
            }
        }
    }

    private static void handleFileURL(URL url, Set<String> result, String path) {
        String parentFile = url.getFile();
        List<File> files = new ArrayList<>();
        listFiles(new File(parentFile), files);

        for (File file : files) {
            String filePath = file.getPath().substring(parentFile.length() + 1);
            if (filePath.endsWith(".class")) {
                String hintName = filePath.substring(0, filePath.length() - 6);
                result.add(path + "." + hintName.replace(File.separator, "."));
            }
        }
    }

    private static void listFiles(File file, List<File> fileList) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    listFiles(f, fileList);
                }
            }
        } else {
            if (file.getName().endsWith(".class")) {
                fileList.add(file);
            }
        }
    }
}
