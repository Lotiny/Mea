package me.lotiny.mea.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class Teleport {

    private final Player player;
    private final Location location;

}
