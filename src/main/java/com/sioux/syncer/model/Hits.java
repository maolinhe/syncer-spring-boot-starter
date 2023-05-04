package com.sioux.syncer.model;

import java.util.List;
import lombok.Data;

@Data
public class Hits<T> {

  private long totalCount;

  private List<T> data;
}
