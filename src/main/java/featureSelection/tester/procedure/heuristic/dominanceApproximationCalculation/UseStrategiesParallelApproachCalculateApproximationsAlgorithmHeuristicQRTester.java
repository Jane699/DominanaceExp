package featureSelection.tester.procedure.heuristic.dominanceApproximationCalculation;

import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.basic.procedure.ProcedureComponent;
import featureSelection.basic.procedure.component.TimeCountedProcedureComponent;
import featureSelection.basic.procedure.container.DefaultProcedureContainer;
import featureSelection.basic.procedure.parameter.ProcedureParameters;
import featureSelection.basic.procedure.report.ReportMapGenerated;
import featureSelection.basic.procedure.statistics.Statistics;
import featureSelection.basic.procedure.statistics.StatisticsCalculated;
import featureSelection.basic.procedure.timer.TimeSum;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common.DominanceCommonCalculate;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.ParallelApproachCalculateApproximationsAlgorithm;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.DominanceApproximation;
import featureSelection.tester.procedure.ComponentTags;
import featureSelection.tester.procedure.param.ParameterConstants;
import featureSelection.tester.utils.DBUtils;
import featureSelection.tester.utils.ProcedureUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.ParallelApproachCalculateApproximationsAlgorithm.Basic.*;

/**
 * @author daiYang_wu
 */

@Slf4j
public class UseStrategiesParallelApproachCalculateApproximationsAlgorithmHeuristicQRTester extends DefaultProcedureContainer<Collection<Integer>>
        implements TimeSum,
        ReportMapGenerated<String, Map<String, Object>>,
        StatisticsCalculated {

    private boolean logOn;
    @Getter
    private Statistics statistics;
    @Getter
    private Map<String, Map<String, Object>> report;

    public UseStrategiesParallelApproachCalculateApproximationsAlgorithmHeuristicQRTester(ProcedureParameters parameters, boolean logOn) {
        super(logOn ? log : null, parameters);
        this.logOn = logOn;
        statistics = new Statistics();
        report = new HashMap<>();
    }

    @Override
    public ProcedureComponent<?>[] initComponents() {
        return new ProcedureComponent<?>[]{
                new TimeCountedProcedureComponent<Map<ClassUnion, Collection<Instance>>>(
                        ComponentTags.TAG_PREPARE,
                        this.getParameters(),
                        (component) -> {
                            if (logOn) {
                                log.info("1. " + component.getDescription());
                            }
                            component.setLocalParameters(new Object[]{
                                    getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_INSTANCES),
                                    getParameters().get("decisionValues"),
                            });
                        },
                        true,
                        (component, parameters) -> {
                            int p = 0;
                            Collection<Instance> dataset = (Collection<Instance>) parameters[p++];
                            List<Integer> decisionValues = (List<Integer>) parameters[p++];
                            Map<ClassUnion, Collection<Instance>> classUnionSet = DominanceCommonCalculate.calculateClassUnionSet(dataset, decisionValues);
                            return classUnionSet;
                        },
                        (component, classUnionSet) -> {
                            getParameters().set(true, "classUnionSet", classUnionSet);

                        }) {
                    @Override
                    public String staticsName() {
                        return shortName() + " | 1. of " + getComponents().size() + "." + " " + getDescription();
                    }

                    @Override
                    public void init() {

                    }
                }.setDescription("calculate class union"),

                new TimeCountedProcedureComponent<DominanceApproximation>(
                        ComponentTags.TAG_LOW_APPROXIMATION,
                        this.getParameters(),
                        (component) -> {
                            if (logOn) {
                                log.info("2. " + component.getDescription());
                            }
                            component.setLocalParameters(new Object[]{
                                    getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_INSTANCES),
                                    getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
                                    getParameters().get("classUnionSet"),
                            });
                        },
                        true,
                        (component, parameters) -> {
                            int p = 0;
                            Collection<Instance> dataset = (Collection<Instance>) parameters[p++];
                            int[] attributes = (int[]) parameters[p++];
                            List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(dataset);
                            Map<ClassUnion, Collection<Instance>> classUnionSet = (Map<ClassUnion, Collection<Instance>>) parameters[p++];
                            Map<ClassUnion, Collection<Instance>> DSLACClassUnionMap = ParallelApproachCalculateApproximationsAlgorithm.Basic.classUnionByDSLAC(decisionValues,classUnionSet);
                            Map<ClassUnion, Collection<Instance>> classUnionLowerApproximationMap = new HashMap<>(classUnionSet.size());
                            for (ClassUnion classUnion : classUnionSet.keySet()) {
                                if (logOn) {
                                    log.info("  calculate lowApproximation for " + classUnion.toString());
                                }
                                classUnionLowerApproximationMap.put(classUnion, calculateLowerApproximationByClassUnionByDSLAC(dataset, attributes, classUnion, classUnionSet,DSLACClassUnionMap));
                            }
                            DominanceApproximation dominanceApproximation = new DominanceApproximation();
                            dominanceApproximation.setLowApproximations(classUnionLowerApproximationMap);
                            return dominanceApproximation;
                        },
                        (component, dominanceApproximation) -> {
                            getParameters().set(true, "dominanceApproximation", dominanceApproximation);
                        }
                ) {
                    @Override
                    public void init() {

                    }

                    @Override
                    public String staticsName() {
                        return shortName() + " | 2. of " + getComponents().size() + "." + " " + getDescription();
                    }
                }.setDescription("calculate low approximation")
        };
    }

    @Override
    public String shortName() {
        return "calculate Approximations by PIAC-DSLAC";
    }

    @Override
    public String reportName() {
        return shortName();
    }


    @Override
    public String staticsName() {
        return shortName();
    }

    @Override
    public long getTime() {
        return getComponents().stream()
                .map(comp -> ProcedureUtils.Time.sumProcedureComponentTimes(comp))
                .reduce(Long::sum).orElse(0L);
    }

    @Override
    public Map<String, Long> getTimeDetailByTags() {
        return ProcedureUtils.Time.sumProcedureComponentsTimesByTags(this);
    }

    @Override
    public String[] getReportMapKeyOrder() {
        return getComponents().stream().map(ProcedureComponent::getDescription).toArray(String[]::new);
    }
}
