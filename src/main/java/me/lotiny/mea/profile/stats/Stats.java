package me.lotiny.mea.profile.stats;

import lombok.Data;

@Data
public class Stats {

    private int amount = 0;

    public void increase() {
        this.amount++;
    }

    public void increase(int amount) {
        this.amount = this.amount + amount;
    }

    public void decrease() {
        this.amount--;
    }

    public void decrease(int amount) {
        this.amount = Math.max(this.amount - amount, 0);
    }
}
