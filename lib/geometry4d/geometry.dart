import 'dart:math';

import 'package:meta/meta.dart';

String get timesSymbol => "\u{22c5}";

String get lambdaSymbol => "\u{03bb}";

String get assignedSymbol => "\u{2254}";

@immutable
class Vector {
  final double x, y, z, w;

  const Vector(
    this.x,
    this.y,
    this.z, [
    this.w = 0.0,
  ]);

  const Vector.zero() : this.of(0.0);

  const Vector.of(final double c) : this(c, c, c, c);

  const Vector.ofX(final double c) : this(c, 0.0, 0.0, 0.0);

  const Vector.ofY(final double c) : this(0.0, c, 0.0, 0.0);

  const Vector.ofZ(final double c) : this(0.0, 0.0, c, 0.0);

  const Vector.ofW(final double c) : this(0.0, 0.0, 0.0, c);

  double operator [](final int index) {
    assert(index >= 0 && index < 4, "Invalid vector index $index");
    switch (index) {
      case 0:
        return x;
      case 1:
        return y;
      case 2:
        return z;
      case 3:
        return w;
      default:
        return null;
    }
  }

  factory Vector.generate(double f(int index)) {
    final components = Iterable.generate(4, f).iterator;
    return Vector(
      (components..moveNext()).current,
      (components..moveNext()).current,
      (components..moveNext()).current,
      (components..moveNext()).current,
    );
  }

  Vector.cross(final Vector a, final Vector b)
      : w = 0.0,
        x = a.y * b.z - a.z - b.y,
        y = a.z * b.x - a.x * b.z,
        z = a.x * b.y - a.y * b.x {
    assert(a.w == b.w, "Only 3d vectors can be crossed");
  }

  static double scalar(final Vector a, final Vector b) =>
      a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;

  Vector operator +(final Vector other) =>
      Vector(x + other.x, y + other.y, z + other.z, w + other.w);

  Vector operator *(final double c) => Vector(x * c, y * c, z * c, w * c);

  Vector operator -(final Vector other) => this + -other;

  Vector operator -() => Vector(-x, -y, -z, -w);

  @override
  String toString() => "[${x.toStringAsFixed(1)}, "
      "${y.toStringAsFixed(1)}, "
      "${z.toStringAsFixed(1)}, "
      "${w.toStringAsFixed(1)}]";

  double get length => sqrt(Vector.scalar(this, this));

  Vector get normalized => Vector(x / length, y / length, z / length);
}

@immutable
class Line {
  final Vector a, d;

  Line.fromDirection(this.a, this.d);

  Line.fromPoints(final Vector a, final Vector b)
      : this.fromDirection(a, b - a);

  Vector call(final double lambda) => a + d * lambda;

  @override
  String toString() => "g : x = $a + $lambdaSymbol$d";
  
  Vector get intersection {
    final lambda = -a.z / d.z;
    if (lambda >= 0.0 && lambda <= 1.0 && lambda.isFinite) {
      return this(lambda);
    } else {
      return null;
    }
  }
}

@immutable
class Triangle {
  final List<Vector> points;

  Triangle(this.points) {
    assert(points.length == 3);
  }
}

@immutable
class Tetrahedron {
  final List<Vector> points;
  final List<Line> lines;

  Tetrahedron(this.points)
      : lines = [
          Line.fromPoints(points[0], points[3]),
          Line.fromPoints(points[1], points[3]),
          Line.fromPoints(points[2], points[3]),
          Line.fromPoints(points[0], points[1]),
          Line.fromPoints(points[1], points[2]),
          Line.fromPoints(points[2], points[0])
        ] {
    assert(points.length == 4, "Each tetrahedron must have 4 points");
  }

  Triangle get intersection {
    final intersectingPoints = lines
        .map((line) => line.intersection)
        .where((line) => line != null)
        .toList();

    assert(intersectingPoints.length <= 3, "Impossible count of intersections");

    if (intersectingPoints.length < 3) {
      return null;
    } else {
      return Triangle(intersectingPoints);
    }
  }
}

/// The base four-dimensional geometry, in the same manner as the triangle
/// forms the base for 2d geometries and the tetrahedron for 3d geometries.
///
/// Reference:
/// https://en.wikipedia.org/wiki/5-cell
///
/// A pentachoron is defined through 5 cells.
@immutable
class Pentachoron {
  final List<Vector> points;
  final List<Tetrahedron> cells;

  Pentachoron(this.points)
      : cells = [
          Tetrahedron([points[0], points[1], points[2], points[3]]),
          Tetrahedron([points[0], points[1], points[2], points[4]]),
          Tetrahedron([points[1], points[2], points[3], points[4]]),
          Tetrahedron([points[2], points[3], points[1], points[4]]),
          Tetrahedron([points[3], points[1], points[2], points[4]])
        ] {
    assert(points.length == 5, "Each pentachoron must have 5 points");
  }

  /// Construct a pentachoron with the edge length 2.
  /// The base cell is origin-centered.
  Pentachoron.simple()
      : this([
          Vector(1.0, 1.0, 1.0, -1.0 / sqrt(5.0)),
          Vector(1.0, -1.0, -1.0, -1.0 / sqrt(5.0)),
          Vector(-1.0, 1.0, -1.0, -1.0 / sqrt(5.0)),
          Vector(-1.0, -1.0, 1.0, -1.0 / sqrt(5.0)),
          Vector(0.0, 0.0, 0.0, sqrt(5.0) - 1.0 / sqrt(5.0)),
        ]);

//  Tetrahedron intersected()
}
