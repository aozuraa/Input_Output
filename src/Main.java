import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("enter input: ");
        String stmt = input.nextLine();
        String[] tokens = lexicalAnalyzer(stmt);
        // no valid statement can be created with less than 3 tokens
        if (tokens.length > 3) {
            System.out.println("Lexemes: " + Arrays.toString(tokens));
            syntaxAnalyzer(tokens);
        } else System.out.println("Invalid statement");
    }

    //break down string into lexemes
    //System.out.print("string" + varname);
    //[System.out.print, (, "string", +, varname, ), ;]
    public static String[] lexicalAnalyzer(String stmt) {
        StringBuilder sb = new StringBuilder(stmt);
        boolean withinString = false;
        boolean isSpecial = false;
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '"' && (sb.charAt(i - 1) != '\\' | isSpecial)) {
                withinString = !withinString;
            } else if (sb.charAt(i) == '\\' && withinString && !isSpecial) {
                if (sb.charAt(i + 1) == '\\') {
                    isSpecial = true;
                    i++;
                }
            } else if (!withinString && (sb.charAt(i) == '=' | sb.charAt(i) == '(' |
                    sb.charAt(i) == ')' | sb.charAt(i) == ';')) {
                sb.insert(i, " ");
                sb.insert(i + 2, " ");
                i += 2;
            } else isSpecial = false;
        }
        stmt = sb.toString();
        return stmt.split("\\s+");
    }

    //choose which syntax checker based on the 1st element in the array
    public static void syntaxAnalyzer(String[] s) {
        String result;
        String[] tokens = s.clone();
        String start = tokens[0];

        try {
            if (start.equals("Scanner")) {
                if (variableChecker(tokens[1]).equals("valid")) {
                    tokens[1] = "<var>";
                    result = scannerChecker(tokens);
                } else result = (variableChecker(tokens[1]));
            } else if (start.equals("System.out.print")) {
                for (int i = 2; i < tokens.length - 2; i++) {
                    if (variableChecker(tokens[i]).equals("valid")) {
                        tokens[i] = "<var>";
                    } else if (tokens[i].startsWith("\"") && tokens[i].endsWith("\"")) {
                        tokens[i] = "<string>";
                    } else if (!tokens[i].equals("+")) {
                        result = (variableChecker(tokens[i]));
                        break;
                    }
                }
                result = (outputChecker(tokens));
            } else if (variableChecker(start).equals("valid")) {
                tokens[0] = "<var>";
                String temp2 = tokens[2].split("\\.")[0];
                if (variableChecker(temp2).equals("valid")) {
                    tokens[2] = "<var>." + tokens[2].split("\\.")[1];
                    result = (inputChecker(tokens));
                } else result = (variableChecker(temp2));
            } else result = ("invalid Statement");

        } catch (Exception e) {
            result = ("invalid statement");
        }
        System.out.println(result);
    }

    //Check the syntax of the <scanner>
    public static String scannerChecker(String[] s) {
        String[] validInit = new String[]{"Scanner", "<var>", "=", "new", "Scanner", "(", "System.in", ")", ";"};
        String[] sizedS = new String[9];
        int j = 0;
        for (int i = 0; i < sizedS.length; i++) {
            sizedS[i] = " ";
        }
        for (int i = 0; i < s.length; i++) {
            sizedS[i] = s[i];
        }
        ArrayList<String> invalidSyntax = new ArrayList<String>(10);
        if (Arrays.equals(s, validInit)) {
            return "valid";
        } else {
            for (int i = 0; i < validInit.length; i++) {
                if (sizedS[i - j].equals(validInit[i])) {
                } else {
                    invalidSyntax.add(validInit[i]);
                    j++;
                }
            }

            return "Invalid Syntax: Missing " + invalidSyntax.toString(); //needs to be changed
        }
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
        String[] dataTypes = new String[]{"nextInt", "nextFloat", "nextBoolean", "next", "nextByte", "nextDouble", "nextShort", "nextLong"};
        String[] validInitForChar = new String[]{"<var>", "=", "<var>.nextX", "(", ")", ".charAt", "(", "0", ")", ";"};
        String[] tempArray = s.clone();
        tempArray[2] = "<var>.nextX";
        if (Arrays.asList(dataTypes).contains(s[2].split("\\.")[1])) {
            if (Arrays.equals(tempArray, validInit) | Arrays.equals(tempArray, validInitForChar)) {
                return "valid";
            } else return "invalid syntax";
        } else return "invalid datatype of scanner";
    }

    //Check if the given variable name is valid.
    public static String variableChecker(String varName) {
        if (Character.isLetter(varName.charAt(0)) | varName.charAt(0) == '$' | varName.charAt(0) == '_') {
            return "valid";
        }
        return "invalid variable name";
    }


}