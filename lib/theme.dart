import 'package:flutter/material.dart';

ThemeData tesserTheme() => ThemeData(
      primarySwatch: Colors.deepOrange,
      accentColor: Colors.deepOrangeAccent,
      fontFamily: "GeoNMS",
      brightness: Brightness.dark,
      textTheme: TextTheme(
        body1: TextStyle(
          fontWeight: FontWeight.bold,
          letterSpacing: 1.0,
        ),
        button: TextStyle(
          fontWeight: FontWeight.w900,
          letterSpacing: 1.2,
        ),
      ),
      primaryTextTheme: TextTheme(
        title: TextStyle(
          fontWeight: FontWeight.bold,
          fontSize: 24.0,
          letterSpacing: 1.2,
        ),
        body1: TextStyle(
          fontWeight: FontWeight.bold,
          letterSpacing: 1.0,
        ),
      ),
    );