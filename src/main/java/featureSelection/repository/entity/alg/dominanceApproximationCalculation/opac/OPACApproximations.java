package featureSelection.repository.entity.alg.dominanceApproximationCalculation.opac;

import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author daiYang_wu
 */
public class OPACApproximations {

    private Map<ClassUnion, int[]> lowApproximationsCl;
    private Map<ClassUnion, int[]> upperApproximationsCl;

    /**
     * OPAC: Alg Init ArrayCl
     *
     * @param decisionValues ： 决策属性值列表
     * @param instanceNums   ： 数据集实例数量
     * @return
     */
    public OPACApproximations(List<Integer> decisionValues, int instanceNums) {
        lowApproximationsCl = new LinkedHashMap<>();
        upperApproximationsCl = new LinkedHashMap<>();

        for (int index = 0; index < decisionValues.size(); index++) {
            Integer d = decisionValues.get(index);
            if (index != 0) {
                ClassUnion uClassUnion = new ClassUnion(d, true);
                lowApproximationsCl.put(uClassUnion, new int[instanceNums]);
                upperApproximationsCl.put(uClassUnion, new int[instanceNums]);
            }
            if (index != decisionValues.size() - 1) {
                ClassUnion lClassUnion = new ClassUnion(d, false);
                lowApproximationsCl.put(lClassUnion, new int[instanceNums]);
                upperApproximationsCl.put(lClassUnion, new int[instanceNums]);
            }

        }
    }

    public Map<ClassUnion, int[]> getLowApproximationsCl() {
        return lowApproximationsCl;
    }

    public Map<ClassUnion, int[]> getUpperApproximationsCl() {
        return upperApproximationsCl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArrayCl \n");
        sb.append("   lowApproximationsCl : \n");
        for (Map.Entry<ClassUnion, int[]> entry : lowApproximationsCl.entrySet()) {
            sb.append("     ");
            sb.append(entry.getKey().toString() + " : ");
            sb.append(ArrayUtils.toString(entry.getValue(), ",") + "\n");
        }
        sb.append("   upperApproximationsCl : \n");
        for (Map.Entry<ClassUnion, int[]> entry : upperApproximationsCl.entrySet()) {
            sb.append("     ");
            sb.append(entry.getKey().toString() + " : ");
            sb.append(ArrayUtils.toString(entry.getValue(), ",") + "\n");
        }

        return sb.toString();

    }


    public List<Integer> chooseArray(int[] array) {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) indexList.add(i + 1);
        }
        return indexList;
    }

    public String formatToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArrayCl \n");
        sb.append("   lowApproximationsCl : \n");
        for (Map.Entry<ClassUnion, int[]> entry : lowApproximationsCl.entrySet()) {
            sb.append("     ");
            sb.append(entry.getKey().toString() + " : ");
            sb.append(chooseArray(entry.getValue()).toString()+"\n");
        }
        sb.append("   upperApproximationsCl : \n");
        for (Map.Entry<ClassUnion, int[]> entry : upperApproximationsCl.entrySet()) {
            sb.append("     ");
            sb.append(entry.getKey().toString() + " : ");
            sb.append(chooseArray(entry.getValue()).toString()+ "\n");
        }

        return sb.toString();
    }
}
