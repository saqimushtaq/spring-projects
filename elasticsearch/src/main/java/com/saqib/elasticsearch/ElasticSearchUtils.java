package com.saqib.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import java.util.function.Supplier;

public class ElasticSearchUtils {

  public static Supplier<Query> querySupplier() {
    return () -> Query.of(q -> q.matchAll(matchAllQuery()));
  }

  public static MatchAllQuery matchAllQuery() {
    return new MatchAllQuery.Builder().build();
  }
}
