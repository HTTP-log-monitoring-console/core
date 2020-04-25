import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLFLogParser {
    private static final Pattern REGEX = Pattern
            .compile("^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S)+ (\\S)+ (\\S)+\" (\\d{3})(.)*$");

    public CLFLogEntry parse(final String line) throws Exception {
        final Matcher matcher = REGEX.matcher(line);
        if (matcher.find()) {
            try {
                
            }
        }
    }
}