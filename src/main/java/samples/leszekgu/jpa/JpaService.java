package samples.leszekgu.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class JpaService {
    private static final Logger log = LoggerFactory.getLogger(JpaService.class);

    CustomerRepository repo;
    EntityManager em;

    @Autowired
    public JpaService(CustomerRepository repo, EntityManager em) {
        this.repo = repo;
        this.em = em;
    }

    public String run() {
        log.info("Start JPA run");

        printDbData();
        optimisticLockSample();

        log.info ("JPA Finished");
        return "Go to http://localhost:8080/h2-console to see the database use db.url': jdbc:h2:mem:testdb' ";
    }

    protected void optimisticLockSample() {
        final List<Customer> joshes = repo.findAllByFirstName("Josh");
        // weak approach to to have old versions of entities
        List<Customer> joshes2 = new ArrayList<>();
        for (Customer josh : joshes) {
            joshes2.add(new Customer(josh.getId(), josh.firstName, josh.lastName, josh.getVersion()));
        }
        // end
        optimisticLockStep1(joshes);
        // step 2 with original joshes list (with old version as step 1 should increment it in db)
        optimisticLockStep2(joshes2);

        log.info("\t\tOptimistic lock example END");
    }

    @Transactional
    protected void optimisticLockStep1(List<Customer> joshes) {
        log.info("\t\tOptimistic lock example");
        // Update Josh lastName
        printCustomersWithName("Josh");
        for (Customer josh : joshes) {
            josh.lastName = "JPA New 1";
        }
        repo.saveAll(joshes);
        log.info("Now 'Josh' surname should be 'JPA New 1' and version raised");
        printCustomersWithName("Josh");
    }

    @Transactional
    protected void optimisticLockStep2(List<Customer> joshes) {
        for (Customer josh : joshes) {
            josh.lastName = "new Value 2";
            josh.setVersion(5);
        }
        repo.saveAll(joshes);
        // Update error should occur
        log.info("Now 'Josh' surname should stay as 'JPA New 1' and version not changed");
        printCustomersWithName("Josh");
    }

    private void printCustomersWithName(String name) {
        log.info("Querying for customer records where first_name = '" + name + "':");
        logQueryResult(repo.findAllByFirstName(name));
    }

    private void printDbData() {
        log.info("Querying for all records");
        log.info("Customers table");
        logQueryResult(repo.findAll());
    }

    private void logQueryResult(Iterable toLog) {
        toLog.forEach(row -> log.info(row.toString()));
    }
}
