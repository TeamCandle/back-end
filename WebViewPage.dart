
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

class WebViewPage extends StatefulWidget{
  const WebViewPage({super.key});

  @override
  State<WebViewPage> createState() => _WebViewPageState();
}

class _WebViewPageState extends State<WebViewPage>{

  late WebViewController _controller;

  @override
  void initState(){
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: WebView(
        initialUrl: 'http://52.79.83.190:80/user/login/kakao',
        javascriptMode: JavascriptMode.unrestricted,
        onWebViewCreated: (WebViewController controller) {
          _controller = controller;
        },
        javascriptChannels: {
          JavascriptChannel(
            name: 'tokenHandler',
            onMessageReceived: (JavascriptMessage message) {
              String _token = message.message;
              print('Token: $_token');
              //Navigator.of(context).pop(message.message);
            },
          ),
        }.toSet(),
      ),
    );
  }
}