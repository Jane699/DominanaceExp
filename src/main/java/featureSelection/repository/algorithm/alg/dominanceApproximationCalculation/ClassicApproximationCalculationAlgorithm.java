package featureSelection.repository.algorithm.alg.dominanceApproximationCalculation;

import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common.DominanceCommonCalculate;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.awt.peer.CanvasPeer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daiYang_wu
 */
public class ClassicApproximationCalculationAlgorithm {
    public static class Basic {

        /**
         * 寻找 Dp+(instance)
         *
         * @param instance
         * @param dataset
         * @return java.util.Collection<featureSelection.basic.model.universe.instance.Instance>
         */
        public static Collection<Instance> findPosSet(Instance instance, Collection<Instance> dataset,int [] attributes) {
            Collection<Instance> dpPosCollection = new ArrayList<>();
            for (Instance instanceComp : dataset) {
                boolean flag = true;
                for (int attribute : attributes) {
                    if (instance.getAttributeValue(attribute) < instanceComp.getAttributeValue(attribute)) {
                        DominanceCommonCalculate.CompareCount.add();
                        flag = false;
                    }
                }
                if (flag) dpPosCollection.add(instanceComp);
            }
            return dpPosCollection;
        }

        /**
         * 寻找 Dp-(instance)
         *
         * @param instance
         * @param dataset
         * @return java.util.Collection<featureSelection.basic.model.universe.instance.Instance>
         */
        public static Collection<Instance> findNegSet(Instance instance, Collection<Instance> dataset,int [] attributes) {
            Collection<Instance> dpNegCollection = new ArrayList<>();
            for (Instance instanceComp : dataset) {
                boolean flag = true;
                for (int attribute : attributes) {
                    if (instance.getAttributeValue(attribute) < instanceComp.getAttributeValue(attribute)) {
                        DominanceCommonCalculate.CompareCount.add();
                        flag = false;
                    }
                }
                if (flag) dpNegCollection.add(instanceComp);
            }
            return dpNegCollection;
        }

        public static Collection<Instance> calculateLowerApproximationByClassUnion(Collection<Instance> dataset, ClassUnion classUnion, Map<ClassUnion, Collection<Instance>> classUnionHashMap,int [] attributes) {
            Collection<Instance> lowApproximation = new ArrayList<>();
            Collection<Instance> classUnionSet = classUnionHashMap.get(classUnion);
            for (Instance clInstance : classUnionSet) {
                if (classUnion.isBiggerOrEqual()) {
                    Collection<Instance> posSet = findPosSet(clInstance, dataset,attributes);
                    if (classUnionSet.containsAll(posSet)) lowApproximation.add(clInstance);
                } else {
                    Collection<Instance> negSet = findNegSet(clInstance, dataset,attributes);
                    if (classUnionSet.containsAll(negSet)) lowApproximation.add(clInstance);
                }
            }
            return lowApproximation;
        }

        public static Collection<Instance> calculateUpperApproximationByClassUnion(Collection<Instance> dataset, ClassUnion classUnion, Map<ClassUnion, Collection<Instance>> classUnionHashMap,int [] attributes) {
            Set<Instance> upApproximation = new HashSet<>();
            Collection<Instance> classUnionSet = classUnionHashMap.get(classUnion);
            for (Instance clInstance : classUnionSet) {
                if (!classUnion.isBiggerOrEqual()) {
                    Collection<Instance> negSet = findNegSet(clInstance, dataset,attributes);
                    upApproximation.addAll(negSet);
                } else {
                    Collection<Instance> posSet = findPosSet(clInstance, dataset,attributes);
                    upApproximation.addAll(posSet);
                }
            }
            return upApproximation.stream().collect(Collectors.toList());
        }

        public static Map<ClassUnion, Collection<Instance>> calculateLowApproximations(Collection<Instance> dataset, Map<ClassUnion, Collection<Instance>> classUnionHashMap,int [] attributes) {
            Map<ClassUnion, Collection<Instance>> lowApproximations = new LinkedHashMap<>();
            for (ClassUnion classUnion : classUnionHashMap.keySet()) {
                lowApproximations.put(classUnion, calculateLowerApproximationByClassUnion(dataset, classUnion, classUnionHashMap,attributes));
            }
            return lowApproximations;
        }

        public static Map<ClassUnion, Collection<Instance>> calculateUpperApproximations(Collection<Instance> dataset, Map<ClassUnion, Collection<Instance>> classUnionHashMap,int [] attributes) {
            Map<ClassUnion, Collection<Instance>> upperApproximations = new LinkedHashMap<>();
            for (ClassUnion classUnion : classUnionHashMap.keySet()) {
                upperApproximations.put(classUnion, calculateUpperApproximationByClassUnion(dataset, classUnion, classUnionHashMap,attributes));
            }
            return upperApproximations;
        }

    }
}
