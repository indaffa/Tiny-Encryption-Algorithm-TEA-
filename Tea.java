public class Tea {
    public static void main(String args[]) {
        int[] key = {0xab1a16be, 0xc4163a89, 0x87e5b018, 0x65ed8705};

        String input = args[0];
        int kbitsize = Integer.parseInt(args[1]);
        long startTime = System.nanoTime();
        
        
        if(args[2].equals("-encrypt")){
            long result = kBitEncrypt(input, key, kbitsize);
            printEncrypted(input, result);
        }

        else if (args[2].equals("-decrypt")){
            long result = kBitDecrypt(input, key, kbitsize);
            printDecrypted(input, result);
        }
        long stopTime = System.nanoTime();
        System.out.println("Time in nanoseconds: " + Long.toString(stopTime - startTime));
    }

    public static void printEncrypted(String input, long cipher){
        StringBuilder hex = new StringBuilder("");
        for(int i = 0; i < input.length() + 1; i++){
            long mask = 15;
            long result = cipher & mask;
            hex.insert(0, toHex(result));
            cipher = cipher >>> 4; 
        }

        System.out.println(hex);
    }

    public static void printDecrypted(String input, long cipher){
        StringBuilder hex = new StringBuilder("");
        for(int i = 0; i < input.length() - 1; i++){
            long mask = 15;
            long result = cipher & mask;
            hex.insert(0, toHex(result));
            cipher = cipher >>> 4; 
        }

        System.out.println(hex);
    }
       
    public static long encrypt(long in, int[] k) {
        int DELTA = 0x9e3779b9;
        long MASK32 = (1L << 32) - 1;
        int v1 = (int) in;
        int v0 = (int) (in >>> 32);
        int sum = 0;
        for (int i = 0; i < 32; i++) {
            sum += DELTA;
            v0 += ((v1 << 4) + k[0]) ^ (v1 + sum) ^ ((v1 >>> 5) + k[1]);
            v1 += ((v0 << 4) + k[2]) ^ (v0 + sum) ^ ((v0 >>> 5) + k[3]);
        }
        return (v0 & MASK32) << 32 | (v1 & MASK32);
    }

    public static long kBitEncrypt(String in, int[] key, int kbitsize){
        long iv = 0xffffffff;
        long allOne = 0xffffffff;
        long encrypted = 0;
        long result = 0;
        long outputBit = 0;
        int total_bits = in.length() * 4;
        long output = 0;
        long inputText = inputTobinary(in);
        long textToXor = 0;
        int no_of_rounds = (int) Math.ceil(((double)(total_bits) / (double) kbitsize)); 
        long inputTextBit = (inputText >>> no_of_rounds * kbitsize - kbitsize);

        for(int i = 0; i < no_of_rounds; i++){
            output = encrypt(iv, key);       // key and iv are sent to tea algorithm
            outputBit = output & (allOne << 64 - kbitsize);
            outputBit = Long.rotateRight(outputBit, 64 - kbitsize);
            result = outputBit ^ inputTextBit;
            encrypted = (encrypted << kbitsize) | result; //push the previous ciphertext to the left to put in the new block
            iv = (iv << kbitsize) | outputBit;             // push the iv to the left to replace with output
            inputText = inputText ^ (inputTextBit << ((no_of_rounds - i) * kbitsize - kbitsize));
            inputTextBit = (inputText >> (no_of_rounds - i - 1) * kbitsize - kbitsize);
       
        // for(int i = 0; i < no_of_rounds; i++){
        //     output = encrypt(iv, key);       // key and iv are sent to tea algorithm
        //     textToXor = inputTobinary;
        
        //     for(int j = 0; j < no_of_rounds - i - 1; j++){
        //         textToXor = (textToXor >>> kbitsize);    //push the text to be xor to the far right 
        //     }
           
        //     output = (output >>> (64 - kbitsize));       // push the output to be xor to the far right
        //     result = output ^ textToXor;                 // xor output and text
        //     encrypted = (encrypted << kbitsize) | result; //push the previous ciphertext to the left to put in the new block
        //     iv = (iv << kbitsize) | output;             // push the iv to the left to replace with output
           
        //     for(int j = 0; j < no_of_rounds - i - 1; j++){
        //         textToXor = (textToXor << kbitsize);    // push the text to its original place
        //     }
           
        //     inputTobinary = inputTobinary ^ textToXor;  // xor the original text with textToXor
        }

        return encrypted;
    }

    public static long kBitDecrypt(String in, int[] key, int kbitsize){
        long iv = 0xffffffff;
        long allOne = 0xffffffff;
        long decrypted = 0;
        long result = 0;
        long outputBit = 0;
        int total_bits = in.length() * 4;
        long output = 0;
        long inputText = inputTobinary(in);
        long textToXor = 0;
        int no_of_rounds = total_bits / kbitsize; 
        long inputTextBit = (inputText >>> no_of_rounds * kbitsize - kbitsize);

        for(int i = 0; i < no_of_rounds; i++){
            output = encrypt(iv, key);       // key and iv are sent to tea algorithm
            outputBit = output & (allOne << 64 - kbitsize);
            outputBit = Long.rotateRight(outputBit, 64 - kbitsize);
            result = outputBit ^ inputTextBit;
            decrypted = (decrypted << kbitsize) | result; //push the previous ciphertext to the left to put in the new block
            iv = (iv << kbitsize) | outputBit;             // push the iv to the left to replace with output
            inputText = inputText ^ (inputTextBit << ((no_of_rounds - i) * kbitsize - kbitsize));
            inputTextBit = (inputText >> (no_of_rounds - i - 1) * kbitsize - kbitsize);



        //     textToXor = inputTobinary;
        
        //     for(int j = 0; j < no_of_rounds - i - 1; j++){
        //         textToXor = (textToXor >>> kbitsize);    //push the text to be xor to the far right 
        //     }
           
        //     output = (output >>> (64 - kbitsize));       // push the output to be xor to the far right
        //     result = output ^ textToXor;                 // xor output and text
        //     decrypted = (decrypted << kbitsize) | result; //push the previous ciphertext to the left to put in the new block
        //     iv = (iv << kbitsize) | output;             // push the iv to the left to replace with output
           
        //     for(int j = 0; j < no_of_rounds - i - 1; j++){
        //         textToXor = (textToXor << kbitsize);    // push the text to its original place
        //     }
           
        //     inputTobinary = inputTobinary ^ textToXor;  // xor the original text with textToXor
        }

        return decrypted;
    }

    public static long inputTobinary(String input){
        long inputTobinary = 0;
        for(int i = 0; i < input.length(); i++){
            char id = input.charAt(i);
            int toint = toInt(id);               //convert char to int example: char '1' = int 1
            inputTobinary = (inputTobinary << 4) | toint;
        }
        return inputTobinary;
    }

    public static int toInt(char value){
        if(value == '0')       return  0;
        else if(value == '1')  return  1;
        else if(value == '2')  return  2;
        else if(value == '3')  return  3;
        else if(value == '4')  return  4;
        else if(value == '5')  return  5;
        else if(value == '6')  return  6;
        else if(value == '7')  return  7;
        else if(value == '8')  return  8;
        else if(value == '9')  return  9;
        else if(value == 'a')  return  10;
        else if(value == 'b')  return  11;
        else if(value == 'c')  return  12;
        else if(value == 'd')  return  13;
        else if(value == 'e')  return  14;
        else return 15; 
    }

    public static String toHex(long value){
        
        if(value == 0)       return "0";
        else if(value == 1)  return "1";
        else if(value == 2)  return "2";
        else if(value == 3)  return "3";
        else if(value == 4)  return "4";
        else if(value == 5)  return "5";
        else if(value == 6)  return "6";
        else if(value == 7)  return "7";
        else if(value == 8)  return "8";
        else if(value == 9)  return "9";
        else if(value == 10) return "a";
        else if(value == 11) return "b";
        else if(value == 12) return "c";
        else if(value == 13) return "d";
        else if(value == 14) return "e";
        else return "f";
    }
         
}

