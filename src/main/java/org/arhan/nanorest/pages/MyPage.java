package org.arhan.nanorest.pages;

import org.arhan.nanorest.core.Action;
import org.arhan.nanorest.core.Page;


@Page(path = "page")
public class MyPage {
  @Action(path = "action")
  public String execute() {
    return "<h1>Hello!</h1>" +
        "<p>NanoREST is cool!</p>";
  }

}
