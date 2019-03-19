package me.nathan3882.androidttrainparse.responding;

public class Service {

    private final long walk;
    private final String arrival;
    private final String departure;

    public Service(String arrival, String departure, long walk) {
        this.arrival = arrival;
        this.departure = departure;
        this.walk = walk;

    }

    public long getWalk() {
        return walk;
    }

    public String getArrival() {
        return arrival;
    }

    public String getDeparture() {
        return departure;
    }
}
