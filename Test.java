import java.io.File;
import java.util.*;

class Test {
    public static void main(String[] args) {
        Map<String, Integer> ext = new HashMap<>();
        Map<String, Integer> minFile = new HashMap<>();
        Map<String, Integer> maxFile = new HashMap<>();
        HashSet<String> dir = new HashSet<>();
        String workingDir = System.getProperty("user.dir");
        File directory = new File(workingDir);

        dir = dirSummary(directory);
        ext = fileSummary(directory);
        minFile = extremeSize(directory, "min");
        maxFile = extremeSize(directory, "max");

        System.out.println(workingDir);
        System.out.println(ext);
        System.out.println(dir);
        System.out.println(minFile);
        System.out.println(maxFile);
    }

    static boolean ignore(File f) {
        HashSet<String> ignoreList = new HashSet<>(Arrays.asList("Test.java", "Test.class", "result"));
        return ignoreList.contains(f.getName());
    }

    static HashMap<String, Integer> fileSummary(File directory) {
        HashMap<String, Integer> counts = new HashMap<>();
        File[] files = directory.listFiles(File::isFile);

        if (files != null) {
            for (File f : files) {
                if (!ignore(f)) {
                    String[] t = f.getName().split("\\.");
                    String check = t[t.length - 1];
                    if (counts.containsKey(check)) {
                        counts.put(check, counts.get(check) + 1);
                    } else {
                        counts.put(check, 1);
                    }
                }
            }
            return counts;
        }

        return null;
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

    static HashMap<String, Integer> extremeSize(File directory, String opt) {
        int sizeInMb = 0;
        HashMap<String, Integer> exFile = new HashMap<>();
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
        val = val / (1024 * 1024);
        sizeInMb = (int) val;
        exFile.put(name, sizeInMb);

        return exFile;
    }

}