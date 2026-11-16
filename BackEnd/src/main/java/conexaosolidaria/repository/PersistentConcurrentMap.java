package conexaosolidaria.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class PersistentConcurrentMap<K, V> extends ConcurrentHashMap<K, V> {
    private final DatabaseStore databaseStore;
    private final String entityType;

    PersistentConcurrentMap(DatabaseStore databaseStore, String entityType, Function<V, K> keyFromValue,
            Class<V> valueType) {
        this.databaseStore = databaseStore;
        this.entityType = entityType;
        for (V value : databaseStore.load(entityType, keyFromValue, valueType)) {
            super.put(keyFromValue.apply(value), value);
        }
    }

    @Override
    public V put(K key, V value) {
        databaseStore.save(entityType, key, value);
        return super.put(key, value);
    }
}