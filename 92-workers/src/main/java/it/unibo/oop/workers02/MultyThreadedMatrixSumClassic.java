package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * implementation of SumMatrix in the classic way.
 */
public final class MultyThreadedMatrixSumClassic implements SumMatrix {
    private final int nthread;

    /**
     * constructor with one element.
     * @param nthread how many threads
     */
    public MultyThreadedMatrixSumClassic(final int nthread) {
        this.nthread = nthread;
    }

    /**
     * the class in wich thread will work.
     */
    private final class Worker extends Thread {
        private final double[][] matrix;
        private final int lenghtRow;
        private final int startPos;
        private final int endPos;
        private double res;
        private int x;
        private int y;

        /**
         * constructor with three arguments.
         * @param matrix the matrix
         * @param startpos the start position
         * @param endPos the end position
         */
        Worker(final double[][] matrix, final int startpos, final int endPos) {
            super();
            this.matrix = matrix;   // NOPMD: the professor do the same in the solution
            this.startPos = startpos;
            this.endPos = endPos > (matrix.length * matrix[0].length - 1)
                ? matrix.length * matrix[0].length
                : endPos;
            this.lenghtRow = matrix[0].length;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startPos + " to position " + endPos); // NOPMD
            final int increments = endPos - startPos;
            x = startPos / lenghtRow;
            y = startPos - x * lenghtRow;
            for (int k = 0; k < increments; k++) {
                this.res = this.res + matrix[x][y];
                y++;
                if (y >= lenghtRow) {
                    y = 0;
                    x++;
                }
            }
        }

        /**
         * method for getting the result.
         * @return the result
         */
        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int totalSize = matrix.length * matrix[0].length;
        final int size = totalSize % nthread + totalSize / nthread;
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < totalSize; start += size) {
            workers.add(new Worker(matrix, start, start + size));
        }
        for (final Worker w: workers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
}
