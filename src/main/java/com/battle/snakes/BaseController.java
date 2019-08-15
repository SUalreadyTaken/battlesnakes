package com.battle.snakes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;

@Slf4j
public class BaseController {

  @RequestMapping(value = "/ping", method = RequestMethod.POST)
  public Object ping() {
    return new HashMap<String, Object>();
  }
}
