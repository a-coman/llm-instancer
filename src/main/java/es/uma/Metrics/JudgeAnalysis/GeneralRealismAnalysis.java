package es.uma.Metrics.JudgeAnalysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import es.uma.Metrics.JudgeAnalysis.HumanEvalLoader.HumanEvalRecord;
import es.uma.Metrics.JudgeAnalysis.HumanEvalLoader.HumanStatementRow;
import es.uma.Metrics.JudgeAnalysis.HumanEvalLoader.ProblemExists;
import es.uma.Metrics.General;
import es.uma.Utils;

/**
 * Comprehensive realism report: judge (List.txt), formal constraints (General),
 * and human evaluation (human_eval.xlsx).
 */
public class GeneralRealismAnalysis {

    private static final String LIST_FILE = "./src/main/java/es/uma/Metrics/List.txt";
    private static final String INSTANCES_FILE = "./src/main/java/es/uma/Metrics/JudgeAnalysis/GeneralBatchRunnerInstances.txt";
    private static final String HUMAN_EVAL_FILE = "./src/main/java/es/uma/Metrics/JudgeAnalysis/human_eval.xlsx";
    private static final String OUTPUT_FILE_DIR = "./src/main/java/es/uma/Metrics/JudgeAnalysis/";
    private static final String OUTPUT_FILE_NAME = "general-realism-analysis.md";

    private static final int STATEMENT_TRUNCATE = 200;
    private static final int COMMENT_TRUNCATE = 120;
    private static final int INVARIANT_DETAIL_TRUNCATE = 200;
    private static final Pattern INVARIANT_NAME = Pattern.compile(
            "([A-Za-z_][A-Za-z0-9_]*::[A-Za-z_][A-Za-z0-9_]*)");

    enum JudgeLabel {
        REALISTIC,
        UNREALISTIC
    }

    enum Alignment {
        AGREE_R,
        JUDGE_LENIENT,
        AGREE_U_FORMAL,
        SEMANTIC_ONLY,
        HUMAN_REJECTS_U,
        HUMAN_PARTIAL
    }

    record InstanceResult(
            GeneralBatchRunner.InstanceRef ref,
            JudgeLabel judge,
            int syntaxErrors,
            int multiplicitiesErrors,
            int invariantsErrors,
            List<String> invariantFailures,
            HumanEvalRecord human,
            Alignment alignment) {

        String label() {
            return ref.label();
        }

        boolean invariantDetected() {
            return invariantsErrors > 0;
        }

        String judgeCode() {
            return judge == JudgeLabel.REALISTIC ? "R" : "U";
        }

        String invCode() {
            return invariantDetected() ? "U" : "R";
        }

        String humanAuditDisplay() {
            if (judge == JudgeLabel.REALISTIC) {
                return human.auditSummary();
            }
            return "—";
        }

        String invariantsDetail() {
            if (invariantFailures.isEmpty()) {
                return "";
            }
            return invariantFailures.stream()
                    .map(GeneralRealismAnalysis::shortInvariantName)
                    .collect(Collectors.joining("; "));
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US); // dots as decimal separator in reports
        Map<String, JudgeLabel> labels = loadJudgeLabels(Paths.get(LIST_FILE));
        Map<String, HumanEvalRecord> humanEval = HumanEvalLoader.load(Paths.get(HUMAN_EVAL_FILE));
        List<GeneralBatchRunner.InstanceRef> instances = GeneralBatchRunner.loadInstances(Paths.get(INSTANCES_FILE));

        System.out.println("Judge labels loaded: " + labels.size());
        System.out.println("Human eval records: " + humanEval.size());
        System.out.println("Batch instances: " + instances.size());

        GeneralBatchRunner.validateAll(instances);

        List<InstanceResult> results = new ArrayList<>();
        for (GeneralBatchRunner.InstanceRef ref : instances) {
            JudgeLabel judge = labels.get(ref.label());
            if (judge == null) {
                throw new RuntimeException("No judge label in List.txt for: " + ref.label());
            }
            HumanEvalRecord human = humanEval.get(ref.label());
            if (human == null) {
                throw new RuntimeException("No human eval record for: " + ref.label());
            }

            String experimentDir = GeneralBatchRunner.resolveExperimentDir(ref.type(), ref.system());
            String diagramPath = ref.diagramPath();
            String instancePath = ref.instancePath(experimentDir);

            System.out.println("Analyzing: " + ref.label() + " [" + judge + "]");
            General g = new General();
            g.calculate(diagramPath, instancePath);

            boolean invFail = g.getInvariantsErrors() > 0;
            Alignment alignment = computeAlignment(judge, invFail, human.problemExists());

            results.add(new InstanceResult(
                    ref,
                    judge,
                    g.getSyntaxErrors(),
                    g.getMultiplicitiesErrors(),
                    g.getInvariantsErrors(),
                    List.copyOf(g.getInvariantsErrorsList()),
                    human,
                    alignment));
        }

        String report = buildReport(results, labels);
        Utils.saveFile(report, OUTPUT_FILE_DIR, OUTPUT_FILE_NAME, false);
        System.out.println("Done. Wrote " + OUTPUT_FILE_DIR + OUTPUT_FILE_NAME);
    }

