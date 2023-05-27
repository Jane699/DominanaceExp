package featureSelection.repository.algorithm.alg.dominanceApproximationCalculation;


import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common.DominanceCommonCalculate;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.opac.OPACApproximations;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;

import java.util.List;


/**
 * Algorithm repository of OPAC, which based on the Article
 * <An optimized method to calculate approximations in Dominance based Rough Set Approach>
 * by Aleena Ahmad a , Usman Qamar a,∗ , Muhammad Summair Raza
 * for calculate approximation
 *
 * @author daiYang_wu
 */
public class OptimizedMethodCalculateApproximationsAlgorithm {
    public static class Basic {
        /**
         * OPAC : Dominance Calculate , 计算两个实例在属性集上的优劣关系
         *
         * @param x1
         * @param x2
         * @return int
         * 1:instance1 劣于 instance2；
         * 2:instance2 优于 instance1;
         * 3:instance1 等于 instance2；
         * 4:instance1 与 instance2 无联系
         */
        public static int dominanceCalculate(Instance x1, Instance x2,int [] attributes) {
            if (x1 == x2 || isEqual(x1, x2,attributes)) return 3;
            if (DominanceCommonCalculate.judgeIsDominance(x1, x2)) return 2;
            else if (DominanceCommonCalculate.judgeIsDominance(x2, x1)) return 1;
            else return 4;
        }


        public static boolean isEqual(Instance ins1, Instance ins2,int[] attributes) {
            boolean flag = true;

            for (int attribute : attributes) {
                if (ins1.getAttributeValue(attribute) != ins2.getAttributeValue(attribute)) {
                    DominanceCommonCalculate.CompareCount.add();
                    flag = false;
                    break;
                }
            }

            return flag;
        }

        /**
         * OPAC : Dominance pos calculate , 判断arrayCl中ClassUnion中方向为biggerOrEqual，dValue=t的上下近似是否满足条件
         *
         * @param instance1
         * @param instance2
         * @param t
         * @param OPACApproximations
         * @return void
         */
        public static void dominancePosCalculate(Instance instance1, Instance instance2, Integer t, OPACApproximations OPACApproximations) {
            Integer d1 = instance1.getAttributeValue(0);
            Integer d2 = instance2.getAttributeValue(0);
            if (d1 >= t && d2 < t) {
                if (OPACApproximations.getLowApproximationsCl().get(new ClassUnion(t, true))[instance2.getNum() - 1] != 1) {
                    OPACApproximations.getLowApproximationsCl().get(new ClassUnion(t, true))[instance1.getNum() - 1] = 2;
                }
                return;
            }
            if (d1 <= t && d2 <= t) {
                if (OPACApproximations.getUpperApproximationsCl().get(new ClassUnion(t, true))[instance2.getNum() - 1] == 1) {
                    OPACApproximations.getUpperApproximationsCl().get(new ClassUnion(t, true))[instance1.getNum() - 1] = 1;
                }
            }
        }

        /**
         * OPAC : Dominance neg calculate, 判断arrayCl中ClassUnion中方向为smallerOrEqual，dValue=t的上下近似是否满足条件
         *
         * @param instance1
         * @param instance2
         * @param t
         * @param OPACApproximations
         * @return void
         */
        public static void dominanceNegCalculate(Instance instance1, Instance instance2, Integer t, OPACApproximations OPACApproximations) {
            Integer d1 = instance1.getAttributeValue(0);
            Integer d2 = instance2.getAttributeValue(0);
            if (d1 <= t && d2 > t) {
                if (OPACApproximations.getLowApproximationsCl().get(new ClassUnion(t, false))[instance2.getNum() - 1] != 1) {
                    OPACApproximations.getLowApproximationsCl().get(new ClassUnion(t, false))[instance1.getNum() - 1] = 2;
                }
            }
            if (d1 >= t && d2 >= t) {
                if (OPACApproximations.getUpperApproximationsCl().get(new ClassUnion(t, false))[instance2.getNum() - 1] == 1) {
                    OPACApproximations.getUpperApproximationsCl().get(new ClassUnion(t, false))[instance1.getNum() - 1] = 1;
                }
            }
        }

