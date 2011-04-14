package org.arhan.nanorest.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ApplicationFactory {

  public static Application createApplication(String packageToScan) {
    Application application = new Application();
    try {
      Class[] classes = getClasses(packageToScan);
      registerPages(classes, application);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    return application;
  }

  private static void registerPages(Class[] classes, Application application) throws InstantiationException, IllegalAccessException {
    for (Class clazz : classes) {
      if (clazz.isAnnotationPresent(Page.class)) {
        @SuppressWarnings({"unchecked"})
        Page pageAnnotation = (Page) clazz.getAnnotation(Page.class);
        Map<String, Method> actions = getActions(clazz);
        application.addPage(pageAnnotation.path(), clazz.newInstance(), actions);
      }
    }
  }

  private static Map<String, Method> getActions(Class clazz) {
    Map<String, Method> actions = new HashMap<String, Method>();
    for (Method method : clazz.getMethods()) {
      if (method.isAnnotationPresent(Action.class)) {
        String actionPath = method.getAnnotation(Action.class).path();
        actions.put(actionPath, method);
      }
    }
    return actions;
  }

  private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    assert classLoader != null;
    String path = packageName.replace('.', '/');
    Enumeration<URL> resources = classLoader.getResources(path);
    List<File> dirs = new ArrayList<File>();
    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      dirs.add(new File(resource.getFile()));
    }
    ArrayList<Class> classes = new ArrayList<Class>();
    for (File directory : dirs) {
      classes.addAll(findClasses(directory, packageName));
    }
    return classes.toArray(new Class[classes.size()]);
  }

  private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
    List<Class> classes = new ArrayList<Class>();
    if (!directory.exists()) {
      return classes;
    }
    File[] files = directory.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        assert !file.getName().contains(".");
        classes.addAll(findClasses(file, packageName + "." + file.getName()));
      }
      else if (file.getName().endsWith(".class")) {
        classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
      }
    }
    return classes;
  }

}
