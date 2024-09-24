import java.io.*;
import java.util.*;

public class FlowLogParser {
    private Map<PortProtocol, String> lookup = new HashMap<>();
    private Map<String, Integer> tagCounts = new HashMap<>();
    private Map<PortProtocol, Integer> portProtocolCounts = new HashMap<>();

    public void parseLookupTable(String lookupFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(lookupFile))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int port = Integer.parseInt(parts[0]);
                    String protocol = parts[1].toLowerCase();
                    String tag = parts[2];
                    lookup.put(new PortProtocol(port, protocol), tag);
                }
            }
        }
    }

    public void parseFlowLogs(String flowLogFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(flowLogFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.trim().split("\\s+");
//                for(String i : fields){
//                    System.out.println(i + "  ");
//                }

                if (fields.length < 14) continue;

                int dstPort = Integer.parseInt(fields[6]);
               // System.out.println(dstPort);
                String protocol = getProtocol(Integer.parseInt(fields[7]));
              // System.out.println(protocol);

                PortProtocol key = new PortProtocol(dstPort, protocol);

                String tag = lookup.getOrDefault(key, "Untagged");

                tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
                portProtocolCounts.put(key, portProtocolCounts.getOrDefault(key, 0) + 1);
            }
        }
    }

    private String getProtocol(int protocolNumber) {
        switch (protocolNumber) {
            case 6: return "tcp";
            case 17: return "udp";
            case 1: return "icmp";
            case 2: return  "igmp";
            case 4: return "ipv4";
            case 20: return "HMP";
            default: return "unknown";
        }
    }

    public void writeOutput(String outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Tag Counts:");
            writer.println("Tag,Count");
            for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }

            writer.println("\nPort/Protocol Combination Counts:");
            writer.println("Port,Protocol,Count");
            for (Map.Entry<PortProtocol, Integer> entry : portProtocolCounts.entrySet()) {
                PortProtocol pp = entry.getKey();
                writer.println(pp.port + "," + pp.protocol + "," + entry.getValue());
            }
        }
    }

    public static void main(String[] args) {
        FlowLogParser parser = new FlowLogParser();
        try {
            parser.parseLookupTable("lookup_table.csv");
            parser.parseFlowLogs("flow_logs.txt");
            parser.writeOutput("output.csv");
            System.out.println("Parsing completed. Output written to output.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class PortProtocol {
        int port;
        String protocol;

        PortProtocol(int port, String protocol) {
            this.port = port;
            this.protocol = protocol;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PortProtocol that = (PortProtocol) o;
            return port == that.port && protocol.equals(that.protocol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(port, protocol);
        }
    }
}
