package probing;

public interface ProbingStrategy {
    int probe(int hashIndex, int step, int tableSize, int q);
}
