package me.lotiny.mea.profile.stats;

import lombok.Getter;

import java.math.RoundingMode;
import java.text.NumberFormat;

@Getter
public class Statistics {

    private final Stats kills = new Stats();
    private final Stats deaths = new Stats();
    private final Stats wins = new Stats();
    private final Stats gamePlayed = new Stats();
    private final Stats chestOpened = new Stats();

    public String getKillDeathRatio() {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);

        float output;

        if (this.deaths.getAmount() <= 0) {
            output = (float) this.kills.getAmount();
        } else {
            output = (float) this.kills.getAmount() / this.deaths.getAmount();
        }

        return numberFormat.format(output);
    }
}
