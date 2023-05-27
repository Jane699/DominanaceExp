package featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec;

import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daiYang_wu
 */
@Data
public class ClassUnionDecHash {
    private Map<Integer, ClassUnionDec> lowClassUnionDecHash;
    private Map<Integer, ClassUnionDec> upperClassUnionDecHash;
    private Collection<ClassUnion> classUnionList;

    public ClassUnionDecHash(Collection<ClassUnion> classUnions) {
        init(classUnions);
    }

    public ClassUnionDec getClassUnionDecByClassUnion(ClassUnion classUnion) {
        if (classUnion.isBiggerOrEqual()) {
            return upperClassUnionDecHash.get(classUnion.getDecisionValue());
        } else {
            return lowClassUnionDecHash.get(classUnion.getDecisionValue());
        }
    }

    public Collection<EquivalenceClass> getLowClassUnionDecPos(Integer d) {
        return lowClassUnionDecHash.get(d).getPosDecList();
    }

    public Collection<EquivalenceClass> getLowClassUnionDecBou(Integer d) {
        return lowClassUnionDecHash.get(d).getBouDecList();
    }

    public Collection<EquivalenceClass> getLowClassUnionDecNeg(Integer d) {
        return lowClassUnionDecHash.get(d).getNegDecList();
    }

    public Collection<EquivalenceClass> getUpperClassUnionDecPos(Integer d) {
        return upperClassUnionDecHash.get(d).getPosDecList();
    }

    public Collection<EquivalenceClass> getUpperClassUnionDecBou(Integer d) {
        return upperClassUnionDecHash.get(d).getBouDecList();
    }

    public Collection<EquivalenceClass> getUpperClassUnionDecNeg(Integer d) {
        return upperClassUnionDecHash.get(d).getNegDecList();
    }

    public void putLowClassUnionDecPos(Integer d, EquivalenceClass e) {
        if (this.lowClassUnionDecHash.get(d) != null) this.lowClassUnionDecHash.get(d).getPosDecList().add(e);
    }

    public void putLowClassUnionDecBou(Integer d, EquivalenceClass e) {
        if (this.lowClassUnionDecHash.get(d) != null) this.lowClassUnionDecHash.get(d).getBouDecList().add(e);
    }

    public void putLowClassUnionDecNeg(Integer d, EquivalenceClass e) {
        if (this.lowClassUnionDecHash.get(d) != null) this.lowClassUnionDecHash.get(d).getNegDecList().add(e);
    }

    public void putUpperClassUnionDecPos(Integer d, EquivalenceClass e) {
        if (this.upperClassUnionDecHash.get(d) != null) this.upperClassUnionDecHash.get(d).getPosDecList().add(e);
    }

    public void putUpperClassUnionDecBou(Integer d, EquivalenceClass e) {
        if (this.upperClassUnionDecHash.get(d) != null) this.upperClassUnionDecHash.get(d).getBouDecList().add(e);
    }

    public void putUpperClassUnionDecNeg(Integer d, EquivalenceClass e) {
        if (this.upperClassUnionDecHash.get(d) != null) this.upperClassUnionDecHash.get(d).getNegDecList().add(e);
    }

    private void init(Collection<ClassUnion> classUnions) {
        this.classUnionList = classUnions;
        this.lowClassUnionDecHash = new LinkedHashMap<>();
        this.upperClassUnionDecHash = new LinkedHashMap<>();
        for (ClassUnion classUnion : classUnions) {
            if (classUnion.isBiggerOrEqual()) {
                upperClassUnionDecHash.put(classUnion.getDecisionValue(), new ClassUnionDec());
            } else {
                lowClassUnionDecHash.put(classUnion.getDecisionValue(), new ClassUnionDec());
            }
        }

    }

    @Data
    public class ClassUnionDec {
        protected List<EquivalenceClass> posDecList = new ArrayList<>();
        protected List<EquivalenceClass> bouDecList = new ArrayList<>();
        protected List<EquivalenceClass> negDecList = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("     DIG      |       aG      |       nG      |       pG      \n");
        for (Map.Entry<Integer, ClassUnionDec> lowEntry : lowClassUnionDecHash.entrySet()) {
            StringBuilder aG = new StringBuilder();

            sb.append(String.format("     <=%d        |       %s      |       %s      |       %s      \n",
                    lowEntry.getKey(),
                    lowEntry.getValue().getPosDecList().stream().map(equivalenceClass ->
                            equivalenceClass.getIntArrayKey().toString()).collect(Collectors.joining("-", "{", "}")),

                    lowEntry.getValue().getNegDecList().stream().map(equivalenceClass ->
                            equivalenceClass.getIntArrayKey().toString()).collect(Collectors.joining("-", "{", "}")),

                    lowEntry.getValue().getBouDecList().stream().map(equivalenceClass ->
                            equivalenceClass.getIntArrayKey().toString()).collect(Collectors.joining("-", "{", "}"))
            ));
        }

        for (Map.Entry<Integer, ClassUnionDec> upperEntry : upperClassUnionDecHash.entrySet()) {
            StringBuilder aG = new StringBuilder();

            sb.append(String.format("     >=%d        |       %s      |       %s      |       %s      \n",
                    upperEntry.getKey(),
                    upperEntry.getValue().getPosDecList().stream().map(equivalenceClass ->
                            equivalenceClass.getIntArrayKey().toString()).collect(Collectors.joining("-", "{", "}")),

                    upperEntry.getValue().getNegDecList().stream().map(equivalenceClass ->
                            equivalenceClass.getIntArrayKey().toString()).collect(Collectors.joining("-", "{", "}")),

                    upperEntry.getValue().getBouDecList().stream().map(equivalenceClass ->
                            equivalenceClass.getIntArrayKey().toString()).collect(Collectors.joining("-", "{", "}"))
            ));
        }

        return sb.toString();
    }
}
