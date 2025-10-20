package org.github.madbrain.demo.rulez;

public class Account {
    private Status status;
    private int miles;
    private int awardedMiles;

    public Account(int miles) {
        this.miles = miles;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getMiles() {
        return miles;
    }

    public void setMiles(int miles) {
        this.miles = miles;
    }

    public int getAwardedMiles() {
        return awardedMiles;
    }

    public void setAwardedMiles(int awardedMiles) {
        this.awardedMiles = awardedMiles;
    }

    public void addAwardedMiles(int miles) {
        this.awardedMiles += miles;
    }
}
