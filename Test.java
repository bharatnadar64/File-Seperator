import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class Test {
    public static void main(String[] args) {
        Scanner inp = new Scanner(System.in);
        int choice;
        Map<String, Integer> ext;
        Map<String, Double> minFile;
        Map<String, Double> maxFile;
        HashSet<String> dir;
        String workingDir = System.getProperty("user.dir");
        File directory = new File(workingDir);
        ext = fileSummary(directory);
        dir = dirSummary(directory);
                    


        while (true) { 
            System.out.println("File Manager");
            System.out.println("1. Summary \n2.Manage Files(Safe Mode) \n3.Manage Files(Strict Mode) \n4.Exit");
            System.out.print("Choice: ");
            choice = inp.nextInt();
            switch (choice) {
                case 1:
                    minFile = extremeSizeInFloat(directory, "min");
                    maxFile = extremeSizeInFloat(directory, "max");
                    System.out.println("File Summary: "+ext);
                    System.out.println("Directory Summary: "+dir);
                    System.out.println("Largest File: "+maxFile);
                    System.out.println("Smallest File: "+minFile);
                    break;
                case 2:
                    String resDir = makeDir(ext.keySet(), dir);
                    moveFiles(directory, workingDir, resDir);
                    System.out.println("Files Managed at ./"+resDir);
                    break;

                case 4: return;
                default:
                   System.out.println("Invalid Choice!");
            }
        }

        
        

        
        
        // System.out.println(workingDir);
        // System.out.println(ext);
        // // System.out.println(dir);
        // System.out.println(minFile);
        // System.out.println(maxFile);
        // System.out.println(ext.keySet());
    }

    static boolean ignore(File f) {
        HashSet<String> ignoreList = new HashSet<>(Arrays.asList("Test.java", "Test.class", ".gitignore",".git"));
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

    static void moveFiles(File directory, String workingDir, String resDir) {
        File[] files = directory.listFiles(File::isFile);

        if (files != null) {
            for (File f : files) {
                if (!ignore(f)) {
                    String check = extName(f.getName());
                    String moveTo = workingDir + File.separator + resDir+File.separator+check+File.separator+f.getName();
                    String copyFrom = workingDir + File.separator + f.getName();
                    try {
                        Files.copy(Paths.get(copyFrom), Paths.get(moveTo));
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
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
                if (!dirNames.contains(d.getName())) {
                    dirNames.add(d.getName());
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
        double sizeInMb =Math.round(((double) val / (1024 * 1024) * 1000)) / 1000.0;
        exFile.put(name, sizeInMb);

        return exFile;
    }

    static String makeDir(Set<String> exts, Set<String> dirs) {
        String workDir = System.getProperty("user.dir");
        int i = 0;
        String res = "result";
        while (dirs.contains(res)) {
            res = "result" + i++;
        }

        File result = new File(workDir + File.separator + res);
        result.mkdir();

        for (String x : exts) {
            if (!dirs.contains(x)) {
                File temp = new File(workDir + File.separator + res + File.separator + x);
                temp.mkdir();
            }
        }
        return res;
    }

}
