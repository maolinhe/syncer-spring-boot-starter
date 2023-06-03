package cn.maolin.syncer.mapper;

import cn.maolin.syncer.annotation.Match;
import cn.maolin.syncer.annotation.Push;
import cn.maolin.syncer.model.MatchWrapper;
import cn.maolin.syncer.model.Hits;
import java.util.List;
import java.util.Map;

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

  @Match
  public Hits<T> match(Map<Object, List<String>> matchMap) {
    return null;
  }

  @Match
  public Hits<T> match(Map<Object, List<String>> matchMap, MatchWrapper matchWrapper) {
    return null;
  }

  @Match
  public Hits<T> boolMatch(Map<Object, List<String>> matchMap) {
    return null;
  }

  @Match
  public Hits<T> boolMatch(Map<Object, List<String>> matchMap, MatchWrapper matchWrapper) {
    return null;
  }
}
