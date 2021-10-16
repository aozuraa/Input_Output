import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String stmt = input.nextLine();
        String[] tokens = lexicalAnalyzer(stmt);
        System.out.println(Arrays.toString(tokens));
        syntaxAnalyzer(tokens);
    }

    //break down string into lexemes
    //System.out.print("string" + varname);
    //[System.out.print, (, "string", +, varname, ), ;]
    public static String[] lexicalAnalyzer(String stmt) {
        StringBuilder sb = new StringBuilder(stmt);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '=' | sb.charAt(i) == '(' |
                    sb.charAt(i) == ')' | sb.charAt(i) == ';' | sb.charAt(i) == '\'') {
                sb.insert(i, " ");
                sb.insert(i + 2, " ");
                i += 2;
            }
        }
        stmt = sb.toString();
        return stmt.split("\\s+");
    }

    //choose which syntax checker based on the 1st element in the array
    public static void syntaxAnalyzer(String[] s) {
        ArrayList<String> resultList = new ArrayList<>();
        String[] tokens = s.clone();
        String start = tokens[0];

        if (start.equals("Scanner")){
            if (variableChecker(tokens[1]).equals("valid")){
                tokens[1] = "<var>";
                if (scannerChecker(tokens).equals("valid")){
                    resultList.add("Valid Statement"+1);
                }else resultList.add(scannerChecker(tokens)+2);
            } else resultList.add(variableChecker(tokens[1])+3);
        }
        else  if (start.equals("System.out.print")){
            for (int i = 2; i < tokens.length-2;i++) {
                if (variableChecker(tokens[i]).equals("valid")){
                    tokens[i] = "<var>";
                }else if (tokens[i].startsWith("\"")&&tokens[i].endsWith("\"")){
                    tokens[i] = "<string>";
                }
                else if (!tokens[i].equals("+")){
                    System.out.println(tokens[i]);
                    resultList.add(variableChecker(tokens[i])+4);
                    break;
                }
            }
            resultList.add(outputChecker(tokens)+5);
        }
        else if (variableChecker(start).equals("valid")){
            tokens[0] = "<var>";
            String temp2 =tokens[2].split("\\.")[0];
            if (variableChecker(temp2).equals("valid")){
                tokens[2] = "<var>."+ tokens[2].split("\\.")[1];
                resultList.add(inputChecker(tokens)+6);
            } else  resultList.add(variableChecker(temp2)+7);
        }
        else resultList.add("invalid Statement"+8);
        System.out.println(resultList.toString());
    }

    //Check the syntax of the <scanner>
    public static String scannerChecker(String[] s) {
        String[] validInit = new String[]{"Scanner", "<var>", "=", "new", "Scanner", "(", "System.in", ")", ";"};
        if (Arrays.equals(s, validInit)) {
            return "valid";
        } else return "invalid something"; //needs to be changed
    }

    //Check the syntax of the <output_statement>
    public static String outputChecker(String[] s) {
        String isValid = "Invalid statement";
        // checks if the array is == [System.out.print, (, <dynamic>, ), ;]
        // where <dynamic> is one of the ff: <string> + <var> or <var> or <var> + <string> or <string>
        // note: it can also do an large amount of <string> + <string> + <string> or <var> + <var> + <var>  or a mix of the two
        if (s[0].equals("System.out.print") && s[1].equals("(") && (s[s.length - 3].equals("<var>") | s[s.length - 3].equals("<string>"))) {
            if (s[s.length - 2].equals(")") && s[s.length - 1].equals(";")) {
                for (int i = 2; i < s.length - 2; i += 2) {
                    if (s[i].equals("<string>") | s[i].equals("<var>")) {
                        isValid = "valid";
                    } else {
                        isValid = "Invalid statement";
                        break;
                    }
                    if (s[i + 1].equals("+") | i + 1 == s.length - 2) {
                        isValid = "valid";
                    } else {
                        isValid = "Invalid statement";
                        break;
                    }
                }
            }
        }
        return isValid;
    }

    //Check the syntax of the <input_statement>
    public static String inputChecker(String[] s) {
        String[] validInit = new String[]{"<var>", "=", "<var>.nextX", "(", ")", ";"};
        String[] dataTypes = new String[]{"nextInt","nextFloat","nextBoolean","next","nextByte","nextDouble","nextShort","nextLong"};
        String[] tempArray = s.clone();
        tempArray[2] = "<var>.nextX";
        if (Arrays.equals(tempArray, validInit)) {
            if (Arrays.asList(dataTypes).contains(s[2].split("\\.")[1])) {
                return "valid";
            }else  return "invalid datatype of scanner";
        } else return "invalid syntax";
    }

    //Check if the given variable name is valid.
    public static String variableChecker(String varName) {
        if (Character.isLetter(varName.charAt(0)) | varName.charAt(0) == '$' | varName.charAt(0) == '_') {
            return "valid";
        }
        return "invalid variable name";
    }


}