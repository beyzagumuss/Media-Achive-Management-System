package hash;

public class PAFHashFunction<K> implements HashFunction<K> {

    @Override
    public int hash(K key, int tableSize) {
        int hashIndex = 0;
        String keyStr = key.toString().toLowerCase();
        int z = 33; // Prime number
        int n = keyStr.length();

        for (int i = 0; i < n; i++) {
            hashIndex += (int) ((keyStr.charAt(i) - 'a' + 1) * Math.pow(z, n - 1 - i));
        }
        hashIndex = hashIndex % tableSize;
        if (hashIndex < 0)
        {
            hashIndex = hashIndex + tableSize;
        }
        return hashIndex;
    }
}
