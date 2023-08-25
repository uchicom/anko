// (C) 2023 uchicom
package com.uchicom.memo.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class ClassUtil {
  public static <T> Set<Class<T>> listClasses(String... packageNames) {
    var classSet = new HashSet<Class<T>>();
    Stream.of(packageNames).forEach(packageName -> classSet.addAll(listClassesSet(packageName)));
    return classSet;
  }

  static <T> Set<Class<T>> listClassesSet(String packageName) {

    final String resourceName = packageName.replace('.', '/');
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final URL root = classLoader.getResource(resourceName);

    if ("file".equals(root.getProtocol())) {
      File[] files = new File(root.getFile()).listFiles((dir, name) -> name.endsWith(".class"));
      return Arrays.asList(files).stream()
          .map(file -> file.getName())
          .map(name -> name.replaceAll(".class$", ""))
          .map(name -> packageName + "." + name)
          .map(fullName -> uncheckCall(() -> (Class<T>) Class.forName(fullName)))
          .collect(Collectors.toSet());
    }
    if ("jar".equals(root.getProtocol())) {
      try (JarFile jarFile = ((JarURLConnection) root.openConnection()).getJarFile()) {
        return Collections.list(jarFile.entries()).stream()
            .map(jarEntry -> jarEntry.getName())
            .filter(name -> name.startsWith(resourceName))
            .filter(name -> name.endsWith(".class"))
            .map(name -> name.replace('/', '.').replaceAll(".class$", ""))
            .map(fullName -> uncheckCall(() -> (Class<T>) classLoader.loadClass(fullName)))
            .collect(Collectors.toSet());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new HashSet<>();
  }

  public static <T> T uncheckCall(Callable<T> callable) {
    try {
      return callable.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