    static Alignment computeAlignment(JudgeLabel judge, boolean invFail, ProblemExists humanProblem) {
        if (judge == JudgeLabel.REALISTIC) {
            return invFail ? Alignment.JUDGE_LENIENT : Alignment.AGREE_R;
        }
        return switch (humanProblem) {
            case NO -> Alignment.HUMAN_REJECTS_U;
            case PARTIAL -> Alignment.HUMAN_PARTIAL;
            case YES -> invFail ? Alignment.AGREE_U_FORMAL : Alignment.SEMANTIC_ONLY;
            case NA -> throw new IllegalStateException("Unexpected NA human problem on judge-U instance");
        };
    }

    static Map<String, JudgeLabel> loadJudgeLabels(Path file) {
        if (!Files.isRegularFile(file)) {
            throw new RuntimeException("List file not found: " + file.toAbsolutePath());
        }

        Map<String, JudgeLabel> labels = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(file)) {
                if (!line.startsWith("gpt_5_2")) {
                    continue;
                }
                int tab = line.indexOf('\t');
                if (tab < 0) {
                    throw new RuntimeException("Expected tab-separated judge verdict: " + line);
                }
                String id = line.substring(0, tab).trim();
                String verdict = line.substring(tab + 1).trim();
                if (verdict.startsWith("\"")) {
                    verdict = verdict.substring(1);
                }
                if (verdict.startsWith("U:")) {
                    labels.put(id, JudgeLabel.UNREALISTIC);
                } else if (verdict.startsWith("R:")) {
                    labels.put(id, JudgeLabel.REALISTIC);
                } else {
                    throw new RuntimeException("Expected R: or U: verdict for: " + id);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read List.txt", e);
        }
        return labels;
    }

