import java.util.Random;

class Individual_NSGA_II extends Individual {

    Individual_NSGA_II(int dimension) {
        super(dimension);
    }

    private Individual_NSGA_II(short[] route, int generation) {
        super(route, generation);
    }

    void mutate(float mutProb) {
        for(int i = 0; i < route.length - 1; i++) {
            if(Math.random() < mutProb) {
                int swapIndex = new Random().nextInt(route.length - 1);
                short temp = route[i];
                route[i] = route[swapIndex];
                route[swapIndex] = temp;
            }
        }
        route[route.length - 1] = route[0];

        setPackingPlanAndFitness();
    }

    Individual_NSGA_II[] cycleCrossing(Individual_NSGA_II parent2, double crossProb, int generation) {
        short[] p2 = parent2.getRoute();
        short[] ch1 = new short[route.length];
        short[] ch2 = new short[route.length];

        if (Math.random() < crossProb) {
            short[] route1 = new short[p2.length - 1];
            short[] route2 = new short[p2.length - 1];
            for (int i = 0; i < route1.length; i++) {
                route1[i] = route[i];
                route2[i] = p2[i];
            }
            short[] child1 = new short[route1.length];
            short[] child2 = new short[route2.length];

            for (int i = 0; i < child1.length; i++) {
                child1[i] = -1;
                child2[i] = -1;
            }
            int beginningValue = route1[0];
            int currentInd = 0;

            boolean isSwapTurn = false;
            while (true) {
                assignGens(isSwapTurn, currentInd, route1, route2, child1, child2);
                if (route1[currentInd] == route2[currentInd]) {
                    isSwapTurn = !isSwapTurn;
                }
                currentInd = findIndexOfaValue(route2[currentInd], route1);
                if (route2[currentInd] == beginningValue) {
                    assignGens(isSwapTurn, currentInd, route1, route2, child1, child2);
                    currentInd = findFirstEmpty(child1);
                    if (currentInd == -1) {
                        break;
                    }
                    beginningValue = route1[currentInd];
                    isSwapTurn = !isSwapTurn;
                }
            }
            ch1 = addLastCity(child1);
            ch2 = addLastCity(child2);
        } else {
            for (int i = 0; i < ch1.length; i++) {
                ch1[i] = route[i];
                ch2[i] = p2[i];
            }
        }
        return new Individual_NSGA_II[]{
                new Individual_NSGA_II(ch1, generation),
                new Individual_NSGA_II(ch2, generation)
        };
    }

    private void assignGens(boolean isSwapTurn, int currentInd, short[] route1, short[] route2, short[] child1, short[] child2) {
        if (!isSwapTurn) {
            child1[currentInd] = route1[currentInd];
            child2[currentInd] = route2[currentInd];
        } else {
            child1[currentInd] = route2[currentInd];
            child2[currentInd] = route1[currentInd];
        }
    }

    private short[] addLastCity(short[] child) {
        short[] ch = new short[child.length + 1];
        System.arraycopy(child, 0, ch, 0, child.length);
        ch[ch.length - 1] = ch[0];
        return ch;
    }

    private short findFirstEmpty(short[] route) {
        short firstEmpty = -1;
        for (int i = 0; i < route.length; i++) {
            if (route[i] == -1) {
                firstEmpty = (short)i;
                break;
            }
        }
        return firstEmpty;
    }

    private int findIndexOfaValue(int value, short[] route) {
        int index = -1;
        for (int i = 0; i < route.length; i++) {
            if (route[i] == value) {
                index = i;
                break;
            }
        }
        return index;
    }
}
