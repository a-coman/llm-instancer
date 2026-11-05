package es.uma.Metrics.JudgeAnalysis;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Loads human evaluation data from human_eval.xlsx (OOXML) without external
 * dependencies.
 */
public final class HumanEvalLoader {

    private static final Pattern CELL_COL = Pattern.compile("^([A-Z]+)");

    public enum ProblemExists {
        YES,
        NO,
        PARTIAL,
        NA
    }

    public record HumanStatementRow(
            String statement,
            ProblemExists rowProblem,
            String rowComment,
            String kind,
            String traceable,
            String valid) {

        boolean hasContent() {
            return !statement.isBlank() || !kind.isBlank() || rowProblem != ProblemExists.NA;
        }
    }

    public record HumanEvalRecord(
            String instanceId,
            ProblemExists problemExists,
            String problemExplanation,
            List<HumanStatementRow> statementRows) {

        public boolean hasUntraceableSpecific() {
            for (HumanStatementRow r : statementRows) {
                if ("Specific".equalsIgnoreCase(r.kind()) && "No".equalsIgnoreCase(r.traceable())) {
                    return true;
                }
            }
            return false;
        }

        public boolean hasInvalidGeneric() {
            for (HumanStatementRow r : statementRows) {
                if ("Generic".equalsIgnoreCase(r.kind()) && "No".equalsIgnoreCase(r.valid())) {
                    return true;
                }
            }
            return false;
        }

        public String auditSummary() {
            int statements = statementRows.size();
            int issues = 0;
            if (hasUntraceableSpecific()) {
                issues++;
            }
            if (hasInvalidGeneric()) {
                issues++;
            }
            return statements + " stmt, " + issues + " issues";
        }

        public String humanProblemDisplay() {
            return switch (problemExists) {
                case YES -> "Yes";
                case NO -> "No";
                case PARTIAL -> "Partial";
                case NA -> "—";
            };
        }

        /**
         * Human verdict for one spreadsheet statement row (R/U/Partial/audit codes).
         */
        public static String humanVerdictForRow(HumanStatementRow row) {
            if (row.rowProblem() != ProblemExists.NA) {
                return switch (row.rowProblem()) {
                    case YES -> "U";
                    case NO -> "R";
                    case PARTIAL -> "Partial";
                    case NA -> "";
                };
            }
            if ("Specific".equalsIgnoreCase(row.kind())) {
                if ("Yes".equalsIgnoreCase(row.traceable())) {
                    return "R";
                }
                if ("No".equalsIgnoreCase(row.traceable())) {
                    return "NotTraceable";
                }
            }
            if ("Generic".equalsIgnoreCase(row.kind())) {
                if ("Yes".equalsIgnoreCase(row.valid())) {
                    return "R";
                }
                if ("No".equalsIgnoreCase(row.valid())) {
                    return "NotJustified";
                }
                if ("Unknown".equalsIgnoreCase(row.valid())) {
                    return "Unknown";
                }
            }
            return "";
        }

        public static String humanCommentsForRow(HumanStatementRow row) {
            if (row.rowProblem() == ProblemExists.NO || row.rowProblem() == ProblemExists.PARTIAL) {
                return row.rowComment();
            }
            return "";
        }
    }

    private HumanEvalLoader() {
    }

    public static Map<String, HumanEvalRecord> load(Path xlsxPath) {
        if (!Files.isRegularFile(xlsxPath)) {
            throw new RuntimeException("Human eval file not found: " + xlsxPath.toAbsolutePath());
        }

        try (ZipFile zip = new ZipFile(xlsxPath.toFile())) {
            List<String> sharedStrings = readSharedStrings(zip);
            List<Map<String, String>> rows = readSheetRows(zip, sharedStrings);
            return parseRows(rows);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read human eval xlsx: " + xlsxPath, e);
        }
    }

    private static List<String> readSharedStrings(ZipFile zip) throws IOException {
        ZipEntry entry = zip.getEntry("xl/sharedStrings.xml");
        if (entry == null) {
            throw new RuntimeException("Missing xl/sharedStrings.xml in xlsx");
        }

        List<String> strings = new ArrayList<>();
        try (InputStream in = zip.getInputStream(entry)) {
            Document doc = parseXml(in);
            NodeList siNodes = doc.getElementsByTagNameNS(
                    "http://schemas.openxmlformats.org/spreadsheetml/2006/main", "si");
            for (int i = 0; i < siNodes.getLength(); i++) {
                Element si = (Element) siNodes.item(i);
                NodeList tNodes = si.getElementsByTagNameNS(
                        "http://schemas.openxmlformats.org/spreadsheetml/2006/main", "t");
                StringBuilder text = new StringBuilder();
                for (int j = 0; j < tNodes.getLength(); j++) {
                    text.append(tNodes.item(j).getTextContent());
                }
                strings.add(text.toString());
            }
        }
        return strings;
    }

