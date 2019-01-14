package samples.leszekgu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import samples.leszekgu.jdbc.JdbcSampleService;
import samples.leszekgu.jpa.JpaService;

@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private JdbcSampleService jdbc;

    @Autowired
    private JpaService jpa;

    @Override
    public void run(String... args) throws Exception {

        jdbc.run();
        try {
            jpa.run();
        } catch (ObjectOptimisticLockingFailureException ex) {
            System.out.println("\t\t\tOptLoc");
            ex.printStackTrace(System.out);
        }

    }
}