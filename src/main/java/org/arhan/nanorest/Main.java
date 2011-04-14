package org.arhan.nanorest;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.arhan.nanorest.core.Dispatcher;

import java.io.File;

public class Main {

  public static void main(String[] args) throws Exception {
    Tomcat tomcat = new Tomcat();
    tomcat.setPort(8080);

    Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

    String name = "dispatcher";

    Wrapper wrapper = ctx.createWrapper();
    wrapper.setName(name);
    wrapper.setLoadOnStartup(1);
    wrapper.setServletClass(Dispatcher.class.getName());
    wrapper.addInitParameter("package-scan", "org.arhan.nanorest.pages");
    ctx.addChild(wrapper);
    ctx.addServletMapping("/*", name);

    tomcat.start();
    tomcat.getServer().await();
  }

}