        /**
         * 初始化 OPACApproximations
         *
         * @param decisionValues
         * @param instanceNums
         * @return featureSelection.repository.entity.alg.dominanceApproximationCalculation.ArrayCl
         */
        public static OPACApproximations initArrayCl(List<Integer> decisionValues, int instanceNums) {
            return new OPACApproximations(decisionValues, instanceNums);
        }

        /**
         * 求解上下近似
         *
         * @param dataset
         * @param OPACApproximations
         * @param t
         * @param decisionValues
         * @param relationArray
         * @return void
         */
        public static void forCalculateDominancePosOrNeg(List<Instance> dataset, OPACApproximations OPACApproximations, Integer t, List<Integer> decisionValues, int[] relationArray,int [] attributes) {
            // step 1 : 为 arrayCl 赋值
            settingValueToArrayCl(dataset, OPACApproximations, t, decisionValues);
            // step 2 : find Dominance positive and negative
            findDominancePosAndNeg(dataset, OPACApproximations, t, decisionValues, relationArray,attributes);
        }


        private static void findDominancePosAndNeg(List<Instance> dataset, OPACApproximations OPACApproximations, Integer t, List<Integer> decisionValues, int[] relationArray,int [] attributes) {
            for (int i = 1; i <= dataset.size(); i++) {
                for (int j = 1; j <= dataset.size(); j++) {
                    if (i > j) continue;
                    Instance x1 = dataset.get(i - 1);
                    Instance x2 = dataset.get(j - 1);
                    int index = (i - 1) * dataset.size() - ((i - 1) * (i - 2)) / 2 + j - i;
                    int dominanceRelation = relationArray[index];
                    if (dominanceRelation == 0) {
                        dominanceRelation = dominanceCalculate(x1, x2,attributes);
                    }
                    relationArray[index] = dominanceRelation;
                    if (dominanceRelation == 1) {
                        if (t != decisionValues.get(0)) {
                            dominancePosCalculate(x1, x2, t, OPACApproximations);
                        }
                        if (t != decisionValues.get(decisionValues.size() - 1))
                            dominanceNegCalculate(x1, x2, t, OPACApproximations);
                    } else if (dominanceRelation == 2) {
                        if (t != decisionValues.get(0)) {
                            dominancePosCalculate(x2, x1, t, OPACApproximations);
                        }
                        if (t != decisionValues.get(decisionValues.size() - 1)) {
                            dominanceNegCalculate(x2, x1, t, OPACApproximations);
                        }
                    }
                }
            }
        }

        private static void settingValueToArrayCl(List<Instance> dataset, OPACApproximations OPACApproximations, Integer t, List<Integer> decisionValues) {
            for (Instance instance : dataset) {
                Integer d = instance.getAttributeValue(0);
                int index = instance.getNum() - 1;
                if (d <= t) {
                    if (t != decisionValues.get(decisionValues.size() - 1)) {
                        OPACApproximations.getUpperApproximationsCl().get(new ClassUnion(t, false))[index] = 1;
                        OPACApproximations.getLowApproximationsCl().get(new ClassUnion(t, false))[index] = 1;
                    }
                }
                if (d >= t) {
                    if (t != decisionValues.get(0)) {
                        OPACApproximations.getUpperApproximationsCl().get(new ClassUnion(t, true))[index] = 1;
                        OPACApproximations.getLowApproximationsCl().get(new ClassUnion(t, true))[index] = 1;
                    }
                }
            }
        }
    }

    public static class StringUtil {
        public static String relationArrayToString(int[] arr, int instanceNum) {
            StringBuilder sb = new StringBuilder();
            sb.append("1DArray : \n");
            int columnLimit = instanceNum;
            int flag = 1;
            for (int i = 0; i < arr.length; i++) {
                sb.append(arr[i] + " ");
                if (flag == columnLimit) {
                    sb.append("\n");
                    columnLimit--;
                    for (int j = 0; j < instanceNum - columnLimit; j++) sb.append("  ");
                    flag = 0;
                }
                flag++;
            }
            return sb.toString();
        }
    }

}



