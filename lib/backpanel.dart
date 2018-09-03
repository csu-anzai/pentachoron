import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:dynamic_theme/dynamic_theme.dart';

/// Houses the backdrop's back panel content.
class BackPanel extends StatelessWidget {

  const BackPanel({
    Key key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final themes = Theme.of(context);
    return ScrollConfiguration(
        behavior: NoGlowScrollBehaviour(),
        child: Scrollbar(
          child: ListView(
            physics: ClampingScrollPhysics(),
            children: <Widget>[
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Center(
                  child: Text(
                    "Geometry",
                    style: themes.textTheme.caption,
                  ),
                ),
              ),
              Row(
                mainAxisSize: MainAxisSize.max,
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Column(
                    children: <Widget>[
                      Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Text("16 Vertices"),
                      ),
                      Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Text("32 Edges"),
                      ),
                    ],
                  ),
                  Column(
                    children: <Widget>[
                      Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Text("24 Faces"),
                      ),
                      Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Text("8 Cells"),
                      ),
                    ],
                  ),
                ],
              ),
              Divider(
                height: 5.0,
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Center(
                  child: Text(
                    "Options",
                    style: themes.textTheme.caption,
                  ),
                ),
              ),
              OptionsSection(),
              Divider(
                height: 5.0,
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Center(
                  child: Text(
                    "Credits",
                    style: themes.textTheme.caption,
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Center(
                  child: Text(
                      "My hobby project to play with four dimensional spatials.\n"
                      "The app is written with Flutter.\n\n"
                      "The source code is freely available at my GitHub repository."),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Center(
                  child: FlatButton(
                    child: Text("Source code".toUpperCase()),
                    onPressed: _launchUrl,
                  ),
                ),
              )
            ],
          ),
        ),
      );
  }
}

class OptionsSection extends StatefulWidget {

  const OptionsSection({Key key,}) : super(key: key);

  @override
  _OptionsSectionState createState() => _OptionsSectionState();
}

class _OptionsSectionState extends State<OptionsSection> {
  var _darkThemeEnabled = true;
  
  _updateBrightness(final BuildContext context) {
    DynamicTheme.of(context).setBrightness(_darkThemeEnabled ? Brightness.dark : Brightness.light);
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        Material(
          type: MaterialType.transparency,
          child: InkWell(
            onTap: () {
              _darkThemeEnabled = !_darkThemeEnabled;
              _updateBrightness(context);
            },
            child: Container(
              padding:
                  const EdgeInsets.symmetric(vertical: 16.0, horizontal: 64.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  Text("Dark Theme"),
                  Checkbox(
                    value: _darkThemeEnabled,
                    onChanged: (checked) {
                      _darkThemeEnabled = checked;
                      _updateBrightness(context);
                    },
                  ),
                ],
              ),
            ),
          ),
        ),
      ],
    );
  }
}

_launchUrl() async {
  const url = "https://github.com/Jim-Eckerlein/tesserapp";
  if (await canLaunch(url)) {
    await launch(url);
  } else {
    print("Cannot launch url");
  }
}

/// A scroll behavior disabling the over-scroll glow effect,
/// as that is not desirable in the backdrop's back panel.
class NoGlowScrollBehaviour extends ScrollBehavior {
  @override
  Widget buildViewportChrome(BuildContext context, Widget child, AxisDirection axisDirection) {
    return child;
  }
}
