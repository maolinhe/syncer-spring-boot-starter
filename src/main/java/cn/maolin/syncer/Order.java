// ==========================
// Copyright (c) 2023-04-26 Sioux
// All rights reserved.
// ==========================
package cn.maolin.syncer;

public enum Order {

  ASC,

  DESC;

  public static Order from(String order) {
    for (Order o : Order.values()) {
      if (o.name().equalsIgnoreCase(order)) {
        return o;
      }
    }

    return null;
  }
}
