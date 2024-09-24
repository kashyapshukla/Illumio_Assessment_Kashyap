# Flow Log Parser

This Java program parses AWS VPC Flow Logs and maps each entry to a tag based on a lookup table.

I decided to implement the Flow Log Parser in Java. Java's robust type system, efficient data structures, and object-oriented features align well with my skillset and allow me to build a maintainable solution.

## Assumptions

1. The program supports only default log format (version 2).
2. The flow log file is a plain text file with a maximum size of 10 MB.
3. The lookup file is a CSV file with a maximum of 10,000 mappings.
4. Tag matching is case-insensitive.
5. For protocols, we assume 6 = TCP, 17 = UDP, 1 = ICMP.2= IGMP, 4 = IPV4, 20 = hmp, Other protocols are marked as "unknown".

## Requirements

- Java 8 or higher

## How to Compile and Run

1. Ensure you have the following files in the same directory:
   - `FlowLogParser.java` (the main Java file)
   - `flow_logs.txt` (the input flow log file)
   - `lookup_table.csv` (the tag lookup table)

2. Compile the Java file:
   ```
   javac FlowLogParser.java
   ```

3. Run the program:
   ```
   java FlowLogParser
   ```

4. The output will be written to `output.csv` in the same directory.

## Testing

The program was tested with the sample data provided in the problem description. Additional tests were performed with:
- Larger input files (up to 10 MB)
- Various edge cases (empty files, malformed log entries, etc.)
- Different lookup table configurations

## Analysis

The program uses HashMaps for efficient lookup of tags based on port and protocol combinations. It processes the flow log file line by line to minimize memory usage, making it suitable for handling large log files.

Time complexity is O(n + m), where n is the number of lines in the flow log file and m is the number of entries in the lookup table.

Space complexity is O(k), where k is the number of unique tags and port/protocol combinations encountered.

The Java implementation offers strong type safety and good performance for processing large datasets.
