# Mock Voicebox API

A mock of the [Voicebox API](http://voiceboxpdx.com/api/v1/documentation.html) for testing.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    VBAPI_ROOM_CODE=<room code to mock> lein ring server-headless

## License

Copyright © 2015 Michael Smith
