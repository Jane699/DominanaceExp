package featureSelection.repository.algorithm.alg.dominanceApproximationCalculation;

import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;

import java.util.*;

import static featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common.DominanceCommonCalculate.judgeIsDominance;

/**
 * @author daiYang_wu
 */
public class ParallelApproachCalculateApproximationsAlgorithm {


    public static class Basic {

        /**
         * 计算某个classUnion下的上近似
         *
         * @param dataset
         * @param attributes
         * @param classUnion
         * @param classUnionHashMap
         * @return java.util.Collection<featureSelection.basic.model.universe.instance.Instance>
         */
        public static Collection<Instance> calculateUpperApproximationByClassUnion(Collection<Instance> dataset, int[] attributes, ClassUnion classUnion, Map<ClassUnion, Collection<Instance>> classUnionHashMap) {
            Collection<Instance> upApproximationSet = new HashSet<>();
            Collection<Instance> classUnionDiffSet = new HashSet<>();
            classUnionDiffSet.addAll(dataset);
            classUnionDiffSet.removeAll(classUnionHashMap.get(classUnion));
            upApproximationSet.addAll(classUnionHashMap.get(classUnion));
            for (Instance cudItem : classUnionDiffSet) {
                for (Instance uaItem : classUnionHashMap.get(classUnion)) {
                    boolean isDominance;
                    if (classUnion.isBiggerOrEqual()) isDominance = judgeIsDominance(cudItem, uaItem, attributes);
                    else isDominance = judgeIsDominance(uaItem, cudItem, attributes);
                    if (isDominance) {
                        upApproximationSet.add(cudItem);
                    }
                }
            }
            return upApproximationSet;
        }

        /**
         * 求解某个classUnion下的下近似
         *
         * @param dataset
         * @param attributes
         * @param classUnion
         * @param classUnionHashMap
         * @return java.util.Collection<featureSelection.basic.model.universe.instance.Instance>
         */
        public static Collection<Instance> calculateLowerApproximationByClassUnion(Collection<Instance> dataset, int[] attributes, ClassUnion classUnion, Map<ClassUnion, Collection<Instance>> classUnionHashMap) {
            Collection<Instance> lowApproximationSet = new HashSet<>();
            Collection<Instance> classUnionDiffSet = new HashSet<>();
            classUnionDiffSet.addAll(dataset);
            classUnionDiffSet.removeAll(classUnionHashMap.get(classUnion));
            lowApproximationSet.addAll(classUnionHashMap.get(classUnion));
            for (Instance cudItem : classUnionDiffSet) {
                for (Iterator iterator = lowApproximationSet.iterator(); iterator.hasNext(); ) {
                    Instance laItem = (Instance) iterator.next();
                    boolean isDominance;
                    if (classUnion.isBiggerOrEqual()) {
                        isDominance = judgeIsDominance(cudItem, laItem, attributes);
                    } else {
                        isDominance = judgeIsDominance(laItem, cudItem, attributes);
                    }
                    if (isDominance) {
                        iterator.remove();
                    }
                }
            }
            return lowApproximationSet;
        }

        /**
         * 计算所有上近似
         *
         * @param dataset
         * @param attributes
         * @param classUnionHashMap
         * @return java.util.Map<featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion, java.util.Collection < featureSelection.basic.model.universe.instance.Instance>>
         */
        public static Map<ClassUnion, Collection<Instance>> calculateUpperApproximations(Collection<Instance> dataset, int[] attributes, Map<ClassUnion, Collection<Instance>> classUnionHashMap) {
            Map<ClassUnion, Collection<Instance>> classUnionUpperApproximationMap = new HashMap<>(classUnionHashMap.size());
            for (ClassUnion classUnion : classUnionHashMap.keySet()) {
                classUnionUpperApproximationMap.put(classUnion, calculateUpperApproximationByClassUnion(dataset, attributes, classUnion, classUnionHashMap));
            }
            return classUnionUpperApproximationMap;
        }

