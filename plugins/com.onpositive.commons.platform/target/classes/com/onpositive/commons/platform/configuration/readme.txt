//this package provides simple API for persisting settings to preferences

Currently it supports persisting objects with fields of:
0) class should have default constructor
1) primitive types
2) types that also may be persisted (but graph structures are not supported)
3) collections collection should be parameterized inheritance is not supported in this case
4) classes should be registered if they may be accessed indirectly

TODO:
4) arrays
