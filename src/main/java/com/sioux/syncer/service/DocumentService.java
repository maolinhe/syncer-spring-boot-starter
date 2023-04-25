// ==========================
// Copyright (c) 2023-04-23 Sioux
// All rights reserved.
// ==========================
package com.sioux.syncer.service;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

  void upsert(String index, Object doc)
      throws IOException, NoSuchFieldException, IllegalAccessException;

  void upsert(String index, String id, Object doc)
      throws IOException, NoSuchFieldException, IllegalAccessException;

  void update(String index, Object doc)
      throws NoSuchFieldException, IllegalAccessException, IOException;

  void deleteById(String index, String id) throws IOException;

  void delete(String index, Object doc)
      throws NoSuchFieldException, IllegalAccessException, IOException;

  <T> List<T> match(String index, String value, List<String> fields,
      int page, int size, String orderBy, String order, Class<T> clazz) throws IOException;

  <T> List<T> match(String index, String value, List<String> fields,
      int page, int size, Class<T> clazz) throws IOException;

  <T> List<T> match(String index, String value, int page, Class<T> clazz) throws IOException;

  <T> List<T> match(String index, String value, Class<T> clazz) throws IOException;
}
