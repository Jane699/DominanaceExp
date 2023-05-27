package featureSelection.repository.entity.alg.dominanceApproximationCalculation.common;

import featureSelection.basic.lang.dataStructure.IntArrayKey;
import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.EquivalenceClass;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daiYang_wu
 */
@Data
public class DominanceApproximation {
    private Map<ClassUnion, Collection<Instance>> lowApproximations = new HashMap<>();
    private Map<ClassUnion, Collection<Instance>> upperApproximations = new HashMap<>();

    public void setApproximationByEquivalenceClass(Map<ClassUnion, Map<IntArrayKey,EquivalenceClass>> lowApproximationByEquivalenceClass, boolean isLow) {
        for (Map.Entry<ClassUnion, Map<IntArrayKey,EquivalenceClass>> entry : lowApproximationByEquivalenceClass.entrySet()) {
            Collection<Instance> instances = new ArrayList<>();
            for (EquivalenceClass equivalenceClass : entry.getValue().values()) {
                instances.addAll(equivalenceClass.getInstances());
            }
            if (isLow) {
                lowApproximations.put(entry.getKey(),instances);
            }else{
                upperApproximations.put(entry.getKey(),instances);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Approximations \n");
        sb.append("   lowApproximations : \n");
        for (Map.Entry<ClassUnion, Collection<Instance>> entry : lowApproximations.entrySet()) {
            sb.append("     ");
            sb.append(entry.getKey().toString() + " : ");
            sb.append(entry.getValue().stream().map(instance -> instance.getNum()).sorted().collect(Collectors.toList()).toString() + "," + "\n");
            ;
        }
        sb.append("   upperApproximations : \n");
        for (Map.Entry<ClassUnion, Collection<Instance>> entry : this.upperApproximations.entrySet()) {
            sb.append("     ");
            sb.append(entry.getKey().toString() + " : ");
            sb.append(entry.getValue().stream().map(instance -> instance.getNum()).sorted().collect(Collectors.toList()).toString() + "\n");
        }
        return sb.toString();
    }
}
