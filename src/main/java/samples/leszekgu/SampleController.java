package samples.leszekgu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import samples.leszekgu.jdbc.JdbcSampleService;
import samples.leszekgu.jpa.JpaService;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class SampleController {

    private JdbcSampleService jdbcService;
    private JpaService jpaService;

    @Autowired
    public SampleController(JdbcSampleService jdbcService, JpaService jpaService) {
        this.jdbcService = jdbcService;
        this.jpaService = jpaService;
    }


    @GetMapping("/start")
    public ResponseEntity start() {
        return ok(jdbcService.run());
    }


    @GetMapping("/jpa")
    public ResponseEntity startJpa() {
        return ok(jpaService.run());
    }

}
