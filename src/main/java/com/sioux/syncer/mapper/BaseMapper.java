// ==========================
// Copyright (c) 2023-04-24 Sioux
// All rights reserved.
// ==========================
package com.sioux.syncer.mapper;

import com.sioux.syncer.annotation.Match;
import java.util.List;

public abstract class BaseMapper<T> {

  @Match
  public List<T> match(String value) {
    return null;
  }
}
