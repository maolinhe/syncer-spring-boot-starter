package cn.maolin.syncer.mapper;

import cn.maolin.syncer.annotation.Match;
import cn.maolin.syncer.annotation.Push;
import cn.maolin.syncer.MatchWrapper;
import cn.maolin.syncer.model.Hits;
import java.util.List;

public abstract class BaseEsMapper<T> {

  @Push
  public void push(T value) {
  }

  @Match
  public Hits<T> match(Object value, List<String> matchFields) {
    return null;
  }

  @Match
  public Hits<T> match(Object value, List<String> matchFields, MatchWrapper matchWrapper) {
    return null;
  }
}