        /**
         * 计算所有下近似
         *
         * @param dataset
         * @param attributes
         * @param classUnionHashMap
         * @return java.util.Map<featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion, java.util.Collection < featureSelection.basic.model.universe.instance.Instance>>
         */
        public static Map<ClassUnion, Collection<Instance>> calculateLowerApproximations(Collection<Instance> dataset, int[] attributes, Map<ClassUnion, Collection<Instance>> classUnionHashMap) {
            Map<ClassUnion, Collection<Instance>> classUnionLowerApproximationMap = new HashMap<>(classUnionHashMap.size());
            for (ClassUnion classUnion : classUnionHashMap.keySet()) {
                classUnionLowerApproximationMap.put(classUnion, calculateLowerApproximationByClassUnion(dataset, attributes, classUnion, classUnionHashMap));
            }
            return classUnionLowerApproximationMap;

        }

        public static Map<ClassUnion, Collection<Instance>> classUnionByDSLAC(List<Integer> dList, Map<ClassUnion, Collection<Instance>> classUnionCollectionMap) {

            Map<ClassUnion, Collection<Instance>> res = new HashMap<>();
            Collection<Instance> firstClassUnionSet = new ArrayList<>(classUnionCollectionMap.get(new ClassUnion(dList.get(0), false)));
            res.put(new ClassUnion(dList.get(0), false), firstClassUnionSet);
            for (int i = 1; i < dList.size() - 1; i++) {
                int d = dList.get(i);
                Collection<Instance> preClassUnionSet = classUnionCollectionMap.get(new ClassUnion(dList.get(i - 1), false));
                Collection<Instance> classUnionSet = new ArrayList<>();
                for (Instance ins : classUnionCollectionMap.get(new ClassUnion(d, false))) {
                    if (!preClassUnionSet.contains(ins)) {
                        classUnionSet.add(ins);
                    }
                }
                res.put(new ClassUnion(d, false), classUnionSet);
            }


            Collection<Instance> lastClassUnionSet = new ArrayList<>(classUnionCollectionMap.get(new ClassUnion(dList.get(dList.size() - 1), true)));
            res.put(new ClassUnion(dList.get(dList.size() - 1), true), lastClassUnionSet);

            for (int i = dList.size() - 2; i > 0; i--) {
                int d = dList.get(i);
                Collection<Instance> preClassUnionSet = classUnionCollectionMap.get(new ClassUnion(dList.get(i + 1), true));
                Collection<Instance> classUnionSet = new ArrayList<>();
                for (Instance ins : classUnionCollectionMap.get(new ClassUnion(d, true))) {
                    if (!preClassUnionSet.contains(ins)) {
                        classUnionSet.add(ins);
                    }
                }
                res.put(new ClassUnion(d, true), classUnionSet);
            }


            return res;
        }


        public static Collection<Instance> calculateLowerApproximationByClassUnionByDSLAC(Collection<Instance> dataset, int[] attributes, ClassUnion classUnion, Map<ClassUnion, Collection<Instance>> classUnionHashMap, Map<ClassUnion, Collection<Instance>> DSLACClassUnionHashMap) {
            Collection<Instance> lowApproximationSet = new HashSet<>();
            Collection<Instance> classUnionDiffSet = new HashSet<>();
            classUnionDiffSet.addAll(dataset);
            classUnionDiffSet.removeAll(classUnionHashMap.get(classUnion));
            lowApproximationSet.addAll(DSLACClassUnionHashMap.get(classUnion));
            for (Instance cudItem : classUnionDiffSet) {
                for (Iterator iterator = lowApproximationSet.iterator(); iterator.hasNext(); ) {
                    Instance laItem = (Instance) iterator.next();
                    boolean isDominance;
                    if (classUnion.isBiggerOrEqual()) {
                        isDominance = judgeIsDominance(cudItem, laItem, attributes);
                    } else {
                        isDominance = judgeIsDominance(laItem, cudItem, attributes);
                    }
                    if (isDominance) {
                        System.out.println("remove : " + laItem.toString());
                        System.out.println("current low approximation set num : " + lowApproximationSet.size());
                        iterator.remove();
                    }
                }
            }

//            for (Iterator iterator = lowApproximationSet.iterator(); iterator.hasNext(); ) {
//                Instance laItem = (Instance) iterator.next();
//                for (Instance cudItem : classUnionDiffSet) {
//                    boolean isDominance;
//                    if (classUnion.isBiggerOrEqual()) {
//                        isDominance = judgeIsDominance(cudItem, laItem, attributes);
//                    } else {
//                        isDominance = judgeIsDominance(laItem, cudItem, attributes);
//                    }
//                    if (isDominance) {
//                        iterator.remove();
//                    }
//                }
//            }
            return lowApproximationSet;
        }


    }
}
