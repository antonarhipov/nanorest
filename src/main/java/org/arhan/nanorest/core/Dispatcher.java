package org.arhan.nanorest.core;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * The one and only servlet needed to dispatch actions
 *
 */
public class Dispatcher extends HttpServlet {

  private Application application;

  @Override
  public void init() throws ServletException {
    String packageToScan = getServletConfig().getInitParameter("package-scan");
    try {
      application = ApplicationFactory.createApplication(packageToScan);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Writer w = resp.getWriter();
    String servletPath = req.getRequestURI();
    w.write(application.executeRequest(servletPath.substring(1)));
    w.flush();
  }

}
