package featureSelection.repository.algorithm.alg.dominanceApproximationCalculation;

import featureSelection.basic.lang.dataStructure.IntArrayKey;
import featureSelection.basic.lang.dataStructure.impl.integerIterator.IntegerArrayIterator;
import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common.DominanceCommonCalculate;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.ClassUnionDecHash;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.EquivalenceClass;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daiYang_wu
 */
public class DominanceEquivalenceClassBasedApproximationCalculationAlgorithm {

    public static class Basic {
        /**
         * 压缩数据获得DECHash
         *
         * @param dataset
         * @return java.util.Map<featureSelection.basic.lang.dataStructure.IntArrayKey, featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.EquivalenceClass>
         */
        public static Map<IntArrayKey, EquivalenceClass> dataReduction(Collection<Instance> dataset, int[] attributes) {

            Map<IntArrayKey, EquivalenceClass> dominanceEquClassData = new HashMap<>();
            IntegerArrayIterator iterator = new IntegerArrayIterator(attributes);
            int[] keyArray;
            IntArrayKey key;
            for (Instance ins : dataset) {
                keyArray = Instance.attributeValuesOf(ins, iterator);
                key = new IntArrayKey(keyArray);
                IntArrayKey intArrayKey = (key);
                EquivalenceClass equivalenceClass = dominanceEquClassData.get(intArrayKey);
                if (null == equivalenceClass) {
                    equivalenceClass = new EquivalenceClass(intArrayKey);
                    dominanceEquClassData.put(intArrayKey, equivalenceClass);
                }
                equivalenceClass.addInstance(ins);
            }
            return dominanceEquClassData;
        }

        /**
         * 生成所有 classUnion ： d 与 >= or <=
         *
         * @param decisionValues
         * @return java.util.Collection<featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion>
         */
        public static Collection<ClassUnion> genClassUnions(List<Integer> decisionValues) {
            Collection<ClassUnion> classUnions = new ArrayList<>();
            Collections.sort(decisionValues);
            for (int i = 0; i < decisionValues.size(); i++) {
                if (i != 0) classUnions.add(new ClassUnion(decisionValues.get(i), true));
                if (i != decisionValues.size() - 1) classUnions.add(new ClassUnion(decisionValues.get(i), false));
            }
            return classUnions;
        }

