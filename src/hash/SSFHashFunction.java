package hash;

public class SSFHashFunction<K> implements HashFunction<K> {
    @Override
    public int hash(K key, int tableSize) {
        int hashIndex = 0;
        String keyStr = key.toString().toLowerCase();
        for (int i = 0; i < keyStr.length(); i++) {
            hashIndex += keyStr.charAt(i) - 'a' + 1;
        }
        hashIndex = hashIndex % tableSize;

        if (hashIndex < 0)
        {
            hashIndex = hashIndex + tableSize;
        }

        return hashIndex;
    }
}
