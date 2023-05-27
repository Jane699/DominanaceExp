package featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec;


import featureSelection.basic.lang.dataStructure.IntArrayKey;
import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.util.*;


/**
 * @author daiYang_wu
 */
@Data
public class EquivalenceClass {
    protected IntArrayKey intArrayKey;
    protected List<Instance> instances = new LinkedList<>();
    protected Integer minDecisionValue = Integer.MAX_VALUE;
    protected Integer maxDecisionValue = Integer.MIN_VALUE;

    public EquivalenceClass(IntArrayKey intArrayKey) {
        this.intArrayKey = intArrayKey;
    }

    /**
     * add new Instance and judge consistent
     *
     * @param ins {@link Instance}
     * @return java.lang.Boolean
     */
    public void addInstance(Instance ins) {
        this.instances.add(ins);
        Integer instanceDecisionValue = ins.getAttributeValue(0);
        if (instanceDecisionValue < minDecisionValue) {
            minDecisionValue = instanceDecisionValue;
        }
        if (instanceDecisionValue > maxDecisionValue) {
            maxDecisionValue = instanceDecisionValue;
        }
    }
}
