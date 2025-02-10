package hash;

public interface HashFunction<K> {
    int hash(K key, int tableSize);
}
