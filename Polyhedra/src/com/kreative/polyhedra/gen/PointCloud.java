package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class PointCloud extends PolyhedronGen {
	public static abstract class UnaryOp {
		public abstract void op(List<Point3D> src, List<Point3D> dst);
		public final List<Point3D> op(List<Point3D> src) {
			List<Point3D> dst = new ArrayList<Point3D>();
			op(src, dst);
			return dst;
		}
	}
	
	public static final class PermuteOrder extends UnaryOp {
		private final boolean e, o;
		public PermuteOrder(String op) {
			op = op.toLowerCase();
			boolean e = op.contains("e");
			boolean o = op.contains("o");
			this.e = e || (!e && !o);
			this.o = o || (!e && !o);
		}
		public PermuteOrder(boolean e, boolean o) {
			this.e = e || (!e && !o);
			this.o = o || (!e && !o);
		}
		public final void op(List<Point3D> src, List<Point3D> dst) {
			for (Point3D p : src) {
				if (e) dst.add(new Point3D(p.getX(), p.getY(), p.getZ()));
				if (o) dst.add(new Point3D(p.getX(), p.getZ(), p.getY()));
				if (o) dst.add(new Point3D(p.getY(), p.getX(), p.getZ()));
				if (e) dst.add(new Point3D(p.getY(), p.getZ(), p.getX()));
				if (e) dst.add(new Point3D(p.getZ(), p.getX(), p.getY()));
				if (o) dst.add(new Point3D(p.getZ(), p.getY(), p.getX()));
			}
		}
	}
	
	public static final class PermuteSign extends UnaryOp {
		private final boolean x, y, z, e, o;
		public PermuteSign(String op) {
			op = op.toLowerCase();
			boolean x = op.contains("x");
			boolean y = op.contains("y");
			boolean z = op.contains("z");
			boolean e = op.contains("e");
			boolean o = op.contains("o");
			this.x = x || (!x && !y && !z);
			this.y = y || (!x && !y && !z);
			this.z = z || (!x && !y && !z);
			this.e = e || (!e && !o);
			this.o = o || (!e && !o);
		}
		public PermuteSign(boolean x, boolean y, boolean z, boolean e, boolean o) {
			this.x = x || (!x && !y && !z);
			this.y = y || (!x && !y && !z);
			this.z = z || (!x && !y && !z);
			this.e = e || (!e && !o);
			this.o = o || (!e && !o);
		}
		public final void op(List<Point3D> src, List<Point3D> dst) {
			for (Point3D p : src) {
				if (               e) dst.add(new Point3D(+p.getX(), +p.getY(), +p.getZ()));
				if (          z && o) dst.add(new Point3D(+p.getX(), +p.getY(), -p.getZ()));
				if (     y      && o) dst.add(new Point3D(+p.getX(), -p.getY(), +p.getZ()));
				if (     y && z && e) dst.add(new Point3D(+p.getX(), -p.getY(), -p.getZ()));
				if (x           && o) dst.add(new Point3D(-p.getX(), +p.getY(), +p.getZ()));
				if (x      && z && e) dst.add(new Point3D(-p.getX(), +p.getY(), -p.getZ()));
				if (x && y      && e) dst.add(new Point3D(-p.getX(), -p.getY(), +p.getZ()));
				if (x && y && z && o) dst.add(new Point3D(-p.getX(), -p.getY(), -p.getZ()));
			}
		}
	}
	
	public static final class SetAverage extends UnaryOp {
		public final void op(List<Point3D> src, List<Point3D> dst) {
			dst.add(Point3D.average(src));
		}
	}
	
	public static final class SetMaximum extends UnaryOp {
		public final void op(List<Point3D> src, List<Point3D> dst) {
			dst.add(Point3D.max(src));
		}
	}
	
	public static final class SetMinimum extends UnaryOp {
		public final void op(List<Point3D> src, List<Point3D> dst) {
			dst.add(Point3D.min(src));
		}
	}
	
	public static final class VectorDivide extends UnaryOp {
		private final double divisor;
		public VectorDivide(double divisor) {
			this.divisor = divisor;
		}
		public final void op(List<Point3D> src, List<Point3D> dst) {
			for (Point3D p : src) dst.add(p.divide(divisor));
		}
	}
	
	public static final class VectorMultiply extends UnaryOp {
		private final double multiplier;
		public VectorMultiply(double multiplier) {
			this.multiplier = multiplier;
		}
		public final void op(List<Point3D> src, List<Point3D> dst) {
			for (Point3D p : src) dst.add(p.multiply(multiplier));
		}
	}
	
	public static final class VectorNegate extends UnaryOp {
		public final void op(List<Point3D> src, List<Point3D> dst) {
			for (Point3D p : src) dst.add(p.negate());
		}
	}
	
	public static final class VectorNormalize extends UnaryOp {
		private final double magnitude;
		public VectorNormalize(double magnitude) {
			this.magnitude = magnitude;
		}
		public final void op(List<Point3D> src, List<Point3D> dst) {
			for (Point3D p : src) dst.add(p.normalize(magnitude));
		}
	}
	
	public static abstract class BinaryOp {
		public abstract void op(List<Point3D> left, List<Point3D> right, List<Point3D> dst);
		public final List<Point3D> op(List<Point3D> left, List<Point3D> right) {
			List<Point3D> dst = new ArrayList<Point3D>();
			op(left, right, dst);
			return dst;
		}
	}
	
	public static final class SetExclusiveOr extends BinaryOp {
		public void op(List<Point3D> left, List<Point3D> right, List<Point3D> dst) {
			LinkedHashSet<Point3D> a = new LinkedHashSet<Point3D>(left); a.removeAll(right);
			LinkedHashSet<Point3D> b = new LinkedHashSet<Point3D>(right); b.removeAll(left);
			dst.addAll(a); dst.addAll(b);
		}
	}
	
	public static final class SetIntersect extends BinaryOp {
		public void op(List<Point3D> left, List<Point3D> right, List<Point3D> dst) {
			LinkedHashSet<Point3D> set = new LinkedHashSet<Point3D>(left);
			set.retainAll(right); dst.addAll(set);
		}
	}
	
	public static final class SetSubtract extends BinaryOp {
		public void op(List<Point3D> left, List<Point3D> right, List<Point3D> dst) {
			LinkedHashSet<Point3D> set = new LinkedHashSet<Point3D>(left);
			set.removeAll(right); dst.addAll(set);
		}
	}
	
	public static final class SetUnion extends BinaryOp {
		public void op(List<Point3D> left, List<Point3D> right, List<Point3D> dst) {
			LinkedHashSet<Point3D> set = new LinkedHashSet<Point3D>(left);
			set.addAll(right); dst.addAll(set);
		}
	}
	
	public static final class VectorAdd extends BinaryOp {
		public void op(List<Point3D> left, List<Point3D> right, List<Point3D> dst) {
			for (Point3D a : left) for (Point3D b : right) dst.add(a.add(b));
		}
	}
	
	public static final class VectorCrossProduct extends BinaryOp {
		public void op(List<Point3D> left, List<Point3D> right, List<Point3D> dst) {
			for (Point3D a : left) for (Point3D b : right) dst.add(a.crossProduct(b));
		}
	}
	
	public static final class VectorSubtract extends BinaryOp {
		public void op(List<Point3D> left, List<Point3D> right, List<Point3D> dst) {
			for (Point3D a : left) for (Point3D b : right) dst.add(a.subtract(b));
		}
	}
	
	public static final class Parser {
		private static final Pattern TOKEN = Pattern.compile("([+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?)|(\\p{L}+)|([\\S&&\\P{Z}])");
		private static final Pattern PERMUTE_SIGN = Pattern.compile("(permute|perm|p)(signs|sign|s)((even|odd|e|o|x|y|z)*)");
		private static final Pattern PERMUTE_ORDER = Pattern.compile("(permute|perm|p)((even|odd|e|o)*)");
		private final List<String> tokens;
		private int index = 0;
		public Parser(String s) {
			tokens = new ArrayList<String>();
			Matcher m = TOKEN.matcher(s);
			while (m.find()) tokens.add(m.group());
		}
		private List<Point3D> parseFactor() {
			if (index >= tokens.size()) throw new IllegalArgumentException("Expected number but found end of input");
			String token = tokens.get(index++);
			Matcher m;
			// Parentheses
			if (token.equals("(")) {
				List<Point3D> dst = new ArrayList<Point3D>();
				while (true) {
					if (index >= tokens.size()) throw new IllegalArgumentException("Expected ) but found end of input");
					if (tokens.get(index).equals(")")) { index++; return dst; }
					dst.addAll(parseExpr());
				}
			}
			if (token.equals("[")) {
				List<Point3D> dst = new ArrayList<Point3D>();
				while (true) {
					if (index >= tokens.size()) throw new IllegalArgumentException("Expected ] but found end of input");
					if (tokens.get(index).equals("]")) { index++; return dst; }
					dst.addAll(parseExpr());
				}
			}
			if (token.equals("{")) {
				List<Point3D> dst = new ArrayList<Point3D>();
				while (true) {
					if (index >= tokens.size()) throw new IllegalArgumentException("Expected } but found end of input");
					if (tokens.get(index).equals("}")) { index++; return dst; }
					dst.addAll(parseExpr());
				}
			}
			// One-to-One Operators
			if (token.equals("+")) {
				return parseFactor();
			}
			if (token.equals("-")) {
				return new VectorNegate().op(parseFactor());
			}
			if (token.equals("%")) {
				return new VectorNormalize(1).op(parseFactor());
			}
			// Many-to-One Operators
			if (token.equalsIgnoreCase("min") || token.equalsIgnoreCase("minimum")) {
				return new SetMinimum().op(parseFactor());
			}
			if (token.equalsIgnoreCase("max") || token.equalsIgnoreCase("maximum")) {
				return new SetMaximum().op(parseFactor());
			}
			if (token.equalsIgnoreCase("avg") || token.equalsIgnoreCase("average")) {
				return new SetAverage().op(parseFactor());
			}
			// One-to-Many Operators
			if ((m = PERMUTE_SIGN.matcher(token)).matches()) {
				return new PermuteSign(m.group(3)).op(parseFactor());
			}
			if ((m = PERMUTE_ORDER.matcher(token)).matches()) {
				return new PermuteOrder(m.group(2)).op(parseFactor());
			}
			// Single Point
			try {
				double x = Double.parseDouble(token);
				if (index < tokens.size() && (tokens.get(index).matches("[.,]"))) index++;
				if (index >= tokens.size()) throw new IllegalArgumentException("Expected number but found end of input");
				token = tokens.get(index++);
				double y = Double.parseDouble(token);
				if (index < tokens.size() && (tokens.get(index).matches("[.,]"))) index++;
				if (index >= tokens.size()) throw new IllegalArgumentException("Expected number but found end of input");
				token = tokens.get(index++);
				double z = Double.parseDouble(token);
				if (index < tokens.size() && (tokens.get(index).matches("[.,:;]"))) index++;
				return Arrays.asList(new Point3D(x, y, z));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Expected number but found " + token);
			}
		}
		private List<Point3D> parseExpr() {
			List<Point3D> dst = new ArrayList<Point3D>(parseFactor());
			while (index < tokens.size()) {
				String token = tokens.get(index);
				// One-to-One Operators
				if (token.equals("*")) {
					index++;
					if (index >= tokens.size()) throw new IllegalArgumentException("Expected number but found end of input");
					token = tokens.get(index++);
					try { dst = new VectorMultiply(Double.parseDouble(token)).op(dst); continue; }
					catch (NumberFormatException e) { throw new IllegalArgumentException("Expected number but found " + token); }
				}
				if (token.equals("/")) {
					index++;
					if (index >= tokens.size()) throw new IllegalArgumentException("Expected number but found end of input");
					token = tokens.get(index++);
					try { dst = new VectorDivide(Double.parseDouble(token)).op(dst); continue; }
					catch (NumberFormatException e) { throw new IllegalArgumentException("Expected number but found " + token); }
				}
				if (token.equals("%")) {
					index++;
					if (index >= tokens.size()) throw new IllegalArgumentException("Expected number but found end of input");
					token = tokens.get(index++);
					try { dst = new VectorNormalize(Double.parseDouble(token)).op(dst); continue; }
					catch (NumberFormatException e) { throw new IllegalArgumentException("Expected number but found " + token); }
				}
				// Many-to-Many Operators
				if (token.equals("#") || token.equalsIgnoreCase("xor")) {
					index++; dst = new SetExclusiveOr().op(dst, parseFactor()); continue;
				}
				if (token.equals("&") || token.equals("∩") || token.equalsIgnoreCase("and")) {
					index++; dst = new SetIntersect().op(dst, parseFactor()); continue;
				}
				if (token.equals("!") || token.equals("\\") || token.equalsIgnoreCase("not")) {
					index++; dst = new SetSubtract().op(dst, parseFactor()); continue;
				}
				if (token.equals("|") || token.equals("∪") || token.equalsIgnoreCase("or")) {
					index++; dst = new SetUnion().op(dst, parseFactor()); continue;
				}
				if (token.equals("^") || token.equals("×") || token.equalsIgnoreCase("cross")) {
					index++; dst = new VectorCrossProduct().op(dst, parseFactor()); continue;
				}
				if (token.equals("-")) {
					index++; dst = new VectorSubtract().op(dst, parseFactor()); continue;
				}
				if (token.equals("+")) {
					index++; dst = new VectorAdd().op(dst, parseFactor()); continue;
				}
				break;
			}
			return dst;
		}
		public void parse(List<Point3D> dst) {
			while (index < tokens.size()) dst.addAll(parseExpr());
		}
		public List<Point3D> parse() {
			List<Point3D> dst = new ArrayList<Point3D>();
			while (index < tokens.size()) dst.addAll(parseExpr());
			return dst;
		}
	}
	
	private final List<Point3D> points;
	
	public PointCloud(Point3D... points) {
		this.points = new ArrayList<Point3D>(Arrays.asList(points));
	}
	
	public PointCloud(Collection<Point3D> points) {
		this.points = new ArrayList<Point3D>(points);
	}
	
	public PointCloud(String s) {
		this.points = new Parser(s).parse();
	}
	
	public PointCloud(String[] args) {
		StringBuffer sb = new StringBuffer();
		for (String arg : args) { sb.append(arg); sb.append(" "); }
		this.points = new Parser(sb.toString()).parse();
	}
	
	public Polyhedron gen() {
		return new Polyhedron(points, Arrays.<List<Integer>>asList(), Arrays.<Color>asList());
	}
	
	public static class Factory extends PolyhedronGen.Factory<PointCloud> {
		public String name() { return "PointCloud"; }
		
		public PointCloud parse(String[] args) {
			try {
				PointCloud p = new PointCloud(args);
				return p.points.isEmpty() ? null : p;
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		
		public Option[] options() {
			return new Option[] {
				new Option(Mult.REPEATED_REQUIRED, Type.TEXT, "list of points")
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}