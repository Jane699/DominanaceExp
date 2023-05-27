package featureSelection.tester.procedure.basic;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SimpleDataset4Test {
    public static List<String[]> sample1 =
            Arrays.asList(
                    "1 1 1 1 0".split(" "),    //1
                    "2 2 1 2 1".split(" "),    //2
                    "1 1 1 1 0".split(" "),    //3
                    "2 3 3 2 0".split(" "),    //4
                    "2 2 1 2 1".split(" "),    //5
                    "3 1 1 2 0".split(" "),    //6
                    "1 2 2 3 2".split(" "),    //7
                    "2 3 2 1 3".split(" "),    //8
                    "3 1 1 2 1".split(" "),    //9
                    "1 2 2 3 2".split(" "),    //10
                    "3 1 1 2 1".split(" "),    //11
                    "2 3 2 1 3".split(" "),    //12
                    "4 3 2 4 1".split(" "),    //13
                    "1 2 2 3 3".split(" "),    //14
                    "4 3 2 4 2".split(" ")    //15*/
            );

    public static List<String[]> sample2 =
            Arrays.asList(
                    "1 1 1 1 0".split(" "),    //1
                    "2 2 1 1 0".split(" "),    //2
                    "1 1 ? 1 0".split(" "),    //3
                    "2 3 1 3 0".split(" "),    //4
                    "2 ? 1 1 1".split(" "),    //5
                    "? 1 2 1 0".split(" "),    //6
                    "2 2 2 2 2".split(" "),    //7
                    "2 ? 2 2 3".split(" "),    //8
                    "3 1 2 1 1".split(" "),    //9
                    "2 2 2 2 2".split(" "),    //10
                    "? ? 2 1 1".split(" "),    //11
                    "2 ? 2 2 3".split(" "),    //12
                    "4 3 4 2 1".split(" "),    //13
                    "2 2 2 2 2".split(" "),    //14
                    "4 3 4 2 2".split(" ")    //15
            );

    public static List<String[]> sample3 =
            Arrays.asList(
                    "2	2	1	1".split("	"),
                    "3	2	1	2".split("	"),
                    "2	3	1	2".split("	"),
                    "1	2	3	1".split("	"),
                    "1	1	2	1".split("	"),
                    "1	2	2	2".split("	"),
                    "3	3	1	3".split("	"),
                    "3	2	2	2".split("	"),
                    "2	2	3	3".split("	"),
                    "3	2	3	3".split("	")
            );

    public static List<String[]> dominanceSample1 =
            Arrays.asList(
                    "3 2 2".split(" "),    //1
                    "3 3 3".split(" "),    //2
                    "2 1 1".split(" "),    //3
                    "3 2 1".split(" "),    //4
                    "2 3 2".split(" "),    //5
                    "3 2 2".split(" "),    //6
                    "1 2 1".split(" ")    //7

//                    "3 2 3".split(" "),    //4
//                    "2 3 3".split(" "),    //5
//                    "3 2 2".split(" "),    //6
//                    "1 2 1".split(" ")     //7
            );

    public static List<String[]> dominanceOPACSample =
            Arrays.asList(
                    "2 2 1 1".split(" "),
                    "3 2 1 2".split(" "),
                    "2 3 1 2".split(" "),
                    "1 2 3 1".split(" "),
                    "1 1 2 1".split(" "),
                    "1 2 2 2".split(" "),
                    "3 3 1 3".split(" "),
                    "3 2 2 2".split(" "),
                    "2 2 3 3".split(" "),
                    "3 2 3 3".split(" ")

            );

    public static List<String[]> dominanceDIGSample =
            Arrays.asList(
                    "3 2 1".split(" "),    //1
                    "3 3 3".split(" "),    //2
                    "2 1 2".split(" "),    //3
                    "3 2 2".split(" "),    //4
                    "2 3 2".split(" "),    //5
                    "3 2 2".split(" "),    //6
                    "1 2 1".split(" "),    //7
                    "3 1 2".split(" "),    //8
                    "3 1 1".split(" "),    //9
                    "3 1 3".split(" "),   //10
                    "2 1 3".split(" ")   //11



//                    "3 2 3".split(" "),    //4
//                    "2 3 3".split(" "),    //5
//                    "3 2 2".split(" "),    //6
//                    "1 2 1".split(" ")     //7
            );

    public static class FromFile {

        public static String dataFileBasicPath = "D:/data/UCI Datasets";

        public static File wine() {
            return new File(dataFileBasicPath, "/S/wine-D.csv");
        }
        public static File pokerHand() {
            return new File("D:\\data\\dataset\\L\\poker-hand\\poker-hand.data");
        }

        public static File krkopt() {
            return new File(dataFileBasicPath, "/S/krkopt.csv");
        }

        public static File sample() {
            return new File("D:\\data\\gra\\(Java MDLP discreted) [2.0 %] kddcup.data.corrected.csv");
        }

        public static File audiology() {
            return new File(dataFileBasicPath, "/S/Audiology/audiology.standardized.csv");
        }
    }

}
