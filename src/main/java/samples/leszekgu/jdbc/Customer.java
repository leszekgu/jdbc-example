package samples.leszekgu.jdbc;

public class Customer {
    private long id;
    private String firstName, lastName;
    private long version;

    public Customer(long id, String firstName, String lastName, long version) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s', version='%d']",
                id, firstName, lastName, version);
    }

    // getters & setters omitted for brevity
}

