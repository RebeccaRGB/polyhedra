package com.kreative.polyhedra;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public enum MetricAggregator {
	AVERAGE {
		public double aggregate(Iterator<Double> iterator) {
			double total = 0;
			int count = 0;
			while (iterator.hasNext()) {
				total += iterator.next();
				count++;
			}
			if (total == 0 || count == 0) return 0;
			return total / count;
		}
	},
	MAXIMUM {
		public double aggregate(Iterator<Double> iterator) {
			Double maximum = null;
			while (iterator.hasNext()) {
				double value = iterator.next();
				if (maximum == null || value > maximum) maximum = value;
			}
			if (maximum == null) return 0;
			return maximum;
		}
	},
	MINIMUM {
		public double aggregate(Iterator<Double> iterator) {
			Double minimum = null;
			while (iterator.hasNext()) {
				double value = iterator.next();
				if (minimum == null || value < minimum) minimum = value;
			}
			if (minimum == null) return 0;
			return minimum;
		}
	},
	RANGE {
		public double aggregate(Iterator<Double> iterator) {
			Double minimum = null;
			Double maximum = null;
			while (iterator.hasNext()) {
				double value = iterator.next();
				if (minimum == null || value < minimum) minimum = value;
				if (maximum == null || value > maximum) maximum = value;
			}
			if (minimum == null || maximum == null) return 0;
			return maximum - minimum;
		}
	};
	public abstract double aggregate(Iterator<Double> iterator);
	
	public static final Map<Float,Integer> createHistogram(Iterator<Double> iterator) {
		TreeMap<Float,Integer> map = new TreeMap<Float,Integer>();
		while (iterator.hasNext()) {
			Float key = iterator.next().floatValue();
			Integer value = map.get(key);
			map.put(key, ((value != null) ? (value + 1) : 1));
		}
		return map;
	}
}