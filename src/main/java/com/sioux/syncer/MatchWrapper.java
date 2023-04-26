// ==========================
// Copyright (c) 2023-04-26 Sioux
// All rights reserved.
// ==========================
package com.sioux.syncer;

import lombok.Data;

@Data
public class MatchWrapper {

  protected int page;

  protected int size;

  protected String orderBy;

  protected Order order;

  private MatchWrapper() {
  }


  public static MatchWrapperBuilder builder() {
    return new MatchWrapperBuilder();
  }

  public static class MatchWrapperBuilder {

    private final MatchWrapper wrapper;

    public MatchWrapperBuilder() {
      wrapper = new MatchWrapper();
    }

    public MatchWrapperBuilder page(int page, int size) {
      wrapper.page = page;
      wrapper.size = size;

      return this;
    }

    public MatchWrapperBuilder order(String orderBy, Order order) {
      wrapper.orderBy = orderBy;
      wrapper.order = order;

      return this;
    }

    public MatchWrapper build() {
      return wrapper;
    }
  }
}