    private static List<Map<String, String>> readSheetRows(ZipFile zip, List<String> sharedStrings)
            throws IOException {
        ZipEntry entry = zip.getEntry("xl/worksheets/sheet1.xml");
        if (entry == null) {
            throw new RuntimeException("Missing xl/worksheets/sheet1.xml in xlsx");
        }

        List<Map<String, String>> rows = new ArrayList<>();
        try (InputStream in = zip.getInputStream(entry)) {
            Document doc = parseXml(in);
            NodeList rowNodes = doc.getElementsByTagNameNS(
                    "http://schemas.openxmlformats.org/spreadsheetml/2006/main", "row");
            for (int i = 0; i < rowNodes.getLength(); i++) {
                Element row = (Element) rowNodes.item(i);
                Map<String, String> cells = new LinkedHashMap<>();
                NodeList cellNodes = row.getElementsByTagNameNS(
                        "http://schemas.openxmlformats.org/spreadsheetml/2006/main", "c");
                for (int j = 0; j < cellNodes.getLength(); j++) {
                    Element cell = (Element) cellNodes.item(j);
                    String ref = cell.getAttribute("r");
                    Matcher m = CELL_COL.matcher(ref);
                    if (!m.find()) {
                        continue;
                    }
                    String col = m.group(1);
                    String value = cellValue(cell, sharedStrings);
                    cells.put(col, value);
                }
                rows.add(cells);
            }
        }
        return rows;
    }

    private static String cellValue(Element cell, List<String> sharedStrings) {
        String type = cell.getAttribute("t");
        NodeList vNodes = cell.getElementsByTagNameNS(
                "http://schemas.openxmlformats.org/spreadsheetml/2006/main", "v");
        if (vNodes.getLength() == 0) {
            return "";
        }
        String raw = vNodes.item(0).getTextContent();
        if ("s".equals(type)) {
            int idx = Integer.parseInt(raw);
            return sharedStrings.get(idx);
        }
        return raw;
    }

    private static Document parseXml(InputStream in) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newDocumentBuilder().parse(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse xlsx XML", e);
        }
    }

    static Map<String, HumanEvalRecord> parseRows(List<Map<String, String>> rows) {
        if (rows.isEmpty()) {
            throw new RuntimeException("Human eval sheet is empty");
        }

        Map<String, String> header = rows.get(0);
        if (!header.getOrDefault("A", "").equals("instance_id")) {
            throw new RuntimeException(
                    "Unexpected human eval header in column A: " + header.getOrDefault("A", "(missing)"));
        }

        Map<String, HumanEvalRecord> records = new LinkedHashMap<>();
        String currentId = null;
        ProblemExists instanceProblem = ProblemExists.NA;
        StringBuilder explanation = new StringBuilder();
        List<HumanStatementRow> statementRows = new ArrayList<>();

        for (int i = 1; i < rows.size(); i++) {
            Map<String, String> cells = rows.get(i);
            String instanceId = cells.getOrDefault("A", "").trim();
            if (!instanceId.isEmpty()) {
                if (currentId != null) {
                    records.put(currentId,
                            buildRecord(currentId, instanceProblem, explanation.toString(), statementRows));
                }
                currentId = instanceId;
                instanceProblem = parseProblem(cells.getOrDefault("C", ""));
                explanation = new StringBuilder();
                statementRows = new ArrayList<>();
                appendExplanation(explanation, cells);
                addStatementRow(statementRows, cells);
            } else if (currentId != null) {
                appendExplanation(explanation, cells);
                addStatementRow(statementRows, cells);
            }
        }

        if (currentId != null) {
            records.put(currentId, buildRecord(currentId, instanceProblem, explanation.toString(), statementRows));
        }

        return records;
    }

    private static void appendExplanation(StringBuilder explanation, Map<String, String> cells) {
        String explainPart = cells.getOrDefault("D", "").trim();
        if (explainPart.isEmpty()) {
            return;
        }
        if (explanation.length() > 0) {
            explanation.append(' ');
        }
        explanation.append(explainPart);
    }

    private static void addStatementRow(List<HumanStatementRow> statementRows, Map<String, String> cells) {
        String statement = cells.getOrDefault("B", "").trim();
        ProblemExists rowProblem = parseProblem(cells.getOrDefault("C", ""));
        String rowComment = cells.getOrDefault("D", "").trim();
        String kind = cells.getOrDefault("E", "").trim();
        String traceable = cells.getOrDefault("F", "").trim();
        String valid = cells.getOrDefault("G", "").trim();

        HumanStatementRow row = new HumanStatementRow(statement, rowProblem, rowComment, kind, traceable, valid);
        if (!row.hasContent()) {
            return;
        }
        statementRows.add(row);
    }

    private static HumanEvalRecord buildRecord(
            String id,
            ProblemExists problem,
            String explanation,
            List<HumanStatementRow> statementRows) {
        return new HumanEvalRecord(id, problem, explanation.trim(), List.copyOf(statementRows));
    }

    private static ProblemExists parseProblem(String value) {
        if (value.isEmpty()) {
            return ProblemExists.NA;
        }
        return switch (value.trim().toLowerCase()) {
            case "yes" -> ProblemExists.YES;
            case "no" -> ProblemExists.NO;
            case "partial" -> ProblemExists.PARTIAL;
            default -> throw new RuntimeException("Unknown human problem value: " + value);
        };
    }
}
