Database_Indexing
=================

Database Indexing based on Id, Last_name and State on Employee DataBase

This project involves the creation of a Java program (Database.java) capable of acting a database that can locate records based upon indexes on three (or more) fields.
The data is a single table of people along with their personal data. The data is provided in a file named us-500.csv. Field attribute names are provided in the first line of the data file.

This program operates entirely from the command line (no GUI).
This program stores its data in a single binary file and each index should be a separate (either binary or text) file.

This program stores its database in a file named "data.db"
Index files is named for the field(s) being indexed and have the extension .ndx, e.g. id.ndx, city.ndx.
Program reads and writes to data.db using the Java API RandomAccessFile class.

Record locations in the data.db file should be identified by the the seek(long) method, where the long variable is the byte offset location from the beginning of the file.
Index files may be either binary or text format:

Required Actions
1. Select - given a field name on which there is an index (unique or non-unique), retrieve all records that match the index and display each record on a separate line.
2. Insert - given a record in comma separated String format (with values single quoted) insert the record into data.db and create new entries in your index files.
"'502','James','Butt','Benton, John B Jr','6649 N Blue Gum St','New Orleans','Orleans','LA','70116','504-621-8927','504-845-1427','jbutt@gmail.com','http://www.bentonjohnbjr.com'"
Note: The record id field is the primary key. Like the EMPLOYEE ssn primary key, you do not need to automatically generate an id value – it should be manually provided by the DBA. Insert on duplicate record id should fail cleanly and return a useful message.
Note: Records inserted into data.db may simply be appended to the end. Only indexes require re-sorting of entries.
3. Delete - given a unique identifier (i.e. key) remove a record from data.db and remove its entry from all indexes.
Note: Deleted records may be overwritten with zeroes in data.db, but do not require re-organizing the data file. Only indexes require re-ordering of entries.
4. Delete on non-existant records should fail cleanly and return a useful message.
5. Modify - given a unique identifier, field_name, and new value of an existing record, update the old value of field_name with new value.
6. Modify of a non-existant record identifier should fail cleanly and return a useful message.
7. Count - your program should be able to return the total number of records in your database.
