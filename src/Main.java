import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 10/20/2017.
 *
 * @author Serhii Petrusha aka Mr_Rism
 */
public class Main {

  public static void main(String[] args) {
    File rootPath = new File("D:\\work\\");
    String[][] result = getDuplicates(rootPath);

    System.out.println(Arrays.toString(result[0]));

  }

  private static String[][] getDuplicates(File rootPath) {
    Map<Long, List<File>> allFiles = scanFolder(rootPath);

    allFiles.values().removeIf(e -> e.size() == 1);

    int i = 0;
    String[][] result = new String[allFiles.size()][];

    for (List<File> sameFiles : allFiles.values()) {

      long dateOfLastModification = (~0L) >>> 1;
      File originFile = null;

      for (File file : sameFiles) {
        long lastModified = 0;
        try {
          lastModified = Files.readAttributes(file.toPath(), BasicFileAttributes.class)
              .creationTime().toMillis();
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (dateOfLastModification > lastModified) {
          dateOfLastModification = lastModified;
          originFile = file;
        }
      }

      sameFiles.set(0, originFile);
      result[i++] =
          sameFiles.stream()
              .map(x -> "" + x.getAbsoluteFile())
              .collect(Collectors.toList())
              .toArray(new String[0]);


    }
    return result;
  }

  private static Map<Long, List<File>> scanFolder(File path) {
    Map<Long, List<File>> files = new HashMap<>();
    if (path.isDirectory()) {
      for (File file : path.listFiles()) {

        if (file.isFile()) {
          addDataToMap(files, file);
        } else {
          scanFolder(file).entrySet().
              forEach(entry -> entry.getValue().
                  forEach(innerFile ->
                      addDataToMap(files, innerFile))
              );
        }
      }
    }
    return files;
  }

  private static void addDataToMap(Map<Long, List<File>> files, File file) {
    if (files.get(file.length()) == null) {
      List<File> duplicatesFiles = new ArrayList<>();
      duplicatesFiles.add(file);
      files.put(file.length(), duplicatesFiles);
    } else {
      files.get(file.length()).add(file);
    }
  }


}
