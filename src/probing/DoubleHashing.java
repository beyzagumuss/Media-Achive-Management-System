package probing;

public class DoubleHashing implements ProbingStrategy {
    @Override
    public int probe(int hashIndex, int step, int tableSize, int q) {
        int d = (q - (hashIndex % q) + q) % q;
        if (d == 0) d = 1;
        return (hashIndex + step * d) % tableSize;
    }
}
