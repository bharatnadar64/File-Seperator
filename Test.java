import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;

class Test {
    static HashSet<String> ignoreList = new HashSet<>(Arrays.asList("Test.java", "Test.class", ".git"));

    public static void main(String[] args) {
        boolean doneStrict = false;
        Scanner inp = new Scanner(System.in);
        int choice;
        Map<String, Integer> ext;
        Map<String, ArrayList<String>> originalState;
        Map<String, Double> minFile;
        Map<String, Double> maxFile;
        String report = null;
        HashSet<String> dir;
        try {
            String workingDir = System.getProperty("user.dir");
            File directory = new File(workingDir);
            originalState = ogState(directory);
            ext = fileSummary(directory);
            dir = dirSummary(directory);

            while (true) {
                System.out.println("File Manager");
                System.out.println(
                        "1. Summary \n2.Manage Files(Safe Mode) \n3.Manage Files(Strict Mode) \n4.Revert Strict Mode \n5.Generate Report(CSV) \n6.Exit");
                System.out.print("Choice: ");
                choice = inp.nextInt();
                switch (choice) {
                    case 1:
                        ext = fileSummary(directory);
                        dir = dirSummary(directory);
                        minFile = extremeSizeInFloat(directory, "min");
                        maxFile = extremeSizeInFloat(directory, "max");
                        System.out.println("File Summary: " + ext);
                        System.out.println("Directory Summary: " + dir);
                        System.out.println("Largest File: " + maxFile);
                        System.out.println("Smallest File: " + minFile);
                        break;
                    case 2:
                        String resDir = makeDir(ext.keySet(), dir, "safe");
                        moveFiles(directory, workingDir, resDir, "safe");
                        System.out.println("Files Managed at ./" + resDir);
                        ignoreList.add(resDir);
                        break;
                    case 3:
                        if (!doneStrict) {
                            originalState = ogState(directory);
                            makeDir(ext.keySet(), dir, "strict");
                            moveFiles(directory, workingDir, workingDir, "strict");
                            doneStrict = true;
                            System.out.println("Files Managed at same directory");
                        } else {
                            System.out.println("Already Achieved Strict Mode");
                        }
                        break;
                    case 4:
                        if (doneStrict) {
                            revert(workingDir, originalState);
                            doneStrict = false;
                        } else {
                            System.out.println("Apply Strict mode first");
                        }
                        break;
                    case 5:
                        ext = fileSummary(directory);
                        report = makeReport(workingDir, ext);
                        if (report != null) {
                            System.out.println("Report Generated at ./" + report);
                            ignoreList.add(report);
                        } else {
                            System.out.println("Can not generated report for Empty Directory");
                        }
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Invalid Choice!");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            inp.close();
        }
    }

    static boolean ignore(File f) {
        return ignoreList.contains(f.getName());
    }

    static String extName(String fullFileName) {
        String[] ext = fullFileName.split("\\.");
        return ext[ext.length - 1];
    }

    static HashMap<String, Integer> fileSummary(File directory) {
        int total = 0;
        HashMap<String, Integer> counts = new HashMap<>();
        File[] files = directory.listFiles(File::isFile);

        if (files != null) {
            for (File f : files) {
                if (!ignore(f)) {
                    String check = extName(f.getName());
                    if (counts.containsKey(check)) {
                        counts.put(check, counts.get(check) + 1);
                    } else {
                        counts.put(check, 1);
                    }
                    total++;
                }
            }
            counts.put("Total files", total);
            return counts;
        }

        return null;
    }

    static void moveFiles(File directory, String workingDir, String resDir, String mode) {
        File[] files = directory.listFiles(File::isFile);

        if (files != null) {
            if (mode.equals("safe")) {
                for (File f : files) {
                    if (!ignore(f)) {
                        String check = extName(f.getName());
                        String moveTo = workingDir + File.separator + resDir + File.separator + check + File.separator
                                + f.getName();
                        String copyFrom = workingDir + File.separator + f.getName();
                        try {
                            Files.copy(Paths.get(copyFrom), Paths.get(moveTo));
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            } else {
                for (File f : files) {
                    if (!ignore(f)) {
                        String check = extName(f.getName());
                        String moveTo = workingDir + File.separator + check + File.separator
                                + f.getName();
                        String copyFrom = workingDir + File.separator + f.getName();
                        try {
                            Files.move(Paths.get(copyFrom), Paths.get(moveTo));
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    static HashSet<String> dirSummary(File directory) {
        HashSet<String> dirNames = new HashSet<>();
        File[] dirs = directory.listFiles(File::isDirectory);

        if (dirs != null) {
            for (File d : dirs) {
                if (!ignore(d)) {
                    if (!dirNames.contains(d.getName())) {
                        dirNames.add(d.getName());
                    }
                }
            }
            return dirNames;
        }

        return null;
    }

    static HashMap<String, Double> extremeSizeInFloat(File directory, String opt) {
        HashMap<String, Double> exFile = new HashMap<>();
        File[] files = directory.listFiles(File::isFile);
        String name = files[0].getName();
        long val = files[0].length();

        for (File f : files) {
            if (!ignore(f)) {
                if (opt.toLowerCase().equals("min")) {
                    if (f.length() < val) {
                        name = f.getName();
                        val = f.length();
                    }
                } else if (opt.toLowerCase().equals("max")) {
                    if (f.length() > val) {
                        name = f.getName();
                        val = f.length();
                    }
                }
            }
        }
        double sizeInMb = Math.round(((double) val / (1024 * 1024) * 1000)) / 1000.0;
        exFile.put(name, sizeInMb);

        return exFile;
    }

    static String makeDir(Set<String> exts, Set<String> dirs, String mode) {
        String workDir = System.getProperty("user.dir");

        if (mode.equals("safe")) {
            int i = 0;
            String res = "result";
            while (dirs.contains(res)) {
                res = "result" + i++;
            }

            File result = new File(workDir + File.separator + res);
            result.mkdir();
            for (String x : exts) {
                if (!dirs.contains(x) && !x.equals("Total files")) {
                    File temp = new File(workDir + File.separator + res + File.separator + x);
                    temp.mkdir();
                }
            }
            return res;
        } else {
            try {
                for (String x : exts) {
                    if (!dirs.contains(x) && !x.equals("Total files")) {
                        File temp = new File(workDir + File.separator + x);
                        temp.mkdir();
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in Achieving Strict mode.. Try safe mode");
            }
        }
        return ".";
    }

    static HashMap<String, ArrayList<String>> ogState(File dir) {
        HashMap<String, ArrayList<String>> og = new HashMap<>();
        File[] files = dir.listFiles(File::isFile);
        if (files != null) {
            for (File f : files) {
                if (!ignore(f)) {
                    String check = extName(f.getName());
                    if (og.containsKey(check)) {
                        og.get(check).add(f.getName());
                    } else {
                        og.put(check, new ArrayList<>(Arrays.asList(f.getName())));
                    }
                }
            }
            return og;
        }
        return null;
    }

    static String makeReport(String filePath, Map<String, Integer> report) throws IOException {

        File workingPath = new File(filePath);

        ArrayList<File> dir = new ArrayList<>(Arrays.asList(workingPath.listFiles(File::isFile)));

        if (!dir.isEmpty()) {
            int i = 0;
            String res = "directoryReport";

            while (containsFileName(dir, res + ".csv") && i < 100) {
                res = "directoryReport" + i++;
            }

            if (containsFileName(dir, res + ".csv")) {
                return null;
            }

            res += ".csv";

            try (FileWriter writer = new FileWriter(res)) {
                writer.append("Time").append(",").append(LocalTime.now().withNano(0).toString()).append("\n");
                for (Map.Entry<String, Integer> entry : report.entrySet()) {
                    writer.append(entry.getKey())
                            .append(",")
                            .append(String.valueOf(entry.getValue()))
                            .append("\n");
                }
                return res;
            }

        }

        return null;
    }

    private static boolean containsFileName(List<File> files, String name) {
        for (File f : files) {
            if (f.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    static void revert(String workingDir, Map<String, ArrayList<String>> originalState) {
        for (String ext : originalState.keySet()) {
            if (!ext.equals("Total files")) {
                String source = workingDir + File.separator + ext;
                File sourceDir = new File(source);
                ArrayList<String> moveFromFiles = originalState.get(ext);
                if (moveFromFiles != null) {
                    for (String moveFrom : moveFromFiles) {
                        String sourceFile = workingDir + File.separator + ext + File.separator + moveFrom;
                        String targetFile = workingDir + File.separator + moveFrom;
                        try {
                            Files.move(Paths.get(sourceFile), Paths.get(targetFile));
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
                try {
                    Files.delete(sourceDir.toPath());
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }

            }
        }
    }
}