        /**
         * 将dec分为三类 : -1,0,1
         *
         * @param decHash
         * @param classUnions
         * @param dList
         * @return java.util.Map<featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion, featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.ClassUnionDec>
         */
        public static ClassUnionDecHash calculateConsDec(Map<IntArrayKey, EquivalenceClass> decHash, Collection<ClassUnion> classUnions, List<Integer> dList, String strategy) {
            //step1 : init  classUnionDecHash
            ClassUnionDecHash classUnionDecHash = new ClassUnionDecHash(classUnions);

            // step2 :
            int datasetMinDIndex = 0;
            int datasetMaxIndex = dList.size() - 1;
            int datasetMinDecisionValue = dList.get(datasetMinDIndex);
            int datasetMaxDecisionValue = dList.get(datasetMaxIndex);

            // DIG+Rule or DIG+Rule+UpperSearch
            if (strategy.contains("Rule")) {
                for (EquivalenceClass dec : decHash.values()) {
                    int minDIndex = dList.indexOf(dec.getMinDecisionValue());
                    int maxDIndex = dList.indexOf(dec.getMaxDecisionValue());
                    //e.min == e.max
                    if (dec.getMinDecisionValue() == dec.getMaxDecisionValue()) {
                        //e.min == e.max == minD
                        if (dec.getMinDecisionValue() == datasetMinDecisionValue) {
                            // <= rule
                            classUnionDecHash.putLowClassUnionDecPos(datasetMinDecisionValue, dec);
                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecNeg(dList.get(index), dec);
                            }
                        }
                        //e.min == e.max = maxD
                        else if (dec.getMinDecisionValue() == datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecNeg(dList.get(index), dec);
                            }
                            // >= rule
                            classUnionDecHash.putUpperClassUnionDecPos(datasetMaxDecisionValue, dec);
                        }
                        // (e.min == e.max)!= (maxD or minD) == v
                        else {
                            int vIndex = dList.indexOf(dec.getMinDecisionValue());
                            // <= rule
                            for (int index = datasetMinDIndex; index < vIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecNeg(dList.get(index), dec);
                            }
                            for (int index = vIndex; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecPos(dList.get(index), dec);
                            }
                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= vIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecPos(dList.get(index), dec);
                            }
                            for (int index = vIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecNeg(dList.get(index), dec);
                            }
                        }

                    } else {        //e.min <> e.max

                        // min 和 max
                        if (dec.getMinDecisionValue() == datasetMinDecisionValue && dec.getMaxDecisionValue() == datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < maxDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecBou(dList.get(index), dec);
                            }
                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecBou(dList.get(index), dec);
                            }
                        }
                        // min 和 D_max
                        if (dec.getMinDecisionValue() == datasetMinDecisionValue && dec.getMaxDecisionValue() != datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < maxDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecBou(dList.get(index), dec);
                            }
                            classUnionDecHash.putLowClassUnionDecPos(dec.getMaxDecisionValue(), dec);
                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= maxDIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecBou(dList.get(index), dec);
                            }

                            for (int index = maxDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecNeg(dList.get(index), dec);
                            }
                        }
                        //D_min 和 max
                        if (dec.getMinDecisionValue() != datasetMinDecisionValue && dec.getMaxDecisionValue() == datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < minDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecNeg(dList.get(index), dec);
                            }
                            for (int index = minDIndex; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecBou(dList.get(index), dec);
                            }
                            // >= rule
                            classUnionDecHash.putUpperClassUnionDecPos(dec.getMinDecisionValue(), dec);

                            for (int index = maxDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecBou(dList.get(index), dec);
                            }
                        }

                        //D_min 和 D_max
                        if (dec.getMinDecisionValue() != datasetMinDecisionValue && dec.getMaxDecisionValue() != datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < minDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecNeg(dList.get(index), dec);
                            }
                            for (int index = minDIndex; index < maxDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecBou(dList.get(index), dec);
                            }
                            classUnionDecHash.putLowClassUnionDecPos(dec.getMaxDecisionValue(), dec);

                            // >= rule
                            classUnionDecHash.putUpperClassUnionDecPos(dec.getMinDecisionValue(), dec);

                            for (int index = minDIndex + 1; index <= maxDIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecBou(dList.get(index), dec);
                            }
                            for (int index = maxDIndex + 1; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecNeg(dList.get(index), dec);
                            }
                        }
                    }
                }
            }
            // DIG or DIG+UpperSearch
            else {
                for (EquivalenceClass dec : decHash.values()) {
                    int minDIndex = dList.indexOf(dec.getMinDecisionValue());
                    int maxDIndex = dList.indexOf(dec.getMaxDecisionValue());
                    //e.min == e.max
                    if (dec.getMinDecisionValue() == dec.getMaxDecisionValue()) {
                        //e.min == e.max == minD
                        if (dec.getMinDecisionValue() == datasetMinDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecPos(dList.get(index), dec);
                            }
                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecNeg(dList.get(index), dec);
                            }
                        }
                        //e.min == e.max = maxD
                        else if (dec.getMinDecisionValue() == datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecNeg(dList.get(index), dec);
                            }
                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecPos(dList.get(index), dec);
                            }
                        }
                        // (e.min == e.max)!= (maxD or minD) == v
                        else {
                            int vIndex = dList.indexOf(dec.getMinDecisionValue());
                            // <= rule
                            for (int index = datasetMinDIndex; index < vIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecNeg(dList.get(index), dec);
                            }
                            for (int index = vIndex; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecPos(dList.get(index), dec);
                            }
                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= vIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecPos(dList.get(index), dec);
                            }
                            for (int index = vIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecNeg(dList.get(index), dec);
                            }
                        }

                    } else {        //e.min <> e.max

                        // min 和 max
                        if (dec.getMinDecisionValue() == datasetMinDecisionValue && dec.getMaxDecisionValue() == datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < maxDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecBou(dList.get(index), dec);
                            }
                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecBou(dList.get(index), dec);
                            }
                        }

                        // min 和 D_max
                        if (dec.getMinDecisionValue() == datasetMinDecisionValue && dec.getMaxDecisionValue() != datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < maxDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecBou(dList.get(index), dec);
                            }
                            for (int index = maxDIndex; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecPos(dList.get(index), dec);
                            }

                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= maxDIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecBou(dList.get(index), dec);
                            }

                            for (int index = maxDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecNeg(dList.get(index), dec);
                            }
                        }
                        //D_min 和 max
                        if (dec.getMinDecisionValue() != datasetMinDecisionValue && dec.getMaxDecisionValue() == datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < minDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecNeg(dList.get(index), dec);
                            }
                            for (int index = minDIndex; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecBou(dList.get(index), dec);
                            }
                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= minDIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecPos(dList.get(index), dec);
                            }
                            for (int index = maxDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecBou(dList.get(index), dec);
                            }
                        }

                        //D_min 和 D_max
                        if (dec.getMinDecisionValue() != datasetMinDecisionValue && dec.getMaxDecisionValue() != datasetMaxDecisionValue) {
                            // <= rule
                            for (int index = datasetMinDIndex; index < minDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecNeg(dList.get(index), dec);
                            }
                            for (int index = minDIndex; index < maxDIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecBou(dList.get(index), dec);
                            }

                            for (int index = maxDIndex; index < datasetMaxIndex; index++) {
                                classUnionDecHash.putLowClassUnionDecPos(dList.get(index), dec);
                            }

                            // >= rule
                            for (int index = datasetMinDIndex + 1; index <= minDIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecPos(dList.get(index), dec);
                            }
                            for (int index = minDIndex + 1; index <= maxDIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecBou(dList.get(index), dec);
                            }
                            for (int index = maxDIndex + 1; index <= datasetMaxIndex; index++) {
                                classUnionDecHash.putUpperClassUnionDecNeg(dList.get(index), dec);
                            }
                        }
                    }
                }
            }


            return classUnionDecHash;
        }


        /**
         * 根据类联结计算上近似
         *
         * @param classUnionDecHash
         * @param classUnion
         * @return java.util.Collection<featureSelection.basic.model.universe.instance.Instance>
         */
        public static Collection<Instance> calculateUpperApproximationByClassUnionRule(int[] attributes, ClassUnionDecHash classUnionDecHash, ClassUnion classUnion) {
            // upper Approximation
            Collection<Instance> upperApproximation = new HashSet<>();
            ClassUnionDecHash.ClassUnionDec classUnionDec = classUnionDecHash.getClassUnionDecByClassUnion(classUnion);
            // add instances from 1-dec and 0-dec to upperApproximation
            upperApproximation.addAll(classUnionDec.getPosDecList().stream().flatMap(dec -> dec.getInstances().stream()).collect(Collectors.toList()));
            upperApproximation.addAll(classUnionDec.getBouDecList().stream().flatMap(dec -> dec.getInstances().stream()).collect(Collectors.toList()));
            // check -1-dec
            Collection<EquivalenceClass> noConsDecList = classUnionDec.getNegDecList();
            Set<EquivalenceClass> posDecList = new HashSet<>();
//            posDecList.addAll(classUnionDec.getPosDecList())
            int d = classUnion.getDecisionValue();
            if (classUnion.isBiggerOrEqual()) {
                while (classUnionDecHash.getUpperClassUnionDecHash().get(d) != null) {
                    posDecList.addAll(classUnionDecHash.getUpperClassUnionDecHash().get(d).getPosDecList());
                    d++;
                }

                for (EquivalenceClass noConsDec : noConsDecList) {
                    if (isUpperApproximationForUpperClassUnion(attributes, noConsDec, posDecList, classUnion)
                            || isUpperApproximationForUpperClassUnion(attributes, noConsDec, classUnionDec.getBouDecList(), classUnion)) {
                        upperApproximation.addAll(noConsDec.getInstances());
                    }
                }

            } else {
                while (classUnionDecHash.getLowClassUnionDecHash().get(d) != null) {
                    posDecList.addAll(classUnionDecHash.getLowClassUnionDecHash().get(d).getPosDecList());
                    d--;
                }

                for (EquivalenceClass noConsDec : noConsDecList) {
                    if (isUpperApproximationForLowClassUnion(attributes, noConsDec, posDecList)
                            || isUpperApproximationForLowClassUnion(attributes, noConsDec, classUnionDec.getBouDecList())) {
                        upperApproximation.addAll(noConsDec.getInstances());
                    }
                }
            }
            System.out.println(classUnion.toString() + "--- posList :" + posDecList.size());
            System.out.println(classUnion.toString() + "--- negList :" + classUnionDec.getNegDecList().size());
            System.out.println(classUnion.toString() + "--- bouList :" + classUnionDec.getBouDecList().size());
            return upperApproximation;
        }

        public static Collection<Instance> calculateUpperApproximationByClassUnion(int[] attributes, ClassUnionDecHash classUnionDecHash, ClassUnion classUnion) {
            // upper Approximation
            Collection<Instance> upperApproximation = new HashSet<>();
            ClassUnionDecHash.ClassUnionDec classUnionDec = classUnionDecHash.getClassUnionDecByClassUnion(classUnion);
            // add instances from 1-dec and 0-dec to upperApproximation
            upperApproximation.addAll(classUnionDec.getPosDecList().stream().flatMap(dec -> dec.getInstances().stream()).collect(Collectors.toList()));
            upperApproximation.addAll(classUnionDec.getBouDecList().stream().flatMap(dec -> dec.getInstances().stream()).collect(Collectors.toList()));
            // check -1-dec
            Set<EquivalenceClass> posDecList = new HashSet<>();
            posDecList.addAll(classUnionDec.getPosDecList());
            Collection<EquivalenceClass> noConsDecList = classUnionDec.getNegDecList();
            if (classUnion.isBiggerOrEqual()) {
                for (EquivalenceClass noConsDec : noConsDecList) {
                    if (isUpperApproximationForUpperClassUnion(attributes, noConsDec, posDecList, classUnion)
                            || isUpperApproximationForUpperClassUnion(attributes, noConsDec, classUnionDec.getBouDecList(), classUnion)) {
                        upperApproximation.addAll(noConsDec.getInstances());
                    }
                }

            } else {

                for (EquivalenceClass noConsDec : noConsDecList) {
                    if (isUpperApproximationForLowClassUnion(attributes, noConsDec, posDecList)
                            || isUpperApproximationForLowClassUnion(attributes, noConsDec, classUnionDec.getBouDecList())) {
                        upperApproximation.addAll(noConsDec.getInstances());
                    }
                }
            }
            System.out.println(classUnion.toString() + "--- posList :" + classUnionDec.getPosDecList().size());
            System.out.println(classUnion.toString() + "--- negList :" + classUnionDec.getNegDecList().size());
            System.out.println(classUnion.toString() + "--- bouList :" + classUnionDec.getBouDecList().size());

            return upperApproximation;
        }

        /**
         * 根据类联结计算上近似
         *
         * @param classUnionDecHash
         * @param classUnion
         * @return java.util.Collection<featureSelection.basic.model.universe.instance.Instance>
         */
        public static Collection<Instance> calculateUpperApproximationByClassUnionRuleAccelerator(int[] attributes,
                                                                                                  ClassUnionDecHash classUnionDecHash,
                                                                                                  ClassUnion classUnion,
                                                                                                  List<ClassUnion> classUnionSort,
                                                                                                  int index,
                                                                                                  Map<ClassUnion, Map<IntArrayKey, EquivalenceClass>> preDIGMap) {
            // upper Approximation
            Collection<Instance> upperApproximation = new HashSet<>();

            ClassUnionDecHash.ClassUnionDec classUnionDec = classUnionDecHash.getClassUnionDecByClassUnion(classUnion);
            // add instances from 1-dec and 0-dec to upperApproximation
            upperApproximation.addAll(classUnionDec.getPosDecList().stream().flatMap(dec -> dec.getInstances().stream()).collect(Collectors.toList()));
            upperApproximation.addAll(classUnionDec.getBouDecList().stream().flatMap(dec -> dec.getInstances().stream()).collect(Collectors.toList()));

            // check -1-dec
            Collection<EquivalenceClass> noConsDecList = classUnionDec.getNegDecList();
            Set<EquivalenceClass> posDecList = new HashSet<>();
            int d = classUnion.getDecisionValue();
            if (classUnion.isBiggerOrEqual()) {
                if (index == classUnionSort.size() - 1) {
                    //放入 PosDec
                    while (classUnionDecHash.getUpperClassUnionDecHash().get(d) != null) {
                        posDecList.addAll(classUnionDecHash.getUpperClassUnionDecHash().get(d).getPosDecList());
                        d++;
                    }
                    for (EquivalenceClass noConsDec : noConsDecList) {
                        if (isUpperApproximationForUpperClassUnion(attributes, noConsDec, posDecList, classUnion)
                                || isUpperApproximationForUpperClassUnion(attributes, noConsDec, classUnionDec.getBouDecList(), classUnion)) {
                            upperApproximation.addAll(noConsDec.getInstances());
                            preDIGMap.get(classUnion).put(noConsDec.getIntArrayKey(), noConsDec);
                        }
                    }
                } else {
                    Map<IntArrayKey, EquivalenceClass> compareDecs = preDIGMap.get(classUnionSort.get(index + 1));
                    for (EquivalenceClass noConsDec : noConsDecList) {
                        if (compareDecs.containsKey(noConsDec.getIntArrayKey())) {
                            upperApproximation.addAll(noConsDec.getInstances());
                            preDIGMap.get(classUnion).put(noConsDec.getIntArrayKey(), noConsDec);
                        }
                    }
                }

            } else {
                //小于等于，从 1 - D
                if (index == 0) {
                    //放入 PosDec
                    while (classUnionDecHash.getLowClassUnionDecHash().get(d) != null) {
                        posDecList.addAll(classUnionDecHash.getLowClassUnionDecHash().get(d).getPosDecList());
                        d--;
                    }
                    for (EquivalenceClass noConsDec : noConsDecList) {
                        if (isUpperApproximationForLowClassUnion(attributes, noConsDec, posDecList)
                                || isUpperApproximationForLowClassUnion(attributes, noConsDec, classUnionDec.getBouDecList())) {
                            upperApproximation.addAll(noConsDec.getInstances());
                            preDIGMap.get(classUnion).put(noConsDec.getIntArrayKey(), noConsDec);
                        }
                    }
                } else {
                    Map<IntArrayKey, EquivalenceClass> compareDecs = preDIGMap.get(classUnionSort.get(index - 1));
                    for (EquivalenceClass noConsDec : noConsDecList) {
                        if (compareDecs.containsKey(noConsDec.getIntArrayKey())) {
                            upperApproximation.addAll(noConsDec.getInstances());
                            preDIGMap.get(classUnion).put(noConsDec.getIntArrayKey(), noConsDec);
                        }
                    }
                }

            }

            return upperApproximation;
        }


        /**
         * 根据类联结计算下近似
         *
         * @param attributes
         * @param classUnionDecHash
         * @param classUnion
         * @return java.util.Collection<featureSelection.basic.model.universe.instance.Instance>
         */
        public static Collection<Instance> calculateLowApproximationByClassUnion(int[] attributes, ClassUnionDecHash classUnionDecHash, ClassUnion classUnion) {
            // lowApproximation result collection
            Collection<Instance> lowApproximation = new HashSet<>();
            ClassUnionDecHash.ClassUnionDec classUnionDec = classUnionDecHash.getClassUnionDecByClassUnion(classUnion);
            // add 1-dec to consDecList
//            List<EquivalenceClass> consDecList = new ArrayList<>();
//            consDecList.addAll(classUnionDec.getPosDecList());
            List<EquivalenceClass> consDecList = classUnionDec.getPosDecList();
            List<Integer> removeIndex = new LinkedList<>();
            // check 1-dec
            // >= or <=
            if (classUnion.isBiggerOrEqual()) {
                for (ListIterator iterator = consDecList.listIterator(); iterator.hasNext(); ) {
                    EquivalenceClass consDec = (EquivalenceClass) iterator.next();
                    if (!isLowApproximationForUpperClassUnion(attributes, consDec, classUnionDec.getNegDecList()) || !isLowApproximationForUpperClassUnion(attributes, consDec, classUnionDec.getBouDecList())) {
//                        iterator.remove();
                        removeIndex.add(iterator.nextIndex());
                    }
                }
            } else {
                for (ListIterator iterator = consDecList.listIterator(); iterator.hasNext(); ) {
                    EquivalenceClass consDec = (EquivalenceClass) iterator.next();
                    if (!isLowApproximationForLowClassUnion(attributes, consDec, classUnionDec.getNegDecList()) || !isLowApproximationForLowClassUnion(attributes, consDec, classUnionDec.getNegDecList())) {
//                        iterator.remove();
                        removeIndex.add(iterator.nextIndex());
                    }
                }
            }

            for (int index = 0; index < consDecList.size(); index++) {
                if (!removeIndex.contains(index)) {
                    lowApproximation.addAll(consDecList.get(index).getInstances());
                }
            }

//            for (EquivalenceClass consDec : consDecList) {
//                lowApproximation.addAll(consDec.getInstances());
//            }

            return lowApproximation;
        }


        private static boolean isLowApproximationForUpperClassUnion(int[] attributes, EquivalenceClass
                consDec, List<EquivalenceClass> noConsDecList) {
//            List<Integer> currentNoDelDecArray = new ArrayList<>();
//            for (int i = 0; i < noConsDecList.size(); i++) currentNoDelDecArray.add(i);
//            for (int attr : attributes) {
//                for (Iterator iterator = currentNoDelDecArray.iterator(); iterator.hasNext(); ) {
//                    EquivalenceClass noConsDec = noConsDecList.get((Integer) iterator.next());
//                    if (consDec.getIntArrayKey().getKey()[attr - 1] > noConsDec.getIntArrayKey().getKey()[attr - 1]) {
//                        DominanceCommonCalculate.CompareCount.add();
//                        iterator.remove();
//                    }
//                }
//            }
//            return 0 == currentNoDelDecArray.size();
            for (Iterator iterator = noConsDecList.iterator(); iterator.hasNext(); ) {
                EquivalenceClass noConsDec = (EquivalenceClass) iterator.next();
                if (DominanceCommonCalculate.judgeIsDominance(noConsDec.getInstances().get(0), consDec.getInstances().get(0), attributes)) {
                    return false;
                }
            }
            return true;
        }

        private static boolean isLowApproximationForLowClassUnion(int[] attributes, EquivalenceClass
                consDec, List<EquivalenceClass> noConsDecList) {
//            List<Integer> currentNoDelDecArray = new ArrayList<>();
//            for (int i = 0; i < noConsDecList.size(); i++) currentNoDelDecArray.add(i);
//            for (int attr : attributes) {
//                for (Iterator iterator = currentNoDelDecArray.iterator(); iterator.hasNext(); ) {
//                    EquivalenceClass noConsDec = noConsDecList.get((Integer) iterator.next());
//                    if (consDec.getIntArrayKey().getKey()[attr - 1] < noConsDec.getIntArrayKey().getKey()[attr - 1]) {
//                        DominanceCommonCalculate.CompareCount.add();
//                        iterator.remove();
//                    }
//                }
//            }
//            return 0 == currentNoDelDecArray.size();
            for (Iterator iterator = noConsDecList.iterator(); iterator.hasNext(); ) {
                EquivalenceClass noConsDec = (EquivalenceClass) iterator.next();
                if (DominanceCommonCalculate.judgeIsDominance(consDec.getInstances().get(0), noConsDec.getInstances().get(0), attributes)) {
                    return false;
                }
            }
            return true;
        }

        private static boolean isUpperApproximationForLowClassUnion(int[] attributes, EquivalenceClass
                noConsDec, Collection<EquivalenceClass> consDecList) {
//            List<Integer> currentNoDelDecArray = new ArrayList<>();
//            for (int i = 0; i < consDecList.size(); i++) currentNoDelDecArray.add(i);
//            for (int attr : attributes) {
//                for (Iterator iterator = currentNoDelDecArray.iterator(); iterator.hasNext(); ) {
//                    EquivalenceClass consDec = consDecList.get((Integer) iterator.next());
//                    if (classUnion.isBiggerOrEqual()) {
//                        if (consDec.getIntArrayKey().getKey()[attr - 1] > noConsDec.getIntArrayKey().getKey()[attr - 1]) {
//                            DominanceCommonCalculate.CompareCount.add();
//                            iterator.remove();
//                        }
//                    } else {
//                        if (consDec.getIntArrayKey().getKey()[attr - 1] < noConsDec.getIntArrayKey().getKey()[attr - 1]) {
//                            DominanceCommonCalculate.CompareCount.add();
//                            iterator.remove();
//                        }
//                    }
//                }
//
//            }
//            return !(0 == currentNoDelDecArray.size());
            for (EquivalenceClass consDec : consDecList) {
                if (DominanceCommonCalculate.judgeIsDominance(consDec.getInstances().get(0), noConsDec.getInstances().get(0), attributes)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean isUpperApproximationForUpperClassUnion(int[] attributes, EquivalenceClass
                noConsDec, Collection<EquivalenceClass> consDecList, ClassUnion classUnion) {
//            List<Integer> currentNoDelDecArray = new ArrayList<>();
//            for (int i = 0; i < consDecList.size(); i++) currentNoDelDecArray.add(i);
//            for (int attr : attributes) {
//                for (Iterator iterator = currentNoDelDecArray.iterator(); iterator.hasNext(); ) {
//                    EquivalenceClass consDec = consDecList.get((Integer) iterator.next());
//                    if (classUnion.isBiggerOrEqual()) {
//                        if (consDec.getIntArrayKey().getKey()[attr - 1] > noConsDec.getIntArrayKey().getKey()[attr - 1]) {
//                            DominanceCommonCalculate.CompareCount.add();
//                            iterator.remove();
//                        }
//                    } else {
//                        if (consDec.getIntArrayKey().getKey()[attr - 1] < noConsDec.getIntArrayKey().getKey()[attr - 1]) {
//                            DominanceCommonCalculate.CompareCount.add();
//                            iterator.remove();
//                        }
//                    }
//                }
//
//            }
//            return !(0 == currentNoDelDecArray.size());
            for (EquivalenceClass consDec : consDecList) {
                if (DominanceCommonCalculate.judgeIsDominance(noConsDec.getInstances().get(0), consDec.getInstances().get(0), attributes)) {
                    return true;
                }
            }
            return false;
        }


    }

    public static class StringFormat {
        public static String decHashFormat(Map<IntArrayKey, EquivalenceClass> decHash) {
            StringBuilder sb = new StringBuilder();
            sb.append("index  |  key   |  recodeList  |  min  |  max \n");
            int index = 0;
            for (Map.Entry<IntArrayKey, EquivalenceClass> entry : decHash.entrySet()) {
                sb.append(++index + " | ");
                sb.append(entry.getKey().toString() + " | ");
                EquivalenceClass equivalenceClass = entry.getValue();
                sb.append(equivalenceClass.getInstances().stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString() + " | ");
                sb.append(equivalenceClass.getMinDecisionValue().toString() + " | ");
                sb.append(equivalenceClass.getMaxDecisionValue().toString() + " | ");
                sb.append("\n");
            }
            return sb.toString();
        }

        public static String consDecFormat(ClassUnionDecHash classUnionDecHash) {
            StringBuilder sb = new StringBuilder();
            sb.append("classUnion  |  1-DEC   |  -1-DEC  |   0-DEC \n");
            Map<Integer, ClassUnionDecHash.ClassUnionDec> lowClassUnionDecHash = classUnionDecHash.getLowClassUnionDecHash();
            for (ClassUnion classUnion : classUnionDecHash.getClassUnionList()) {
                sb.append(classUnion.toString() + " | ");
                sb.append(classUnionDecHash.getClassUnionDecByClassUnion(classUnion).getPosDecList() + " | ");
                sb.append(classUnionDecHash.getClassUnionDecByClassUnion(classUnion).getNegDecList() + " | ");
                sb.append(classUnionDecHash.getClassUnionDecByClassUnion(classUnion).getBouDecList() + " | ");
            }
            return sb.toString();
        }
    }
}
