package vasilisushka;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SuffixingApp {

    final static Logger logger = Logger.getLogger("SuffixingApp");

    public static String suffixing(Properties props) throws Exception {
        //добавляем суффикс
        String suffix = props.getProperty("suffix");
        String file = props.getProperty("files");
        String[] temp = file.split("[.]");
        temp[0] = temp[0] + suffix + ".";
        return String.join("", temp);
    }

    public static void fileProcessing(Properties props) throws Exception {
        if (props.getProperty("files") == null || props.getProperty("files").isEmpty()) {
            logger.log(Level.WARNING, "No files are configured to be copied/moved");
            return;
        }
        if (props.getProperty("suffix") == null) {
            logger.log(Level.SEVERE, "No suffix is configured");
            return;
        }
        String absoluteFilePath = suffixing(props);
        if (!Files.exists(Paths.get(props.getProperty("files")))) {
            logger.log(Level.SEVERE, "No such file: "+
               (Paths.get(props.getProperty("files"))).toString().replaceAll("\\\\","/"));
            return;
        }
        //копирование
        if (props.getProperty("mode").equalsIgnoreCase("copy")) {
            Files.copy(Paths.get(props.getProperty("files")), Paths.get(absoluteFilePath));
            logger.info(Paths.get(props.getProperty("files")).toString().replaceAll("\\\\", "/") + " -> " + Paths.get(absoluteFilePath).toString().replaceAll("\\\\", "/"));
        } else if (props.getProperty("mode").equalsIgnoreCase("move")) {
            //delete
            Files.move(Paths.get(props.getProperty("files")), Paths.get(absoluteFilePath));
            logger.info(Paths.get(props.getProperty("files")).toString().replaceAll("\\\\", "/") + " => " + Paths.get(absoluteFilePath).toString().replaceAll("\\\\", "/"));

        } else {
            logger.log(Level.SEVERE, "Mode is not recognized: " + props.getProperty("mode"));
        }
    }

    public static void fileSeparator(String param) throws Exception {
        //открываем проперти
        Properties props = new Properties();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(param), StandardCharsets.UTF_8);
        props.load(new InputStreamReader(new FileInputStream(param), StandardCharsets.UTF_8));
        isr.close();

        String file = props.getProperty("files");
        if (file == null) {
            logger.log(Level.WARNING,"No files are configured to be copied/moved");
            return;
        }
        String[] temp = file.split("[:]");
        for (String str:temp) {
            props.setProperty("files",str);
            fileProcessing(props);
        }
    }


    public static void main(String[] args) throws Exception {
        fileSeparator(args[0]);
    }
}
