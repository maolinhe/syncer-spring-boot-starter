// ==========================
// Copyright (c) 2023-04-23 Sioux
// All rights reserved.
// ==========================
package cn.maolin.syncer.service;

import com.alibaba.fastjson2.JSON;
import cn.maolin.syncer.MatchWrapper;
import cn.maolin.syncer.model.Hits;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.util.StringUtils;

@Data
@Slf4j
@SuppressWarnings("all")
public class DocumentServiceImpl implements DocumentService {

  private final RestHighLevelClient restClient;

  private static final int DEFAULT_MATCH_SIZE = 32;

  public DocumentServiceImpl(RestHighLevelClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public void upsert(String index, Object doc)
      throws IOException, NoSuchFieldException, IllegalAccessException {
    upsert(index, getId(doc), doc);
  }

  @Override
  public void upsert(String index, String id, Object doc)
      throws IOException, NoSuchFieldException, IllegalAccessException {
    IndexRequest request = new IndexRequest(index)
        .id(id)
        .source(JSON.toJSONString(doc), XContentType.JSON);
    restClient.index(request, RequestOptions.DEFAULT);
  }

  @Override
  public void update(String index, Object doc)
      throws NoSuchFieldException, IllegalAccessException, IOException {
    UpdateRequest updateRequest = new UpdateRequest(index, getId(doc));
    updateRequest.doc(JSON.toJSONString(doc), XContentType.JSON);
    restClient.update(updateRequest, RequestOptions.DEFAULT);
  }

  @Override
  public void deleteById(String index, String id) throws IOException {
    DeleteRequest deleteRequest = new DeleteRequest(index, id);
    restClient.delete(deleteRequest, RequestOptions.DEFAULT);
  }

  @Override
  public void delete(String index, Object doc)
      throws NoSuchFieldException, IllegalAccessException, IOException {
    deleteById(index, getId(doc));
  }

  private String getId(Object doc) throws NoSuchFieldException, IllegalAccessException {
    Class<?> clazz = doc.getClass();
    Field idField = clazz.getDeclaredField("id");
    if (idField == null) {
      return UUID.randomUUID().toString();
    }

    idField.setAccessible(true);

    Object id = idField.get(doc);
    if (id instanceof String || id instanceof Integer || id instanceof Long) {
      return String.valueOf(id);
    } else {
      throw new IllegalArgumentException("No unique id found");
    }
  }

  @Override
  public <T> Hits<T> match(String index, Object value, List<String> fields, Class<T> clazz)
      throws IOException {
    MatchWrapper matchWrapper = MatchWrapper.builder()
        .page(1L, DEFAULT_MATCH_SIZE)
        .build();
    return match(index, value, fields, matchWrapper, clazz);
  }

  @Override
  public <T> Hits<T> match(String index, Object value, List<String> fields,
      MatchWrapper wrapper, Class<T> clazz) throws IOException {
    if (fields.isEmpty()) {
      Hits<T> tHits = new Hits<>();
      tHits.setData(new ArrayList<>());
      return tHits;
    }

    long startIndex = (wrapper.getPage() - 1) * wrapper.getSize();
    SearchRequest searchRequest = new SearchRequest(index);
    SearchSourceBuilder sourceBuilder = searchRequest.source()
        .query(QueryBuilders.multiMatchQuery(value, fields.toArray(new String[fields.size()])))
        .from((int) startIndex).size(wrapper.getSize());

    if (StringUtils.hasText(wrapper.getOrderBy())) {
      if (SortOrder.ASC.name().equalsIgnoreCase(wrapper.getOrder().name())) {
        sourceBuilder.sort(wrapper.getOrderBy(), SortOrder.ASC);
      } else {
        sourceBuilder.sort(wrapper.getOrderBy(), SortOrder.DESC);
      }
    }

    SearchResponse response = restClient.search(searchRequest, RequestOptions.DEFAULT);
    return parse(response, clazz);
  }

  private <T> Hits<T> parse(SearchResponse response, Class<T> clazz) {
    Hits<T> dataHits = new Hits<>();

    SearchHits searchHits = response.getHits();
    dataHits.setTotalCount(searchHits.getTotalHits().value);

    ArrayList<T> data = new ArrayList<>();
    for (SearchHit hit : searchHits.getHits()) {
      data.add(JSON.parseObject(hit.getSourceAsString(), clazz));
    }
    dataHits.setData(data);
    return dataHits;
  }
}
