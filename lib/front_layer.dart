import 'package:flutter/material.dart';
import 'package:tesserapp/canvas_4d.dart';
import 'package:tesserapp/generic/angle.dart';

class FrontLayer extends StatefulWidget {
  @override
  FrontLayerState createState() => FrontLayerState();
}

class FrontLayerState extends State<FrontLayer> {
  CameraPosition cameraPosition;

  @override
  void initState() {
    super.initState();
    cameraPosition = CameraPosition(distance: 10.0);
  }

  @override
  Widget build(final BuildContext context) => GestureDetector(
        onPanUpdate: (details) {
          setState(() {
            cameraPosition.polar = cameraPosition.polar +
                Angle.fromRadians(-details.delta.dx * 0.01);
            cameraPosition.azimuth = cameraPosition.azimuth +
                Angle.fromRadians(details.delta.dy * 0.01);
          });
        },
        child: Canvas4d(
          color: Theme.of(context).accentColor,
          cameraPosition: cameraPosition,
          faces: cube(Position.zero(), 2.0) +
              cube(Position(3.0, 0.0, 0.0), 1.0),
//          faces: const <Face>[
//            Face(
//              Position(0.0, 1.0, 0.0),
//              Position(0.0, -1.0, -1.0),
//              Position(0.0, -1.0, 1.0),
//            ),
//          ],
        ),
      );
}
