package org.arhan.nanorest.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * The root class of the so-called framework used by {@link Dispatcher}
 *
 */
public class Application {

  private Map<String, Object> pages = new HashMap<String, Object>();
  private Map<String, Map<String, Method>> actions = new HashMap<String, Map<String, Method>>();


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

}
