package org.arhan.nanorest.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The root class of the so-called framework used by {@link Dispatcher}
 *
 */
public class Application {

  private Map<String, Object> pages = new HashMap<String, Object>();
  private Map<String, Map<String, Method>> actions = new HashMap<String, Map<String, Method>>();
  private final String packageToScan;


  public Application(String packageToScan) {
    this.packageToScan = packageToScan;
  }

  protected String getPackageToScan() {
    return packageToScan;
  }

  public void addPage(String path, Object page, Map<String, Method> actions) {
    this.pages.put(path, page);
    this.actions.put(path, actions);
  }

  public String executeRequest(String path) {
    String[] pageAndAction = path.split("/");

    if (pageAndAction.length == 2) {
      return String.valueOf(invokePageAction(pageAndAction));
    }

    return path;
  }

  private Object invokePageAction(String[] pageAndAction) {
    try {
      Object page = pages.get(pageAndAction[0]);
      if (page == null) {
        // try to find new pages
        ApplicationFactory.updateApplication(this);
        page = pages.get(pageAndAction[0]);
      }
      if (page != null) {
        Method action = actions.get(pageAndAction[0]).get(pageAndAction[1]);
        if (action != null) {
          return action.invoke(page);
        }
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    throw new RuntimeException("No executable action");
  }

  private List<Class<?>> getPageClasses() {
    ArrayList<Class<?>> list = new ArrayList<Class<?>>();
    for (Map.Entry<String, Object> page : pages.entrySet()) {
      list.add(page.getValue().getClass());
    }
    return list;
  }

  public void afterHotSwap(Class<?> clazz) {
    List<Class<?>> myClasses = getPageClasses();
    for (Class<?> myClazz : myClasses) {
      if (clazz.isAssignableFrom(myClazz)) {
        try {
          ApplicationFactory.updateClassMetaInfo(this, clazz);
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        System.out.println("Nanorest: reconfigured class '" + clazz + "'.");
        break;
      }
    }
  }
}
