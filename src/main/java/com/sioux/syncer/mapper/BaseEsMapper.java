// ==========================
// Copyright (c) 2023-04-24 Sioux
// All rights reserved.
// ==========================
package com.sioux.syncer.mapper;

import com.sioux.syncer.MatchWrapper;
import com.sioux.syncer.annotation.Match;
import com.sioux.syncer.annotation.Push;
import java.util.List;

public abstract class BaseEsMapper<T> {

  @Push
  public void push(Object value) {
  }

  @Match
  public List<T> match(Object value) {
    return null;
  }

  @Match
  public List<T> match(Object value, List<String> matchFields) {
    return null;
  }

  @Match
  public List<T> match(Object value, List<String> matchFields, MatchWrapper matchWrapper) {
    return null;
  }
}
