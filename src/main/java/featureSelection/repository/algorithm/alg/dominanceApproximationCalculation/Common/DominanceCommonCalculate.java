package featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common;

import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daiYang_wu
 */
public class DominanceCommonCalculate {
    /**
     * 计算所有类联结
     *
     * @param dataset
     * @param decisionValues
     * @return java.util.Map<featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion, java.util.Collection < featureSelection.basic.model.universe.instance.Instance>>
     */
    public static Map<ClassUnion, Collection<Instance>> calculateClassUnionSet(Collection<Instance> dataset, List<Integer> decisionValues) {
        Collections.sort(decisionValues);
        Map<ClassUnion, Collection<Instance>> classUnionHashMap = new LinkedHashMap<>();
        for (int index = 0; index < decisionValues.size(); index++) {
            int decisionValue = decisionValues.get(index);
            Collection<Instance> upClassUnionSet = new HashSet<>();
            Collection<Instance> lowClassUnionSet = new HashSet<>();
            for (Instance ins : dataset) {
                if (ins.getAttributeValue(0) >= decisionValue)
                    upClassUnionSet.add(ins);
                if (ins.getAttributeValue(0) <= decisionValue)
                    lowClassUnionSet.add(ins);
            }
            if (index == 0) {
                classUnionHashMap.put(new ClassUnion(decisionValue, false), lowClassUnionSet);
                continue;
            }
            if (index == decisionValues.size() - 1) {
                classUnionHashMap.put(new ClassUnion(decisionValue, true), upClassUnionSet);
                continue;
            }
            // 最小决策值没有向上的类联结
            classUnionHashMap.put(new ClassUnion(decisionValue, true), upClassUnionSet);
            // 最大决策值没有向下的类联结
            classUnionHashMap.put(new ClassUnion(decisionValue, false), lowClassUnionSet);
        }
        return classUnionHashMap;
    }

    public static boolean judgeIsDominance(Instance ins1, Instance ins2, int[] attributes) {
        boolean flag = true;
        for (int attribute : attributes) {
            if (ins1.getAttributeValue(attribute) < ins2.getAttributeValue(attribute)) {
                DominanceCommonCalculate.CompareCount.add();
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static boolean judgeIsDominance(Instance ins1, Instance ins2) {
        int[] ins1Values = ins1.getConditionAttributeValues();
        int[] ins2Values = ins2.getConditionAttributeValues();

        boolean flag = true;
        for (int index = 0; index < ins1Values.length; index++) {
            if (ins1Values[index] < ins2Values[index]) {
                DominanceCommonCalculate.CompareCount.add();
                flag = false;
            }
        }
        return flag;
    }


    public static class CompareCount {
        private static long count = 0;

        public static void add() {
            count++;
        }

        public static void reset() {
            count = 0;
        }

        public static long getCount() {
            return count;
        }
    }

    public static class StringFormat {
        public static String classUnionSetToString(Map<ClassUnion, Collection<Instance>> classUnionSet) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<ClassUnion, Collection<Instance>> entry : classUnionSet.entrySet()) {
                sb.append("     ");
                sb.append(entry.getKey().toString() + " : ");
                sb.append(entry.getValue().stream().map(instance -> instance.getNum()).sorted().collect(Collectors.toList()).toString() + "," + "\n");
            }
            return sb.toString();
        }

    }

}
