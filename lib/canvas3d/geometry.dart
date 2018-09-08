part of canvas3d;

class Geometry {
  final List<Polygon> polygons;
  final bool outlined;
  final Matrix4 transform;
  
  /// Create a geometry from a set of points to be transformed through
  /// [rotation], [translation] and [scale], and a default color.
  Geometry({
    @required final List<Polygon> polygons,
    final Color color,
    this.outlined = false,
    Rotation rotation,
    Vector3 translation,
    Vector3 scale,
  })  : transform = rotation?.transform ??
      Matrix4.identity() *
          Matrix4.translation(translation ?? Vector3.zero()),
        polygons = polygons
            .map((poly) => Polygon(
          poly.positions,
          poly.color ?? color ?? Color(0xff000000),
        ))
            .toList();
}
