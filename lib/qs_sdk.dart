library qs_sdk;

import 'package:flutter/services.dart';

class QSSDK {
  static const EventChannel eventChannel =
      EventChannel('com.beautinow.qs.scan');

  void setup({void Function(dynamic) onEvent, Function onError}) {
    eventChannel.receiveBroadcastStream().listen(onEvent, onError: onError);
  }
}
