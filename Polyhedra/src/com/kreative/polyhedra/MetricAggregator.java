package com.kreative.polyhedra;

import java.util.Iterator;

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
}