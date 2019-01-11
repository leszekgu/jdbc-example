package samples.leszekgu.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JdbcSampleService {

    private static final Logger log = LoggerFactory.getLogger(JdbcSampleService.class);
    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> new Customer(
            rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getLong("version")
    );
    JdbcTemplate jdbcTemplate;
    NamedParameterJdbcTemplate namedTemplate;

    @Autowired
    public JdbcSampleService(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedTemplate = namedTemplate;
    }

    public String run() {
        log.info("Creating tables");

        prepareDB();
        insertSampleData();
        printDbData();

        optimisticLockSample();

        return "Go to http://localhost:8080/h2-console to see the database use db.url': jdbc:h2:mem:testdb' ";
    }

    private void optimisticLockSample() {
        log.info("\t\tOptimistic lock example");
        printCustomersWithName("Josh");
        // Update Josh lastName
        int updateCount = jdbcTemplate.update(
                "UPDATE customers set last_name = ?, version = version + 1 WHERE first_name = ? and version = 0", "New 1", "Josh"
        );
        log.info("UpdatedRows: " + updateCount);
        log.info("Now 'Josh' surname should be 'New 1' and version raised");
        printCustomersWithName("Josh");

        // Update Josh lastName with fake version given
        updateCount = jdbcTemplate.update(
                "UPDATE customers set last_name = ?, version = version + 1 WHERE first_name = ? and version = 2", "New 3", "Josh"
        );
        log.info("UpdatedRows: " + updateCount);
        if (updateCount == 0) { // works only for single row update (for example this query could update 2 rows)
            log.error("Update error - either optimistic lock or row does not exist");
        }
        log.info("Now 'Josh' surname should stay as 'New 1' and version not changed");
        printCustomersWithName("Josh");

        log.info("\t\tOptimistic lock example END");
    }

    private void insertSampleData() {
        log.info("\tInserting sample data");
        // Split up the array of whole names into an array of first/last names
        List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

        log.info("\tInserting sample data END");
    }

    private void prepareDB() {
        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers(" +
                "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255), version BIGINT default 0)");
    }

    private void printCustomersWithName(String name) {
        log.info("Querying for customer records where first_name = '" + name + "':");
        logQueryResult(
                jdbcTemplate.query(
                        "SELECT id, first_name, last_name, version FROM customers WHERE first_name = ?",
                        new Object[]{name},
                        customerRowMapper
                ));
    }

    private void printDbData() {
        log.info("Querying for all records");
        log.info("Customers table");
        logQueryResult(
                jdbcTemplate.query(
                        "SELECT id, first_name, last_name, version FROM customers",
                        customerRowMapper
                ));
    }

    private void logQueryResult(List toLog) {
        toLog.forEach(row -> log.info(row.toString()));
    }
}
