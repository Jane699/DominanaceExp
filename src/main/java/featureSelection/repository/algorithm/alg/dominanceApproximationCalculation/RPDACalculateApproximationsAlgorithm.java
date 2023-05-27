package featureSelection.repository.algorithm.alg.dominanceApproximationCalculation;

import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common.DominanceCommonCalculate;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.rpda.RPDAApproximations;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.rpda.RPDAStoreInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daiYang_wu
 */
public class RPDACalculateApproximationsAlgorithm {
    public static class Basic {
        /**
         * 计算与统计类连结
         *
         * @param dataset
         * @param decisionValueList
         * @return void
         */
        public static RPDAStoreInfo decisionClassCounter(Collection<Instance> dataset, List<Integer> decisionValueList) {
            RPDAStoreInfo rpdaStoreInfo = new RPDAStoreInfo(decisionValueList.size());
            for (int decisionValue : decisionValueList) {
                rpdaStoreInfo.initDecisionKey(decisionValue);
                for (Instance ins : dataset) {
                    if (ins.getAttributeValue(0) == decisionValue) {
                        rpdaStoreInfo.getStHash().get(decisionValue).add(ins);
                    } else if (ins.getAttributeValue(0) > decisionValue) {
                        rpdaStoreInfo.getUtHash().get(decisionValue).add(ins);
                    } else if (ins.getAttributeValue(0) < decisionValue) {
                        rpdaStoreInfo.getLtHash().get(decisionValue).add(ins);
                    }
                }
            }
            return rpdaStoreInfo;
        }

        public static RPDAApproximations initRPDAApproximations(Collection<Instance> dataset, List<Integer> decisionValueList) {
            RPDAApproximations rpdaApproximations = new RPDAApproximations(dataset.size() / 4);
            rpdaApproximations.initRPDAApproximations(decisionValueList);
            return rpdaApproximations;
        }

        public static RPDAApproximations calculateApproximations(RPDAStoreInfo rpdaStoreInfo, List<Integer> decisionValueList, RPDAApproximations rpdaApproximations, int [] attributes) {
            for (int index = 0; index < decisionValueList.size(); index++) {
                calculateApproximationByIndex(rpdaStoreInfo, decisionValueList, rpdaApproximations, index,attributes);
            }
            return rpdaApproximations;
        }

        public static void calculateApproximationByIndex(RPDAStoreInfo rpdaStoreInfo, List<Integer> decisionValueList, RPDAApproximations rpdaApproximations, int index, int [] attributes) {
            int t = decisionValueList.get(index);
            if (t != decisionValueList.get(decisionValueList.size() - 1)) { //  当前为 0 -- t-1
                int tAdd = decisionValueList.get(index + 1);
                for (Instance sInstance : rpdaStoreInfo.getStHash().get(t)) {
                    for (int currentIndex = index; currentIndex < decisionValueList.size(); currentIndex++)
                        rpdaApproximations.getPUL().get(decisionValueList.get(currentIndex)).add(sInstance);
                    rpdaApproximations.getPUU().get(t).add(sInstance);
                    int count = 0;
                    for (Instance uInstance : rpdaStoreInfo.getUtHash().get(t)) {
                        rpdaApproximations.getPUU().get(t).add(uInstance);
                        if (judgeRelation(sInstance, uInstance,attributes)) {
                            rpdaApproximations.getPUL().get(t).add(uInstance);
                            if (uInstance.getAttributeValue(0) > t) count++;
                            rpdaApproximations.getPLU().get(tAdd).remove(uInstance);
                        }
                        if (judgeRelation(uInstance, sInstance,attributes)) {
                            if (!rpdaApproximations.getPUL().get(t).contains(uInstance))
                                rpdaApproximations.getPLU().get(tAdd).add(uInstance);
                        }
                    }
                    if (count == 0) {
                        rpdaApproximations.getPLL().get(t).add(sInstance);
                        rpdaApproximations.getPLL().get(tAdd).add(sInstance);
                    }
                    if (count > 0) {
                        rpdaApproximations.getPLL().get(tAdd).add(sInstance);
                        rpdaApproximations.getPUU().get(tAdd).add(sInstance);
                    }
//                        rpdaApproximations.getPLU().put(t + 1,
//                                rpdaApproximations.getPLU().get(t + 1)
//                                        .stream()
//                                        .filter((instance -> !rpdaApproximations.getPUL().get(t).contains(instance)))
//                                        .collect(Collectors.toList()));
//                        for (Instance xk : rpdaApproximations.getPUL().get(t)) {
//                            if (rpdaApproximations.getPLU().get(t + 1).contains(xk)) {
//
//                                rpdaApproximations.getPLU().get(t + 1).remove(xk);
//                            }
//                        }
                }
            } else {     //当前为t
                for (Instance sInstance : rpdaStoreInfo.getStHash().get(t)) {
                    rpdaApproximations.getPUL().get(decisionValueList.get(index)).add(sInstance);
                    rpdaApproximations.getPUU().get(t).add(sInstance);
                }
            }

        }

        /**
         * 功能描述
         *
         * @param ins1
         * @param ins2
         * @return boolean
         */
        public static boolean judgeRelation(Instance ins1, Instance ins2, int[] attributes) {

            boolean flag = true;
            for (int attribute : attributes) {
                if (ins1.getAttributeValue(attribute) < ins2.getAttributeValue(attribute)) {
                    DominanceCommonCalculate.CompareCount.add();
                    flag = false;
                }
            }
            return flag;
        }
    }
}
