package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class SampleController {

    private JdbcSampleService service;

    @Autowired
    public SampleController(JdbcSampleService service) {
        this.service = service;
    }

    @GetMapping("/start")
    public ResponseEntity start() {
        return ok(service.run());
    }

}
