# Release Notes
* 1.0-SNAPSHOT - first release
* 1.0.1-SNAPSHOT - Add new function "getRow(String colName, String value)" to filter rows based on given column and value
* 1.0.2-SNAPSHOT - Add common abstractions required for tests with Excel scenarios
* 1.0.3-SNAPSHOT - Add utils class to handle excel with oids and columns
* 2.0.0-SNAPSHOT - Refactor Table class. Wrap HashMap(row) returned by Table in a new custom RowMap which extends HashMap and
methods to handle table row indexes
* 2.0.1-SNAPSHOT - Add new method to scenario abstract class
* 2.0.2-SNAPSHOT - Add new method to save setup status
* 2.0.3-SNAPSHOT - Better handling of opened resources