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
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.OptimizedMethodCalculateApproximationsAlgorithm;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.opac.OPACApproximations;
import featureSelection.tester.procedure.ComponentTags;
import featureSelection.tester.procedure.param.ParameterConstants;
import featureSelection.tester.utils.ProcedureUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author daiYang_wu
 */
@Slf4j
public class OptimizedMethodCalculateApproximationsAlgorithmQRTester extends DefaultProcedureContainer<Collection<Integer>>
        implements TimeSum,
        ReportMapGenerated<String, Map<String, Object>>,
        StatisticsCalculated {
    private boolean logOn;
    @Getter
    private Statistics statistics;
    @Getter
    private Map<String, Map<String, Object>> report;

    public OptimizedMethodCalculateApproximationsAlgorithmQRTester(ProcedureParameters parameters, boolean logOn) {
        super(logOn ? log : null, parameters);
        this.logOn = logOn;
        statistics = new Statistics();
        report = new HashMap<>();
    }

    @Override
    public ProcedureComponent<?>[] initComponents() {
        return new ProcedureComponent<?>[]{
                new TimeCountedProcedureComponent<OPACApproximations>(
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
                            return OptimizedMethodCalculateApproximationsAlgorithm.Basic.initArrayCl(decisionValues, dataset.size());
                        },
                        (component, opacapproximations) -> {
                            getParameters().set(true, "ArrayCl", opacapproximations);
                        }
                ) {
                    @Override
                    public void init() {
                    }

                    @Override
                    public String staticsName() {
                        return shortName() + " | 1. of " + getComponents().size() + "." + " " + getDescription();
                    }
                }.setDescription("init ArrayCl"),

                new TimeCountedProcedureComponent<OPACApproximations>(
                        ComponentTags.TAG_APPROXIMATION,
                        this.getParameters(),
                        (component) -> {
                            if (logOn) {
                                log.info("2. " + component.getDescription());
                            }
                            component.setLocalParameters(new Object[]{
                                    getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_INSTANCES),
                                    getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
                                    getParameters().get("decisionValues"),
                                    getParameters().get("ArrayCl"),
                            });
                        },
                        true,
                        (component, parameters) -> {
                            int p = 0;
                            Collection<Instance> dataset = (Collection<Instance>) parameters[p++];
                            int [] attributes = (int[]) parameters[p++];
                            List<Integer> decisionValues = (List<Integer>) parameters[p++];
                            OPACApproximations OPACApproximations = (OPACApproximations) parameters[p++];
                            int n = dataset.size();
                            int[] relationArray = new int[(n * (1 + n)) / 2];
                            for (Integer t : decisionValues) {
                                log.info("current t : " + t.toString());
                                OptimizedMethodCalculateApproximationsAlgorithm.Basic.forCalculateDominancePosOrNeg((List<Instance>) dataset, OPACApproximations, t, decisionValues, relationArray,attributes);
                            }
                            return OPACApproximations;
                        },
                        (component, OPACApproximations) -> {
                            getParameters().set(true, "ArrayCl", OPACApproximations);
                            getStatistics().put("compareCount", DominanceCommonCalculate.CompareCount.getCount());
                            DominanceCommonCalculate.CompareCount.reset();
                        }
                ) {
                    @Override
                    public void init() {

                    }

                    @Override
                    public String staticsName() {
                        return shortName() + " | 2. of " + getComponents().size() + "." + " " + getDescription();
                    }
                }.setDescription(" calculate dominance pos and neg for all instance")
        };
    }

    @Override
    public String shortName() {
        return "calculate lowerApproximation by OPAC";
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
