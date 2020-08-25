package com.sfmap.api.services.poisearch;

import com.sfmap.api.services.poisearch.PoiSearch.Query;
import com.sfmap.api.services.poisearch.PoiSearch.SearchBound;

class QueryInternal {
	public String config;
	public String resType;
	public String enc;
	public String a_k;
	public Query mQuery;
	public SearchBound mBound;

	public QueryInternal(Query q, SearchBound bnd) {
		mQuery = q;
		mBound = bnd;
	}
}
