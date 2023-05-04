// ==========================
// Copyright (c) 2023-04-23 Sioux
// All rights reserved.
// ==========================
package com.sioux.syncer.service;

import com.sioux.syncer.MatchWrapper;
import com.sioux.syncer.model.Hits;
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

  <T> Hits<T> match(String index, Object value, List<String> fields, Class<T> clazz)
      throws IOException;

  <T> Hits<T> match(String index, Object value, List<String> fields, MatchWrapper matchWrapper,
      Class<T> clazz) throws IOException;
}
