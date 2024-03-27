package me.lotiny.mea.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class LootChest {

    private final int tier;
    private final int itemAmount;
    private final int chance;

    private final List<LootItem> lootItems = new ArrayList<>();

}
