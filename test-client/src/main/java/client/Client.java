package client;

import picocli.CommandLine;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Model.CommandSpec;
import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Command(name = "client", mixinStandardHelpOptions = true, version = "client 1.0",
        description = "Run getAmount and addAmount requests for account service. \n" +
                "Need to provide next arguments: -u=url -rCount=integer -wCount=integer id1 id2 id3 ...\n" +
                "Example: -u=http://localhost:8080 -rCount=2 -wCount=2  1 2 3 4")
public class Client implements Runnable {

    @Spec
    private CommandSpec spec;
    @Parameters(arity = "1..*", description = "Any positive number of id numbers. For example, id1 id2 id3 ...")
    private List<Integer> ids;
    @Option(names = "-u", required = true, description = "host url of account service's server, " +
            "like -u=http://localhost:8080")
    private String url;
    private int rCount;
    private int wCount;

    @Option(names = "-rCount", required = true, description = "Number of readers " +
            "to call in a loop getAmount method of account service")
    public void setRCountValue(int value) {
        if (value < 0) {
            throw new ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for option '-rCount': " +
                            "value must be non negative integer.", value));
        }
        rCount = value;
    }

    @Option(names = "-wCount", required = true, description = "Number of writers" +
            " to call in a loop addAmount method of account service")
    public void setWCountValue(int value) {
        if (value < 0) {
            throw new ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for option '-wCount': " +
                            "value must be non negative integer.", value));
        }
        wCount = value;
    }

    public static void main(String[] args) {
        new CommandLine(new Client()).execute(args);
    }

    @Override
    public void run() {
        HttpClient client = HttpClient.newHttpClient();
        ExecutorService executorService = Executors.newFixedThreadPool(rCount+wCount);
        for (int i = 0; i < rCount;i++) {
            int id = i < ids.size()? ids.get(i) : ids.get( i % ids.size());
            executorService.submit(new Reader(client, url, id));
        }

        for (int i = 0; i < wCount;i++) {
            int id = i < ids.size()? ids.get(i) : ids.get( i % ids.size());
            executorService.submit(new Writer(client, url, id));
        }
    }
}
