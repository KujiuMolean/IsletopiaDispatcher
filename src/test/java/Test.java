import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Test {
    public static void main(String[] args) {
        LocalDate.parse("2001 02 19", DateTimeFormatter.ofPattern("yyyy M d"));
    }
}
