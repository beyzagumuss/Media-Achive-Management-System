package probing;

public class LinearProbing implements ProbingStrategy {
    @Override
    public int probe(int hashIndex, int step, int tableSize, int q) {
        return (hashIndex + step) % tableSize;
    }
}