    private static String buildReport(List<InstanceResult> results, Map<String, JudgeLabel> allLabels) {
        int batchSize = results.size();

        List<InstanceResult> judgeErrorMissed = new ArrayList<>();
        List<InstanceResult> agreementJudgeU = new ArrayList<>();
        List<InstanceResult> judgeUInvariantPass = new ArrayList<>();
        List<InstanceResult> agreementJudgeR = new ArrayList<>();

        int realisticCount = 0;
        int unrealisticCount = 0;
        int humanConfirmsU = 0;
        int humanRejectsU = 0;
        int humanPartialU = 0;
        int invYesHumanYes = 0;
        int invYesHumanNo = 0;
        int invNoHumanYes = 0;
        int invNoHumanNo = 0;
        int rUntraceable = 0;
        int rInvalidGeneric = 0;

        Map<Alignment, Integer> alignmentCounts = new EnumMap<>(Alignment.class);
        for (Alignment a : Alignment.values()) {
            alignmentCounts.put(a, 0);
        }

        for (InstanceResult r : results) {
            if (r.judge() == JudgeLabel.REALISTIC) {
                realisticCount++;
                if (r.human().hasUntraceableSpecific()) {
                    rUntraceable++;
                }
                if (r.human().hasInvalidGeneric()) {
                    rInvalidGeneric++;
                }
            } else {
                unrealisticCount++;
                switch (r.human().problemExists()) {
                    case YES -> humanConfirmsU++;
                    case NO -> humanRejectsU++;
                    case PARTIAL -> humanPartialU++;
                    default -> {
                    }
                }
                if (r.invariantDetected()) {
                    if (r.human().problemExists() == ProblemExists.YES) {
                        invYesHumanYes++;
                    } else if (r.human().problemExists() == ProblemExists.NO) {
                        invYesHumanNo++;
                    }
                } else {
                    if (r.human().problemExists() == ProblemExists.YES) {
                        invNoHumanYes++;
                    } else if (r.human().problemExists() == ProblemExists.NO) {
                        invNoHumanNo++;
                    }
                }
            }

            alignmentCounts.merge(r.alignment(), 1, Integer::sum);

            if (r.judge() == JudgeLabel.REALISTIC && r.invariantDetected()) {
                judgeErrorMissed.add(r);
            } else if (r.judge() == JudgeLabel.UNREALISTIC && r.invariantDetected()) {
                agreementJudgeU.add(r);
            } else if (r.judge() == JudgeLabel.UNREALISTIC && !r.invariantDetected()) {
                judgeUInvariantPass.add(r);
            } else {
                agreementJudgeR.add(r);
            }
        }

        int invariantFailures = judgeErrorMissed.size() + agreementJudgeU.size();
        int judgeErrors = judgeErrorMissed.size();
        int agreement = agreementJudgeU.size() + agreementJudgeR.size();

        double judgeErrorOnR = realisticCount == 0 ? 0 : 100.0 * judgeErrors / realisticCount;
        double judgeUCoverageOfInvariantFailures = invariantFailures == 0 ? 0
                : 100.0 * agreementJudgeU.size() / invariantFailures;
        double judgeUWithFormalSupport = unrealisticCount == 0 ? 0
                : 100.0 * agreementJudgeU.size() / unrealisticCount;
        double formalAgreementRate = batchSize == 0 ? 0 : 100.0 * agreement / batchSize;

        int humanConfirmsOrPartial = humanConfirmsU + humanPartialU;
        double humanConfirmRateOnU = unrealisticCount == 0 ? 0
                : 100.0 * humanConfirmsOrPartial / unrealisticCount;

        StringBuilder sb = new StringBuilder();
        appendMethodology(sb);
        appendSummary(sb, batchSize, allLabels.size(), realisticCount, unrealisticCount,
                invariantFailures, judgeErrors, agreement,
                humanConfirmsU, humanPartialU, humanRejectsU, humanConfirmRateOnU,
                invYesHumanYes, invNoHumanYes, invYesHumanNo, invNoHumanNo,
                rUntraceable, rInvalidGeneric);
        appendJudgeInvariantCrossTable(sb, agreementJudgeU, judgeUInvariantPass, judgeErrorMissed, agreementJudgeR);
        appendJudgeQualityMetrics(sb, judgeErrorOnR, judgeErrors, realisticCount,
                judgeUWithFormalSupport, agreementJudgeU.size(), unrealisticCount,
                judgeUCoverageOfInvariantFailures, invariantFailures,
                formalAgreementRate, agreement, batchSize);
        appendAlignmentCounts(sb, alignmentCounts);
        appendStatementTable(sb, results);
        appendInsightSections(sb, results);

        return sb.toString();
    }

