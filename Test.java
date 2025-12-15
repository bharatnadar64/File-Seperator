import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class Test {
    public static void main(String[] args) {
        boolean doneStrict = false;
        Scanner inp = new Scanner(System.in);
        int choice;
        Map<String, Integer> ext;
        Map<String, Integer> originalState;
        Map<String, Double> minFile;
        Map<String, Double> maxFile;
        HashSet<String> dir;
        try {
            String workingDir = System.getProperty("user.dir");
            File directory = new File(workingDir);
            originalState = fileSummary(directory);
            ext = fileSummary(directory);
            dir = dirSummary(directory);

            while (true) {
                System.out.println("File Manager");
                System.out.println(
                        "1. Summary \n2.Manage Files(Safe Mode) \n3.Manage Files(Strict Mode) \n4.Revert Strict Mode \n5.Exit");
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
                        break;
                    case 3:
                        if (!doneStrict) {
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
                        } else {
                            System.out.println("Apply Strict mode first");
                        }
                        break;
                    case 5:
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
        HashSet<String> ignoreList = new HashSet<>(Arrays.asList("Test.java", "Test.class", ".git"));
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

    static void revert(String workingDir, Map<String, Integer> originalState) {
        for (String ext : originalState.keySet()) {
            if (!ext.equals("Total files")) {
                String source = workingDir + File.separator + ext;
                File sourceDir = new File(source);
                File[] moveFromFiles = sourceDir.listFiles();
                if (moveFromFiles != null) {
                    for (File moveFrom : moveFromFiles) {
                        String sourceFile = workingDir + File.separator + ext + File.separator + moveFrom.getName();
                        String targetFile = workingDir + File.separator + moveFrom.getName();
                        try {
                            Files.move(Paths.get(sourceFile), Paths.get(targetFile));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Files.delete(sourceDir.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
