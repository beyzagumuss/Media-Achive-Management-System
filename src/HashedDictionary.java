import probing.ProbingStrategy;
import hash.HashFunction;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashedDictionary<K, V> implements DictionaryInterface<K, V> {
    // The dictionary:
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 101;     // Must be prime
    private static final int MAX_CAPACITY = 200000;

    // The hash table:
    private Entry<K, V>[] hashTable;
    private static final int MAX_SIZE = 2 * MAX_CAPACITY;
    private boolean integrityOK = false;
    private double MAX_LOAD_FACTOR; // Fraction of hash table

    private HashFunction hashFunction;private ProbingStrategy probingStrategy;
    long collisionCount = 0;
    private int q;

    public HashedDictionary(HashFunction hashFunction, ProbingStrategy probingStrategy, double loadFactor) {
        this(DEFAULT_CAPACITY, hashFunction, probingStrategy, loadFactor); // Call next constructor
    } // end default constructor

    public HashedDictionary(int initialCapacity, HashFunction hashFunction, ProbingStrategy probingStrategy, double loadFactor) {
        initialCapacity = checkCapacity(initialCapacity);
        numberOfEntries = 0;    // Dictionary is empty

        // Set up hash table:
        // Initial size of hash table is same as initialCapacity if it is prime;
        // otherwise increase it until it is prime size
        int tableSize = getNextPrime(initialCapacity);
        checkSize(tableSize);// Check that size is not too large
        q = getNextPrime(tableSize / 2);

        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        Entry<K, V>[] temp = (Entry<K, V>[]) new Entry[tableSize];
        hashTable = temp;
        integrityOK = true;

        this.MAX_LOAD_FACTOR= loadFactor;
        this.hashFunction = hashFunction;
        this.probingStrategy = probingStrategy;
    } // end constructor

    // -------------------------
    // We've added this method to display the hash table for illustration and testing
    // -------------------------
    public void displayHashTable() {
        checkIntegrity();
        for (int index = 0; index < hashTable.length; index++) {
            if (hashTable[index] == null)
                System.out.println("null ");
            else {
                Entry<K, V> entry = hashTable[index];
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
        } // end for
        System.out.println();
    } // end displayHashTable
    // -------------------------
    private int getHashIndex(K key) {
        return hashFunction.hash(key, hashTable.length);
    }
    public V add(K key, V value) {
        checkIntegrity();
        if ((key == null) || (value == null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        else {
            V oldValue = null; // Value to return
            int index = getHashIndex(key);
            index = probe(index, key); // Check for and resolve collision
            // Assertion: index is within legal range for hashTable
            assert (index >= 0) && (index < hashTable.length);
            if ((hashTable[index] == null)) { // Key not found, so insert new entry
                hashTable[index] = new Entry<>(key, value);
                numberOfEntries++;
                oldValue = null;
            } else { // Key found; get old value for return and then replace it
                oldValue = hashTable[index].getValue();
                if (value instanceof Media) {
                    Media mediaToChange = (Media) oldValue;
                    Media temp = (Media) value;
                    mediaToChange.addPlatform(temp.getPlatformsAsString(), temp.getAvailableCountriesAsString());
                }
            }
            if (isHashTableTooFull()) {
                int newSize = getNextPrime(hashTable.length * 2);
                checkSize(newSize);
                resize(newSize);
            }

            return oldValue;
        } // end if
    } // end add

    private int probe(int hashIndex, K key) {
        int step = 0;
        int index = hashIndex;
        while (hashTable[index] != null && !hashTable[index].getKey().equals(key)) {
            if (hashTable[index] != null && !hashTable[index].getKey().equals(key)) { // If there is a collision
                collisionCount++;
            }
            step++;
            index = probingStrategy.probe(hashIndex, step, hashTable.length, q);
            if (index < 0) {
                index += hashTable.length;
            }
        }
        return index;
    }

    public V remove(K key) {
        checkIntegrity();
        V removedValue = null;
        int index = getHashIndex(key);
        index = locate(index, key);
        if (index != -1) {// Key found; flag entry as removed and return its value
            removedValue = hashTable[index].getValue();
            hashTable[index] = null;
            numberOfEntries--;
        }// end if
        // Else key not found; return null
        return removedValue;
    } // end remove

    private int locate(int hashIndex, K key) {
        int step = 0;
        int index = hashIndex;
        boolean found = false;
        while (!found && (hashTable[index] != null)) {
            if (hashTable[index] != null && key.equals(hashTable[index].getKey())) {
                found = true; // Key found
            } else { // Follow probe sequence
                step++;
                index = probingStrategy.probe(hashIndex, step, hashTable.length, q);
            }
            if (index < 0) {
                index += hashTable.length;
            }
        }
        return found ? index : -1;
    }


    public V getValue(K key) {
        checkIntegrity();
        V result = null;
        int index = getHashIndex(key);
        index = locate(index, key);
        if (index != -1)
            result = hashTable[index].getValue(); // Key found; get value
        // Else key not found; return null
		/*
		if(result == null)
			System.out.println("\nMedia not found for id: " + key + "\n");
		*/
        return result;
    } // end getValue

    public boolean contains(K key) {
        return getValue(key) != null;
    } // end contains

    public boolean isEmpty() {
        return numberOfEntries == 0;
    } // end isEmpty

    public int getSize() {
        return numberOfEntries;
    } // end getSize

    public final void clear() {
        checkIntegrity();
        for (int index = 0; index < hashTable.length; index++)
            hashTable[index] = null;

        numberOfEntries = 0;
    } // end clear


    public Iterator<K> getKeyIterator() {
        return new KeyIterator();
    } // end getKeyIterator

    public Iterator<V> getValueIterator() {
        return new ValueIterator();
    } // end getValueIterator


    // Increases the size of the hash table to a prime >= twice its old size.
    // In doing so, this method must rehash the table entries.
    // Precondition: checkIntegrity has been called.
    private void resize(int capacity) {
        Entry<K, V>[] oldTable = hashTable;
        int oldSize = hashTable.length;


        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        Entry<K, V>[] tempTable = (Entry<K, V>[]) new Entry[capacity]; // Increase size of array
        hashTable = tempTable;
        numberOfEntries = 0; // Reset number of dictionary entries, since
        // it will be incremented by add during rehash
        q = getNextPrime(hashTable.length / 2);

        // Rehash dictionary entries from old array to the new and bigger array;
        // skip both null locations and removed entries
        for (int index = 0; index < oldSize; index++) {
            if (oldTable[index] != null) {
                Entry<K, V> entry = oldTable[index];
                add(entry.getKey(), entry.getValue());
            }

        } // end for
    } // end resize


    // Returns true if lambda > MAX_LOAD_FACTOR for hash table;
    // otherwise returns false.
    private boolean isHashTableTooFull() {
        return numberOfEntries > MAX_LOAD_FACTOR * hashTable.length;
    } // end isHashTableTooFull

    // Returns a prime integer that is >= the given integer.
    private int getNextPrime(int integer) {
        // if even, add 1 to make odd
        if (integer % 2 == 0) {
            integer++;
        } // end if

        // test odd integers
        while (!isPrime(integer)) {
            integer = integer + 2;
        } // end while

        return integer;
    } // end getNextPrime

    // Returns true if the given integer is prime.
    private boolean isPrime(int integer) {
        boolean result;
        boolean done = false;

        // 2 and 3 are prime
        if ((integer == 2) || (integer == 3)) {
            result = true;
        }

        // 1 and even numbers are not prime
        else if ((integer == 1) || (integer % 2 == 0)) {
            result = false;
        } else // integer is odd and >= 5
        {
            assert (integer % 2 != 0) && (integer >= 5);

            // a prime is odd and not divisible by every odd integer up to its square root
            result = true; // assume prime
            for (int divisor = 3; !done && (divisor * divisor <= integer); divisor = divisor + 2) {
                if (integer % divisor == 0) {
                    result = false; // divisible; not prime
                    done = true;
                } // end if
            } // end for
        } // end if

        return result;
    } // end isPrime

    // Throws an exception if this object is not initialized.
    private void checkIntegrity() {
        if (!integrityOK)
            throw new SecurityException("HashedDictionary object is corrupt.");
    } // end checkIntegrity

    // Ensures that the client requests a capacity
    // that is not too small or too large.
    private int checkCapacity(int capacity) {
        if (capacity < DEFAULT_CAPACITY)
            capacity = DEFAULT_CAPACITY;
        else if (capacity > MAX_CAPACITY)
            throw new IllegalStateException("Attempt to create a dictionary " +
                    "whose capacity is larger than " +
                    MAX_CAPACITY);
        return capacity;
    } // end checkCapacity

    // Throws an exception if the hash table becomes too large.
    private void checkSize(int size) {
        if (size > MAX_SIZE)
            throw new IllegalStateException("Dictionary has become too large.");
    } // end checkSize


    private class ValueIterator implements Iterator<V> {
        private int currentIndex;
        private int numberLeft;

        private ValueIterator() {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        }

        @Override
        public boolean hasNext() {
            return numberLeft > 0;
        }

        @Override
        public V next() {
            V result = null;
            if (hasNext()) {
                while (hashTable[currentIndex] == null) {
                    currentIndex++;
                }

                result = hashTable[currentIndex].getValue();
                currentIndex++;
                numberLeft--;

            } else {
                throw new NoSuchElementException();
            }
            return result;
        }
    }

    private class KeyIterator implements Iterator<K> {
        private int currentIndex;
        private int numberLeft;

        private KeyIterator() {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        }

        @Override
        public boolean hasNext() {
            return numberLeft > 0;
        }

        @Override
        public K next() {
            K result = null;
            if (hasNext()) {
                while (currentIndex < hashTable.length && hashTable[currentIndex] == null) {
                    currentIndex++;
                }

                result = hashTable[currentIndex].getKey();
                currentIndex++;
                numberLeft--;

            } else {
                throw new NoSuchElementException();
            }
            return result;
        }
    }

    public class Entry<K, V> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

    }
} // end HashedDictionary