    private static void appendMethodology(StringBuilder sb) {
        sb.append("# Comprehensive realism analysis\n\n");
        sb.append("**Judge** — Gemini R/U labels in [`List.txt`](src/main/java/es/uma/Metrics/List.txt). ");
        sb.append("Primary automated realism verdict.\n\n");
        sb.append("**Formal constraints** — Syntax, multiplicities, and invariant checks from ");
        sb.append("[`General`](src/main/java/es/uma/Metrics/General.java) on `src/main/resources/prompts/` diagrams. ");
        sb.append("Invariants are a **partial** formal reference (not all semantic unrealism is encoded).\n\n");
        sb.append("**Human review** — [`human_eval.xlsx`](src/main/java/es/uma/Metrics/JudgeAnalysis/human_eval.xlsx), ");
        sb.append("one table row per judge statement line.\n\n");
        sb.append("**Column codes** (R = realistic / agrees, U = unrealistic / problem exists):\n");
        sb.append("- `judge` — Gemini verdict from [`List.txt`](src/main/java/es/uma/Metrics/List.txt).\n");
        sb.append("- `inv` — invariant check: fail → `U`, pass → `R`.\n");
        sb.append("- `human` — problem exists Yes → `U`, No → `R`, Partial → `Partial`; ");
        sb.append("Specific traceable Yes → `R`, No → `NotTraceable`; ");
        sb.append("Generic valid Yes → `R`, No → `NotJustified`, Unknown → `Unknown`.\n");
        sb.append("- `human_comments` — column D when problem exists is No or Partial.\n");
        sb.append("- `inv_detail` — failed invariant names when `inv` is `U`.\n\n");
        sb.append("**Reading disagreements:**\n");
        sb.append("- **R + invariant failure** → judge too lenient (`JUDGE_LENIENT`).\n");
        sb.append("- **U + invariant failure + human Yes** → strong consensus (`AGREE_U_FORMAL`).\n");
        sb.append(
                "- **U + invariant pass + human Yes** → semantic unrealism without formal coverage (`SEMANTIC_ONLY`).\n");
        sb.append("- **U + human No** → human disputes the judge (`HUMAN_REJECTS_U`), even if invariants fail.\n\n");
    }

    private static void appendSummary(StringBuilder sb, int batchSize, int labelCount,
            int realisticCount, int unrealisticCount, int invariantFailures, int judgeErrors, int agreement,
            int humanConfirmsU, int humanPartialU, int humanRejectsU, double humanConfirmRateOnU,
            int invYesHumanYes, int invNoHumanYes, int invYesHumanNo, int invNoHumanNo,
            int rUntraceable, int rInvalidGeneric) {

        sb.append("## Summary\n\n");
        sb.append("| Metric | Value |\n|---|---|\n");
        sb.append("| Batch instances analyzed | ").append(batchSize).append(" |\n");
        sb.append("| Judge labels in List.txt | ").append(labelCount).append(" |\n");
        sb.append("| Judge: Realistic (R) | ").append(realisticCount).append(" |\n");
        sb.append("| Judge: Unrealistic (U) | ").append(unrealisticCount).append(" |\n");
        sb.append("| Instances with invariant failures | ").append(invariantFailures).append(" |\n");
        sb.append("| Judge errors (R but invariants fail) | ").append(judgeErrors).append(" |\n");
        sb.append("| Agreement (judge matches invariants) | ").append(agreement).append(" |\n\n");

        sb.append("### Judge × human (U instances only, n=").append(unrealisticCount).append(")\n\n");
        sb.append("| Metric | Value |\n|---|---|\n");
        sb.append("| Human confirms problem (Yes) | ").append(humanConfirmsU).append(" |\n");
        sb.append("| Human partial (Partial) | ").append(humanPartialU).append(" |\n");
        sb.append("| Human rejects judge (No) | ").append(humanRejectsU).append(" |\n");
        sb.append("| Human confirms rate (Yes + Partial) | ").append(formatPct(humanConfirmRateOnU))
                .append(" (").append(humanConfirmsU + humanPartialU).append("/").append(unrealisticCount)
                .append(") |\n\n");

        sb.append("### Invariants × human (U instances only)\n\n");
        sb.append("|  | Human Yes | Human No |\n");
        sb.append("|---|---|---|\n");
        sb.append("| Invariant failures | ").append(invYesHumanYes).append(" | ").append(invYesHumanNo)
                .append(" |\n");
        sb.append("| No invariant failures | ").append(invNoHumanYes).append(" | ").append(invNoHumanNo)
                .append(" |\n\n");

        sb.append("### R explanation audit (R instances only)\n\n");
        sb.append("| Metric | Value |\n|---|---|\n");
        sb.append("| R with non-traceable specific statement | ").append(rUntraceable).append(" |\n");
        sb.append("| R with invalid generic statement | ").append(rInvalidGeneric).append(" |\n\n");
    }

