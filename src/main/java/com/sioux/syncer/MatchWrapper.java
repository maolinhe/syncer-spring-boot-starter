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

  private static final int DEFAULT_MATCH_SIZE = 32;

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

    public MatchWrapperBuilder page(Integer page, Integer size) {
      wrapper.page = page == null || page <= 0 ? 1 : page;
      wrapper.size = size == null || size <= 0 ? DEFAULT_MATCH_SIZE : size > 1000 ? 1000 : size;
      return this;
    }

    public MatchWrapperBuilder page(Integer page) {
      wrapper.page = page == null || page <= 0 ? 1 : page;
      wrapper.size = DEFAULT_MATCH_SIZE;
      return this;
    }

    public MatchWrapperBuilder order(String orderBy, Order order) {
      wrapper.orderBy = orderBy;
      wrapper.order = order;

      return this;
    }

    public MatchWrapperBuilder order(String orderBy, String order) {
      Order o = Order.from(order);
      if (o != null) {
        return order(orderBy, o);
      }

      return this;
    }

    public MatchWrapper build() {
      return wrapper;
    }
  }
}
