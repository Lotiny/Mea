package me.lotiny.mea.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@RequiredArgsConstructor
public class LootChest {

    private final int tier;
    private final int chance;

    private final Map<Integer, Integer> items = new ConcurrentHashMap<>();
}