    private static void appendJudgeInvariantCrossTable(StringBuilder sb,
            List<InstanceResult> agreementJudgeU,
            List<InstanceResult> judgeUInvariantPass,
            List<InstanceResult> judgeErrorMissed,
            List<InstanceResult> agreementJudgeR) {

        sb.append("## Cross-table (judge × invariants)\n\n");
        sb.append("|  | Invariant failures | No invariant failures |\n");
        sb.append("|---|---|---|\n");
        sb.append("| **Judge: Unrealistic (U)** | Agreement: judge U + formal support (")
                .append(agreementJudgeU.size()).append(") | Judge U, invariants pass (")
                .append(judgeUInvariantPass.size()).append(") |\n");
        sb.append("| **Judge: Realistic (R)** | Judge error: said R, invariants fail (")
                .append(judgeErrorMissed.size()).append(") | Agreement: judge R + formally OK (")
                .append(agreementJudgeR.size()).append(") |\n\n");
    }

    private static void appendJudgeQualityMetrics(StringBuilder sb,
            double judgeErrorOnR, int judgeErrors, int realisticCount,
            double judgeUWithFormalSupport, int agreementJudgeU, int unrealisticCount,
            double judgeUCoverageOfInvariantFailures, int invariantFailures,
            double formalAgreementRate, int agreement, int batchSize) {

        sb.append("## Judge quality metrics (judge × invariants)\n\n");
        sb.append("| Metric | Value | Meaning |\n");
        sb.append("|---|---|---|\n");
        sb.append("| Judge error rate on R labels | ").append(formatPct(judgeErrorOnR)).append(" (")
                .append(judgeErrors).append("/").append(realisticCount)
                .append(") | Said realistic but invariants failed |\n");
        sb.append("| Judge U with formal support | ").append(formatPct(judgeUWithFormalSupport)).append(" (")
                .append(agreementJudgeU).append("/").append(unrealisticCount)
                .append(") | Said unrealistic and invariants agree |\n");
        sb.append("| Judge U among invariant failures | ").append(formatPct(judgeUCoverageOfInvariantFailures))
                .append(" (").append(agreementJudgeU).append("/").append(invariantFailures)
                .append(") | Of formally failing instances, how many judge marked U |\n");
        sb.append("| Formal agreement rate | ").append(formatPct(formalAgreementRate)).append(" (")
                .append(agreement).append("/").append(batchSize)
                .append(") | Judge and invariants same sign |\n\n");
    }

    private static void appendAlignmentCounts(StringBuilder sb, Map<Alignment, Integer> alignmentCounts) {
        sb.append("## Three-way alignment counts\n\n");
        sb.append("| Alignment | Count | Meaning |\n");
        sb.append("|---|---|---|\n");
        sb.append("| `AGREE_R` | ").append(alignmentCounts.get(Alignment.AGREE_R))
                .append(" | R, invariants pass |\n");
        sb.append("| `JUDGE_LENIENT` | ").append(alignmentCounts.get(Alignment.JUDGE_LENIENT))
                .append(" | R, invariants fail |\n");
        sb.append("| `AGREE_U_FORMAL` | ").append(alignmentCounts.get(Alignment.AGREE_U_FORMAL))
                .append(" | U, human Yes, invariants fail |\n");
        sb.append("| `SEMANTIC_ONLY` | ").append(alignmentCounts.get(Alignment.SEMANTIC_ONLY))
                .append(" | U, human Yes, invariants pass |\n");
        sb.append("| `HUMAN_REJECTS_U` | ").append(alignmentCounts.get(Alignment.HUMAN_REJECTS_U))
                .append(" | U, human No |\n");
        sb.append("| `HUMAN_PARTIAL` | ").append(alignmentCounts.get(Alignment.HUMAN_PARTIAL))
                .append(" | U, human Partial |\n\n");
    }

