import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProgrammingAssignment2 {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Please enter a valid .machine file.");
        }
        else {
            ArrayList<String> instructions = binaryParser(args[0]);
            disassemble(instructions);
        }
    }

    public static ArrayList<String> binaryParser(String inputFile) {
        ArrayList<String> instructions = new ArrayList<>();
        int ch;
        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            int count = 0;
            String binaryInstruction = "";
            while((ch = inputStream.read()) != -1) {
                String temp = String.format("%8s", Integer.toBinaryString(ch & 0xFF)).replace(' ', '0');
                binaryInstruction += temp;
                if(count == 3) {
                    instructions.add(binaryInstruction);
                    binaryInstruction = "";
                    count = 0;
                }
                else {
                    count++;
                }
            }

            inputStream.close();
        }
        catch(IOException e) {
            System.out.println("Error, invalid file.");
        }

        return instructions;
    }

    public static void disassemble(ArrayList<String> instructions) {
        ArrayList<Integer> labels = new ArrayList<>();
        ArrayList<String> print = new ArrayList<>();

        for(int i = 0; i < instructions.size(); i++) {
            String opcodeBinary = instructions.get(i).substring(0, 11);
            String opcode = "";
            boolean isR = false;
            boolean isD = false;
            boolean isI = false;
            boolean isCB = false;
            boolean isB = false;

            //Opcode of 11
            if(opcodeBinary.equals("10001011000")) {
                isR = true;
                opcode = "ADD";
            }
            else if(opcodeBinary.equals("10001010000")) {
                isR = true;
                opcode = "AND";
            }
            else if(opcodeBinary.equals("11010110000")) {
                isR = true;
                opcode = "BR";
            }
            else if(opcodeBinary.equals("11001010000")) {
                isR = true;
                opcode = "EOR";
            }
            else if(opcodeBinary.equals("11111000010")) {
                isD = true;
                opcode = "LDUR";
            }
            else if(opcodeBinary.equals("11010011011")) {
                isR = true;
                opcode = "LSL";
            }
            else if(opcodeBinary.equals("11010011010")) {
                isR = true;
                opcode = "LSR";
            }
            else if(opcodeBinary.equals("10101010000")) {
                isR = true;
                opcode = "ORR";
            }
            else if(opcodeBinary.equals("11111000000")) {
                isD = true;
                opcode = "STUR";
            }
            else if(opcodeBinary.equals("11001011000")) {
                isR = true;
                opcode = "SUB";
            }
            else if(opcodeBinary.equals("11101011000")) {
                isR = true;
                opcode = "SUBS";
            }
            else if(opcodeBinary.equals("10011011000")) {
                isR = true;
                opcode = "MUL";
            }
            else if(opcodeBinary.equals("11111111101")) {
                isR = true;
                opcode = "PRNT";
            }
            else if(opcodeBinary.equals("11111111100")) {
                isR = true;
                opcode = "PRNL";
            }
            else if(opcodeBinary.equals("11111111110")) {
                isR = true;
                opcode = "DUMP";
            }
            else if(opcodeBinary.equals("11111111111")) {
                isR = true;
                opcode = "HALT";
            }
            else {
                opcodeBinary = instructions.get(i).substring(0, 10);
            }

            //Opcode of 10
            if(opcodeBinary.equals("1001000100")) {
                isI = true;
                opcode = "ADDI";
            }
            else if(opcodeBinary.equals("1001001000")) {
                isI = true;
                opcode = "ANDI";
            }
            else if(opcodeBinary.equals("1101001000")) {
                isI = true;
                opcode = "EORI";
            }
            else if(opcodeBinary.equals("1011001000")) {
                isI = true;
                opcode = "ORRI";
            }
            else if(opcodeBinary.equals("1101000100")) {
                isI = true;
                opcode = "SUBI";
            }
            else if(opcodeBinary.equals("1111000100")) {
                isI = true;
                opcode = "SUBIS";
            }
            else {
                opcodeBinary = instructions.get(i).substring(0, 8);
            }

            //Opcode of 8
            if(opcodeBinary.equals("01010100")) {
                isCB = true;
                opcode = "B.";
            }
            else if(opcodeBinary.equals("10110101")) {
                isCB = true;
                opcode = "CBNZ";
            }
            else if(opcodeBinary.equals("10110100")) {
                isCB = true;
                opcode = "CBZ";
            }
            else {
                opcodeBinary = instructions.get(i).substring(0, 6);
            }

            //Opcode of 6
            if(opcodeBinary.equals("000101")) {
                isB = true;
                opcode = "B";
            }
            else if(opcodeBinary.equals("100101")) {
                isB = true;
                opcode = "BL";
            }

            if(isR) {
                int rm = Integer.parseInt(instructions.get(i).substring(11, 16), 2);
                int shamt = Integer.parseInt(instructions.get(i).substring(16, 22), 2);
                int rn = Integer.parseInt(instructions.get(i).substring(22, 27), 2);
                int rd = Integer.parseInt(instructions.get(i).substring(27, 32), 2);

                if(opcode.equals("LSL") || opcode.equals("LSR")) {
                    print.add(opcode + " X" + rd + ", X" + rn + ", #" + shamt);
                }
                else if(opcode.equals("BR")) {
                    print.add(opcode + " X" + rn);
                }
                else if(opcode.equals("PRNT")) {
                    print.add(opcode + " X" + rd);
                }
                else if(opcode.equals("PRNL") || opcode.equals("DUMP") || opcode.equals("HALT")) {
                    print.add(opcode);
                }
                else {
                    print.add(opcode + " X" + rd + ", X" + rn + ", X" + rm);
                }
            }
            if(isD) {
                int address = Integer.parseInt(instructions.get(i).substring(11, 20), 2);
                int rn = Integer.parseInt(instructions.get(i).substring(22, 27), 2);
                int rt = Integer.parseInt(instructions.get(i).substring(27, 32), 2);

                print.add(opcode + " X" + rt + ", [X" + rn + ", #" + address + "]");
            }
            if(isI) {
                int immediate = 0;
                if(instructions.get(i).charAt(10) == '1') {
                    immediate = twosComplement(instructions.get(i).substring(11, 22));
                    immediate *= -1;
                }
                else {
                    immediate = Integer.parseInt(instructions.get(i).substring(11,22), 2);
                }
                int rn = Integer.parseInt(instructions.get(i).substring(22, 27), 2);
                int rd = Integer.parseInt(instructions.get(i).substring(27, 32), 2);

                print.add(opcode + " X" + rd + ", X" + rn + ", #" + immediate);
            }
            if(isCB) {
                int address = 0;
                if(instructions.get(i).charAt(8) == '1') {
                    address = twosComplement(instructions.get(i).substring(9, 27));
                    address *= -1;
                }
                else {
                    address = Integer.parseInt(instructions.get(i).substring(9, 27), 2);
                }
                int rt = Integer.parseInt(instructions.get(i).substring(27, 32), 2);

                if(opcode.equals("B.")) {
                    if(rt == 0) {
                        print.add(opcode + "EQ Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 1) {
                        print.add(opcode + "NE Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 2) {
                        print.add(opcode + "HS Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 3) {
                        print.add(opcode + "LO Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 4) {
                        print.add(opcode + "MI Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 5) {
                        print.add(opcode + "PL Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 6) {
                        print.add(opcode + "VS Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 7) {
                        print.add(opcode + "VC Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 8) {
                        print.add(opcode + "HI Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 9) {
                        print.add(opcode + "LS Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 10) {
                        print.add(opcode + "GE Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 11) {
                        print.add(opcode + "LT Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 12) {
                        print.add(opcode + "GT Label" + (i + address));
                        labels.add(i + address);
                    }
                    else if(rt == 13) {
                        print.add(opcode + "LE Label" + (i + address));
                        labels.add(i + address);
                    }
                }
                else {
                    print.add(opcode + " X" + rt + ", Label" + (i + address));
                    labels.add(i + address);
                }
            }
            if(isB) {
                int address = 0;
                if(instructions.get(i).charAt(6) == '1') {
                    address = twosComplement(instructions.get(i).substring(7, 32));
                    address *= -1;
                }
                else {
                    address = Integer.parseInt(instructions.get(i).substring(7, 32), 2);
                }

                print.add(opcode + " Label" + (i + address));
                labels.add(i + address);
            }
        }
        //Adding labels to their proper positions
        for(int i = print.size(); i >= 0; i--){
            if(labels.contains(i)) {
                print.add(i, "Label" + i + ":");
            }
        }
        for (String s : print) {
            System.out.println(s);
        }
    }

    public static int twosComplement(String binaryValue) {
        int i = 0;
        for(i = binaryValue.length() - 1; i >= 0; i--) {
            if(binaryValue.charAt(i) == '1') {
                break;
            }
        }

        if(i == -1) {
            return Integer.parseInt("1" + binaryValue, 2);
        }
        else {
            StringBuilder temp = new StringBuilder(binaryValue);
            for(int j = i - 1; j >= 0; j--) {
                if(temp.charAt(j) == '1') {
                    temp.setCharAt(j, '0');
                }
                else {
                    temp.setCharAt(j, '1');
                }
            }
            return Integer.parseInt(temp.toString(), 2);
        }
    }
}
