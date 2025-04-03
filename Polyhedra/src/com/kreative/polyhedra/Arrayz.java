package com.kreative.polyhedra;

import java.util.HashMap;
import java.util.Map;

public final class Arrayz {
	public static final class MapEntry<K,V> {
		private final K k;
		private final V v;
		private MapEntry(K k, V v) {
			this.k = k;
			this.v = v;
		}
	}
	
	public static final <K,V> MapEntry<K,V> mapEntry(K k, V v) {
		return new MapEntry<K,V>(k, v);
	}
	
	@SafeVarargs
	public static final <K,V> Map<K,V> asMap(MapEntry<K,V>... entries) {
		HashMap<K,V> map = new HashMap<K,V>();
		for (MapEntry<K,V> e : entries) map.put(e.k, e.v);
		return map;
	}
	
	private Arrayz() {}
}