    private static void appendStatementTable(StringBuilder sb, List<InstanceResult> results) {
        sb.append("## Per-statement table\n\n");
        sb.append("| instanceId | statement | judge | inv | human | human_comments | inv_detail |\n");
        sb.append("|---|---|---|---|---|---|---|\n");

        int rowCount = 0;
        for (InstanceResult r : results) {
            List<HumanStatementRow> rows = r.human().statementRows();
            if (rows.isEmpty()) {
                throw new RuntimeException("No human eval statement rows for: " + r.label());
            }
            String invDetail = r.invariantDetected()
                    ? escapeCell(truncate(r.invariantsDetail(), INVARIANT_DETAIL_TRUNCATE))
                    : "";
            for (HumanStatementRow stmt : rows) {
                sb.append("| ").append(escapeCell(r.label())).append(" | ");
                sb.append(escapeCell(truncate(stmt.statement(), STATEMENT_TRUNCATE))).append(" | ");
                sb.append(r.judgeCode()).append(" | ");
                sb.append(r.invCode()).append(" | ");
                sb.append(escapeCell(HumanEvalRecord.humanVerdictForRow(stmt))).append(" | ");
                sb.append(escapeCell(truncate(HumanEvalRecord.humanCommentsForRow(stmt), COMMENT_TRUNCATE)))
                        .append(" | ");
                sb.append(invDetail).append(" |\n");
                rowCount++;
            }
        }
        sb.append("\n_Total statement rows: ").append(rowCount).append("_\n\n");
    }

    private static void appendInsightSections(StringBuilder sb, List<InstanceResult> results) {
        appendAlignmentSection(sb, "Judge errors (JUDGE_LENIENT)",
                filterByAlignment(results, Alignment.JUDGE_LENIENT), true, true);
        appendAlignmentSection(sb, "Human rejects judge U (HUMAN_REJECTS_U)",
                filterByAlignment(results, Alignment.HUMAN_REJECTS_U), true, true);
        appendAlignmentSection(sb, "Semantic-only unrealism (SEMANTIC_ONLY)",
                filterByAlignment(results, Alignment.SEMANTIC_ONLY), false, true);
        appendAlignmentSection(sb, "Triple agreement — U, human Yes, invariants fail (AGREE_U_FORMAL)",
                filterByAlignment(results, Alignment.AGREE_U_FORMAL), true, false);
        appendAlignmentSection(sb, "Human partial on U (HUMAN_PARTIAL)",
                filterByAlignment(results, Alignment.HUMAN_PARTIAL), true, true);
    }

    private static List<InstanceResult> filterByAlignment(List<InstanceResult> results, Alignment alignment) {
        return results.stream().filter(r -> r.alignment() == alignment).toList();
    }

    private static void appendAlignmentSection(StringBuilder sb, String title,
            List<InstanceResult> list, boolean showFailures, boolean showHumanNote) {
        sb.append("## ").append(title).append(" (").append(list.size()).append(")\n\n");
        if (list.isEmpty()) {
            sb.append("_None._\n\n");
            return;
        }
        for (InstanceResult r : list) {
            sb.append("- **").append(r.label()).append("** — ");
            sb.append("judge: ").append(r.judgeCode()).append(", ");
            sb.append("inv: ").append(r.invCode()).append(", ");
            sb.append("invariants: ").append(r.invariantsErrors());
            if (r.judge() == JudgeLabel.UNREALISTIC) {
                sb.append(", human: ").append(r.human().humanProblemDisplay());
            } else {
                sb.append(", audit: ").append(r.humanAuditDisplay());
            }
            sb.append("\n");
            if (showHumanNote && !r.human().problemExplanation().isEmpty()) {
                sb.append("  - _Human note:_ ").append(r.human().problemExplanation().trim().replace("\n", " "))
                        .append("\n");
            }
            if (showFailures && !r.invariantFailures().isEmpty()) {
                for (String failure : r.invariantFailures()) {
                    sb.append("  - `").append(failure.trim().replace("\n", " ")).append("`\n");
                }
            }
        }
        sb.append("\n");
    }

    static String shortInvariantName(String failure) {
        Matcher matcher = INVARIANT_NAME.matcher(failure);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return failure.trim().replace("\n", " ");
    }

    private static String escapeCell(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "/").replace("\n", " ").replace("\r", "");
    }

    private static String truncate(String value, int maxLen) {
        if (value == null || value.length() <= maxLen) {
            return value == null ? "" : value;
        }
        return value.substring(0, maxLen - 3) + "...";
    }

    private static String formatPct(double value) {
        return String.format("%.2f%%", value);
    }
}
